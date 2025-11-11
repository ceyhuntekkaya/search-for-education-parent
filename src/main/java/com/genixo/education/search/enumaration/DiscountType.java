package com.genixo.education.search.enumaration;

public enum DiscountType {
    FIXED_AMOUNT,       // Sabit tutar (1000 TL)
    PERCENTAGE,         // Yüzde (10%)
    FREE_MONTHS,        // Ücretsiz ay
    BUY_X_GET_Y,        // X al Y öde
    TIERED,             // Kademeli indirim
    BUNDLE,             // Paket indirimi
    NO_DISCOUNT         // İndirim yok (diğer kampanya türleri için)
}
