package com.koreanair.reservation.domain.event;

/**
 * Observer 패턴의 Observer/Listener 계약.
 *
 * <p>구현체는 관심 있는 {@link DomainEvent} 하위 타입을 instanceof로 가려서 처리한다.
 * Iteration 3에서 도입.
 */
public interface EventListener {

    void onEvent(DomainEvent event);
}
