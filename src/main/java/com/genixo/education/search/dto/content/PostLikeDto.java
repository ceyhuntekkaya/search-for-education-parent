package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String userFullName;
    private String userProfileImage;
    private ReactionType reactionType;
    private LocalDateTime likedAt;
}