const mongoose = require('mongoose');

const foodSchema = new mongoose.Schema({
  BestFood: {
    type: Boolean,
    default: false
  },
  CategoryId: {
    type: Number,
    required: [true, 'CategoryId là bắt buộc'],
    ref: 'Category'
  },
  Description: {
    type: String,
    required: [true, 'Mô tả là bắt buộc'],
    trim: true
  },
  ImagePath: {
    type: String,
    required: [true, 'ImagePath là bắt buộc'],
    trim: true
  },
  Price: {
    Value: {
      type: Number,
      required: [true, 'Price.Value là bắt buộc'],
      min: [0, 'Giá phải lớn hơn hoặc bằng 0']
    },
    Range: {
      type: String,
      required: [true, 'Price.Range là bắt buộc'],
      enum: ['1$ - 10$', '10$ - 30$', 'more than 30$'],
      trim: true
    }
  },
  Star: {
    type: Number,
    required: [true, 'Star là bắt buộc'],
    min: [0, 'Đánh giá phải từ 0 đến 5'],
    max: [5, 'Đánh giá phải từ 0 đến 5']
  },
  Time: {
    Value: {
      type: String,
      required: [true, 'Time.Value là bắt buộc'],
      enum: ['0 - 10 min', '10 - 30 min', 'more than 30 min'],
      trim: true
    }
  },
  Location: {
    loc: {
      type: String,
      required: [true, 'Location.loc là bắt buộc'],
      trim: true
    }
  },
  Title: {
    type: String,
    required: [true, 'Tiêu đề là bắt buộc'],
    trim: true
  }
}, {
  timestamps: true
});

const Food = mongoose.model('Food', foodSchema);

module.exports = Food;