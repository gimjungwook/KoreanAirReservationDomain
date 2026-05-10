package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.*;
import net.java.amateras.uml.model.RootModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Generates the Korean Air Reservation System Class Diagram (.cld)
 * for ITERATION 2 — full scope including Strategy refund family,
 * RefundHandler, AuthService.verifyGuest, ReservationLookupService,
 * Ticket entity, and PaymentGateway.sendRefund.
 *
 * Uses AmaterasModeler's own model classes + XStream serialization.
 *
 * Output: src/classDiagram-iter2.cld
 *
 * Companion: {@link GenerateClassDiagramIter1} emits the
 * walking-skeleton subset (iter1 scope only).
 *
 * NOTE: Runs outside Eclipse, so we must bootstrap UMLPlugin by
 *       reflectively invoking ReflectionFactory to bypass the
 *       AbstractUIPlugin constructor chain.
 */
public class GenerateClassDiagramIter2 {

    /**
     * Bootstrap the UMLPlugin singleton without triggering the full
     * AbstractUIPlugin constructor.  Reflectively calls
     * sun.reflect.ReflectionFactory.newConstructorForSerialization()
     * to allocate via Object's no-arg ctor, then sets required fields.
     */
    static {
        try {
            // --- Reflectively obtain ReflectionFactory ---
            Class<?> rfClass = Class.forName("sun.reflect.ReflectionFactory");
            MethodHandle getRF = MethodHandles.lookup().findStatic(
                    rfClass, "getReflectionFactory", MethodType.methodType(rfClass));
            Object rf = getRF.invoke();

            MethodHandle newCtorForSer = MethodHandles.lookup().findVirtual(
                    rfClass, "newConstructorForSerialization",
                    MethodType.methodType(Constructor.class, Class.class, Constructor.class));

            Constructor<?> objCtor = Object.class.getDeclaredConstructor();
            Constructor<?> fakeCtor = (Constructor<?>) newCtorForSer.invoke(rf, UMLPlugin.class, objCtor);
            UMLPlugin fakePlugin = (UMLPlugin) fakeCtor.newInstance();

            // Set resourceBundle so getResourceString() works
            ResourceBundle rb;
            try {
                rb = ResourceBundle.getBundle("net.java.amateras.uml.UMLPlugin");
            } catch (MissingResourceException e) {
                // Dummy bundle: returns the key itself as the value
                rb = new ResourceBundle() {
                    @Override protected Object handleGetObject(String key) { return key; }
                    @Override public java.util.Enumeration<String> getKeys() {
                        return java.util.Collections.emptyEnumeration();
                    }
                };
            }
            Field rbField = UMLPlugin.class.getDeclaredField("resourceBundle");
            rbField.setAccessible(true);
            rbField.set(fakePlugin, rb);

            // Set the static plugin singleton
            Field pluginField = UMLPlugin.class.getDeclaredField("plugin");
            pluginField.setAccessible(true);
            pluginField.set(null, fakePlugin);

            System.out.println("[init] UMLPlugin bootstrapped successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to bootstrap UMLPlugin", e);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to bootstrap UMLPlugin", t);
        }
    }

    // ── Colors ──────────────────────────────────────────────────────
    static RGB WHITE      = new RGB(255, 255, 255);
    static RGB BLACK      = new RGB(0, 0, 0);
    static RGB ENTITY_BG  = new RGB(255, 255, 206);   // light yellow
    static RGB CONTROL_BG = new RGB(206, 230, 255);   // light blue
    static RGB BOUNDARY_BG = new RGB(220, 255, 220);  // light green

    // ── Helper: create AttributeModel ───────────────────────────────
    static AttributeModel attr(String name, String type, Visibility vis) {
        AttributeModel a = new AttributeModel();
        a.setName(name);
        a.setType(type);
        a.setVisibility(vis);
        return a;
    }

    // ── Helper: create OperationModel ───────────────────────────────
    static OperationModel op(String name, String returnType, Visibility vis) {
        OperationModel o = new OperationModel();
        o.setName(name);
        o.setType(returnType);
        o.setVisibility(vis);
        o.setParams(new ArrayList<>());
        return o;
    }

    static OperationModel op(String name, String returnType, Visibility vis, String[][] params) {
        OperationModel o = new OperationModel();
        o.setName(name);
        o.setType(returnType);
        o.setVisibility(vis);
        List<Argument> args = new ArrayList<>();
        for (String[] p : params) {
            Argument arg = new Argument();
            arg.setName(p[0]);
            arg.setType(p[1]);
            args.add(arg);
        }
        o.setParams(args);
        return o;
    }

    // ── Helper: create ClassModel ───────────────────────────────────
    static ClassModel cls(String name, String stereo, int x, int y, int w, int h,
                          RGB bg, RootModel root) {
        ClassModel c = new ClassModel();
        c.setName(name);
        if (stereo != null && !stereo.isEmpty()) {
            c.setStereoType(stereo);
        }
        c.setConstraint(new Rectangle(x, y, w, h));
        c.setBackgroundColor(bg);
        c.setForegroundColor(BLACK);
        c.setShowIcon(true);
        root.addChild(c);
        return c;
    }

