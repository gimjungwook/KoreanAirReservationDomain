package com.koreanair.reservation.domain.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer 패턴의 Subject 베이스. 구독자 관리 + 이벤트 브로드캐스트.
 *
 * <p>도메인/Control 클래스는 이 타입을 상속하거나 인스턴스로 보유하여
 * 자신의 상태 변화를 {@link DomainEvent}로 발행한다.
 *
 * <p>Iteration 3에서 도입. 구독 순서는 등록 순서를 따른다.
 */
public class EventPublisher {

    private final List<EventListener> observers = new ArrayList<>();

    public void attach(EventListener listener) {
        if (listener == null || observers.contains(listener)) {
            return;
        }
        observers.add(listener);
    }

    public void detach(EventListener listener) {
        observers.remove(listener);
    }

    public int subscriberCount() {
        return observers.size();
    }

    public void notifyObservers(DomainEvent event) {
        if (event == null) {
            return;
        }
        for (EventListener listener : new ArrayList<>(observers)) {
            try {
                listener.update(event);
            } catch (RuntimeException ex) {
                System.out.println("[EVENT] listener " + listener.getClass().getSimpleName()
                        + " failed on " + event.getEventType() + ": " + ex.getMessage());
            }
        }
    }
}
