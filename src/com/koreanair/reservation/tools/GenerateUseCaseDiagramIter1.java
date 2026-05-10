package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.usecasediagram.model.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;

/**
 * Generates the Korean Air Reservation System Use Case Diagram (.ucd) — Iteration 1 scope.
 *
 * <p>Iter1 활성 9개 UC 중, generator 모델에 단독 UC 로 존재하는 7개만 노출.
 * (Enter Passenger Info / Confirm Reservation 은 별도 UC 로 분리되어 있지 않으므로
 *  Book Flight 흐름 안에 흡수된 것으로 간주 — 4/28 발표 자료의 Iter1 UC 다이어그램 합의에 따름.)
 *
 * <p>Iter1 actor: Passenger / Skypass Member / Payment Gateway / Skypass System.
 * Admin / Guest / GDS 는 iter2+ 시점에 등장.
 *
 * <p>Run as: Java Application (right-click → Run As → Java Application)
 * Output: src/reservationSystem-iter1.ucd
 */
public class GenerateUseCaseDiagramIter1 {

    static RGB WHITE = new RGB(255, 255, 206);
    static RGB BLACK = new RGB(0, 0, 0);

    static UsecaseModel uc(String name, int x, int y, int w, int h, SystemModel parent) {
        UsecaseModel m = new UsecaseModel();
        m.setName(name);
        m.setConstraint(new Rectangle(x, y, w, h));
        m.setBackgroundColor(WHITE);
        m.setForegroundColor(BLACK);
        m.setShowIcon(true);
        parent.addChild(m);
        return m;
    }

    static UsecaseActorModel actor(String name, int x, int y, UsecaseRootModel root) {
        UsecaseActorModel a = new UsecaseActorModel();
        a.setName(name);
        a.setConstraint(new Rectangle(x, y, -1, -1));
        a.setBackgroundColor(WHITE);
        a.setForegroundColor(BLACK);
        a.setShowIcon(true);
        root.addChild(a);
        return a;
    }

    static void assoc(UsecaseActorModel actor, UsecaseModel uc) {
        UsecaseRelationModel r = new UsecaseRelationModel();
        r.setShowIcon(true);
        r.setSource(actor);
        r.setTarget(uc);
        r.attachSource();
        r.attachTarget();
    }

    static void include(UsecaseModel from, UsecaseModel to) {
        UsecaseIncludeModel r = new UsecaseIncludeModel();
        r.setShowIcon(true);
        r.setSource(from);
        r.setTarget(to);
        r.attachSource();
        r.attachTarget();
    }

    static void extend(UsecaseModel from, UsecaseModel to) {
        UsecaseExtendModel r = new UsecaseExtendModel();
        r.setShowIcon(true);
        r.setSource(from);
        r.setTarget(to);
        r.attachSource();
        r.attachTarget();
    }

    public static void main(String[] args) throws Exception {
        // === ROOT ===
        UsecaseRootModel root = new UsecaseRootModel();
        root.setBackgroundColor(WHITE);
        root.setForegroundColor(BLACK);
        root.setShowIcon(true);

        // === SYSTEM ===
        SystemModel system = new SystemModel();
        system.setName("Korean Air Reservation System (Iteration 1)");
        system.setConstraint(new Rectangle(220, 30, 800, 700));
        system.setBackgroundColor(WHITE);
        system.setForegroundColor(BLACK);
        system.setShowIcon(true);
        root.addChild(system);

        // === USE CASES (Iter1 활성 — 7개) ===
        // Left column
        UsecaseModel login            = uc("Login",           60,  50,  140, 40, system);
        UsecaseModel searchFlights    = uc("Search Flights",  60,  170, 170, 40, system);
        UsecaseModel bookFlight       = uc("Book Flight",     60,  290, 150, 40, system);
        UsecaseModel selectSeat       = uc("Select Seat",     60,  410, 140, 40, system);

        // Center column
        UsecaseModel viewBooking      = uc("View Booking",    380, 50,  170, 40, system);
        UsecaseModel makePayment      = uc("Make Payment",    380, 290, 170, 40, system);
        UsecaseModel applyMileage     = uc("Apply Mileage",   380, 410, 170, 40, system);

        // === ACTORS (Iter1 — 4명) ===
        UsecaseActorModel passenger   = actor("Passenger",        60,  550, root);
        UsecaseActorModel member      = actor("Skypass Member",   30,  650, root);
        UsecaseActorModel paymentGW   = actor("Payment Gateway",  1080, 350, root);
        UsecaseActorModel skypassSys  = actor("Skypass System",   1080, 500, root);

        // === ASSOCIATIONS (Actor → UC) ===
        assoc(passenger, searchFlights);
        assoc(passenger, bookFlight);
        assoc(passenger, viewBooking);

        assoc(member, login);
        assoc(member, applyMileage);

        assoc(paymentGW, makePayment);
        assoc(skypassSys, applyMileage);

        // === INCLUDE ===
        include(bookFlight, makePayment);
        include(applyMileage, login);

        // === EXTEND ===
        extend(selectSeat, bookFlight);
        extend(applyMileage, makePayment);

        // === SERIALIZE ===
        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);

        String outputPath = "src/reservationSystem-iter1.ucd";
        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write(xml);
        }

        System.out.println("Generated: " + outputPath);
        System.out.println("Use cases: 7 (Iter1 active scope), Actors: 4");
        System.out.println("Associations: 7, Includes: 2, Extends: 2");
        System.out.println("Total connections: 11");
    }
}
