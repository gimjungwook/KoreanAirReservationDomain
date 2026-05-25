package com.koreanair.reservation.domain.bus;

/**
 * Iteration 3 추가 요구사항: 대한항공 항공권 구매와 연계되는
 * 6개 대도시 우등고속 버스 티켓 목적지.
 */
public enum BusCity {
    SEOUL("서울", "SEL", "Korea", 18000L),
    BUSAN("부산", "PUS", "Korea", 39000L),
    DAEGU("대구", "TAE", "Korea", 31000L),
    GWANGJU("광주", "KWJ", "Korea", 34000L),
    DAEJEON("대전", "DJE", "Korea", 22000L),
    INCHEON("인천", "ICN", "Korea", 17000L),
    TOKYO("도쿄", "NRT", "Japan", 24000L),
    LOS_ANGELES("로스앤젤레스", "LAX", "USA", 36000L),
    NEW_YORK("뉴욕", "JFK", "USA", 42000L),
    SINGAPORE("싱가포르", "SIN", "Singapore", 28000L),
    SYDNEY("시드니", "SYD", "Australia", 38000L),
    PARIS("파리", "CDG", "France", 33000L);

    private final String displayName;
    private final String cityCode;
    private final String country;
    private final long premiumFare;

    BusCity(String displayName, String cityCode, String country, long premiumFare) {
        this.displayName = displayName;
        this.cityCode = cityCode;
        this.country = country;
        this.premiumFare = premiumFare;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCountry() {
        return country;
    }

    public long getPremiumFare() {
        return premiumFare;
    }

    @Override
    public String toString() {
        return displayName + " (" + country + " · " + cityCode + ")";
    }
}
