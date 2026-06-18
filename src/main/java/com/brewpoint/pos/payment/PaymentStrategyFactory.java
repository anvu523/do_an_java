package com.brewpoint.pos.payment;

import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.util.ValidationException;

public final class PaymentStrategyFactory {
    private PaymentStrategyFactory() {
    }

    public static PaymentStrategy create(PaymentMethod method) {
        if (method == PaymentMethod.CASH) {
            return new CashPaymentStrategy();
        }
        if (method == PaymentMethod.MANUAL_BANK_TRANSFER) {
            return new ManualBankTransferStrategy();
        }
        throw new ValidationException("Phương thức thanh toán không hợp lệ.");
    }
}
