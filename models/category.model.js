const mongoose = require('mongoose');

const categorySchema = new mongoose.Schema({
  Id: {
    type: Number,
    required: [true, 'Id là bắt buộc'],
    unique: true
  },
  ImagePath: {
    type: String,
    required: [true, 'ImagePath là bắt buộc'],
    trim: true
  },
  Name: {
    type: String,
    required: [true, 'Tên danh mục là bắt buộc'],
    trim: true
  }
}, {
  timestamps: true
});

const Category = mongoose.model('Category', categorySchema);

module.exports = Category;