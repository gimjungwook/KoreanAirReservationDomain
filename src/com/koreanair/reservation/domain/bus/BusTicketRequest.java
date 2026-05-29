package com.koreanair.reservation.domain.bus;

/**
 * 항공권 발급과 동반 요청되는 우등고속 셔틀 발권 요청.
 *
 * <p>출발 도시(집) + 선택한 운행 스케줄 + 좌석을 캡슐화한다. nullable optional 요청.
 */
public final class BusTicketRequest {

    private final BusCity originCity;
    private final BusSchedule schedule;
    private final BusSeat seat;

    public BusTicketRequest(BusCity originCity, BusSchedule schedule, BusSeat seat) {
        this.originCity = originCity;
        this.schedule = schedule;
        this.seat = seat;
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

    public boolean hasSeatSelection() {
        return schedule != null && seat != null;
    }
}
