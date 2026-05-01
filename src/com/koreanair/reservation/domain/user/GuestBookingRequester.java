package com.koreanair.reservation.domain.user;

public class GuestBookingRequester extends User {

    private String guestRequestId;
    private String verificationCode;

    public GuestBookingRequester() {
    }

    public GuestBookingRequester(String guestRequestId) {
        this.guestRequestId = guestRequestId;
    }

    public String getGuestRequestId() {
        return guestRequestId;
    }

    public void setGuestRequestId(String guestRequestId) {
        this.guestRequestId = guestRequestId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean verifyReservationAccess(String pnr, String name, String email) {
        if (pnr == null || pnr.isBlank()) {
            return false;
        }
        if (name == null || name.isBlank()) {
            return false;
        }
        if (email == null || email.isBlank()) {
            return false;
        }
        return true;
    }
}
