package com.genixo.education.search.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.genixo.education.search.common.util.LenientLocalDateDeserializer;
import com.genixo.education.search.enumaration.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentUpdateDto {
    private Long appointmentSlotId;
    private Long schoolId;
    private Long parentUserId;

    private AppointmentType appointmentType;
    private Boolean isOnline;

    private String studentName;
    private Integer studentAge;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Optional student birth date")
    @JsonDeserialize(using = LenientLocalDateDeserializer.class)
    private LocalDate studentBirthDate;

    private String studentGender;
    private String currentSchool;
    private String gradeInterested;

    private String specialRequests;
    private String notes;
}