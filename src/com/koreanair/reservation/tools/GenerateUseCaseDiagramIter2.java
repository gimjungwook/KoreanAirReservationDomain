package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.usecasediagram.model.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;

/**
 * Generates the Korean Air Reservation System Use Case Diagram (.ucd)
 * using AmaterasModeler's own model classes + XStream serialization.
 *
 * Run as: Java Application (right-click → Run As → Java Application)
 * Output: src/reservationSystem.ucd
 */
public class GenerateUseCaseDiagramIter2 {

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
        system.setName("Korean Air Reservation System");
        system.setConstraint(new Rectangle(220, 30, 1000, 900));
        system.setBackgroundColor(WHITE);
        system.setForegroundColor(BLACK);
        system.setShowIcon(true);
        root.addChild(system);

        // === USE CASES ===
        // Left column
        UsecaseModel login            = uc("Login",                     60,  50,  140, 40, system);
        UsecaseModel searchFlights    = uc("Search Flights",            60,  160, 170, 40, system);
        UsecaseModel bookFlight       = uc("Book Flight",               60,  280, 150, 40, system);
        UsecaseModel bookMultiSeg     = uc("Book Multi-segment Trip",   60,  400, 240, 40, system);
        UsecaseModel selectSeat       = uc("Select Seat",               60,  520, 140, 40, system);

        // Center column
        UsecaseModel makePayment      = uc("Make Payment",              380, 280, 170, 40, system);
        UsecaseModel applyMileage     = uc("Apply Mileage",             380, 400, 170, 40, system);
        UsecaseModel viewBooking      = uc("View Booking",              380, 50,  170, 40, system);
        UsecaseModel cancelBooking    = uc("Cancel Booking",            380, 160, 170, 40, system);
        UsecaseModel viewETicket      = uc("View e-Ticket",             380, 520, 150, 40, system);
        UsecaseModel refundDenied     = uc("Refund Denied",             380, 640, 170, 40, system);
        UsecaseModel retrievePNR      = uc("Retrieve Booking by PNR",   380, 760, 240, 40, system);
        UsecaseModel issueTicket      = uc("Issue e-Ticket",            60,  640, 170, 40, system);

        // Right column
        UsecaseModel reviewRefund     = uc("Review Refund Request",     700, 160, 240, 40, system);
        UsecaseModel manageFlight     = uc("Manage Flight Schedule",    700, 50,  250, 40, system);
        UsecaseModel updateStatus     = uc("Update Flight Status",      700, 280, 220, 40, system);
        UsecaseModel searchInterline  = uc("Search Interline Flights",  700, 400, 250, 40, system);

        // === ACTORS ===
        UsecaseActorModel passenger   = actor("Passenger",        60,  350, root);
        UsecaseActorModel member      = actor("Skypass Member",   30,  600, root);
        UsecaseActorModel guest       = actor("Guest",            60,  850, root);
        UsecaseActorModel admin       = actor("Admin",            1350, 200, root);
        UsecaseActorModel paymentGW   = actor("Payment Gateway",  1320, 500, root);
        UsecaseActorModel skypassSys  = actor("Skypass System",   1320, 700, root);
        UsecaseActorModel gds         = actor("GDS",              1350, 900, root);

        // === ASSOCIATIONS (Actor → UC) ===
        assoc(passenger, searchFlights);
        assoc(passenger, bookFlight);
        assoc(passenger, viewBooking);
        assoc(passenger, cancelBooking);
        assoc(passenger, viewETicket);
        assoc(passenger, bookMultiSeg);

        assoc(guest, retrievePNR);

        assoc(member, login);
        assoc(member, applyMileage);

        assoc(admin, login);
        assoc(admin, manageFlight);
        assoc(admin, updateStatus);
        assoc(admin, reviewRefund);
        assoc(admin, refundDenied);

        assoc(paymentGW, makePayment);
        assoc(skypassSys, applyMileage);
        assoc(gds, searchInterline);

        // === INCLUDE ===
        include(bookFlight, makePayment);
        include(bookFlight, issueTicket);   // iter2: Make Payment 성공 후 e-Ticket 발권
        include(applyMileage, login);
        include(bookMultiSeg, bookFlight);
        include(retrievePNR, viewBooking);
        include(retrievePNR, cancelBooking);

        // === EXTEND ===
        extend(selectSeat, bookFlight);
        extend(applyMileage, makePayment);
        extend(refundDenied, cancelBooking);
        extend(searchInterline, searchFlights);

        // === SERIALIZE ===
        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);

        String outputPath = "src/reservationSystem-iter2.ucd";
        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write(xml);
        }

        System.out.println("Generated: " + outputPath);
        System.out.println("Use cases: 17 (+Issue e-Ticket iter2), Actors: 7");
        System.out.println("Associations: 17, Includes: 6 (+bookFlight->issueTicket), Extends: 4");
        System.out.println("Total connections: 27");
    }
}
