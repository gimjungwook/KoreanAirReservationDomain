package com.koreanair.reservation.control;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.user.Member;

/**
 * 인증 서비스 — Control 계층.
 *
 * <p>Iteration 1: hard-coded 샘플 회원 1명만 로그인 가능. 실제 DB / 해시 검증은 없음.
 * <p>Iteration 2: SHA-256 + salt 기반 비밀번호 검증 추가 ({@link #loginWithHash(String, String)}),
 *              비회원 PNR 검증 ({@link #verifyGuest(String, String, String)}) 추가.
 *              기존 평문 검증 경로는 backward-compat 용으로 유지.
 * <p>TODO(iter3): 세션 timeout, 외부 IdP 연동.
 * <p>TODO(iter4): SkypassInterface 통합 — verifyGuest 의 실제 회원/예약 교차 검증.
 */
public class AuthService {

    private final Map<String, Member> memberBySkypass = new HashMap<>();
    private final Map<String, String> passwordBySkypass = new HashMap<>();
    private final Map<String, Member> memberByName = new HashMap<>();

    // Iteration 2: salted-hash 기반 검증용 병행 맵.
    private final Map<String, String> saltBySkypass = new HashMap<>();
    private final Map<String, String> hashedPasswordBySkypass = new HashMap<>();

    private int memberSequence = 4;

    private Member current;

    public AuthService() {
    }

    public void registerSample(Member member, String skypassNumber) {
        registerMember(member, skypassNumber, "pw-stub");
    }

    public Member registerMember(Member member, String skypassNumber, String password) {
        if (member == null || skypassNumber == null || skypassNumber.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("회원 등록 정보가 올바르지 않습니다.");
        }
        memberBySkypass.put(skypassNumber, member);
        // Iteration 1 backward-compat: 평문 비밀번호 보관.
        passwordBySkypass.put(skypassNumber, password);
        // Iteration 2: salt + SHA-256 해시 병행 보관.
        String salt = generateSalt();
        saltBySkypass.put(skypassNumber, salt);
        hashedPasswordBySkypass.put(skypassNumber, hashPassword(salt, password));
        memberByName.put(member.getName(), member);
        return member;
    }

    public String generateSkypassNumber() {
        int seq = memberSequence++;   // 발급마다 증가 — 신규 가입자가 같은 번호를 받지 않도록
        return String.format("SKY-%03d-%03d", seq / 1000, seq % 1000);
    }

    public Member loginByName(String name, String password) {
        Member m = memberByName.get(name);
        if (m == null) {
            return null;
        }
        String skypass = m.getMemberNumber();
        return loginWithHash(skypass, password);
    }

    /**
     * Iteration 1 호환 진입점 — 내부적으로 {@link #loginWithHash(String, String)} 로 위임.
     */
    public Member login(String skypassNumber, String passwordStub) {
        return loginWithHash(skypassNumber, passwordStub);
    }

    /**
     * Iteration 2: salt + SHA-256 해시 기반 비밀번호 검증.
     *
     * <ul>
     *   <li>salt 가 없으면 (iter 1 회원) {@link #passwordBySkypass} 평문 비교로 fallback.</li>
     *   <li>salt 가 있으면 hashPassword(salt, password) 와 저장된 해시를 비교.</li>
     * </ul>
     *
     * @return 일치하면 Member, 아니면 null. 성공 시 {@link #current} 갱신.
     */
    public Member loginWithHash(String skypassNumber, String password) {
        if (skypassNumber == null || password == null) {
            return null;
        }
        Member m = memberBySkypass.get(skypassNumber);
        if (m == null) {
            return null;
        }
        String salt = saltBySkypass.get(skypassNumber);
        String expectedHash = hashedPasswordBySkypass.get(skypassNumber);
        if (salt == null || expectedHash == null) {
            // iter 1 fallback: 평문 비교.
            String expectedPassword = passwordBySkypass.get(skypassNumber);
            if (!password.equals(expectedPassword)) {
                return null;
            }
        } else {
            String actualHash = hashPassword(salt, password);
            if (!expectedHash.equals(actualHash)) {
                return null;
            }
        }
        this.current = m;
        return m;
    }

    /**
     * Iteration 2 비회원 검증 (deliberately permissive).
     *
     * <p>로직:
     * <ol>
     *   <li>{@code Reservation.findByPnr(pnr)} 로 예약 존재 여부 확인.</li>
     *   <li>name / email / pnr 모두 비공백이며 email 에 "@" 가 포함되어 있어야 한다.</li>
     *   <li>Iter 4 에서 SkypassInterface 와 contactEmail 교차 검증 추가 예정.</li>
     * </ol>
     *
     * @return 위 조건이 모두 충족되면 true.
     */
    public boolean verifyGuest(String pnr, String name, String email) {
        if (pnr == null || pnr.isBlank()) {
            return false;
        }
        if (name == null || name.isBlank()) {
            return false;
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            return false;
        }
        Reservation reservation = Reservation.findByPnr(pnr);
        if (reservation == null) {
            return false;
        }
        System.out.println("[GUEST] verified: " + pnr + " " + name + " " + email);
        return true;
    }

    public void logout() {
        this.current = null;
    }

    public Member currentMember() {
        return current;
    }

    // --- Iteration 2 비밀번호 해시 헬퍼 ---

    private static String hashPassword(String salt, String password) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String generateSalt() {
        var bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        var sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
