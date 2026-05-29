package com.koreanair.reservation.control;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.flight.AirportCatalog;
import com.koreanair.reservation.domain.flight.AirportLocation;
import com.koreanair.reservation.domain.flight.FlightSchedule;

/**
 * FlightSearchService — Control 계층.
 *
 * <p>Iteration 1: in-memory sample dataset 단순 필터.
 * <p>Iteration 4: Composite(AirportLocation) 도입 — 도시 코드(NYC/TYO/SEL/LON)나
 * 공항 코드 어느 쪽이든 동일 인터페이스로 처리. AirportCatalog 가 코드 해석을 담당한다.
 */
public class FlightSearchService {

    private final List<FlightSchedule> catalog = new ArrayList<>();
    private final AirportCatalog airportCatalog;

    public FlightSearchService() {
        this(new AirportCatalog());
    }

    public FlightSearchService(AirportCatalog airportCatalog) {
        this.airportCatalog = airportCatalog != null ? airportCatalog : new AirportCatalog();
    }

    public AirportCatalog getAirportCatalog() {
        return airportCatalog;
    }

    public void addSchedule(FlightSchedule schedule) {
        catalog.add(schedule);
    }

    /**
     * Airport 코드 직접 매칭(legacy). 단일 공항 끼리만 검색.
     */
    public List<FlightSchedule> search(String fromAirportCode, String toAirportCode, LocalDate date) {
        AirportLocation from = airportCatalog.lookup(fromAirportCode);
        AirportLocation to = airportCatalog.lookup(toAirportCode);
        if (from != null && to != null) {
            return searchByLocation(from, to, date);
        }
        List<FlightSchedule> matches = new ArrayList<>();
        for (FlightSchedule schedule : catalog) {
            if (schedule.matchesDirect(fromAirportCode, toAirportCode, date)) {
                matches.add(schedule);
            }
        }
        return matches;
    }

    /**
     * Composite 패턴 활용 검색. AirportCity(다공항 도시)가 들어오면 자식 공항 전체에 대해 매칭.
     */
    public List<FlightSchedule> searchByLocation(AirportLocation from, AirportLocation to, LocalDate date) {
        List<FlightSchedule> matches = new ArrayList<>();
        for (FlightSchedule schedule : catalog) {
            if (schedule.matchesDirect(from, to, date)) {
                matches.add(schedule);
            }
        }
        return matches;
    }

    public List<FlightSchedule> getCatalog() {
        return catalog;
    }
}
