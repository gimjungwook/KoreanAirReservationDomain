package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.user.Member;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** 회원 가입 — AuthService.registerMember + generateSkypassNumber. */
public final class RegistrationController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    @FXML
    private void onRegister() {
        String name = txt(nameField), email = txt(emailField);
        String pw = passwordField.getText() == null ? "" : passwordField.getText();
        String pw2 = confirmField.getText() == null ? "" : confirmField.getText();
        if (name.isEmpty()) { err("이름을 입력하세요."); return; }
        if (!email.contains("@")) { err("올바른 이메일을 입력하세요."); return; }
        if (pw.length() < 4) { err("비밀번호는 4자 이상이어야 합니다."); return; }
        if (!pw.equals(pw2)) { err("비밀번호가 일치하지 않습니다."); return; }
        try {
            String skypass = ctx.auth.generateSkypassNumber();
            Member m = new Member(skypass);
            m.setName(name);
            m.setEmail(email);
            ctx.auth.registerMember(m, skypass, pw);
            message.getStyleClass().removeAll("error-text");
            if (!message.getStyleClass().contains("ok-text")) message.getStyleClass().add("ok-text");
            message.setText("가입 완료! Skypass 번호: " + skypass + " — 이 이름/비밀번호로 로그인하세요.");
        } catch (Exception ex) {
            err("가입 실패: " + ex.getMessage());
        }
    }

    @FXML
    private void onBackToLogin() { nav.showLogin(); }

    private void err(String m) {
        message.getStyleClass().removeAll("ok-text");
        if (!message.getStyleClass().contains("error-text")) message.getStyleClass().add("error-text");
        message.setText(m);
    }

    private static String txt(TextField f) { return f.getText() == null ? "" : f.getText().trim(); }
}
