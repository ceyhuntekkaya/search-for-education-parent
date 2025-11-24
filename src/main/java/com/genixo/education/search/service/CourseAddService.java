package com.genixo.education.search.service;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.institution.*;
import com.genixo.education.search.entity.location.Country;
import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Neighborhood;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.InstitutionTypeGroupRepository;
import com.genixo.education.search.repository.insitution.InstitutionTypeRepository;
import com.genixo.education.search.repository.insitution.PropertyGroupTypeRepository;
import com.genixo.education.search.repository.insitution.PropertyTypeRepository;
import com.genixo.education.search.repository.location.CountryRepository;
import com.genixo.education.search.repository.location.DistrictRepository;
import com.genixo.education.search.repository.location.NeighborhoodRepository;
import com.genixo.education.search.repository.location.ProvinceRepository;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseAddService {
    /*
    private static final Locale TR_LOCALE = new Locale("tr", "TR");
    private static final Collator TR_COLLATOR = Collator.getInstance(TR_LOCALE);

    static {
        TR_COLLATOR.setStrength(Collator.PRIMARY); // Case-insensitive sorting
    }

    private final PropertyGroupTypeRepository propertyGroupTypeRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final InstitutionTypeGroupRepository institutionTypeGroupRepository;
    private final InstitutionTypeRepository institutionTypeRepository;


    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final CountryRepository countryRepository;


    private final LocationAddService locationAddService;


    public String addCourseList() {

        List<PropItem> items = List.of(

                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSAL ÖĞRENCİ SINAVLARI", "YKS – TYT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSAL ÖĞRENCİ SINAVLARI", "YKS – AYT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSAL ÖĞRENCİ SINAVLARI", "YKS – YDT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSAL ÖĞRENCİ SINAVLARI", "LGS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSAL ÖĞRENCİ SINAVLARI", "MSÜ"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "KPSS ORTAÖĞRETİM"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "KPSS ÖNLİSANS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "KPSS LİSANS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "KPSS ALAN BİLGİSİ"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "ÖABT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "EKPSS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "POMEM – PMYO – PAEM"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "ASKERİ OKUL SINAVLARI"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "BEKÇİLİK SINAVI"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "KAMU VE KARİYER SINAVLARI", "BANKA & KURUM SINAVLARI"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "ALES"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "DGS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "TUS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "DUS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "EUS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "YÖKDİL"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "YDS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "AKADEMİK VE LİSANSÜSTÜ SINAVLAR", "HAZIRLIK ATLAMA / PROFICIENCY"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "TOEFL IBT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "IELTS ACADEMIC"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "IELTS GENERAL"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "PTE ACADEMIC"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "TOEIC"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "CAMBRIDGE KET"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "CAMBRIDGE PET"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "CAMBRIDGE FCE"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "CAMBRIDGE CAE"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "CAMBRIDGE CPE"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "DUOLINGO ENGLISH TEST"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "TESTDAF"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "GOETHE (A1–C2)"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "DELF / DALF"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "CELI / CILS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "HSK"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "JLPT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI DİL SINAVLARI", "TORFL"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "SAT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "ACT"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "AP"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "IB EXAM PREP"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "IGCSE"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "A-LEVEL"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "ABITUR"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "ULUSLARARASI AKADEMİK SINAVLAR", "MATURA"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "CISCO CCNA"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "CISCO CCNP"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "AWS CERTIFICATIONS"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "MICROSOFT AZURE / MCSA"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "ORACLE / SAP"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "PMP"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "ITIL"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "CPA"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "CFA"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "CIMA"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "FRM"),
                new PropItem("SINAV HAZIRLIK KURSLARI", "SERTİFİKASYON SINAVLARI", "MYK SINAV HAZIRLIĞI"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL İNGİLİZCE"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL ALMANCA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL FRANSIZCA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL İSPANYOLCA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL İTALYANCA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL RUSÇA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL ÇİNCE (MANDARİN)"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL JAPONCA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL ARAPÇA"),
                new PropItem("YABANCI DİL KURSLARI", "GENEL DİL KURSLARI", "GENEL TÜRKÇE (YABANCILAR İÇİN)"),
                new PropItem("YABANCI DİL KURSLARI", "KONUŞMA ODAKLI KURSLAR", "İNGİLİZCE KONUŞMA KULÜBÜ"),
                new PropItem("YABANCI DİL KURSLARI", "KONUŞMA ODAKLI KURSLAR", "ALMANCA KONUŞMA PRATİĞİ"),
                new PropItem("YABANCI DİL KURSLARI", "KONUŞMA ODAKLI KURSLAR", "FRANSIZCA KONUŞMA ATÖLYESİ"),
                new PropItem("YABANCI DİL KURSLARI", "KONUŞMA ODAKLI KURSLAR", "TELAFFUZ VE AKSAN DÜZELTME"),
                new PropItem("YABANCI DİL KURSLARI", "KONUŞMA ODAKLI KURSLAR", "GÜNLÜK KONUŞMA PROGRAMI"),
                new PropItem("YABANCI DİL KURSLARI", "KONUŞMA ODAKLI KURSLAR", "İŞ İNGİLİZCESİ KONUŞMA MODÜLÜ"),
                new PropItem("YABANCI DİL KURSLARI", "İŞ DÜNYASI / MESLEKİ DİL KURSLARI", "BUSINESS ENGLISH"),
                new PropItem("YABANCI DİL KURSLARI", "İŞ DÜNYASI / MESLEKİ DİL KURSLARI", "MESLEKİ İNGİLİZCE (MEDICAL, LEGAL, TECHNICAL)"),
                new PropItem("YABANCI DİL KURSLARI", "İŞ DÜNYASI / MESLEKİ DİL KURSLARI", "AKADEMİK İNGİLİZCE"),
                new PropItem("YABANCI DİL KURSLARI", "İŞ DÜNYASI / MESLEKİ DİL KURSLARI", "SUNUM İNGİLİZCESİ (PRESENTATION ENGLISH)"),
                new PropItem("YABANCI DİL KURSLARI", "İŞ DÜNYASI / MESLEKİ DİL KURSLARI", "KURUMSAL İNGİLİZCE"),
                new PropItem("YABANCI DİL KURSLARI", "İŞ DÜNYASI / MESLEKİ DİL KURSLARI", "PROFESYONEL YAZIŞMA (EMAIL ENGLISH)"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOCUKLAR İÇİN DİL KURSLARI", "MINI ENGLISH (4–6 YAŞ)"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOCUKLAR İÇİN DİL KURSLARI", "KIDS ENGLISH (7–12 YAŞ)"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOCUKLAR İÇİN DİL KURSLARI", "TEENS ENGLISH (12–17 YAŞ)"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOCUKLAR İÇİN DİL KURSLARI", "ÇOCUK KONUŞMA KULÜBÜ"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOCUKLAR İÇİN DİL KURSLARI", "ÇOCUK KELİME VE OKUMA GELİŞİMİ"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOCUKLAR İÇİN DİL KURSLARI", "OYUN TEMELLİ YABANCI DİL"),
                new PropItem("YABANCI DİL KURSLARI", "YOĞUNLAŞTIRILMIŞ DİL PROGRAMLARI", "HIZLANDIRILMIŞ İNGİLİZCE"),
                new PropItem("YABANCI DİL KURSLARI", "YOĞUNLAŞTIRILMIŞ DİL PROGRAMLARI", "HIZLANDIRILMIŞ ALMANCA"),
                new PropItem("YABANCI DİL KURSLARI", "YOĞUNLAŞTIRILMIŞ DİL PROGRAMLARI", "YAZ OKULU DİL PROGRAMI"),
                new PropItem("YABANCI DİL KURSLARI", "YOĞUNLAŞTIRILMIŞ DİL PROGRAMLARI", "YURT DIŞI DİL HAZIRLIK (SINAVSIZ)"),
                new PropItem("YABANCI DİL KURSLARI", "YOĞUNLAŞTIRILMIŞ DİL PROGRAMLARI", "BİREBİR ÖZEL DERS DİL PROGRAMI"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOK DİLLİ EĞİTİM PROGRAMLARI", "ÇİFT-DİL PROGRAMLARI (BILINGUAL)"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOK DİLLİ EĞİTİM PROGRAMLARI", "2+ DİL AYNI ANDA ÖĞRENME PROGRAMI"),
                new PropItem("YABANCI DİL KURSLARI", "ÇOK DİLLİ EĞİTİM PROGRAMLARI", "DİL ATÖLYELERİ (HAFTALIK MODÜL)"),
                new PropItem("YABANCI DİL KURSLARI", "ÖZEL ALAN DİL PROGRAMLARI", "TURİZM İNGİLİZCESİ"),
                new PropItem("YABANCI DİL KURSLARI", "ÖZEL ALAN DİL PROGRAMLARI", "HAVACILIK İNGİLİZCESİ"),
                new PropItem("YABANCI DİL KURSLARI", "ÖZEL ALAN DİL PROGRAMLARI", "DENİZCİLİK İNGİLİZCESİ"),
                new PropItem("YABANCI DİL KURSLARI", "ÖZEL ALAN DİL PROGRAMLARI", "AKADEMİK YAZMA (ACADEMIC WRITING)"),
                new PropItem("YABANCI DİL KURSLARI", "ÖZEL ALAN DİL PROGRAMLARI", "SUNUM BECERİLERİ (PRESENTATION SKILLS)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "SCRATCH BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "BLOK TABANLI KODLAMA (KODLAMA 101)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "PYTHON BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "JAVASCRIPT BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "C# OYUN KODLAMA BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "ALGORİTMİK DÜŞÜNME"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "KODLAMAYA GİRİŞ (HER YAŞ)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "KODLAMA", "MOBİL UYGULAMA GELİŞTİRME TEMEL (HOBİ)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "ROBOTİK KODLAMA (ARDUINO)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "ROBOTİK KODLAMA (LEGO EV3 / SPIKE PRIME)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "MAKER ATÖLYESİ (3D KALEM, ELEKTRONİK SETLER)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "AKILLI CİHAZ YAPIMI (BASİT DEVRELER)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "IOT (NESNELERİN İNTERNETİ) BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "DRONE YAPIMI / DRONE ATÖLYESİ (HOBİ)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "ROBOTİK & MAKER", "SENSÖR TEKNOLOJİLERİ BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "STEM LABORATUVAR ÇALIŞMALARI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "FEN DENEY ATÖLYESİ (İLERİ SEVİYE ÇOCUK)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "MATEMATİK – ALGORİTMA ATÖLYELERİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "3D TASARIM ATÖLYESİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "3D YAZICI BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "BİLİM VE PROJE GELİŞTİRME ATÖLYESİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "STEM – BİLİM & TEKNOLOJİ", "ÇOCUK BİLİM KULÜBÜ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL OKURYAZARLIK", "BİLGİSAYAR OKURYAZARLIĞI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL OKURYAZARLIK", "DİJİTAL DÜNYA FARKINDALIĞI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL OKURYAZARLIK", "İNTERNETTE GÜVENLİ DAVRANIŞ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL OKURYAZARLIK", "TEMEL DİJİTAL BECERİLER – DOSYALAMA"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL OKURYAZARLIK", "DİJİTAL VATANDAŞLIK"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL MEDYA – ÜRETİM", "BAŞLANGIÇ GRAFİK TASARIM"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL MEDYA – ÜRETİM", "BAŞLANGIÇ VIDEO İÇERİK ÜRETİMİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL MEDYA – ÜRETİM", "FOTOĞRAF DÜZENLEME BAŞLANGIÇ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL MEDYA – ÜRETİM", "SOSYAL MEDYA İÇERİK ÜRETİMİ (HOBİ)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL MEDYA – ÜRETİM", "PODCAST BAŞLANGIÇ ATÖLYESİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "DİJİTAL MEDYA – ÜRETİM", "DİJİTAL HİKÂYE ANLATICILIĞI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "YAPAY ZEKÂ – YENİ NESİL BECERİLER", "AI FARKINDALIK EĞİTİMİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "YAPAY ZEKÂ – YENİ NESİL BECERİLER", "CHATGPT / ÜRETKEN YAPAY ZEKÂ KULLANIMI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "YAPAY ZEKÂ – YENİ NESİL BECERİLER", "VERİ OKURYAZARLIĞI (DATA LITERACY)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "YAPAY ZEKÂ – YENİ NESİL BECERİLER", "MAKİNE ÖĞRENMESİ BAŞLANGIÇ (HOBİ)"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "YAPAY ZEKÂ – YENİ NESİL BECERİLER", "DİJİTAL YARATICI DÜŞÜNME ATÖLYESİ"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "SİBER GÜVENLİK", "TEMEL SİBER GÜVENLİK"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "SİBER GÜVENLİK", "SİBER ZORBALIK FARKINDALIĞI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "SİBER GÜVENLİK", "GÜVENLİ İNTERNET KULLANIMI"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "SİBER GÜVENLİK", "ÇOCUKLAR İÇİN SİBER GÜVENLİK"),
                new PropItem("ÖZEL EĞİTİM PROGRAMLARI (IB, STEM, CODING, PORTFOLIO VB.)", "SİBER GÜVENLİK", "PAROLA VE DİJİTAL GÜVENLİK TEMEL PRENSİPLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "İŞ İNGİLİZCESİ (BUSINESS ENGLISH)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "AKADEMİK İNGİLİZCE"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "HUKUK İNGİLİZCESİ (LEGAL ENGLISH)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "TIP İNGİLİZCESİ (MEDICAL ENGLISH)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "TEKNİK İNGİLİZCE"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "HAVACILIK İNGİLİZCESİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "TURİZM İNGİLİZCESİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "DENİZCİLİK İNGİLİZCESİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "YAZILIM SEKTÖRÜ İNGİLİZCESİ (TECH ENGLISH)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEKİ DİL EĞİTİMLERİ", "E-MAIL / RAPOR YAZMA İNGİLİZCESİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "KURUMSAL İLETİŞİM BECERİLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "SUNUM TEKNİKLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "LİDERLİK VE YÖNETİCİLİK EĞİTİMLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "MÜŞTERİ İLİŞKİLERİ YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "PROFESYONEL SATIŞ TEKNİKLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "EKİP YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "İŞ ANALİZİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "PROBLEM ÇÖZME – KARAR VERME"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "KURUMSAL YAZIŞMA"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "PROFESYONEL İŞ DAVRANIŞLARI VE PROTOKOL"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "KURUMSAL EĞİTİMLER", "KARİYER PLANLAMA"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "İNSAN KAYNAKLARI UZMANLIĞI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "PERFORMANS YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "EĞİTİM VE GELİŞİM UZMANLIĞI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "İŞE ALIM – MÜLAKAT TEKNİKLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "BORDROLAMA"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "ORGANİZASYON YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İNSAN KAYNAKLARI – YÖNETİM", "ÇALIŞAN BAĞLILIĞI YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "GİRİŞİMCİLİK VE START-UP YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "MARKA YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "STRATEJİK PLANLAMA"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "PROJE YÖNETİMİ (PM)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "DİJİTAL PAZARLAMA UZMANLIĞI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "E-TİCARET PROFESYONEL EĞİTİM"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "CRM YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞLETME – GİRİŞİMİCİLİK – YÖNETİM", "KURUMSAL FİNANS TEMELLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "PMP HAZIRLIK"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "SCRUM / AGILE SERTİFİKA HAZIRLIK"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "GOOGLE SERTİFİKA PROGRAMLARI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "META – TIKTOK – LINKEDIN MARKETING SERTİFİKALARI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "CISCO CCNA TEMEL HAZIRLIK"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "MICROSOFT OFFICE SPECIALIST (MOS)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "ISO KALİTE YÖNETİM SİSTEMİ EĞİTİMLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL SERTİFİKASYON PROGRAMLARI", "VERİ ANALİTİĞİ TEMEL SERTİFİKA"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞ GÜVENLİĞİ – ZORUNLU EĞİTİM", "İŞ SAĞLIĞI VE GÜVENLİĞİ FARKINDALIK"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞ GÜVENLİĞİ – ZORUNLU EĞİTİM", "YANGIN EĞİTİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞ GÜVENLİĞİ – ZORUNLU EĞİTİM", "ACİL DURUM YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞ GÜVENLİĞİ – ZORUNLU EĞİTİM", "ERGONOMİ EĞİTİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "İŞ GÜVENLİĞİ – ZORUNLU EĞİTİM", "OFİS GÜVENLİĞİ VE RİSK FARKINDALIĞI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "BANKACILIK VE FİNANS SEKTÖRÜ EĞİTİMLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "HAVACILIK YER HİZMETLERİ UZMANLIĞI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "LOJİSTİK – TEDARİK ZİNCİRİ EĞİTİMLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "SAĞLIK SEKTÖRÜ PROFESYONEL EĞİTİMLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "TURİZM VE OTELCİLİK YÖNETİMİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "EMLAK DANIŞMANLIĞI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "MESLEK BAZLI EĞİTİMLER", "MEDYA – İLETİŞİM PROFESYONEL EĞİTİMLERİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL İLETİŞİM & KİŞİSEL MARKA", "DİKSİYON (PROFESYONEL)"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL İLETİŞİM & KİŞİSEL MARKA", "İKNA VE MÜZAKERE"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL İLETİŞİM & KİŞİSEL MARKA", "KİŞİSEL MARKA OLUŞTURMA"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL İLETİŞİM & KİŞİSEL MARKA", "SOSYAL MEDYANIN PROFESYONEL KULLANIMI"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL İLETİŞİM & KİŞİSEL MARKA", "KURUMSAL BEDEN DİLİ"),
                new PropItem("MESLEKİ DİL VE İŞ HAYATI KURSLARI", "PROFESYONEL İLETİŞİM & KİŞİSEL MARKA", "PROFESYONEL İÇERİK ÜRETİMİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "FITNESS TEMEL EĞİTİMİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "FONKSİYONEL ANTRENMAN"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "KİŞİSEL ANTRENMAN (PERSONAL TRAINING)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "KONDİSYON GELİŞTİRME"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "VÜCUT GELİŞTİRME (AMATÖR)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "KARDİYO – INTERVAL TRAINING"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "FITNESS VE FONKSİYONEL ANTRENMAN", "CROSS TRAINING BAŞLANGIÇ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "PILATES – YOGA – ESNEME", "MAT PILATES"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "PILATES – YOGA – ESNEME", "REFORMER PILATES"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "PILATES – YOGA – ESNEME", "HAMİLE PILATESİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "PILATES – YOGA – ESNEME", "YOGA (VINYASA / HATHA / YIN)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "PILATES – YOGA – ESNEME", "ESNEME & MOBİLİTE"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "PILATES – YOGA – ESNEME", "NEFES TEKNİKLERİ (BREATHWORK)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "KARATE"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "TAEKWONDO"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "AIKIDO"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "WING CHUN"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "KICKBOKS"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "MUAY THAI (HOBİ)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "BOKS"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "MMA BAŞLANGIÇ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAVUNMA SPORLARI", "KENDİNİ SAVUNMA (SELF-DEFENSE)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SU SPORLARI", "YÜZME BAŞLANGIÇ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SU SPORLARI", "SERBEST YÜZME TEKNİK GELİŞTİRME"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SU SPORLARI", "SU TOPU BAŞLANGICI"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SU SPORLARI", "DALIŞ EĞİTİMİ (HOBİ)"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SU SPORLARI", "SERBEST DALIŞ FARKINDALIĞI"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "RAKET SPORLARI", "TENİS"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "RAKET SPORLARI", "MASA TENİSİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "RAKET SPORLARI", "BADMİNTON"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "RAKET SPORLARI", "SQUASH"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "TAKIM SPORLARI", "FUTBOL TEMEL EĞİTİM"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "TAKIM SPORLARI", "BASKETBOL TEMEL EĞİTİM"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "TAKIM SPORLARI", "VOLEYBOL TEMEL EĞİTİM"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "TAKIM SPORLARI", "HENTBOL BAŞLANGIÇ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "ÇOCUK SPOR PROGRAMLARI", "ÇOCUK SPOR ATÖLYELERİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "ÇOCUK SPOR PROGRAMLARI", "ÇOCUK JİMNASTİK"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "ÇOCUK SPOR PROGRAMLARI", "MOTOR BECERİ GELİŞİMİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "ÇOCUK SPOR PROGRAMLARI", "ÇOCUK KOORDİNASYON EĞİTİMİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "ÇOCUK SPOR PROGRAMLARI", "ÇOCUK KOŞU & HAREKET"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAĞLIK – WELLNESS", "POSTÜR DÜZELTME"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAĞLIK – WELLNESS", "DENGE – KOORDİNASYON ÇALIŞMALARI"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAĞLIK – WELLNESS", "SAĞLIKLI YAŞAM KOÇLUĞU"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAĞLIK – WELLNESS", "BESLENME FARKINDALIK"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAĞLIK – WELLNESS", "MEDITASYON"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "SAĞLIK – WELLNESS", "MIND–BODY WELLNESS"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "OUTDOOR – DOĞA SPORLARI", "TREKKING – DOĞA YÜRÜYÜŞÜ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "OUTDOOR – DOĞA SPORLARI", "KAMPÇILIK TEMEL EĞİTİMİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "OUTDOOR – DOĞA SPORLARI", "BİSİKLET SÜRÜŞ TEKNİKLERİ"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "OUTDOOR – DOĞA SPORLARI", "OKÇULUK"),
                new PropItem("SPOR VE SAĞLIK / FİTNESS KURSLARI", "OUTDOOR – DOĞA SPORLARI", "ORIENTEERING (YÖN BULMA)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "RESİM (KARAKALEM – SULUBOYA – YAĞLI BOYA)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "DİJİTAL RESİM"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "HEYKEL"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "SERAMİK – ÇÖMLEK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "KALİGRAFİ – HAT SANATLARI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "İLLÜSTRASYON"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "KARİKATÜR – ÇİZGİ ROMAN ÇİZİMİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "TAKI TASARIM (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "ORİGAMİ – KAĞIT SANATLARI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "MOZAİK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SANAT KURSLARI", "AHŞAP BOYAMA – HOBİ AHŞAP"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "PİYANO"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "GİTAR (AKUSTİK – ELEKTRO – KLASİK)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "KEMAN"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "ÇELLO"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "BATERİ – PERKÜSYON"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "BAĞLAMA"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "YAN FLÜT"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "KLARNET"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "ŞAN EĞİTİMİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "MÜZİK TEORİ – SOLFEJ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MÜZİK KURSLARI", "ÇOCUK ERKEN MÜZİK EĞİTİMİ (ORFF)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "LATİN DANSLARI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "SALON DANSLARI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "MODERN DANS"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "HİP-HOP"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "ZUMBA"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "BALE"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "HALK DANSLARI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DANS KURSLARI", "ÇOCUK DANS EĞİTİMLERİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "PİLATES"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "YOGA"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "FITNESS"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "BOKS"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "KİCKBOKS"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "MMA (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "YÜZME"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "TENİS"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "MASA TENİSİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "OKÇULUK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "JİMNASTİK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "ÇOCUK SPOR ATÖLYELERİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "SPOR & FITNESS KURSLARI", "DAĞCILIK – DOĞA SPORLARI TEMEL EĞİTİMLERİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "DİKİŞ – NAKIŞ (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "PATCHWORK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "AMİGURUMİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "KEÇE TASARIM"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "SABUN YAPIMI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "MUM YAPIMI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "EV DEKORASYON ATÖLYESİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "PUNCH NAKIŞ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "EL SANATLARI / ATÖLYELER", "DERİ AKSESUAR YAPIMI (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "ZAMAN YÖNETİMİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "DİKSİYON – ETKİLİ KONUŞMA"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "BEDEN DİLİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "HIZLI OKUMA (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "MINDFULNESS"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "STRES YÖNETİMİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "HAFIZA TEKNİKLERİ (KİŞİSEL KULLANIM)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "DRAMA KURSU (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "KİŞİSEL GELİŞİM", "TİYATRO BAŞLANGIÇ KURSU"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "FOTOĞRAF – VIDEO – MEDYA", "TEMEL FOTOĞRAFÇILIK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "FOTOĞRAF – VIDEO – MEDYA", "MOBİL FOTOĞRAFÇILIK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "FOTOĞRAF – VIDEO – MEDYA", "VIDEO ÇEKİM TEMELLERİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "FOTOĞRAF – VIDEO – MEDYA", "KISA FİLM ATÖLYESİ (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MUTFAK & GASTRONOMİ (HOBİ)", "EV PASTACILIĞI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MUTFAK & GASTRONOMİ (HOBİ)", "EKMEK YAPIMI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MUTFAK & GASTRONOMİ (HOBİ)", "ÇİKOLATA ATÖLYESİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MUTFAK & GASTRONOMİ (HOBİ)", "BARİSTA (HOBİ)"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MUTFAK & GASTRONOMİ (HOBİ)", "HOBİ DÜNYA MUTFAKLARI"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "MUTFAK & GASTRONOMİ (HOBİ)", "ÇOCUK MUTFAK ATÖLYESİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DOĞA – HAYAT TARZI", "BAHÇECİLİK"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DOĞA – HAYAT TARZI", "BİTKİ YETİŞTİRİCİLİĞİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DOĞA – HAYAT TARZI", "KAMPÇILIK TEMEL EĞİTİMLERİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DOĞA – HAYAT TARZI", "BALIKÇILIK EĞİTİMİ"),
                new PropItem("HOBİ VE KİŞİSEL GELİŞİM KURSLARI", "DOĞA – HAYAT TARZI", "HAYVAN BAKIMI – PET CARE (HOBİ)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "YAZILIM GELİŞTİRME"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "WEB GELİŞTİRME (FRONTEND – BACKEND – FULL STACK)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "MOBİL UYGULAMA GELİŞTİRME (IOS – ANDROID)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "VERİ BİLİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "YAPAY ZEKÂ VE MAKİNE ÖĞRENİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "SİBER GÜVENLİK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "OYUN GELİŞTİRME (UNITY, UNREAL)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "BULUT BİLİŞİM (AWS, AZURE, GOOGLE CLOUD)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "VERİ TABANI YÖNETİMİ (SQL, NOSQL)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "DEVOPS – CI/CD"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "BİLGİ TEKNOLOJİLERİ – YAZILIM", "ROBOTİK SÜREÇ OTOMASYONU (RPA)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "OFİS VE BÜRO YÖNETİMİ", "BİLGİSAYAR İŞLETMENLİĞİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "OFİS VE BÜRO YÖNETİMİ", "MICROSOFT OFFICE (WORD, EXCEL, POWERPOINT)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "OFİS VE BÜRO YÖNETİMİ", "DİJİTAL ARŞİVLEME VE EVRAK YÖNETİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "OFİS VE BÜRO YÖNETİMİ", "SEKRETERLİK VE YÖNETİCİ ASİSTANLIĞI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "GRAFİK TASARIM"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "UI/UX TASARIM"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "WEB TASARIM"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "VIDEO MONTAJ (PREMIERE – FINAL CUT)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "MOTION DESIGN (AFTER EFFECTS)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "3D MODELLEME (BLENDER, MAYA, 3DS MAX)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "DİJİTAL İLLÜSTRASYON"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "FOTOĞRAFÇILIK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "SOSYAL MEDYA İÇERİK ÜRETİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GRAFİK – TASARIM – DİJİTAL MEDYA", "DRONE KULLANIM SERTİFİKASI (İHA0–İHA1)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "GENEL MUHASEBE"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "BİLGİSAYARLI MUHASEBE (LOGO, MİKRO, ETA)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "İNSAN KAYNAKLARI YÖNETİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "FİNANSAL ANALİZ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "E-TİCARET VE E-İHRACAT"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "PAZARLAMA VE SATIŞ TEKNİKLERİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MUHASEBE – FİNANS – İŞLETME", "GİRİŞİMCİLİK KURSLARI (KOSGEB ONAYLI)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "SAĞLIK VE BAKIM", "İLK YARDIM EĞİTİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "SAĞLIK VE BAKIM", "HASTA VE YAŞLI BAKIM KURSU"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "SAĞLIK VE BAKIM", "ÇOCUK GELİŞİMİ DESTEK EĞİTİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "SAĞLIK VE BAKIM", "TIBBİ SEKRETERLİK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "SAĞLIK VE BAKIM", "ECZANE YARDIMCILIĞI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "SAĞLIK VE BAKIM", "AĞIZ VE DİŞ SAĞLIĞI DESTEK ELEMANI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "KUAFÖRLÜK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "CİLT BAKIMI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "MAKYAJ ARTİSTLİĞİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "PROTEZ TIRNAK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "EPİLASYON – LAZER SİSTEMLERİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "MASAJ TEKNİKLERİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GÜZELLİK – ESTETİK – KİŞİSEL BAKIM", "SAÇ TASARIM UZMANLIĞI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "AŞÇILIK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "PASTACILIK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "EKMEKÇİLİK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "BARİSTA EĞİTİMİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "BAR VE İÇECEK HAZIRLAMA"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "DÜNYA MUTFAKLARI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "GASTRONOMİ – MUTFAK SANATLARI", "HİJYEN VE SANİTASYON"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "ELEKTRİK – ELEKTRONİK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "KAYNAKÇILIK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "CNC OPERATÖRLÜĞÜ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "3D YAZICI OPERATÖRLÜĞÜ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "MEKANİK BAKIM"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "OTOMOTİV TEKNİSYENLİĞİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "ROBOTİK OTOMASYON"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TEKNİK – MEKANİK – ENDÜSTRİYEL", "FORKLİFT OPERATÖRLÜĞÜ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MODA – TEKSTİL – EL SANATLARI", "STİLİSTLİK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MODA – TEKSTİL – EL SANATLARI", "MODELİSTLİK"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MODA – TEKSTİL – EL SANATLARI", "DİKİŞ – NAKIŞ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MODA – TEKSTİL – EL SANATLARI", "TEKSTİL TASARIMI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MODA – TEKSTİL – EL SANATLARI", "MODA ÜRETİM SÜREÇLERİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "MODA – TEKSTİL – EL SANATLARI", "DERİ ÇANTA – AKSESUAR YAPIMI"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TURİZM – ULAŞTIRMA – HİZMET", "TURİZM REHBERLİĞİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TURİZM – ULAŞTIRMA – HİZMET", "OTELCİLİK VE ÖN BÜRO"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TURİZM – ULAŞTIRMA – HİZMET", "KABİN MEMURLUĞU (HOSTESLİK)"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TURİZM – ULAŞTIRMA – HİZMET", "KARA YOLU TAŞIMACILIĞI – SRC"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TURİZM – ULAŞTIRMA – HİZMET", "HAVALİMANI YER HİZMETLERİ"),
                new PropItem("MESLEKİ VE TEKNİK KURSLAR", "TURİZM – ULAŞTIRMA – HİZMET", "SERVİS ELEMANI EĞİTİMİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "TÜRKÇE – DİL BECERİLERİ", "TÜRKÇE DERS DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "TÜRKÇE – DİL BECERİLERİ", "DİL BİLGİSİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "TÜRKÇE – DİL BECERİLERİ", "PARAGRAF OKUMA-ANLAMA"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "TÜRKÇE – DİL BECERİLERİ", "YAZMA BECERİLERİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "MATEMATİK", "TEMEL MATEMATİK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "MATEMATİK", "ORTAOKUL MATEMATİK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "MATEMATİK", "LİSE MATEMATİK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "MATEMATİK", "FONKSİYONLAR"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "MATEMATİK", "LİMİT – TÜREV – İNTEGRAL"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "MATEMATİK", "PROBLEM ÇÖZME TEKNİKLERİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "FEN BİLİMLERİ", "ORTAOKUL FEN"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "FEN BİLİMLERİ", "FİZİK DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "FEN BİLİMLERİ", "KİMYA DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "FEN BİLİMLERİ", "BİYOLOJİ DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "SOSYAL BİLİMLER", "SOSYAL BİLGİLER"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "SOSYAL BİLİMLER", "T.C. İNKILAP TARİHİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "SOSYAL BİLİMLER", "TARİH"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "SOSYAL BİLİMLER", "COĞRAFYA"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YABANCI DİL (OKUL TAKVİYESİ)", "İNGİLİZCE OKUL DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YABANCI DİL (OKUL TAKVİYESİ)", "ALMANCA OKUL DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YABANCI DİL (OKUL TAKVİYESİ)", "FRANSIZCA OKUL DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ETÜT VE ÖDEV TAKİP", "BİREBİR ETÜT"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ETÜT VE ÖDEV TAKİP", "GRUP ETÜT"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ETÜT VE ÖDEV TAKİP", "ÖDEV TAKİP PROGRAMI"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ETÜT VE ÖDEV TAKİP", "YAZILI HAZIRLIK ÇALIŞMALARI"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ETÜT VE ÖDEV TAKİP", "ÖĞRENME EKSİKLERİ TAMAMLAMA"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YETİŞTİRME PROGRAMLARI", "OKUMA-YAZMA GÜÇLENDİRME"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YETİŞTİRME PROGRAMLARI", "TEMEL BECERİ GELİŞTİRME"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YETİŞTİRME PROGRAMLARI", "AKADEMİK BAŞARI GELİŞTİRME"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "YETİŞTİRME PROGRAMLARI", "ÜSTÜN YETENEKLİ ÖĞRENCİ DESTEK PROGRAMI"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "HIZLANDIRILMIŞ PROGRAMLAR", "YARIYIL TATİLİ HIZLANDIRILMIŞ KAMP"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "HIZLANDIRILMIŞ PROGRAMLAR", "YAZ OKULU AKADEMİK TAKVİYE"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "HIZLANDIRILMIŞ PROGRAMLAR", "HAFTASONU AKADEMİK DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "HIZLANDIRILMIŞ PROGRAMLAR", "TEMELDEN HIZLANDIRILMIŞ TÜRKÇE/MATEMATİK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ÖĞRENME BECERİLERİ", "HIZLI OKUMA"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ÖĞRENME BECERİLERİ", "HAFIZA TEKNİKLERİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ÖĞRENME BECERİLERİ", "ETKİN NOT TUTMA"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ÖĞRENME BECERİLERİ", "ZAMAN YÖNETİMİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "ÖĞRENME BECERİLERİ", "ÇALIŞMA BECERİLERİ EĞİTİMİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "STEM – BİLİM – PROJE", "STEM DESTEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "STEM – BİLİM – PROJE", "FEN BİLİMLERİ DENEY ATÖLYELERİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "STEM – BİLİM – PROJE", "KODLAMA TEMELLİ OKUL TAKVİYESİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "STEM – BİLİM – PROJE", "TÜBİTAK PROJE HAZIRLIK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "ÜSTÜN YETENEKLİ GELİŞİM PROGRAMI"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "DİSİPLİNLER ARASI BİLİM UYGULAMALARI"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "İLERİ SEVİYE ROBOTİK-KODLAMA"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "MATEMATİK ÖZEL YETENEK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "YARATICI YAZARLIK"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "GÖRSEL SANATLAR ÖZEL YETENEK EĞİTİMİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "MÜZİK ÖZEL YETENEK EĞİTİMİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "PROJE TABANLI ÖĞRENME"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "BİLİMSEL ARAŞTIRMA TEKNİKLERİ"),
                new PropItem("AKADEMİK / TAKVİYE VE YETİŞTİRME KURSLARI", "BİLİM VE SANAT (BİLSEM)", "BİLSEM HAZIRLIK PROGRAMI")
        );

        List<String> uniqueGroupTypes = getUniqueGroupTypes(items);

        if (!uniqueGroupTypes.isEmpty()) {
            return "hata";
        }

        for (int i = 0; i < 10000; i++) {
            try {
                InstitutionTypeGroup institutionTypeGroup = addInstitutionTypeGroup("Kurslar", 5);

                for (String groupType : uniqueGroupTypes) {
                    InstitutionType institutionType = addInstitutionType(groupType, institutionTypeGroup);

                    List<String> propertyTypesByGroupType = getPropertyTypesByGroupType(items, groupType);

                    for (String propertyTypesGroup : propertyTypesByGroupType) {

                        PropertyGroupType propertyGroupType = addPropertyGroupType(propertyTypesGroup, institutionType);
                        List<String> propertiesByPropertyType = getPropertiesByPropertyType(items, propertyTypesGroup, groupType);

                        for (String propertyTypes : propertiesByPropertyType) {
                            PropertyType propertyType = addPropertyType(propertyTypes, propertyGroupType);
                            System.out.println(propertyType.getName());
                        }

                    }
                }
            } catch (Exception e) {
                continue;
            }
            break;
        }


        return "OK";
    }


    private InstitutionTypeGroup addInstitutionTypeGroup(String name, Integer orderNumber) {
        InstitutionTypeGroup institutionTypeGroup = new InstitutionTypeGroup();
        institutionTypeGroup.setName(name);
        institutionTypeGroup.setDisplayName(name);
        institutionTypeGroup.setDescription(name);
        institutionTypeGroup.setIconUrl("default.png");
        institutionTypeGroup.setColorCode("#ff0000");
        institutionTypeGroup.setSortOrder(orderNumber);
        institutionTypeGroup.setDefaultProperties("{\"age_range\": \"3-6\", \"education_level\": \"preschool\", \"curriculum_type\": \"play_based\"}");

        List<InstitutionTypeGroup> check = institutionTypeGroupRepository.checkIfExist(name);
        if (check.isEmpty()) {
            return institutionTypeGroupRepository.saveAndFlush(institutionTypeGroup);
        }
        return check.get(0);
    }

    private InstitutionType addInstitutionType(String name, InstitutionTypeGroup institutionTypeGroup) {
        InstitutionType institutionType = new InstitutionType();

        institutionType.setName(name);
        institutionType.setDisplayName(name);
        institutionType.setDescription(name);
        institutionType.setIconUrl("default.png");
        institutionType.setColorCode("#ff0000");
        institutionType.setGroup(institutionTypeGroup);
        institutionType.setSortOrder(1);
        institutionType.setDefaultProperties("{\"age_range\": \"3-6\", \"education_level\": \"preschool\", \"curriculum_type\": \"play_based\"}");

        //private Set<InstitutionProperty> properties = new HashSet<>();

        List<InstitutionType> check = institutionTypeRepository.checkIfExist(name, institutionTypeGroup.getId());

        if (check.isEmpty()) {
            return institutionTypeRepository.saveAndFlush(institutionType);
        }
        return check.get(0);

    }

    private PropertyGroupType addPropertyGroupType(String name, InstitutionType institutionType) {
        PropertyGroupType propertyGroupType = new PropertyGroupType();

        propertyGroupType.setName(name);
        propertyGroupType.setDisplayName(name);
        propertyGroupType.setInstitutionType(institutionType);
        propertyGroupType.setSortOrder(1);
        propertyGroupType.setIsMultiple(false);

        List<PropertyGroupType> check = propertyGroupTypeRepository.checkIfExist(name, institutionType.getId());

        if (check.isEmpty()) {
            return propertyGroupTypeRepository.saveAndFlush(propertyGroupType);
        }
        return check.get(0);


    }

    private PropertyType addPropertyType(String name, PropertyGroupType propertyGroupType) {
        PropertyType propertyType = new PropertyType();
        propertyType.setName(name);
        propertyType.setDisplayName(name);
        propertyType.setPropertyGroupType(propertyGroupType);
        propertyType.setSortOrder(1);

        List<PropertyType> check = propertyTypeRepository.checkIfExist(name, propertyGroupType.getId());

        if (check.isEmpty()) {
            return propertyTypeRepository.saveAndFlush(propertyType);
        }
        return check.get(0);


    }

    public List<String> getUniqueGroupTypes(List<PropItem> items) {
        return items.stream()
                .map(PropItem::getNameGroupType)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getPropertyTypesByGroupType(List<PropItem> items, String groupType) {
        return items.stream()
                .filter(item -> item.getNameGroupType().equals(groupType))
                .map(PropItem::getNamePropertyType)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getPropertiesByPropertyType(List<PropItem> items, String propertyType, String groupType) {
        return items.stream()
                .filter(item -> item.getNamePropertyType().equals(propertyType) && item.getNameGroupType().equals(groupType))
                .map(PropItem::getNameProperty)
                .distinct()
                .collect(Collectors.toList());
    }


    public String addLocationList() {

        List<LocationData> data = locationAddService.loadLocationsFromCsv();
        int i = 0;
        int j = 0;
        Country country = countryRepository.findById(1L).orElse(null);
        if (country != null) {
            List<String> sehirler = locationAddService.getUniqueCities();
            List<Province> provinceList = new ArrayList<>();
            for (String provice : sehirler) {
                provice = capitalizeWords(provice);
                Province p = addProvince(country, provice);
                provinceList.add(p);
                List<String> ilceler = locationAddService.getUniqueDistricts(provice);
                Set<District> districtList = new HashSet<>();
                for (String district : ilceler) {
                    district = capitalizeWords(district);
                    District district1 = addDistrict(district, p);
                    districtList.add(district1);
                    List<String> mahalleler = locationAddService.getUniqueNeighborhoods(provice, district);

                    Set<Neighborhood> neighborhoodList = new HashSet<>();
                    for (String neighborhood : mahalleler) {
                        neighborhood = capitalizeWords(neighborhood);
                        neighborhoodList.add(addNeighborhood(neighborhood, district1));
                        i++;
                    }


                    System.out.println(j + ". İL /  SIRA: " + i + "  - İL: " + p.getName() + "  - İLÇE: " + district1.getName() + " - MAHALLE SAYISI: " + neighborhoodList.size());

                    district1.setNeighborhoods(neighborhoodList);
                    districtRepository.saveAndFlush(district1);
                }

                p.setDistricts(districtList);
                provinceRepository.saveAndFlush(p);

                j++;
            }
        }
        return "OK";
    }


    public String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase(TR_LOCALE))  // ← DEĞİŞTİ
                        .append(word.substring(1).toLowerCase(TR_LOCALE))    // ← DEĞİŞTİ
                        .append(" ");
            }
        }

        return result.toString().trim();
    }


    private Province addProvince(Country country, String name) {


        List<Province> provinceList = provinceRepository.checkIfExist(name, country.getId());
        if (!provinceList.isEmpty()) {
            return provinceList.get(0);
        }
        Province province = new Province();
        province.setCountry(country);
        province.setName(name);
        province.setCode("code");
        return provinceRepository.saveAndFlush(province);
    }

    private District addDistrict(String name, Province province) {

        List<District> districtList = districtRepository.checkIfExist(name, province.getId());
        if (!districtList.isEmpty()) {
            return districtList.get(0);
        }
        District district = new District();
        district.setProvince(province);
        district.setName(name);
        district.setDistrictType(DistrictType.MERKEZ);
        return districtRepository.saveAndFlush(district);
    }


    private String generateUniqueSlug(String title) {
        if (!StringUtils.hasText(title)) {
            title = "neighborhood(-" + System.currentTimeMillis();
        }

        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

    }


    private Neighborhood addNeighborhood(String name, District district) {

        List<Neighborhood> neighborhoodList = neighborhoodRepository.checkIfExist(name, district.getId());
        if (!neighborhoodList.isEmpty()) {
            return neighborhoodList.get(0);
        }
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setDistrict(district);
        neighborhood.setName(name);
        neighborhood.setNeighborhoodType(NeighborhoodType.MERKEZ);
        String slug = generateUniqueSlug(name + System.currentTimeMillis());
        neighborhood.setSlug(slug);
        return neighborhoodRepository.saveAndFlush(neighborhood);
    }


     */

}
