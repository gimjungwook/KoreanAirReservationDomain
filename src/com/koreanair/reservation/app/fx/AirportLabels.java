package com.koreanair.reservation.app.fx;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 공항/도시 코드 → 사용자에게 보여줄 한국어 라벨 매핑.
 *
 * <p>도메인(SampleData)의 {@code airportName} 은 영문 정식 명칭이라 고객 화면용으로는
 * 딱딱하다. 예시 단계에서는 "한국어 도시명 + 코드"만 노출하기로 해서, 화면 표시에 쓰는
 * 매핑을 여기에 따로 둔다(도메인 모델은 건드리지 않는다).
 *
 * <p>{@link #ORDERED} 는 검색 드롭다운 노출 순서 — 도시(Composite) 묶음을 먼저,
 * 그다음 개별 공항을 권역별로 정렬한다.
 */
public final class AirportLabels {

    private AirportLabels() {}

    /** 코드 → 한국어 라벨. LinkedHashMap 이라 드롭다운 노출 순서를 그대로 따른다. */
    public static final Map<String, String> KOREAN = new LinkedHashMap<>();

    static {
        // 도시(Composite) — 권역 내 모든 공항 포함
        KOREAN.put("SEL", "서울 (전체)");
        KOREAN.put("TYO", "도쿄 (전체)");
        KOREAN.put("NYC", "뉴욕 (전체)");
        KOREAN.put("LON", "런던 (전체)");
        // 대한민국
        KOREAN.put("ICN", "인천");
        KOREAN.put("GMP", "김포");
        // 일본
        KOREAN.put("NRT", "도쿄 · 나리타");
        KOREAN.put("HND", "도쿄 · 하네다");
        KOREAN.put("FUK", "후쿠오카");
        // 동남아 · 중화권
        KOREAN.put("SIN", "싱가포르");
        KOREAN.put("BKK", "방콕");
        KOREAN.put("HKG", "홍콩");
        KOREAN.put("PVG", "상하이 · 푸둥");
        KOREAN.put("DEL", "델리");
        // 미주
        KOREAN.put("LAX", "로스앤젤레스");
        KOREAN.put("SFO", "샌프란시스코");
        KOREAN.put("JFK", "뉴욕 · JFK");
        KOREAN.put("LGA", "뉴욕 · 라과디아");
        KOREAN.put("EWR", "뉴욕 · 뉴어크");
        KOREAN.put("BOS", "보스턴");
        KOREAN.put("YYZ", "토론토");
        // 유럽
        KOREAN.put("LHR", "런던 · 히드로");
        KOREAN.put("LGW", "런던 · 개트윅");
        KOREAN.put("STN", "런던 · 스탠스테드");
        KOREAN.put("CDG", "파리 · 샤를드골");
        KOREAN.put("FRA", "프랑크푸르트");
        KOREAN.put("FCO", "로마");
        KOREAN.put("BCN", "바르셀로나");
        KOREAN.put("MAD", "마드리드");
        // 중동
        KOREAN.put("DXB", "두바이");
        // 오세아니아
        KOREAN.put("SYD", "시드니");
        KOREAN.put("MEL", "멜버른");
    }

    /** 검색 드롭다운에 노출할 코드 순서(KOREAN 의 등록 순서와 동일). */
    public static final java.util.List<String> ORDERED =
            java.util.List.copyOf(KOREAN.keySet());

    /** "인천 (ICN)" 형태의 표시 문자열. 매핑이 없으면 코드만 반환. */
    public static String display(String code) {
        if (code == null) return "";
        String key = code.trim().toUpperCase();
        String ko = KOREAN.get(key);
        return ko == null ? key : ko + " (" + key + ")";
    }
}
