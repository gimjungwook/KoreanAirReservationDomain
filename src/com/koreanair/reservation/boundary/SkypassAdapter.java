package com.koreanair.reservation.boundary;

import java.util.Map;
import java.util.Objects;

/**
 * DP#8 Adapter — Adapter.
 *
 * <p>외부 {@link RemoteSkypassApi}(Adaptee) 의 raw 응답 Map 을 내부 {@link SkypassInterface}(Target)
 * 시그니처로 변환한다. 도메인 코드는 SkypassInterface 만 알면 되고, 외부 API 변경 시 본 어댑터만 갱신.
 */
public class SkypassAdapter implements SkypassInterface {

    private final RemoteSkypassApi remoteApi;

    public SkypassAdapter(RemoteSkypassApi remoteApi) {
        this.remoteApi = Objects.requireNonNull(remoteApi);
    }

    @Override
    public Object verifyMembership(String skypassNumber, String password) {
        Map<String, Object> res = remoteApi.postAuth(skypassNumber, password);
        return Boolean.TRUE.equals(res.get("ok")) ? skypassNumber : null;
    }

    @Override
    public int getMileageBalance(String skypassNumber) {
        Map<String, Object> res = remoteApi.getMileage(skypassNumber);
        Object bal = res.get("balance");
        return bal instanceof Integer ? (Integer) bal : 0;
    }

    @Override
    public boolean deductMileage(String skypassNumber, int amount) {
        Map<String, Object> res = remoteApi.postDeduct(skypassNumber, amount);
        return Boolean.TRUE.equals(res.get("success"));
    }

    @Override
    public Object verifyAndDeduct(String skypassNumber, int amount) {
        // 외부 API 는 단일 엔드포인트가 없어 verify(getMileage)+deduct 2단 호출로 어댑팅.
        Map<String, Object> check = remoteApi.getMileage(skypassNumber);
        Object status = check.get("status");
        if (!(status instanceof Integer) || (Integer) status != 200) {
            return null;
        }
        return deductMileage(skypassNumber, amount) ? skypassNumber : null;
    }
}
