package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileUpdateDto {

    @Size(max = 100)
    private String fullName;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 50)
    private String city;

    @Size(max = 100)
    private String branch;

    @Size(max = 50)
    private String educationLevel;

    private Integer experienceYears;
    private String bio;

    @Size(max = 500)
    private String profilePhotoUrl;

    @Size(max = 500)
    private String videoUrl;

    @Size(max = 500)
    private String cvUrl;

    private Boolean isActive;
    private List<Long> provinceIds;
}
