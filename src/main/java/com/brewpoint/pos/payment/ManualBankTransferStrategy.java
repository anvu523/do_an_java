package com.brewpoint.pos.payment;

import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.util.ValidationException;
import java.math.BigDecimal;

public class ManualBankTransferStrategy implements PaymentStrategy {
    public PaymentResult validate(BigDecimal trustedTotal, PaymentInput input) {
        if (input == null || !input.isTransferConfirmed()) {
            throw new ValidationException("Phải xác nhận đã nhận tiền chuyển khoản.");
        }
        return new PaymentResult(trustedTotal, BigDecimal.ZERO);
    }
}
