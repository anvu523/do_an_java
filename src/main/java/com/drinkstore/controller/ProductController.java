package com.drinkstore.controller;

import com.drinkstore.model.Product;
import com.drinkstore.service.ProductService;

import java.sql.SQLException;
import java.util.List;

public class ProductController {
    private final ProductService productService = new ProductService();

    public List<Product> findAll() throws SQLException {
        return productService.findAll();
    }

    public List<Product> findActive() throws SQLException {
        return productService.findActive();
    }

    public List<Product> search(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        return productService.search(keyword, categoryId, activeOnly);
    }

    public int create(Product product) throws SQLException {
        return productService.create(product);
    }

    public void update(Product product) throws SQLException {
        productService.update(product);
    }

    public void delete(int productId) throws SQLException {
        productService.delete(productId);
    }
}
