package com.koreanair.reservation.tools;

import net.java.amateras.uml.sequencediagram.model.*;
import java.io.FileWriter;

/**
 * Generates 5 Sequence Diagrams (.sqd) using concrete ECB classes from the Class Diagram.
 * Boundary → Control → Entity flow is explicit in every diagram.
 *
 * <p>Iteration 1 (3): bookFlight, adminOperations, memberBookingTicket.
 * <p>Iteration 2 (2): cancelRefund (Strategy 패턴 시연), lookupReservation (Guest 3중 검증).
 */
public class GenerateSequenceDiagramsIter2 {

    // ──────────────────────────────────────────────
    // 1. Book Flight (Passenger → ECB classes)
    // ──────────────────────────────────────────────
    static String buildBookFlight() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel passenger    = b.createActor("Passenger");
        InstanceModel resUI     = b.createInstance("ReservationUI");
        InstanceModel bookCtrl  = b.createInstance("BookingController");
        InstanceModel reservation = b.createInstance("Reservation");
        InstanceModel seat      = b.createInstance("Seat");
        InstanceModel payProc   = b.createInstance("PaymentProcessor");
        InstanceModel payGwI    = b.createInstance("PaymentGatewayInterface");

        b.init(passenger);

        // Search flights: Passenger → UI → BC
        b.createMessage("searchFlights(origin, dest, date, tripType)", resUI);
          b.createMessage("processSearch(searchCriteria)", bookCtrl);
          b.endMessage();
        b.endMessage();

        // Select itinerary: UI → BC → Reservation.create()
        b.createMessage("selectItinerary(flightId, fareClass)", resUI);
          b.createMessage("initiateBooking(flightId, fareClass)", bookCtrl);
            b.createMessage("create(status=Initiated)", reservation);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Enter passenger info: UI → BC → Reservation.update()
        b.createMessage("enterPassengerInfo(name, contact, passport)", resUI);
          b.createMessage("setPassengerInfo(reservationId, data)", bookCtrl);
            b.createMessage("updatePassengerInfo(data)", reservation);
            b.endMessage();
            b.createMessage("updateStatus(PendingPayment)", reservation);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Seat selection: UI → BC → Seat.hold()
        b.createMessage("selectSeat(seatNumber)", resUI);
          b.createMessage("assignSeat(reservationId, seatNumber)", bookCtrl);
            b.createMessage("hold(seatNumber, timeout=15min)", seat);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Payment: UI → PaymentProcessor → PaymentGatewayInterface
        b.createMessage("submitPayment(paymentInfo)", resUI);
          b.createMessage("processPayment(reservationId, paymentInfo)", payProc);
            b.createMessage("sendAuthorizationRequest(amount, paymentInfo)", payGwI);
            b.endMessage();
            // On approval: update Reservation and Seat status
            b.createMessage("updateStatus(Confirmed)", reservation);
            b.endMessage();
            b.createMessage("updateStatus(Booked)", seat);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 2. Admin Operations (Admin → ECB classes)
    // ──────────────────────────────────────────────
    static String buildAdminOperations() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel admin        = b.createActor("Admin");
        InstanceModel resUI     = b.createInstance("ReservationUI");
        InstanceModel bookCtrl  = b.createInstance("BookingController");
        InstanceModel flightSch = b.createInstance("FlightSchedule");
        InstanceModel refundH   = b.createInstance("RefundHandler");
        InstanceModel refundReq = b.createInstance("RefundRequest");
        InstanceModel fareRule  = b.createInstance("FareRule");
        InstanceModel payGwI    = b.createInstance("PaymentGatewayInterface");

        b.init(admin);

        // Login: Admin → UI → BC
        b.createMessage("login(adminId, password)", resUI);
          b.createMessage("authenticateAdmin(adminId, password)", bookCtrl);
          b.endMessage();
        b.endMessage();

