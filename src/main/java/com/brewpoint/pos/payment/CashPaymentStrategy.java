package com.brewpoint.pos.payment;

import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.util.ValidationException;
import java.math.BigDecimal;

public class CashPaymentStrategy implements PaymentStrategy {
    public PaymentResult validate(BigDecimal trustedTotal, PaymentInput input) {
        BigDecimal received = input == null ? null : input.getReceivedAmount();
        if (received == null || received.compareTo(trustedTotal) < 0) {
            throw new ValidationException("Số tiền khách đưa chưa đủ.");
        }
        return new PaymentResult(received, received.subtract(trustedTotal));
    }
}
