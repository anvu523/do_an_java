package com.drinkstore.controller;

import com.drinkstore.model.ProductSalesStat;
import com.drinkstore.service.StatisticService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatisticController {
    private final StatisticService statisticService = new StatisticService();

    public BigDecimal revenueByDate(LocalDate date) throws SQLException {
        return statisticService.revenueByDate(date);
    }

    public BigDecimal revenueByMonth(int year, int month) throws SQLException {
        return statisticService.revenueByMonth(year, month);
    }

    public int countOrders() throws SQLException {
        return statisticService.countOrders();
    }

    public List<ProductSalesStat> topSellingProducts(int limit) throws SQLException {
        return statisticService.topSellingProducts(limit);
    }
}
