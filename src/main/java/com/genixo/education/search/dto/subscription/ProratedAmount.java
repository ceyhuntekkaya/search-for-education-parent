package com.genixo.education.search.dto.subscription;

import java.math.BigDecimal;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ProratedAmount {
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private BigDecimal chargeAmount;
    private long daysRemaining;
}