        // Create flight schedule: UI → BC → FlightSchedule.create()
        b.createMessage("createFlightSchedule(flightNo, route, time, aircraft)", resUI);
          b.createMessage("createSchedule(scheduleData)", bookCtrl);
            b.createMessage("create(flightNo, departure, arrival, aircraftType)", flightSch);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Update flight status: UI → BC → FlightSchedule.updateStatus()
        b.createMessage("updateFlightStatus(flightNo, Delayed)", resUI);
          b.createMessage("changeFlightStatus(flightNo, newStatus)", bookCtrl);
            b.createMessage("updateStatus(Delayed)", flightSch);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // View pending refunds: UI → RefundHandler → RefundRequest
        b.createMessage("viewPendingRefunds()", resUI);
          b.createMessage("getPendingRequests()", refundH);
            b.createMessage("queryByStatus(PENDING)", refundReq);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Review refund detail: RefundHandler → RefundRequest + FareRule
        b.createMessage("reviewRefundDetail(requestId)", resUI);
          b.createMessage("getRefundDetail(requestId)", refundH);
            b.createMessage("getDetail(requestId)", refundReq);
            b.endMessage();
            b.createMessage("checkRefundPolicy(fareClass)", fareRule);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Approve refund: RefundHandler → RefundRequest + PaymentGatewayInterface
        b.createMessage("approveRefund(requestId, approvedAmount)", resUI);
          b.createMessage("processRefund(requestId, approvedAmount)", refundH);
            b.createMessage("updateStatus(APPROVED)", refundReq);
            b.endMessage();
            b.createMessage("sendRefund(originalPaymentId, amount)", payGwI);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Deny refund: RefundHandler → RefundRequest
        b.createMessage("denyRefund(requestId, reason)", resUI);
          b.createMessage("denyRefund(requestId, reason)", refundH);
            b.createMessage("updateStatus(DENIED, reason)", refundReq);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 3. Skypass Member Booking & e-Ticket (Member → ECB classes)
    // ──────────────────────────────────────────────
    static String buildMemberBookingTicket() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel member       = b.createActor("SkypassMember");
        InstanceModel resUI     = b.createInstance("ReservationUI");
        InstanceModel bookCtrl  = b.createInstance("BookingController");
        InstanceModel skypassI  = b.createInstance("SkypassInterface");
        InstanceModel mileageAcct = b.createInstance("MileageAccount");
        InstanceModel payProc   = b.createInstance("PaymentProcessor");
        InstanceModel payGwI    = b.createInstance("PaymentGatewayInterface");
        InstanceModel ticket    = b.createInstance("Ticket");

        b.init(member);

        // Login: UI → BC → SkypassInterface (external boundary)
        b.createMessage("login(skypassNumber, password)", resUI);
          b.createMessage("authenticateMember(skypassNumber, password)", bookCtrl);
            b.createMessage("verifyMembership(skypassNumber, password)", skypassI);
            b.endMessage();
            b.createMessage("getMileageBalance(skypassNumber)", skypassI);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Search flights: UI → BC
        b.createMessage("searchFlights(origin, dest, date)", resUI);
          b.createMessage("processSearch(criteria)", bookCtrl);
          b.endMessage();
        b.endMessage();

        // Select itinerary (auto-filled profile): UI → BC
        b.createMessage("selectItinerary(flightId, fareClass)", resUI);
          b.createMessage("initiateBooking(flightId, fareClass, memberId)", bookCtrl);
          b.endMessage();
        b.endMessage();

        // Confirm passenger info: UI → BC
        b.createMessage("confirmPassengerInfo()", resUI);
          b.createMessage("confirmInfo(reservationId)", bookCtrl);
          b.endMessage();
        b.endMessage();

