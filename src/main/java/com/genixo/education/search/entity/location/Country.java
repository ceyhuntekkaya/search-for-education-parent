package com.genixo.education.search.entity.location;
import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

// ========================= COUNTRY =========================
@Entity
@Table(name = "countries")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Country extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "iso_code_2", unique = true, nullable = false, length = 2)
    private String isoCode2; // TR, US, etc.

    @Column(name = "iso_code_3", unique = true, nullable = false, length = 3)
    private String isoCode3; // TUR, USA, etc.

    @Column(name = "phone_code")
    private String phoneCode; // +90, +1, etc.

    @Column(name = "currency_code", length = 3)
    private String currencyCode; // TRY, USD, etc.

    @Column(name = "currency_symbol")
    private String currencySymbol; // ₺, $, etc.

    @Column(name = "flag_emoji")
    private String flagEmoji; // 🇹🇷, 🇺🇸, etc.

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "timezone")
    private String timezone; // Europe/Istanbul

    @Column(name = "is_supported")
    private Boolean isSupported = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // Relationships
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Province> provinces = new HashSet<>();
}
