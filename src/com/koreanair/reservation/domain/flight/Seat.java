package com.koreanair.reservation.domain.flight;

public class Seat {

    private String seatNumber;
    private CabinClass cabinClass;
    private SeatStatus status;
    private boolean windowSeat;
    private boolean aisleSeat;
    private boolean extraLegroom;

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
        this.status = SeatStatus.HELD;
    }

    public void hold(int timeoutMinutes) {
        hold();
    }

    public void updateStatus(SeatStatus newStatus) {
        this.status = newStatus;
    }

    public void updateStatus(String newStatus) {
        this.status = SeatStatus.valueOf(newStatus);
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }
}
