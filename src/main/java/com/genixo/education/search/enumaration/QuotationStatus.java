package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum QuotationStatus {
    DRAFT("Taslak"),
    SUBMITTED("Gönderildi"),
    UNDER_REVIEW("İnceleniyor"),
    ACCEPTED("Kabul Edildi"),
    REJECTED("Reddedildi"),
    EXPIRED("Süresi Doldu");

    private final String label;

    QuotationStatus(String label) {
        this.label = label;
    }
}

