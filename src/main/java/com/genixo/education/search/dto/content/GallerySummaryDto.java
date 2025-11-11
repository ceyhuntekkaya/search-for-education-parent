package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.GalleryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GallerySummaryDto {
    private Long id;
    private String title;
    private String slug;
    private GalleryType galleryType;
    private String coverImageUrl;
    private Long itemCount;
    private Long viewCount;
    private Boolean isFeatured;
    private String institutionName;
    private LocalDateTime createdAt;
}