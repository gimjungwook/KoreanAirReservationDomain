package com.koreanair.reservation.app.fx;

import com.koreanair.reservation.app.MockPaymentGateway;
import com.koreanair.reservation.app.sample.SampleData;
import com.koreanair.reservation.app.sample.SampleData.SeedResult;
import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.BusTicketPurchaseListener;
import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.control.FlightSearchService;
import com.koreanair.reservation.control.PaymentProcessor;
import com.koreanair.reservation.control.RefundHandler;
import com.koreanair.reservation.control.ReservationLookupService;
import com.koreanair.reservation.control.TicketPurchasePublisher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX 런처 — Swing 의 SwingApp 을 대체하는 새 Boundary 엔트리 포인트.
 *
 * <p>Control/Domain 인프라 wiring 은 Swing 버전과 동일하다. UI 만 FXML + CSS 기반의
 * JavaFX 로 교체했을 뿐, 9개 디자인 패턴은 control/domain 계층에 그대로 살아 있다.
 *
 * <p>실행: {@code mvn javafx:run}  (또는 ./mvnw javafx:run)
 */
public final class FxApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // --- 1) 의존성 구성 (SwingApp 과 동일) ---
        AuthService auth = new AuthService();
        FlightSearchService search = new FlightSearchService();
        PaymentGatewayInterface gateway = new MockPaymentGateway();
        PaymentProcessor paymentProcessor = new PaymentProcessor(gateway);
        RefundHandler refundHandler = new RefundHandler(gateway);
        ReservationLookupService lookupService = new ReservationLookupService(auth);
        TicketPurchasePublisher ticketPublisher = new TicketPurchasePublisher();
        BusTicketingService busTicketingService = new BusTicketingService();
        ticketPublisher.subscribe(new BusTicketPurchaseListener(busTicketingService));
        BookingController booking = new BookingController(
                auth, search, paymentProcessor, refundHandler, lookupService);

        // --- 2) Sample seed (회원 + 항공편 카탈로그) ---
        SeedResult seed = SampleData.seedAll(auth, search);

        AppContext ctx = new AppContext(auth, search, paymentProcessor, refundHandler,
                lookupService, booking, ticketPublisher, busTicketingService, seed);

        // --- 3) Shell 로드 + Navigator ---
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shell.fxml"));
        Parent shellRoot = loader.load();
        ShellController shell = loader.getController();
        Navigator nav = new Navigator(ctx, shell);

        Scene scene = new Scene(shellRoot, 1080, 760);
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setTitle("대한항공 예약 시스템 — JavaFX");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(680);

        // --- 4) 초기 화면: 검색 (비회원도 탐색 가능, 예약 시 로그인 요구) ---
        nav.showSearch();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
