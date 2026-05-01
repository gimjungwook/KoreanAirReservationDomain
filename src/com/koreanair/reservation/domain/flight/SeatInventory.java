package com.koreanair.reservation.domain.flight;

public class SeatInventory {

    private static long idSequence = 0L;

    private Long inventoryId;
    private BookingClass bookingClass;
    private int totalSeats;
    private int availableSeats;

    public SeatInventory() {
        this.inventoryId = ++idSequence;
    }

    public SeatInventory(BookingClass bookingClass, int totalSeats) {
        this.inventoryId = ++idSequence;
        this.bookingClass = bookingClass;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean canReserve(int seatCount) {
        return availableSeats >= seatCount;
    }

    public boolean reserve(BookingClass bookingClass) {
        if (this.bookingClass == bookingClass && availableSeats > 0) {
            availableSeats--;
            return true;
        }
        return false;
    }

    public boolean release(BookingClass bookingClass) {
        if (this.bookingClass == bookingClass && availableSeats < totalSeats) {
            availableSeats++;
            return true;
        }
        return false;
    }

    public void adjustTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}
