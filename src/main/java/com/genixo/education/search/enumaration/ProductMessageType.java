package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum ProductMessageType {
    PRODUCT_INQUIRY("Ürün Hakkında"),
    QUOTATION_DISCUSSION("Teklif Görüşmesi"),
    ORDER_COMMUNICATION("Sipariş İletişimi"),
    SUPPORT("Destek");

    private final String label;

    ProductMessageType(String label) {
        this.label = label;
    }
}