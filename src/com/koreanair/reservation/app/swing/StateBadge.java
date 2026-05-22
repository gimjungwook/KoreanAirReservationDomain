package com.koreanair.reservation.app.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StateBadge extends JPanel {

    private static final String[] STATES = {
            "Initiated", "PendingPayment", "Confirmed", "Ticketed", "Cancelled", "Refunded"
    };
    private static final String[] LABELS_KR = {
            "예약 신청", "결제 대기", "확정", "발권", "취소", "환불"
    };

    private final JLabel[] labels = new JLabel[STATES.length];

    public StateBadge() {
        super(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        setBackground(ModernUI.PRIMARY);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        for (int i = 0; i < STATES.length; i++) {
            JLabel l = new JLabel(LABELS_KR[i], SwingConstants.CENTER);
            l.setOpaque(false);
            l.setFont(new Font("System", Font.PLAIN, 11));
            l.setForeground(new Color(0xCC, 0xE4, 0xFF));
            l.setPreferredSize(new Dimension(64, 24));
            labels[i] = l;
            add(l);
        }
    }

    public void setCurrentState(String stateName) {
        for (int i = 0; i < STATES.length; i++) {
            boolean active = STATES[i].equalsIgnoreCase(stateName);
            labels[i].setForeground(active ? Color.WHITE : new Color(0x99, 0xCB, 0xFF));
            labels[i].setFont(new Font("System", active ? Font.BOLD : Font.PLAIN, 12));
        }
    }

    public void reset() {
        for (JLabel l : labels) {
            l.setForeground(new Color(0x99, 0xCB, 0xFF));
            l.setFont(new Font("System", Font.PLAIN, 11));
        }
    }
}
