package com.brewpoint.pos.strategy;

import com.brewpoint.pos.model.PaymentInput;
import java.math.BigDecimal;

public interface PaymentStrategy {
    PaymentResult validate(BigDecimal trustedTotal, PaymentInput input);
}
