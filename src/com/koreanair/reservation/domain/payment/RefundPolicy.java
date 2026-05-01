package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;

public interface RefundPolicy {

    BigDecimal calculateRefundAmount(BigDecimal baseAmount);

    String getRefundType();
}
