package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryDto {
    private Long id;
    private String name;
    private String nameEn;
    private String isoCode2;
    private String isoCode3;
    private String phoneCode;
    private String currencyCode;
    private String currencySymbol;
    private String flagEmoji;
    private Double latitude;
    private Double longitude;
    private String timezone;
    private Boolean isSupported;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
}