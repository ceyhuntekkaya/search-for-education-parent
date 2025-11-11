package com.genixo.education.search.dto.content;

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
public class MessageGroupDto {

    private GroupType groupType;
    private List<MessageDto> conversations;
    private Integer totalConversations;
    private String personName;
    private Long userId;
    private LocalDateTime lastMessageDate;



    public enum GroupType {
        BY_SENDER,
        BY_ASSIGNED_TO,
        BY_SCHOOL
    }
}
