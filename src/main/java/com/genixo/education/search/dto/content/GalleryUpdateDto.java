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
public class GalleryUpdateDto {
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
}
