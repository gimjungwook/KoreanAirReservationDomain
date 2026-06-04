package com.koreanair.reservation.control;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.control.payment.RefundPolicyResolver;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.payment.NoRefundPolicy;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.Refund;
import com.koreanair.reservation.domain.payment.RefundPolicy;
import com.koreanair.reservation.domain.payment.RefundRequest;
import com.koreanair.reservation.domain.payment.RefundStatus;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;

/**
 * RefundHandler — Control 계층 환불 흐름 오케스트레이터.
 *
 * <p>Iteration 2 인메모리 구현:
 * <ul>
 *   <li>{@link #evaluateRefund(String, String)} : 환불 요청 생성 후 pending 큐 등록.</li>
 *   <li>{@link #processRefund(String, BigDecimal)} : 승인 → 게이트웨이 호출 → 완료.</li>
 *   <li>{@link #denyRefund(String, String)} : 거절 처리.</li>
 * </ul>
 * <p>Iteration 3+ : DB persistence, 비동기 PG 콜백 처리.
 */
public class RefundHandler {

    private final Map<String, RefundRequest> pending = new HashMap<>();
    private final Map<String, Refund> completed = new HashMap<>();
    private final Map<String, String> requestToPnr = new HashMap<>();
    private final PaymentGatewayInterface gateway;
    /**
     * Strategy 패턴 Context 역할 — 교과서 그림과 동일하게 Context 가 현재 전략(RefundPolicy)을
     * 필드로 보유한다(-strategy). 디폴트는 NoRefundPolicy, {@link #setStrategy(RefundPolicy)} 로 런타임 교체.
     */
    private RefundPolicy strategy = new NoRefundPolicy();
    /** DP#1 Strategy — 정책 선택(팩토리)을 전담하는 Resolver. RefundHandler 는 setStrategy + delegate 만 수행한다. */
    private final RefundPolicyResolver policyResolver = new RefundPolicyResolver();
    private static int requestSeq = 1;
    private static int refundSeq = 1;
    private static final java.time.format.DateTimeFormatter REFUND_YYMM =
            java.time.format.DateTimeFormatter.ofPattern("yyMM");

    public RefundHandler() {
        this.gateway = null;
    }

    public RefundHandler(PaymentGatewayInterface gateway) {
        this.gateway = gateway;
    }

    /**
     * Strategy 패턴 — 교과서 Context.setStrategy(Strategy). Context 가 보유한 환불 정책을
     * 런타임에 교체한다. 운임 등급이 바뀌면 핸들러를 고치지 않고 정책 객체만 갈아끼우면 된다.
     */
    public void setStrategy(RefundPolicy strategy) {
        this.strategy = strategy;
    }

    /** 현재 Context 에 적용된 환불 정책(Strategy). */
    public RefundPolicy getStrategy() {
        return this.strategy;
    }

    /**
     * 환불 요청 평가 — Reservation 의 결제액을 기반으로 환불 금액을 산정한 뒤
     * RefundRequest 를 생성하여 pending 큐에 등록한다.
     *
     * @return 새로 생성된 RefundRequest, Reservation 미존재 시 null.
     */
    public RefundRequest evaluateRefund(String pnr, String fareClass) {
        Reservation reservation = Reservation.findByPnr(pnr);
        if (reservation == null) {
            return null;
        }

        // FareRule 탐색: itinerary -> 첫 segment -> flightSchedule. 없으면 fareClass 인자로 합성.
        FareRule fareRule = resolveFareRuleFrom(reservation);
        if (fareRule == null) {
            fareRule = synthesize(fareClass);
        }
        // Strategy 패턴(교과서 그림 5-6) — Context 는 현재 전략을 필드(-strategy)로 교체(setStrategy)한 뒤,
        // 계산은 "보유한 전략 객체"에 위임한다: ContextMethod() -> this.strategy.strategyMethod().
        setStrategy(policyResolver.resolve(fareRule));

        // 결제 합계 계산 — payments.amount 단순 합산.
        BigDecimal paid = BigDecimal.ZERO;
        for (Payment p : reservation.getPayments()) {
            if (p != null && p.getAmount() != null) {
                paid = paid.add(p.getAmount());
            }
        }
        BigDecimal refundAmount = this.strategy.calculateRefundAmount(paid);
        System.out.printf("[STRATEGY] FareRule(%s) -> %s -> %,d KRW%n",
                fareRule.getFareClass() != null ? fareRule.getFareClass() : "?",
                this.strategy.getClass().getSimpleName(),
                refundAmount.longValue());

        String requestId = "REQ-" + (requestSeq++);
        RefundRequest request = new RefundRequest(requestId, refundAmount, "Cancellation refund");
        pending.put(requestId, request);
        requestToPnr.put(requestId, pnr);
        return request;
    }