        // Apply mileage: UI → PaymentProcessor → SkypassInterface → MileageAccount
        b.createMessage("applyMileage(mileageAmount)", resUI);
          b.createMessage("applyMileage(reservationId, amount)", payProc);
            b.createMessage("verifyAndDeduct(skypassNumber, amount)", skypassI);
            b.endMessage();
            b.createMessage("updateBalance(remainingMileage)", mileageAcct);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // Payment: UI → PaymentProcessor → PaymentGatewayInterface
        b.createMessage("submitPayment(paymentInfo)", resUI);
          b.createMessage("processPayment(reservationId, paymentInfo)", payProc);
            b.createMessage("sendAuthorizationRequest(adjustedAmount, paymentInfo)", payGwI);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        // View my bookings: UI → BC
        b.createMessage("viewMyBookings()", resUI);
          b.createMessage("getBookingHistory(memberId)", bookCtrl);
          b.endMessage();
        b.endMessage();

        // View e-ticket: UI → BC → Ticket
        b.createMessage("viewETicket(pnrNumber)", resUI);
          b.createMessage("getTicketDetail(pnrNumber)", bookCtrl);
            b.createMessage("getByReservation(pnrNumber)", ticket);
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 4. Cancel & Refund (Iteration 2 — Strategy 패턴 시연)
    //    Member → ReservationUI → BookingController → Reservation (+State)
    //                                              → RefundHandler → RefundPolicy
    //                                              → PaymentGatewayInterface
    // ──────────────────────────────────────────────
    static String buildCancelRefund() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel member         = b.createActor("Member");
        InstanceModel resUI       = b.createInstance("ReservationUI");
        InstanceModel bookCtrl    = b.createInstance("BookingController");
        InstanceModel reservation = b.createInstance("Reservation");
        InstanceModel resState    = b.createInstance("ReservationState");
        InstanceModel refundH     = b.createInstance("RefundHandler");
        InstanceModel refundPolicy = b.createInstance("RefundPolicy");
        InstanceModel payGwI      = b.createInstance("PaymentGatewayInterface");

        b.init(member);

        // 1) 사용자 취소 요청 → BC.processCancellation(pnr)
        b.createMessage("requestCancellation(pnr)", resUI);
          b.createMessage("processCancellation(pnr)", bookCtrl);

            // 2) Reservation.requestCancellation() → State 전이 (Confirmed/Ticketed → CancellationRequested)
            b.createMessage("requestCancellation()", reservation);
              b.createMessage("transitionTo(CancellationRequestedState)", resState);
              b.endMessage();
            b.endMessage();

            // 3) Reservation.confirmCancellation() → State 전이 (CancellationRequested → Cancelled)
            b.createMessage("confirmCancellation()", reservation);
              b.createMessage("transitionTo(CancelledState)", resState);
              b.endMessage();
            b.endMessage();

            // 4) Reservation.requestRefund() (FareRule 검증 포함) → State 전이 (Cancelled → RefundRequested)
            b.createMessage("requestRefund()", reservation);
              b.createMessage("validateFareRule()", reservation);
              b.endMessage();
              b.createMessage("transitionTo(RefundRequestedState)", resState);
              b.endMessage();
            b.endMessage();

            // 5) RefundHandler.evaluateRefund(pnr, fareClass)
            //    → resolvePolicy(fareRule) (Strategy 핵심: Full/Partial/No 중 선택)
            //    → policy.calculateRefundAmount(paid)
            b.createMessage("evaluateRefund(pnr, fareClass)", refundH);
              b.createSelfCallMessage("resolvePolicy(fareRule)");
              b.endMessage();
              b.createMessage("calculateRefundAmount(paid)", refundPolicy);
              b.endMessage();
            b.endMessage();

            // 6) RefundHandler.processRefund(requestId, amount)
            //    → 게이트웨이로 송금 → Reservation.processRefundDecision(true) → Refunded
            b.createMessage("processRefund(requestId, amount)", refundH);
              b.createMessage("sendRefund(paymentId, amount)", payGwI);
              b.endMessage();
            b.endMessage();

            b.createMessage("processRefundDecision(approved=true)", reservation);
              b.createMessage("transitionTo(RefundedState)", resState);
              b.endMessage();
            b.endMessage();

