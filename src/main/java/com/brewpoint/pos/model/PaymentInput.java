package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class PaymentInput {
    private PaymentMethod method;
    private BigDecimal receivedAmount;
    private boolean transferConfirmed;

    public PaymentInput() {
    }

    public PaymentInput(PaymentMethod method, BigDecimal receivedAmount, boolean transferConfirmed) {
        this.method = method;
        this.receivedAmount = receivedAmount;
        this.transferConfirmed = transferConfirmed;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public boolean isTransferConfirmed() {
        return transferConfirmed;
    }

    public void setTransferConfirmed(boolean transferConfirmed) {
        this.transferConfirmed = transferConfirmed;
    }
}
