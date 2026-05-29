package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;
import com.koreanair.reservation.domain.payment.PaymentStatus;
import com.koreanair.reservation.domain.reservation.Reservation;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public final class PaymentController {

    @FXML private Label pnrLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> methodCombo;
    @FXML private Label mileageLabel;
    @FXML private CheckBox busAddon;
    @FXML private ComboBox<BusCity> busCityCombo;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;
    private FareRule fareRule;

    // 구간(segment)당 기본 운임/세금 — 왕복(2)·다구간(N)은 구간 수만큼 합산.
    private static final long FARE_PER_SEGMENT = 320_000L;
    private static final long TAX_PER_SEGMENT = 32_000L;

    private int segmentCount() {
        try {
            int n = reservation.getItinerary().getSegments().size();
            return Math.max(1, n);
        } catch (Exception e) {
            return 1;
        }
    }
    private long baseFare() { return FARE_PER_SEGMENT * segmentCount(); }
    private long tax() { return TAX_PER_SEGMENT * segmentCount(); }

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(Reservation reservation, FareRule fareRule) {
        this.reservation = reservation;
        this.fareRule = fareRule;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        totalLabel.setText(String.format("₩%,d (%d개 구간)", baseFare() + tax() + ctx.seatSurcharge(), segmentCount()));

        methodCombo.getItems().setAll("신용카드", "카카오페이", "애플페이", "계좌이체", "마일리지");
        methodCombo.getSelectionModel().selectFirst();
        // Skypass 외부 API(Adapter)로 마일리지 잔액 조회 — 회원 로그인 시.
        if (ctx.isSignedIn() && ctx.loggedInMember() != null) {
            try {
                int bal = ctx.skypass.getMileageBalance(ctx.loggedInMember().getMemberNumber());
                mileageLabel.setText("보유 마일리지 " + String.format("%,d", bal) + " M");
            } catch (Exception ignore) {
                mileageLabel.setText("");
            }
        } else {
            mileageLabel.setText("");
        }

        busCityCombo.getItems().setAll(ctx.busTicketingService.supportedCities());
        busCityCombo.setDisable(true);
        busAddon.setSelected(false);
        busAddon.selectedProperty().addListener((o, was, now) -> busCityCombo.setDisable(!now));
    }

    @FXML
    private void onPay() {
        if (reservation == null || fareRule == null) {
            message.setText("결제 대상 예약이 없습니다.");
            return;
        }
        if (busAddon.isSelected() && busCityCombo.getValue() == null) {
            message.setText("셔틀버스 연계를 선택했습니다. 출발 도시를 골라주세요.");
            return;
        }
        try {
            // DP#6 Factory Method — 선택한 결제 수단에 맞는 PaymentMethodProcessor 를 팩토리가 생성.
            Payment payment = ctx.booking.confirmPaymentWith(
                    reservation, fareRule, baseFare(), tax() + ctx.seatSurcharge(),
                    methodOf(methodCombo.getValue()), ctx.gateway, ctx.sessionMileage);
            if (payment == null || payment.getStatus() != PaymentStatus.PAID) {
                message.setText("결제가 실패했습니다. (잔액/한도 확인) 다시 시도하세요.");
                return;
            }
            // 셔틀버스 연계 발권 (Observer 패턴 시연) — 선택 시에만.
            BusTicket bus = null;
            if (busAddon.isSelected() && busCityCombo.getValue() != null) {
                try {
                    bus = ctx.issueLinkedBusTicket(reservation,
                            new BusTicketRequest(busCityCombo.getValue(), null, null));
                } catch (Exception ex) {
                    System.out.println("[BUS] issue failed: " + ex.getMessage());
                }
            }
            nav.showConfirmation(reservation, payment, bus);
        } catch (Exception ex) {
            message.setText("결제 오류: " + ex.getMessage());
        }
    }

    private static PaymentMethod methodOf(String label) {
        if (label == null) return PaymentMethod.CREDIT_CARD;
        return switch (label) {
            case "카카오페이" -> PaymentMethod.KAKAO_PAY;
            case "애플페이" -> PaymentMethod.APPLE_PAY;
            case "계좌이체" -> PaymentMethod.BANK_TRANSFER;
            case "마일리지" -> PaymentMethod.MILEAGE;
            default -> PaymentMethod.CREDIT_CARD;
        };
    }

    @FXML
    private void onBack() { nav.showSeat(reservation); }
}
