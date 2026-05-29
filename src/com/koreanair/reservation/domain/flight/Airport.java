package com.koreanair.reservation.domain.flight;

import java.util.List;

/**
 * Composite 패턴의 Leaf — 단일 공항.
 */
public class Airport implements AirportLocation {

    private String airportCode;
    private String airportName;
    private String city;
    private String country;

    public String getAirportCode() {
        return airportCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String getCode() {
        return airportCode;
    }

    @Override
    public String getDisplayName() {
        return airportName + " (" + airportCode + ")";
    }

    @Override
    public List<Airport> getAirports() {
        return List.of(this);
    }

    @Override
    public boolean matches(String code) {
        return airportCode != null && airportCode.equalsIgnoreCase(code);
    }

    @Override
    public boolean isComposite() {
        return false;
    }
}
