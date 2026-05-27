package com.drinkstore.service;

import com.drinkstore.dao.CategoryDAO;
import com.drinkstore.model.Category;
import com.drinkstore.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> findAll() throws SQLException {
        return categoryDAO.findAll();
    }

    public List<Category> search(String keyword) throws SQLException {
        return categoryDAO.searchByName(keyword == null ? "" : keyword.trim());
    }

    public int create(Category category) throws SQLException {
        validate(category);
        return categoryDAO.insert(category);
    }

    public void update(Category category) throws SQLException {
        validate(category);
        categoryDAO.update(category);
    }

    public void delete(int categoryId) throws SQLException {
        categoryDAO.delete(categoryId);
    }

    private void validate(Category category) {
        category.setName(ValidationUtil.requireText(category.getName(), "Tên loại"));
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }
    }
}
