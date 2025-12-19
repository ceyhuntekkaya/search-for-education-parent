package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQInvitationDto {

    private Long id;
    private Long rfqId;
    private String rfqTitle;
    private Long supplierId;
    private String supplierCompanyName;
    private LocalDateTime invitedAt;
}

