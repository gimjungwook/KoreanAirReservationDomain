package com.koreanair.reservation.domain.bus;

/**
 * Iteration 3 추가 요구사항: 대한항공 항공권 구매와 연계되는
 * 주요 국가별 도시 우등고속/프리미엄 연계 교통 목적지.
 */
public enum BusCity {
    SEOUL("서울", "SEL", "Korea", 18000L),
    BUSAN("부산", "PUS", "Korea", 39000L),
    DAEGU("대구", "TAE", "Korea", 31000L),
    GWANGJU("광주", "KWJ", "Korea", 34000L),
    DAEJEON("대전", "DJE", "Korea", 22000L),
    INCHEON("인천", "ICN", "Korea", 17000L),
    TOKYO("도쿄", "NRT", "Japan", 24000L),
    OSAKA("오사카", "OSA", "Japan", 27000L),
    NAGOYA("나고야", "NGO", "Japan", 26000L),
    FUKUOKA("후쿠오카", "FUK", "Japan", 22000L),
    LOS_ANGELES("로스앤젤레스", "LAX", "USA", 36000L),
    NEW_YORK("뉴욕", "JFK", "USA", 42000L),
    SAN_FRANCISCO("샌프란시스코", "SFO", "USA", 35000L),
    LAS_VEGAS("라스베이거스", "LAS", "USA", 33000L),
    BOSTON("보스턴", "BOS", "USA", 37000L),
    SINGAPORE("싱가포르", "SIN", "Singapore", 28000L),
    SYDNEY("시드니", "SYD", "Australia", 38000L),
    MELBOURNE("멜버른", "MEL", "Australia", 36000L),
    PARIS("파리", "CDG", "France", 33000L),
    LYON("리옹", "LYS", "France", 30000L),
    FRANKFURT("프랑크푸르트", "FRA", "Germany", 32000L),
    MUNICH("뮌헨", "MUC", "Germany", 31000L),
    LONDON("런던", "LHR", "UK", 35000L),
    MANCHESTER("맨체스터", "MAN", "UK", 31000L),
    ROME("로마", "FCO", "Italy", 32000L),
    MILAN("밀라노", "MIL", "Italy", 30000L),
    TORONTO("토론토", "YYZ", "Canada", 34000L),
    VANCOUVER("밴쿠버", "YVR", "Canada", 33000L),
    BANGKOK("방콕", "BKK", "Thailand", 25000L),
    CHIANG_MAI("치앙마이", "CNX", "Thailand", 23000L),
    HONG_KONG("홍콩", "HKG", "Hong Kong", 26000L),
    SHANGHAI("상하이", "PVG", "China", 24000L),
    BEIJING("베이징", "PEK", "China", 25000L),
    DELHI("델리", "DEL", "India", 27000L),
    MUMBAI("뭄바이", "BOM", "India", 28000L);

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
