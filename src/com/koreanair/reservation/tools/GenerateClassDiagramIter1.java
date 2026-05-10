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
 * for ITERATION 1 — Walking Skeleton scope only (State pattern골격).
 *
 * Iter1에 포함:
 *   - Entity: Passenger, SkypassMember, Guest, MileageAccount(stub),
 *             FlightSchedule, AircraftType, Seat, Airport,
 *             Reservation, Itinerary, Segment, Payment, FareRule
 *   - State 패턴: ReservationState interface + 8개 구체 상태 클래스
 *                (활성 메서드는 enterPassengerInfo / processPayment /
 *                 handlePaymentFailure 3개만 실제 코드 연결,
 *                 나머지 5개 메서드는 인터페이스 선언만)
 *   - Control: BookingController, PaymentProcessor,
 *              AuthService(평문), FlightSearchService
 *   - Boundary: ReservationUI, PaymentGatewayInterface(refund 메서드 제외),
 *               SkypassInterface(stub), GDSInterface(stub)
 *
 * Iter2에서 추가 예정 (이 다이어그램에서 제외):
 *   - Ticket, RefundRequest, Refund (entity)
 *   - RefundHandler, ReservationLookupService (control)
 *   - AuthService.verifyGuest / loginWithHash
 *   - BookingController.processCancellation / verifyGuestIdentity 등
 *   - RefundPolicy interface + Full/Partial/NoRefundPolicy (Strategy family)
 *   - PaymentGatewayInterface.sendRefund
 *   - SkypassInterface.deductMileage / verifyAndDeduct
 *
 * Uses AmaterasModeler's own model classes + XStream serialization.
 *
 * Output: src/classDiagram-iter1.cld
 *
 * Companion: {@link GenerateClassDiagramIter2} emits the iter2 full scope.
 *
 * NOTE: Runs outside Eclipse, so we must bootstrap UMLPlugin by
 *       reflectively invoking ReflectionFactory to bypass the
 *       AbstractUIPlugin constructor chain.
 */
