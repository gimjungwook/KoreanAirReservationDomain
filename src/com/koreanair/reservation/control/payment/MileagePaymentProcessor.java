package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.domain.passenger.MileageAccount;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.MileagePayment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/**
 * DP#6 Factory Method — ConcreteCreator: 마일리지 결제.
 *
 * <p>외부 gateway 가 아니라 사용자 MileageAccount 잔액에서 차감.
 */
public class MileagePaymentProcessor extends PaymentMethodProcessor {

    private final MileageAccount account;

    public MileagePaymentProcessor(MileageAccount account) {
        super(null);
        this.account = account;
    }

    @Override
    public Payment createPayment() {
        return new MileagePayment();
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.MILEAGE;
    }

    /**
     * Factory Method 의 anOperation(processCharge)은 Creator 에 고정하고, 마일리지 특화 동작은
     * primitive hook authorize() 만 override 한다(외부 gateway 대신 MileageAccount 잔액 차감).
     */
    @Override
    protected boolean authorize(Payment payment) {
        if (account == null) {
            throw new IllegalStateException("마일리지 계정이 필요합니다.");
        }
        return payment.getAmount() != null && account.withdraw(payment.getAmount());
    }
}
