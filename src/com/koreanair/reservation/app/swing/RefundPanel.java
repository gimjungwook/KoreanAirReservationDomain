package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 환불 결과 화면 — 취소 + 자동 환불 완료 후 진입.
 *
 * <p>iter 2 단순화: 진행 단계는 정적 텍스트로 표현하고 PNR / 금액 / 정책 이름만 표시한다.
 */
public class RefundPanel extends JPanel {

    private final MainFrame frame;

    private final JLabel pnrLabel = new JLabel(" ");
    private final JLabel amountLabel = new JLabel(" ");
    private final JLabel policyLabel = new JLabel(" ");
    private final JLabel progressLabel = new JLabel(" ");

    private final JButton homeButton = new JButton("다른 항공편 예약");
    private final JButton lookupButton = new JButton("예약 조회로");

    public RefundPanel(MainFrame parent) {
        super(new BorderLayout());
        this.frame = parent;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
    }

    private void buildLayout() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ModernUI.BACKGROUND);
        center.setOpaque(true);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ModernUI.CARD_BG);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.SUCCESS, 2),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel check = new JLabel("✓", SwingConstants.CENTER);
        check.setFont(new Font("System", Font.BOLD, 48));
        check.setForeground(ModernUI.SUCCESS);
        check.setPreferredSize(new Dimension(80, 80));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(check, c);

        JLabel title = new JLabel("환불 처리 완료");
        title.setFont(ModernUI.FONT_TITLE);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        c.gridy = 1;
        card.add(title, c);

        progressLabel.setFont(ModernUI.FONT_BODY);
        progressLabel.setForeground(ModernUI.TEXT_SECONDARY);
        progressLabel.setText("취소 → 환불 평가 → 환불 승인 → 환불 완료");
        c.gridy = 2;
        card.add(progressLabel, c);

        c.gridwidth = 1;
        c.gridy = 3; c.gridx = 0;
        card.add(makeKey("PNR"), c);
        c.gridx = 1;
        pnrLabel.setFont(new Font("Monaco", Font.PLAIN, 16));
        pnrLabel.setForeground(ModernUI.PRIMARY);
        card.add(pnrLabel, c);

        c.gridy = 4; c.gridx = 0;
        card.add(makeKey("환불 금액"), c);
        c.gridx = 1;
        amountLabel.setFont(ModernUI.FONT_HEADING);
        amountLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(amountLabel, c);

        c.gridy = 5; c.gridx = 0;
        card.add(makeKey("적용 정책"), c);
        c.gridx = 1;
        policyLabel.setFont(ModernUI.FONT_BODY);
        policyLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(policyLabel, c);

        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0; wc.gridy = 0;
        center.add(card, wc);
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        ModernUI.styleButtonSecondary(lookupButton);
        lookupButton.addActionListener(e -> frame.showLookup());
        footer.add(lookupButton);
        ModernUI.styleButton(homeButton);
        homeButton.addActionListener(e -> frame.startNewBooking());
        footer.add(homeButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private JLabel makeKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ModernUI.FONT_SMALL);
        l.setForeground(ModernUI.TEXT_SECONDARY);
        return l;
    }

    /** 호출자가 결과 데이터를 주입. */
    public void setRefundInfo(String pnr, BigDecimal refundAmount, String policyName) {
        pnrLabel.setText(pnr != null ? pnr : "-");
        amountLabel.setText(refundAmount != null
                ? String.format("%,d KRW", refundAmount.longValue()) : "-");
        policyLabel.setText(policyName != null ? policyName : "-");
    }
}
