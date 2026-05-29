package com.koreanair.reservation.app.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * KE 스타일 단계 진행 인디케이터.
 *
 * <p>검색 → 승객 → 좌석 → 결제 → 완료. 현재 단계와 완료 단계를 색·아이콘으로 구분.
 */
public class StepIndicator extends JPanel {

    public static final int STEP_SEARCH = 0;
    public static final int STEP_PASSENGER = 1;
    public static final int STEP_SEAT = 2;
    public static final int STEP_PAYMENT = 3;
    public static final int STEP_DONE = 4;

    private static final String[] LABELS = {"항공편", "탑승객", "좌석", "결제", "완료"};

    private int currentStep = STEP_SEARCH;

    public StepIndicator() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 58));
    }

    public void setCurrentStep(int step) {
        if (step < 0) step = 0;
        if (step > STEP_DONE) step = STEP_DONE;
        this.currentStep = step;
        repaint();
    }

    public int getCurrentStep() {
        return currentStep;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int n = LABELS.length;
        int w = getWidth();
        int h = getHeight();
        int circleSize = 22;
        int sideMargin = 32;
        int usable = w - sideMargin * 2;
        int step = n > 1 ? usable / (n - 1) : 0;
        int y = h / 2 - 5;

        // 연결선 (얇고 옅게)
        g2.setStroke(new BasicStroke(1.25f));
        for (int i = 0; i < n - 1; i++) {
            int x1 = sideMargin + step * i + circleSize / 2;
            int x2 = sideMargin + step * (i + 1) - circleSize / 2;
            boolean done = i < currentStep;
            g2.setColor(done ? ModernUI.KE_NAVY : ModernUI.BORDER);
            g2.drawLine(x1, y, x2, y);
        }

        // 원
        for (int i = 0; i < n; i++) {
            int x = sideMargin + step * i - circleSize / 2;
            boolean done = i < currentStep;
            boolean active = i == currentStep;
            Color fill;
            Color border;
            Color textColor;
            if (active) {
                fill = ModernUI.KE_NAVY;
                border = ModernUI.KE_NAVY;
                textColor = Color.WHITE;
            } else if (done) {
                fill = ModernUI.KE_NAVY_LIGHT;
                border = ModernUI.KE_NAVY;
                textColor = ModernUI.KE_NAVY;
            } else {
                fill = Color.WHITE;
                border = ModernUI.BORDER_STRONG;
                textColor = ModernUI.TEXT_MUTED;
            }
            g2.setColor(fill);
            g2.fillOval(x, y - circleSize / 2, circleSize, circleSize);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y - circleSize / 2, circleSize, circleSize);

            // 번호 또는 체크
            g2.setColor(textColor);
            g2.setFont(done && !active ? ModernUI.FONT_BODY_BOLD : ModernUI.FONT_BODY_BOLD);
            String marker = done && !active ? "✓" : String.valueOf(i + 1);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (circleSize - fm.stringWidth(marker)) / 2;
            int ty = y + fm.getAscent() / 2 - 2;
            g2.drawString(marker, tx, ty);

            // 라벨
            g2.setColor(active ? ModernUI.KE_NAVY : (done ? ModernUI.TEXT_PRIMARY : ModernUI.TEXT_MUTED));
            g2.setFont(active ? ModernUI.FONT_BODY_BOLD : ModernUI.FONT_SMALL);
            String label = LABELS[i];
            FontMetrics fm2 = g2.getFontMetrics();
            int lx = sideMargin + step * i - fm2.stringWidth(label) / 2;
            int ly = y + circleSize / 2 + 16;
            g2.drawString(label, lx, ly);
        }
        g2.dispose();
    }
}
