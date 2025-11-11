package com.genixo.education.search.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.genixo.education.search.enumaration.GalleryType;
import com.genixo.education.search.enumaration.GalleryVisibility;
import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GalleryCreateDto {
    private Long id;
    private Long brandId;
    private Long campusId;
    private Long schoolId;
    private String title;
    private String description;
    private GalleryType galleryType;
    private GalleryVisibility visibility;
    private String coverImageUrl;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean allowComments;
    private Boolean allowDownloads;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String tags;
    private List<GalleryItemCreateDto> items;
}

