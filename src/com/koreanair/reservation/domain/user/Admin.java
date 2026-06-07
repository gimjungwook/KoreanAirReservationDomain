package com.koreanair.reservation.domain.user;

import com.koreanair.reservation.domain.flight.Flight;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.FlightStatus;

public class Admin extends User {

    private String employeeId;
    private String department;

    public FlightSchedule createSchedule(Flight flight) {
        if (flight == null) {
            return null;
        }
        FlightSchedule schedule = new FlightSchedule();
        schedule.setFlight(flight);
        schedule.setStatus(FlightStatus.SCHEDULED);
        return schedule;
    }

    public void changeFlightStatus(FlightSchedule schedule, FlightStatus status) {
        if (schedule != null && status != null) {
            // FlightSchedule(ConcreteSubject)이 상태 전이를 발행 → Observer 통지
            schedule.changeStatus(status);
        }
    }
}
