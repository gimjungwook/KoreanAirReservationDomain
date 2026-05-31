package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;

/**
 * Strategy 패턴(교과서 그림 5-6) — Strategy 인터페이스.
 * 그림과 동일하게 단 하나의 strategyMethod 만 선언한다: {@link #calculateRefundAmount(BigDecimal)}.
 */
public interface RefundPolicy {

    /** 교과서 strategyMethod() 에 대응. 운임 규칙별 환불액 산정 알고리즘. */
    BigDecimal calculateRefundAmount(BigDecimal baseAmount);
}