    // ── Helper: create InterfaceModel ───────────────────────────────
    static InterfaceModel iface(String name, String stereo, int x, int y, int w, int h,
                                RGB bg, RootModel root) {
        InterfaceModel i = new InterfaceModel();
        i.setName(name);
        if (stereo != null && !stereo.isEmpty()) {
            i.setStereoType(stereo);
        }
        i.setConstraint(new Rectangle(x, y, w, h));
        i.setBackgroundColor(bg);
        i.setForegroundColor(BLACK);
        i.setShowIcon(true);
        root.addChild(i);
        return i;
    }

    // ── Helper: Generalization (inheritance) ────────────────────────
    static void generalization(ClassModel child, ClassModel parent) {
        GeneralizationModel g = new GeneralizationModel();
        g.setShowIcon(true);
        g.setSource(child);
        g.setTarget(parent);
        g.attachSource();
        g.attachTarget();
    }

    // ── Helper: Association ─────────────────────────────────────────
    static void association(CommonEntityModel from, CommonEntityModel to,
                            String fromMult, String toMult) {
        AssociationModel a = new AssociationModel();
        a.setShowIcon(true);
        a.setFromMultiplicity(fromMult);
        a.setToMultiplicity(toMult);
        a.setSource(from);
        a.setTarget(to);
        a.attachSource();
        a.attachTarget();
    }

    // ── Helper: Composition (filled diamond) ────────────────────────
    static void composition(CommonEntityModel whole, CommonEntityModel part,
                            String fromMult, String toMult) {
        CompositeModel c = new CompositeModel();
        c.setShowIcon(true);
        c.setFromMultiplicity(fromMult);
        c.setToMultiplicity(toMult);
        c.setSource(whole);
        c.setTarget(part);
        c.attachSource();
        c.attachTarget();
    }

    // ── Helper: Dependency (dashed arrow) ───────────────────────────
    static void dependency(CommonEntityModel from, CommonEntityModel to) {
        DependencyModel d = new DependencyModel();
        d.setShowIcon(true);
        d.setSource(from);
        d.setTarget(to);
        d.attachSource();
        d.attachTarget();
    }

    // ── Helper: Realization (interface implementation) ───────────────
    static void realization(CommonEntityModel impl, InterfaceModel iface) {
        RealizationModel r = new RealizationModel();
        r.setShowIcon(true);
        r.setSource(impl);
        r.setTarget(iface);
        r.attachSource();
        r.attachTarget();
    }

