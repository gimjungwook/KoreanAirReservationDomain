package com.koreanair.reservation.domain.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer 패턴(교과서 그림 9-4)의 <b>Subject</b> 베이스.
 *
 * <p>그림의 Subject 와 동일하게 구독자 관리 연산만 갖는다:
 * {@link #attach(EventListener)}, {@link #detach(EventListener)}, {@link #notifyObservers()}.
 * 관찰 상태(-subjectState)와 {@code getState()/setState()} 는 그림처럼 각
 * <b>ConcreteSubject</b>(TicketPurchasePublisher, FlightSchedule, SeatHoldMonitor, PaymentProcessor)
 * 가 보유한다.
 *
 * <p>PULL 모델: {@code notifyObservers()} 는 무인자로 각 옵서버의 {@code update()} 만 호출하고,
 * 옵서버는 자신의 ConcreteSubject {@code getState()} 로 상태를 당겨간다.
 * Iteration 3 도입, Iteration 4 에서 push→pull 정합.
 */
public class EventPublisher {

    private final List<EventListener> observers = new ArrayList<>();

    /** 교과서 Subject.attach(observer: Observer). */
    public void attach(EventListener observer) {
        if (observer == null || observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }

    /** 교과서 Subject.detach(observer: Observer). */
    public void detach(EventListener observer) {
        observers.remove(observer);
    }

    public int subscriberCount() {
        return observers.size();
    }

    /** 교과서 Subject.notifyObservers() — 무인자. 각 옵서버의 update() 호출(옵서버가 pull). */
    public void notifyObservers() {
        for (EventListener observer : new ArrayList<>(observers)) {
            try {
                observer.update();
            } catch (RuntimeException ex) {
                System.out.println("[EVENT] observer " + observer.getClass().getSimpleName()
                        + " update() failed: " + ex.getMessage());
            }
        }
    }
}
