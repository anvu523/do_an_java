package com.brewpoint.pos.strategy;

import java.math.BigDecimal;

public class PaymentResult {
    private final BigDecimal receivedAmount;
    private final BigDecimal changeAmount;

    public PaymentResult(BigDecimal receivedAmount, BigDecimal changeAmount) {
        this.receivedAmount = receivedAmount;
        this.changeAmount = changeAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
}
