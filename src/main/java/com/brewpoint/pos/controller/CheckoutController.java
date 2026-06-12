package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.CartLine;
import com.brewpoint.pos.model.CartLineRequest;
import com.brewpoint.pos.model.CheckoutRequest;
import com.brewpoint.pos.model.CheckoutResult;
import com.brewpoint.pos.service.CheckoutService;

import java.sql.SQLException;

public class CheckoutController {
    private final CheckoutService checkoutService = new CheckoutService();

    public CartLine previewLine(CartLineRequest request) throws SQLException {
        return checkoutService.previewLine(request);
    }

    public CheckoutResult checkout(CheckoutRequest request) throws SQLException {
        return checkoutService.checkout(request);
    }
}
