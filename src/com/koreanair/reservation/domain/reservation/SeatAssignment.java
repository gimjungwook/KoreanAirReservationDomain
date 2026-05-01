package com.koreanair.reservation.domain.reservation;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.Seat;

public class SeatAssignment {

    private static long idSequence = 0L;

    private Long seatAssignmentId;
    private FlightSchedule flightSchedule;
    private Seat seat;
    private SeatAssignmentStatus status;

    public SeatAssignment() {
        this.seatAssignmentId = ++idSequence;
        this.status = SeatAssignmentStatus.RESERVED;
    }

    public SeatAssignment(FlightSchedule schedule, Seat seat) {
        this.seatAssignmentId = ++idSequence;
        this.flightSchedule = schedule;
        this.seat = seat;
        this.status = SeatAssignmentStatus.RESERVED;
    }

    public Long getSeatAssignmentId() {
        return seatAssignmentId;
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public Seat getSeat() {
        return seat;
    }

    public SeatAssignmentStatus getStatus() {
        return status;
    }

    public void changeSeat(Seat newSeat) {
        this.seat = newSeat;
    }

    public void cancel() {
        this.status = SeatAssignmentStatus.CANCELLED;
    }
}
