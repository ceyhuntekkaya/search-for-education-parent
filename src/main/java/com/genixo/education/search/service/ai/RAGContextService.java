package com.genixo.education.search.service.ai;

import com.genixo.education.search.dto.ai.PriceRangeInfo;
import com.genixo.education.search.dto.ai.RAGContextDTO;
import com.genixo.education.search.repository.insitution.*;
import com.genixo.education.search.repository.location.DistrictRepository;
import com.genixo.education.search.repository.location.ProvinceRepository;
import com.genixo.education.search.repository.pricing.SchoolPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RAGContextService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final InstitutionTypeGroupRepository institutionTypeGroupRepository;
    private final InstitutionTypeRepository institutionTypeRepository;
    private final PropertyGroupTypeRepository propertyGroupTypeRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final SchoolPricingRepository schoolPricingRepository;


    // 1. Tüm aktif şehirleri getir
    public List<String> getAvailableCities() {
        return provinceRepository.getAllProvinceNames();
    }

    // 2. Şehre göre ilçeleri getir
    public List<String> getDistrictsByCity(String provinceName) {
        return districtRepository.getAllDistrictsNamesByProvinceName(provinceName);
    }

    // 3. Tüm okul tür gruplarını getir
    public List<String> getInstitutionTypeGroups() {
        return institutionTypeGroupRepository.getAllInstitutionTypeGroupNames();
    }

    // 4. Okul Türü  okul türlerini getir
    public List<String> getInstitutionTypes(String institutionTypeGroupName) {
        return institutionTypeRepository.getAllInstitutionTypeNamesByInstitutionTypeGroupName(institutionTypeGroupName);
    }

    // 5. Okul özelliklerini getir (kategorili) id, name
    public Map<Long, String> getSchoolPropertyGroups(String institutionTypeName) {
        return  propertyGroupTypeRepository.getSchoolPropertyGroups(institutionTypeName);
    }

    // 6. Okul özelliklerini getir (kategorili) id, name
    public Map<Long, String> getSchoolProperties(String schoolPropertyGroupName) {
        return  propertyTypeRepository.getSchoolProperties(schoolPropertyGroupName);
    }

    // 7. Fiyat aralığı istatistikleri
    public PriceRangeInfo getPriceRangeInfo(String city, String institutionType) {
        return schoolPricingRepository.getPriceStats(city, institutionType);
    }

    // 8. Tüm context'i birleştir (AI prompt için)
    public String buildDynamicContext(RAGContextDTO contextDTO) {
        StringBuilder context = new StringBuilder();

        context.append("=== KULLANICI SEÇİMLERİ VE MEVCUT SEÇENEKLER ===\n\n");

        // Şehir seçimi
        if (contextDTO.getSelectedCity() != null) {
            context.append("SEÇİLEN ŞEHİR: ").append(contextDTO.getSelectedCity()).append("\n");
            List<String> districts = getDistrictsByCity(contextDTO.getSelectedCity());
            context.append("MEVCUT İLÇELER: ").append(String.join(", ", districts)).append("\n\n");
        } else {
            context.append("ŞEHİR SEÇİMİ BEKLENİYOR\n");
            context.append("MEVCUT ŞEHİRLER: ").append(String.join(", ", getAvailableCities())).append("\n\n");
        }

        // İlçe seçimi
        if (contextDTO.getSelectedDistrict() != null) {
            context.append("SEÇİLEN İLÇE: ").append(contextDTO.getSelectedDistrict()).append("\n\n");
        }

        // Okul türü seçimi
        if (contextDTO.getSelectedInstitutionType() != null) {
            context.append("SEÇİLEN OKUL TÜRÜ: ").append(contextDTO.getSelectedInstitutionType()).append("\n");

            // Bu tür için mevcut özellikler
            Map<Long, String> propertyGroups = getSchoolPropertyGroups(contextDTO.getSelectedInstitutionType());
            context.append("\nBU OKUL TÜRÜ İÇİN MEVCUT ÖZELLİKLER:\n");
            propertyGroups.forEach((id, groupName) -> {
                context.append("- ").append(groupName).append(": ");
                Map<Long, String> properties = getSchoolProperties(groupName);
                context.append(String.join(", ", properties.values())).append("\n");
            });
            context.append("\n");
        } else {
            context.append("OKUL TÜRÜ SEÇİMİ BEKLENİYOR\n");
            context.append("MEVCUT OKUL TÜR GRUPLARI: ").append(String.join(", ", getInstitutionTypeGroups())).append("\n\n");
        }

        // Seçilen özellikler
        if (contextDTO.getSelectedProperties() != null && !contextDTO.getSelectedProperties().isEmpty()) {
            context.append("SEÇİLEN ÖZELLİKLER: ").append(String.join(", ", contextDTO.getSelectedProperties())).append("\n\n");
        }

        // Fiyat aralığı
        if (contextDTO.getMinPrice() != null || contextDTO.getMaxPrice() != null) {
            context.append("BÜTÇİ: ");
            if (contextDTO.getMinPrice() != null) {
                context.append(formatPrice(contextDTO.getMinPrice()));
            }
            if (contextDTO.getMinPrice() != null && contextDTO.getMaxPrice() != null) {
                context.append(" - ");
            }
            if (contextDTO.getMaxPrice() != null) {
                context.append(formatPrice(contextDTO.getMaxPrice()));
            }
            context.append("\n\n");
        }

        return context.toString();
    }

    /**
     * İlk sistem promptu oluştur
     */
    public String buildInitialSystemPrompt() {
        return """
            Sen bir okul arama asistanısın. Kullanıcılara Türkiye'deki okul arama sürecinde yardımcı oluyorsun.
            
            GÖREVIN:
            1. Kullanıcıdan şu bilgileri SIRA İLE ve TEK TEK topla:
               a) Şehir (ZORUNLU)
               b) İlçe (İSTEĞE BAĞLI - kullanıcı istemezse "İlçe önemli değil" derse geç)
               c) Okul Türü Grubu (ZORUNLU - Anaokulu, İlkokul, Ortaokul, Lise)
               d) Okul Türü (ZORUNLU - Seçilen gruba ait)
               e) Bütçe (İSTEĞE BAĞLI)
               f) Özellik Grubu (İSTEĞE BAĞLI)
               g) Özellikler (Grup seçildiyse ZORUNLU)
            
            ÖNEMLİ KURALLAR:
            - HER SEFERINDE SADECE 1 SORU SOR
            - Kullanıcı cevap vermeden bir sonraki soruya geçme
            - "Zorunludur" gibi baskıcı ifadeler kullanma
            - Dostça ve yönlendirici ol
            - Kullanıcı bir şeyi atlamak isterse saygı göster
            
            ÖRNEK İYİ KONUŞMA:
            - Sen: "Hangi şehirde okul arıyorsunuz?"
            - Kullanıcı: "İstanbul"
            - Sen: "Harika! İstanbul'da hangi ilçeyi tercih edersiniz? Veya şehrin genelinde arayalım mı?"
            - Kullanıcı: "Kadıköy"
            - Sen: "Anladım. Hangi seviyede okul arıyorsunuz? (Anaokulu, İlkokul, Ortaokul, Lise)"
            
            KÖTÜ ÖRNEK (YAPMA!):
            - Sen: "Şehir, ilçe, okul türü ve bütçe bilgilerini söyleyin"
            - Sen: "Şehir seçimi zorunludur!"
            
            JSON FORMATI:
            Her yanıtında şu JSON'u döndür:
            {
              "city": "kullanıcının söylediği şehir veya null",
              "district": "kullanıcının söylediği ilçe veya null",
              "institutionTypeGroup": "kullanıcının söylediği grup veya null",
              "institutionType": "kullanıcının söylediği tür veya null",
              "schoolPropertyGroup": "özellik grubu veya null",
              "schoolProperties": ["özellik1", "özellik2"] veya [],
              "minPrice": sayı veya null,
              "maxPrice": sayı veya null,
              "explain": "kullanıcıdan ek not varsa",
              "nextStep": "sıradaki adım",
              "userMessage": "kullanıcıya göstereceğin samimi mesaj"
            }
            
            nextStep DEĞERLERİ:
            - "city": Şehir soruyorsun
            - "district": İlçe soruyorsun (ama kullanıcı atlayabilir)
            - "institutionTypeGroup": Okul türü grubu soruyorsun
            - "institutionType": Okul türü soruyorsun
            - "price": Bütçe soruyorsun (kullanıcı atlayabilir)
            - "propertyGroup": Özellik grubu soruyorsun (kullanıcı atlayabilir)
            - "properties": Özellikler soruyorsun
            - "complete": Tüm bilgiler toplandı!
            
            COMPLETE OLMA ŞARTI:
            city ✓ VE institutionTypeGroup ✓ VE institutionType ✓ 
            (district, price, properties opsiyonel)
            
            ÖNEMLİ: Yanıtın SADECE JSON olmalı!
            """;
    }

    /**
     * Fiyat formatla (Türkçe format)
     */
    private String formatPrice(Double price) {
        if (price == null) {
            return "Belirtilmemiş";
        }
        return String.format("%,.0f TL/yıl", price);
    }

    /**
     * Tüm RAG context bilgilerini topla (DTO olarak)
     */
    public RAGContextDTO buildFullContext() {
        return RAGContextDTO.builder()
                .availableCities(getAvailableCities())
                .availableInstitutionTypeGroups(getInstitutionTypeGroups())
                .build();
    }

    /**
     * Context'i validate et
     */
    public boolean validateContext(String city, String district) {
        if (city == null || city.isEmpty()) {
            return false;
        }

        List<String> availableCities = getAvailableCities();
        if (!availableCities.contains(city)) {
            return false;
        }

        if (district != null && !district.isEmpty()) {
            List<String> availableDistricts = getDistrictsByCity(city);
            return availableDistricts.contains(district);
        }

        return true;
    }
}
