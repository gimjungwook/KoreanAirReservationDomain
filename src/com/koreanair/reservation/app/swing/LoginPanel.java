package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.domain.user.Member;

public class LoginPanel extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel messageLabel = new JLabel(" ");
    private final JButton loginButton = new JButton("로그인");
    private final JButton registerButton = new JButton("회원가입");

    private final MainFrame frame;
    private final AuthService authService;
    private final SwingReservationUI ui;

    public LoginPanel(MainFrame frame, AuthService authService, SwingReservationUI ui) {
        super(new BorderLayout());
        this.frame = frame;
        this.authService = authService;
        this.ui = ui;
        applyModernLook();
        buildLayout();
        wireEvents();
    }

    private void applyModernLook() {
        setBackground(ModernUI.BACKGROUND);
    }

    private void buildLayout() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ModernUI.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(48, 56, 48, 56)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel("KOREAN AIR", SwingConstants.CENTER);
        logoLabel.setFont(new Font(ModernUI.FONT_DISPLAY.getFamily(), Font.BOLD, 26));
        logoLabel.setForeground(ModernUI.KE_NAVY);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(logoLabel, c);

        JLabel title = new JLabel("대한항공 Skypass", SwingConstants.CENTER);
        title.setFont(ModernUI.FONT_TITLE);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        c.gridy = 1;
        card.add(title, c);

        JLabel subtitle = new JLabel("예약 시스템에 오신 것을 환영합니다", SwingConstants.CENTER);
        subtitle.setFont(ModernUI.FONT_SMALL);
        subtitle.setForeground(ModernUI.TEXT_SECONDARY);
        c.gridy = 2;
        card.add(subtitle, c);

        c.gridwidth = 1;
        c.gridy = 3; c.gridx = 0;
        c.insets = new Insets(24, 0, 4, 8);
        card.add(createFieldLabel("이름"), c);
        c.gridx = 1;
        c.insets = new Insets(24, 8, 4, 0);
        styleTextField(nameField);
        nameField.setText("김정욱");
        card.add(nameField, c);

        c.gridy = 4; c.gridx = 0;
        c.insets = new Insets(8, 0, 4, 8);
        card.add(createFieldLabel("비밀번호"), c);
        c.gridx = 1;
        c.insets = new Insets(8, 8, 4, 0);
        styleTextField(passwordField);
        passwordField.setText("pw1234");
        card.add(passwordField, c);

        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        c.insets = new Insets(4, 0, 0, 0);
        messageLabel.setForeground(ModernUI.ERROR);
        messageLabel.setFont(ModernUI.FONT_SMALL);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(messageLabel, c);

        styleButton(loginButton);
        c.gridy = 6;
        c.insets = new Insets(20, 0, 8, 0);
        loginButton.setPreferredSize(new Dimension(280, 48));
        card.add(loginButton, c);

        styleButtonSecondary(registerButton);
        c.gridy = 7;
        c.insets = new Insets(0, 0, 0, 0);
        registerButton.setPreferredSize(new Dimension(280, 40));
        card.add(registerButton, c);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(ModernUI.BACKGROUND);
        c.gridy = 0;
        centerWrapper.add(card, c);

        JLabel hint = new JLabel("샘플 이름: 김정욱 / 비밀번호: pw1234", SwingConstants.CENTER);
        hint.setFont(ModernUI.FONT_SMALL);
        hint.setForeground(ModernUI.TEXT_SECONDARY);
        hint.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        add(centerWrapper, BorderLayout.CENTER);
        add(hint, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernUI.FONT_SMALL);
        label.setForeground(ModernUI.TEXT_SECONDARY);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(ModernUI.FONT_BODY);
        field.setForeground(ModernUI.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setBackground(ModernUI.CARD_BG);
        field.setSelectionColor(ModernUI.PRIMARY);
        field.setCaretColor(ModernUI.PRIMARY);
    }

    private void styleButton(JButton btn) {
        btn.setFont(ModernUI.FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ModernUI.PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
    }

    private void styleButtonSecondary(JButton btn) {
        btn.setFont(ModernUI.FONT_SMALL);
        btn.setForeground(ModernUI.PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.PRIMARY, 1),
                BorderFactory.createEmptyBorder(10, 28, 10, 28)));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
    }

    private void wireEvents() {
        loginButton.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        registerButton.addActionListener(e -> {
            RegistrationDialog dialog = new RegistrationDialog(null, authService);
            dialog.setVisible(true);
        });
    }

    private void doLogin() {
        String name = nameField.getText().trim();
        String pw = new String(passwordField.getPassword());
        if (name.isEmpty()) {
            messageLabel.setText("이름을 입력해 주세요.");
            return;
        }
        Member m = authService.loginByName(name, pw);
        if (m == null) {
            messageLabel.setText("등록된 회원이 아닙니다. 회원가입을 해주세요.");
            return;
        }
        messageLabel.setText(" ");
        frame.onLoginSuccess(m);
    }

    public void focusFirst() {
        nameField.requestFocusInWindow();
    }
}