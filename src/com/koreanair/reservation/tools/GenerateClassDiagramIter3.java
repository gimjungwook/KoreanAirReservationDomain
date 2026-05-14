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
 * Iteration 3 Class Diagram — Observer 패턴 family + Iter3 신규 클래스만.
 *
 * <p>Iter2 도메인 전체를 다시 그리지 않고, 신규 추가된 Observer infrastructure 와
 * Connecting Itinerary / Mileage Payment / SeatHoldMonitor 만 포커스.
 *
 * Output: src/classDiagram-iter3.cld
 */
public class GenerateClassDiagramIter3 {

    static {
        try {
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

            ResourceBundle rb;
            try {
                rb = ResourceBundle.getBundle("net.java.amateras.uml.UMLPlugin");
            } catch (MissingResourceException e) {
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

            Field pluginField = UMLPlugin.class.getDeclaredField("plugin");
            pluginField.setAccessible(true);
            pluginField.set(null, fakePlugin);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to bootstrap UMLPlugin", t);
        }
    }

    static RGB WHITE      = new RGB(255, 255, 255);
    static RGB BLACK      = new RGB(0, 0, 0);
    static RGB EVENT_BG   = new RGB(255, 230, 230);
    static RGB CONTROL_BG = new RGB(206, 230, 255);
    static RGB ENTITY_BG  = new RGB(255, 255, 206);
    static RGB BOUNDARY_BG = new RGB(220, 255, 220);

    static AttributeModel attr(String name, String type, Visibility vis) {
        AttributeModel a = new AttributeModel();
        a.setName(name); a.setType(type); a.setVisibility(vis);
        return a;
    }

    static OperationModel op(String name, String returnType, Visibility vis) {
        OperationModel o = new OperationModel();
        o.setName(name); o.setType(returnType); o.setVisibility(vis);
        o.setParams(new ArrayList<>());
        return o;
    }

    static OperationModel op(String name, String returnType, Visibility vis, String[][] params) {
        OperationModel o = new OperationModel();
        o.setName(name); o.setType(returnType); o.setVisibility(vis);
        List<Argument> args = new ArrayList<>();
        for (String[] p : params) {
            Argument arg = new Argument();
            arg.setName(p[0]); arg.setType(p[1]);
            args.add(arg);
        }
        o.setParams(args);
        return o;
    }

    static ClassModel cls(String name, String stereo, int x, int y, int w, int h,
                          RGB bg, RootModel root) {
        ClassModel c = new ClassModel();
        c.setName(name);
        if (stereo != null && !stereo.isEmpty()) c.setStereoType(stereo);
        c.setConstraint(new Rectangle(x, y, w, h));
        c.setBackgroundColor(bg); c.setForegroundColor(BLACK); c.setShowIcon(true);
        root.addChild(c);
        return c;
    }

    static InterfaceModel iface(String name, String stereo, int x, int y, int w, int h,
                                RGB bg, RootModel root) {
        InterfaceModel i = new InterfaceModel();
        i.setName(name);
        if (stereo != null && !stereo.isEmpty()) i.setStereoType(stereo);
        i.setConstraint(new Rectangle(x, y, w, h));
        i.setBackgroundColor(bg); i.setForegroundColor(BLACK); i.setShowIcon(true);
        root.addChild(i);
        return i;
    }

    static void generalization(ClassModel child, ClassModel parent) {
        GeneralizationModel g = new GeneralizationModel();
        g.setShowIcon(true);
        g.setSource(child); g.setTarget(parent);
        g.attachSource(); g.attachTarget();
    }

    static void realization(CommonEntityModel impl, InterfaceModel ifc) {
        RealizationModel r = new RealizationModel();
        r.setShowIcon(true);
        r.setSource(impl); r.setTarget(ifc);
        r.attachSource(); r.attachTarget();
    }

    static void dependency(CommonEntityModel from, CommonEntityModel to) {
        DependencyModel d = new DependencyModel();
        d.setShowIcon(true);
        d.setSource(from); d.setTarget(to);
        d.attachSource(); d.attachTarget();
    }

    static void association(CommonEntityModel from, CommonEntityModel to, String fromM, String toM) {
        AssociationModel a = new AssociationModel();
        a.setShowIcon(true);
        a.setFromMultiplicity(fromM); a.setToMultiplicity(toM);
        a.setSource(from); a.setTarget(to);
        a.attachSource(); a.attachTarget();
    }

    public static void main(String[] args) throws Exception {
        RootModel root = new RootModel();
        root.setBackgroundColor(WHITE); root.setForegroundColor(BLACK); root.setShowIcon(true);

        // ── Observer infrastructure ──
        ClassModel eventBase = cls("DomainEvent", "abstract", 40, 30, 230, 130, EVENT_BG, root);
        eventBase.addChild(attr("occurredAt", "LocalDateTime", Visibility.PRIVATE));
        eventBase.addChild(attr("sourceId", "String", Visibility.PRIVATE));
        eventBase.addChild(op("getEventType", "String", Visibility.PUBLIC));

        InterfaceModel listener = iface("EventListener", "interface", 320, 30, 230, 80, EVENT_BG, root);
        listener.addChild(op("onEvent", "void", Visibility.PUBLIC, new String[][]{{"event", "DomainEvent"}}));

        ClassModel publisher = cls("EventPublisher", "abstract", 600, 30, 250, 160, EVENT_BG, root);
        publisher.addChild(attr("listeners", "List<EventListener>", Visibility.PRIVATE));
        publisher.addChild(op("subscribe", "void", Visibility.PUBLIC, new String[][]{{"listener", "EventListener"}}));
        publisher.addChild(op("unsubscribe", "void", Visibility.PUBLIC, new String[][]{{"listener", "EventListener"}}));
        publisher.addChild(op("publish", "void", Visibility.PROTECTED, new String[][]{{"event", "DomainEvent"}}));

        // ── Concrete Events ──
        ClassModel seatExpEvt = cls("SeatHoldExpiredEvent", "event", 40, 200, 230, 100, EVENT_BG, root);
        seatExpEvt.addChild(attr("seat", "Seat", Visibility.PRIVATE));
        seatExpEvt.addChild(attr("reservationPnr", "String", Visibility.PRIVATE));

        ClassModel payFailEvt = cls("PaymentFailedEvent", "event", 290, 200, 230, 120, EVENT_BG, root);
        payFailEvt.addChild(attr("payment", "Payment", Visibility.PRIVATE));
        payFailEvt.addChild(attr("reservationPnr", "String", Visibility.PRIVATE));
        payFailEvt.addChild(attr("reason", "String", Visibility.PRIVATE));

        ClassModel flightChgEvt = cls("FlightStatusChangedEvent", "event", 540, 200, 270, 120, EVENT_BG, root);
        flightChgEvt.addChild(attr("schedule", "FlightSchedule", Visibility.PRIVATE));
        flightChgEvt.addChild(attr("previousStatus", "FlightStatus", Visibility.PRIVATE));
        flightChgEvt.addChild(attr("newStatus", "FlightStatus", Visibility.PRIVATE));

        generalization(seatExpEvt, eventBase);
        generalization(payFailEvt, eventBase);
        generalization(flightChgEvt, eventBase);

        // ── Subjects (extend EventPublisher) ──
        ClassModel monitor = cls("SeatHoldMonitor", "control", 40, 360, 260, 130, CONTROL_BG, root);
        monitor.addChild(op("track", "void", Visibility.PUBLIC, new String[][]{{"seat", "Seat"}, {"pnr", "String"}}));
        monitor.addChild(op("sweep", "int", Visibility.PUBLIC));

        ClassModel payProc = cls("PaymentProcessor", "control", 340, 360, 260, 140, CONTROL_BG, root);
        payProc.addChild(op("processPaymentCharge", "Payment", Visibility.PUBLIC, new String[][]{{"amount", "long"}, {"pnr", "String"}}));
        payProc.addChild(op("processMileagePayment", "Payment", Visibility.PUBLIC, new String[][]{{"account", "MileageAccount"}, {"cost", "long"}, {"pnr", "String"}}));

        ClassModel flightSchedule = cls("FlightSchedule", "entity", 650, 360, 250, 120, ENTITY_BG, root);
        flightSchedule.addChild(op("changeStatus", "void", Visibility.PUBLIC, new String[][]{{"status", "FlightStatus"}}));

        generalization(monitor, publisher);
        generalization(payProc, publisher);
        generalization(flightSchedule, publisher);

        // ── Listeners ──
        ClassModel holdL = cls("ReservationHoldListener", "control", 40, 540, 260, 100, CONTROL_BG, root);
        holdL.addChild(op("onEvent", "void", Visibility.PUBLIC, new String[][]{{"event", "DomainEvent"}}));

        ClassModel cancelL = cls("ReservationAutoCancelListener", "control", 320, 540, 290, 100, CONTROL_BG, root);
        cancelL.addChild(op("onEvent", "void", Visibility.PUBLIC, new String[][]{{"event", "DomainEvent"}}));

        ClassModel affectedL = cls("AffectedReservationListener", "control", 630, 540, 290, 100, CONTROL_BG, root);
        affectedL.addChild(op("onEvent", "void", Visibility.PUBLIC, new String[][]{{"event", "DomainEvent"}}));

        realization(holdL, listener);
        realization(cancelL, listener);
        realization(affectedL, listener);

        // ── Connecting / Multi-city ──
        ClassModel itinerary = cls("Itinerary", "entity", 40, 700, 240, 160, ENTITY_BG, root);
        itinerary.addChild(attr("tripType", "String", Visibility.PRIVATE));
        itinerary.addChild(attr("segments", "List<Segment>", Visibility.PRIVATE));
        itinerary.addChild(op("isConnectionTimeValid", "boolean", Visibility.PUBLIC,
                new String[][]{{"mct", "Duration"}}));
        itinerary.addChild(op("getTotalDuration", "Duration", Visibility.PUBLIC));
        itinerary.addChild(op("connecting", "Itinerary", Visibility.PUBLIC,
                new String[][]{{"first", "FlightSchedule"}, {"second", "FlightSchedule"}}));

        ClassModel itinSvc = cls("ItinerarySearchService", "control", 320, 700, 270, 140, CONTROL_BG, root);
        itinSvc.addChild(op("searchDirect", "List<Itinerary>", Visibility.PUBLIC,
                new String[][]{{"from", "String"}, {"to", "String"}, {"date", "LocalDate"}}));
        itinSvc.addChild(op("searchConnecting", "List<Itinerary>", Visibility.PUBLIC,
                new String[][]{{"from", "String"}, {"to", "String"}, {"date", "LocalDate"}, {"mct", "Duration"}}));

        association(itinSvc, itinerary, "1", "*");

        // ── Mileage / Skypass ──
        InterfaceModel skypassI = iface("SkypassInterface", "interface", 630, 700, 250, 120, BOUNDARY_BG, root);
        skypassI.addChild(op("getMileageBalance", "int", Visibility.PUBLIC, new String[][]{{"skypassNumber", "String"}}));
        skypassI.addChild(op("deductMileage", "boolean", Visibility.PUBLIC,
                new String[][]{{"skypassNumber", "String"}, {"amount", "int"}}));

        ClassModel mockSkypass = cls("MockSkypassInterface", "boundary", 630, 850, 250, 100, BOUNDARY_BG, root);
        realization(mockSkypass, skypassI);

        // Dependencies between Subjects and Events
        dependency(monitor, seatExpEvt);
        dependency(payProc, payFailEvt);
        dependency(flightSchedule, flightChgEvt);

        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[]{"**"});

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(root);
        try (FileWriter fw = new FileWriter("src/classDiagram-iter3.cld")) {
            fw.write(xml);
        }
        System.out.println("Generated: src/classDiagram-iter3.cld");
        System.out.println("Iter3 focus: Observer infra + 3 Subjects + 3 Listeners + Connecting/Mileage");
    }
}
