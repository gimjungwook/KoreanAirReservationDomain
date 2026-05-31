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
 *
 * <p>교과서 Composite 그림과 동일하게 자식을 Component 타입(-components: 0..* AirportLocation)으로
 * 보유하고, addComponent/removeComponent child-management 연산을 제공한다. 자식이 Component 타입이므로
 * 도시 안에 도시(중첩 Composite)도 담을 수 있다.
 */
public class AirportCity extends AirportLocation {

    private final String cityCode;
    private final String cityName;
    private final String country;
    private final List<AirportLocation> components = new ArrayList<>();

    public AirportCity(String cityCode, String cityName, String country) {
        this.cityCode = Objects.requireNonNull(cityCode);
        this.cityName = Objects.requireNonNull(cityName);
        this.country = country;
    }

    /** 교과서 Composite.addComponent(Component) — 자식(Leaf 또는 중첩 Composite)을 추가. */
    public void addComponent(AirportLocation component) {
        if (component == null) {
            throw new IllegalArgumentException("component must not be null");
        }
        components.add(component);
    }

    /** 교과서 Composite.removeComponent(Component) — 자식을 제거. */
    public void removeComponent(AirportLocation component) {
        components.remove(component);
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
        // 자식 Component 전체를 재귀적으로 평탄화 — Leaf 는 자기 자신, 중첩 Composite 는 그 자식 전체.
        List<Airport> all = new ArrayList<>();
        for (AirportLocation component : components) {
            all.addAll(component.getAirports());
        }
        return Collections.unmodifiableList(all);
    }

    @Override
    public boolean matches(String code) {
        if (code == null) {
            return false;
        }
        if (cityCode.equalsIgnoreCase(code)) {
            return true;
        }
        for (AirportLocation component : components) {
            if (component.matches(code)) {
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
