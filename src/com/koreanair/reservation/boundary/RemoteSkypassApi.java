package com.koreanair.reservation.boundary;

import java.util.HashMap;
import java.util.Map;

/**
 * DP#8 Adapter — Adaptee (외부 API).
 *
 * <p>실 Skypass HTTP API 를 시뮬레이션하는 raw 인터페이스. 응답은 status/payload Map 형태로
 * 외부 JSON 시그니처와 유사하다. 내부 도메인은 이 형식을 직접 다루지 않는다.
 */
public class RemoteSkypassApi {

    private final Map<String, Integer> balances = new HashMap<>();
    private final Map<String, String> tokens = new HashMap<>();

    public void registerAccount(String memberCode, String token, int mileage) {
        balances.put(memberCode, mileage);
        tokens.put(memberCode, token);
    }

    /**
     * POST /v1/auth — { ok: bool, member: {...} }
     */
    public Map<String, Object> postAuth(String memberCode, String token) {
        Map<String, Object> res = new HashMap<>();
        String expected = tokens.get(memberCode);
        boolean ok = expected != null && expected.equals(token);
        res.put("status", ok ? 200 : 401);
        res.put("ok", ok);
        if (ok) {
            Map<String, Object> member = new HashMap<>();
            member.put("memberCode", memberCode);
            member.put("balance", balances.getOrDefault(memberCode, 0));
            res.put("member", member);
        }
        return res;
    }

    /**
     * GET /v1/mileage/{memberCode} — { balance: int }
     */
    public Map<String, Object> getMileage(String memberCode) {
        Map<String, Object> res = new HashMap<>();
        Integer bal = balances.get(memberCode);
        res.put("status", bal != null ? 200 : 404);
        res.put("balance", bal != null ? bal : 0);
        return res;
    }

    /**
     * POST /v1/mileage/{memberCode}/deduct — { success: bool, remaining: int }
     */
    public Map<String, Object> postDeduct(String memberCode, int amount) {
        Map<String, Object> res = new HashMap<>();
        Integer bal = balances.get(memberCode);
        if (bal == null || amount <= 0 || bal < amount) {
            res.put("status", 400);
            res.put("success", false);
            res.put("remaining", bal != null ? bal : 0);
        } else {
            int remaining = bal - amount;
            balances.put(memberCode, remaining);
            res.put("status", 200);
            res.put("success", true);
            res.put("remaining", remaining);
        }
        return res;
    }
}
