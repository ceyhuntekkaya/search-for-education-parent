package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkContentOperationDto {
    private String operation; // DELETE, ARCHIVE, PUBLISH, MODERATE
    private List<Long> entityIds;
    private String entityType; // POST, GALLERY, GALLERY_ITEM, MESSAGE
    private Boolean validateOnly;
}