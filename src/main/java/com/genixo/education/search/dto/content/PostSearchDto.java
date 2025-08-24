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
public class PostSearchDto {
    private String searchTerm;
    private Long schoolId;
    private Long authorId;
    private PostType postType;
    private PostStatus status;
    private Boolean isFeatured;
    private Boolean isPinned;
    private LocalDateTime publishedAfter;
    private LocalDateTime publishedBefore;
    private String tags;
    private String hashtags;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}