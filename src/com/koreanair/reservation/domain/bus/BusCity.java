package com.koreanair.reservation.domain.bus;

/**
 * 대한항공 항공권 구매와 연계되는 출발지 도시.
 *
 * <p>국내 6개 대도시(승객 거주지) → 인천공항(ICN) 방면 우등고속 프리미엄 셔틀 버스의 출발 도시 목록.
 * Iteration 4 에서 의미를 "공항→도시 목적지" 에서 "도시→공항 출발지(집에서 공항으로)" 로 정정함.
 */
public enum BusCity {
    SEOUL("서울", "SEL", 18000L),
    BUSAN("부산", "PUS", 39000L),
    DAEGU("대구", "TAE", 31000L),
    GWANGJU("광주", "KWJ", 34000L),
    DAEJEON("대전", "DJE", 22000L),
    INCHEON("인천", "ICN", 17000L);

    private final String displayName;
    private final String cityCode;
    private final long premiumFare;

    BusCity(String displayName, String cityCode, long premiumFare) {
        this.displayName = displayName;
        this.cityCode = cityCode;
        this.premiumFare = premiumFare;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public long getPremiumFare() {
        return premiumFare;
    }

    @Override
    public String toString() {
        return displayName + " (" + cityCode + ")";
    }
}
