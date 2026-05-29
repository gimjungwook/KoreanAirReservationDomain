package com.koreanair.reservation.domain.bus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 항공권 구매와 연계 발매되는 우등고속 프리미엄 셔틀 티켓.
 *
 * <p>방향: 출발 도시(집) → 인천공항(ICN). Iteration 4 에서 의미 정정.
 * 좌석 선택과 운행 스케줄 정보를 함께 보관.
 */
public class BusTicket {

    private static final DateTimeFormatter BUS_DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");
    private static int busTicketSequence = 1;

    private final String ticketNumber;
    private final String reservationPnr;
    private final String airTicketNumber;
    private final String passengerName;
    private final BusCity originCity;
    private final BusSchedule schedule;
    private final BusSeat seat;
    private final long fare;
    private final LocalDateTime issuedAt;

    private BusTicket(String ticketNumber,
                      String reservationPnr,
                      String airTicketNumber,
                      String passengerName,
                      BusCity originCity,
                      BusSchedule schedule,
                      BusSeat seat,
                      long fare) {
        this.ticketNumber = ticketNumber;
        this.reservationPnr = reservationPnr;
        this.airTicketNumber = airTicketNumber;
        this.passengerName = passengerName;
        this.originCity = originCity;
        this.schedule = schedule;
        this.seat = seat;
        this.fare = fare;
        this.issuedAt = LocalDateTime.now();
    }

    public static BusTicket issue(String reservationPnr,
                                  String airTicketNumber,
                                  String passengerName,
                                  BusCity originCity,
                                  BusSchedule schedule,
                                  BusSeat seat) {
        if (originCity == null) {
            throw new IllegalArgumentException("출발 도시는 필수입니다.");
        }
        String ticketNumber = String.format("KOBUS-%s-%04d",
                LocalDateTime.now().format(BUS_DATE_FMT), busTicketSequence++);
        return new BusTicket(ticketNumber, reservationPnr, airTicketNumber,
                passengerName, originCity, schedule, seat, originCity.getPremiumFare());
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

    public BusCity getOriginCity() {
        return originCity;
    }

    public BusSchedule getSchedule() {
        return schedule;
    }

    public BusSeat getSeat() {
        return seat;
    }

    public long getFare() {
        return fare;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
