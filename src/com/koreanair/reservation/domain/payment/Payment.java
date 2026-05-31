package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DP#6 Factory Method — Product (교과서 그림과 동일하게 abstract).
 * 구체 결제 수단(ConcreteProduct: CreditCardPayment 등)이 자기 PaymentMethod 를 고정한다.
 */
public abstract class Payment {

    private Long paymentId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private List<Refund> refunds = new ArrayList<>();

    /** ConcreteProduct 가 자기 결제 수단을 고정하는 생성자. */
    protected Payment(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public List<Refund> getRefunds() {
        return refunds;
    }

    public void pay() {
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void addRefund(Refund refund) {
        refunds.add(refund);
    }

    public boolean isRefundable() {
        return status == PaymentStatus.PAID || status == PaymentStatus.PARTIALLY_REFUNDED;
    }
}
