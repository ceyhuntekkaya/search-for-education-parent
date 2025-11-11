package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentCreateDto {
    private Long postId;
    private Long parentCommentId;
    private String content;
}