package com.koreanair.reservation.app.fx;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * 앱 외곽(shell) 컨트롤러 — 상단 헤더(로고/네비), 단계 표시줄(StepIndicator),
 * State 패턴 상태 배지, 그리고 화면이 갈아끼워지는 content 영역을 관리한다.
 * Swing 의 MainFrame 헤더 + StepIndicator + StateBadge 를 합친 역할.
 */
public final class ShellController {

    @FXML private Label stateBadge;
    @FXML private HBox stepBar;
    @FXML private StackPane content;
    @FXML private Button homeBtn;
    @FXML private Button lookupBtn;
    @FXML private Button loginBtn;
    @FXML private Button logoutBtn;
    @FXML private Button settingsBtn;

    private Navigator nav;

    private static final List<String> STEP_LABELS =
            List.of("① 검색", "② 승객정보", "③ 좌석", "④ 결제", "⑤ 완료");

    @FXML
    private void initialize() {
        buildSteps();
        refreshAuth(false);
        setStateBadge(null);
    }

    void setNavigator(Navigator nav) {
        this.nav = nav;
    }

    private void buildSteps() {
        stepBar.getChildren().clear();
        for (int i = 0; i < STEP_LABELS.size(); i++) {
            Label l = new Label(STEP_LABELS.get(i));
            l.getStyleClass().add("step");
            stepBar.getChildren().add(l);
        }
    }

    // ---- 헤더 버튼 핸들러 (FXML onAction) ----
    @FXML private void onHome()   { if (nav != null) nav.startNewBooking(); }
    @FXML private void onLookup() { if (nav != null) nav.showLookup(); }
    @FXML private void onLogin()  { if (nav != null) nav.showLogin(); }
    @FXML private void onLogout() { if (nav != null) nav.doLogout(); }
    @FXML private void onSettings() { if (nav != null) nav.showSettings(); }
    @FXML private void onPatternGuide() { if (nav != null) nav.showPatternGuide(); }

    // ---- Navigator 가 호출 ----
    public void setContent(Node node) {
        content.getChildren().setAll(node);
    }

    public void refreshAuth(boolean signedIn) {
        lookupBtn.setVisible(signedIn);
        lookupBtn.setManaged(signedIn);
        logoutBtn.setVisible(signedIn);
        logoutBtn.setManaged(signedIn);
        loginBtn.setVisible(!signedIn);
        loginBtn.setManaged(!signedIn);
    }

    public void showStep(boolean visible) {
        stepBar.setVisible(visible);
        stepBar.setManaged(visible);
    }

    public void setStep(int currentStep) {
        for (int i = 0; i < stepBar.getChildren().size(); i++) {
            Node n = stepBar.getChildren().get(i);
            n.getStyleClass().remove("step-active");
            n.getStyleClass().remove("step-done");
            if (i < currentStep) n.getStyleClass().add("step-done");
            else if (i == currentStep) n.getStyleClass().add("step-active");
        }
    }

    /** State 패턴 시연 — 현재 예약 상태를 헤더 배지에 표시. */
    public void setStateBadge(String stateName) {
        if (stateName == null || stateName.isBlank()) {
            stateBadge.setText("상태 —");
            stateBadge.setVisible(false);
            stateBadge.setManaged(false);
        } else {
            stateBadge.setText("STATE · " + stateName);
            stateBadge.setVisible(true);
            stateBadge.setManaged(true);
        }
    }
}
