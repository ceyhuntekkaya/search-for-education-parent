package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryItemUpdateDto {
    private String title;
    private String description;
    private String altText;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean isCover;
    private String tags;

    // Location
    private String locationName;
    private Double latitude;
    private Double longitude;
}