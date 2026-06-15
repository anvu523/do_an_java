package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.model.StatisticSummary;
import com.brewpoint.pos.service.StatisticService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatisticController {
    private final StatisticService statisticService ;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public StatisticSummary summary(LocalDate selectedDate, Integer employeeId) throws SQLException {
        return statisticService.summary(selectedDate, employeeId);
    }

    public List<ProductSalesStat> topProducts(LocalDate selectedDate, Integer employeeId) throws SQLException {
        return statisticService.topProducts(selectedDate, employeeId);
    }
}
