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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.ReservationLookupService;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
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

    // 비회원 조회 — 폼 필드.
    private final JTextField pnrField = new JTextField(18);
    private final JTextField nameField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JLabel guestMessage = new JLabel(" ");

    private final JButton searchButton = new JButton("조회");
    private final JButton cancelButton = new JButton("취소");

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

        JLabel hint = new JLabel("로그인한 회원의 예약 목록입니다. 행을 클릭하면 취소 화면으로 이동합니다.");
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

        JScrollPane scroll = new JScrollPane(memberTable);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        scroll.getViewport().setBackground(ModernUI.CARD_BG);
        card.add(scroll, BorderLayout.CENTER);

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
        card.add(pnrField, c);

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
            refreshMemberList();
        });
        guestMode.addActionListener(e -> {
            modeLayout.show(modePanel, CARD_GUEST);
            guestMessage.setText(" ");
        });
        searchButton.addActionListener(e -> doSearch());
        cancelButton.addActionListener(e -> frame.showSearch());
    }

    /** MainFrame 이 카드 전환 시 호출. 로그인 회원의 예약 목록을 새로고침한다. */
    public void refresh() {
        if (memberMode.isSelected()) {
            refreshMemberList();
        }
    }

    private void refreshMemberList() {
        memberTableModel.setRowCount(0);
        memberResults.clear();
        Member current = authService != null ? authService.currentMember() : null;
        if (current == null) {
            return;
        }
        List<Reservation> list = lookupService.findByMember(current);
        if (list == null) return;
        for (Reservation r : list) {
            memberResults.add(r);
            memberTableModel.addRow(toRow(r));
        }
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
        if (row < 0 || row >= memberResults.size()) return;
        Reservation r = memberResults.get(row);
        frame.showCancellation(r);
    }

    private void doGuestLookup() {
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
        frame.showCancellation(r);
    }

}
