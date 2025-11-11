package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DunningManagementDto {
    private Long subscriptionId;
    private String campaignName;
    private Integer daysPastDue;
    private String dunningStage; // EARLY, MIDDLE, LATE, FINAL
    private LocalDateTime nextActionDate;
    private String nextActionType; // EMAIL, SMS, CALL, SUSPEND, CANCEL
    private Boolean isActive;

    // Communication history
    private List<String> communicationLog;
    private LocalDateTime lastCommunication;
    private String lastCommunicationType;

    // Payment promises
    private LocalDateTime paymentPromiseDate;
    private BigDecimal promisedAmount;
    private Boolean promiseKept;
}