public class GenerateClassDiagramIter1 {

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
    //  MAIN — Iter1 (Walking Skeleton)
    // ═════════════════════════════════════════════════════════════════
    public static void main(String[] args) throws Exception {

        // === ROOT ===
        RootModel root = new RootModel();
        root.setBackgroundColor(WHITE);
        root.setForegroundColor(BLACK);
        root.setShowIcon(true);

        // ─────────────────────────────────────────────────────────────
        //  ENTITY CLASSES  (light yellow)
        //  Height formula: 55 + (attrs + ops) × 18
        //  (좌표는 iter2 generator 와 정렬해 두어 발표 시 before/after 비교가
        //   직관적으로 보이도록 동일 위치를 유지)
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

        // MileageAccount — iter1 시점은 단순 balance 보유 stub
        // (deposit/withdraw 는 iter2 에서 추가)
        ClassModel mileageAccount = cls("MileageAccount", "entity", 30, 395, 220, 130,
                ENTITY_BG, root);                                      // 2a+2o=4 → 127→130
        mileageAccount.addChild(attr("accountId", "Long", Visibility.PRIVATE));
        mileageAccount.addChild(attr("balance", "BigDecimal", Visibility.PRIVATE));
        mileageAccount.addChild(op("getBalance", "BigDecimal", Visibility.PUBLIC));
        mileageAccount.addChild(op("updateBalance", "void", Visibility.PUBLIC,
                new String[][]{{"remainingMileage", "BigDecimal"}}));

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

        // FareRule — iter1 도 운임 검증에 직접 쓰임 (단, checkRefundPolicy 는 iter2)
        ClassModel fareRule = cls("FareRule", "entity", 540, 250, 240, 170,
                ENTITY_BG, root);                                      // 4a+2o=6 → 163→170
        fareRule.addChild(attr("fareClass", "String", Visibility.PRIVATE));
        fareRule.addChild(attr("isRefundable", "boolean", Visibility.PRIVATE));
        fareRule.addChild(attr("changeFee", "BigDecimal", Visibility.PRIVATE));
        fareRule.addChild(attr("cancellationPenalty", "BigDecimal", Visibility.PRIVATE));
        fareRule.addChild(op("isRefundable", "boolean", Visibility.PUBLIC));
        fareRule.addChild(op("getChangeFee", "BigDecimal", Visibility.PUBLIC));

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
        // iter1 의 Reservation 은 walking skeleton 만 활성:
        //  - enterPassengerInfo / processPayment / handlePaymentFailure 본문 활성
        //  - issueTicket / requestCancellation / confirmCancellation /
        //    requestRefund / processRefundDecision 는 메서드 자체가 아직 없음
        //  - addTicket / addPayment / findByPnr 는 iter2 신규
        ClassModel reservation = cls("Reservation", "entity", 290, 490, 230, 235,
                ENTITY_BG, root);                                      // 4a+6o=10 → 235→235
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
        reservation.addChild(op("enterPassengerInfo", "void", Visibility.PUBLIC,
                new String[][]{{"p", "Passenger"}}));
        reservation.addChild(op("processPayment", "void", Visibility.PUBLIC));

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

        // ── Payment (bottom row) ─────────────────────────────────
        // iter1 결제는 mock gateway 통해 PAID 마킹까지만
        ClassModel payment = cls("Payment", "entity", 290, 750, 220, 220,
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

        // ─────────────────────────────────────────────────────────────
        //  CONTROL CLASSES  (light blue) — iter1 walking skeleton
        // ─────────────────────────────────────────────────────────────

        // BookingController — iter1: 검색/예약개시/승객정보/결제확정/관리자&회원 인증/이력 조회
        // (processCancellation, verifyGuestIdentity, reconfirmGuestIdentity, assignSeat 본문은 iter2)
        ClassModel bookingController = cls("BookingController", "control", 1340, 30, 270, 235,
                CONTROL_BG, root);                                     // 10o → 235→235
        bookingController.addChild(op("processSearch", "List", Visibility.PUBLIC,
                new String[][]{{"searchCriteria", "Object"}}));
        bookingController.addChild(op("initiateBooking", "Reservation", Visibility.PUBLIC,
                new String[][]{{"flightId", "Long"}, {"fareClass", "String"}}));
        bookingController.addChild(op("setPassengerInfo", "void", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"passengerData", "Object"}}));
        bookingController.addChild(op("confirmInfo", "void", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}}));
        bookingController.addChild(op("createSchedule", "FlightSchedule", Visibility.PUBLIC,
                new String[][]{{"scheduleData", "Object"}}));
        bookingController.addChild(op("changeFlightStatus", "void", Visibility.PUBLIC,
                new String[][]{{"flightNo", "String"}, {"newStatus", "String"}}));
        bookingController.addChild(op("authenticateAdmin", "boolean", Visibility.PUBLIC,
                new String[][]{{"adminId", "String"}, {"password", "String"}}));
        bookingController.addChild(op("authenticateMember", "Object", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        bookingController.addChild(op("getBookingHistory", "Object", Visibility.PUBLIC,
                new String[][]{{"memberId", "Long"}}));
        bookingController.addChild(op("getTicketDetail", "Object", Visibility.PUBLIC,
                new String[][]{{"pnr", "String"}}));

        // PaymentProcessor — iter1: validateFareRule + processPaymentCharge
        // (calculateTotal/applyMileage 도 동일 시그니처는 iter1 시점에 이미 등장)
        ClassModel paymentProcessor = cls("PaymentProcessor", "control", 1340, 285, 270, 130,
                CONTROL_BG, root);                                     // 4o → 127→130
        paymentProcessor.addChild(op("processPayment", "boolean", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"paymentInfo", "Object"}}));
        paymentProcessor.addChild(op("validateFareRule", "boolean", Visibility.PUBLIC,
                new String[][]{{"fareClass", "String"}}));
        paymentProcessor.addChild(op("calculateTotal", "BigDecimal", Visibility.PUBLIC,
                new String[][]{{"fare", "BigDecimal"}, {"tax", "BigDecimal"}, {"seatSurcharge", "BigDecimal"}}));
        paymentProcessor.addChild(op("applyMileage", "boolean", Visibility.PUBLIC,
                new String[][]{{"reservationId", "Long"}, {"mileageAmount", "int"}}));

        // AuthService — iter1: 평문 비교 ver
        // (verifyGuest, loginWithHash 는 iter2 신규)
        ClassModel authService = cls("AuthService", "control", 1340, 435, 270, 165,
                CONTROL_BG, root);                                     // 6o → 163→165
        authService.addChild(op("registerMember", "Member", Visibility.PUBLIC,
                new String[][]{{"member", "Member"}, {"skypassNumber", "String"}, {"password", "String"}}));
        authService.addChild(op("login", "Member", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        authService.addChild(op("loginByName", "Member", Visibility.PUBLIC,
                new String[][]{{"name", "String"}, {"password", "String"}}));
        authService.addChild(op("logout", "void", Visibility.PUBLIC));
        authService.addChild(op("currentMember", "Member", Visibility.PUBLIC));
        authService.addChild(op("generateSkypassNumber", "String", Visibility.PUBLIC));

        // FlightSearchService — iter1 walking skeleton 핵심
        ClassModel flightSearchService = cls("FlightSearchService", "control", 1340, 620, 270, 115,
                CONTROL_BG, root);                                     // 3o → 109→115
        flightSearchService.addChild(op("addSchedule", "void", Visibility.PUBLIC,
                new String[][]{{"schedule", "FlightSchedule"}}));
        flightSearchService.addChild(op("search", "List", Visibility.PUBLIC,
                new String[][]{{"fromAirportCode", "String"}, {"toAirportCode", "String"}, {"date", "LocalDate"}}));
        flightSearchService.addChild(op("getCatalog", "List", Visibility.PUBLIC));

        // ─────────────────────────────────────────────────────────────
        //  BOUNDARY CLASSES  (light green)
        // ─────────────────────────────────────────────────────────────

        ClassModel reservationUI = cls("ReservationUI", "boundary", 1640, 30, 270, 115,
                BOUNDARY_BG, root);                                    // 3o → 109→115
        reservationUI.addChild(op("displaySearchResults", "void", Visibility.PUBLIC,
                new String[][]{{"flights", "List"}}));
        reservationUI.addChild(op("displaySeatMap", "void", Visibility.PUBLIC,
                new String[][]{{"seats", "List"}}));
        reservationUI.addChild(op("displayBookingConfirmation", "void", Visibility.PUBLIC,
                new String[][]{{"pnrNumber", "String"}}));

        // PaymentGatewayInterface — iter1 은 결제 승인까지만 (sendRefund 는 iter2)
        ClassModel paymentGateway = cls("PaymentGatewayInterface", "boundary", 1640, 165, 270, 95,
                BOUNDARY_BG, root);                                    // 2o → 91→95
        paymentGateway.addChild(op("sendAuthorizationRequest", "Object", Visibility.PUBLIC,
                new String[][]{{"amount", "BigDecimal"}, {"paymentInfo", "Object"}}));
        paymentGateway.addChild(op("receiveTransactionResult", "Object", Visibility.PUBLIC));

        // SkypassInterface — iter1 design stub (deductMileage/verifyAndDeduct 는 iter2)
        ClassModel skypassInterface = cls("SkypassInterface", "boundary", 1640, 280, 270, 95,
                BOUNDARY_BG, root);                                    // 2o → 91→95
        skypassInterface.addChild(op("verifyMembership", "Object", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"password", "String"}}));
        skypassInterface.addChild(op("getMileageBalance", "int", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}}));

        // GDSInterface — iter1 design stub
        ClassModel gdsInterface = cls("GDSInterface", "boundary", 1640, 395, 270, 100,
                BOUNDARY_BG, root);                                    // 2o → 91→100
        gdsInterface.addChild(op("searchInterlineFlights", "List", Visibility.PUBLIC,
                new String[][]{{"origin", "String"}, {"destination", "String"}}));
        gdsInterface.addChild(op("getPartnerAvailability", "List", Visibility.PUBLIC,
                new String[][]{{"flightNumber", "String"}}));

        // ─────────────────────────────────────────────────────────────
        //  STATE PATTERN — ReservationState (iter1 골격)
        //  인터페이스에는 8개 메서드 모두 선언 — but 구체 상태는
        //  enter/process/handle 3 개만 활성, 나머지 5 개는 stub.
        //  Reservation 아래 (x≈300, y≈990) 에 배치
        // ─────────────────────────────────────────────────────────────

        InterfaceModel reservationState = iface("ReservationState", "", 305, 990, 255, 200,
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

        // 8 concrete state classes — row 1 (y=1190)
        ClassModel initiatedState = cls("InitiatedState", "", 30, 1190, 230, 70,
                ENTITY_BG, root);
        ClassModel pendingPaymentState = cls("PendingPaymentState", "", 275, 1190, 255, 70,
                ENTITY_BG, root);
        ClassModel confirmedState = cls("ConfirmedState", "", 545, 1190, 230, 70,
                ENTITY_BG, root);
        ClassModel ticketedState = cls("TicketedState", "", 790, 1190, 220, 70,
                ENTITY_BG, root);

        // 8 concrete state classes — row 2 (y=1280)
        ClassModel cancellationRequestedState = cls("CancellationRequestedState", "", 30, 1280, 290, 70,
                ENTITY_BG, root);
        ClassModel cancelledState = cls("CancelledState", "", 335, 1280, 220, 70,
                ENTITY_BG, root);
        ClassModel refundRequestedState = cls("RefundRequestedState", "", 570, 1280, 255, 70,
                ENTITY_BG, root);
        ClassModel refundedState = cls("RefundedState", "", 840, 1280, 215, 70,
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
        association(flightSchedule, fareRule, "1", "*");              // FlightSchedule -- FareRule

        // ── Dependencies (control/boundary → entity) ────────────────
        dependency(bookingController, reservation);
        dependency(bookingController, passenger);
        dependency(bookingController, itinerary);
        dependency(bookingController, flightSearchService);
        dependency(bookingController, authService);
        dependency(paymentProcessor, payment);
        dependency(paymentProcessor, fareRule);
        dependency(flightSearchService, flightSchedule);

        dependency(reservationUI, bookingController);
        dependency(paymentGateway, paymentProcessor);
        dependency(skypassInterface, mileageAccount);
        dependency(gdsInterface, flightSchedule);

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

        String outputPath = "src/classDiagram-iter1.cld";
        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write(xml);
        }

        System.out.println("[Iter1] Generated: " + outputPath);
        System.out.println();
        System.out.println("=== Summary (Iter1 — Walking Skeleton) ===");
        System.out.println("Entity classes:       13  (Passenger, SkypassMember, Guest, MileageAccount,");
        System.out.println("                          FlightSchedule, FareRule, AircraftType, Seat, Airport,");
        System.out.println("                          Reservation, Itinerary, Segment, Payment)");
        System.out.println("Control classes:       4  (BookingController, PaymentProcessor, AuthService, FlightSearchService)");
        System.out.println("Boundary classes:      4  (ReservationUI, PaymentGatewayInterface, SkypassInterface, GDSInterface)");
        System.out.println("--- Design Patterns (Iter1) ---");
        System.out.println("State interfaces:      1  (ReservationState — 8 메서드 선언)");
        System.out.println("State concretes:       8  (Initiated/PendingPayment/Confirmed 본문 활성, 5개 stub)");
        System.out.println("Strategy:              0  (RefundPolicy family 는 iter2 에서 도입)");
        System.out.println("Total classes:        30  (21 도메인/ECB + 9 State)");
        System.out.println();
        System.out.println("Reservation         ops: 6 (enterPassengerInfo/processPayment 활성, 나머지 iter2)");
        System.out.println("BookingController   ops: 10 (cancellation/guest 식별 본문은 iter2)");
        System.out.println("AuthService         ops: 6 (verifyGuest/loginWithHash 는 iter2)");
        System.out.println("PaymentGatewayInterface ops: 2 (sendRefund 는 iter2)");
        System.out.println("SkypassInterface    ops: 2 (deductMileage/verifyAndDeduct 는 iter2)");
        System.out.println();
        System.out.println("Generalizations:       2  (SkypassMember->Passenger, Guest->Passenger)");
        System.out.println("Compositions:          4  (+Reservation*--ReservationState)");
        System.out.println("Associations:          7  (Refund/Ticket 관련 0)");
        System.out.println("Dependencies:         12  (ECB 횡단)");
        System.out.println("Realizations:          8  (8 State concretes → ReservationState)");
        System.out.println("Total relations:      33");
    }
}
