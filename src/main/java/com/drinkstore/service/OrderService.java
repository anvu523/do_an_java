package com.drinkstore.service;

import com.drinkstore.dao.OrderDAO;
import com.drinkstore.model.Order;
import com.drinkstore.model.OrderDetail;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();

    public List<Order> findOrders(Integer orderId, LocalDate date, Integer employeeId) throws SQLException {
        return orderDAO.findOrders(orderId, date, employeeId);
    }

    public List<OrderDetail> findDetails(int orderId) throws SQLException {
        return orderDAO.findDetails(orderId);
    }
}
