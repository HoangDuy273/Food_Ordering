const express = require('express');
const router = express.Router();
const Food = require('../models/foods.model'); // Giả sử schema Food được định nghĩa trong file food.model.js

// Lấy danh sách các món ăn "best" (BestFood = true)
router.get('/best', async (req, res) => {
  try {
    const bestFoods = await Food.find({ BestFood: true });
    if (bestFoods.length > 0) {
      res.json(bestFoods);
    } else {
      res.status(200).json([]); // Trả về mảng rỗng nếu không có best food
    }
  } catch (err) {
    res.status(500).json({ message: 'Lỗi khi lấy danh sách món ăn tốt nhất: ' + err.message });
  }
});

// Lấy danh sách tất cả món ăn
router.get('/', async (req, res) => {
  try {
    const foods = await Food.find();
    res.json(foods);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi khi lấy danh sách món ăn: ' + err.message });
  }
});

// Lấy chi tiết một món ăn theo Id
router.get('/:id', async (req, res) => {
  try {
    const food = await Food.findOne({ Id: req.params.id });
    if (food) {
      res.json(food);
    } else {
      res.status(404).json({ message: 'Không tìm thấy món ăn' });
    }
  } catch (err) {
    res.status(500).json({ message: 'Lỗi khi lấy món ăn: ' + err.message });
  }
});

// Thêm món ăn mới
router.post('/', async (req, res) => {
  const food = new Food(req.body);
  try {
    const newFood = await food.save();
    res.status(201).json(newFood);
  } catch (err) {
    res.status(400).json({ message: 'Lỗi khi thêm món ăn: ' + err.message });
  }
});

// Cập nhật món ăn
router.put('/:id', async (req, res) => {
  try {
    const food = await Food.findOneAndUpdate({ Id: req.params.id }, req.body, { new: true });
    if (food) {
      res.json(food);
    } else {
      res.status(404).json({ message: 'Không tìm thấy món ăn' });
    }
  } catch (err) {
    res.status(400).json({ message: 'Lỗi khi cập nhật món ăn: ' + err.message });
  }
});

// Xóa món ăn
router.delete('/:id', async (req, res) => {
  try {
    const food = await Food.findOneAndDelete({ Id: req.params.id });
    if (food) {
      res.json({ message: 'Món ăn đã được xóa' });
    } else {
      res.status(404).json({ message: 'Không tìm thấy món ăn' });
    }
  } catch (err) {
    res.status(500).json({ message: 'Lỗi khi xóa món ăn: ' + err.message });
  }
});

module.exports = router;