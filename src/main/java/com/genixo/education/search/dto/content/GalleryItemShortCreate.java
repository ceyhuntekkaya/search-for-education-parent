package com.genixo.education.search.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.genixo.education.search.enumaration.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GalleryItemShortCreate {
    private MediaType itemType;
    private String fileUrl;
    private String fileName;
    private Integer sortOrder;
}
