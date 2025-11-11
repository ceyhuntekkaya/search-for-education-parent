package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateParentSchoolListRequest {

    @NotBlank(message = "Liste adı boş olamaz")
    @Size(max = 100, message = "Liste adı en fazla 100 karakter olabilir")
    private String listName;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;

    private Boolean isDefault = false;

    @Size(max = 7, message = "Renk kodu geçersiz")
    private String colorCode;

    @Size(max = 50, message = "İkon adı en fazla 50 karakter olabilir")
    private String icon;

    @Size(max = 1000, message = "Notlar en fazla 1000 karakter olabilir")
    private String notes;
}
