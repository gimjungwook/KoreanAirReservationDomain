package com.koreanair.reservation.domain.flight;

import java.util.List;

/**
 * Composite 패턴의 Component.
 *
 * <p>교과서 Composite 그림과 동일하게 Component 역할을 abstract class 로 둔다.
 * 단일 공항(Airport, Leaf)과 다공항 도시(AirportCity, Composite)를 동일 타입으로 다룬다.
 * 검색 입력은 도시 코드(NYC/TYO/SEL/LON)나 공항 코드(JFK/LGA/EWR 등) 어느 쪽이든 받을 수 있다.
 */
public abstract class AirportLocation {

    public abstract String getCode();

    public abstract String getDisplayName();

    /**
     * Composite는 자식 공항 전체, Leaf는 자기 자신만 반환.
     */
    public abstract List<Airport> getAirports();

    /**
     * Composite는 자식 코드 중 하나라도 일치하면 true, Leaf는 자기 코드와 비교.
     */
    public abstract boolean matches(String code);

    /**
     * Composite/Leaf 구분.
     */
    public abstract boolean isComposite();
}
