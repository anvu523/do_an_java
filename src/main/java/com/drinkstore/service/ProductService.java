package com.drinkstore.service;

import com.drinkstore.dao.ProductDAO;
import com.drinkstore.model.Product;
import com.drinkstore.util.ValidationException;
import com.drinkstore.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> findAll() throws SQLException {
        return productDAO.findAll();
    }

    public List<Product> findActive() throws SQLException {
        return productDAO.findActive();
    }

    public List<Product> search(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        return productDAO.search(keyword, categoryId, activeOnly);
    }

    public int create(Product product) throws SQLException {
        validate(product);
        return productDAO.insert(product);
    }

    public void update(Product product) throws SQLException {
        validate(product);
        productDAO.update(product);
    }

    public void delete(int productId) throws SQLException {
        productDAO.delete(productId);
    }

    private void validate(Product product) {
        if (product.getCategoryId() <= 0) {
            throw new ValidationException("Phải chọn loại sản phẩm.");
        }
        product.setName(ValidationUtil.requireText(product.getName(), "Tên sản phẩm"));
        if (product.getPrice() == null || product.getPrice().signum() <= 0) {
            throw new ValidationException("Giá bán phải lớn hơn 0.");
        }
        if (product.getStockQuantity() < 0) {
            throw new ValidationException("Số lượng tồn phải lớn hơn hoặc bằng 0.");
        }
        if (product.getStatus() == null) {
            throw new ValidationException("Phải chọn trạng thái sản phẩm.");
        }
    }
}
