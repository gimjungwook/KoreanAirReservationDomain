package com.koreanair.reservation.control;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.flight.Airport;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.FlightStatus;
import com.koreanair.reservation.domain.passenger.MileageAccount;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentStatus;
import com.koreanair.reservation.domain.payment.RefundRequest;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
import com.koreanair.reservation.domain.reservation.state.InvalidStateTransitionException;
import com.koreanair.reservation.domain.user.Member;

/**
 * BookingController — Control 계층.
 *
 * <p>Iteration 1 Walking Skeleton: "Book Flight" Happy Path 를 오케스트레이션.
 * Search → Select → Enter Passenger Info → Validate Fare → Pay → Confirm.
 *
 * <p>기존 메서드 시그니처는 보존. 신규 Iteration 1 메서드는 별도 오버로드로 추가.
 *
 * <p>Iteration 2: 취소/환불 흐름 ({@link #processCancellation(String)}) 및
 * RefundHandler / ReservationLookupService 주입 생성자 추가.
 */
public class BookingController {

    // --- Iteration 1 주입 의존성 (default = null, walking skeleton 에서만 사용) ---
    private AuthService authService;
    private FlightSearchService flightSearch;
    private PaymentProcessor paymentProcessor;

    // --- Iteration 2 주입 의존성 (default = null) ---
    private RefundHandler refundHandler;
    private ReservationLookupService lookupService;

    public BookingController() {
    }

    /** Walking skeleton 전용 생성자 (iter 1). */
    public BookingController(AuthService authService,
                             FlightSearchService flightSearch,
                             PaymentProcessor paymentProcessor) {
        this.authService = authService;
        this.flightSearch = flightSearch;
        this.paymentProcessor = paymentProcessor;
    }

    /** Iteration 2 생성자 — RefundHandler / ReservationLookupService 주입. */
    public BookingController(AuthService authService,
                             FlightSearchService flightSearch,
                             PaymentProcessor paymentProcessor,
                             RefundHandler refundHandler,
                             ReservationLookupService lookupService) {
        this.authService = authService;
        this.flightSearch = flightSearch;
        this.paymentProcessor = paymentProcessor;
        this.refundHandler = refundHandler;
        this.lookupService = lookupService;
    }

    // --- 기존 메서드 (시그니처 보존, 구현 미완 유지) ---

    public Object processSearch(Object searchCriteria) {
        return null;
    }

    public Reservation initiateBooking(Long flightId, String fareClass) {
        return null;
    }

    public Reservation initiateBooking(Long flightId, String fareClass, Long memberId) {
        return null;
    }

    public void setPassengerInfo(Long reservationId, Object passengerData) {
    }

    public void confirmInfo(Long reservationId) {
    }

    public void assignSeat(Long reservationId, String seatNumber) {
        // 레거시 시그니처. PNR-기반 오버로드로 라우팅하지 못하면 로그만 출력한다.
        System.out.println("[SEAT] " + seatNumber + " assigned to reservation " + reservationId);
    }

