package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.ReservationLookupService;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
import com.koreanair.reservation.domain.user.User;
import com.koreanair.reservation.domain.user.Member;

/**
 * 예약 조회 화면 — 회원/비회원 분기.
 *
 * <p>Iteration 2:
 * <ul>
 *   <li>회원 모드: 로그인된 회원의 예약 목록을 JTable 로 표시. 행 클릭 → 취소 화면 전이.</li>
 *   <li>비회원 모드: PNR + 이름 + 이메일 검증 후 예약 단건을 취소 화면으로 전달.</li>
 * </ul>
 */
public class LookupPanel extends JPanel {

    private static final String CARD_MEMBER = "member";
    private static final String CARD_GUEST = "guest";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MainFrame frame;
    @SuppressWarnings("unused")
    private final BookingController booking;
    private final ReservationLookupService lookupService;
    private final AuthService authService;

    private final JRadioButton memberMode = new JRadioButton("회원 조회", true);
    private final JRadioButton guestMode = new JRadioButton("비회원 조회");

    private final CardLayout modeLayout = new CardLayout();
    private final JPanel modePanel = new JPanel(modeLayout);

    // 회원 조회 — 결과 테이블.
    private final DefaultTableModel memberTableModel =
            new DefaultTableModel(new Object[] { "PNR", "출발", "도착", "출발 일자", "상태" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable memberTable = new JTable(memberTableModel);
    private final JLabel memberStatusLabel = new JLabel(" ");
    private final JTextField selectedSummaryLabel = new JTextField("선택된 예약이 없습니다.");
    private final JButton copySelectedPnrButton = new JButton("선택 PNR 복사");

    // 비회원 조회 — 폼 필드.
    private final JTextField pnrField = new JTextField(18);
    private final JTextField nameField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JLabel guestMessage = new JLabel(" ");
    private final JButton fillGuestButton = new JButton("현재/선택 예약 자동 입력");
    private final JButton pastePnrButton = new JButton("PNR 붙여넣기");

    private final JButton searchButton = new JButton("조회");
    private final JButton cancelButton = new JButton("← 항공 예약 홈");

    // 회원 모드에서 row index → Reservation 매핑 보관.
    private List<Reservation> memberResults = new java.util.ArrayList<>();

    public LookupPanel(MainFrame parent,
                       BookingController bookingController,
                       ReservationLookupService lookupService,
                       AuthService authService) {
        super(new BorderLayout());
        this.frame = parent;
        this.booking = bookingController;
        this.lookupService = lookupService;
        this.authService = authService;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
        wireEvents();
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernUI.BACKGROUND);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel title = new JLabel("예약 조회");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel modeBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        modeBar.setBackground(ModernUI.BACKGROUND);
        modeBar.setOpaque(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(memberMode);
        grp.add(guestMode);
        memberMode.setBackground(ModernUI.BACKGROUND);
        guestMode.setBackground(ModernUI.BACKGROUND);
        memberMode.setFont(ModernUI.FONT_BODY);
        guestMode.setFont(ModernUI.FONT_BODY);
        modeBar.add(memberMode);
        modeBar.add(guestMode);
        header.add(modeBar, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        modePanel.setBackground(ModernUI.BACKGROUND);
        modePanel.setOpaque(true);
        modePanel.add(buildMemberCard(), CARD_MEMBER);
        modePanel.add(buildGuestCard(), CARD_GUEST);
        add(modePanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        ModernUI.styleButtonSecondary(cancelButton);
        ModernUI.styleButton(searchButton);
        footer.add(cancelButton);
        footer.add(searchButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildMemberCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ModernUI.BACKGROUND);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel hint = new JLabel("예약 상태에 맞는 동작을 제공합니다. 결제 대기는 즉시 취소, 확정/발권은 취소·환불 화면으로 이동합니다.");
        hint.setFont(ModernUI.FONT_SMALL);
        hint.setForeground(ModernUI.TEXT_SECONDARY);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(hint, BorderLayout.NORTH);

        memberTable.setFont(ModernUI.FONT_BODY);
        memberTable.setRowHeight(32);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setGridColor(ModernUI.BORDER);
        memberTable.setBackground(ModernUI.CARD_BG);
        memberTable.setShowVerticalLines(false);
        JTableHeader head = memberTable.getTableHeader();
        head.setFont(ModernUI.FONT_SUBHEADING);
        head.setBackground(ModernUI.PRIMARY_LIGHT);
        head.setForeground(ModernUI.TEXT_PRIMARY);

        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    openSelected();
                }
            }
        });
        memberTable.getSelectionModel().addListSelectionListener(this::onMemberSelectionChanged);

        JScrollPane scroll = new JScrollPane(memberTable);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        scroll.getViewport().setBackground(ModernUI.CARD_BG);
        card.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setBackground(ModernUI.BACKGROUND);
        bottom.setOpaque(true);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        memberStatusLabel.setFont(ModernUI.FONT_SMALL);
        memberStatusLabel.setForeground(ModernUI.TEXT_SECONDARY);
        bottom.add(memberStatusLabel, BorderLayout.NORTH);

        JPanel summaryRow = new JPanel(new BorderLayout(10, 0));
        summaryRow.setBackground(ModernUI.BACKGROUND);
        summaryRow.setOpaque(true);
        ModernUI.styleSelectableValue(selectedSummaryLabel,
                ModernUI.FONT_SMALL,
                ModernUI.TEXT_PRIMARY,
                ModernUI.PRIMARY_LIGHT);
        selectedSummaryLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        selectedSummaryLabel.setToolTipText("마우스로 드래그하거나 Cmd/Ctrl+C로 선택 예약 정보를 복사할 수 있습니다.");
        summaryRow.add(selectedSummaryLabel, BorderLayout.CENTER);
        ModernUI.styleButtonSecondary(copySelectedPnrButton);
        copySelectedPnrButton.setFont(ModernUI.FONT_SMALL);
        copySelectedPnrButton.setEnabled(false);
        summaryRow.add(copySelectedPnrButton, BorderLayout.EAST);
        bottom.add(summaryRow, BorderLayout.CENTER);

        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildGuestCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(ModernUI.BACKGROUND);
        wrapper.setOpaque(true);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ModernUI.CARD_BG);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 28, 20, 28)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel pnrLbl = new JLabel("예약번호 (PNR)");
        pnrLbl.setFont(ModernUI.FONT_SMALL);
        pnrLbl.setForeground(ModernUI.TEXT_SECONDARY);
        c.gridx = 0; c.gridy = 0;
        card.add(pnrLbl, c);

        c.gridx = 1;
        ModernUI.styleTextField(pnrField);
        JPanel pnrInputPanel = new JPanel(new BorderLayout(8, 0));
        pnrInputPanel.setBackground(ModernUI.CARD_BG);
        pnrInputPanel.setOpaque(true);
        pnrInputPanel.add(pnrField, BorderLayout.CENTER);
        ModernUI.styleButtonSecondary(pastePnrButton);
        pastePnrButton.setFont(ModernUI.FONT_SMALL);
        pnrInputPanel.add(pastePnrButton, BorderLayout.EAST);
        card.add(pnrInputPanel, c);

        JLabel nameLbl = new JLabel("이름");
        nameLbl.setFont(ModernUI.FONT_SMALL);
        nameLbl.setForeground(ModernUI.TEXT_SECONDARY);
        c.gridx = 0; c.gridy = 1;
        card.add(nameLbl, c);

        c.gridx = 1;
        ModernUI.styleTextField(nameField);
        card.add(nameField, c);

        JLabel emailLbl = new JLabel("이메일");
        emailLbl.setFont(ModernUI.FONT_SMALL);
        emailLbl.setForeground(ModernUI.TEXT_SECONDARY);
        c.gridx = 0; c.gridy = 2;
        card.add(emailLbl, c);

        c.gridx = 1;
        ModernUI.styleTextField(emailField);
        card.add(emailField, c);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        ModernUI.styleButtonSecondary(fillGuestButton);
        fillGuestButton.setFont(ModernUI.FONT_SMALL);
        card.add(fillGuestButton, c);

        JLabel helper = new JLabel("데모 중에는 현재 결제 완료 예약 또는 회원 조회에서 선택한 예약을 자동 입력할 수 있습니다.");
        helper.setFont(ModernUI.FONT_SMALL);
        helper.setForeground(ModernUI.TEXT_SECONDARY);
        c.gridy = 4;
        card.add(helper, c);

        c.gridy = 5;
        guestMessage.setFont(ModernUI.FONT_SMALL);
        guestMessage.setForeground(ModernUI.ERROR);
        card.add(guestMessage, c);

        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0; wc.gridy = 0;
        wrapper.add(card, wc);
        return wrapper;
    }

    private void wireEvents() {
        memberMode.addActionListener(e -> {
            modeLayout.show(modePanel, CARD_MEMBER);
            searchButton.setText("선택 예약 열기");
            refreshMemberList();
        });
        guestMode.addActionListener(e -> {
            modeLayout.show(modePanel, CARD_GUEST);
            searchButton.setText("PNR 조회");
            guestMessage.setForeground(ModernUI.ERROR);
            guestMessage.setText(" ");
        });
        searchButton.addActionListener(e -> doSearch());
        cancelButton.addActionListener(e -> frame.startNewBooking());
        fillGuestButton.addActionListener(e -> fillGuestFields());
        copySelectedPnrButton.addActionListener(e -> copySelectedPnr());
        pastePnrButton.addActionListener(e -> pastePnr());
        pnrField.addActionListener(e -> doGuestLookup());
        nameField.addActionListener(e -> doGuestLookup());
        emailField.addActionListener(e -> doGuestLookup());
    }

    /** MainFrame 이 카드 전환 시 호출. 로그인 회원의 예약 목록을 새로고침한다. */
    public void refresh() {
        searchButton.setText(memberMode.isSelected() ? "선택 예약 열기" : "PNR 조회");
        if (memberMode.isSelected()) {
            refreshMemberList();
        }
    }

    private void refreshMemberList() {
        memberTableModel.setRowCount(0);
        memberResults.clear();
        selectedSummaryLabel.setText("선택된 예약이 없습니다.");
        copySelectedPnrButton.setEnabled(false);
        Member current = authService != null ? authService.currentMember() : null;
        if (current == null) {
            memberStatusLabel.setText("로그인된 회원이 없습니다. 먼저 로그인하거나 비회원 조회를 선택하세요.");
            return;
        }
        List<Reservation> list = lookupService.findByMember(current);
        if (list == null || list.isEmpty()) {
            memberStatusLabel.setText("아직 예약이 없습니다. Book Flight에서 예약을 완료하면 이곳에 바로 표시됩니다.");
            return;
        }
        for (Reservation r : list) {
            memberResults.add(r);
            memberTableModel.addRow(toRow(r));
        }
        memberStatusLabel.setText(String.format("%s님의 예약 %d건이 조회되었습니다. 첫 번째 예약을 자동 선택했습니다.",
                current.getName(), memberResults.size()));
        memberTable.setRowSelectionInterval(0, 0);
        updateSelectedSummary(memberResults.get(0));
    }

    private Object[] toRow(Reservation r) {
        String pnr = r.getPnrNumber() != null ? r.getPnrNumber() : "-";
        String origin = "-";
        String dest = "-";
        String depDate = "-";
        Itinerary it = r.getItinerary();
        if (it != null && it.getSegments() != null && !it.getSegments().isEmpty()) {
            Segment first = it.getSegments().get(0);
            FlightSchedule fs = first != null ? first.getFlightSchedule() : null;
            if (fs != null && fs.getFlight() != null && fs.getFlight().getRoute() != null) {
                if (fs.getFlight().getRoute().getOrigin() != null
                        && fs.getFlight().getRoute().getOrigin().getAirportCode() != null) {
                    origin = fs.getFlight().getRoute().getOrigin().getAirportCode();
                }
                if (fs.getFlight().getRoute().getDestination() != null
                        && fs.getFlight().getRoute().getDestination().getAirportCode() != null) {
                    dest = fs.getFlight().getRoute().getDestination().getAirportCode();
                }
            }
            if (fs != null && fs.getDepartureDateTime() != null) {
                depDate = fs.getDepartureDateTime().toLocalDate().format(DATE_FMT);
            }
        }
        return new Object[] { pnr, origin, dest, depDate, r.getStateName() };
    }

    private void doSearch() {
        if (memberMode.isSelected()) {
            int row = memberTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "예약 행을 선택하세요.",
                        "안내",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            openSelected();
        } else {
            doGuestLookup();
        }
    }

    private void openSelected() {
        int row = memberTable.getSelectedRow();
        if (row < 0 || row >= memberResults.size()) {
            JOptionPane.showMessageDialog(this,
                    "예약 목록에서 열 예약을 선택하세요.",
                    "안내",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Reservation r = memberResults.get(row);
        openReservationByState(r);
    }

    private void onMemberSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = memberTable.getSelectedRow();
        if (row >= 0 && row < memberResults.size()) {
            Reservation selected = memberResults.get(row);
            updateSelectedSummary(selected);
            updatePrimaryAction(selected);
        }
    }

    private void updateSelectedSummary(Reservation r) {
        if (r == null) {
            selectedSummaryLabel.setText("선택된 예약이 없습니다.");
            return;
        }
        Object[] row = toRow(r);
        selectedSummaryLabel.setText(String.format(
                "선택됨: PNR %s · %s → %s · %s · 상태 %s",
                row[0], row[1], row[2], row[3], row[4]));
        copySelectedPnrButton.setEnabled(r.getPnrNumber() != null);
    }

    private void updatePrimaryAction(Reservation r) {
        if (!memberMode.isSelected()) {
            searchButton.setText("PNR 조회");
            return;
        }
        String state = r != null ? r.getStateName() : "";
        if ("PendingPayment".equals(state)) {
            searchButton.setText("결제 대기 예약 취소");
        } else if ("Confirmed".equals(state) || "Ticketed".equals(state)) {
            searchButton.setText("취소/환불 진행");
        } else if ("Cancelled".equals(state) || "Refunded".equals(state)) {
            searchButton.setText("처리 완료 예약 보기");
        } else {
            searchButton.setText("선택 예약 열기");
        }
    }

    private void openReservationByState(Reservation r) {
        if (r == null) {
            return;
        }
        String state = r.getStateName();
        if ("PendingPayment".equals(state)) {
            cancelPendingPayment(r);
            return;
        }
        if ("Confirmed".equals(state) || "Ticketed".equals(state)) {
            frame.showCancellation(r);
            return;
        }
        if ("Cancelled".equals(state) || "Refunded".equals(state)) {
            JOptionPane.showMessageDialog(this,
                    "이미 처리 완료된 예약입니다.\nPNR: " + r.getPnrNumber() + "\n상태: " + state,
                    "예약 상태",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "현재 상태에서는 취소/환불을 진행할 수 없습니다.\nPNR: "
                        + r.getPnrNumber() + "\n상태: " + state,
                "상태 확인",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelPendingPayment(Reservation r) {
        int answer = JOptionPane.showConfirmDialog(this,
                "이 예약은 아직 결제 대기 상태입니다.\n환불 없이 예약만 취소할까요?\n\nPNR: "
                        + r.getPnrNumber(),
                "결제 대기 예약 취소",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            r.handlePaymentFailure();
            frame.syncReservationState(r);
            refreshMemberList();
            JOptionPane.showMessageDialog(this,
                    "결제 대기 예약이 취소되었습니다.\nPNR: " + r.getPnrNumber(),
                    "취소 완료",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "결제 대기 예약 취소 중 오류: " + ex.getMessage(),
                    "취소 실패",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doGuestLookup() {
        guestMessage.setForeground(ModernUI.ERROR);
        guestMessage.setText(" ");
        String pnr = pnrField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (pnr.isEmpty() || name.isEmpty() || email.isEmpty()) {
            guestMessage.setText("PNR, 이름, 이메일을 모두 입력하세요.");
            return;
        }
        Reservation r = lookupService.findByGuestPnr(pnr, name, email);
        if (r == null) {
            JOptionPane.showMessageDialog(this,
                    "예약을 찾을 수 없습니다. 입력 정보를 확인하세요.",
                    "조회 실패",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        openReservationByState(r);
    }

    private void fillGuestFields() {
        Reservation source = frame.currentReservation();
        int selectedRow = memberTable.getSelectedRow();
        if (source == null && selectedRow >= 0 && selectedRow < memberResults.size()) {
            source = memberResults.get(selectedRow);
        }
        if (source == null && !memberResults.isEmpty()) {
            source = memberResults.get(0);
        }
        if (source == null) {
            guestMessage.setText("자동 입력할 예약이 없습니다. 먼저 예약을 생성하거나 회원 예약을 선택하세요.");
            return;
        }
        pnrField.setText(source.getPnrNumber() != null ? source.getPnrNumber() : "");
        Passenger passenger = !source.getPassengers().isEmpty() ? source.getPassengers().get(0) : null;
        String name = passenger != null ? passenger.getName() : "";
        String email = passenger != null ? passenger.getContactInfo() : null;
        User requester = source.getRequester();
        if ((name == null || name.isBlank()) && requester instanceof Member) {
            name = ((Member) requester).getName();
        }
        if (email == null || email.isBlank()) {
            if (requester instanceof Member) {
                email = ((Member) requester).getEmail();
            }
        }
        nameField.setText(name != null ? name : "");
        emailField.setText(email != null && !email.isBlank() ? email : "guest@example.com");
        guestMessage.setForeground(ModernUI.TEXT_SECONDARY);
        guestMessage.setText("현재/선택 예약 정보가 입력되었습니다. 조회를 누르면 같은 검증 흐름을 탑니다.");
    }

    private void copySelectedPnr() {
        Reservation source = selectedReservation();
        if (source == null || source.getPnrNumber() == null) {
            return;
        }
        ModernUI.copyToClipboard(source.getPnrNumber());
        memberStatusLabel.setText("PNR이 클립보드에 복사되었습니다: " + source.getPnrNumber());
    }

    private void pastePnr() {
        String text = ModernUI.pasteFromClipboard().trim();
        if (text.isEmpty()) {
            guestMessage.setForeground(ModernUI.ERROR);
            guestMessage.setText("클립보드에 붙여넣을 PNR이 없습니다.");
            return;
        }
        pnrField.setText(text);
        guestMessage.setForeground(ModernUI.TEXT_SECONDARY);
        guestMessage.setText("클립보드에서 PNR을 붙여넣었습니다.");
    }

    private Reservation selectedReservation() {
        int row = memberTable.getSelectedRow();
        if (row >= 0 && row < memberResults.size()) {
            return memberResults.get(row);
        }
        return null;
    }

}
