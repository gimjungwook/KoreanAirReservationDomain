package com.koreanair.reservation.domain.reservation;

import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.flight.Fare;
import com.koreanair.reservation.domain.flight.FlightSchedule;

public class ReservationItem {

    private Long reservationItemId;
    private FlightSchedule flightSchedule;
    private Fare selectedFare;
    private List<Ticket> tickets = new ArrayList<>();

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public Fare getSelectedFare() {
        return selectedFare;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
