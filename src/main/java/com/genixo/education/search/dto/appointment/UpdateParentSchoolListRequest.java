package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.ListStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateParentSchoolListRequest {

    @Size(max = 100, message = "Liste adı en fazla 100 karakter olabilir")
    private String listName;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;

    private ListStatus status;

    private Boolean isDefault;

    @Size(max = 7, message = "Renk kodu geçersiz")
    private String colorCode;

    @Size(max = 50, message = "İkon adı en fazla 50 karakter olabilir")
    private String icon;

    @Size(max = 1000, message = "Notlar en fazla 1000 karakter olabilir")
    private String notes;
}