package com.koreanair.reservation.control.payment;

import java.math.BigDecimal;

import com.koreanair.reservation.domain.event.PaymentFailedEvent;
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
    protected Payment createPayment(long amount) {
        return stamp(new MileagePayment(), amount);
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.MILEAGE;
    }

    @Override
    public Payment processCharge(long amount, String reservationPnr) {
        if (account == null) {
            throw new IllegalStateException("마일리지 계정이 필요합니다.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        Payment payment = createPayment(amount);
        boolean charged = account.withdraw(BigDecimal.valueOf(amount));
        if (charged) {
            payment.pay();
        } else {
            payment.fail();
            publish(new PaymentFailedEvent(payment, reservationPnr, "insufficient-mileage"));
        }
        return payment;
    }
}
