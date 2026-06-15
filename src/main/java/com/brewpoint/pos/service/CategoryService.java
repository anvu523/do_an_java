package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.CategoryDAO;
import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO ;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> findAll(boolean activeOnly) throws SQLException {
        return categoryDAO.findAll(activeOnly);
    }

    public int create(Category category) throws SQLException {
        validate(category);
        return categoryDAO.insert(category);
    }

    public void update(Category category) throws SQLException {
        if (category.getCategoryId() <= 0) {
            throw new ValidationException("Chọn danh mục cần sửa.");
        }
        validate(category);
        categoryDAO.update(category);
    }

    public void deactivate(int categoryId) throws SQLException {
        if (categoryId <= 0) {
            throw new ValidationException("Chọn danh mục cần ngừng sử dụng.");
        }
        categoryDAO.deactivate(categoryId);
    }

    private void validate(Category category) {
        category.setName(ValidationUtils.requireText(category.getName(), "Tên danh mục"));
        if (category.getDisplayOrder() < 0) {
            throw new ValidationException("Thứ tự hiển thị không được âm.");
        }
    }
}
