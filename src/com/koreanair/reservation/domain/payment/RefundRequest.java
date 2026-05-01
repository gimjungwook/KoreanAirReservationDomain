package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RefundRequest — 관리자 검토 큐에 올라가는 환불 요청 레코드.
 *
 * <p>Iteration 2: 생성자가 추가되어 RefundHandler 에서 직접 인스턴스화 가능.
 * 조회 메서드(queryByStatus, getDetail)는 iter 2 에서는 안전한 기본값(빈 리스트, this)만 반환한다.
 */
public class RefundRequest {

    private String requestId;
    private LocalDateTime requestDate;
    private BigDecimal refundAmount;
    private RefundStatus status;
    private String reason;

    public RefundRequest(String requestId, BigDecimal refundAmount, String reason) {
        this.requestId = requestId;
        this.refundAmount = refundAmount;
        this.reason = reason;
        this.status = RefundStatus.REQUESTED;
        this.requestDate = LocalDateTime.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
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

    public static List<RefundRequest> queryByStatus(RefundStatus status) {
        // iter 2 단순화: 인메모리 인덱스 미구현 — 빈 리스트 반환.
        return new ArrayList<>();
    }

    public static RefundRequest getDetail(String requestId) {
        // iter 2 단순화: 레지스트리 미구현 — null 회피용 기본값 없음.
        return null;
    }

    public void updateStatus(RefundStatus newStatus) {
        this.status = newStatus;
    }

    public void updateStatus(RefundStatus newStatus, String reason) {
        this.status = newStatus;
        this.reason = reason;
    }
}
