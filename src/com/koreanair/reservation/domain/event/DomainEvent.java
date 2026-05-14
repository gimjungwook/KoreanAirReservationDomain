package com.koreanair.reservation.domain.event;

import java.time.LocalDateTime;

/**
 * Observer 패턴의 Event 베이스. Iteration 3에서 도입.
 *
 * <p>모든 도메인 이벤트는 발생 시각과 sourceId(이벤트를 발행한 객체의 식별자)를 갖는다.
 * 구체 이벤트 타입은 {@link EventListener#onEvent(DomainEvent)}에서 instanceof로 분기한다.
 */
public abstract class DomainEvent {

    private final LocalDateTime occurredAt;
    private final String sourceId;

    protected DomainEvent(String sourceId) {
        this.sourceId = sourceId;
        this.occurredAt = LocalDateTime.now();
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getEventType() {
        return getClass().getSimpleName();
    }
}
