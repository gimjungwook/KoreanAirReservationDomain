package com.koreanair.reservation.domain.event;

/**
 * Observer 패턴(교과서 그림 9-4)의 Observer 계약.
 *
 * <p>교과서와 동일한 <b>PULL 모델</b>: {@code update()} 는 무인자다. 통지를 받은 옵서버는
 * 자신이 보유한 ConcreteSubject 역참조(-concreteSubject)의 {@code getState()} 로
 * 현재 상태(가장 최근 {@link DomainEvent})를 직접 당겨와 처리한다.
 * Iteration 3에서 도입, Iteration 4에서 push→pull 로 교과서 정합.
 */
public interface EventListener {

    /** 교과서 Observer.update() — 무인자. 옵서버가 subject.getState() 로 상태를 pull 한다. */
    void update();
}
