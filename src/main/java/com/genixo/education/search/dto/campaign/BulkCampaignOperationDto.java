package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkCampaignOperationDto {
    private String operation; // ACTIVATE, DEACTIVATE, UPDATE_STATUS, ASSIGN_SCHOOLS, REMOVE_SCHOOLS
    private List<Long> campaignIds;
    private CampaignStatus newStatus;
    private List<Long> schoolIds;
    private String reason;
    private Boolean notifySchools;
    private Boolean notifyParents;
}