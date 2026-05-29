package com.koreanair.reservation.domain.payment;

/**
 * 결제 수단. Iteration 4: APPLE_PAY, KAKAO_PAY 추가.
 */
public enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,
    SIMPLE_PAY,
    MILEAGE,
    APPLE_PAY,
    KAKAO_PAY
}
