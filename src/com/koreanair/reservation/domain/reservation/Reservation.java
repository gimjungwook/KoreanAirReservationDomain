package com.koreanair.reservation.domain.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.Refund;
import com.koreanair.reservation.domain.reservation.state.InitiatedState;
import com.koreanair.reservation.domain.reservation.state.ReservationState;
import com.koreanair.reservation.domain.user.User;

/**
 * Reservation — State 패턴의 Context 클래스이자 기존 도메인 Entity.
 *
 * <p>State 패턴 도입 방침(Iteration 1):
 * <ul>
 *   <li>{@link #currentState} 필드가 핵심. 생애주기 메서드는 모두 현재 상태 객체로 위임.</li>
 *   <li>기존 {@link ReservationStatus} enum 은 컨텍스트 정보용으로 보존. State 전이 시 동기화.</li>
 *   <li>기존 메서드 시그니처는 그대로 유지 (다른 클래스에서 호출 중일 수 있음).</li>
 * </ul>
 *
 * <p>전이 결정권은 State 구현체({@code com.koreanair.reservation.domain.reservation.state.*})에만 있다.
 * 외부에서 {@link #setState(ReservationState)} 를 호출하면 패턴 불변식이 깨지므로
 * "State 객체만 호출한다"는 규약을 문서로 고정한다.
 */
public class Reservation {

    private Long reservationId;
    private String reservationNumber;
    private User requester;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reservationDate;
    private Itinerary itinerary;
    private List<Passenger> passengers = new ArrayList<>();
    private List<ReservationItem> reservationItems = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Iteration 2: PNR -> Reservation 인메모리 레지스트리.
     * setReservationNumber() 시점에 등록되어 findByPnr 조회를 지원한다.
     */
    private static final Map<String, Reservation> REGISTRY = new HashMap<>();

    // --- State 패턴 ---
    private ReservationState currentState;

    public Reservation() {
        this.currentState = new InitiatedState();   // 초기 상태
        this.status = ReservationStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.reservationDate = this.createdAt;
        this.itinerary = new Itinerary();
    }

    // --- 기존 getter / setter (시그니처 보존) ---

    public Long getReservationId() {
        return reservationId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public User getRequester() {
        return requester;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public List<ReservationItem> getReservationItems() {
        return reservationItems;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    public void addReservationItem(ReservationItem reservationItem) {
        reservationItems.add(reservationItem);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    /** Iteration 2: 발권 시 Reservation 의 tickets 컬렉션에 e-Ticket 을 추가. */
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    /**
     * 기존 빈 메서드 → State 위임으로 채움. processPayment 경로의 최종 단계.
     * 외부 호출자는 결제가 실제 성공한 뒤 이 메서드를 통해 상태 전이를 트리거한다.
     */
    public void confirmReservation() {
        currentState.processPayment(this);
    }

    /**
     * 기존 빈 메서드 → State 위임으로 채움.
     * TODO(iter2): Ticket 도메인 객체 실제 발급 로직은 ConfirmedState.issueTicket 에서 수행.
     */
    public void issueTickets() {
        currentState.issueTicket(this);
    }

    /**
     * 기존 빈 메서드 → State 위임으로 채움.
     * TODO(iter2): Confirmed → CancellationRequested → Cancelled 2-step 확정 흐름으로 분리.
     */
    public void cancelReservation() {
        currentState.requestCancellation(this);
    }

    public boolean canBeCancelled() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.TICKETED;
    }

    public Refund requestRefund(Payment payment, String reason) {
        // TODO(iter2): CancelledState 에서 requestRefund 전이 후 RefundHandler 호출.
        currentState.requestRefund(this);
        return null;
    }

    public void evaluateImpactOfFlightStatusChange() {
    }

    public static Reservation create(ReservationStatus initialStatus) {
        Reservation r = new Reservation();
        r.status = initialStatus;
        return r;
    }

    public static Reservation create(String initialStatus) {
        return create(ReservationStatus.valueOf(initialStatus));
    }

    public void updatePassengerInfo(Object passengerData) {
    }

    /**
     * 기존 시그니처 보존. ReservationStatus enum 컨텍스트만 갱신.
     * State 객체와의 동기화는 State 전이 메서드 내부에서 자동으로 수행된다.
     */
    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Iteration 2: PNR 로 Reservation 을 조회한다.
     * 등록은 {@link #setReservationNumber(String)} 호출 시점에 자동 수행.
     */
    public static Reservation findByPnr(String pnr) {
        if (pnr == null) {
            return null;
        }
        return REGISTRY.get(pnr);
    }

    public String getContactEmail() {
        return null;
    }

    public Reservation getReservationDetail(String pnr) {
        return findByPnr(pnr);
    }

    // --- State 패턴 전용 메서드 (신규) ---

    /**
     * 상태 전이. 설계상 ReservationState 구현체에서만 호출해야 한다.
     * Java 의 접근제어로는 교차 패키지에서 호출 가능을 완전히 막을 수 없으므로,
     * "State 객체만 호출한다"는 규약을 문서로 고정한다.
     *
     * <p>TODO(iter3): Observer 패턴 도입 시 이 지점에서 상태 변경 이벤트를 발행한다.
     */
    public void setState(ReservationState next) {
        System.out.printf("[STATE] %s -> %s%n", this.currentState.name(), next.name());
        this.currentState = next;
    }

    public ReservationState getCurrentState() {
        return currentState;
    }

    public String getStateName() {
        return currentState.name();
    }

    // --- State 위임 생애주기 메서드 (신규, Iteration 1 전용) ---

    public void enterPassengerInfo(Passenger p) {
        if (p != null) {
            addPassenger(p);
        }
        currentState.enterPassengerInfo(this);
    }

    public void enterPassengerInfo() {
        enterPassengerInfo(null);
    }

    public void processPayment() {
        currentState.processPayment(this);
    }

    public void handlePaymentFailure() {
        currentState.handlePaymentFailure(this);
    }

    public void issueTicket() {
        currentState.issueTicket(this);
    }

    public void requestCancellation() {
        currentState.requestCancellation(this);
    }

    public void confirmCancellation() {
        currentState.confirmCancellation(this);
    }

    public void requestRefund() {
        currentState.requestRefund(this);
    }

    public void processRefundDecision(boolean approved) {
        currentState.processRefundDecision(this, approved);
    }

    // --- Iteration 1 Walking Skeleton 보조 setter ---

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
        if (reservationNumber != null) {
            REGISTRY.put(reservationNumber, this);
            com.koreanair.reservation.control.ReservationRegistry.DEFAULT.register(this);
        }
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    /** Walking skeleton 에서 PNR 개념을 reservationNumber 로 재사용. */
    public String getPnrNumber() {
        return reservationNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
}
