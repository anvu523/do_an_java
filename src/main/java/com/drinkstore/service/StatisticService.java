package com.drinkstore.service;

import com.drinkstore.dao.StatisticDAO;
import com.drinkstore.model.ProductSalesStat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatisticService {
    private final StatisticDAO statisticDAO = new StatisticDAO();

    public BigDecimal revenueByDate(LocalDate date) throws SQLException {
        return statisticDAO.revenueByDate(date);
    }

    public BigDecimal revenueByMonth(int year, int month) throws SQLException {
        return statisticDAO.revenueByMonth(year, month);
    }

    public int countOrders() throws SQLException {
        return statisticDAO.countOrders();
    }

    public List<ProductSalesStat> topSellingProducts(int limit) throws SQLException {
        return statisticDAO.topSellingProducts(limit);
    }
}
