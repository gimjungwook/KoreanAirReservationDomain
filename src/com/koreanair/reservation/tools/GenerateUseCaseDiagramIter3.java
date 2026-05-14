package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.usecasediagram.model.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;

/**
 * Iteration 3 Use Case Diagram generator.
 *
 * <p>Iter2 대비 신규 use case:
 * <ul>
 *   <li>Search Connecting Flights — 환승 일정 검색 (Itinerary CONNECTING)</li>
 *   <li>Book Multi-city Trip — multi-city itinerary</li>
 *   <li>Pay with Mileage — 마일리지 결제</li>
 *   <li>Notify Flight Schedule Change — Observer 전파</li>
 *   <li>Auto-cancel on Hold Expiry — 좌석 hold 만료 자동 취소</li>
 * </ul>
 *
 * Output: src/reservationSystem-iter3.ucd
 */
public class GenerateUseCaseDiagramIter3 {

    static RGB WHITE = new RGB(255, 255, 206);
    static RGB BLACK = new RGB(0, 0, 0);
    static RGB RED   = new RGB(220, 38, 38);

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

    static UsecaseModel ucNew(String name, int x, int y, int w, int h, SystemModel parent) {
        UsecaseModel m = uc(name, x, y, w, h, parent);
        m.setForegroundColor(RED);
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
        UsecaseRootModel root = new UsecaseRootModel();
        root.setBackgroundColor(WHITE);
        root.setForegroundColor(BLACK);
        root.setShowIcon(true);

        SystemModel system = new SystemModel();
        system.setName("Korean Air Reservation System — Iter 3");
        system.setConstraint(new Rectangle(220, 30, 1100, 1000));
        system.setBackgroundColor(WHITE);
        system.setForegroundColor(BLACK);
        system.setShowIcon(true);
        root.addChild(system);

        // Existing (iter 1~2)
        UsecaseModel login            = uc("Login",                     60,  50,  140, 40, system);
        UsecaseModel searchFlights    = uc("Search Flights",            60,  160, 170, 40, system);
        UsecaseModel bookFlight       = uc("Book Flight",               60,  280, 150, 40, system);
        UsecaseModel selectSeat       = uc("Select Seat",               60,  400, 140, 40, system);
        UsecaseModel makePayment      = uc("Make Payment",              380, 280, 170, 40, system);
        UsecaseModel issueTicket      = uc("Issue e-Ticket",            60,  520, 170, 40, system);
        UsecaseModel viewBooking      = uc("View Booking",              380, 50,  170, 40, system);
        UsecaseModel cancelBooking    = uc("Cancel Booking",            380, 160, 170, 40, system);
        UsecaseModel viewETicket      = uc("View e-Ticket",             380, 400, 150, 40, system);
        UsecaseModel retrievePNR      = uc("Retrieve Booking by PNR",   380, 520, 240, 40, system);
        UsecaseModel reviewRefund     = uc("Review Refund Request",     700, 160, 240, 40, system);
        UsecaseModel manageFlight     = uc("Manage Flight Schedule",    700, 50,  250, 40, system);

        // Iter 3 new (red)
        UsecaseModel searchConnect    = ucNew("Search Connecting Flights",  60,  640, 250, 40, system);
        UsecaseModel bookMulti        = ucNew("Book Multi-city Trip",       60,  760, 240, 40, system);
        UsecaseModel payMileage       = ucNew("Pay with Mileage",           380, 280 + 360, 200, 40, system);
        UsecaseModel notifyChange     = ucNew("Notify Flight Schedule Change", 700, 280, 280, 40, system);
        UsecaseModel autoCancel       = ucNew("Auto-cancel on Hold Expiry", 700, 400, 280, 40, system);

        // Actors
        UsecaseActorModel passenger   = actor("Passenger",        60,  350, root);
        UsecaseActorModel member      = actor("Skypass Member",   30,  600, root);
        UsecaseActorModel guest       = actor("Guest",            60,  900, root);
        UsecaseActorModel admin       = actor("Admin",            1400, 200, root);
        UsecaseActorModel paymentGW   = actor("Payment Gateway",  1380, 500, root);
        UsecaseActorModel skypassSys  = actor("Skypass System",   1380, 700, root);
        UsecaseActorModel gds         = actor("GDS",              1400, 900, root);

        // Associations
        assoc(passenger, searchFlights);
        assoc(passenger, bookFlight);
        assoc(passenger, viewBooking);
        assoc(passenger, cancelBooking);
        assoc(passenger, viewETicket);
        assoc(passenger, searchConnect);
        assoc(passenger, bookMulti);
        assoc(member, login);
        assoc(member, payMileage);
        assoc(guest, retrievePNR);
        assoc(admin, login);
        assoc(admin, manageFlight);
        assoc(admin, reviewRefund);
        assoc(paymentGW, makePayment);
        assoc(skypassSys, payMileage);
        assoc(gds, searchConnect);

        // Include
        include(bookFlight, makePayment);
        include(bookFlight, issueTicket);
        include(bookMulti, bookFlight);
        include(searchConnect, searchFlights);
        include(retrievePNR, viewBooking);
        include(retrievePNR, cancelBooking);

        // Extend
        extend(selectSeat, bookFlight);
        extend(payMileage, makePayment);
        extend(notifyChange, manageFlight);
        extend(autoCancel, makePayment);

        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);

        String outputPath = "src/reservationSystem-iter3.ucd";
        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write(xml);
        }

        System.out.println("Generated: " + outputPath);
        System.out.println("Iter3 new UCs: searchConnecting, bookMulti, payMileage, notifyChange, autoCancel");
    }
}
