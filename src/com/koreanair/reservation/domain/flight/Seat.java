package com.koreanair.reservation.domain.flight;

import java.time.LocalDateTime;

public class Seat {

    private String seatNumber;
    private CabinClass cabinClass;
    private SeatStatus status;
    private boolean windowSeat;
    private boolean aisleSeat;
    private boolean extraLegroom;
    private LocalDateTime holdExpiresAt;
    private String heldByPnr;

    public Seat() {
    }

    public Seat(String seatNumber, CabinClass cabinClass) {
        this.seatNumber = seatNumber;
        this.cabinClass = cabinClass;
        this.status = SeatStatus.AVAILABLE;
        this.windowSeat = false;
        this.aisleSeat = false;
        this.extraLegroom = false;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public boolean isWindowSeat() {
        return windowSeat;
    }

    public boolean isAisleSeat() {
        return aisleSeat;
    }

    public boolean isExtraLegroom() {
        return extraLegroom;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public void hold() {
        hold(15, null);
    }

    public void hold(int timeoutMinutes) {
        hold(timeoutMinutes, null);
    }

    public void hold(int timeoutMinutes, String pnr) {
        this.status = SeatStatus.HELD;
        this.holdExpiresAt = LocalDateTime.now().plusMinutes(timeoutMinutes);
        this.heldByPnr = pnr;
    }

    public LocalDateTime getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public String getHeldByPnr() {
        return heldByPnr;
    }

    public boolean isHoldExpired() {
        return status == SeatStatus.HELD
                && holdExpiresAt != null
                && LocalDateTime.now().isAfter(holdExpiresAt);
    }

    public boolean isHoldExpiredAt(LocalDateTime now) {
        return status == SeatStatus.HELD
                && holdExpiresAt != null
                && now != null
                && now.isAfter(holdExpiresAt);
    }

    public void updateStatus(SeatStatus newStatus) {
        this.status = newStatus;
    }

    public void updateStatus(String newStatus) {
        this.status = SeatStatus.valueOf(newStatus);
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.holdExpiresAt = null;
        this.heldByPnr = null;
    }
}
