package com.koreanair.reservation.app.swing;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import com.koreanair.reservation.boundary.ReservationUI;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;

/**
 * ReservationUI 의 Swing 구현 — JOptionPane 기반 메시지 출력을 담당.
 *
 * <p>화면 전환 자체는 {@link MainFrame} 이 CardLayout 으로 수행하고, 이 클래스는
 * 도메인 계층이 "UI 에 알려야 할 메시지" 를 발행할 때만 개입한다. Boundary 인터페이스
 * 규약을 유지해 기존 콘솔 구현과 교환 가능성을 보존한다.
 *
 * <p>TODO(iter2): displaySeatMap — AircraftType 기반 Swing 좌석맵 렌더.
 * <p>TODO(iter2): displayCancellationPreview — 위약금 프리뷰 다이얼로그.
 */
public class SwingReservationUI implements ReservationUI {

    /** 메시지 다이얼로그 부모로 쓸 메인 프레임 (null 허용 — 최상위 다이얼로그로 뜸). */
    private Component parent;

    public SwingReservationUI() {
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    @Override
    public void displaySearchResults(Object flightList) {
        // 검색 결과 리스트 렌더링은 SearchPanel 의 JTable 이 담당하므로
        // 여기서는 호출 로그만 남긴다. displaySearchResults 는 도메인 계약상 존재하지만
        // GUI 에서는 SearchPanel 이 모델을 직접 구성하는 편이 자연스럽다.
        int count = (flightList instanceof List<?>) ? ((List<?>) flightList).size() : 0;
        System.out.println("[SwingReservationUI] displaySearchResults 호출됨 — count=" + count
                + " (실제 렌더링은 SearchPanel JTable 담당)");
    }

    @Override
    public void displaySeatMap(Object aircraftType) {
        // Iteration 2: 좌석 선택 화면은 SeatSelectionPanel 이 담당하며 MainFrame.showSeatSelection
        // 으로 직접 진입한다. ReservationUI 인터페이스 호환성 유지를 위해 본 메서드는 진입 안내만 표시.
        if (parent instanceof MainFrame) {
            MainFrame frame = (MainFrame) parent;
            frame.showSeatSelection(null);
            return;
        }
        // Fallback — 부모 프레임이 없는 경우 안내 다이얼로그.
        JOptionPane.showMessageDialog(parent,
                "좌석 선택 화면으로 이동하려면 메인 프레임이 필요합니다.",
                "안내",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayItineraryDetail(FlightSchedule schedule) {
        JOptionPane.showMessageDialog(parent,
                formatItineraryDetail(schedule),
                "일정 상세",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayBookingConfirmation(String pnrNumber) {
        JOptionPane.showMessageDialog(parent,
                "예약이 완료되었습니다.\nPNR: " + pnrNumber,
                "예약 확정",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayBookingConfirmation(Reservation reservation, Payment payment) {
        // 확정 화면 전체는 ConfirmationPanel 이 담당하고, 이 메서드는 보조 팝업으로만 쓰인다.
        StringBuilder sb = new StringBuilder();
        sb.append("예약이 완료되었습니다.\n");
        if (reservation != null) {
            sb.append("PNR: ").append(reservation.getPnrNumber()).append('\n');
            sb.append("상태: ").append(reservation.getStateName()).append('\n');
        }
        if (payment != null) {
            sb.append("결제: ").append(payment.getStatus())
              .append(" / ").append(payment.getAmount()).append(" KRW");
        }
        JOptionPane.showMessageDialog(parent, sb.toString(),
                "예약 확정", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "오류",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * GUI 내부에서 FlightSchedule 을 테이블 행으로 변환할 때 사용하는 헬퍼.
     * Sample 데이터 특성상 scheduleId 등이 null 일 수 있어 항상 fallback 을 제공한다.
     */
    public static Object[] toTableRow(int index, FlightSchedule s) {
        String label = "Sample Flight " + index;
        String flightNo = "-";
        String origin = "-";
        String dest = "-";
        String status = "SCHEDULED";
        if (s != null) {
            if (s.getFlight() != null) {
                if (s.getFlight().getFlightNumber() != null) {
                    flightNo = s.getFlight().getFlightNumber();
                    label = flightNo;
                }
                if (s.getFlight().getRoute() != null) {
                    if (s.getFlight().getRoute().getOrigin() != null
                            && s.getFlight().getRoute().getOrigin().getAirportCode() != null) {
                        origin = s.getFlight().getRoute().getOrigin().getAirportCode();
                    }
                    if (s.getFlight().getRoute().getDestination() != null
                            && s.getFlight().getRoute().getDestination().getAirportCode() != null) {
                        dest = s.getFlight().getRoute().getDestination().getAirportCode();
                    }
                }
            }
            if (s.getStatus() != null) {
                status = s.getStatus().toString();
            }
        }
        return new Object[] { index, label, flightNo, origin, dest, status };
    }

    public static String formatItineraryDetail(FlightSchedule s) {
        if (s == null) {
            return "선택된 항공편이 없습니다.";
        }
        Object[] row = toTableRow(1, s);
        StringBuilder sb = new StringBuilder();
        sb.append("항공편: ").append(row[2]).append('\n');
        sb.append("구간: ").append(row[3]).append(" -> ").append(row[4]).append('\n');
        sb.append("출발: ").append(s.getDepartureDateTime()).append('\n');
        sb.append("도착: ").append(s.getArrivalDateTime()).append('\n');
        if (s.getFareRule() != null) {
            sb.append("운임 클래스: ").append(s.getFareRule().getFareClass()).append('\n');
            sb.append("환불 가능: ").append(s.getFareRule().isRefundable() ? "예" : "아니오").append('\n');
            sb.append("변경 수수료: ").append(s.getFareRule().getChangeFee()).append(" KRW\n");
            sb.append("취소 위약금: ").append(s.getFareRule().getCancellationPenalty()).append(" KRW");
        }
        return sb.toString();
    }
}
