const express = require('express');
const router = express.Router();
const Order = require('../models/order.model');
const { authenticate } = require('../middlewares/auth.middleware');

router.get('/', authenticate, async (req, res) => {
    try {
        const orders = await Order.find({ user: req.userId }).populate('items.food');
        // Chuyển đổi response để khớp với OrderResponse ở client
        const formattedOrders = orders.map(order => ({
            success: true,
            data: {
                orderId: order._id.toString(),
                status: order.status,
                totalAmount: order.total,
                estimatedDeliveryTime: order.estimatedDeliveryTime || '30 minutes' // Giá trị mặc định nếu không có
            },
            message: 'Success'
        }));
        res.json(formattedOrders);
    } catch (error) {
        res.status(500).json({ success: false, message: 'Lỗi khi lấy đơn hàng: ' + error.message });
    }
});

router.post('/', authenticate, async (req, res) => {
    try {
        const { items, deliveryAddress, phoneNumber, notes, paymentMethod } = req.body;

        // Kiểm tra dữ liệu đầu vào
        if (!items || !Array.isArray(items) || items.length === 0) {
            return res.status(400).json({ success: false, message: 'Danh sách món ăn không hợp lệ' });
        }
        if (!deliveryAddress || typeof deliveryAddress !== 'string' || deliveryAddress.trim() === '') {
            return res.status(400).json({ success: false, message: 'Địa chỉ giao hàng không được để trống' });
        }
        if (!phoneNumber || typeof phoneNumber !== 'string' || phoneNumber.trim() === '') {
            return res.status(400).json({ success: false, message: 'Số điện thoại không được để trống' });
        }

        let subtotal = 0;
        items.forEach(item => {
            if (!item.id || typeof item.quantity !== 'number' || !item.price || typeof item.price !== 'number') {
                return res.status(400).json({ success: false, message: 'Thông tin món ăn không hợp lệ' });
            }
            subtotal += item.quantity * item.price;
        });

        const delivery = 10.00;
        const tax = 1.00;
        const total = subtotal + delivery + tax;

        const order = new Order({
            user: req.userId,
            items: items.map(item => ({ id: item.id, quantity: item.quantity })),
            deliveryAddress: deliveryAddress.trim(),
            phoneNumber: phoneNumber.trim(),
            notes: notes ? notes.trim() : '',
            paymentMethod: paymentMethod || 'Cash on Delivery',
            subtotal,
            delivery,
            tax,
            total,
            status: 'pending',
            estimatedDeliveryTime: '30 minutes' // Thêm trường này
        });

        const savedOrder = await order.save();
        res.status(201).json({
            success: true,
            data: {
                orderId: savedOrder._id,
                status: 'pending',
                totalAmount: total,
                estimatedDeliveryTime: '30 minutes'
            }
        });
    } catch (error) {
        res.status(400).json({ success: false, message: 'Lỗi khi đặt hàng: ' + error.message });
    }
});

module.exports = router;