package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum StockTrackingType {
    UNLIMITED("Sınırsız"),
    LIMITED("Sınırlı");

    private final String label;

    StockTrackingType(String label) {
        this.label = label;
    }
}
