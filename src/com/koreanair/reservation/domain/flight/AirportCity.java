package com.koreanair.reservation.domain.flight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Composite 패턴의 Composite — 같은 도시에 여러 공항이 묶이는 메트로 그룹.
 *
 * <p>예) NYC = JFK + LGA + EWR, TYO = NRT + HND, LON = LHR + LGW + STN, SEL = ICN + GMP.
 * 도시 코드(IATA city code)는 공항 코드와 구분된 별도 키로 동작한다.
 */
public class AirportCity implements AirportLocation {

    private final String cityCode;
    private final String cityName;
    private final String country;
    private final List<Airport> airports = new ArrayList<>();

    public AirportCity(String cityCode, String cityName, String country) {
        this.cityCode = Objects.requireNonNull(cityCode);
        this.cityName = Objects.requireNonNull(cityName);
        this.country = country;
    }

    public AirportCity add(Airport airport) {
        if (airport == null) {
            throw new IllegalArgumentException("airport must not be null");
        }
        airports.add(airport);
        return this;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String getCode() {
        return cityCode;
    }

    @Override
    public String getDisplayName() {
        return cityName + " (" + cityCode + ")";
    }

    @Override
    public List<Airport> getAirports() {
        return Collections.unmodifiableList(airports);
    }

    @Override
    public boolean matches(String code) {
        if (code == null) {
            return false;
        }
        if (cityCode.equalsIgnoreCase(code)) {
            return true;
        }
        for (Airport a : airports) {
            if (a.matches(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isComposite() {
        return true;
    }
}
