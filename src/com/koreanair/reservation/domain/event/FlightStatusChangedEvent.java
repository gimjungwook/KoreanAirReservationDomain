package com.koreanair.reservation.domain.event;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.FlightStatus;

/**
 * FlightSchedule.changeStatus 시 발행. Iteration 3.
 *
 * <p>발행자: FlightSchedule. 구독자: AffectedReservationListener
 * (관련 Reservation으로 전파).
 */
public class FlightStatusChangedEvent extends DomainEvent {

    private final FlightSchedule schedule;
    private final FlightStatus previousStatus;
    private final FlightStatus newStatus;

    public FlightStatusChangedEvent(FlightSchedule schedule,
                                    FlightStatus previousStatus,
                                    FlightStatus newStatus) {
        super(schedule != null ? schedule.getFlightNumber() : "unknown");
        this.schedule = schedule;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public FlightSchedule getSchedule() {
        return schedule;
    }

    public FlightStatus getPreviousStatus() {
        return previousStatus;
    }

    public FlightStatus getNewStatus() {
        return newStatus;
    }
}
