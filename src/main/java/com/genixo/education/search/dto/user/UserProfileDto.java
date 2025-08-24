package com.genixo.education.search.dto.user;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String profileImageUrl;

    // Location information
    private Long countryId;
    private String countryName;
    private Long provinceId;
    private String provinceName;
    private Long districtId;
    private String districtName;
    private Long neighborhoodId;
    private String neighborhoodName;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    // Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
    private String preferredLanguage;
    private String timezone;
}