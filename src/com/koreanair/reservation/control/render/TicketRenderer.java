package com.koreanair.reservation.control.render;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * DP#7 Template Method — AbstractClass.
 *
 * <p>티켓 출력은 header → body → footer 의 동일 흐름을 따른다. 출력 매체(텍스트/HTML/탑승권)는
 * 서브클래스가 각 hook 메서드를 오버라이드해 결정한다.
 */
public abstract class TicketRenderer {

    /** Template Method (final). */
    public final String render(Reservation reservation, Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append(header(reservation, ticket));
        sb.append(separator());
        sb.append(body(reservation, ticket));
        sb.append(separator());
        sb.append(footer(reservation, ticket));
        return sb.toString();
    }

    protected abstract String header(Reservation reservation, Ticket ticket);

    protected abstract String body(Reservation reservation, Ticket ticket);

    protected abstract String footer(Reservation reservation, Ticket ticket);

    /** Default hook — 서브클래스가 구분선 형태 변경 가능. */
    protected String separator() {
        return "\n";
    }
}
