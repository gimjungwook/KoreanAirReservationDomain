package com.koreanair.reservation.app.fx.screen;

import java.util.Currency;

import com.koreanair.reservation.app.AppConfig;
import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * 전역 환경 설정 화면 — DP#5 Singleton(AppConfig).
 * 모든 변경은 AppConfig.getInstance() 한 인스턴스에 반영되고, 등록된 listener 가 즉시 UI 를 재렌더한다.
 */
public final class SettingsController {

    @FXML private ComboBox<Integer> fontSizeCombo;
    @FXML private ComboBox<String> currencyCombo;
    @FXML private CheckBox modernThemeCheck;
    @FXML private CheckBox seatMetaCheck;
    @FXML private Label message;

    private Navigator nav;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        AppConfig cfg = AppConfig.getInstance();
        if (fontSizeCombo.getItems().isEmpty()) {
            fontSizeCombo.getItems().setAll(12, 13, 14, 16, 18);
            currencyCombo.getItems().setAll("KRW", "USD");
        }
        fontSizeCombo.setValue(cfg.getFontSize());
        currencyCombo.setValue(cfg.getCurrency().getCurrencyCode());
        modernThemeCheck.setSelected(cfg.isModernTheme());
        seatMetaCheck.setSelected(cfg.isShowSeatMetadata());
        message.setText("");
    }

    @FXML
    private void onApply() {
        AppConfig cfg = AppConfig.getInstance();   // 단일 인스턴스
        if (fontSizeCombo.getValue() != null) cfg.setFontSize(fontSizeCombo.getValue());
        if (currencyCombo.getValue() != null) cfg.setCurrency(Currency.getInstance(currencyCombo.getValue()));
        cfg.setModernTheme(modernThemeCheck.isSelected());
        cfg.setShowSeatMetadata(seatMetaCheck.isSelected());   // → addChangeListener 들이 즉시 반영
        message.setText("설정이 적용되었습니다.");
    }

    @FXML
    private void onClose() { nav.showSearch(); }
}
