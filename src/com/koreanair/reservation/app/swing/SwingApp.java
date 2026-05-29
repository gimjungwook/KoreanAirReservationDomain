package com.koreanair.reservation.app.swing;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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

/**
 * Iteration 1~3 Swing GUI 런처.
 *
 * <p>기존 콘솔 드라이버({@link com.koreanair.reservation.app.App}) 와 동일한 Control/Domain 인프라 위에
 * Swing UI 만 얹은 별도 엔트리 포인트. 시그니처 변경이나 도메인 수정 없이 Boundary 만 교체한다.
 *
 * <p>Iteration 1: Login → Search → Passenger Info → Payment → Confirmation.
 * <p>Iteration 2: 헤더의 "예약 조회" 버튼으로 Lookup → Cancellation → Refund 흐름에 진입.
 * <p>Iteration 3: 결제 완료 화면에서 e-Ticket 발급 후 Observer 로 우등고속 버스티켓을 연계 발매.
 *
 * <p>실행:
 *   java -cp bin com.koreanair.reservation.app.swing.SwingApp
 *
 * <p>Headless 환경(HeadlessException) 에서는 생성자 호출까지만 시도하고 종료. 실제 렌더링은 사용자 수동 확인.
 */
public final class SwingApp {

    private SwingApp() {}

    public static void main(String[] args) {
        // Look & Feel — 실패해도 기본으로 fallback.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
            // no-op
        }

        // --- 1) 의존성 구성 (App.java 와 동일한 wiring + iter 2 RefundHandler/Lookup) ---
        AuthService auth = new AuthService();
        FlightSearchService search = new FlightSearchService();
        PaymentGatewayInterface gateway = new MockPaymentGateway();
        PaymentProcessor paymentProcessor = new PaymentProcessor(gateway);
        RefundHandler refundHandler = new RefundHandler(gateway);
        ReservationLookupService lookupService = new ReservationLookupService(auth);
        TicketPurchasePublisher ticketPublisher = new TicketPurchasePublisher();
        BusTicketingService busTicketingService = new BusTicketingService();
        ticketPublisher.subscribe(new BusTicketPurchaseListener(busTicketingService));
        System.out.println("[SwingApp] TicketPurchasePublisher subscriberCount = "
                + ticketPublisher.subscriberCount());
        System.out.println("[SwingApp] BusTicketPurchaseListener registered");
        BookingController booking = new BookingController(
                auth, search, paymentProcessor, refundHandler, lookupService);
        SwingReservationUI ui = new SwingReservationUI();

        // --- 2) Sample seed (회원 SKY-000-001 김정욱 + 항공편 카탈로그) ---
        SeedResult seed = SampleData.seedAll(auth, search);

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[SwingApp] headless mode: Swing frame creation skipped");
            return;
        }

        // --- 3) Swing 메인 프레임 — EDT 에서 생성 ---
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                    auth, search, paymentProcessor, booking,
                    refundHandler, lookupService,
                    ui, seed,
                    ticketPublisher, busTicketingService);
            frame.setVisible(true);
            frame.showSearch();
        });
    }
}
