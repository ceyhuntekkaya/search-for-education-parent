package com.genixo.education.search.dto.webinar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOrganizerCreateDto {

    @NotBlank(message = "Düzenleyen adı zorunludur")
    @Size(max = 200)
    private String name;

    @NotNull(message = "Düzenleyen tipi zorunludur")
    @Size(max = 30)
    private String type;

    @Size(max = 5000)
    private String description;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 300)
    private String website;

    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 2000)
    private String socialMediaLinks;

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isActive = true;

    private String slug; // Opsiyonel - verilmezse name'den otomatik üretilir
}
