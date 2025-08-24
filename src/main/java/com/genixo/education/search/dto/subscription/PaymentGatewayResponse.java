package com.genixo.education.search.dto.subscription;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PaymentGatewayResponse {
    private boolean successful;
    private String transactionId;
    private String gatewayName;
    private String errorMessage;
    private String rawResponse;
}