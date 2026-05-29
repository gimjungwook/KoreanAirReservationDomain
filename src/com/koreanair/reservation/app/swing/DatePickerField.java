package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

/**
 * KE 스타일 달력 입력 — 클릭 시 JPopupMenu 로 7x6 day grid 띄움.
 *
 * <p>월/년 navigator + 오늘/선택 강조 + 다른 월 옅게.
 * 외부 라이브러리 없이 순수 Swing 구현.
 */
public class DatePickerField extends JPanel {

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)");
    private static final String[] WEEK_HEADERS = {"일", "월", "화", "수", "목", "금", "토"};

    private final JLabel valueLabel = new JLabel();
    private final JButton trigger;
    private LocalDate selectedDate;
    private LocalDate minDate;
    private Consumer<LocalDate> onChange;
    private JPopupMenu popup;
    private YearMonth visibleMonth;

    public DatePickerField() {
        super(new BorderLayout());
        setOpaque(false);
        this.selectedDate = LocalDate.now().plusDays(1);
        this.minDate = LocalDate.now();
        this.visibleMonth = YearMonth.from(selectedDate);

        valueLabel.setFont(ModernUI.FONT_BODY);
        valueLabel.setForeground(ModernUI.TEXT_PRIMARY);

        JLabel icon = new JLabel("▾");
        icon.setFont(ModernUI.FONT_BODY_BOLD);
        icon.setForeground(ModernUI.KE_NAVY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        trigger = new JButton();
        trigger.setLayout(new BorderLayout());
        trigger.add(icon, BorderLayout.WEST);
        trigger.add(valueLabel, BorderLayout.CENTER);
        trigger.setHorizontalAlignment(SwingConstants.LEFT);
        trigger.setBackground(Color.WHITE);
        trigger.setForeground(ModernUI.TEXT_PRIMARY);
        trigger.setBorder(BorderFactory.createCompoundBorder(
                new ModernUI.RoundedBorder(ModernUI.BORDER, 1, ModernUI.CORNER_RADIUS_SM),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        trigger.setFocusPainted(false);
        trigger.setFocusable(false);
        trigger.setContentAreaFilled(false);
        trigger.setOpaque(true);
        trigger.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        trigger.addActionListener(e -> openPopup());
        add(trigger, BorderLayout.CENTER);

        updateLabel();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        if (date == null) {
            return;
        }
        this.selectedDate = date;
        this.visibleMonth = YearMonth.from(date);
        updateLabel();
        if (onChange != null) {
            onChange.accept(date);
        }
    }

    public void setMinDate(LocalDate min) {
        this.minDate = min;
    }

    public void setOnChange(Consumer<LocalDate> cb) {
        this.onChange = cb;
    }

    public void setEnabledState(boolean enabled) {
        trigger.setEnabled(enabled);
    }

    private void updateLabel() {
        valueLabel.setText(selectedDate.format(DISPLAY_FMT));
    }

    private void openPopup() {
        popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createCompoundBorder(
                new ModernUI.RoundedBorder(ModernUI.BORDER_STRONG, 1, ModernUI.CORNER_RADIUS),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        popup.setBackground(Color.WHITE);

        JPanel calendar = buildCalendar();
        popup.add(calendar);
        popup.show(trigger, 0, trigger.getHeight() + 8);
    }

    private JPanel buildCalendar() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBackground(Color.WHITE);
        root.setOpaque(true);
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setPreferredSize(new Dimension(320, 300));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setOpaque(true);
        JButton prev = navButton("‹");
        JButton next = navButton("›");
        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.KE_NAVY);
        title.setText(visibleMonth.getYear() + "년 " + visibleMonth.getMonthValue() + "월");

        prev.addActionListener(e -> {
            visibleMonth = visibleMonth.minusMonths(1);
            title.setText(visibleMonth.getYear() + "년 " + visibleMonth.getMonthValue() + "월");
            refreshGrid(root, title);
        });
        next.addActionListener(e -> {
            visibleMonth = visibleMonth.plusMonths(1);
            title.setText(visibleMonth.getYear() + "년 " + visibleMonth.getMonthValue() + "월");
            refreshGrid(root, title);
        });
        header.add(prev, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        root.add(buildGrid(), BorderLayout.CENTER);
        return root;
    }

    private JButton navButton(String txt) {
        JButton btn = new JButton(txt);
        btn.setFont(new java.awt.Font(ModernUI.FONT_BODY.getFamily(), java.awt.Font.BOLD, 20));
        btn.setForeground(ModernUI.KE_NAVY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }

    private void refreshGrid(JPanel root, JLabel title) {
        java.awt.Component[] comps = root.getComponents();
        for (java.awt.Component c : comps) {
            if (c != root.getLayout()) {
                if (c.getParent() == root && c != title.getParent()) {
                    root.remove(c);
                }
            }
        }
        root.add(buildGrid(), BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    private JPanel buildGrid() {
        JPanel grid = new JPanel(new BorderLayout(0, 4));
        grid.setBackground(Color.WHITE);
        grid.setOpaque(true);

        JPanel weekRow = new JPanel(new GridLayout(1, 7, 4, 4));
        weekRow.setOpaque(false);
        for (int i = 0; i < 7; i++) {
            JLabel l = new JLabel(WEEK_HEADERS[i], SwingConstants.CENTER);
            l.setFont(ModernUI.FONT_SMALL);
            Color c = (i == 0) ? ModernUI.KE_RED : (i == 6 ? ModernUI.KE_NAVY : ModernUI.TEXT_SECONDARY);
            l.setForeground(c);
            weekRow.add(l);
        }
        grid.add(weekRow, BorderLayout.NORTH);

        JPanel days = new JPanel(new GridLayout(6, 7, 4, 4));
        days.setOpaque(false);

        // 시작 — 1일이 무슨 요일인지 (일요일 = 0 기준)
        LocalDate first = visibleMonth.atDay(1);
        int startCol = first.getDayOfWeek().getValue() % 7; // SUN=0, MON=1, ..., SAT=6
        LocalDate cursor = first.minusDays(startCol);

        for (int i = 0; i < 42; i++) {
            LocalDate d = cursor.plusDays(i);
            days.add(makeDayCell(d));
        }
        grid.add(days, BorderLayout.CENTER);
        return grid;
    }

    private JLabel makeDayCell(LocalDate d) {
        JLabel cell = new JLabel(String.valueOf(d.getDayOfMonth()), SwingConstants.CENTER);
        cell.setFont(ModernUI.FONT_BODY);
        cell.setOpaque(true);
        cell.setPreferredSize(new Dimension(38, 36));
        boolean otherMonth = !YearMonth.from(d).equals(visibleMonth);
        boolean disabled = minDate != null && d.isBefore(minDate);
        boolean isToday = d.equals(LocalDate.now());
        boolean isSelected = d.equals(selectedDate);

        Color fg;
        Color bg = Color.WHITE;
        if (isSelected) {
            bg = ModernUI.KE_NAVY;
            fg = Color.WHITE;
        } else if (otherMonth) {
            fg = ModernUI.TEXT_MUTED;
        } else if (disabled) {
            fg = ModernUI.TEXT_MUTED;
            bg = new Color(0xF9, 0xFA, 0xFB);
        } else if (d.getDayOfWeek() == DayOfWeek.SUNDAY) {
            fg = ModernUI.KE_RED;
        } else if (d.getDayOfWeek() == DayOfWeek.SATURDAY) {
            fg = ModernUI.KE_NAVY;
        } else {
            fg = ModernUI.TEXT_PRIMARY;
        }
        if (isToday && !isSelected) {
            cell.setBorder(BorderFactory.createCompoundBorder(
                    new ModernUI.RoundedBorder(ModernUI.KE_NAVY, 1, 6),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }
        cell.setBackground(bg);
        cell.setForeground(fg);

        final Color defaultBg = bg;
        final Color defaultFg = fg;

        if (!disabled) {
            cell.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedDate(d);
                    if (popup != null) {
                        popup.setVisible(false);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!d.equals(selectedDate)) {
                        cell.setBackground(ModernUI.KE_NAVY_LIGHT);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!d.equals(selectedDate)) {
                        cell.setBackground(defaultBg);
                        cell.setForeground(defaultFg);
                    }
                }
            });
        }
        return cell;
    }
}
