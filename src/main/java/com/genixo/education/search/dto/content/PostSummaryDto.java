package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.PostStatus;
import com.genixo.education.search.enumaration.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryDto {
    private Long id;
    private String title;
    private String slug;
    private PostType postType;
    private PostStatus status;
    private String featuredImageUrl;
    private LocalDateTime publishedAt;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private Boolean isFeatured;
    private Boolean isPinned;
    private String schoolName;
    private String authorName;
}
