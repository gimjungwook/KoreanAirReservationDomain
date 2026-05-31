package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;

public class NoRefundPolicy implements RefundPolicy {

    @Override
    public BigDecimal calculateRefundAmount(BigDecimal baseAmount) {
        return BigDecimal.ZERO;
    }
}
