package com.drinkstore.controller;

import com.drinkstore.model.Order;
import com.drinkstore.model.OrderDetail;
import com.drinkstore.service.OrderService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderController {
    private final OrderService orderService = new OrderService();

    public List<Order> findOrders(Integer orderId, LocalDate date, Integer employeeId) throws SQLException {
        return orderService.findOrders(orderId, date, employeeId);
    }

    public List<OrderDetail> findDetails(int orderId) throws SQLException {
        return orderService.findDetails(orderId);
    }
}
