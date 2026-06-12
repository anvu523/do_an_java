package com.brewpoint.pos.model;

import java.util.ArrayList;
import java.util.List;

public class CheckoutRequest {
    private int employeeId;
    private List<CartLineRequest> lines = new ArrayList<CartLineRequest>();
    private PaymentInput paymentInput;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public List<CartLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<CartLineRequest> lines) {
        this.lines = lines == null ? new ArrayList<CartLineRequest>() : new ArrayList<CartLineRequest>(lines);
    }

    public PaymentInput getPaymentInput() {
        return paymentInput;
    }

    public void setPaymentInput(PaymentInput paymentInput) {
        this.paymentInput = paymentInput;
    }
}
