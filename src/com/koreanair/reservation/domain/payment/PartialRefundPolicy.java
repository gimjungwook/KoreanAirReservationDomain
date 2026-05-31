package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PartialRefundPolicy implements RefundPolicy {

    private static final BigDecimal HALF = new BigDecimal("0.5");

    @Override
    public BigDecimal calculateRefundAmount(BigDecimal baseAmount) {
        if (baseAmount == null) {
            return BigDecimal.ZERO;
        }
        return baseAmount.multiply(HALF).setScale(0, RoundingMode.HALF_UP);
    }
}
