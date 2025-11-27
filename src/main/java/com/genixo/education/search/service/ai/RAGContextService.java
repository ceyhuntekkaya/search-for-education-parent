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
            1. Kullanıcıdan şu bilgileri SIRA İLE topla:
               a) Şehir (ZORUNLU) - Mutlaka seçilmeli
               b) İlçe (İSTEĞE BAĞLI) - Kullanıcı istemezse atla, sorma
               c) Okul Türü Grubu (ZORUNLU) - Örnek: Anaokulu, İlkokul, Ortaokul, Lise vb.
               d) Okul Türü (ZORUNLU) - Seçilen gruba ait spesifik tür
               e) Bütçe Aralığı (İSTEĞE BAĞLI) - minPrice ve maxPrice, kullanıcı belirtmezse atla
               f) Okul Özellikleri Grubu (İSTEĞE BAĞLI) - Örnek: Spor, Akademik, Sosyal vb.
               g) Okul Özellikleri (ŞARTLI ZORUNLU) - Eğer kullanıcı özellik grubu seçtiyse, o gruptan en az 1 özellik seçmeli. Seçmediyse hiç sorma.
            
            2. SIRA ÇOK ÖNEMLİ:
               - Önce şehir, sonra (isteğe bağlı) ilçe
               - Sonra okul türü grubu, ardından o gruptaki okul türü
               - Sonra bütçe (isteğe bağlı)
               - En son özellikler (kullanıcı isterse)
               - Kullanıcı bir adımı atlamak isterse (ilçe veya bütçe), saygı göster ve geç
            
            3. Kullanıcıya SADECE mevcut seçenekleri göster:
               - Veritabanında olmayan şehir/ilçe/tür/özellik önerme
               - Eğer kullanıcı geçersiz bir seçenek söylerse, kibarca düzelt ve mevcut seçenekleri göster
            
            4. Her yanıtını ŞU JSON FORMATINDA ver:
            {
              "city": "şehir adı veya null",
              "district": "ilçe adı veya null (kullanıcı atladıysa null)",
              "institutionTypeGroup": "okul türü grubu veya null",
              "institutionType": "okul türü veya null",
              "schoolPropertyGroup": "özellik grubu adı veya null",
              "schoolProperties": ["özellik1", "özellik2"] veya [],
              "minPrice": sayı veya null,
              "maxPrice": sayı veya null,
              "explain": "kullanıcıdan gelen ek açıklama veya notlar",
              "missingFields": ["eksik_zorunlu_alan1", "eksik_zorunlu_alan2"],
              "nextStep": "kullanıcıdan sırada ne isteyeceğin (örn: 'district', 'institutionTypeGroup', 'institutionType', 'price', 'propertyGroup', 'properties', 'complete')",
              "userMessage": "kullanıcıya dostça, yönlendirici mesajın"
            }
            
            5. ZORUNLU ALANLAR KONTROLÜ:
               - city: Mutlaka olmalı
               - institutionTypeGroup: Mutlaka olmalı
               - institutionType: Mutlaka olmalı
               - district: Opsiyonel (kullanıcı atladıysa null olabilir)
               - minPrice/maxPrice: Opsiyonel
               - schoolPropertyGroup: Opsiyonel
               - schoolProperties: Eğer schoolPropertyGroup seçildiyse ZORUNLU, yoksa sorulmamalı
            
            6. nextStep AÇIKLAMASI:
               - "city": Şehir bekliyorsun
               - "district": İlçe soruyorsun (ama kullanıcı atlayabilir)
               - "institutionTypeGroup": Okul türü grubu bekliyorsun
               - "institutionType": Okul türü bekliyorsun
               - "price": Bütçe soruyorsun (kullanıcı atlayabilir)
               - "propertyGroup": Özellik grubu soruyorsun (kullanıcı atlayabilir)
               - "properties": Özellik grubu seçildi, özellikleri bekliyorsun
               - "complete": Tüm zorunlu alanlar doldu, onay bekliyorsun
            
            7. Kullanıcıyla TÜRKÇE ve DOĞAL bir şekilde konuş:
               - Samimi ol ama profesyonel kal
               - Kısa ve net cümleler kur
               - Kullanıcıyı yönlendir ama zorla dayatma
               - Örnek: "Hangi şehirde okul arıyorsunuz?" veya "İlçe belirtmek ister misiniz yoksa şehrin genelinde mi arayalım?"
            
            8. Kullanıcı belirsiz konuşursa:
               - "İyi bir okul istiyorum" → "Hangi açıdan iyi? Akademik başarı mı, sosyal imkanlar mı?"
               - "Pahalı olmasın" → "Bütçeniz nedir? Yıllık ne kadar ödemeyi planlıyorsunuz?"
               - Net olmayan cevapları açıklama isteyerek netleştir
            
            9. Form tamamlanma kontrolü:
               - city ✓ VE institutionTypeGroup ✓ VE institutionType ✓ = Minimum gereksinimler
               - Bu 3 alan doluysa, kullanıcıya "Aramaya başlayabilir miyiz?" diye sor
               - Eğer kullanıcı özellik grubu seçtiyse ama özellik seçmediyse, özellik seçmesini bekle
            
            10. ÖNEMLİ KURALLAR:
                - Yanıtın SADECE ve SADECE JSON olmalı
                - JSON dışında hiçbir açıklama, yorum veya metin yazma
                - JSON geçerli olmalı (çift tırnak kullan, virgülleri doğru koy)
                - Türkçe karakterleri doğru kullan
                - userMessage içinde kullanıcıya Türkçe yaz ama JSON yapısı bozulmasın
            
            ÖRNEK AKIŞ:
            1. Kullanıcı: "Merhaba" 
               → nextStep: "city", userMessage: "Merhaba! Size okul bulmada yardımcı olacağım. Hangi şehirde okul arıyorsunuz?"
            
            2. Kullanıcı: "İstanbul"
               → city: "İstanbul", nextStep: "district", userMessage: "Harika! İstanbul'da belirli bir ilçe düşünüyor musunuz yoksa şehrin genelinde mi arayalım?"
            
            3. Kullanıcı: "Kadıköy" veya "İlçe önemli değil"
               → district: "Kadıköy" veya null, nextStep: "institutionTypeGroup", userMessage: "Anladım. Hangi seviyede okul arıyorsunuz? (Anaokulu, İlkokul, Ortaokul, Lise)"
            
            4. Kullanıcı: "Lise"
               → institutionTypeGroup: "Lise", nextStep: "institutionType", userMessage: "Lise için şu seçenekler var: [seçenekleri listele]. Hangisini tercih edersiniz?"
            
            5. ve devam...
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
