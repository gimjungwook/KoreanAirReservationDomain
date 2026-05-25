package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.passenger.PassengerType;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
import com.koreanair.reservation.domain.user.Member;

public class PassengerPanel extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextField passportField = new JTextField(20);
    private final JTextField birthField = new JTextField(20);
    private final JLabel flightInfoLabel = new JLabel(" ");
    private final JButton nextButton = new JButton("다음 단계 →");
    private final JButton backButton = new JButton("← 뒤로");

    private final MainFrame frame;
    private final BookingController booking;
    private final SwingReservationUI ui;

    private FlightSchedule selected;
    private Reservation reservation;
    private Member member;

    public PassengerPanel(MainFrame frame, BookingController booking, SwingReservationUI ui) {
        super(new BorderLayout());
        this.frame = frame;
        this.booking = booking;
        this.ui = ui;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildContent();
    }

    private void buildContent() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ModernUI.BACKGROUND);
        centerPanel.setOpaque(true);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;

        JLabel stepLabel = new JLabel("STEP 2");
        stepLabel.setFont(ModernUI.FONT_SMALL);
        stepLabel.setForeground(ModernUI.PRIMARY);
        stepLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        stepLabel.setOpaque(false);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        centerPanel.add(stepLabel, c);

        JLabel title = new JLabel("승객 정보 입력");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        title.setOpaque(false);
        c.gridy = 1; c.gridwidth = 2;
        centerPanel.add(title, c);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(ModernUI.CARD_BG);
        formCard.setOpaque(true);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0; c.insets = new Insets(4, 4, 4, 4);

        JLabel flightLabel = new JLabel("선택 항공편");
        flightLabel.setFont(ModernUI.FONT_SMALL);
        flightLabel.setForeground(ModernUI.TEXT_SECONDARY);
        flightLabel.setOpaque(false);
        formCard.add(flightLabel, c);

        c.gridx = 1;
        flightInfoLabel.setFont(ModernUI.FONT_BODY);
        flightInfoLabel.setForeground(ModernUI.PRIMARY);
        flightInfoLabel.setOpaque(false);
        formCard.add(flightInfoLabel, c);

        c.gridy = 3; c.gridx = 0;
        JLabel nameLabel = new JLabel("이름 (Name)");
        nameLabel.setFont(ModernUI.FONT_SMALL);
        nameLabel.setForeground(ModernUI.TEXT_SECONDARY);
        nameLabel.setOpaque(false);
        formCard.add(nameLabel, c);

        c.gridx = 1;
        ModernUI.styleTextField(nameField);
        formCard.add(nameField, c);

        c.gridy = 4; c.gridx = 0;
        JLabel passportLabel = new JLabel("여권번호 (Passport)");
        passportLabel.setFont(ModernUI.FONT_SMALL);
        passportLabel.setForeground(ModernUI.TEXT_SECONDARY);
        passportLabel.setOpaque(false);
        formCard.add(passportLabel, c);

        c.gridx = 1;
        ModernUI.styleTextField(passportField);
        formCard.add(passportField, c);

        c.gridy = 5; c.gridx = 0;
        JLabel birthLabel = new JLabel("생년월일 (YYYY-MM-DD)");
        birthLabel.setFont(ModernUI.FONT_SMALL);
        birthLabel.setForeground(ModernUI.TEXT_SECONDARY);
        birthLabel.setOpaque(false);
        formCard.add(birthLabel, c);

        c.gridx = 1;
        ModernUI.styleTextField(birthField);
        formCard.add(birthField, c);

        c.gridy = 6; c.gridx = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        ModernUI.styleButton(nextButton);
        nextButton.addActionListener(e -> doNext());
        formCard.add(nextButton, c);

        c.gridy = 2; c.gridx = 0; c.gridwidth = 1; c.anchor = GridBagConstraints.NORTHWEST;
        centerPanel.add(formCard, c);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.setBackground(ModernUI.CARD_BG);
        rightBtns.setOpaque(true);

        ModernUI.styleButtonSecondary(backButton);
        rightBtns.add(backButton);

        footer.add(rightBtns, BorderLayout.EAST);
        footer.setPreferredSize(new Dimension(0, 52));
        add(footer, BorderLayout.SOUTH);

        backButton.addActionListener(e -> frame.showSearch());
    }

    public void prepare(FlightSchedule selected, Member me) {
        this.selected = selected;
        this.member = me;
        nameField.setText("");
        passportField.setText("");
        birthField.setText("");
        Object[] row = SwingReservationUI.toTableRow(1, selected);
        flightInfoLabel.setText(String.format("%s (%s → %s)", row[1], row[3], row[4]));

        this.reservation = booking.initiateBooking(selected);
        if (reservation != null && me != null) {
            reservation.setRequester(me);
            if (nameField.getText().trim().isEmpty() && me.getName() != null) {
                nameField.setText(me.getName());
            }
        }
        frame.onReservationCreated(reservation);
    }

    public void prepare(Itinerary itinerary, Member me) {
        this.selected = firstSchedule(itinerary);
        this.member = me;
        nameField.setText("");
        passportField.setText("");
        birthField.setText("");
        flightInfoLabel.setText(itinerarySummary(itinerary));

        this.reservation = booking.initiateBooking(itinerary);
        if (reservation != null && me != null) {
            reservation.setRequester(me);
            if (nameField.getText().trim().isEmpty() && me.getName() != null) {
                nameField.setText(me.getName());
            }
        }
        frame.onReservationCreated(reservation);
    }

    public void prepareExisting(Reservation reservation, Member me) {
        this.reservation = reservation;
        this.member = me;
        this.selected = firstSchedule(reservation);
        nameField.setText("");
        passportField.setText("");
        birthField.setText("");
        if (selected != null) {
            Object[] row = SwingReservationUI.toTableRow(1, selected);
            flightInfoLabel.setText(String.format("%s (%s → %s)", row[1], row[3], row[4]));
        } else {
            flightInfoLabel.setText("기존 예약 " + (reservation != null ? reservation.getPnrNumber() : "-"));
        }

        Passenger passenger = reservation != null && !reservation.getPassengers().isEmpty()
                ? reservation.getPassengers().get(0)
                : null;
        if (passenger != null) {
            nameField.setText(passenger.getName() != null ? passenger.getName() : "");
            passportField.setText(passenger.getPassportNumber() != null ? passenger.getPassportNumber() : "");
            birthField.setText(passenger.getDateOfBirth() != null
                    ? passenger.getDateOfBirth().toString()
                    : "");
        } else if (me != null && me.getName() != null) {
            nameField.setText(me.getName());
        }
    }

    private FlightSchedule firstSchedule(Reservation reservation) {
        if (reservation == null
                || reservation.getItinerary() == null
                || reservation.getItinerary().getSegments() == null
                || reservation.getItinerary().getSegments().isEmpty()) {
            return null;
        }
        return reservation.getItinerary().getSegments().get(0).getFlightSchedule();
    }

    private FlightSchedule firstSchedule(Itinerary itinerary) {
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return null;
        }
        Segment first = itinerary.getSegments().get(0);
        return first != null ? first.getFlightSchedule() : null;
    }

    private String itinerarySummary(Itinerary itinerary) {
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return "선택 여정 없음";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(itinerary.getTripType() != null ? itinerary.getTripType() : "ITINERARY");
        sb.append(" · ");
        for (int i = 0; i < itinerary.getSegments().size(); i++) {
            FlightSchedule schedule = itinerary.getSegments().get(i).getFlightSchedule();
            if (schedule == null || schedule.getFlight() == null || schedule.getFlight().getRoute() == null) {
                continue;
            }
            if (i > 0) {
                sb.append(" / ");
            }
            sb.append(schedule.getFlight().getFlightNumber())
                    .append(" ")
                    .append(schedule.getFlight().getRoute().getOrigin().getAirportCode())
                    .append("→")
                    .append(schedule.getFlight().getRoute().getDestination().getAirportCode());
        }
        return sb.toString();
    }

    private void doNext() {
        if (reservation == null) {
            ui.displayError("예약이 생성되지 않았습니다.");
            return;
        }
        if (nameField.getText().trim().isEmpty()) {
            ui.displayError("승객 이름을 입력하세요.");
            return;
        }
        try {
            java.time.LocalDate birthDate = java.time.LocalDate.parse(birthField.getText().trim());
            Passenger passenger = Passenger.create(
                    nameField.getText().trim(),
                    member != null ? member.getEmail() : null,
                    passportField.getText().trim(),
                    birthDate,
                    PassengerType.ADULT);
            booking.setPassengerInfo(reservation, passenger);
            frame.onPassengerInfoEntered(reservation);
        } catch (java.time.format.DateTimeParseException ex) {
            ui.displayError("생년월일 형식이 올바르지 않습니다. 예: 1999-01-31");
        } catch (IllegalArgumentException ex) {
            ui.displayError(ex.getMessage());
        }
    }
}
