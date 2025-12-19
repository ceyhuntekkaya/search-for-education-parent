package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum RFQStatus {
    DRAFT("Taslak"),
    PUBLISHED("Yayınlandı"),
    CLOSED("Kapandı"),
    CANCELLED("İptal Edildi");

    private final String label;

    RFQStatus(String label) {
        this.label = label;
    }
}