package com.koreanair.reservation.domain.flight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Composite 패턴 사용 측 헬퍼 — 등록된 Airport(Leaf)와 AirportCity(Composite)를 코드로 조회.
 *
 * <p>FlightSearchService가 도시 코드(NYC/TYO/SEL/LON)와 공항 코드(JFK/LGA/EWR/...)를 동일하게
 * 해석하도록 단일 등록소를 제공한다.
 */
public class AirportCatalog {

    private final Map<String, Airport> airportsByCode = new HashMap<>();
    private final Map<String, AirportCity> citiesByCode = new HashMap<>();

    public AirportCatalog registerAirport(Airport airport) {
        if (airport != null && airport.getAirportCode() != null) {
            airportsByCode.put(airport.getAirportCode().toUpperCase(), airport);
        }
        return this;
    }

    public AirportCatalog registerCity(AirportCity city) {
        if (city != null && city.getCityCode() != null) {
            citiesByCode.put(city.getCityCode().toUpperCase(), city);
            for (Airport a : city.getAirports()) {
                registerAirport(a);
            }
        }
        return this;
    }

    /**
     * 도시 코드 우선 조회, 없으면 공항 코드 조회.
     */
    public AirportLocation lookup(String code) {
        if (code == null) {
            return null;
        }
        String key = code.trim().toUpperCase();
        AirportCity city = citiesByCode.get(key);
        if (city != null) {
            return city;
        }
        return airportsByCode.get(key);
    }

    public List<AirportCity> getMultiAirportCities() {
        List<AirportCity> result = new ArrayList<>();
        for (AirportCity c : citiesByCode.values()) {
            if (c.getAirports().size() > 1) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Airport> getAllAirports() {
        return Collections.unmodifiableList(new ArrayList<>(airportsByCode.values()));
    }
}
