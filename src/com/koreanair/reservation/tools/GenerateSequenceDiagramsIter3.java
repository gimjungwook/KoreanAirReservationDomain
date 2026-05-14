package com.koreanair.reservation.tools;

import net.java.amateras.uml.sequencediagram.model.*;
import java.io.FileWriter;

/**
 * Iteration 3 Sequence Diagrams (.sqd) — Observer 패턴 4종 + Connecting Flight.
 *
 * <p>iter3 핵심 5개 시퀀스:
 * <ol>
 *   <li>seatHoldExpiry — SeatHoldMonitor.sweep → SeatHoldExpiredEvent → 좌석 해제 + Reservation 취소</li>
 *   <li>paymentFailureAutoCancel — PaymentProcessor.fail → PaymentFailedEvent → handlePaymentFailure</li>
 *   <li>flightStatusPropagation — FlightSchedule.changeStatus → FlightStatusChangedEvent → 전 Reservation 알림</li>
 *   <li>mileagePayment — PaymentProcessor.processMileagePayment → MileageAccount.withdraw → SkypassInterface</li>
 *   <li>connectingSearch — ItinerarySearchService.searchConnecting → MCT 검증 → Itinerary 반환</li>
 * </ol>
 */
public class GenerateSequenceDiagramsIter3 {

    // ──────────────────────────────────────────────
    // 1. Seat Hold Expiry (Observer 패턴)
    // ──────────────────────────────────────────────
    static String buildSeatHoldExpiry() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel scheduler          = b.createActor("Scheduler");
        InstanceModel monitor         = b.createInstance("SeatHoldMonitor");
        InstanceModel event           = b.createInstance("SeatHoldExpiredEvent");
        InstanceModel listener        = b.createInstance("ReservationHoldListener");
        InstanceModel seat            = b.createInstance("Seat");
        InstanceModel reservation     = b.createInstance("Reservation");

        b.init(scheduler);

        b.createMessage("sweep()", monitor);
          b.createSelfCallMessage("for each tracked seat");
          b.endMessage();
          b.createMessage("isHoldExpired()", seat);
          b.endMessage();

          // Publish event
          b.createMessage("new SeatHoldExpiredEvent(seat, pnr)", event);
          b.endMessage();
          b.createMessage("publish(event)", monitor);
            b.createMessage("onEvent(event)", listener);
              b.createMessage("release()", seat);
              b.endMessage();
              b.createMessage("findByPnr(pnr)", reservation);
              b.endMessage();
              b.createMessage("handlePaymentFailure()", reservation);
              b.endMessage();
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 2. Payment Failure → Auto Cancel
    // ──────────────────────────────────────────────
    static String buildPaymentFailureAutoCancel() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel passenger          = b.createActor("Passenger");
        InstanceModel ui              = b.createInstance("ReservationUI");
        InstanceModel bookCtrl        = b.createInstance("BookingController");
        InstanceModel payProc         = b.createInstance("PaymentProcessor");
        InstanceModel payGw           = b.createInstance("PaymentGatewayInterface");
        InstanceModel event           = b.createInstance("PaymentFailedEvent");
        InstanceModel listener        = b.createInstance("ReservationAutoCancelListener");
        InstanceModel reservation     = b.createInstance("Reservation");

        b.init(passenger);

        b.createMessage("submitPayment(paymentInfo)", ui);
          b.createMessage("confirmPayment(reservation, fareRule, baseFare, tax)", bookCtrl);
            b.createMessage("processPaymentCharge(amount, pnr)", payProc);
              b.createMessage("authorize(payment)", payGw);
              b.endMessage();
              b.createSelfCallMessage("[declined] payment.fail()");
              b.endMessage();
              b.createMessage("new PaymentFailedEvent(payment, pnr, reason)", event);
              b.endMessage();
              b.createMessage("publish(event)", payProc);
                b.createMessage("onEvent(event)", listener);
                  b.createMessage("findByPnr(pnr)", reservation);
                  b.endMessage();
                  b.createMessage("handlePaymentFailure()", reservation);
                    b.createSelfCallMessage("State: PendingPayment -> Cancelled");
                    b.endMessage();
                  b.endMessage();
                b.endMessage();
              b.endMessage();
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 3. Flight Status Propagation
    // ──────────────────────────────────────────────
    static String buildFlightStatusPropagation() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel admin              = b.createActor("Admin");
        InstanceModel ui              = b.createInstance("ReservationUI");
        InstanceModel bookCtrl        = b.createInstance("BookingController");
        InstanceModel schedule        = b.createInstance("FlightSchedule");
        InstanceModel event           = b.createInstance("FlightStatusChangedEvent");
        InstanceModel listener        = b.createInstance("AffectedReservationListener");
        InstanceModel registry        = b.createInstance("ReservationRegistry");
        InstanceModel reservation     = b.createInstance("Reservation");

