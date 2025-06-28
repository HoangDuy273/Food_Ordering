const express = require('express');
const router = express.Router();
const Category = require('../models/category.model')

router.get('/', async (req, res) => {
  try {
    const categories = await Category.find();
    res.json(categories);
  } catch (err) {
    res.status(500).json({ message: 'Lỗi khi lấy danh sách danh mục: ' + err.message });
  }
});

router.post('/', async (req, res) => {
  try {
    const category = new Category(req.body);
    const newCategory = await category.save();
    res.status(201).json(newCategory);
  } catch (err) {
    res.status(400).json({ message: 'Lỗi khi thêm danh mục: ' + err.message });
  }
});

module.exports = router;