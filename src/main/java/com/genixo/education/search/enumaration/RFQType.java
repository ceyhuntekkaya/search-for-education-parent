package com.genixo.education.search.enumaration;

import lombok.Getter;

@Getter
public enum RFQType {
    OPEN("Açık İlan"),
    INVITED("Davetiye İlan");

    private final String label;

    RFQType(String label) {
        this.label = label;
    }
}
