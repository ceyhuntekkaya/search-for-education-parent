package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierUpdateDto {

    private String companyName;

    private String taxNumber;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private String address;

    private String description;

    private Boolean isActive;
}

