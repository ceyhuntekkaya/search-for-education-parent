package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.CommentStatus;
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
public class PostCommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String userFullName;
    private String userProfileImage;
    private Long parentCommentId;
    private String content;
    private CommentStatus status;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private Long likeCount;
    private Long replyCount;
    private Boolean isModerated;
    private Boolean isFlagged;
    private List<PostCommentDto> replies;
    private LocalDateTime createdAt;
}
