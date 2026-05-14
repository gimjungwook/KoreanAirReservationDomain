package com.koreanair.reservation.boundary;

import java.util.HashMap;
import java.util.Map;

/**
 * Iteration 3 — 외부 Skypass 시스템 mock. {@link SkypassInterface} 구현.
 *
 * <p>실서비스라면 KAL의 외부 마일리지 시스템 API를 호출한다. 본 학습 프로젝트에서는
 * in-memory map으로 검증 + 차감을 시뮬레이션한다.
 */
public class MockSkypassInterface implements SkypassInterface {

    private final Map<String, Integer> balances = new HashMap<>();
    private final Map<String, String> passwords = new HashMap<>();

    public void seed(String skypassNumber, String password, int mileage) {
        balances.put(skypassNumber, mileage);
        passwords.put(skypassNumber, password);
    }

    @Override
    public Object verifyMembership(String skypassNumber, String password) {
        String expected = passwords.get(skypassNumber);
        if (expected == null || !expected.equals(password)) {
            return null;
        }
        return skypassNumber;
    }

    @Override
    public int getMileageBalance(String skypassNumber) {
        return balances.getOrDefault(skypassNumber, 0);
    }

    @Override
    public boolean deductMileage(String skypassNumber, int amount) {
        if (amount <= 0) {
            return false;
        }
        int current = balances.getOrDefault(skypassNumber, 0);
        if (current < amount) {
            return false;
        }
        balances.put(skypassNumber, current - amount);
        return true;
    }

    @Override
    public Object verifyAndDeduct(String skypassNumber, int amount) {
        if (!balances.containsKey(skypassNumber)) {
            return null;
        }
        return deductMileage(skypassNumber, amount) ? skypassNumber : null;
    }
}