        b.init(admin);

        b.createMessage("changeFlightStatus(flightNumber, CANCELLED)", ui);
          b.createMessage("changeFlightStatus(flightNumber, status)", bookCtrl);
            b.createMessage("changeStatus(CANCELLED)", schedule);
              b.createMessage("new FlightStatusChangedEvent(schedule, prev, new)", event);
              b.endMessage();
              b.createMessage("publish(event)", schedule);
                b.createMessage("onEvent(event)", listener);
                  b.createMessage("all()", registry);
                  b.endMessage();
                  b.createSelfCallMessage("filter Reservations referencing schedule");
                  b.endMessage();
                  b.createMessage("evaluateImpactOfFlightStatusChange()", reservation);
                  b.endMessage();
                b.endMessage();
              b.endMessage();
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 4. Mileage Payment
    // ──────────────────────────────────────────────
    static String buildMileagePayment() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel member             = b.createActor("Skypass Member");
        InstanceModel ui              = b.createInstance("ReservationUI");
        InstanceModel bookCtrl        = b.createInstance("BookingController");
        InstanceModel payProc         = b.createInstance("PaymentProcessor");
        InstanceModel account         = b.createInstance("MileageAccount");
        InstanceModel skypass         = b.createInstance("SkypassInterface");
        InstanceModel reservation     = b.createInstance("Reservation");

        b.init(member);

        b.createMessage("payWithMileage(reservation, mileageCost)", ui);
          b.createMessage("confirmMileagePayment(reservation, account, mileageCost)", bookCtrl);
            b.createMessage("processMileagePayment(account, mileageCost, pnr)", payProc);
              b.createMessage("getBalance()", account);
              b.endMessage();
              b.createMessage("withdraw(amount)", account);
              b.endMessage();
              b.createMessage("verifyAndDeduct(skypassNumber, amount)", skypass);
              b.endMessage();
              b.createSelfCallMessage("[success] payment.pay()");
              b.endMessage();
            b.endMessage();
            b.createMessage("addPayment(payment)", reservation);
            b.endMessage();
            b.createMessage("processPayment()", reservation);
              b.createSelfCallMessage("State: PendingPayment -> Confirmed");
              b.endMessage();
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    // ──────────────────────────────────────────────
    // 5. Connecting Flight Search
    // ──────────────────────────────────────────────
    static String buildConnectingSearch() {
        SequenceModelBuilder b = new SequenceModelBuilder();

        ActorModel passenger          = b.createActor("Passenger");
        InstanceModel ui              = b.createInstance("ReservationUI");
        InstanceModel itinSearch      = b.createInstance("ItinerarySearchService");
        InstanceModel flightSearch    = b.createInstance("FlightSearchService");
        InstanceModel itinerary       = b.createInstance("Itinerary");

        b.init(passenger);

        b.createMessage("searchConnecting(from, to, date)", ui);
          b.createMessage("searchConnecting(from, to, date, MCT)", itinSearch);
            b.createMessage("getCatalog()", flightSearch);
            b.endMessage();
            b.createSelfCallMessage("for each pair (a, b) where a.dest == b.origin && b.dest == to");
            b.endMessage();
            b.createMessage("Itinerary.connecting(a, b)", itinerary);
            b.endMessage();
            b.createMessage("isConnectionTimeValid(MCT)", itinerary);
              b.createSelfCallMessage("layover = arrival(a) - departure(b)");
              b.endMessage();
              b.createSelfCallMessage("layover >= MCT ? accept : reject");
              b.endMessage();
            b.endMessage();
          b.endMessage();
        b.endMessage();

        return b.toXML();
    }

    public static void main(String[] args) throws Exception {
        writeFile("src/seatHoldExpiry-iter3.sqd", buildSeatHoldExpiry());
        writeFile("src/paymentFailureAutoCancel-iter3.sqd", buildPaymentFailureAutoCancel());
        writeFile("src/flightStatusPropagation-iter3.sqd", buildFlightStatusPropagation());
        writeFile("src/mileagePayment-iter3.sqd", buildMileagePayment());
        writeFile("src/connectingSearch-iter3.sqd", buildConnectingSearch());
        System.out.println("All 5 iter3 sequence diagrams generated!");
    }

    static void writeFile(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
        }
        System.out.println("Generated: " + path + " (" + content.length() + " bytes)");
    }
}
