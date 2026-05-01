package com.koreanair.reservation.domain.passenger;

public class Guest extends Passenger {

    private String guestSessionId;
    private String pnr;
    private String name;
    private String email;

    public Guest() {
    }

    public Guest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getGuestSessionId() {
        return guestSessionId;
    }

    public void setGuestSessionId(String guestSessionId) {
        this.guestSessionId = guestSessionId;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    @Override
    public String getName() {
        return name != null ? name : super.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean verifyIdentity(String pnr, String name, String email) {
        if (this.pnr == null || pnr == null) {
            return false;
        }
        if (!this.pnr.equals(pnr)) {
            return false;
        }
        if (this.name == null || name == null
                || !this.name.trim().equalsIgnoreCase(name.trim())) {
            return false;
        }
        if (this.email == null || email == null
                || !this.email.trim().equalsIgnoreCase(email.trim())) {
            return false;
        }
        return true;
    }
}