    /**
     * Iteration 2: 좌석 배정 — SeatInventory.reserve + SeatAssignment 생성.
     *
     * @param reservation 좌석을 배정할 예약. 첫 segment 의 FlightSchedule 에서
     *                    SeatInventory 를 찾는다.
     * @param seatNumber  좌석 번호 (예: "12A"). cabinClass / bookingClass 는
     *                    FlightSchedule.getFareRule().getFareClass() 에서 추정한다.
     * @return 생성된 SeatAssignment. 좌석 부족 / 정보 누락 시 null.
     */
    public com.koreanair.reservation.domain.reservation.SeatAssignment assignSeat(
            Reservation reservation, String seatNumber) {
        if (reservation == null || seatNumber == null || seatNumber.isBlank()) {
            return null;
        }
        com.koreanair.reservation.domain.reservation.Itinerary itinerary = reservation.getItinerary();
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            System.out.println("[SEAT] segment 정보 없음 — 좌석 배정 생략");
            return null;
        }
        com.koreanair.reservation.domain.reservation.Segment first = itinerary.getSegments().get(0);
        com.koreanair.reservation.domain.flight.FlightSchedule schedule = first.getFlightSchedule();
        if (schedule == null) {
            return null;
        }
        com.koreanair.reservation.domain.flight.FareRule fareRule = schedule.getFareRule();
        com.koreanair.reservation.domain.flight.BookingClass bookingClass =
                resolveBookingClass(fareRule != null ? fareRule.getFareClass() : null);
        com.koreanair.reservation.domain.flight.SeatInventory inventory =
                schedule.findSeatInventory(bookingClass);
        if (inventory == null && !schedule.getSeatInventories().isEmpty()) {
            inventory = schedule.getSeatInventories().get(0);
        }
        if (inventory != null) {
            inventory.reserve(bookingClass);
        }
        com.koreanair.reservation.domain.flight.Seat seat =
                new com.koreanair.reservation.domain.flight.Seat(seatNumber,
                        com.koreanair.reservation.domain.flight.CabinClass.ECONOMY);
        seat.hold();
        com.koreanair.reservation.domain.reservation.SeatAssignment assignment =
                new com.koreanair.reservation.domain.reservation.SeatAssignment(schedule, seat);
        System.out.println("[SEAT] " + seatNumber + " (" + bookingClass + ") assigned to PNR="
                + reservation.getPnrNumber());
        return assignment;
    }

    private com.koreanair.reservation.domain.flight.BookingClass resolveBookingClass(String fareClass) {
        if (fareClass == null) {
            return com.koreanair.reservation.domain.flight.BookingClass.Y;
        }
        try {
            return com.koreanair.reservation.domain.flight.BookingClass.valueOf(fareClass);
        } catch (IllegalArgumentException ex) {
            return com.koreanair.reservation.domain.flight.BookingClass.Y;
        }
    }

    /**
     * Iteration 2 메인 흐름 — PNR 기반 취소 + 자동 환불.
     *
     * <ol>
     *   <li>PNR 로 Reservation 조회. 없으면 IllegalArgumentException.</li>
     *   <li>requestCancellation() → confirmCancellation() (Confirmed/Ticketed → Cancelled).</li>
     *   <li>requestRefund() 시도. FareRule 이 환불 불가면
     *       {@link InvalidStateTransitionException} 으로 흐름 종료.</li>
     *   <li>RefundHandler 로 evaluate + process 후 processRefundDecision(true) 로 RefundedState 전이.</li>
     * </ol>
     */
    public void processCancellation(String pnr) {
        Reservation reservation = Reservation.findByPnr(pnr);
        if (reservation == null) {
            throw new IllegalArgumentException("PNR 을 찾을 수 없습니다: " + pnr);
        }

        // 1) Confirmed/Ticketed → CancellationRequested → Cancelled
        reservation.requestCancellation();
        reservation.confirmCancellation();

        // 2) Cancelled → RefundRequested. 환불 불가 운임이면 여기서 종료.
        try {
            reservation.requestRefund();
        } catch (InvalidStateTransitionException ex) {
            System.out.println("[CANCEL] 환불 불가 운임 — PNR=" + pnr);
            return;
        }

        // 3) RefundHandler 로 evaluate + process.
        if (refundHandler != null) {
            String fareClass = resolveFareClass(reservation);
            RefundRequest request = refundHandler.evaluateRefund(pnr, fareClass);
            if (request != null) {
                refundHandler.processRefund(request.getRequestId(), request.getRefundAmount());
            }
        }

        // 4) RefundRequested → Refunded.
        reservation.processRefundDecision(true);
        System.out.println("[CANCEL] PNR=" + pnr + " 처리 완료 — 최종 상태=" + reservation.getStateName());
    }

    /**
     * Reservation 의 itinerary 첫 segment FlightSchedule.FareRule.fareClass 추출.
     * 도달 불가 시 "Y" 디폴트 (resolvePolicy 에서 FullRefundPolicy 로 귀결).
     */
    private String resolveFareClass(Reservation reservation) {
        Itinerary itinerary = reservation.getItinerary();
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return "Y";
        }
        Segment first = itinerary.getSegments().get(0);
        if (first == null || first.getFlightSchedule() == null) {
            return "Y";
        }
        FareRule rule = first.getFlightSchedule().getFareRule();
        if (rule == null || rule.getFareClass() == null) {
            return "Y";
        }
        return rule.getFareClass();
    }

    public void changeFlightStatus(String flightNumber, FlightStatus newStatus) {
    }

    public FlightSchedule createSchedule(Object scheduleData) {
        return null;
    }

    public boolean authenticateAdmin(String adminId, String password) {
        return false;
    }

    public Object authenticateMember(String skypassNumber, String password) {
        return null;
    }

    public Object getBookingHistory(Long memberId) {
        return null;
    }

    public Object getTicketDetail(String pnr) {
        return null;
    }

    public boolean verifyGuestIdentity(String pnr, String name, String email) {
        return false;
    }

    public boolean reconfirmGuestIdentity(String pnr, String email) {
        return false;
    }

    // --- Iteration 1 Walking Skeleton 전용 메서드 (신규 시그니처) ---

    /** 1) 검색 — 공항 코드 + 일자로 직항편 조회. */
    public List<FlightSchedule> processSearch(String fromAirportCode,
                                              String toAirportCode,
                                              LocalDate date) {
        if (flightSearch == null) {
            return new ArrayList<>();
        }
        return flightSearch.search(fromAirportCode, toAirportCode, date);
    }

    /** 직항 검색 결과를 itinerary 형태로 조회. */
    public List<Itinerary> searchDirectItineraries(String fromAirportCode,
                                                   String toAirportCode,
                                                   LocalDate date) {
        return itinerarySearch().searchDirect(fromAirportCode, toAirportCode, date);
    }

    /** 1-stop 환승 검색. MCT는 국제선 기본 90분을 적용한다. */
    public List<Itinerary> searchConnectingItineraries(String fromAirportCode,
                                                       String toAirportCode,
                                                       LocalDate date) {
        return itinerarySearch().searchConnecting(fromAirportCode, toAirportCode, date,
                Itinerary.INTERNATIONAL_MCT);
    }

    /** 다도시 검색. 각 leg는 하루 간격의 데모 일정으로 연결된다. */
    public List<Itinerary> searchMultiCityItineraries(List<String> airportCodes,
                                                      LocalDate startDate) {
        return itinerarySearch().searchMultiCity(airportCodes, startDate, Duration.ofMinutes(90));
    }

    /** 발표 데모 기본 다도시 코스: ICN → NRT → JFK → LAX. */
    public List<Itinerary> searchDemoMultiCityItineraries(LocalDate startDate) {
        return itinerarySearch().searchDemoMultiCity(startDate);
    }

    private ItinerarySearchService itinerarySearch() {
        return new ItinerarySearchService(flightSearch);
    }

    /** 전체 항공편 목록 조회 (초기 표시용). */
    public List<FlightSchedule> getAllSchedules() {
        if (flightSearch == null) {
            return new ArrayList<>();
        }
        return flightSearch.getCatalog();
    }

    /** 2) 선택된 flight 로 Reservation 생성 (Initiated 상태). */
    public Reservation initiateBooking(FlightSchedule selected) {
        if (selected == null || !selected.isAvailableForBooking()) {
            throw new IllegalArgumentException("예약 가능한 직항편을 선택해야 합니다.");
        }
        if (selected.getFlight() == null || selected.getFlight().getRoute() == null) {
            throw new IllegalArgumentException("항공편 경로 정보가 없습니다.");
        }
        Reservation r = new Reservation();
        r.setReservationNumber("PNR-" + System.currentTimeMillis());
        r.getItinerary().addSegment(new com.koreanair.reservation.domain.reservation.Segment(selected));
        return r;
    }

    /** 선택된 itinerary로 Reservation 생성. 직항/환승/다도시를 같은 예약 흐름으로 처리한다. */
    public Reservation initiateBooking(Itinerary itinerary) {
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            throw new IllegalArgumentException("예약 가능한 여정을 선택해야 합니다.");
        }
        Reservation r = new Reservation();
        r.setReservationNumber("PNR-" + System.currentTimeMillis());
        r.getItinerary().setTripType(itinerary.getTripType());
        for (Segment segment : itinerary.getSegments()) {
            if (segment == null || segment.getFlightSchedule() == null
                    || !segment.getFlightSchedule().isAvailableForBooking()) {
                throw new IllegalArgumentException("예약 불가 항공편이 포함되어 있습니다.");
            }
            r.getItinerary().addSegment(new Segment(segment.getFlightSchedule()));
        }
        return r;
    }

    /** 3) 승객 정보 입력 — State: Initiated → PendingPayment. */
    public void setPassengerInfo(Reservation reservation, Passenger passenger) {
        reservation.enterPassengerInfo(passenger);
    }

    /**
     * 4~5) 운임 검증 + 결제.
     *   - 운임 규칙 검증 실패 시 IllegalArgumentException.
     *   - 결제 실패 시 Reservation 을 handlePaymentFailure 로 전이.
     *   - 결제 성공 시 processPayment 호출로 Reservation: PendingPayment → Confirmed.
     *
     * @return 결제 완료된 Payment 객체 (성공 / 실패 여부는 payment.getStatus() 로 확인).
     */
    public Payment confirmPayment(Reservation reservation,
                                  FareRule fareRule,
                                  long baseFare,
                                  long tax) {
        if (!paymentProcessor.validateFareRule(fareRule)) {
            throw new IllegalArgumentException("운임 규칙 검증 실패: " + fareRule);
        }
        long total = paymentProcessor.calculateTotalAmount(baseFare, tax);
        Payment payment = paymentProcessor.processPaymentCharge(total, reservation.getPnrNumber());

        if (payment.getStatus() == PaymentStatus.PAID) {
            reservation.addPayment(payment);
            reservation.processPayment();      // State 전이
        }
        // 실패 시 handlePaymentFailure 전이는 ReservationAutoCancelListener가 자동 호출 (iter3).
        return payment;
    }

    /**
     * Iteration 3 — 마일리지 결제. MileageAccount에서 차감 후 결제 전이를 트리거한다.
     * 잔액 부족 시 PaymentFailedEvent가 자동 발행되어 listener가 Reservation을 취소한다.
     */
    public Payment confirmMileagePayment(Reservation reservation,
                                         MileageAccount account,
                                         long mileageCost) {
        if (reservation == null || account == null) {
            throw new IllegalArgumentException("Reservation/MileageAccount가 필요합니다.");
        }
        Payment payment = paymentProcessor.processMileagePayment(
                account, mileageCost, reservation.getPnrNumber());
        if (payment.getStatus() == PaymentStatus.PAID) {
            reservation.addPayment(payment);
            reservation.processPayment();
        }
        return payment;
    }

    /** 현재 로그인된 회원. */
    public Member currentMember() {
        return authService != null ? authService.currentMember() : null;
    }

    // --- airport helper (walking skeleton 편의) ---

    public static Airport airport(String code, String name, String city) {
        // TODO(iter1): Airport 생성자가 비어 있어 factory 로 최소 속성만 세팅.
        //              정식 생성자/세터는 도메인 측에서 보강 필요.
        return new Airport();
    }
}
