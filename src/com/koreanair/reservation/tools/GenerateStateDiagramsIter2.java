package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.activitydiagram.model.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;

/**
 * Generates State Diagrams as Activity Diagrams (.acd) for:
 * 1. Reservation - booking lifecycle
 * 2. FlightSchedule - flight operations lifecycle
 * 3. Seat - seat availability lifecycle
 */
public class GenerateStateDiagramsIter2 {

    static RGB WHITE = new RGB(255, 255, 206);
    static RGB BLACK = new RGB(0, 0, 0);

    static ActionModel state(String name, int x, int y, int w, int h, ActivityModel root) {
        ActionModel a = new ActionModel();
        a.setActionName(name);
        a.setConstraint(new Rectangle(x, y, w, h));
        a.setBackgroundColor(WHITE);
        a.setForegroundColor(BLACK);
        a.setShowIcon(true);
        root.addChild(a);
        return a;
    }

    /** Invisible routing waypoint — blends into canvas background */
    static ActionModel waypoint(int x, int y, ActivityModel root) {
        ActionModel w = new ActionModel();
        w.setActionName("");
        w.setConstraint(new Rectangle(x, y, 5, 5));
        w.setBackgroundColor(new RGB(255, 255, 255));
        w.setForegroundColor(new RGB(255, 255, 255));
        w.setShowIcon(true);
        root.addChild(w);
        return w;
    }

    static InitialStateModel initial(int x, int y, ActivityModel root) {
        InitialStateModel m = new InitialStateModel();
        m.setConstraint(new Rectangle(x, y, 20, 20));
        m.setBackgroundColor(WHITE);
        m.setForegroundColor(BLACK);
        m.setShowIcon(true);
        root.addChild(m);
        return m;
    }

    static FinalStateModel finalState(int x, int y, ActivityModel root) {
        FinalStateModel m = new FinalStateModel();
        m.setConstraint(new Rectangle(x, y, 20, 20));
        m.setBackgroundColor(WHITE);
        m.setForegroundColor(BLACK);
        m.setShowIcon(true);
        root.addChild(m);
        return m;
    }

    static void flow(Object from, Object to, String label) {
        FlowModel f = new FlowModel();
        f.setShowIcon(true);
        if (label != null && !label.isEmpty()) {
            f.setCondition(label);
        }
        f.setSource((net.java.amateras.uml.model.AbstractUMLEntityModel) from);
        f.setTarget((net.java.amateras.uml.model.AbstractUMLEntityModel) to);
        f.attachSource();
        f.attachTarget();
    }

