package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.activitydiagram.model.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;

/**
 * Iteration 3 State Diagrams.
 *
 * <p>Iter3는 Reservation 상태를 추가하지 않는다. 대신 다음 두 가지를 시각화한다:
 * <ul>
 *   <li>Seat 생애주기에 hold 만료 자동 해제 전이 강조</li>
 *   <li>FlightSchedule 생애주기는 Iter2와 동일하지만, "Observer publish" 부수효과를 라벨에 명시</li>
 * </ul>
 */
public class GenerateStateDiagramsIter3 {

    static RGB WHITE = new RGB(255, 255, 206);
    static RGB BLACK = new RGB(0, 0, 0);
    static RGB RED   = new RGB(220, 38, 38);

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

    static InitialStateModel initial(int x, int y, ActivityModel root) {
        InitialStateModel m = new InitialStateModel();
        m.setConstraint(new Rectangle(x, y, 20, 20));
        m.setBackgroundColor(WHITE); m.setForegroundColor(BLACK); m.setShowIcon(true);
        root.addChild(m);
        return m;
    }

    static FinalStateModel finalState(int x, int y, ActivityModel root) {
        FinalStateModel m = new FinalStateModel();
        m.setConstraint(new Rectangle(x, y, 20, 20));
        m.setBackgroundColor(WHITE); m.setForegroundColor(BLACK); m.setShowIcon(true);
        root.addChild(m);
        return m;
    }

    static void flow(Object from, Object to, String label) {
        FlowModel f = new FlowModel();
        f.setShowIcon(true);
        if (label != null && !label.isEmpty()) f.setCondition(label);
        f.setSource((net.java.amateras.uml.model.AbstractUMLEntityModel) from);
        f.setTarget((net.java.amateras.uml.model.AbstractUMLEntityModel) to);
        f.attachSource(); f.attachTarget();
    }

    static String serialize(ActivityModel root) {
        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);
    }

    static ActivityModel createRoot() {
        ActivityModel root = new ActivityModel();
        root.setBackgroundColor(WHITE); root.setForegroundColor(BLACK); root.setShowIcon(true);
        return root;
    }

    static void generateSeat() throws Exception {
        ActivityModel root = createRoot();

        InitialStateModel start = initial(300, 10, root);
        ActionModel available = state("Available", 250, 70, 150, 40, root);
        ActionModel held = state("Held (holdExpiresAt)", 220, 200, 210, 40, root);
        ActionModel booked = state("Booked", 250, 330, 150, 40, root);
        ActionModel occupied = state("Occupied", 250, 460, 150, 40, root);
        FinalStateModel end = finalState(310, 540, root);

        flow(start, available, "seat initialized");
        flow(available, held, "passenger selects seat (hold 15min)");
        flow(held, available, "[iter3] SeatHoldMonitor.sweep → SeatHoldExpiredEvent → release()");
        flow(held, booked, "payment approved");
        flow(booked, available, "booking cancelled");
        flow(booked, occupied, "passenger boarded");
        flow(occupied, end, "");

        try (FileWriter fw = new FileWriter("src/seatState-iter3.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/seatState-iter3.acd");
    }

    static void generateFlightSchedule() throws Exception {
        ActivityModel root = createRoot();

        InitialStateModel start = initial(250, 10, root);
        ActionModel scheduled = state("Scheduled", 180, 70, 160, 40, root);
        ActionModel delayed = state("Delayed (publish event)", 480, 70, 250, 40, root);
        ActionModel boardingOpen = state("BoardingOpen", 180, 230, 170, 40, root);
        ActionModel departed = state("Departed", 180, 360, 150, 40, root);
        ActionModel arrived = state("Arrived", 180, 490, 140, 40, root);
        ActionModel cancelled = state("Cancelled (publish event)", 480, 230, 270, 40, root);
        FinalStateModel end1 = finalState(230, 570, root);
        FinalStateModel end2 = finalState(580, 310, root);

        flow(start, scheduled, "flight schedule created");
        flow(scheduled, delayed, "[iter3] changeStatus(DELAYED) publish FlightStatusChangedEvent");
        flow(scheduled, boardingOpen, "boarding time reached");
        flow(scheduled, cancelled, "[iter3] changeStatus(CANCELLED) publish + AffectedReservationListener 호출");
        flow(boardingOpen, departed, "aircraft departed");
        flow(departed, arrived, "aircraft arrived");
        flow(arrived, end1, "");
        flow(cancelled, end2, "");

        try (FileWriter fw = new FileWriter("src/flightScheduleState-iter3.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/flightScheduleState-iter3.acd");
    }

    public static void main(String[] args) throws Exception {
        generateSeat();
        generateFlightSchedule();
        System.out.println("All iter3 state diagrams generated!");
    }
}
