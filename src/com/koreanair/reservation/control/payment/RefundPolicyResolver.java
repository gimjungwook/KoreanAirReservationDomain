package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.payment.FullRefundPolicy;
import com.koreanair.reservation.domain.payment.NoRefundPolicy;
import com.koreanair.reservation.domain.payment.PartialRefundPolicy;
import com.koreanair.reservation.domain.payment.RefundPolicy;

/**
 * DP#1 Strategy — 정책 선택(Resolver/팩토리).
 *
 * <p>운임 규칙(FareRule)을 보고 적용할 RefundPolicy(ConcreteStrategy)를 결정해 반환한다.
 * 이 분기는 원래 {@code RefundHandler.resolvePolicy} 안에 if/else 로 들어 있던 것을 그대로
 * 추출한 것이다. 추출 결과 RefundHandler(교과서 Context)는 정책 "선택" 책임을 더 이상 갖지
 * 않고 {@code setStrategy(resolver.resolve(fareRule))} + delegate 만 수행하므로, 교과서
 * Context/Strategy 역할 분리가 더 또렷해진다.
 *
 * <p>매핑 규칙(동작 불변):
 * <ul>
 *   <li>fareRule == null 또는 환불 불가(!isRefundable) → {@link NoRefundPolicy}</li>
 *   <li>fareClass 가 "Y" 또는 "B" → {@link FullRefundPolicy}</li>
 *   <li>그 외 → {@link PartialRefundPolicy}</li>
 * </ul>
 *
 * <p>새 환불 정책을 추가할 때 RefundHandler 를 건드리지 않고 이 Resolver 의 분기와 정책 클래스만
 * 늘리면 된다(Open-Closed Principle).
 */
public class RefundPolicyResolver {

    /**
     * 운임 규칙에 대응하는 환불 정책(Strategy)을 생성해 반환한다.
     *
     * @param fareRule 환불 대상 예약의 운임 규칙(없으면 null 가능)
     * @return 운임 규칙에 맞는 RefundPolicy ConcreteStrategy
     */
    public RefundPolicy resolve(FareRule fareRule) {
        if (fareRule == null || !fareRule.isRefundable()) {
            return new NoRefundPolicy();
        }
        String fareClass = fareRule.getFareClass();
        if ("Y".equals(fareClass) || "B".equals(fareClass)) {
            return new FullRefundPolicy();
        }
        return new PartialRefundPolicy();
    }
}
