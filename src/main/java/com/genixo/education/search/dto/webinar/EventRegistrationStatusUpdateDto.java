package com.genixo.education.search.dto.webinar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationStatusUpdateDto {

    @NotBlank(message = "Kayıt durumu zorunludur")
    @Size(max = 20)
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
}
