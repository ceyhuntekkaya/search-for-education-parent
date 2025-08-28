package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickAddSchoolRequest {

    private List<Long> schoolIds;
    private Long parentSchoolListId; // If null, adds to default list
    private String searchQuery; // To track which search this came from
    private Boolean markAsFavorite = false;
}