    static String serialize(ActivityModel root) {
        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);
    }

    static ActivityModel createRoot() {
        ActivityModel root = new ActivityModel();
        root.setBackgroundColor(WHITE);
        root.setForegroundColor(BLACK);
        root.setShowIcon(true);
        return root;
    }

    static void generateReservation() throws Exception {
        ActivityModel root = createRoot();

        InitialStateModel start = initial(300, 10, root);

        // Spread states horizontally to prevent label overlap on bidirectional flows
        ActionModel initiated = state("Initiated", 250, 70, 150, 40, root);
        ActionModel pendingPayment = state("PendingPayment", 200, 170, 180, 40, root);
        ActionModel confirmed = state("Confirmed", 350, 290, 150, 40, root);
        ActionModel ticketed = state("Ticketed", 620, 190, 140, 40, root);
        ActionModel cancReq = state("CancellationRequested", 580, 400, 220, 40, root);
        ActionModel cancelled = state("Cancelled", 200, 530, 150, 40, root);
        ActionModel refundReq = state("RefundRequested", 520, 650, 180, 40, root);
        ActionModel refunded = state("Refunded", 520, 770, 150, 40, root);

        // Waypoint routes "refund denied" backward flow to avoid label overlap
        ActionModel wpRefundDenied = waypoint(210, 670, root);

        FinalStateModel end1 = finalState(520, 540, root);   // non-refundable
        FinalStateModel end2 = finalState(570, 840, root);

        flow(start, initiated, "itinerary selected");
        flow(initiated, pendingPayment, "passenger info entered");
        flow(pendingPayment, confirmed, "payment approved");
        flow(pendingPayment, cancelled, "payment failed / timeout");
        flow(confirmed, ticketed, "e-ticket issued");
        flow(confirmed, cancReq, "cancellation requested");
        flow(ticketed, cancReq, "cancellation requested");
        flow(cancReq, cancelled, "fare rule verified");
        flow(cancelled, refundReq, "refund requested");      // diagonal right-down
        flow(cancelled, end1, "non-refundable fare");
        flow(refundReq, refunded, "refund approved");
        flow(refundReq, wpRefundDenied, "refund denied");     // goes left to waypoint
        flow(wpRefundDenied, cancelled, "");                   // then up to Cancelled
        flow(refunded, end2, "");

        try (FileWriter fw = new FileWriter("src/reservationState-iter2.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/reservationState-iter2.acd");
    }

    static void generateFlightSchedule() throws Exception {
        ActivityModel root = createRoot();

        InitialStateModel start = initial(250, 10, root);

        ActionModel scheduled = state("Scheduled", 180, 70, 160, 40, root);
        ActionModel delayed = state("Delayed", 500, 70, 140, 40, root);
        ActionModel boardingOpen = state("BoardingOpen", 180, 230, 170, 40, root);
        ActionModel departed = state("Departed", 180, 360, 150, 40, root);
        ActionModel arrived = state("Arrived", 180, 490, 140, 40, root);
        ActionModel cancelled = state("Cancelled", 500, 230, 150, 40, root);

        // Waypoint routes "delay resolved" backward flow below the "delay reported" line
        ActionModel wpDelayResolved = waypoint(350, 150, root);

        FinalStateModel end1 = finalState(230, 570, root);
        FinalStateModel end2 = finalState(550, 310, root);

        flow(start, scheduled, "flight schedule created");
        flow(scheduled, delayed, "delay reported");            // horizontal right
        flow(delayed, wpDelayResolved, "delay resolved");      // diagonal down-left to waypoint
        flow(wpDelayResolved, scheduled, "");                   // diagonal up-left to Scheduled
        flow(scheduled, boardingOpen, "boarding time reached");
        flow(delayed, boardingOpen, "delay resolved, boarding starts");
        flow(boardingOpen, departed, "aircraft departed");
        flow(departed, arrived, "aircraft arrived");
        flow(scheduled, cancelled, "flight cancelled");
        flow(delayed, cancelled, "cancelled after delay");
        flow(arrived, end1, "");
        flow(cancelled, end2, "");

        try (FileWriter fw = new FileWriter("src/flightScheduleState-iter2.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/flightScheduleState-iter2.acd");
    }

    static void generateSeat() throws Exception {
        ActivityModel root = createRoot();

        InitialStateModel start = initial(300, 10, root);

        ActionModel available = state("Available", 250, 70, 150, 40, root);
        ActionModel held = state("Held", 250, 200, 140, 40, root);
        ActionModel booked = state("Booked", 250, 330, 150, 40, root);
        ActionModel checkedIn = state("CheckedIn", 250, 460, 150, 40, root);
        ActionModel occupied = state("Occupied", 250, 590, 150, 40, root);

        // Waypoints route backward flows to the LEFT, avoiding overlap with forward flows
        ActionModel wpHeldBack = waypoint(80, 140, root);     // Held → Available return
        ActionModel wpBookedBack = waypoint(80, 200, root);   // Booked → Available return

        FinalStateModel end = finalState(300, 670, root);

        flow(start, available, "seat initialized");
        flow(available, held, "passenger selects seat");       // straight down
        flow(held, wpHeldBack, "hold timeout / cancelled");    // goes left to waypoint
        flow(wpHeldBack, available, "");                        // then up-right to Available
        flow(held, booked, "payment approved");                // straight down
        flow(booked, wpBookedBack, "booking cancelled");       // goes left to waypoint
        flow(wpBookedBack, available, "");                      // then up-right to Available
        flow(booked, checkedIn, "check-in completed");
        flow(checkedIn, occupied, "passenger boarded");
        flow(occupied, end, "");

        try (FileWriter fw = new FileWriter("src/seatState-iter2.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/seatState-iter2.acd");
    }

    public static void main(String[] args) throws Exception {
        generateReservation();
        generateFlightSchedule();
        generateSeat();
        System.out.println("All 3 state diagrams generated!");
    }
}
