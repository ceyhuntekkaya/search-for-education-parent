package com.genixo.education.search.dto.webinar;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOrganizerUpdateDto {

    @Size(max = 200)
    private String name;

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

    private Boolean isVerified;
    private Boolean isActive;
    private String slug;
}
