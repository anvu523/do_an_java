package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.model.Topping;
import com.brewpoint.pos.service.CategoryService;
import com.brewpoint.pos.service.ProductService;
import com.brewpoint.pos.service.ToppingService;

import java.sql.SQLException;
import java.util.List;

public class CatalogController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ToppingService toppingService;

    public CatalogController(CategoryService categoryService, ProductService productService, ToppingService toppingService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.toppingService = toppingService;
    }

    public List<Category> findCategories(boolean activeOnly) throws SQLException {
        return categoryService.findAll(activeOnly);
    }

    public int createCategory(Category category) throws SQLException {
        return categoryService.create(category);
    }

    public void updateCategory(Category category) throws SQLException {
        categoryService.update(category);
    }

    public void deactivateCategory(int categoryId) throws SQLException {
        categoryService.deactivate(categoryId);
    }

    public void updateCategoryDisplayOrders(List<Category> categories) throws SQLException {
        categoryService.updateDisplayOrders(categories);
    }

    public List<Product> searchProducts(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        return productService.search(keyword, categoryId, activeOnly);
    }

    public Product findProduct(int productId) throws SQLException {
        return productService.findById(productId);
    }

    public int createProduct(Product product) throws SQLException {
        return productService.create(product);
    }

    public void updateProduct(Product product) throws SQLException {
        productService.update(product);
    }

    public void deactivateProduct(int productId) throws SQLException {
        productService.deactivate(productId);
    }

    public List<ProductSize> findSizes(int productId, boolean activeOnly) throws SQLException {
        return productService.findSizes(productId, activeOnly);
    }

    public int createSize(ProductSize size) throws SQLException {
        return productService.createSize(size);
    }

    public void updateSize(ProductSize size) throws SQLException {
        productService.updateSize(size);
    }

    public void deactivateSize(int productSizeId) throws SQLException {
        productService.deactivateSize(productSizeId);
    }

    public List<Topping> findToppings(boolean activeOnly) throws SQLException {
        return toppingService.findAll(activeOnly);
    }

    public int createTopping(Topping topping) throws SQLException {
        return toppingService.create(topping);
    }

    public void updateTopping(Topping topping) throws SQLException {
        toppingService.update(topping);
    }

    public void deactivateTopping(int toppingId) throws SQLException {
        toppingService.deactivate(toppingId);
    }
}
