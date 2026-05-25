package com.koreanair.reservation.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.flight.Airport;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 대한항공 연계 우등고속 버스 티켓 발매 서비스.
 *
 * <p>현재 iteration에서는 6개 대도시 고정 catalog + in-memory 발매 결과 보관만 수행한다.
 */
public class BusTicketingService {

    private final List<BusTicket> issuedTickets = new ArrayList<>();

    public List<BusCity> supportedCities() {
        return List.of(BusCity.values());
    }

    public List<BusCity> recommendedCities(Reservation reservation) {
        Set<BusCity> recommended = new LinkedHashSet<>();
        Itinerary itinerary = reservation != null ? reservation.getItinerary() : null;
        if (itinerary != null && itinerary.getSegments() != null) {
            for (Segment segment : itinerary.getSegments()) {
                FlightSchedule schedule = segment != null ? segment.getFlightSchedule() : null;
                Airport destination = destinationAirport(schedule);
                BusCity matched = matchCity(destination);
                if (matched != null) {
                    recommended.add(matched);
                }
            }
        }
        if (recommended.isEmpty()) {
            recommended.add(BusCity.SEOUL);
            recommended.add(BusCity.BUSAN);
            recommended.add(BusCity.INCHEON);
        }
        return new ArrayList<>(recommended);
    }

    public BusTicket issuePremiumTicket(Reservation reservation, Ticket airTicket, BusCity city) {
        if (reservation == null || airTicket == null || city == null) {
            throw new IllegalArgumentException("Reservation, airTicket, city가 필요합니다.");
        }
        Passenger passenger = airTicket.getPassenger();
        String passengerName = passenger != null ? passenger.getName() : "(unknown)";
        BusTicket busTicket = BusTicket.issue(
                reservation.getPnrNumber(),
                airTicket.getTicketNumber(),
                passengerName,
                city);
        issuedTickets.add(busTicket);
        System.out.printf("[BUS] premium bus ticket %s issued: city=%s pnr=%s fare=%,d%n",
                busTicket.getTicketNumber(),
                city.getDisplayName() + "/" + city.getCountry(),
                reservation.getPnrNumber(),
                busTicket.getFare());
        return busTicket;
    }

    private BusCity matchCity(Airport airport) {
        if (airport == null || airport.getAirportCode() == null) {
            return null;
        }
        for (BusCity city : BusCity.values()) {
            if (city.getCityCode().equalsIgnoreCase(airport.getAirportCode())) {
                return city;
            }
        }
        return null;
    }

    private Airport destinationAirport(FlightSchedule schedule) {
        if (schedule == null || schedule.getFlight() == null || schedule.getFlight().getRoute() == null) {
            return null;
        }
        return schedule.getFlight().getRoute().getDestination();
    }

    public List<BusTicket> getIssuedTickets() {
        return Collections.unmodifiableList(issuedTickets);
    }
}
