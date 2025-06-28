const express = require('express');
const router = express.Router();
const CartItem = require('../models/cart.model');
const Food = require('../models/foods.model');
const { authenticate } = require('../middlewares/auth.middleware');
const mongoose = require('mongoose');

router.get('/', authenticate, async (req, res) => {
  try {
    const cartItems = await CartItem.find({ user: req.userId }).populate('food');
    console.log('Fetched cart items:', cartItems);
    res.json({ success: true, data: cartItems });
  } catch (error) {
    console.error('Error fetching cart:', error);
    res.status(500).json({ success: false, message: 'Lỗi khi lấy giỏ hàng: ' + error.message });
  }
});

router.post('/', authenticate, async (req, res) => {
  try {
    const { foodId, quantity } = req.body;
    console.log('Received request:', { foodId, quantity, userId: req.userId });

    // Kiểm tra userId hợp lệ
    if (!mongoose.Types.ObjectId.isValid(req.userId)) {
      return res.status(400).json({ success: false, message: 'userId không hợp lệ' });
    }

    // Kiểm tra foodId hợp lệ
    if (!mongoose.Types.ObjectId.isValid(foodId)) {
      return res.status(400).json({ success: false, message: 'foodId không hợp lệ' });
    }

    // Kiểm tra món ăn tồn tại
    const food = await Food.findById(foodId);
    if (!food) {
      return res.status(400).json({ success: false, message: 'Món ăn không tồn tại' });
    }

    // Kiểm tra số lượng
    if (!Number.isInteger(quantity) || quantity <= 0) {
      return res.status(400).json({ success: false, message: 'Số lượng phải là số nguyên dương' });
    }

    // Tạo hoặc cập nhật cart item
    let cartItem = await CartItem.findOne({ 
      user: new mongoose.Types.ObjectId(req.userId), 
      food: new mongoose.Types.ObjectId(foodId) 
    });
    if (cartItem) {
      cartItem.quantity += quantity;
    } else {
      cartItem = new CartItem({
        food: new mongoose.Types.ObjectId(foodId),
        quantity,
        user: new mongoose.Types.ObjectId(req.userId) // Chuyển userId thành ObjectId
      });
    }

    const savedItem = await cartItem.save();
    await savedItem.populate('food');
    console.log('Saved cart item:', savedItem);
    res.status(201).json({ success: true, data: savedItem });
  } catch (error) {
    console.error('Error adding to cart:', error);
    res.status(400).json({ success: false, message: 'Lỗi khi thêm vào giỏ hàng: ' + error.message });
  }
});

router.put('/:id', authenticate, async (req, res) => {
  try {
    const { quantity } = req.body;
    if (!mongoose.Types.ObjectId.isValid(req.params.id)) {
      return res.status(400).json({ success: false, message: 'Cart item ID không hợp lệ' });
    }

    if (!mongoose.Types.ObjectId.isValid(req.userId)) {
      return res.status(400).json({ success: false, message: 'userId không hợp lệ' });
    }

    if (!Number.isInteger(quantity) || quantity <= 0) {
      return res.status(400).json({ success: false, message: 'Số lượng phải là số nguyên dương' });
    }

    const cartItem = await CartItem.findOneAndUpdate(
      { _id: req.params.id, user: new mongoose.Types.ObjectId(req.userId) },
      { quantity },
      { new: true }
    ).populate('food');

    if (!cartItem) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy mục trong giỏ hàng' });
    }

    res.json({ success: true, data: cartItem });
  } catch (error) {
    console.error('Error updating cart item:', error);
    res.status(400).json({ success: false, message: 'Lỗi khi cập nhật số lượng: ' + error.message });
  }
});

router.delete('/:id', authenticate, async (req, res) => {
  try {
    if (!mongoose.Types.ObjectId.isValid(req.params.id)) {
      return res.status(400).json({ success: false, message: 'Cart item ID không hợp lệ' });
    }

    if (!mongoose.Types.ObjectId.isValid(req.userId)) {
      return res.status(400).json({ success: false, message: 'userId không hợp lệ' });
    }

    const cartItem = await CartItem.findOneAndDelete({ 
      _id: req.params.id, 
      user: new mongoose.Types.ObjectId(req.userId) 
    });
    if (!cartItem) {
      return res.status(404).json({ success: false, message: 'Không tìm thấy mục trong giỏ hàng' });
    }

    res.json({ success: true, message: 'Món đã được xóa khỏi giỏ hàng' });
  } catch (error) {
    console.error('Error deleting cart item:', error);
    res.status(500).json({ success: false, message: 'Lỗi khi xóa món: ' + error.message });
  }
});

module.exports = router;