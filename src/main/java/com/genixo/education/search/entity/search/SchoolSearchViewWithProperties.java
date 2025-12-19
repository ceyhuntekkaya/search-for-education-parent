package com.genixo.education.search.entity.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * School Search View WITH PROPERTIES
 * Institution properties dahil geliştirilmiş versiyon
 */
@Entity
@Table(name = "school_search_materialized_v2")
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSearchViewWithProperties {

    @Id
    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "school_slug")
    private String schoolSlug;

    @Column(name = "is_active")
    private Boolean isActive;

    // ============ KURUM TİPİ ============

    @Column(name = "institution_type_id")
    private Long institutionTypeId;

    @Column(name = "institution_type_name")
    private String institutionTypeName;

    @Column(name = "institution_type_display_name")
    private String institutionTypeDisplayName;

    @Column(name = "institution_type_description", columnDefinition = "TEXT")
    private String institutionTypeDescription;

    @Column(name = "institution_type_color")
    private String institutionTypeColor;

    @Column(name = "institution_type_icon")
    private String institutionTypeIcon;

    // ============ KAMPÜS BİLGİLERİ ============

    @Column(name = "campus_id")
    private Long campusId;

    @Column(name = "campus_name")
    private String campusName;

    @Column(name = "campus_slug")
    private String campusSlug;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "address")
    private String address;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "campus_is_subscribed")
    private Boolean campusIsSubscribed;

    // ============ YER BİLGİLERİ ============

    @Column(name = "neighborhood_id")
    private Long neighborhoodId;

    @Column(name = "neighborhood_name")
    private String neighborhoodName;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "district_slug")
    private String districtSlug;

    @Column(name = "district_type")
    private String districtType;

    @Column(name = "district_is_central")
    private Boolean districtIsCentral;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "province_slug")
    private String provinceSlug;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "plate_code")
    private String plateCode;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "iso_code_2")
    private String isoCode2;

    // ============ MARKA BİLGİSİ ============

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "brand_slug")
    private String brandSlug;

    @Column(name = "brand_logo")
    private String brandLogo;

    @Column(name = "brand_is_active")
    private Boolean brandIsActive;

    // ============ PUANLAMA VE İSTATİSTİKLER ============

    @Column(name = "rating_average")
    private Double ratingAverage;

    @Column(name = "rating_count")
    private Long ratingCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "post_count")
    private Long postCount;

    @Column(name = "campus_rating_average")
    private Double campusRatingAverage;

    @Column(name = "campus_rating_count")
    private Long campusRatingCount;

    @Column(name = "campus_view_count")
    private Long campusViewCount;

    // ============ KAPASİTE BİLGİLERİ ============

    @Column(name = "student_capacity")
    private Integer studentCapacity;

    @Column(name = "current_student_count")
    private Integer currentStudentCount;

    @Column(name = "class_size_average")
    private Integer classSizeAverage;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    // ============ ÜCRET BİLGİLERİ ============

    @Column(name = "monthly_fee")
    private Double monthlyFee;

    @Column(name = "annual_fee")
    private Double annualFee;

    @Column(name = "registration_fee")
    private Double registrationFee;

    // ============ EĞİTİM BİLGİLERİ ============

    @Column(name = "curriculum_type")
    private String curriculumType;

    @Column(name = "language_of_instruction")
    private String languageOfInstruction;

    @Column(name = "foreign_languages")
    private String foreignLanguages;

    @Column(name = "extension")
    private String extension;

    // ============ İLETİŞİM BİLGİLERİ ============

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "campus_email")
    private String campusEmail;

    @Column(name = "campus_phone")
    private String campusPhone;

    // ============ GÖRSEL VARLIKLARI ============

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "campus_logo_url")
    private String campusLogoUrl;

    @Column(name = "campus_cover_image_url")
    private String campusCoverImageUrl;

    // ============ META BİLGİLER ============

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    // ============ AÇIKLAMALAR ============

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "campus_description", columnDefinition = "TEXT")
    private String campusDescription;

    // ============ ZAMAN BİLGİLERİ ============

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============ ÖZELLİKLER (PROPERTIES) ============

    /**
     * Tüm özellikler JSON array formatında
     * Her property için detaylı bilgi içerir
     */
    @Type(JsonType.class)
    @Column(name = "properties_json", columnDefinition = "jsonb")
    private JsonNode propertiesJson;

    /**
     * Özellikler grup bazında organize edilmiş
     * Key: property_group_name, Value: properties array
     */
    @Type(JsonType.class)
    @Column(name = "properties_by_group", columnDefinition = "jsonb")
    private JsonNode propertiesByGroup;

    /**
     * Özellikler içinde arama için text
     */
    @Column(name = "properties_searchable_text", columnDefinition = "TEXT")
    private String propertiesSearchableText;

    /**
     * Aktif özellik sayısı
     */
    @Column(name = "property_count")
    private Long propertyCount;

    // ============ SCORE HESAPLAMALARI ============

    @Column(name = "popularity_score")
    private Double popularityScore;

    @Column(name = "trust_score")
    private Integer trustScore;

    @Column(name = "quality_score")
    private Double qualityScore;

    @Column(name = "activity_score")
    private Double activityScore;

    // ============ HELPER METHODS ============

    /**
     * Belirli bir özellik grubundaki özellikleri getir
     */
    public JsonNode getPropertiesByGroupName(String groupName) {
        if (propertiesByGroup == null || !propertiesByGroup.has(groupName)) {
            return null;
        }
        return propertiesByGroup.get(groupName);
    }

    /**
     * Belirli bir özelliğin değerini getir
     */
    public String getPropertyValue(String propertyName) {
        if (propertiesJson == null || !propertiesJson.isArray()) {
            return null;
        }

        for (JsonNode property : propertiesJson) {
            if (property.has("property_name") &&
                    property.get("property_name").asText().equals(propertyName)) {

                // Değeri data type'a göre al
                if (property.has("text_value") && !property.get("text_value").isNull()) {
                    return property.get("text_value").asText();
                }
                if (property.has("number_value") && !property.get("number_value").isNull()) {
                    return property.get("number_value").asText();
                }
                if (property.has("boolean_value") && !property.get("boolean_value").isNull()) {
                    return property.get("boolean_value").asText();
                }
                if (property.has("date_value") && !property.get("date_value").isNull()) {
                    return property.get("date_value").asText();
                }
            }
        }

        return null;
    }

    /**
     * Boolean özellik değerini kontrol et
     */
    public Boolean hasProperty(String propertyName) {
        if (propertiesJson == null || !propertiesJson.isArray()) {
            return false;
        }

        for (JsonNode property : propertiesJson) {
            if (property.has("property_name") &&
                    property.get("property_name").asText().equals(propertyName)) {

                if (property.has("boolean_value") && !property.get("boolean_value").isNull()) {
                    return property.get("boolean_value").asBoolean();
                }

                // Boolean olmayan property varsa true dön
                return true;
            }
        }

        return false;
    }

    /**
     * Tüm boolean özellikleri listele
     */
    public Map<String, Boolean> getBooleanProperties() {
        if (propertiesJson == null || !propertiesJson.isArray()) {
            return Map.of();
        }

        Map<String, Boolean> result = new java.util.HashMap<>();

        for (JsonNode property : propertiesJson) {
            if (property.has("data_type") &&
                    property.get("data_type").asText().equals("BOOLEAN") &&
                    property.has("property_name") &&
                    property.has("boolean_value") &&
                    !property.get("boolean_value").isNull()) {

                result.put(
                        property.get("property_name").asText(),
                        property.get("boolean_value").asBoolean()
                );
            }
        }

        return result;
    }

    /**
     * Özellik var mı kontrol et
     */
    public boolean hasAnyProperty() {
        return propertyCount != null && propertyCount > 0;
    }

    // ============ DİĞER HELPER METHODS ============

    /**
     * Logo URL (okul veya kampüs)
     */
    public String getEffectiveLogoUrl() {
        return logoUrl != null ? logoUrl : campusLogoUrl;
    }

    /**
     * Cover image URL (okul veya kampüs)
     */
    public String getEffectiveCoverImageUrl() {
        return coverImageUrl != null ? coverImageUrl : campusCoverImageUrl;
    }

    /**
     * Email (okul veya kampüs)
     */
    public String getEffectiveEmail() {
        return email != null ? email : campusEmail;
    }

    /**
     * Telefon (okul veya kampüs)
     */
    public String getEffectivePhone() {
        return phone != null ? phone : campusPhone;
    }

    /**
     * Yaş aralığı metni
     */
    public String getAgeRangeText() {
        if (minAge == null && maxAge == null) {
            return null;
        }
        if (minAge != null && maxAge != null) {
            return minAge + "-" + maxAge + " yaş";
        }
        if (minAge != null) {
            return minAge + "+ yaş";
        }
        return maxAge + " yaşa kadar";
    }

    /**
     * Ücret aralığı metni
     */
    public String getFeeRangeText() {
        if (monthlyFee == null && annualFee == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (monthlyFee != null) {
            sb.append(String.format("%.0f ₺/ay", monthlyFee));
        }
        if (annualFee != null) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(String.format("%.0f ₺/yıl", annualFee));
        }
        return sb.toString();
    }

    /**
     * Doluluk oranı (%)
     */
    public Integer getOccupancyRate() {
        if (studentCapacity == null || currentStudentCount == null || studentCapacity == 0) {
            return null;
        }
        return (int) ((currentStudentCount * 100.0) / studentCapacity);
    }

    public String getFullLocation() {
        StringBuilder sb = new StringBuilder();
        if (neighborhoodName != null) {
            sb.append(neighborhoodName).append(", ");
        }
        if (districtName != null) {
            sb.append(districtName).append(", ");
        }
        if (provinceName != null) {
            sb.append(provinceName);
        }
        return sb.toString();
    }

    public Double calculateDistance(Double targetLat, Double targetLon) {
        if (this.latitude == null || this.longitude == null ||
                targetLat == null || targetLon == null) {
            return null;
        }

        final int EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(targetLat - this.latitude);
        double lonDistance = Math.toRadians(targetLon - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(targetLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public String getRatingStars() {
        if (ratingAverage == null) {
            return "☆☆☆☆☆";
        }

        int fullStars = (int) Math.floor(ratingAverage);
        boolean hasHalfStar = (ratingAverage - fullStars) >= 0.5;

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (hasHalfStar) {
            stars.append("⯨");
        }
        while (stars.length() < 5) {
            stars.append("☆");
        }

        return stars.toString();
    }

    public String getTrustLevel() {
        if (trustScore == null) {
            return "UNKNOWN";
        }
        if (trustScore >= 100) return "VERIFIED";
        if (trustScore >= 75) return "HIGH";
        if (trustScore >= 50) return "MEDIUM";
        if (trustScore >= 25) return "LOW";
        return "UNRATED";
    }
}