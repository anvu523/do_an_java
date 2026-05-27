package com.drinkstore.controller;

import com.drinkstore.model.OrderDetail;
import com.drinkstore.service.SaleService;

import java.sql.SQLException;
import java.util.List;

public class SaleController {
    private final SaleService saleService = new SaleService();

    public int checkout(int employeeId, List<OrderDetail> cartItems) throws SQLException {
        return saleService.checkout(employeeId, cartItems);
    }
}
