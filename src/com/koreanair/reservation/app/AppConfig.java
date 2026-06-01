package com.koreanair.reservation.app;

import java.util.Currency;
import java.util.Locale;

/**
 * DP#4 Singleton — 시스템 전역 환경 설정.
 *
 * <p>폰트, 표시 언어, 통화 등 UI/도메인 양쪽에서 동일하게 참조되는 옵션을 한 곳에 모은다.
 * 변경 시 등록된 Listener를 통보해 Swing UI 가 즉시 재렌더링한다.
 * <p>Double-checked locking 으로 thread-safe 초기화. 생성자는 private.
 */
public final class AppConfig {

    private static volatile AppConfig instance;

    private String fontFamily = "Pretendard";
    private int fontSize = 13;
    private Locale displayLocale = Locale.KOREA;
    private Currency currency = Currency.getInstance("KRW");
    private boolean modernTheme = true;
    private boolean showSeatMetadata = true;

    private final java.util.List<java.util.function.Consumer<AppConfig>> listeners = new java.util.ArrayList<>();

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        AppConfig local = instance;
        if (local == null) {
            synchronized (AppConfig.class) {
                local = instance;
                if (local == null) {
                    local = new AppConfig();
                    instance = local;
                }
            }
        }
        return local;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        notifyListeners();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        notifyListeners();
    }

    public Locale getDisplayLocale() {
        return displayLocale;
    }

    public void setDisplayLocale(Locale displayLocale) {
        this.displayLocale = displayLocale;
        notifyListeners();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        notifyListeners();
    }

    public boolean isModernTheme() {
        return modernTheme;
    }

    public void setModernTheme(boolean modernTheme) {
        this.modernTheme = modernTheme;
        notifyListeners();
    }

    public boolean isShowSeatMetadata() {
        return showSeatMetadata;
    }

    public void setShowSeatMetadata(boolean showSeatMetadata) {
        this.showSeatMetadata = showSeatMetadata;
        notifyListeners();
    }

    public void addChangeListener(java.util.function.Consumer<AppConfig> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeChangeListener(java.util.function.Consumer<AppConfig> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (java.util.function.Consumer<AppConfig> l : new java.util.ArrayList<>(listeners)) {
            l.accept(this);
        }
    }
}
