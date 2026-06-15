package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.StatisticDAO;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.model.StatisticSummary;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatisticService {
    private final StatisticDAO statisticDAO ;

    public StatisticService(StatisticDAO statisticDAO) {
        this.statisticDAO = statisticDAO;
    }

    public StatisticSummary summary(LocalDate selectedDate, Integer employeeId) throws SQLException {
        return statisticDAO.summary(selectedDate, employeeId);
    }

    public List<ProductSalesStat> topProducts(LocalDate selectedDate, Integer employeeId) throws SQLException {
        return statisticDAO.topProducts(selectedDate, employeeId);
    }
}
