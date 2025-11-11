package com.genixo.education.search.dto.content;

import com.genixo.education.search.dto.institution.BrandSummaryDto;
import com.genixo.education.search.dto.institution.CampusSummaryDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.dto.user.UserSummaryDto;
import com.genixo.education.search.enumaration.GalleryType;
import com.genixo.education.search.enumaration.GalleryVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryDto {
    private Long id;
    private String title;
    private String description;
    private String slug;
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

    // Stats
    private Long itemCount;
    private Long viewCount;
    private Long downloadCount;
    private Long totalSizeBytes;

    // Relationships
    private BrandSummaryDto brand;
    private CampusSummaryDto campus;
    private SchoolSummaryDto school;
    private UserSummaryDto createdByUser;
    private List<GalleryItemDto> items;
    private Boolean isActive;
    private LocalDateTime createdAt;
}