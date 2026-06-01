package com.koreanair.reservation.domain.bus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 출발 도시(집) → 인천공항(ICN) 우등고속 프리미엄 셔틀 운행 스케줄.
 *
 * <p>예) 부산-인천공항 06:00 출발, 11:00 도착. 좌석 28석.
 */
public class BusSchedule {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MM/dd HH:mm");

    private final String scheduleId;
    private final BusCity originCity;
    private final LocalDateTime departureDateTime;
    private final LocalDateTime arrivalDateTime;
    private final List<BusSeat> seats;

    public BusSchedule(String scheduleId,
                       BusCity originCity,
                       LocalDateTime departureDateTime,
                       LocalDateTime arrivalDateTime,
                       List<BusSeat> seats) {
        this.scheduleId = scheduleId;
        this.originCity = originCity;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.seats = new ArrayList<>(seats);
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public BusCity getOriginCity() {
        return originCity;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public List<BusSeat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public List<BusSeat> availableSeats() {
        List<BusSeat> result = new ArrayList<>();
        for (BusSeat s : seats) {
            if (s.isAvailable()) {
                result.add(s);
            }
        }
        return result;
    }

    public BusSeat findSeat(String seatNumber) {
        for (BusSeat s : seats) {
            if (s.getSeatNumber().equalsIgnoreCase(seatNumber)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return originCity.getDisplayName() + " → ICN  "
                + departureDateTime.format(TIME_FMT) + " 출발 · "
                + arrivalDateTime.format(TIME_FMT) + " 도착 · 좌석 "
                + availableSeats().size() + "/" + seats.size();
    }
}
