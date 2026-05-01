package com.koreanair.reservation.domain.passenger;

import java.math.BigDecimal;

public class MileageAccount {

    private Long accountId;
    private BigDecimal balance;

    public MileageAccount() {
        this.balance = BigDecimal.ZERO;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance != null ? balance : BigDecimal.ZERO;
    }

    public void updateBalance(BigDecimal remainingMileage) {
        this.balance = remainingMileage;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null) {
            return;
        }
        this.balance = getBalance().add(amount);
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        if (getBalance().compareTo(amount) >= 0) {
            this.balance = getBalance().subtract(amount);
            return true;
        }
        return false;
    }
}
