const mongoose = require('mongoose');

const orderSchema = new mongoose.Schema({
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    items: [{ food: { type: mongoose.Schema.Types.ObjectId, ref: 'Food' }, quantity: Number }],
    subtotal: { type: Number, required: true },
    delivery: { type: Number, default: 10.00 },
    tax: { type: Number, default: 1.00 },
    total: { type: Number, required: true },
    couponCode: { type: String },
    status: { type: String, enum: ['pending', 'completed', 'cancelled'], default: 'pending' },
    estimatedDeliveryTime: { type: String, default: '30 minutes' }, // Thêm trường này
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now }
});

orderSchema.pre('save', function(next) {
    this.updatedAt = Date.now();
    next();
});

module.exports = mongoose.model('Order', orderSchema);