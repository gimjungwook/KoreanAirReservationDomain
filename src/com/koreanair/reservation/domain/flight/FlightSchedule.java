package com.koreanair.reservation.domain.flight;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.event.EventPublisher;
import com.koreanair.reservation.domain.event.FlightStatusChangedEvent;

/**
 * FlightSchedule. Iteration 3에서 Observer 패턴의 Subject로 격상.
 * {@link #changeStatus(FlightStatus)} 호출 시 {@link FlightStatusChangedEvent}를 발행.
 */
public class FlightSchedule extends EventPublisher {

    private Long scheduleId;
    private Flight flight;
    private AircraftType aircraftType;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private FlightStatus status;
    private FareRule fareRule;
    private List<SeatInventory> seatInventories = new ArrayList<>();

    public Long getScheduleId() {
        return scheduleId;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getFlightNumber() {
        return flight != null ? flight.getFlightNumber() : null;
    }

    public Duration getDuration() {
        if (departureDateTime == null || arrivalDateTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(departureDateTime, arrivalDateTime);
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public FareRule getFareRule() {
        return fareRule;
    }

    public void setFareRule(FareRule fareRule) {
        this.fareRule = fareRule;
    }

    public List<SeatInventory> getSeatInventories() {
        return seatInventories;
    }

    public boolean isAvailableForBooking() {
        return status == FlightStatus.SCHEDULED || status == FlightStatus.DELAYED;
    }

    public SeatInventory findSeatInventory(BookingClass bookingClass) {
        return null;
    }

    public void changeStatus(FlightStatus status) {
        FlightStatus previous = this.status;
        this.status = status;
        if (previous != status) {
            publish(new FlightStatusChangedEvent(this, previous, status));
        }
    }

    public void addSeatInventory(SeatInventory seatInventory) {
        seatInventories.add(seatInventory);
    }

    public static FlightSchedule create(String flightNumber, Airport departure, Airport arrival, AircraftType aircraftType) {
        FlightSchedule schedule = new FlightSchedule();
        Route route = new Route();
        route.setOrigin(departure);
        route.setDestination(arrival);
        Flight createdFlight = new Flight();
        createdFlight.setFlightNumber(flightNumber);
        createdFlight.setRoute(route);
        schedule.flight = createdFlight;
        schedule.aircraftType = aircraftType;
        schedule.status = FlightStatus.SCHEDULED;
        return schedule;
    }

    public boolean matchesDirect(String fromAirportCode, String toAirportCode, LocalDate date) {
        if (flight == null || flight.getRoute() == null || departureDateTime == null) {
            return false;
        }
        Airport origin = flight.getRoute().getOrigin();
        Airport destination = flight.getRoute().getDestination();
        return origin != null
                && destination != null
                && origin.getAirportCode().equalsIgnoreCase(fromAirportCode)
                && destination.getAirportCode().equalsIgnoreCase(toAirportCode)
                && departureDateTime.toLocalDate().equals(date)
                && isAvailableForBooking();
    }

    /**
     * Composite 패턴 활용 매칭: AirportLocation(Airport leaf 또는 AirportCity composite) 기반.
     * <p>도시 코드(NYC, TYO, SEL, LON)가 들어오면 해당 도시의 모든 공항 중 하나라도 매칭되면 true.
     */
    public boolean matchesDirect(AirportLocation from, AirportLocation to, LocalDate date) {
        if (from == null || to == null
                || flight == null || flight.getRoute() == null || departureDateTime == null) {
            return false;
        }
        Airport origin = flight.getRoute().getOrigin();
        Airport destination = flight.getRoute().getDestination();
        return origin != null
                && destination != null
                && from.matches(origin.getAirportCode())
                && to.matches(destination.getAirportCode())
                && departureDateTime.toLocalDate().equals(date)
                && isAvailableForBooking();
    }

    public void updateStatus(FlightStatus newStatus) {
        changeStatus(newStatus);
    }
}
