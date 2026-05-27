package com.drinkstore.controller;

import com.drinkstore.model.Category;
import com.drinkstore.service.CategoryService;

import java.sql.SQLException;
import java.util.List;

public class CategoryController {
    private final CategoryService categoryService = new CategoryService();

    public List<Category> findAll() throws SQLException {
        return categoryService.findAll();
    }

    public List<Category> search(String keyword) throws SQLException {
        return categoryService.search(keyword);
    }

    public int create(Category category) throws SQLException {
        return categoryService.create(category);
    }

    public void update(Category category) throws SQLException {
        categoryService.update(category);
    }

    public void delete(int categoryId) throws SQLException {
        categoryService.delete(categoryId);
    }
}
