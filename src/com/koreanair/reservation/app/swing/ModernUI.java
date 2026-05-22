package com.koreanair.reservation.app.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public final class ModernUI {

    public static final Color PRIMARY = new Color(0x00, 0x5F, 0xC7);
    public static final Color PRIMARY_HOVER = new Color(0x00, 0x4A, 0xA5);
    public static final Color PRIMARY_LIGHT = new Color(0xE8, 0xF1, 0xFE);
    public static final Color SECONDARY = new Color(0x6C, 0x75, 0x80);
    public static final Color BACKGROUND = new Color(0xF0, 0xF4, 0xF8);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(0x1A, 0x1A, 0x2E);
    public static final Color TEXT_SECONDARY = new Color(0x6C, 0x75, 0x80);
    public static final Color BORDER = new Color(0xD6, 0xDF, 0xE8);
    public static final Color BORDER_FOCUS = new Color(0x00, 0x5F, 0xC7);
    public static final Color SUCCESS = new Color(0x00, 0xB8, 0x7A);
    public static final Color SUCCESS_BG = new Color(0xE8, 0xF5, 0xF1);
    public static final Color ERROR = new Color(0xE5, 0x3E, 0x3E);
    public static final Color WARNING = new Color(0xF5, 0xA6, 0x23);

    public static final Font FONT_TITLE = new Font("System", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("System", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("System", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("System", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("System", Font.PLAIN, 12);
    public static final Font FONT_MONO = new Font("Monaco", Font.PLAIN, 13);

    public static final int CORNER_RADIUS = 10;
    public static final int CORNER_RADIUS_SM = 6;
    public static final int SHADOW_OFFSET = 3;

    public static final Insets BUTTON_INSETS = new Insets(12, 28, 12, 28);
    public static final Insets FIELD_INSETS = new Insets(12, 16, 12, 16);
    public static final Insets CARD_INSETS = new Insets(28, 36, 28, 36);
    public static final Insets SEARCH_BAR_INSETS = new Insets(16, 20, 16, 20);

    public static final String CSS_NORMAL = "label { color: #6C7580; font-size: 12px; }";
    public static final String CSS_ERROR = "label { color: #E53E3E; font-size: 12px; }";

    public static void styleButton(JButton btn) {
        btn.setFont(FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
    }

    public static void styleButtonSecondary(JButton btn) {
        btn.setFont(FONT_BODY);
        btn.setForeground(PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1),
                BorderFactory.createEmptyBorder(12, 28, 12, 28)));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
    }

    public static void styleButtonSuccess(JButton btn) {
        btn.setFont(FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SUCCESS);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
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
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setBackground(Color.WHITE);
        field.setSelectionColor(PRIMARY);
        field.setCaretColor(PRIMARY);
    }

    public static void styleSearchField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        field.setBackground(Color.WHITE);
        field.setSelectionColor(PRIMARY);
        field.setCaretColor(PRIMARY);
    }

    public static void styleCard(JComponent component) {
        component.setBackground(CARD_BG);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
    }

    public static void copyToClipboard(String text) {
        if (text == null) {
            return;
        }
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }

    public static String pasteFromClipboard() {
        try {
            Object data = Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            return data != null ? data.toString() : "";
        } catch (Exception ex) {
            return "";
        }
    }

    private ModernUI() {}
}
