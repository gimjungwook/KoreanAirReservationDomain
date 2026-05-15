package com.koreanair.reservation.domain.bus;

/**
 * Iteration 3 추가 요구사항: 대한항공 항공권 구매와 연계되는
 * 6개 대도시 우등고속 버스 티켓 목적지.
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
}