    // ═════════════════════════════════════════════════════════════════
    //  MAIN
    // ═════════════════════════════════════════════════════════════════
    public static void main(String[] args) throws Exception {

        // === ROOT ===
        RootModel root = new RootModel();
        root.setBackgroundColor(WHITE);
        root.setForegroundColor(BLACK);
        root.setShowIcon(true);

        // ─────────────────────────────────────────────────────────────
        //  ENTITY CLASSES  (light yellow)
        //  Height formula: 55 + (attrs + ops) × 18, rounded to nearest 5
        // ─────────────────────────────────────────────────────────────

        // ── Passenger family (top-left) ─────────────────────────────
        ClassModel passenger = cls("Passenger", "entity", 30, 30, 220, 170,
                ENTITY_BG, root);                                      // 3a+3o=6 → 163→170
        passenger.addChild(attr("passengerId", "String", Visibility.PRIVATE));
        passenger.addChild(attr("name", "String", Visibility.PRIVATE));
        passenger.addChild(attr("contactInfo", "String", Visibility.PRIVATE));
        passenger.addChild(op("getPassengerId", "String", Visibility.PUBLIC));
        passenger.addChild(op("getName", "String", Visibility.PUBLIC));
        passenger.addChild(op("getContactInfo", "String", Visibility.PUBLIC));

        ClassModel skypassMember = cls("SkypassMember", "entity", 30, 230, 220, 135,
                ENTITY_BG, root);                                      // 2a+2o=4 → 127→135
        skypassMember.addChild(attr("skypassNumber", "String", Visibility.PRIVATE));
        skypassMember.addChild(attr("tier", "String", Visibility.PRIVATE));
        skypassMember.addChild(op("getSkypassNumber", "String", Visibility.PUBLIC));
        skypassMember.addChild(op("getTier", "String", Visibility.PUBLIC));

        ClassModel guest = cls("Guest", "entity", 290, 230, 200, 100,
                ENTITY_BG, root);                                      // 1a+1o=2 → 91→100
        guest.addChild(attr("guestSessionId", "String", Visibility.PRIVATE));
        guest.addChild(op("getGuestSessionId", "String", Visibility.PUBLIC));

        ClassModel mileageAccount = cls("MileageAccount", "entity", 30, 395, 220, 170,
                ENTITY_BG, root);                                      // 2a+4o=6 → 163→170
        mileageAccount.addChild(attr("accountId", "Long", Visibility.PRIVATE));
        mileageAccount.addChild(attr("balance", "BigDecimal", Visibility.PRIVATE));
        mileageAccount.addChild(op("getBalance", "BigDecimal", Visibility.PUBLIC));
        mileageAccount.addChild(op("updateBalance", "void", Visibility.PUBLIC,
                new String[][]{{"remainingMileage", "BigDecimal"}}));
        mileageAccount.addChild(op("deposit", "void", Visibility.PUBLIC,
                new String[][]{{"amount", "BigDecimal"}}));
        mileageAccount.addChild(op("withdraw", "boolean", Visibility.PUBLIC,
                new String[][]{{"amount", "BigDecimal"}}));

        // ── FlightSchedule family (top-center) ─────────────────────
        ClassModel flightSchedule = cls("FlightSchedule", "entity", 540, 30, 240, 200,
                ENTITY_BG, root);                                      // 4a+4o=8 → 199→200
        flightSchedule.addChild(attr("flightNumber", "String", Visibility.PRIVATE));
        flightSchedule.addChild(attr("departureTime", "DateTime", Visibility.PRIVATE));
        flightSchedule.addChild(attr("arrivalTime", "DateTime", Visibility.PRIVATE));
        flightSchedule.addChild(attr("status", "String", Visibility.PRIVATE));
        flightSchedule.addChild(op("getFlightNumber", "String", Visibility.PUBLIC));
        flightSchedule.addChild(op("getDuration", "Duration", Visibility.PUBLIC));
        flightSchedule.addChild(op("create", "FlightSchedule", Visibility.PUBLIC,
                new String[][]{{"flightNumber", "String"}, {"departure", "Airport"}, {"arrival", "Airport"}, {"aircraftType", "AircraftType"}}));
        flightSchedule.addChild(op("updateStatus", "void", Visibility.PUBLIC,
                new String[][]{{"newStatus", "String"}}));

        ClassModel fareRule = cls("FareRule", "entity", 540, 250, 240, 190,
                ENTITY_BG, root);                                      // 4a+3o=7 → 181→190
        fareRule.addChild(attr("fareClass", "String", Visibility.PRIVATE));
        fareRule.addChild(attr("isRefundable", "boolean", Visibility.PRIVATE));
        fareRule.addChild(attr("changeFee", "BigDecimal", Visibility.PRIVATE));
        fareRule.addChild(attr("cancellationPenalty", "BigDecimal", Visibility.PRIVATE));
        fareRule.addChild(op("isRefundable", "boolean", Visibility.PUBLIC));
        fareRule.addChild(op("getChangeFee", "BigDecimal", Visibility.PUBLIC));
        fareRule.addChild(op("checkRefundPolicy", "RefundPolicy", Visibility.PUBLIC));

        // ── AircraftType, Seat, Airport (top-right) ─────────────────
        ClassModel aircraftType = cls("AircraftType", "entity", 820, 30, 210, 135,
                ENTITY_BG, root);                                      // 2a+2o=4 → 127→135
        aircraftType.addChild(attr("typeCode", "String", Visibility.PRIVATE));
        aircraftType.addChild(attr("modelName", "String", Visibility.PRIVATE));
        aircraftType.addChild(op("getTypeCode", "String", Visibility.PUBLIC));
        aircraftType.addChild(op("getModelName", "String", Visibility.PUBLIC));

        ClassModel seat = cls("Seat", "entity", 1070, 30, 210, 200,
                ENTITY_BG, root);                                      // 3a+5o=8 → 199→200
        seat.addChild(attr("seatNumber", "String", Visibility.PRIVATE));
        seat.addChild(attr("cabinClass", "CabinClass", Visibility.PRIVATE));
        seat.addChild(attr("status", "SeatStatus", Visibility.PRIVATE));
        seat.addChild(op("getSeatNumber", "String", Visibility.PUBLIC));
        seat.addChild(op("getStatus", "SeatStatus", Visibility.PUBLIC));
        seat.addChild(op("hold", "void", Visibility.PUBLIC,
                new String[][]{{"timeoutMinutes", "int"}}));
        seat.addChild(op("updateStatus", "void", Visibility.PUBLIC,
                new String[][]{{"newStatus", "SeatStatus"}}));
        seat.addChild(op("release", "void", Visibility.PUBLIC));

        ClassModel airport = cls("Airport", "entity", 1070, 250, 210, 150,
                ENTITY_BG, root);                                      // 3a+2o=5 → 145→150
        airport.addChild(attr("airportCode", "String", Visibility.PRIVATE));
        airport.addChild(attr("airportName", "String", Visibility.PRIVATE));
        airport.addChild(attr("city", "String", Visibility.PRIVATE));
        airport.addChild(op("getAirportCode", "String", Visibility.PUBLIC));
        airport.addChild(op("getAirportName", "String", Visibility.PUBLIC));

        // ── Reservation chain (middle row) ──────────────────────────
        ClassModel reservation = cls("Reservation", "entity", 290, 490, 230, 380,
                ENTITY_BG, root);                                      // 4a+13o=17 → 361→380
        reservation.addChild(attr("pnrNumber", "String", Visibility.PRIVATE));
        reservation.addChild(attr("reservationDate", "LocalDateTime", Visibility.PRIVATE));
        reservation.addChild(attr("currentState", "ReservationState", Visibility.PRIVATE));
        reservation.addChild(attr("status", "ReservationStatus", Visibility.PRIVATE));
        reservation.addChild(op("getPnrNumber", "String", Visibility.PUBLIC));
        reservation.addChild(op("getStatus", "ReservationStatus", Visibility.PUBLIC));
        reservation.addChild(op("create", "Reservation", Visibility.PUBLIC,
                new String[][]{{"initialStatus", "ReservationStatus"}}));
        reservation.addChild(op("setState", "void", Visibility.PUBLIC,
                new String[][]{{"next", "ReservationState"}}));
        reservation.addChild(op("getStateName", "String", Visibility.PUBLIC));
        reservation.addChild(op("enterPassengerInfo", "void", Visibility.PUBLIC,
                new String[][]{{"p", "Passenger"}}));
        reservation.addChild(op("processPayment", "void", Visibility.PUBLIC));
        reservation.addChild(op("handlePaymentFailure", "void", Visibility.PUBLIC));
        reservation.addChild(op("issueTicket", "void", Visibility.PUBLIC));
        reservation.addChild(op("addTicket", "void", Visibility.PUBLIC,
                new String[][]{{"ticket", "Ticket"}}));
        reservation.addChild(op("addPayment", "void", Visibility.PUBLIC,
                new String[][]{{"payment", "Payment"}}));
        reservation.addChild(op("requestCancellation", "void", Visibility.PUBLIC));
        reservation.addChild(op("confirmCancellation", "void", Visibility.PUBLIC));
        reservation.addChild(op("requestRefund", "void", Visibility.PUBLIC));
        reservation.addChild(op("processRefundDecision", "void", Visibility.PUBLIC,
                new String[][]{{"approved", "boolean"}}));
        reservation.addChild(op("findByPnr", "Reservation", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}}));
        reservation.addChild(op("getContactEmail", "String", Visibility.PUBLIC));
        reservation.addChild(op("getReservationDetail", "Reservation", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}}));

        ClassModel itinerary = cls("Itinerary", "entity", 560, 490, 210, 115,
                ENTITY_BG, root);                                      // 1a+2o=3 → 109→115
        itinerary.addChild(attr("tripType", "String", Visibility.PRIVATE));
        itinerary.addChild(op("getTripType", "String", Visibility.PUBLIC));
        itinerary.addChild(op("getSegments", "List", Visibility.PUBLIC));

        ClassModel segment = cls("Segment", "entity", 810, 490, 240, 205,
                ENTITY_BG, root);                                      // 4a+4o=8 → 199→205
        segment.addChild(attr("sequenceNumber", "int", Visibility.PRIVATE));
        segment.addChild(attr("departureTime", "LocalDateTime", Visibility.PRIVATE));
        segment.addChild(attr("arrivalTime", "LocalDateTime", Visibility.PRIVATE));
        segment.addChild(attr("connectionTime", "Duration", Visibility.PRIVATE));
        segment.addChild(op("getSequenceNumber", "int", Visibility.PUBLIC));
        segment.addChild(op("getDepartureTime", "LocalDateTime", Visibility.PUBLIC));
        segment.addChild(op("getFlightSchedule", "FlightSchedule", Visibility.PUBLIC));
        segment.addChild(op("setFlightSchedule", "void", Visibility.PUBLIC,
                new String[][]{{"flightSchedule", "FlightSchedule"}}));

        // ── Transaction family (bottom row, y=800 due to taller Reservation) ───
        ClassModel payment = cls("Payment", "entity", 290, 880, 220, 220,
                ENTITY_BG, root);                                      // 4a+5o=9 → 217→220
        payment.addChild(attr("paymentId", "Long", Visibility.PRIVATE));
        payment.addChild(attr("amount", "BigDecimal", Visibility.PRIVATE));
        payment.addChild(attr("paymentMethod", "PaymentMethod", Visibility.PRIVATE));
        payment.addChild(attr("status", "PaymentStatus", Visibility.PRIVATE));
        payment.addChild(op("getPaymentId", "Long", Visibility.PUBLIC));
        payment.addChild(op("getAmount", "BigDecimal", Visibility.PUBLIC));
        payment.addChild(op("getStatus", "PaymentStatus", Visibility.PUBLIC));
        payment.addChild(op("pay", "void", Visibility.PUBLIC));
        payment.addChild(op("fail", "void", Visibility.PUBLIC));

        ClassModel ticket = cls("Ticket", "entity", 550, 800, 210, 220,
                ENTITY_BG, root);                                      // 3a+6o=9 → 217→220
        ticket.addChild(attr("ticketNumber", "String", Visibility.PRIVATE));
        ticket.addChild(attr("status", "TicketStatus", Visibility.PRIVATE));
        ticket.addChild(attr("issuedAt", "LocalDateTime", Visibility.PRIVATE));
        ticket.addChild(op("getTicketNumber", "String", Visibility.PUBLIC));
        ticket.addChild(op("getStatus", "TicketStatus", Visibility.PUBLIC));
        ticket.addChild(op("issue", "void", Visibility.PUBLIC));
        ticket.addChild(op("cancel", "void", Visibility.PUBLIC));
        ticket.addChild(op("generate", "Ticket", Visibility.PUBLIC,
                new String[][]{{"reservation", "Reservation"}, {"passenger", "Passenger"}, {"seat", "Seat"}}));
        ticket.addChild(op("getByReservation", "Ticket", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}}));

        ClassModel refundRequest = cls("RefundRequest", "entity", 800, 800, 230, 235,
                ENTITY_BG, root);                                      // 5a+5o=10 → 235→235
        refundRequest.addChild(attr("requestId", "String", Visibility.PRIVATE));
        refundRequest.addChild(attr("requestDate", "LocalDateTime", Visibility.PRIVATE));
        refundRequest.addChild(attr("refundAmount", "BigDecimal", Visibility.PRIVATE));
        refundRequest.addChild(attr("status", "RefundStatus", Visibility.PRIVATE));
        refundRequest.addChild(attr("reason", "String", Visibility.PRIVATE));
        refundRequest.addChild(op("getRequestId", "String", Visibility.PUBLIC));
        refundRequest.addChild(op("getStatus", "RefundStatus", Visibility.PUBLIC));
        refundRequest.addChild(op("queryByStatus", "List", Visibility.PUBLIC,
                new String[][]{{"status", "RefundStatus"}}));
        refundRequest.addChild(op("getDetail", "RefundRequest", Visibility.PUBLIC,
                new String[][]{{"requestId", "String"}}));
        refundRequest.addChild(op("updateStatus", "void", Visibility.PUBLIC,
                new String[][]{{"newStatus", "RefundStatus"}}));

        // ─────────────────────────────────────────────────────────────
        //  CONTROL CLASSES  (light blue)
        // ─────────────────────────────────────────────────────────────

        ClassModel bookingController = cls("BookingController", "control", 1340, 490, 260, 310,
                CONTROL_BG, root);                                     // 14o → 307→310
        bookingController.addChild(op("processSearch", "List", Visibility.PUBLIC,
                new String[][]{{"searchCriteria", "Object"}}));
        bookingController.addChild(op("initiateBooking", "Reservation", Visibility.PUBLIC,
                new String[][]{{"flightId", "Long"}, {"fareClass", "String"}}));
        bookingController.addChild(op("setPassengerInfo", "void", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"passengerData", "Object"}}));
        bookingController.addChild(op("confirmInfo", "void", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}}));
        bookingController.addChild(op("assignSeat", "void", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"seatNumber", "String"}}));
        bookingController.addChild(op("processCancellation", "void", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}}));
        bookingController.addChild(op("changeFlightStatus", "void", Visibility.PUBLIC,
                new String[][]{{"flightNo", "String"}, {"newStatus", "String"}}));
        bookingController.addChild(op("createSchedule", "FlightSchedule", Visibility.PUBLIC,
                new String[][]{{"scheduleData", "Object"}}));
        bookingController.addChild(op("authenticateAdmin", "boolean", Visibility.PUBLIC,
                new String[][]{{"adminId", "String"}, {"password", "String"}}));
        bookingController.addChild(op("authenticateMember", "Object", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        bookingController.addChild(op("getBookingHistory", "Object", Visibility.PUBLIC,
                new String[][]{{"memberId", "Long"}}));
        bookingController.addChild(op("getTicketDetail", "Object", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}}));
        bookingController.addChild(op("verifyGuestIdentity", "boolean", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}, {"name", "String"}, {"email", "String"}}));
        bookingController.addChild(op("reconfirmGuestIdentity", "boolean", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}, {"email", "String"}}));

        ClassModel paymentProcessor = cls("PaymentProcessor", "control", 1340, 820, 260, 130,
                CONTROL_BG, root);                                     // 4o → 127→130
        paymentProcessor.addChild(op("processPayment", "boolean", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"paymentInfo", "Object"}}));
        paymentProcessor.addChild(op("validateFareRule", "boolean", Visibility.PUBLIC,
                new String[][]{{"fareClass", "String"}}));
        paymentProcessor.addChild(op("calculateTotal", "BigDecimal", Visibility.PUBLIC,
                new String[][]{{"fare", "BigDecimal"}, {"tax", "BigDecimal"}, {"seatSurcharge", "BigDecimal"}}));
        paymentProcessor.addChild(op("applyMileage", "boolean", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"mileageAmount", "int"}}));

        ClassModel refundHandler = cls("RefundHandler", "control", 1340, 970, 260, 165,
                CONTROL_BG, root);                                     // 6o → 163→165
        refundHandler.addChild(op("evaluateRefund", "Object", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}, {"fareClass", "String"}}));
        refundHandler.addChild(op("getRefundDetail", "RefundRequest", Visibility.PUBLIC,
                new String[][]{{"requestId", "String"}}));
        refundHandler.addChild(op("processRefund", "void", Visibility.PUBLIC,
                new String[][]{{"requestId", "String"}, {"approvedAmount", "BigDecimal"}}));
        refundHandler.addChild(op("denyRefund", "void", Visibility.PUBLIC,
                new String[][]{{"requestId", "String"}, {"reason", "String"}}));
        refundHandler.addChild(op("getPendingRequests", "List", Visibility.PUBLIC));
        refundHandler.addChild(op("resolvePolicy", "RefundPolicy", Visibility.PRIVATE,
                new String[][]{{"fareRule", "FareRule"}}));

        // ─────────────────────────────────────────────────────────────
        //  BOUNDARY CLASSES  (light green)
        // ─────────────────────────────────────────────────────────────

        ClassModel reservationUI = cls("ReservationUI", "boundary", 1640, 490, 270, 115,
                BOUNDARY_BG, root);                                    // 3o → 109→115
        reservationUI.addChild(op("displaySearchResults", "void", Visibility.PUBLIC,
                new String[][]{{"flights", "List"}}));
        reservationUI.addChild(op("displaySeatMap", "void", Visibility.PUBLIC,
                new String[][]{{"seats", "List"}}));
        reservationUI.addChild(op("displayBookingConfirmation", "void", Visibility.PUBLIC,
                new String[][]{{"pnrNumber", "String"}}));

        ClassModel paymentGateway = cls("PaymentGatewayInterface", "boundary", 1640, 625, 270, 115,
                BOUNDARY_BG, root);                                    // 3o → 109→115
        paymentGateway.addChild(op("sendAuthorizationRequest", "Object", Visibility.PUBLIC,
                new String[][]{{"amount", "BigDecimal"}, {"paymentInfo", "Object"}}));
        paymentGateway.addChild(op("receiveTransactionResult", "Object", Visibility.PUBLIC));
        paymentGateway.addChild(op("sendRefund", "Object", Visibility.PUBLIC,
                new String[][]{{"originalPaymentId", "String"}, {"amount", "BigDecimal"}}));

        ClassModel skypassInterface = cls("SkypassInterface", "boundary", 1640, 760, 270, 135,
                BOUNDARY_BG, root);                                    // 4o → 127→135
        skypassInterface.addChild(op("verifyMembership", "Object", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        skypassInterface.addChild(op("getMileageBalance", "int", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}}));
        skypassInterface.addChild(op("deductMileage", "boolean", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"amount", "int"}}));
        skypassInterface.addChild(op("verifyAndDeduct", "Object", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"amount", "int"}}));

        ClassModel gdsInterface = cls("GDSInterface", "boundary", 1640, 915, 270, 100,
                BOUNDARY_BG, root);                                    // 2o → 91→100
        gdsInterface.addChild(op("searchInterlineFlights", "List", Visibility.PUBLIC,
                new String[][]{{"origin", "String"}, {"destination", "String"}}));
        gdsInterface.addChild(op("getPartnerAvailability", "List", Visibility.PUBLIC,
                new String[][]{{"flightNumber", "String"}}));

        // ─────────────────────────────────────────────────────────────
        //  ITERATION 2 신규 — Refund entity / AuthService / FlightSearchService / ReservationLookupService
        //  좌하단 빈 공간 (x=30, y=620~) 에 배치하여 기존 레이아웃 보존
        // ─────────────────────────────────────────────────────────────

        // Refund (entity) — RefundRequest 와 별개의 트랜잭션 결과 엔티티
        ClassModel refund = cls("Refund", "entity", 30, 620, 240, 280,
                ENTITY_BG, root);                                      // 5a+8o=13 → 289→280
        refund.addChild(attr("refundId", "Long", Visibility.PRIVATE));
        refund.addChild(attr("refundIdString", "String", Visibility.PRIVATE));
        refund.addChild(attr("refundAmount", "BigDecimal", Visibility.PRIVATE));
        refund.addChild(attr("status", "RefundStatus", Visibility.PRIVATE));
        refund.addChild(attr("reason", "String", Visibility.PRIVATE));
        refund.addChild(op("getRefundId", "Long", Visibility.PUBLIC));
        refund.addChild(op("getRefundIdString", "String", Visibility.PUBLIC));
        refund.addChild(op("getRefundAmount", "BigDecimal", Visibility.PUBLIC));
        refund.addChild(op("getStatus", "RefundStatus", Visibility.PUBLIC));
        refund.addChild(op("approve", "void", Visibility.PUBLIC));
        refund.addChild(op("reject", "void", Visibility.PUBLIC,
                new String[][]{{"reason", "String"}}));
        refund.addChild(op("complete", "void", Visibility.PUBLIC));

        // AuthService (control) — iter2 신규
        ClassModel authService = cls("AuthService", "control", 1340, 30, 260, 220,
                CONTROL_BG, root);                                     // 8o → 199→220
        authService.addChild(op("registerMember", "Member", Visibility.PUBLIC,
                new String[][]{{"member", "Member"}, {"skypassNumber", "String"}, {"password", "String"}}));
        authService.addChild(op("login", "Member", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        authService.addChild(op("loginWithHash", "Member", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        authService.addChild(op("loginByName", "Member", Visibility.PUBLIC,
                new String[][]{{"name", "String"}, {"password", "String"}}));
        authService.addChild(op("verifyGuest", "boolean", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}, {"name", "String"}, {"email", "String"}}));
        authService.addChild(op("logout", "void", Visibility.PUBLIC));
        authService.addChild(op("currentMember", "Member", Visibility.PUBLIC));
        authService.addChild(op("generateSkypassNumber", "String", Visibility.PUBLIC));

        // FlightSearchService (control) — iter1 walking-skeleton, iter2에서도 활용
        ClassModel flightSearchService = cls("FlightSearchService", "control", 1340, 270, 260, 115,
                CONTROL_BG, root);                                     // 3o → 109→115
        flightSearchService.addChild(op("addSchedule", "void", Visibility.PUBLIC,
                new String[][]{{"schedule", "FlightSchedule"}}));
        flightSearchService.addChild(op("search", "List", Visibility.PUBLIC,
                new String[][]{{"fromAirportCode", "String"}, {"toAirportCode", "String"}, {"date", "LocalDate"}}));
        flightSearchService.addChild(op("getCatalog", "List", Visibility.PUBLIC));

        // ReservationLookupService (control) — iter2 신규 (회원/비회원 분기 조회)
        ClassModel reservationLookupService = cls("ReservationLookupService", "control", 1340, 395, 260, 95,
                CONTROL_BG, root);                                     // 2o → 91→95
        reservationLookupService.addChild(op("findByMember", "List", Visibility.PUBLIC,
                new String[][]{{"member", "Member"}}));
        reservationLookupService.addChild(op("findByGuestPnr", "Reservation", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}, {"name", "String"}, {"email", "String"}}));

        // ─────────────────────────────────────────────────────────────
        //  STRATEGY PATTERN — RefundPolicy (light yellow, entity area)
        //  Positioned right-center, below RefundHandler (x≈1050, y≈1155)
        // ─────────────────────────────────────────────────────────────

        InterfaceModel refundPolicy = iface("RefundPolicy", "", 1050, 1155, 255, 95,
                ENTITY_BG, root);                                      // 0a+2o=2 → 91→95
        refundPolicy.addChild(op("calculateRefundAmount", "int", Visibility.PUBLIC,
                new String[][]{{"baseAmount", "int"}}));
        refundPolicy.addChild(op("getRefundType", "String", Visibility.PUBLIC));

        ClassModel fullRefundPolicy = cls("FullRefundPolicy", "", 750, 1305, 225, 95,
                ENTITY_BG, root);                                      // 0a+2o=2 → 91→95
        fullRefundPolicy.addChild(op("calculateRefundAmount", "int", Visibility.PUBLIC,
                new String[][]{{"baseAmount", "int"}}));
        fullRefundPolicy.addChild(op("getRefundType", "String", Visibility.PUBLIC));

        ClassModel partialRefundPolicy = cls("PartialRefundPolicy", "", 1005, 1305, 255, 95,
                ENTITY_BG, root);                                      // 0a+2o=2 → 91→95
        partialRefundPolicy.addChild(op("calculateRefundAmount", "int", Visibility.PUBLIC,
                new String[][]{{"baseAmount", "int"}}));
        partialRefundPolicy.addChild(op("getRefundType", "String", Visibility.PUBLIC));

        ClassModel noRefundPolicy = cls("NoRefundPolicy", "", 1290, 1305, 225, 95,
                ENTITY_BG, root);                                      // 0a+2o=2 → 91→95
        noRefundPolicy.addChild(op("calculateRefundAmount", "int", Visibility.PUBLIC,
                new String[][]{{"baseAmount", "int"}}));
        noRefundPolicy.addChild(op("getRefundType", "String", Visibility.PUBLIC));

        // ─────────────────────────────────────────────────────────────
        //  STATE PATTERN — ReservationState (light yellow, entity area)
        //  Positioned below Reservation chain (x≈300, y≈1105)
        // ─────────────────────────────────────────────────────────────

        InterfaceModel reservationState = iface("ReservationState", "", 305, 1105, 255, 200,
                ENTITY_BG, root);                                      // 0a+8o=8 → 199→200
        reservationState.addChild(op("enterPassengerInfo", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("processPayment", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("handlePaymentFailure", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("issueTicket", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("requestCancellation", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("confirmCancellation", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("requestRefund", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}}));
        reservationState.addChild(op("processRefundDecision", "void", Visibility.PUBLIC,
                new String[][]{{"ctx", "Reservation"}, {"approved", "boolean"}}));

        // 8 concrete state classes — row 1 (y=1305)
        ClassModel initiatedState = cls("InitiatedState", "", 30, 1305, 230, 70,
                ENTITY_BG, root);
        ClassModel pendingPaymentState = cls("PendingPaymentState", "", 275, 1305, 255, 70,
                ENTITY_BG, root);
        ClassModel confirmedState = cls("ConfirmedState", "", 545, 1305, 230, 70,
                ENTITY_BG, root);
        ClassModel ticketedState = cls("TicketedState", "", 790, 1305, 220, 70,
                ENTITY_BG, root);

        // 8 concrete state classes — row 2 (y=1395)
        ClassModel cancellationRequestedState = cls("CancellationRequestedState", "", 30, 1395, 290, 70,
                ENTITY_BG, root);
        ClassModel cancelledState = cls("CancelledState", "", 335, 1395, 220, 70,
                ENTITY_BG, root);
        ClassModel refundRequestedState = cls("RefundRequestedState", "", 570, 1395, 255, 70,
                ENTITY_BG, root);
        ClassModel refundedState = cls("RefundedState", "", 840, 1395, 215, 70,
                ENTITY_BG, root);

        // ─────────────────────────────────────────────────────────────
        //  RELATIONSHIPS
        // ─────────────────────────────────────────────────────────────

        // ── Generalization (inheritance) ────────────────────────────
        generalization(skypassMember, passenger);   // SkypassMember extends Passenger
        generalization(guest, passenger);           // Guest extends Passenger

        // ── Composition (filled diamond) ────────────────────────────
        composition(reservation, itinerary, "1", "1");        // Reservation *-- Itinerary
        composition(itinerary, segment, "1", "1..*");         // Itinerary *-- Segment
        composition(aircraftType, seat, "1", "*");            // AircraftType *-- Seat

        // ── Associations ────────────────────────────────────────────
        association(passenger, reservation, "1", "0..*");             // Passenger -- Reservation
        association(skypassMember, mileageAccount, "1", "1");         // SkypassMember -- MileageAccount
        association(segment, flightSchedule, "*", "1");               // Segment -- FlightSchedule
        association(segment, airport, "*", "1");                      // Segment -- Airport
        association(flightSchedule, aircraftType, "*", "1");          // FlightSchedule -- AircraftType
        association(reservation, payment, "1", "0..*");               // Reservation -- Payment
        association(reservation, ticket, "1", "0..*");                // Reservation -- Ticket
        association(ticket, segment, "*", "1");                        // Ticket -- Segment
        association(flightSchedule, fareRule, "1", "*");              // FlightSchedule -- FareRule
        association(reservation, refundRequest, "1", "0..1");         // Reservation -- RefundRequest

        // ── Dependencies (control/boundary → entity) ────────────────
        dependency(bookingController, reservation);
        dependency(bookingController, passenger);
        dependency(bookingController, itinerary);
        dependency(paymentProcessor, payment);
        dependency(paymentProcessor, fareRule);
        dependency(refundHandler, refundRequest);
        dependency(refundHandler, reservation);

        dependency(reservationUI, bookingController);
        dependency(paymentGateway, paymentProcessor);
        dependency(skypassInterface, mileageAccount);
        dependency(gdsInterface, flightSchedule);

        // ── Iteration 2 신규 의존 / 연관 ─────────────────────────────
        dependency(refundHandler, refund);              // RefundHandler creates Refund
        association(refundRequest, refund, "1", "0..1");// RefundRequest -> Refund (승인 시 1:1)
        dependency(authService, reservation);            // verifyGuest 가 Reservation.findByPnr 사용
        dependency(flightSearchService, flightSchedule); // 직항 검색
        dependency(bookingController, flightSearchService);
        dependency(bookingController, authService);
        dependency(bookingController, refundHandler);
        dependency(bookingController, reservationLookupService);
        dependency(reservationLookupService, authService);
        dependency(reservationLookupService, reservation);

        // ── Strategy pattern (RefundPolicy) ─────────────────────────
        realization(fullRefundPolicy, refundPolicy);
        realization(partialRefundPolicy, refundPolicy);
        realization(noRefundPolicy, refundPolicy);
        dependency(refundHandler, refundPolicy);        // RefundHandler uses RefundPolicy

        // ── State pattern (ReservationState) ────────────────────────
        composition(reservation, reservationState, "1", "1");   // Reservation *-- ReservationState
        realization(initiatedState, reservationState);
        realization(pendingPaymentState, reservationState);
        realization(confirmedState, reservationState);
        realization(ticketedState, reservationState);
        realization(cancellationRequestedState, reservationState);
        realization(cancelledState, reservationState);
        realization(refundRequestedState, reservationState);
        realization(refundedState, reservationState);

        // ─────────────────────────────────────────────────────────────
        //  SERIALIZE
        // ─────────────────────────────────────────────────────────────
        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);

        String outputPath = "src/classDiagram-iter2.cld";
        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write(xml);
        }

        System.out.println("[Iter2] Generated: " + outputPath);
        System.out.println();
        System.out.println("=== Summary (Iter2 — Full Scope) ===");
        System.out.println("Entity classes:   16  (+Refund iter2)");
        System.out.println("Control classes:   6  (+AuthService +FlightSearchService +ReservationLookupService iter2)");
        System.out.println("Boundary classes:  4  (original)");
        System.out.println("--- Design Patterns (3rd revision) ---");
        System.out.println("Strategy interfaces: 1  (RefundPolicy)");
        System.out.println("Strategy concretes:  3  (FullRefundPolicy, PartialRefundPolicy, NoRefundPolicy)");
        System.out.println("State interfaces:    1  (ReservationState)");
        System.out.println("State concretes:     8  (Initiated~Refunded)");
        System.out.println("Total classes:      39  (26 original + 13 pattern)");
        System.out.println();
        System.out.println("BookingController ops: 14 (processSearch~reconfirmGuestIdentity)");
        System.out.println("PaymentProcessor  ops:  4 (+calculateTotal)");
        System.out.println("RefundHandler     ops:  6 (+getRefundDetail, +resolvePolicy)");
        System.out.println("AuthService       ops:  8 (+loginWithHash, +verifyGuest iter2)");
        System.out.println("FlightSearchService ops: 3");
        System.out.println("ReservationLookupService ops: 2 (iter2 신규)");
        System.out.println("PaymentGatewayInterface ops: 3 (+sendRefund)");
        System.out.println("SkypassInterface  ops:  4 (+deductMileage, +verifyAndDeduct)");
        System.out.println();
        System.out.println("Generalizations:   2  (SkypassMember->Passenger, Guest->Passenger)");
        System.out.println("Compositions:      4  (+Reservation*--ReservationState)");
        System.out.println("Associations:     11  (+RefundRequest--Refund)");
        System.out.println("Dependencies:     20  (+iter2 9개)");
        System.out.println("Realizations:     11  (3 Strategy + 8 State)");
        System.out.println("Total relations:  48");
    }
}