          b.endMessage(); // processCancellation
        b.endMessage();   // requestCancellation(pnr)

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 5. Lookup Reservation (Iteration 2 — Guest 3중 검증 + alt 분기)
    //    Guest → ReservationUI → BookingController → AuthService
    //                                             → ReservationLookupService → Reservation
    // ──────────────────────────────────────────────
    static String buildLookupReservation() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel guest          = b.createActor("Guest");
        InstanceModel resUI       = b.createInstance("ReservationUI");
        InstanceModel bookCtrl    = b.createInstance("BookingController");
        InstanceModel authSvc     = b.createInstance("AuthService");
        InstanceModel lookupSvc   = b.createInstance("ReservationLookupService");
        InstanceModel reservation = b.createInstance("Reservation");

        b.init(guest);

        // 1) Guest 가 PNR + 이름 + 이메일 입력
        b.createMessage("lookupGuestBooking(pnr, name, email)", resUI);
          b.createMessage("lookupGuestBooking(pnr, name, email)", bookCtrl);

            // 2) 3중 검증 — AuthService.verifyGuest(pnr, name, email)
            //    내부적으로 PNR 존재 + name/email 비공백 + email 에 '@' 포함을 확인
            b.createMessage("verifyGuest(pnr, name, email)", authSvc);
              b.createSelfCallMessage("validate(pnr, name, email)");
              b.endMessage();
              b.createMessage("findByPnr(pnr)", reservation);
              b.endMessage();
            b.endMessage();

            // 3-a) alt[성공] → ReservationLookupService.findByGuestPnr(pnr, name, email)
            //                → Reservation.findByPnr(pnr) → 예약 상세 반환
            b.createMessage("[verified] findByGuestPnr(pnr, name, email)", lookupSvc);
              b.createMessage("verifyGuest(pnr, name, email)", authSvc);
              b.endMessage();
              b.createMessage("findByPnr(pnr)", reservation);
              b.endMessage();
            b.endMessage();

            // 3-b) alt[실패] → 에러 응답
            //                  (Note: 5회 연속 실패 시 15분 잠금 — AuthService 내부 정책)
            b.createSelfCallMessage("[verification failed] returnError(\"INVALID_CREDENTIALS\")");
            b.endMessage();
            b.createSelfCallMessage("[Note] 5회 연속 실패 시 15분 잠금");
            b.endMessage();

          b.endMessage(); // BC.lookupGuestBooking
        b.endMessage();   // UI.lookupGuestBooking

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        String xml1 = buildBookFlight();
        writeFile("src/bookFlight-iter2.sqd", xml1);
        System.out.println("Generated: src/bookFlight-iter2.sqd (" + xml1.length() + " bytes)");

        String xml2 = buildAdminOperations();
        writeFile("src/adminOperations-iter2.sqd", xml2);
        System.out.println("Generated: src/adminOperations-iter2.sqd (" + xml2.length() + " bytes)");

        String xml3 = buildMemberBookingTicket();
        writeFile("src/memberBookingTicket-iter2.sqd", xml3);
        System.out.println("Generated: src/memberBookingTicket-iter2.sqd (" + xml3.length() + " bytes)");

        // Iteration 2 신규 2개
        String xml4 = buildCancelRefund();
        writeFile("src/cancelRefund-iter2.sqd", xml4);
        System.out.println("Generated: src/cancelRefund-iter2.sqd (" + xml4.length() + " bytes)");

        String xml5 = buildLookupReservation();
        writeFile("src/lookupReservation-iter2.sqd", xml5);
        System.out.println("Generated: src/lookupReservation-iter2.sqd (" + xml5.length() + " bytes)");

        System.out.println("\nAll 5 sequence diagrams generated with concrete ECB classes!");
        System.out.println("  - Iteration 1: bookFlight, adminOperations, memberBookingTicket");
        System.out.println("  - Iteration 2: cancelRefund (Strategy), lookupReservation (Guest 3중 검증)");
    }

    static void writeFile(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
        }
    }
}
