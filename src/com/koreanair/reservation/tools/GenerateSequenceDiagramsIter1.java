package com.koreanair.reservation.tools;

import net.java.amateras.uml.sequencediagram.model.*;
import java.io.FileWriter;

/**
 * Generates 3 Sequence Diagrams (.sqd) for Iteration 1 scope using concrete ECB classes.
 *
 * <p>Iteration 1 (3): bookFlight, adminOperations, memberBookingTicket.
 *   ※ 같은 시나리오의 iter1·iter2 본문 콘텐츠는 동일 — 발표 자료에서 before/after 비교를
 *      위한 시점 구분용으로 출력 파일만 분리.
 *
 * <p>Iteration 2 신규(cancelRefund, lookupReservation)는 본 generator 에서 제외.
 *   해당 시나리오는 GenerateSequenceDiagramsIter2 에서 생성.
 */
public class GenerateSequenceDiagramsIter1 {

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
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        String xml1 = buildBookFlight();
        writeFile("src/bookFlight-iter1.sqd", xml1);
        System.out.println("Generated: src/bookFlight-iter1.sqd (" + xml1.length() + " bytes)");

        String xml2 = buildAdminOperations();
        writeFile("src/adminOperations-iter1.sqd", xml2);
        System.out.println("Generated: src/adminOperations-iter1.sqd (" + xml2.length() + " bytes)");

        String xml3 = buildMemberBookingTicket();
        writeFile("src/memberBookingTicket-iter1.sqd", xml3);
        System.out.println("Generated: src/memberBookingTicket-iter1.sqd (" + xml3.length() + " bytes)");

        System.out.println("\nAll 3 sequence diagrams (iter1) generated with concrete ECB classes!");
        System.out.println("  - Iteration 1: bookFlight, adminOperations, memberBookingTicket");
    }

    static void writeFile(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
        }
    }
}
