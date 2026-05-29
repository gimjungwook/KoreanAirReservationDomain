package com.koreanair.reservation.app.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 * KE Theme — Iteration 4 디자인 시스템 리브랜드.
 *
 * <p>KE 브랜드 컬러(#003087 deep navy + #E4002B accent red) 기반 색·타이포·컴포넌트 헬퍼.
 * 카드형 컨테이너 + 라운드 모서리 + drop shadow + 단계 시각화 가능.
 */
public final class ModernUI {

    // KE 공식 브랜드
    public static final Color KE_NAVY = new Color(0x00, 0x30, 0x87);
    public static final Color KE_NAVY_DARK = new Color(0x00, 0x21, 0x60);
    public static final Color KE_NAVY_LIGHT = new Color(0xE8, 0xEE, 0xF7);
    public static final Color KE_RED = new Color(0xE4, 0x00, 0x2B);
    public static final Color KE_RED_DARK = new Color(0xB3, 0x00, 0x22);
    public static final Color KE_RED_LIGHT = new Color(0xFB, 0xE6, 0xEA);

    // 기능 토큰 (legacy alias 유지 — 기존 코드 호환)
    public static final Color PRIMARY = KE_NAVY;
    public static final Color PRIMARY_HOVER = KE_NAVY_DARK;
    public static final Color PRIMARY_LIGHT = KE_NAVY_LIGHT;
    public static final Color ACCENT = KE_RED;
    public static final Color ACCENT_HOVER = KE_RED_DARK;
    public static final Color SECONDARY = new Color(0x6B, 0x72, 0x80);

    public static final Color BACKGROUND = new Color(0xF5, 0xF7, 0xFA);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(0x18, 0x1F, 0x2E);
    public static final Color TEXT_SECONDARY = new Color(0x4B, 0x55, 0x63);
    public static final Color TEXT_MUTED = new Color(0x7B, 0x84, 0x92);
    public static final Color BORDER = new Color(0xE5, 0xE7, 0xEB);
    public static final Color BORDER_STRONG = new Color(0xCB, 0xD1, 0xD9);
    public static final Color BORDER_FOCUS = KE_NAVY;
    public static final Color SHADOW = new Color(0, 0, 0, 22);

    public static final Color SUCCESS = new Color(0x00, 0x9C, 0x6B);
    public static final Color SUCCESS_BG = new Color(0xE6, 0xF6, 0xEE);
    public static final Color ERROR = new Color(0xE4, 0x00, 0x2B);
    public static final Color WARNING = new Color(0xF5, 0xA6, 0x23);

    // 좌석 등급 (iter4)
    public static final Color SEAT_FIRST = new Color(0xC8, 0x9A, 0x14);
    public static final Color SEAT_FIRST_BG = new Color(0xFB, 0xF3, 0xDB);
    public static final Color SEAT_BUSINESS = KE_NAVY;
    public static final Color SEAT_BUSINESS_BG = new Color(0xDD, 0xE6, 0xF5);
    public static final Color SEAT_PREMIUM = new Color(0x6B, 0x5B, 0x95);
    public static final Color SEAT_PREMIUM_BG = new Color(0xEC, 0xE8, 0xF4);
    public static final Color SEAT_ECONOMY = new Color(0x4A, 0x55, 0x68);
    public static final Color SEAT_ECONOMY_BG = new Color(0xF1, 0xF4, 0xF8);
    public static final Color SEAT_HELD = new Color(0xC8, 0xD0, 0xDA);
    public static final Color SEAT_OCCUPIED = new Color(0x99, 0xA3, 0xB0);

    // 타이포
    private static final String FONT_FAMILY = pickFontFamily();
    public static final Font FONT_DISPLAY = new Font(FONT_FAMILY, Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 17);
    public static final Font FONT_SUBHEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Menlo", Font.PLAIN, 13);

    public static final int CORNER_RADIUS = 12;
    public static final int CORNER_RADIUS_SM = 8;
    public static final int SHADOW_OFFSET = 4;

    public static final Insets BUTTON_INSETS = new Insets(12, 24, 12, 24);
    public static final Insets FIELD_INSETS = new Insets(12, 14, 12, 14);
    public static final Insets CARD_INSETS = new Insets(24, 28, 24, 28);

    // 간격 토큰 (Codex 리뷰 1번 — 화면 간 리듬 통일)
    public static final int PAGE_PADDING = 28;
    public static final int CARD_PADDING = 24;
    public static final int SECTION_GAP = 20;
    public static final int FIELD_GAP = 12;
    public static final int FIELD_LABEL_GAP = 6;

    private static String pickFontFamily() {
        String[] candidates = {"Pretendard", "Apple SD Gothic Neo", "Noto Sans KR", "Malgun Gothic", "Helvetica Neue"};
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.Set<String> available = new java.util.HashSet<>(java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));
        for (String c : candidates) {
            if (available.contains(c)) {
                return c;
            }
        }
        return "SansSerif";
    }

    // === Buttons ===

    /** Primary CTA: KE navy 풀 채움. */
    public static void styleButton(JButton btn) {
        applyRoundButton(btn, KE_NAVY, Color.WHITE, KE_NAVY_DARK);
    }

    /** Accent CTA: KE red 풀 채움 (예약·결제 confirm). */
    public static void styleButtonAccent(JButton btn) {
        applyRoundButton(btn, KE_RED, Color.WHITE, KE_RED_DARK);
    }

    /** Secondary: 흰 배경 + navy border + navy 텍스트. */
    public static void styleButtonSecondary(JButton btn) {
        applyRoundButton(btn, Color.WHITE, KE_NAVY, KE_NAVY_LIGHT);
        btn.setBorder(new RoundedBorder(KE_NAVY, 1, CORNER_RADIUS_SM));
    }

    /** Success 변형. */
    public static void styleButtonSuccess(JButton btn) {
        applyRoundButton(btn, SUCCESS, Color.WHITE, SUCCESS.darker());
    }

    private static void applyRoundButton(JButton btn, Color bg, Color fg, Color hover) {
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.putClientProperty("hoverBg", hover);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                JButton b = (JButton) c;
                Color baseBg = b.isEnabled() ? b.getBackground() : new Color(0xCB, 0xD1, 0xD9);
                Color paintBg = baseBg;
                if (b.getModel().isRollover() && b.isEnabled()) {
                    Object h = b.getClientProperty("hoverBg");
                    if (h instanceof Color) {
                        paintBg = (Color) h;
                    }
                }
                g2.setColor(paintBg);
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), CORNER_RADIUS_SM * 2, CORNER_RADIUS_SM * 2);
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    public static void styleLabel(JLabel label) {
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);
    }

    public static void styleLabelPrimary(JLabel label) {
        label.setFont(FONT_SUBHEADING);
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 1, CORNER_RADIUS_SM),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setBackground(Color.WHITE);
        field.setSelectionColor(KE_NAVY_LIGHT);
        field.setCaretColor(KE_NAVY);
    }

    public static void styleSearchField(JTextField field) {
        styleTextField(field);
    }

    /** KE 스타일 콤보박스 — 라운드 + 패딩 + 커스텀 arrow + 행 패딩. */
    public static void styleComboBox(javax.swing.JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 1, CORNER_RADIUS_SM),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        combo.setFocusable(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▾");
                btn.setFont(FONT_BODY_BOLD);
                btn.setForeground(TEXT_SECONDARY);
                btn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                btn.setBackground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                                   Object value, int idx,
                                                                   boolean selected, boolean focused) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, idx, selected, focused);
                lbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                lbl.setFont(FONT_BODY);
                if (selected) {
                    lbl.setBackground(KE_NAVY_LIGHT);
                    lbl.setForeground(KE_NAVY);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(TEXT_PRIMARY);
                }
                return lbl;
            }
        });
    }

    public static void styleSelectableValue(JTextField field,
                                            Font font,
                                            Color foreground,
                                            Color background) {
        field.setEditable(false);
        field.setFocusable(true);
        field.setFont(font);
        field.setForeground(foreground);
        field.setBackground(background);
        field.setOpaque(true);
        field.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        field.setSelectionColor(KE_NAVY_LIGHT);
        field.setSelectedTextColor(TEXT_PRIMARY);
        field.setCaretColor(foreground);
        field.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    /** Card 컨테이너 — 라운드 + drop shadow. */
    public static void styleCard(JComponent component) {
        component.setBackground(CARD_BG);
        component.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        component.setOpaque(false);
    }

    /** JPanel을 카드로 — 페인트 시 라운드 흰 배경 + shadow. */
    public static JPanel cardPanel() {
        JPanel card = new CardPanel();
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        return card;
    }

    public static Border roundedBorder(Color color, int thickness, int radius) {
        return new RoundedBorder(color, thickness, radius);
    }

    public static void copyToClipboard(String text) {
        if (text == null) {
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }

    public static String pasteFromClipboard() {
        try {
            Object data = Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            return data != null ? data.toString() : "";
        } catch (Exception ex) {
            return "";
        }
    }

    // === 라운드 + 그림자 컴포넌트 ===

    /** 라운드 border. */
    public static final class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2,
                    w - thickness - 1, h - thickness - 1,
                    radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    /** Drop shadow border — 부드러운 두 겹 알파 fill. */
    public static final class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(x + 2, y + 4, w - 4, h - 4, CORNER_RADIUS * 2, CORNER_RADIUS * 2);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, CORNER_RADIUS * 2, CORNER_RADIUS * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c) {
            return new Insets(0, 0, SHADOW_OFFSET, SHADOW_OFFSET);
        }
    }

    /** 흰 배경 라운드 + 부드러운 그림자 카드 패널. */
    public static class CardPanel extends JPanel {
        public CardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int rad = CORNER_RADIUS * 2;
            // 부드러운 그림자 = 두 겹 alpha fill
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(2, 6, w - 4 - SHADOW_OFFSET, h - 6 - SHADOW_OFFSET, rad, rad);
            g2.setColor(new Color(0, 0, 0, 16));
            g2.fillRoundRect(2, 3, w - 4 - SHADOW_OFFSET, h - 3 - SHADOW_OFFSET, rad, rad);
            // bg
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, w - SHADOW_OFFSET, h - SHADOW_OFFSET, rad, rad);
            // border (옅게)
            g2.setColor(new Color(BORDER.getRed(), BORDER.getGreen(), BORDER.getBlue(), 120));
            g2.drawRoundRect(0, 0, w - SHADOW_OFFSET - 1, h - SHADOW_OFFSET - 1, rad, rad);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private ModernUI() {}
}
