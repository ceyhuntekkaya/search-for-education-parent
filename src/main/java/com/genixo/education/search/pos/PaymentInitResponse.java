package com.genixo.education.search.pos;

public record PaymentInitResponse(
        boolean success,
        String iframeUrl,
        String message
) {}