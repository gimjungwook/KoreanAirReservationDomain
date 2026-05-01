package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund — 환불 트랜잭션 결과 객체.
 *
 * <p>Iteration 2: RefundHandler 가 정책에 따라 산정한 금액으로 인스턴스를 생성한 뒤
 * approve / reject / complete 단계별 상태 전이를 거친다.
 */
public class Refund {

    private static long refundIdSequence = 0L;

    private Long refundId;
    private String refundIdString;
    private BigDecimal refundAmount;
    private RefundStatus status;
    private String reason;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public Refund(String refundId, BigDecimal refundAmount, String reason) {
        this.refundId = ++refundIdSequence;
        this.refundIdString = refundId;
        this.refundAmount = refundAmount;
        this.reason = reason;
        this.status = RefundStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
        this.completedAt = null;
    }

    public Long getRefundId() {
        return refundId;
    }

    public String getRefundIdString() {
        return refundIdString;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void approve() {
        this.status = RefundStatus.APPROVED;
    }

    public void reject() {
        this.status = RefundStatus.REJECTED;
    }

    public void reject(String reason) {
        this.status = RefundStatus.REJECTED;
        this.reason = reason;
    }

    public void complete() {
        this.status = RefundStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
