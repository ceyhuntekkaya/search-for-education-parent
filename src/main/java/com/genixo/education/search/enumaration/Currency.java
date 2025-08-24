package com.genixo.education.search.enumaration;

public enum Currency {
    TRY("₺", "Turkish Lira"),
    USD("$", "US Dollar"),
    EUR("€", "Euro"),
    GBP("£", "British Pound"),
    CHF("CHF", "Swiss Franc"),
    CAD("C$", "Canadian Dollar"),
    AUD("A$", "Australian Dollar"),
    JPY("¥", "Japanese Yen"),
    CNY("¥", "Chinese Yuan"),
    RUB("₽", "Russian Ruble"),
    SAR("﷼", "Saudi Riyal"),
    AED("د.إ", "UAE Dirham"),
    QAR("ر.ق", "Qatari Riyal"),
    KWD("د.ك", "Kuwaiti Dinar"),
    BHD("ب.د", "Bahraini Dinar");

    private final String symbol;
    private final String displayName;

    Currency(String symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public String getSymbol() { return symbol; }
    public String getDisplayName() { return displayName; }
}