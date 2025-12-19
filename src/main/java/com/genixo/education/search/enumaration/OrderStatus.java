package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Beklemede"),
    CONFIRMED("Onaylandı"),
    PREPARING("Hazırlanıyor"),
    SHIPPED("Kargoya Verildi"),
    DELIVERED("Teslim Edildi"),
    CANCELLED("İptal Edildi"),
    RETURNED("İade Edildi");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}
