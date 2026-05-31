package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;

public class FullRefundPolicy implements RefundPolicy {

    @Override
    public BigDecimal calculateRefundAmount(BigDecimal baseAmount) {
        return baseAmount;
    }
}
