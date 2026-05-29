package com.koreanair.reservation.domain.bus;

/**
 * 우등고속 프리미엄 셔틀 좌석.
 *
 * <p>1+2 배열(통로 기준 좌측 1석, 우측 2석)로 통상 28석 운영. seatNumber 는 "1A", "1B", "1C" 등.
 */
public class BusSeat {

    private final String seatNumber;
    private final boolean windowSeat;
    private final boolean aisleSeat;
    private boolean occupied;
    private String heldByPnr;

    public BusSeat(String seatNumber, boolean windowSeat, boolean aisleSeat) {
        this.seatNumber = seatNumber;
        this.windowSeat = windowSeat;
        this.aisleSeat = aisleSeat;
        this.occupied = false;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean isWindowSeat() {
        return windowSeat;
    }

    public boolean isAisleSeat() {
        return aisleSeat;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public String getHeldByPnr() {
        return heldByPnr;
    }

    public boolean isAvailable() {
        return !occupied;
    }

    public void occupy(String pnr) {
        if (occupied) {
            throw new IllegalStateException("Bus seat already occupied: " + seatNumber);
        }
        this.occupied = true;
        this.heldByPnr = pnr;
    }

    public void release() {
        this.occupied = false;
        this.heldByPnr = null;
    }

    @Override
    public String toString() {
        return seatNumber + (windowSeat ? " (창가)" : aisleSeat ? " (통로)" : "");
    }
}
