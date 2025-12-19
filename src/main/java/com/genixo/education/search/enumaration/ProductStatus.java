package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE("Aktif"),
    PASSIVE("Pasif"),
    OUT_OF_STOCK("Stokta Yok"),
    DISCONTINUED("Üretimi Durdu");

    private final String label;

    ProductStatus(String label) {
        this.label = label;
    }
}
