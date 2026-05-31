package com.koreanair.reservation.domain.payment;

/** DP#6 Factory Method — ConcreteProduct: 신용카드 결제. 결제 수단은 서브클래스가 고정한다(setter 불필요). */
public class CreditCardPayment extends Payment {
    public CreditCardPayment() {
        super(PaymentMethod.CREDIT_CARD);
    }
}
