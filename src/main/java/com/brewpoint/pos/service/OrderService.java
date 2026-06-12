package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.OrderDAO;
import com.brewpoint.pos.model.OrderItemDetail;
import com.brewpoint.pos.model.OrderSummary;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();

    public List<OrderSummary> search(String orderCode, LocalDate date, Integer employeeId) throws SQLException {
        return orderDAO.search(orderCode, date, employeeId);
    }

    public List<OrderItemDetail> findDetails(long orderId) throws SQLException {
        return orderDAO.findDetails(orderId);
    }
}
