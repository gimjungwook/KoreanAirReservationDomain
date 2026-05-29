package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/** DP#6 Factory Method — ConcreteCreator: 계좌이체. */
public class BankTransferPaymentProcessor extends PaymentMethodProcessor {

    public BankTransferPaymentProcessor(PaymentGatewayInterface gateway) {
        super(gateway);
    }

    @Override
    protected Payment createPayment(long amount) {
        return basePayment(amount, PaymentMethod.BANK_TRANSFER);
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.BANK_TRANSFER;
    }

    @Override
    protected String declineReason() {
        return "bank-transfer-timeout";
    }
}
