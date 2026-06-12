package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.model.StatisticSummary;
import com.brewpoint.pos.service.StatisticService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatisticController {
    private final StatisticService statisticService = new StatisticService();

    public StatisticSummary summary(LocalDate selectedDate) throws SQLException {
        return statisticService.summary(selectedDate);
    }

    public List<ProductSalesStat> topProducts(LocalDate selectedDate) throws SQLException {
        return statisticService.topProducts(selectedDate);
    }
}
