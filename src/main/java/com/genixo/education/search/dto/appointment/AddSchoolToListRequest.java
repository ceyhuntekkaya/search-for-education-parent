package com.genixo.education.search.dto.appointment;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSchoolToListRequest {

    private Long schoolId;
    private Long parentSchoolListId;
    private Integer starRating;
    private Boolean isFavorite = false;
    private Integer priorityOrder = 0;

    @Size(max = 1000, message = "Kişisel notlar en fazla 1000 karakter olabilir")
    private String personalNotes;

    @Size(max = 500, message = "Artılar en fazla 500 karakter olabilir")
    private String pros;

    @Size(max = 500, message = "Eksiler en fazla 500 karakter olabilir")
    private String cons;

    @Size(max = 200, message = "Etiketler en fazla 200 karakter olabilir")
    private String tags;

    private String addedFromSearch;
    private LocalDateTime visitPlannedDate;
}