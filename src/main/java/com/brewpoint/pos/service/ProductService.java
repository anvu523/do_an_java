package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.ProductDAO;
import com.brewpoint.pos.dao.ProductSizeDAO;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.model.ProductStatus;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final ProductSizeDAO sizeDAO = new ProductSizeDAO();

    public List<Product> search(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        return productDAO.search(keyword, categoryId, activeOnly);
    }

    public Product findById(int productId) throws SQLException {
        return productDAO.findById(productId);
    }

    public int create(Product product) throws SQLException {
        validate(product);
        return productDAO.insert(product);
    }

    public void update(Product product) throws SQLException {
        if (product.getProductId() <= 0) {
            throw new ValidationException("Chọn sản phẩm cần sửa.");
        }
        validate(product);
        productDAO.update(product);
    }

    public void deactivate(int productId) throws SQLException {
        if (productId <= 0) {
            throw new ValidationException("Chọn sản phẩm cần ngừng bán.");
        }
        productDAO.deactivate(productId);
    }

    public List<ProductSize> findSizes(int productId, boolean activeOnly) throws SQLException {
        return sizeDAO.findByProductId(productId, activeOnly);
    }

    public int createSize(ProductSize size) throws SQLException {
        validateSize(size);
        return sizeDAO.insert(size);
    }

    public void updateSize(ProductSize size) throws SQLException {
        if (size.getProductSizeId() <= 0) {
            throw new ValidationException("Chọn size cần sửa.");
        }
        validateSize(size);
        sizeDAO.update(size);
    }

    public void deactivateSize(int productSizeId) throws SQLException {
        if (productSizeId <= 0) {
            throw new ValidationException("Chọn size cần ngừng bán.");
        }
        sizeDAO.deactivate(productSizeId);
    }

    private void validate(Product product) {
        if (product.getCategoryId() <= 0) {
            throw new ValidationException("Phải chọn danh mục.");
        }
        product.setProductCode(ValidationUtils.requireText(product.getProductCode(), "Mã sản phẩm").toUpperCase());
        product.setName(ValidationUtils.requireText(product.getName(), "Tên sản phẩm"));
        if (product.getStockQuantity() < 0) {
            throw new ValidationException("Tồn kho không được âm.");
        }
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.ACTIVE);
        }
    }

    private void validateSize(ProductSize size) {
        if (size.getProductId() <= 0) {
            throw new ValidationException("Size phải thuộc một sản phẩm.");
        }
        size.setSizeCode(ValidationUtils.requireText(size.getSizeCode(), "Mã size").toUpperCase());
        size.setSizeName(ValidationUtils.requireText(size.getSizeName(), "Tên size"));
        if (size.getSalePrice() == null || size.getSalePrice().signum() <= 0) {
            throw new ValidationException("Giá size phải lớn hơn 0.");
        }
    }
}