    /**
     * 환불 승인 처리. 요청을 APPROVED 로 전이시키고 게이트웨이로 환불 호출 후 완료 처리.
     */
    public void processRefund(String requestId, BigDecimal approvedAmount) {
        RefundRequest request = pending.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + requestId);
        }
        request.updateStatus(RefundStatus.APPROVED);

        String refundId = String.format("Refund-%s-%03d",
                java.time.LocalDate.now().format(REFUND_YYMM), refundSeq++);
        Refund refund = new Refund(refundId, approvedAmount, request.getReason());
        refund.approve();

        // 게이트웨이 환불 호출 — Reservation 의 첫 결제건을 환불 대상으로 사용.
        if (gateway != null) {
            String pnr = requestToPnr.get(requestId);
            if (pnr != null) {
                Reservation reservation = Reservation.findByPnr(pnr);
                if (reservation != null && !reservation.getPayments().isEmpty()) {
                    Payment payment = reservation.getPayments().get(0);
                    gateway.refund(payment, approvedAmount);
                }
            }
        }

        refund.complete();
        System.out.println("[REFUND] " + refund.getRefundIdString() + " disbursed: " + approvedAmount);

        pending.remove(requestId);
        completed.put(requestId, refund);
    }

    /** 환불 거절 처리. */
    public void denyRefund(String requestId, String reason) {
        RefundRequest request = pending.get(requestId);
        if (request != null) {
            request.updateStatus(RefundStatus.REJECTED);
        }
        BigDecimal amount = request != null ? request.getRefundAmount() : BigDecimal.ZERO;
        Refund refund = new Refund("REF-" + requestId, amount, reason);
        refund.reject(reason);

        pending.remove(requestId);
        completed.put(requestId, refund);
        System.out.println("[REFUND] " + requestId + " denied: " + reason);
    }

    /** 단건 조회 — pending 에서 우선 조회. iter 2 단순화: completed 는 RefundRequest 가 아니라 Refund 라 미반영. */
    public RefundRequest getRefundDetail(String requestId) {
        return pending.get(requestId);
    }

    /** 처리 대기중인 환불 요청 전체 조회. */
    public List<RefundRequest> getPendingRequests() {
        return new ArrayList<>(pending.values());
    }

    /**
     * 환불 미리보기 — pending 큐에 등록하지 않고 정책/금액만 계산한다.
     * GUI 의 "환불 미리보기" 버튼이 사용한다.
     */
    public BigDecimal previewRefund(String pnr, String fareClass) {
        Reservation reservation = Reservation.findByPnr(pnr);
        if (reservation == null) {
            return BigDecimal.ZERO;
        }
        FareRule fareRule = resolveFareRuleFrom(reservation);
        if (fareRule == null) {
            fareRule = synthesize(fareClass);
        }
        setStrategy(policyResolver.resolve(fareRule));
        BigDecimal paid = BigDecimal.ZERO;
        for (Payment p : reservation.getPayments()) {
            if (p != null && p.getAmount() != null) {
                paid = paid.add(p.getAmount());
            }
        }
        return this.strategy.calculateRefundAmount(paid);
    }

    /** 미리보기에서 사용할 정책 이름 (Strategy 클래스명). */
    public String previewPolicyName(String pnr, String fareClass) {
        Reservation reservation = Reservation.findByPnr(pnr);
        if (reservation == null) {
            return "(unknown)";
        }
        FareRule fareRule = resolveFareRuleFrom(reservation);
        if (fareRule == null) {
            fareRule = synthesize(fareClass);
        }
        setStrategy(policyResolver.resolve(fareRule));
        return this.strategy.getClass().getSimpleName();
    }

    /**
     * Iteration 2 단순화: Itinerary -> 첫 Segment -> FlightSchedule -> FareRule 탐색.
     */
    private FareRule resolveFareRuleFrom(Reservation reservation) {
        Itinerary itinerary = reservation.getItinerary();
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return null;
        }
        Segment first = itinerary.getSegments().get(0);
        if (first == null) {
            return null;
        }
        FlightSchedule schedule = first.getFlightSchedule();
        return schedule != null ? schedule.getFareRule() : null;
    }

    /**
     * Iteration 2 단순화: 도메인에서 FareRule 을 못 찾았을 때 fareClass 힌트로 합성하는 fallback.
     * "Y"/"B" 만 환불 가능한 정책으로 처리하는 RefundPolicyResolver 분기를 그대로 활용한다.
     *
     * <p>FareRule 의 setter 가 없어 reflection 없이는 fareClass 주입이 불가하므로
     * 이 fallback 은 "어떤 FareRule 이든 일단 비-null 보장" 정도의 역할이다.
     * (FareRule 의 isRefundable() 디폴트 false 이므로 NoRefundPolicy 로 귀결됨.)
     */
    private FareRule synthesize(String fareClass) {
        // FareRule 에 setter 가 없으므로 빈 FareRule 만 생성. RefundPolicyResolver 가 NoRefundPolicy 로 귀결.
        return new FareRule();
    }
}
