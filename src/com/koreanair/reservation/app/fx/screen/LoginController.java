package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.user.Member;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public final class LoginController {

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    @FXML
    private void onLogin() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String pw = passwordField.getText() == null ? "" : passwordField.getText();
        if (name.isEmpty()) {
            message.setText("이름을 입력해 주세요.");
            return;
        }
        Member m = ctx.auth.loginByName(name, pw);
        if (m == null) {
            message.setText("등록된 회원이 아닙니다. (샘플: 김정욱 / pw1234)");
            return;
        }
        message.setText("");
        nav.onLoginSuccess(m);
    }

    @FXML
    private void onGuest() {
        nav.showSearch();
    }

    @FXML
    private void onSignup() {
        nav.showRegistration();
    }
}
