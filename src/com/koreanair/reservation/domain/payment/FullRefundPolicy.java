package com.koreanair.reservation.domain.payment;

import java.math.BigDecimal;

public class FullRefundPolicy implements RefundPolicy {

    @Override
    public BigDecimal calculateRefundAmount(BigDecimal baseAmount) {
        return baseAmount;
    }

    @Override
    public String getRefundType() {
        return "FULL";
    }
}
