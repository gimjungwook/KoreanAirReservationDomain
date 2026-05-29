package com.koreanair.reservation.app.fx.screen;

import java.math.BigDecimal;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public final class RefundController {

    @FXML private Label pnrLabel;
    @FXML private Label policyLabel;
    @FXML private Label amountLabel;

    private Navigator nav;
    private AppContext ctx;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(String pnr, BigDecimal amount, String policyName) {
        pnrLabel.setText(pnr != null ? pnr : "-");
        policyLabel.setText(policyName != null ? policyName : "-");
        amountLabel.setText(amount != null ? String.format("₩%,d", amount.longValue()) : "-");
    }

    @FXML
    private void onLookup() { nav.showLookup(); }

    @FXML
    private void onNew() { nav.startNewBooking(); }
}
