package com.koreanair.reservation.domain.passenger;

import java.math.BigDecimal;

public class SkypassMember extends Passenger {

    private String skypassNumber;
    private String tier;
    private MileageAccount mileageAccount;

    public SkypassMember() {
        this.mileageAccount = new MileageAccount();
    }

    public SkypassMember(String skypassNumber, String tier) {
        this.skypassNumber = skypassNumber;
        this.tier = tier;
        this.mileageAccount = new MileageAccount();
    }

    public String getSkypassNumber() {
        return skypassNumber;
    }

    public void setSkypassNumber(String skypassNumber) {
        this.skypassNumber = skypassNumber;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public MileageAccount getMileageAccount() {
        return mileageAccount;
    }

    public BigDecimal getMileageBalance() {
        return mileageAccount != null ? mileageAccount.getBalance() : BigDecimal.ZERO;
    }
}
