package com.genixo.education.search.service;

import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.repository.insitution.BrandRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationAddService {

    private final BrandRepository brandRepository;

    private static final Locale TR_LOCALE = new Locale("tr", "TR");
    private static final Collator TR_COLLATOR = Collator.getInstance(TR_LOCALE);

    static {
        TR_COLLATOR.setStrength(Collator.PRIMARY); // Case-insensitive sorting
    }

// 5392272698
    public String loadBrands() {
        List<String> brands = parseBrandNames();
        int i=0;
        for (String brand : brands) {

            boolean b = brandRepository.existsByNameIgnoreCase(brand);
            if (b) {
                continue;
            }
i++;
            Brand brandObj = new Brand();
            brandObj.setName(brand);
            brandObj.setSlug(generateUniqueSlug(brand+i));
            brandObj.setDescription(brand);
            brandObj.setLogoUrl("default.jpg");
            brandObj.setCoverImageUrl("default.jpg");
            brandObj.setIsActive(true);
            brandRepository.saveAndFlush(brandObj);

        }
        return "Ok";
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


    public List<String> parseBrandNames() {


        String brandNameList = "BAHÇEŞEHİR KOLEJİ\n" +
                "DOĞA KOLEJİ\n" +
                "FİNAL KOLEJİ\n" +
                "SINAV KOLEJİ\n" +
                "MEKTEBİM KOLEJİ\n" +
                "DÜŞÜNÜR KOLEJİ\n" +
                "SİMYA KOLEJİ\n" +
                "BİL KOLEJİ\n" +
                "MBA KOLEJİ\n" +
                "UĞUR KOLEJİ\n" +
                "GİRNE KOLEJİ\n" +
                "AÇI KOLEJİ\n" +
                "NESİBE AYDIN KOLEJİ\n" +
                "TED KOLEJİ\n" +
                "ÇÖZÜM KOLEJİ\n" +
                "GÖKKUŞAĞI KOLEJİ\n" +
                "MURAT YILDIRIM KOLEJİ\n" +
                "BİREY KOLEJİ\n" +
                "SAFA KOLEJİ\n" +
                "ŞEHİR KOLEJİ\n" +
                "GÜNEŞ KOLEJİ\n" +
                "MAYA KOLEJİ\n" +
                "KEY KOLEJİ\n" +
                "ERA KOLEJİ\n" +
                "İSTEK KOLEJİ\n" +
                "ÇAMLICA KOLEJİ\n" +
                "AKADEMİ KOLEJİ\n" +
                "KÜLTÜR KOLEJİ\n" +
                "CEBİR KOLEJİ\n" +
                "BİLFEN KOLEJİ\n" +
                "BİLNET KOLEJİ\n" +
                "SÜLEYMANİYE KOLEJİ\n" +
                "KENT KOLEJİ\n" +
                "BİRİKİM KOLEJİ\n" +
                "FEN KOLEJİ\n" +
                "ANADOLU KOLEJİ\n" +
                "BİLGE KOLEJİ\n" +
                "MELTEM KOLEJİ\n" +
                "AKKOL KOLEJİ\n" +
                "CİHANGİR KOLEJİ\n" +
                "HÜRRİYET KOLEJİ\n" +
                "KOCATÜRK KOLEJİ\n" +
                "TEMA KOLEJİ\n" +
                "BİLİM KOLEJİ\n" +
                "YÜKSELİŞ KOLEJİ\n" +
                "İSTANBUL LİDER KOLEJİ\n" +
                "SULTAN FATİH KOLEJİ\n" +
                "OĞUZKAAN KOLEJİ\n" +
                "İNCİ KOLEJİ\n" +
                "AKA KOLEJİ\n" +
                "SEVGİ KOLEJİ\n" +
                "AKANT KOLEJİ\n" +
                "OKYANUS KOLEJİ\n" +
                "DEVA KOLEJİ\n" +
                "ENVAR KOLEJİ\n" +
                "KAVRAM KOLEJİ\n" +
                "TÜRK KOLEJİ\n" +
                "YEDİ RENKLİ ÇINAR KOLEJİ\n" +
                "MERİÇ KOLEJİ\n" +
                "ÖNERİ KOLEJİ\n" +
                "BİLTEK KOLEJİ\n" +
                "TAÇ KOLEJİ\n" +
                "AY KOLEJİ\n" +
                "BAŞAK KOLEJİ\n" +
                "YEDİİKLİM KOLEJİ\n" +
                "BİLİMSEV KOLEJİ\n" +
                "TEVFİK FİKRET KOLEJİ\n" +
                "UYGAR KOLEJİ\n" +
                "YENİ ÇİZGİ AKADEMİ KOLEJİ\n" +
                "TURUNCU KOLEJİ\n" +
                "GELİŞİM KOLEJİ\n" +
                "YENİLİKÇİ ÖĞRENME KOLEJİ\n" +
                "GÜNDOĞDU KOLEJİ\n" +
                "DURU KOLEJİ\n" +
                "DOĞUŞ KOLEJİ\n" +
                "ADA KOLEJİ\n" +
                "AK KOLEJİ\n" +
                "AÇILIM KOLEJİ\n" +
                "TEKDEN KOLEJİ\n" +
                "BOĞAZİÇİ KOLEJİ\n" +
                "LALE BAHÇESİ KOLEJİ\n" +
                "KADIKÖY ANADOLU VAKFI KOLEJİ\n" +
                "HEDEF KOLEJİ\n" +
                "KANGURU KOLEJİ\n" +
                "DOKU KOLEJİ\n" +
                "EGEBİL KOLEJİ\n" +
                "NAZMİ ARIKAN FEN BİLİMLERİ KOLEJİ\n" +
                "MARMARA KOLEJİ\n" +
                "BAŞARI KOLEJİ\n" +
                "ERDEM KOLEJİ\n" +
                "SEV KOLEJİ\n" +
                "AYIŞIĞI KOLEJİ\n" +
                "TARHAN KOLEJİ\n" +
                "ERKAN KOLEJİ\n" +
                "ACIBADEM KOLEJİ\n" +
                "ADABİLİM KOLEJİ\n" +
                "ADALYA KOLEJİ\n" +
                "ADAY KOLEJİ\n" +
                "ADAŞEHİR KOLEJİ\n" +
                "ADIM KOLEJİ\n" +
                "AFYON KOLEJİ\n" +
                "AKADEMİK KOLEJİ\n" +
                "AKADEMİSİ KOLEJİ\n" +
                "AKASYA KOLEJİ\n" +
                "AKIL KOLEJİ\n" +
                "AKIM KOLEJİ\n" +
                "AKIN KOLEJİ\n" +
                "AKIL KOLEJİ\n" +
                "AKSA KOLEJİ\n" +
                "AKSOY KOLEJİ\n" +
                "AKÇAĞLAYAN KOLEJİ\n" +
                "ALEV KOLEJİ\n" +
                "ALİ KOLEJİ\n" +
                "ALTIN KOLEJİ\n" +
                "ALTINAY KOLEJİ\n" +
                "ALTINKÜRE KOLEJİ\n" +
                "ALTINYAKA KOLEJİ\n" +
                "ALTINYILDIZ KOLEJİ\n" +
                "ALTINŞEHİR KOLEJİ\n" +
                "AMAÇ KOLEJİ\n" +
                "ANAKENT KOLEJİ\n" +
                "ANAŞEHİR KOLEJİ\n" +
                "ARDIÇ KOLEJİ\n" +
                "AREL KOLEJİ\n" +
                "ARİ KOLEJİ\n" +
                "ARMADA KOLEJİ\n" +
                "ARMAĞAN KOLEJİ\n" +
                "ARSLAN KOLEJİ\n" +
                "AS KOLEJİ\n" +
                "ASAF KOLEJİ\n" +
                "ASFA KOLEJİ\n" +
                "ASİL KOLEJİ\n" +
                "ASİYE KOLEJİ\n" +
                "ASLAN KOLEJİ\n" +
                "ASLANTÜRK KOLEJİ\n" +
                "ATACAN KOLEJİ\n" +
                "ATAEL KOLEJİ\n" +
                "ATAK KOLEJİ\n" +
                "ATAYURT KOLEJİ\n" +
                "ATAÇAĞ KOLEJİ\n" +
                "AY KOLEJİ\n" +
                "AYDINLAR KOLEJİ\n" +
                "AYDİL KOLEJİ\n" +
                "AYDOS KOLEJİ\n" +
                "AYIŞIĞI KOLEJİ\n" +
                "AŞİYAN KOLEJİ\n" +
                "BABAOĞLU KOLEJİ\n" +
                "BADEMLİ KOLEJİ\n" +
                "BAHTİYAR KOLEJİ\n" +
                "BALAT KOLEJİ\n" +
                "BALKANLAR KOLEJİ\n" +
                "BARIŞ KOLEJİ\n" +
                "BAYETAV KOLEJİ\n" +
                "BAĞ KOLEJİ\n" +
                "BAŞARAN KOLEJİ\n" +
                "BAŞARFEN KOLEJİ\n" +
                "BAŞARILI KOLEJİ\n" +
                "BAŞARIR KOLEJİ\n" +
                "BAŞKENT KOLEJİ\n" +
                "BAŞLANGIÇ KOLEJİ\n" +
                "BEK KOLEJİ\n" +
                "BENİM KOLEJİ\n" +
                "BERİL KOLEJİ\n" +
                "BEYFEN KOLEJİ\n" +
                "BEYSUKENT KOLEJİ\n" +
                "BEYTEPE KOLEJİ\n" +
                "BEYZA KOLEJİ\n" +
                "BİLBAŞAR KOLEJİ\n" +
                "BİLGEALP KOLEJİ\n" +
                "BİLGEKENT KOLEJİ\n" +
                "BİLGEM KOLEJİ\n" +
                "BİLGİKENT KOLEJİ\n" +
                "BİLGİNCE KOLEJİ\n" +
                "BİLGİYURT KOLEJİ\n" +
                "BİLGİÇ KOLEJİ\n" +
                "BİLİMKENT KOLEJİ\n" +
                "BİLİMSEV KOLEJİ\n" +
                "BİLİNÇ KOLEJİ\n" +
                "BİLSEV KOLEJİ\n" +
                "BİLTEPE KOLEJİ\n" +
                "BİLTES KOLEJİ\n" +
                "BİR KOLEJİ\n" +
                "BİRFEN KOLEJİ\n" +
                "BİRKENT KOLEJİ\n" +
                "BİRLER KOLEJİ\n" +
                "BİS KOLEJİ\n" +
                "BİTEK KOLEJİ\n" +
                "BİZ KOLEJİ\n" +
                "BOĞAZHİSAR KOLEJİ\n" +
                "BULUŞ KOLEJİ\n" +
                "CAN KOLEJİ\n" +
                "CANDAN KOLEJİ\n" +
                "CANER KOLEJİ\n" +
                "CENT KOLEJİ\n" +
                "CEVHER KOLEJİ\n" +
                "DARÜŞŞAFAKA KOLEJİ\n" +
                "DELTA KOLEJİ\n" +
                "DENEYİM KOLEJİ\n" +
                "DENGE KOLEJİ\n" +
                "DENİZ KOLEJİ\n" +
                "DENİZATI KOLEJİ\n" +
                "DERECE KOLEJİ\n" +
                "DEVRAN KOLEJİ\n" +
                "DEĞER KOLEJİ\n" +
                "DİKMEN KOLEJİ\n" +
                "DİL KOLEJİ\n" +
                "DİNAMİK KOLEJİ\n" +
                "DİRİLİŞ KOLEJİ\n" +
                "DOLMABAHÇE KOLEJİ\n" +
                "DURU KOLEJİ\n" +
                "DÖNENCE KOLEJİ\n" +
                "DÖŞKAYA KOLEJİ\n" +
                "DÜNYA KOLEJİ\n" +
                "EFDAL KOLEJİ\n" +
                "EKİM KOLEJİ\n" +
                "EKOBİLİM KOLEJİ\n" +
                "EKSEN KOLEJİ\n" +
                "ELMA KOLEJİ\n" +
                "ELVANKENT KOLEJİ\n" +
                "ENDER KOLEJİ\n" +
                "ENDERUN KOLEJİ\n" +
                "ENERJİ KOLEJİ\n" +
                "ENLEM KOLEJİ\n" +
                "ERALTIN KOLEJİ\n" +
                "ERASLAN KOLEJİ\n" +
                "ERCİYES KOLEJİ\n" +
                "ERDEMLER KOLEJİ\n" +
                "ERİŞ KOLEJİ\n" +
                "EROL KOLEJİ\n" +
                "ERSEV KOLEJİ\n" +
                "ERYETENEK KOLEJİ\n" +
                "ESENTEPE KOLEJİ\n" +
                "ETKİN KOLEJİ\n" +
                "EV KOLEJİ\n" +
                "EVRE KOLEJİ\n" +
                "EVRİM KOLEJİ\n" +
                "EYÜBOĞLU KOLEJİ\n" +
                "EZGİLİLER KOLEJİ\n" +
                "FARK KOLEJİ\n" +
                "FARKLI KOLEJİ\n" +
                "FATMA KOLEJİ\n" +
                "FATOŞ KOLEJİ\n" +
                "FENTEK KOLEJİ\n" +
                "FERHATLAR KOLEJİ\n" +
                "FİDE KOLEJİ\n" +
                "FİDEM KOLEJİ\n" +
                "FİGEN KOLEJİ\n" +
                "FİKİR KOLEJİ\n" +
                "FİLİZ KOLEJİ\n" +
                "FONO KOLEJİ\n" +
                "FORM KOLEJİ\n" +
                "FORMAT KOLEJİ\n" +
                "FORMKAMPÜS KOLEJİ\n" +
                "GAZİ KOLEJİ\n" +
                "GELECEĞE KOLEJİ\n" +
                "GELECEĞİM KOLEJİ\n" +
                "GENETİK KOLEJİ\n" +
                "GENÇLERİN KOLEJİ\n" +
                "GENÇLİK KOLEJİ\n" +
                "GLOBAL KOLEJİ\n" +
                "GRUP KOLEJİ\n" +
                "GÖKALP KOLEJİ\n" +
                "GÖKAY KOLEJİ\n" +
                "GÖKBORA KOLEJİ\n" +
                "GÖKYÜZÜ KOLEJİ\n" +
                "GÖRKEM KOLEJİ\n" +
                "GÜLNİHAL KOLEJİ\n" +
                "GÜMÜŞTEPE KOLEJİ\n" +
                "GÜNDEM KOLEJİ\n" +
                "GÜNEYKENT KOLEJİ\n" +
                "GÜNEŞLİ KOLEJİ\n" +
                "GÜNHAN KOLEJİ\n" +
                "GÜRTAN KOLEJİ\n" +
                "GÜRÇAĞ KOLEJİ\n" +
                "GÜZELYALI KOLEJİ\n" +
                "HALİÇ KOLEJİ\n" +
                "HALKALI KOLEJİ\n" +
                "HAMDULLAH KOLEJİ\n" +
                "HAS KOLEJİ\n" +
                "HASBAHÇE KOLEJİ\n" +
                "HASEKİ KOLEJİ\n" +
                "HAYAT KOLEJİ\n" +
                "HAZERBEY KOLEJİ\n" +
                "HECE KOLEJİ\n" +
                "HEDEFİM KOLEJİ\n" +
                "HİSAR KOLEJİ\n" +
                "HÜDA KOLEJİ\n" +
                "HÜMA KOLEJİ\n" +
                "IRMAK KOLEJİ\n" +
                "IŞIKKENT KOLEJİ\n" +
                "IŞIN KOLEJİ\n" +
                "JALE KOLEJİ\n" +
                "KADİR KOLEJİ\n" +
                "KAMER KOLEJİ\n" +
                "KANUNİ KOLEJİ\n" +
                "KARAOĞLAN KOLEJİ\n" +
                "KARATARAKLI KOLEJİ\n" +
                "KAVAKLIDERE KOLEJİ\n" +
                "KAYABAŞI KOLEJİ\n" +
                "KAYAŞEHİR KOLEJİ\n" +
                "KAZANIM KOLEJİ\n" +
                "KEMAL KOLEJİ\n" +
                "KEREM KOLEJİ\n" +
                "KİRAÇ KOLEJİ\n" +
                "KIRDAR KOLEJİ\n" +
                "KIVILCIM KOLEJİ\n" +
                "KİLİTTAŞI KOLEJİ\n" +
                "KİPAŞ KOLEJİ\n" +
                "KOCATEPE KOLEJİ\n" +
                "KORAY KOLEJİ\n" +
                "KORHAN KOLEJİ\n" +
                "KOÇAŞ KOLEJİ\n" +
                "KUZEY KOLEJİ\n" +
                "KÖRFEZİM KOLEJİ\n" +
                "KÖYÜ KOLEJİ\n" +
                "KÜLLİYAT KOLEJİ\n" +
                "KÜPKÖK KOLEJİ\n" +
                "KÜÇÜK KOLEJİ\n" +
                "LALE KOLEJİ\n" +
                "LARA KOLEJİ\n" +
                "LEVENT KOLEJİ\n" +
                "LÖSEV KOLEJİ\n" +
                "MADALYON KOLEJİ\n" +
                "MAHİR KOLEJİ\n" +
                "MAHMUT KOLEJİ\n" +
                "MARAL KOLEJİ\n" +
                "MART KOLEJİ\n" +
                "MATBİLİM KOLEJİ\n" +
                "MATFEN KOLEJİ\n" +
                "MEDENİYET KOLEJİ\n" +
                "MEF KOLEJİ\n" +
                "MEFKURE KOLEJİ\n" +
                "MERCAN KOLEJİ\n" +
                "MERİDYEN KOLEJİ\n" +
                "MERT KOLEJİ\n" +
                "MERTER KOLEJİ\n" +
                "MESAFE KOLEJİ\n" +
                "MESAI KOLEJİ\n" +
                "METOD KOLEJİ\n" +
                "MEZAİM KOLEJİ\n" +
                "MİMAR KOLEJİ\n" +
                "MİNECAN KOLEJİ\n" +
                "MİS KOLEJİ\n" +
                "MODAFEN KOLEJİ\n" +
                "MODEL KOLEJİ\n" +
                "MURADİYE KOLEJİ\n" +
                "MÜJGAN KOLEJİ\n" +
                "MÜREKKEP KOLEJİ\n" +
                "MÜRÜVVET KOLEJİ\n" +
                "NADİDE KOLEJİ\n" +
                "NAMIK KOLEJİ\n" +
                "NAR KOLEJİ\n" +
                "NARİN KOLEJİ\n" +
                "NASUHBEY KOLEJİ\n" +
                "NENE KOLEJİ\n" +
                "NESİLDEN KOLEJİ\n" +
                "NET KOLEJİ\n" +
                "NEVA KOLEJİ\n" +
                "NEVZAT KOLEJİ\n" +
                "NİLGÜN KOLEJİ\n" +
                "NİTELİKLİ KOLEJİ\n" +
                "NOVA KOLEJİ\n" +
                "NUN KOLEJİ\n" +
                "ODAK KOLEJİ\n" +
                "OKAN KOLEJİ\n" +
                "OKTAYLAR KOLEJİ\n" +
                "OKUTGEN KOLEJİ\n" +
                "ONUR KOLEJİ\n" +
                "ORANTI KOLEJİ\n" +
                "ORMAN KOLEJİ\n" +
                "ORTADOĞULULAR KOLEJİ\n" +
                "PAKKAN KOLEJİ\n" +
                "PARLA KOLEJİ\n" +
                "PARMAK KOLEJİ\n" +
                "PAYLAŞIM KOLEJİ\n" +
                "PELİKAN KOLEJİ\n" +
                "PERMA KOLEJİ\n" +
                "Pİ KOLEJİ\n" +
                "PİRİ KOLEJİ\n" +
                "PİRİREİS KOLEJİ\n" +
                "POYRAZ KOLEJİ\n" +
                "POZİTİF KOLEJİ\n" +
                "PUSULA KOLEJİ\n" +
                "REHBER KOLEJİ\n" +
                "RÜZGAR KOLEJİ\n" +
                "SAFİR KOLEJİ\n" +
                "SANAT KOLEJİ\n" +
                "SANCAK KOLEJİ\n" +
                "SANKO KOLEJİ\n" +
                "SAVAŞ KOLEJİ\n" +
                "SAYGIN KOLEJİ\n" +
                "SEKİZGEN KOLEJİ\n" +
                "SELİMİYE KOLEJİ\n" +
                "SERPİL KOLEJİ\n" +
                "SERVET KOLEJİ\n" +
                "SEV KOLEJİ\n" +
                "SEYMEN KOLEJİ\n" +
                "SEZİN KOLEJİ\n" +
                "SINAVI KOLEJİ\n" +
                "SİMGE KOLEJİ\n" +
                "SİNERJİ KOLEJİ\n" +
                "SUAT KOLEJİ\n" +
                "SULAR KOLEJİ\n" +
                "SÜHEYLA KOLEJİ\n" +
                "SÜMER KOLEJİ\n" +
                "T KOLEJİ\n" +
                "TAN KOLEJİ\n" +
                "TANI KOLEJİ\n" +
                "TARABYA KOLEJİ\n" +
                "TARZ KOLEJİ\n" +
                "TAŞ KOLEJİ\n" +
                "TEKFEN KOLEJİ\n" +
                "TEKNOFEN KOLEJİ\n" +
                "TEKNOKENT KOLEJİ\n" +
                "TEKSEN KOLEJİ\n" +
                "TEKYILDIZ KOLEJİ\n" +
                "TEMEL KOLEJİ\n" +
                "TEORİ KOLEJİ\n" +
                "TOPLUM KOLEJİ\n" +
                "TRAKYA KOLEJİ\n" +
                "TUNA KOLEJİ\n" +
                "TUNALI KOLEJİ\n" +
                "TUNÇSİPER KOLEJİ\n" +
                "TURAN KOLEJİ\n" +
                "TURKUVAZ KOLEJİ\n" +
                "TUĞRA KOLEJİ\n" +
                "TÜR KOLEJİ\n" +
                "UFUKTEPE KOLEJİ\n" +
                "ULU KOLEJİ\n" +
                "ULUDAĞ KOLEJİ\n" +
                "ULUS KOLEJİ\n" +
                "UÇAN KOLEJİ\n" +
                "UĞURALP KOLEJİ\n" +
                "VENÜS KOLEJİ\n" +
                "VERA KOLEJİ\n" +
                "VİLDAN KOLEJİ\n" +
                "VİLLA KOLEJİ\n" +
                "YAREN KOLEJİ\n" +
                "YAVUZLAR KOLEJİ\n" +
                "YAVUZOĞLU KOLEJİ\n" +
                "YAŞAMFEN KOLEJİ\n" +
                "YEDİBİLİM KOLEJİ\n" +
                "YELDEĞİRMENİ KOLEJİ\n" +
                "YENİLİKÇİ KOLEJİ\n" +
                "YENİOKUL KOLEJİ\n" +
                "YERYÜZÜ KOLEJİ\n" +
                "YEŞİLKÖY KOLEJİ\n" +
                "YİRMİ KOLEJİ\n" +
                "YİĞİT KOLEJİ\n" +
                "YOLU KOLEJİ\n" +
                "YÖN KOLEJİ\n" +
                "YÖNELT KOLEJİ\n" +
                "YÖNTEMİM KOLEJİ\n" +
                "YÜCE KOLEJİ\n" +
                "YÜCEL KOLEJİ\n" +
                "YÜKSEL KOLEJİ\n" +
                "YÜZYIL KOLEJİ\n" +
                "ZARAFET KOLEJİ\n" +
                "ZEHRA KOLEJİ\n" +
                "ZEMİN KOLEJİ\n" +
                "ZİNDE KOLEJİ\n" +
                "ZORLU KOLEJİ\n" +
                "ÇAKABEY KOLEJİ\n" +
                "ÇAKIR KOLEJİ\n" +
                "ÇAMLARALTI KOLEJİ\n" +
                "ÇAĞ KOLEJİ\n" +
                "ÇAĞFEN KOLEJİ\n" +
                "ÇAĞI KOLEJİ\n" +
                "ÇAĞIN KOLEJİ\n" +
                "ÇAĞRI KOLEJİ\n" +
                "ÇELİK KOLEJİ\n" +
                "ÇEVRE KOLEJİ\n" +
                "ÇINARIM KOLEJİ\n" +
                "ÖNERİ KOLEJİ\n" +
                "ÖRNEK KOLEJİ\n" +
                "ÖZDEMİR KOLEJİ\n" +
                "ÖZELSİN KOLEJİ\n" +
                "ÖZGE KOLEJİ\n" +
                "ÖZGÜR KOLEJİ\n" +
                "ÖZKOCAMAN KOLEJİ\n" +
                "ÖZŞEN KOLEJİ\n" +
                "Ü KOLEJİ\n" +
                "ÜLKEM KOLEJİ\n" +
                "ÜN KOLEJİ\n" +
                "ÜNİ KOLEJİ\n" +
                "ÜNSAL KOLEJİ\n" +
                "ÜTOPYA KOLEJİ\n" +
                "İHLAS KOLEJİ\n" +
                "İKBAL KOLEJİ\n" +
                "İLERİ KOLEJİ\n" +
                "İLHAN KOLEJİ\n" +
                "İLİMKENT KOLEJİ\n" +
                "İLKEM KOLEJİ\n" +
                "İLKGÜNEŞ KOLEJİ\n" +
                "İLMEK KOLEJİ\n" +
                "İMZA KOLEJİ\n" +
                "İNTEGRAL KOLEJİ\n" +
                "İRFAN KOLEJİ\n" +
                "İSMAİL KOLEJİ\n" +
                "İSTEM KOLEJİ\n" +
                "İYİ KOLEJİ\n" +
                "İZFEN KOLEJİ\n" +
                "İÇEL KOLEJİ\n" +
                "ŞAHİKA KOLEJİ\n" +
                "ŞAHİNKAYA KOLEJİ\n" +
                "ŞEBNEM KOLEJİ\n" +
                "ŞEFİK KOLEJİ\n" +
                "ŞEHRİBAN KOLEJİ\n" +
                "ŞENAY KOLEJİ\n" +
                "ŞENER KOLEJİ\n" +
                "ŞEREF KOLEJİ";


        Collator turkishCollator = Collator.getInstance(new Locale("tr", "TR"));
        turkishCollator.setStrength(Collator.PRIMARY);

        return Arrays.stream(brandNameList.split("\n"))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(this::capitalizeWords)
                .collect(Collectors.toCollection(() -> new TreeSet<>(turkishCollator)))
                .stream()
                .collect(Collectors.toList());
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



/*
    private List<LocationData> cachedLocations;

    @PostConstruct
    public void init() {
        this.cachedLocations = loadLocationsFromCsv();
    }


    private boolean equalsIgnoreCaseTurkish(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        }
        return str1.toUpperCase(TR_LOCALE).equals(str2.toUpperCase(TR_LOCALE));
    }

    public List<LocationData> loadLocationsFromCsv() {
        List<LocationData> locations = new ArrayList<>();

        try {
            ClassPathResource resource = new ClassPathResource("listeler.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;

                    try {
                        if (line.trim().isEmpty()) {
                            continue;
                        }

                        String[] parts = line.split(";");

                        LocationData location = new LocationData();
                        location.setIl(parts.length > 0 ? parts[0].trim() : "");
                        location.setIlce(parts.length > 1 ? parts[1].trim() : "");
                        location.setMerkez(parts.length > 2 ? parts[2].trim() : "");
                        location.setMahalle(parts.length > 3 ? parts[3].trim() : "");

                        locations.add(location);

                    } catch (Exception e) {
                        log.warn("Satır {} işlenirken hata oluştu: {}", lineNumber, line, e);
                    }
                }

                log.info("{} adet lokasyon verisi yüklendi", locations.size());

            }

        } catch (Exception e) {
            log.error("CSV dosyası okunurken hata oluştu", e);
        }

        return locations;
    }


    public List<String> getUniqueCities() {
        return cachedLocations.stream()
                .map(LocationData::getIl)
                .filter(il -> il != null && !il.isEmpty())
                .distinct()
                .sorted(TR_COLLATOR::compare)
                .collect(Collectors.toList());
    }


    public List<String> getUniqueDistricts(String il) {
        return cachedLocations.stream()
                .filter(loc -> loc.getIl() != null && equalsIgnoreCaseTurkish(loc.getIl(), il))
                .map(loc -> {
                    String ilce = loc.getIlce();
                    String merkez = loc.getMerkez();

                    // Merkez boş değilse "İlçe - Merkez" formatında döndür
                    if (merkez != null && !merkez.trim().isEmpty()) {
                        return ilce + " - " + merkez;
                    }
                    // Merkez boşsa sadece ilçe
                    return ilce;
                })
                .filter(district -> district != null && !district.isEmpty())
                .distinct()
                .sorted(TR_COLLATOR::compare)
                .collect(Collectors.toList());
    }


    public List<String> getUniqueNeighborhoods(String il, String ilce) {
        return cachedLocations.stream()
                .filter(loc -> {
                    // İl kontrolü
                    if (loc.getIl() == null || !equalsIgnoreCaseTurkish(loc.getIl(), il)) {
                        return false;
                    }

                    // İlçe kontrolü
                    // Eğer gelen ilçe "İlçe - Merkez" formatındaysa ayır
                    if (ilce.contains(" - ")) {
                        String[] parts = ilce.split(" - ");
                        String ilceAdi = parts[0].trim();
                        String merkezAdi = parts[1].trim();

                        return equalsIgnoreCaseTurkish(loc.getIlce(), ilceAdi) &&
                                loc.getMerkez() != null &&
                                equalsIgnoreCaseTurkish(loc.getMerkez(), merkezAdi);
                    } else {
                        // Sadece ilçe adı verilmişse ve merkez boşsa
                        return equalsIgnoreCaseTurkish(loc.getIlce(), ilce) &&
                                (loc.getMerkez() == null || loc.getMerkez().trim().isEmpty());
                    }
                })
                .map(LocationData::getMahalle)
                .filter(mahalle -> mahalle != null && !mahalle.isEmpty())
                .distinct()
                .sorted(TR_COLLATOR::compare)
                .collect(Collectors.toList());
    }
    */
}