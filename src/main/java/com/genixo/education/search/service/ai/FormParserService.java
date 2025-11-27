package com.genixo.education.search.service.ai;

import com.genixo.education.search.dto.ai.FormDataDTO;
import com.genixo.education.search.dto.ai.FormValidationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormParserService {

    private final ObjectMapper objectMapper;
    private final RAGContextService ragContextService;

    /**
     * AI yanıtını parse et ve FormDataDTO'ya çevir
     */
    public FormDataDTO parseAIResponse(String aiResponse) {
        log.debug("Parsing AI response: {}", aiResponse);

        try {
            // JSON'u temizle (markdown code block varsa)
            String cleanJson = cleanJsonResponse(aiResponse);

            // JSON'u parse et
            FormDataDTO formData = objectMapper.readValue(cleanJson, FormDataDTO.class);

            // Doldurulma durumlarını güncelle
            updateFillStatus(formData);

            // Tamamlanma yüzdesini hesapla
            calculateCompletionPercentage(formData);

            // Minimum gereksinimleri kontrol et
            checkMinimumRequirements(formData);

            log.info("Successfully parsed AI response. Next step: {}", formData.getNextStep());
            return formData;

        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response as JSON: {}", aiResponse, e);

            // Fallback: Basit text parsing dene
            return fallbackTextParsing(aiResponse);
        }
    }

    /**
     * JSON string'i temizle (markdown, whitespace, vb.)
     */
    private String cleanJsonResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "{}";
        }

        // Markdown code block'ları temizle
        String cleaned = response.trim();
        cleaned = cleaned.replaceAll("```json\\s*", "");
        cleaned = cleaned.replaceAll("```\\s*", "");

        // İlk { ile son } arasını al
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        log.debug("Cleaned JSON: {}", cleaned);
        return cleaned.trim();
    }

    /**
     * Fallback: JSON parse edilemezse basit text parsing
     */
    private FormDataDTO fallbackTextParsing(String response) {
        log.warn("Using fallback text parsing for AI response");

        FormDataDTO formData = FormDataDTO.builder()
                .userMessage(response)
                .nextStep("unknown")
                .missingFields(new ArrayList<>())
                .build();

        return formData;
    }

    /**
     * Form alanlarının doldurulma durumunu güncelle
     */
    private void updateFillStatus(FormDataDTO formData) {
        formData.setCityFilled(formData.getCity() != null && !formData.getCity().isEmpty());
        formData.setDistrictFilled(formData.getDistrict() != null && !formData.getDistrict().isEmpty());
        formData.setInstitutionTypeGroupFilled(formData.getInstitutionTypeGroup() != null && !formData.getInstitutionTypeGroup().isEmpty());
        formData.setInstitutionTypeFilled(formData.getInstitutionType() != null && !formData.getInstitutionType().isEmpty());
        formData.setPropertyGroupFilled(formData.getSchoolPropertyGroup() != null && !formData.getSchoolPropertyGroup().isEmpty());
        formData.setPropertiesFilled(formData.getSchoolProperties() != null && !formData.getSchoolProperties().isEmpty());
        formData.setPriceFilled(formData.getMinPrice() != null || formData.getMaxPrice() != null);
    }

    /**
     * Form tamamlanma yüzdesini hesapla
     */
    private void calculateCompletionPercentage(FormDataDTO formData) {
        int totalFields = 7; // city, district, institutionTypeGroup, institutionType, properties, minPrice, maxPrice
        int filledFields = 0;

        if (Boolean.TRUE.equals(formData.getCityFilled())) filledFields++;
        if (Boolean.TRUE.equals(formData.getDistrictFilled())) filledFields++;
        if (Boolean.TRUE.equals(formData.getInstitutionTypeGroupFilled())) filledFields++;
        if (Boolean.TRUE.equals(formData.getInstitutionTypeFilled())) filledFields++;
        if (Boolean.TRUE.equals(formData.getPropertiesFilled())) filledFields++;
        if (Boolean.TRUE.equals(formData.getPriceFilled())) filledFields++;

        int percentage = (int) ((filledFields / (double) totalFields) * 100);
        formData.setCompletionPercentage(percentage);

        log.debug("Form completion: {}%", percentage);
    }

    /**
     * Minimum gereksinimleri kontrol et (city + institutionTypeGroup + institutionType)
     */
    private void checkMinimumRequirements(FormDataDTO formData) {
        boolean meetsRequirements = Boolean.TRUE.equals(formData.getCityFilled())
                && Boolean.TRUE.equals(formData.getInstitutionTypeGroupFilled())
                && Boolean.TRUE.equals(formData.getInstitutionTypeFilled());

        formData.setMeetsMinimumRequirements(meetsRequirements);

        log.debug("Meets minimum requirements: {}", meetsRequirements);
    }

    /**
     * Form data'yı validate et
     */
    public FormValidationDTO validateFormData(FormDataDTO formData) {
        log.debug("Validating form data");

        List<FormValidationDTO.ValidationError> errors = new ArrayList<>();
        List<FormValidationDTO.ValidationWarning> warnings = new ArrayList<>();

        // Zorunlu alan kontrolleri
        if (formData.getCity() == null || formData.getCity().isEmpty()) {
            errors.add(FormValidationDTO.ValidationError.builder()
                    .field("city")
                    .message("Şehir seçimi zorunludur")
                    .build());
        } else {
            // Şehir geçerli mi?
            List<String> availableCities = ragContextService.getAvailableCities();
            if (!availableCities.contains(formData.getCity())) {
                errors.add(FormValidationDTO.ValidationError.builder()
                        .field("city")
                        .message("Geçersiz şehir: " + formData.getCity())
                        .suggestedValue(findClosestMatch(formData.getCity(), availableCities))
                        .build());
            }
        }

        // İlçe kontrolü (opsiyonel ama geçerliyse doğru olmalı)
        if (formData.getDistrict() != null && !formData.getDistrict().isEmpty() && formData.getCity() != null) {
            List<String> availableDistricts = ragContextService.getDistrictsByCity(formData.getCity());
            if (!availableDistricts.contains(formData.getDistrict())) {
                errors.add(FormValidationDTO.ValidationError.builder()
                        .field("district")
                        .message("Geçersiz ilçe: " + formData.getDistrict())
                        .suggestedValue(findClosestMatch(formData.getDistrict(), availableDistricts))
                        .build());
            }
        }

        // Okul türü grubu kontrolü
        if (formData.getInstitutionTypeGroup() == null || formData.getInstitutionTypeGroup().isEmpty()) {
            errors.add(FormValidationDTO.ValidationError.builder()
                    .field("institutionTypeGroup")
                    .message("Okul türü grubu seçimi zorunludur")
                    .build());
        } else {
            List<String> availableGroups = ragContextService.getInstitutionTypeGroups();
            if (!availableGroups.contains(formData.getInstitutionTypeGroup())) {
                errors.add(FormValidationDTO.ValidationError.builder()
                        .field("institutionTypeGroup")
                        .message("Geçersiz okul türü grubu: " + formData.getInstitutionTypeGroup())
                        .suggestedValue(findClosestMatch(formData.getInstitutionTypeGroup(), availableGroups))
                        .build());
            }
        }

        // Okul türü kontrolü
        if (formData.getInstitutionType() == null || formData.getInstitutionType().isEmpty()) {
            errors.add(FormValidationDTO.ValidationError.builder()
                    .field("institutionType")
                    .message("Okul türü seçimi zorunludur")
                    .build());
        } else if (formData.getInstitutionTypeGroup() != null) {
            List<String> availableTypes = ragContextService.getInstitutionTypes(formData.getInstitutionTypeGroup());
            if (!availableTypes.contains(formData.getInstitutionType())) {
                errors.add(FormValidationDTO.ValidationError.builder()
                        .field("institutionType")
                        .message("Geçersiz okul türü: " + formData.getInstitutionType())
                        .suggestedValue(findClosestMatch(formData.getInstitutionType(), availableTypes))
                        .build());
            }
        }

        // Özellik grubu seçildiyse, en az 1 özellik seçilmeli
        if (formData.getSchoolPropertyGroup() != null && !formData.getSchoolPropertyGroup().isEmpty()) {
            if (formData.getSchoolProperties() == null || formData.getSchoolProperties().isEmpty()) {
                errors.add(FormValidationDTO.ValidationError.builder()
                        .field("schoolProperties")
                        .message("Özellik grubu seçtiniz, lütfen en az 1 özellik seçin")
                        .build());
            }
        }

        // Fiyat aralığı kontrolü
        if (formData.getMinPrice() != null && formData.getMaxPrice() != null) {
            if (formData.getMinPrice() > formData.getMaxPrice()) {
                errors.add(FormValidationDTO.ValidationError.builder()
                        .field("price")
                        .message("Minimum fiyat, maksimum fiyattan büyük olamaz")
                        .build());
            }
        }

        // Uyarılar
        if (formData.getDistrict() == null || formData.getDistrict().isEmpty()) {
            warnings.add(FormValidationDTO.ValidationWarning.builder()
                    .field("district")
                    .message("İlçe belirtmediniz, şehrin genelinde arama yapılacak")
                    .build());
        }

        if (formData.getMinPrice() == null && formData.getMaxPrice() == null) {
            warnings.add(FormValidationDTO.ValidationWarning.builder()
                    .field("price")
                    .message("Bütçe belirtmediniz, tüm fiyat aralıklarında arama yapılacak")
                    .build());
        }

        boolean isValid = errors.isEmpty();

        log.info("Validation result: {} errors, {} warnings", errors.size(), warnings.size());

        return FormValidationDTO.builder()
                .isValid(isValid)
                .errors(errors)
                .warnings(warnings)
                .build();
    }

    /**
     * En yakın eşleşmeyi bul (basit string similarity)
     */
    private String findClosestMatch(String input, List<String> options) {
        if (input == null || options == null || options.isEmpty()) {
            return null;
        }

        String closest = null;
        int minDistance = Integer.MAX_VALUE;

        String inputLower = input.toLowerCase().trim();

        for (String option : options) {
            String optionLower = option.toLowerCase().trim();

            // Eğer başlangıç eşleşiyorsa, yüksek öncelik
            if (optionLower.startsWith(inputLower)) {
                return option;
            }

            // Levenshtein distance
            int distance = levenshteinDistance(inputLower, optionLower);
            if (distance < minDistance) {
                minDistance = distance;
                closest = option;
            }
        }

        // Çok uzak eşleşmeleri döndürme
        if (minDistance > input.length()) {
            return null;
        }

        return closest;
    }

    /**
     * Levenshtein distance (string similarity)
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * İki FormDataDTO'yu birleştir (yeni veri ile eski veriyi merge et)
     */
    public FormDataDTO mergeFormData(FormDataDTO existing, FormDataDTO newData) {
        log.debug("Merging form data");

        if (existing == null) {
            return newData;
        }
        if (newData == null) {
            return existing;
        }

        // Yeni değerler varsa, onları kullan; yoksa eski değerleri koru
        FormDataDTO merged = FormDataDTO.builder()
                .city(newData.getCity() != null ? newData.getCity() : existing.getCity())
                .district(newData.getDistrict() != null ? newData.getDistrict() : existing.getDistrict())
                .institutionTypeGroup(newData.getInstitutionTypeGroup() != null ? newData.getInstitutionTypeGroup() : existing.getInstitutionTypeGroup())
                .institutionType(newData.getInstitutionType() != null ? newData.getInstitutionType() : existing.getInstitutionType())
                .schoolPropertyGroup(newData.getSchoolPropertyGroup() != null ? newData.getSchoolPropertyGroup() : existing.getSchoolPropertyGroup())
                .schoolProperties(newData.getSchoolProperties() != null && !newData.getSchoolProperties().isEmpty()
                        ? newData.getSchoolProperties()
                        : existing.getSchoolProperties())
                .minPrice(newData.getMinPrice() != null ? newData.getMinPrice() : existing.getMinPrice())
                .maxPrice(newData.getMaxPrice() != null ? newData.getMaxPrice() : existing.getMaxPrice())
                .explain(newData.getExplain() != null ? newData.getExplain() : existing.getExplain())
                .nextStep(newData.getNextStep())
                .userMessage(newData.getUserMessage())
                .missingFields(newData.getMissingFields())
                .build();

        // Durumları güncelle
        updateFillStatus(merged);
        calculateCompletionPercentage(merged);
        checkMinimumRequirements(merged);

        return merged;
    }

    /**
     * FormDataDTO'dan user-friendly özet oluştur
     */
    public String generateFormSummary(FormDataDTO formData) {
        if (formData == null) {
            return "Form henüz doldurulmamış.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("📋 Form Özeti:\n\n");

        if (formData.getCity() != null) {
            summary.append("🏙️ Şehir: ").append(formData.getCity()).append("\n");
        }
        if (formData.getDistrict() != null) {
            summary.append("📍 İlçe: ").append(formData.getDistrict()).append("\n");
        }
        if (formData.getInstitutionTypeGroup() != null) {
            summary.append("🏫 Okul Türü Grubu: ").append(formData.getInstitutionTypeGroup()).append("\n");
        }
        if (formData.getInstitutionType() != null) {
            summary.append("🎓 Okul Türü: ").append(formData.getInstitutionType()).append("\n");
        }
        if (formData.getSchoolProperties() != null && !formData.getSchoolProperties().isEmpty()) {
            summary.append("✨ Özellikler: ").append(String.join(", ", formData.getSchoolProperties())).append("\n");
        }
        if (formData.getMinPrice() != null || formData.getMaxPrice() != null) {
            summary.append("💰 Bütçe: ");
            if (formData.getMinPrice() != null) {
                summary.append(String.format("%,.0f TL", formData.getMinPrice()));
            }
            if (formData.getMinPrice() != null && formData.getMaxPrice() != null) {
                summary.append(" - ");
            }
            if (formData.getMaxPrice() != null) {
                summary.append(String.format("%,.0f TL", formData.getMaxPrice()));
            }
            summary.append("\n");
        }

        summary.append("\n").append("Tamamlanma: %").append(formData.getCompletionPercentage());

        return summary.toString();
    }
}
