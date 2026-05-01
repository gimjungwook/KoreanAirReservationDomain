package com.koreanair.reservation.domain.flight;

import java.math.BigDecimal;

import com.koreanair.reservation.domain.payment.FullRefundPolicy;
import com.koreanair.reservation.domain.payment.NoRefundPolicy;
import com.koreanair.reservation.domain.payment.PartialRefundPolicy;
import com.koreanair.reservation.domain.payment.RefundPolicy;

/**
 * FareRule — 운임 규칙 도메인 객체.
 *
 * <p>Iteration 2: {@link #checkRefundPolicy()} 가 운임 정보를 보고 적절한 RefundPolicy
 * 구현(Strategy) 을 반환한다. RefundHandler 의 private resolvePolicy 와 동일한 분기 정책을
 * 도메인에 노출시킨다.
 */
public class FareRule {

    private Long fareRuleId;
    private String fareClass;
    private boolean refundable;
    private BigDecimal changeFee;
    private BigDecimal cancellationPenalty;

    public String getFareClass() {
        return fareClass;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public BigDecimal getChangeFee() {
        return changeFee;
    }

    public BigDecimal getCancellationPenalty() {
        return cancellationPenalty;
    }

    /**
     * 운임 규칙에 따른 환불 정책(Strategy)을 결정한다.
     * <ul>
     *   <li>환불 불가 운임 → {@link NoRefundPolicy}</li>
     *   <li>fareClass 가 "Y" 또는 "B" → {@link FullRefundPolicy}</li>
     *   <li>그 외 → {@link PartialRefundPolicy}</li>
     * </ul>
     */
    public RefundPolicy checkRefundPolicy() {
        if (!refundable) {
            return new NoRefundPolicy();
        }
        if ("Y".equals(fareClass) || "B".equals(fareClass)) {
            return new FullRefundPolicy();
        }
        return new PartialRefundPolicy();
    }
}
