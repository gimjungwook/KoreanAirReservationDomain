package com.koreanair.reservation.domain.payment;

/** DP#5 Factory Method — ConcreteProduct: 계좌이체 결제. 결제 수단은 서브클래스가 고정한다(setter 불필요). */
public class BankTransferPayment extends Payment {
    public BankTransferPayment() {
        super(PaymentMethod.BANK_TRANSFER);
    }
}
