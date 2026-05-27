package com.drinkstore.service;

import com.drinkstore.dao.OrderDAO;
import com.drinkstore.model.Order;
import com.drinkstore.model.OrderDetail;
import com.drinkstore.util.ValidationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class SaleService {
    private final OrderDAO orderDAO = new OrderDAO();

    public int checkout(int employeeId, List<OrderDetail> cartItems) throws SQLException {
        if (employeeId <= 0) {
            throw new ValidationException("Không xác định được nhân viên lập hóa đơn.");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new ValidationException("Giỏ hàng đang trống.");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail detail : cartItems) {
            if (detail.getProductId() <= 0) {
                throw new ValidationException("Chi tiết hóa đơn thiếu mã sản phẩm.");
            }
            if (detail.getQuantity() <= 0) {
                throw new ValidationException("Số lượng mua phải lớn hơn 0.");
            }
            if (detail.getUnitPrice() == null || detail.getUnitPrice().signum() <= 0) {
                throw new ValidationException("Đơn giá phải lớn hơn 0.");
            }
            detail.setLineTotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            total = total.add(detail.getLineTotal());
        }
        Order order = new Order();
        order.setEmployeeId(employeeId);
        order.setTotalAmount(total);
        order.setDetails(cartItems);
        return orderDAO.insert(order);
    }
}
