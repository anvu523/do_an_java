package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.OrderItemDetail;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.service.OrderService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderController {
    private final OrderService orderService ;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<OrderSummary> search(String orderCode, LocalDate date, Integer employeeId) throws SQLException {
        return orderService.search(orderCode, date, employeeId);
    }

    public List<OrderItemDetail> findDetails(long orderId) throws SQLException {
        return orderService.findDetails(orderId);
    }
}
