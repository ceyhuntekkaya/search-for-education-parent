package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkOperationDto {
    private String operation; // CREATE, UPDATE, DELETE, ACTIVATE, DEACTIVATE
    private List<Long> entityIds;
    private Map<String, Object> updateData;
    private Boolean validateOnly;
}