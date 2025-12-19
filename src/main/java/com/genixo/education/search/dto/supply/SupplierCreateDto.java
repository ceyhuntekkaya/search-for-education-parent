package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierCreateDto {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Tax number is required")
    private String taxNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String address;

    private String description;

    @Builder.Default
    private Boolean isActive = true;
}

