package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.GalleryType;
import com.genixo.education.search.enumaration.GalleryVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GallerySearchDto {
    private String searchTerm;
    private Long brandId;
    private Long campusId;
    private Long schoolId;
    private GalleryType galleryType;
    private GalleryVisibility visibility;
    private Boolean isFeatured;
    private String tags;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}