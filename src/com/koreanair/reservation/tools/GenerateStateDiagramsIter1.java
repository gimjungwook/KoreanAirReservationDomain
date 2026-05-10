package com.koreanair.reservation.tools;

import com.thoughtworks.xstream.XStream;
import net.java.amateras.uml.activitydiagram.model.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

import java.io.FileWriter;

/**
 * Generates State Diagrams as Activity Diagrams (.acd) for Iteration 1 scope:
 * 1. Reservation — booking lifecycle (8 노드 모두 표시, 단 활성 전이는 4개만)
 * 2. FlightSchedule — 변화 없음 (iter1·iter2 동일)
 * 3. Seat — 변화 없음 (iter1·iter2 동일)
 *
 * <p>Reservation 다이어그램은 4/28 발표 자료의 "Walking Skeleton" 합의에 따라
 *   start→Initiated→PendingPayment→Confirmed / PendingPayment→Cancelled 4개 전이만
 *   실제 구현·전이 노출. 나머지 5개 전이(Confirmed→Ticketed, *→CancellationRequested,
 *   CancellationRequested→Cancelled, Cancelled→RefundRequested/end1, RefundRequested→*)는
 *   iter2 활성 예정이므로 본 generator 에서 그리지 않음.
 *   단 노드(상태) 자체는 8개 모두 박스로 표시 — Class Diagram 의 스텁 처리와 동일한 의도.
 */
public class GenerateStateDiagramsIter1 {

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

        // 8 노드 모두 표시 (iter2 활성 예정 노드 포함, 단 전이는 iter1 활성분만)
        ActionModel initiated      = state("Initiated",             250, 70,  150, 40, root);
        ActionModel pendingPayment = state("PendingPayment",        200, 170, 180, 40, root);
        ActionModel confirmed      = state("Confirmed",             350, 290, 150, 40, root);
        ActionModel ticketed       = state("Ticketed (iter2)",      620, 190, 180, 40, root);
        ActionModel cancReq        = state("CancellationRequested (iter2)", 580, 400, 280, 40, root);
        ActionModel cancelled      = state("Cancelled",             200, 530, 150, 40, root);
        ActionModel refundReq      = state("RefundRequested (iter2)", 520, 650, 230, 40, root);
        ActionModel refunded       = state("Refunded (iter2)",      520, 770, 200, 40, root);

        // (iter1 단계에선 final state 도 활성 분기만 그려둠 — refund/non-refundable 종단은 iter2)
        // 활성 전이 4개:
        //   start → Initiated
        //   Initiated → PendingPayment   (passenger info entered)
        //   PendingPayment → Confirmed   (payment approved)
        //   PendingPayment → Cancelled   (payment failed / timeout)
        flow(start,          initiated,      "itinerary selected");
        flow(initiated,      pendingPayment, "passenger info entered");
        flow(pendingPayment, confirmed,      "payment approved");
        flow(pendingPayment, cancelled,      "payment failed / timeout");

        // ticketed / cancReq / refundReq / refunded → 노드만 두고 전이 미연결 (iter2 활성 예정)
        // unused warning 회피를 위해 명시적으로 참조만 유지
        if (ticketed == null || cancReq == null || refundReq == null || refunded == null) {
            throw new IllegalStateException("iter2 stub nodes must be present");
        }

        try (FileWriter fw = new FileWriter("src/reservationState-iter1.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/reservationState-iter1.acd");
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
        flow(scheduled, delayed, "delay reported");
        flow(delayed, wpDelayResolved, "delay resolved");
        flow(wpDelayResolved, scheduled, "");
        flow(scheduled, boardingOpen, "boarding time reached");
        flow(delayed, boardingOpen, "delay resolved, boarding starts");
        flow(boardingOpen, departed, "aircraft departed");
        flow(departed, arrived, "aircraft arrived");
        flow(scheduled, cancelled, "flight cancelled");
        flow(delayed, cancelled, "cancelled after delay");
        flow(arrived, end1, "");
        flow(cancelled, end2, "");

        try (FileWriter fw = new FileWriter("src/flightScheduleState-iter1.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/flightScheduleState-iter1.acd");
    }

    static void generateSeat() throws Exception {
        ActivityModel root = createRoot();

        InitialStateModel start = initial(300, 10, root);

        ActionModel available = state("Available", 250, 70, 150, 40, root);
        ActionModel held = state("Held", 250, 200, 140, 40, root);
        ActionModel booked = state("Booked", 250, 330, 150, 40, root);
        ActionModel checkedIn = state("CheckedIn", 250, 460, 150, 40, root);
        ActionModel occupied = state("Occupied", 250, 590, 150, 40, root);

        ActionModel wpHeldBack = waypoint(80, 140, root);
        ActionModel wpBookedBack = waypoint(80, 200, root);

        FinalStateModel end = finalState(300, 670, root);

        flow(start, available, "seat initialized");
        flow(available, held, "passenger selects seat");
        flow(held, wpHeldBack, "hold timeout / cancelled");
        flow(wpHeldBack, available, "");
        flow(held, booked, "payment approved");
        flow(booked, wpBookedBack, "booking cancelled");
        flow(wpBookedBack, available, "");
        flow(booked, checkedIn, "check-in completed");
        flow(checkedIn, occupied, "passenger boarded");
        flow(occupied, end, "");

        try (FileWriter fw = new FileWriter("src/seatState-iter1.acd")) {
            fw.write(serialize(root));
        }
        System.out.println("Generated: src/seatState-iter1.acd");
    }

    public static void main(String[] args) throws Exception {
        generateReservation();
        generateFlightSchedule();
        generateSeat();
        System.out.println("All 3 state diagrams (iter1) generated!");
    }
}
