package com.genixo.education.search.dto.appointment;

import jakarta.validation.constraints.NotBlank;
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
public class CreateParentListNoteRequest {

    private Long parentSchoolListId;

    @Size(max = 200, message = "Not başlığı en fazla 200 karakter olabilir")
    private String noteTitle;

    @NotBlank(message = "Not içeriği boş olamaz")
    @Size(max = 5000, message = "Not içeriği en fazla 5000 karakter olabilir")
    private String noteContent;

    private Boolean isImportant = false;
    private LocalDateTime reminderDate;
}