package com.koreanair.reservation.domain.bus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 항공권 구매와 연계 발매되는 우등고속 버스 티켓.
 *
 * <p>Iteration 3 발표용 범위에서는 in-memory 발권 객체로 둔다.
 */
public class BusTicket {

    private static final DateTimeFormatter BUS_DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");
    private static int busTicketSequence = 1;

    private final String ticketNumber;
    private final String reservationPnr;
    private final String airTicketNumber;
    private final String passengerName;
    private final BusCity destinationCity;
    private final long fare;
    private final LocalDateTime issuedAt;

    private BusTicket(String ticketNumber,
                      String reservationPnr,
                      String airTicketNumber,
                      String passengerName,
                      BusCity destinationCity,
                      long fare) {
        this.ticketNumber = ticketNumber;
        this.reservationPnr = reservationPnr;
        this.airTicketNumber = airTicketNumber;
        this.passengerName = passengerName;
        this.destinationCity = destinationCity;
        this.fare = fare;
        this.issuedAt = LocalDateTime.now();
    }

    public static BusTicket issue(String reservationPnr,
                                  String airTicketNumber,
                                  String passengerName,
                                  BusCity destinationCity) {
        if (destinationCity == null) {
            throw new IllegalArgumentException("버스 목적 도시는 필수입니다.");
        }
        String ticketNumber = String.format("KOBUS-%s-%04d",
                LocalDateTime.now().format(BUS_DATE_FMT), busTicketSequence++);
        return new BusTicket(ticketNumber, reservationPnr, airTicketNumber,
                passengerName, destinationCity, destinationCity.getPremiumFare());
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getReservationPnr() {
        return reservationPnr;
    }

    public String getAirTicketNumber() {
        return airTicketNumber;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public BusCity getDestinationCity() {
        return destinationCity;
    }

    public long getFare() {
        return fare;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
