package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentModerationDto {
    private Long contentId;
    private String contentType; // POST, COMMENT, GALLERY_ITEM
    private String moderationAction; // APPROVE, REJECT, FLAG, UNFLAG
    private String moderationReason;
    private String moderatorNotes;
    private Double moderationScore;
    private String moderationLabels; // JSON
}