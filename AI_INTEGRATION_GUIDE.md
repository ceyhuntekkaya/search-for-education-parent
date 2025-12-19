# AI Okul Arama Entegrasyonu - Teknik Döküman

## 📋 Genel Bakış

AI sistemi, kullanıcıdan **isim bazlı** parametreler alarak okul araması yapacak.

**Fark:**
- Frontend: ID kullanır (institutionTypeId: 1, provinceId: 6)
- AI: İsim kullanır (institutionTypeName: "LİSE", provinceName: "ANKARA")

---

## 🔌 AI Endpoint'leri

### 1. Ana Arama Endpoint'i

**URL:** `POST /api/v1/schools/search/by-names`

**Request Body:**
```json
{
  "institutionTypeName": "LİSE",
  "provinceName": "ANKARA",
  "districtName": "ÇANKAYA",
  "neighborhoodName": "BİLKENT",
  "minAge": 14,
  "maxAge": 18,
  "minFee": 10000,
  "maxFee": 50000,
  "curriculumType": "IB",
  "languageOfInstruction": "İNGİLİZCE",
  "minRating": 4.0,
  "isSubscribed": true,
  "propertyFilters": [
    "BASKETBOL KULÜBÜ",
    "DRAMA",
    "YÜZME HAVUZU",
    "IB PROGRAMI"
  ],
  "searchTerm": "",
  "sortBy": "rating",
  "sortDirection": "desc",
  "page": 0,
  "size": 12
}
```

**Response:**
```json
{
  "content": [
    {
      "id": 123,
      "name": "TED Ankara Koleji",
      "slug": "ted-ankara-koleji",
      "description": "1931'den beri kaliteli eğitim...",
      "campusName": "TED Ankara Kampüsü",
      "neighborhoodName": "Bilkent",
      "districtName": "Çankaya",
      "provinceName": "Ankara",
      "fullLocation": "Bilkent, Çankaya, Ankara",
      "address": "Üniversiteler Mah. 1606. Cad. No:16",
      "latitude": 39.868279,
      "longitude": 32.748697,
      "logoUrl": "https://...",
      "coverImageUrl": "https://...",
      "ratingAverage": 4.5,
      "ratingCount": 234,
      "ratingStars": "★★★★⯨",
      "institutionTypeDisplayName": "Lise",
      "curriculumType": "IB",
      "languageOfInstruction": "İngilizce",
      "monthlyFee": 25000.0,
      "annualFee": 300000.0,
      "feeRangeText": "25000 ₺/ay | 300000 ₺/yıl",
      "minAge": 14,
      "maxAge": 18,
      "ageRangeText": "14-18 yaş",
      "phone": "+90 312 586 8000",
      "email": "info@ted.org.tr",
      "websiteUrl": "https://ted.org.tr",
      "propertyCount": 15,
      "qualityScore": 87.5,
      "trustLevel": "VERIFIED"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 12
  },
  "totalElements": 45,
  "totalPages": 4
}
```

### 2. Coğrafi Arama Endpoint'i

**URL:** `POST /api/v1/schools/search/nearby-by-names?latitude=39.925533&longitude=32.866287&radiusKm=5.0`

**Request Body:**
```json
{
  "institutionTypeName": "LİSE",
  "page": 0,
  "size": 12
}
```

**Response:** Aynı format, ek olarak mesafe bilgisi.

---

## 📝 Alan Açıklamaları

### ZORUNLU Alanlar

| Alan | Tip | Açıklama | Örnek |
|------|-----|----------|-------|
| institutionTypeName | String | Kurum tipi ADI | "LİSE", "ORTAOKUL", "İLKOKUL" |
| provinceName | String | İl ADI | "ANKARA", "İSTANBUL", "İZMİR" |

### OPSIYONEL Alanlar

| Alan | Tip | Açıklama | Örnek |
|------|-----|----------|-------|
| districtName | String | İlçe adı | "ÇANKAYA", "BEŞİKTAŞ" |
| neighborhoodName | String | Mahalle adı | "BİLKENT", "ETİLER" |
| minAge | Integer | Minimum yaş | 6 |
| maxAge | Integer | Maksimum yaş | 18 |
| minFee | Double | Minimum ücret (TL/ay) | 5000.0 |
| maxFee | Double | Maksimum ücret (TL/ay) | 50000.0 |
| curriculumType | String | Müfredat tipi | "IB", "MEB", "CAMBRIDGE" |
| languageOfInstruction | String | Eğitim dili | "İNGİLİZCE", "TÜRKÇE" |
| minRating | Double | Minimum puan | 4.0 |
| isSubscribed | Boolean | Abone kampüs | true, false |
| propertyFilters | String[] | Özellik isimleri | ["BASKETBOL KULÜBÜ", "DRAMA"] |
| searchTerm | String | Genel arama | "ted kolej" |
| sortBy | String | Sıralama | "rating", "price", "name", "created" |
| sortDirection | String | Yön | "asc", "desc" |
| page | Integer | Sayfa | 0 |
| size | Integer | Sayfa boyutu | 12 |

---

## 🎯 Property Filters (Özellikler)

AI, özellik **display name**'lerini kullanır.

### Örnek Özellikler:

**Tesisler:**
- "YÜZME HAVUZU"
- "BASKETBOL SALONU"
- "FUTBOL SAHASI"
- "KÜTÜPHANE"
- "LAB"
- "KANTIN"

**Aktiviteler:**
- "BASKETBOL KULÜBÜ"
- "FUTBOL KULÜBÜ"
- "DRAMA"
- "MÜZİK"
- "RESİM"

**Diller:**
- "İNGİLİZCE"
- "ALMANCA"
- "FRANSIZCA"
- "İSPANYOLCA"

**Akreditasyonlar:**
- "IB PROGRAMI"
- "CAMBRIDGE"
- "MEB ONAYLı"

### Kullanım:
```json
{
  "propertyFilters": [
    "YÜZME HAVUZU",
    "BASKETBOL KULÜBÜ",
    "IB PROGRAMI"
  ]
}
```

**Mantık:** Tüm özelliklere sahip okulları bulur (AND mantığı).

---

## 🔤 Büyük/Küçük Harf

**Sistem case-insensitive çalışır:**

```json
// Bunların hepsi aynı
"provinceName": "ANKARA"
"provinceName": "Ankara"
"provinceName": "ankara"

// Türkçe karakterler desteklenir
"districtName": "ÇANKAYA" ✅
"districtName": "çankaya" ✅
```

---

## 📊 Sıralama (Sorting)

### sortBy Değerleri:

| Değer | Açıklama |
|-------|----------|
| `rating` | Puana göre |
| `price` | Ücrete göre |
| `name` | İsme göre |
| `created` | Eklenme tarihine göre |

### sortDirection Değerleri:

| Değer | Açıklama |
|-------|----------|
| `asc` | Artan (A→Z, 1→9) |
| `desc` | Azalan (Z→A, 9→1) |

### Örnek:
```json
{
  "sortBy": "rating",
  "sortDirection": "desc"
}
```
En yüksek puanlıdan başlar.

---

## 🌍 Coğrafi Arama

### Nasıl Çalışır?

1. Kullanıcıdan koordinat al
2. Yarıçap belirle (default: 10 km)
3. Endpoint'e gönder

### Örnek Request:

```bash
POST /api/v1/schools/search/nearby-by-names?latitude=39.925533&longitude=32.866287&radiusKm=5.0

{
  "institutionTypeName": "LİSE"
}
```

### Response:
Normal response + mesafe bilgisi (distanceKm).

---

## ⚠️ Validasyon Hataları

### 400 Bad Request

**Zorunlu alan eksik:**
```json
{
  "error": "institutionTypeName and provinceName are required"
}
```

**Çözüm:** `institutionTypeName` ve `provinceName` mutlaka gönder.

---

## 🧪 Test Örnekleri

### Örnek 1: Basit Arama
```json
{
  "institutionTypeName": "LİSE",
  "provinceName": "ANKARA",
  "page": 0,
  "size": 12
}
```

### Örnek 2: Detaylı Arama
```json
{
  "institutionTypeName": "LİSE",
  "provinceName": "ANKARA",
  "districtName": "ÇANKAYA",
  "minFee": 10000,
  "maxFee": 30000,
  "minRating": 4.0,
  "propertyFilters": ["YÜZME HAVUZU", "IB PROGRAMI"],
  "sortBy": "rating",
  "sortDirection": "desc",
  "page": 0,
  "size": 12
}
```

### Örnek 3: Sadece İsme Göre
```json
{
  "institutionTypeName": "LİSE",
  "provinceName": "İSTANBUL",
  "searchTerm": "kolej",
  "sortBy": "name",
  "sortDirection": "asc",
  "page": 0,
  "size": 20
}
```

### Örnek 4: Coğrafi
```bash
POST /api/v1/schools/search/nearby-by-names?latitude=41.0082&longitude=28.9784&radiusKm=3.0

{
  "institutionTypeName": "İLKOKUL"
}
```

---

## 🔍 AI'dan Beklenen Veri Formatı

### Kullanıcı der ki:
> "Ankara Çankaya'da IB programı olan, yüzme havuzu bulunan, aylık 20-40 bin arası liseler"

### AI çıkarmalı:
```json
{
  "institutionTypeName": "LİSE",
  "provinceName": "ANKARA",
  "districtName": "ÇANKAYA",
  "minFee": 20000,
  "maxFee": 40000,
  "propertyFilters": [
    "IB PROGRAMI",
    "YÜZME HAVUZU"
  ],
  "sortBy": "rating",
  "sortDirection": "desc",
  "page": 0,
  "size": 12
}
```

---

## 💡 AI İpuçları

### 1. Boş Alanları Gönderme
```json
// YANLIŞ ❌
{
  "districtName": "",
  "curriculumType": ""
}

// DOĞRU ✅
{
  // Boş alanları hiç gönderme
}
```

### 2. Türkçe Karakterler
```json
// Her ikisi de çalışır
"provinceName": "ANKARA"
"provinceName": "ankara"
```

### 3. Özellik İsimleri
```json
// Tam ismini kullan
"YÜZME HAVUZU" ✅
"havuz" ❌

// Sistem display_name ile eşleştirir
```

### 4. Default Değerler
```json
// AI bunları set edebilir
{
  "page": 0,
  "size": 12,
  "sortBy": "rating",
  "sortDirection": "desc"
}
```

---

## 📊 Response Alanları

### Okul Bilgileri
| Alan | Açıklama |
|------|----------|
| id | Okul ID |
| name | Okul adı |
| slug | URL slug |
| description | Açıklama |

### Lokasyon
| Alan | Açıklama |
|------|----------|
| provinceName | İl |
| districtName | İlçe |
| neighborhoodName | Mahalle |
| fullLocation | "Mahalle, İlçe, İl" |
| address | Açık adres |
| latitude | Enlem |
| longitude | Boylam |

### Ücret
| Alan | Açıklama |
|------|----------|
| monthlyFee | Aylık (TL) |
| annualFee | Yıllık (TL) |
| feeRangeText | "5000 ₺/ay \| 60000 ₺/yıl" |

### Rating
| Alan | Açıklama |
|------|----------|
| ratingAverage | Ortalama puan (0-5) |
| ratingCount | Oy sayısı |
| ratingStars | "★★★★⯨" |
| trustLevel | "VERIFIED", "HIGH", "MEDIUM", "LOW" |

### İletişim
| Alan | Açıklama |
|------|----------|
| phone | Telefon |
| email | Email |
| websiteUrl | Website |

### Diğer
| Alan | Açıklama |
|------|----------|
| propertyCount | Özellik sayısı |
| qualityScore | Kalite skoru (0-100) |
| studentCapacity | Kapasite |
| minAge, maxAge | Yaş aralığı |

---

## 🚀 Hızlı Başlangıç

### 1. Test URL
```
POST https://api.egitimiste.com/api/v1/schools/search/by-names
```

### 2. Minimum Request
```json
{
  "institutionTypeName": "LİSE",
  "provinceName": "ANKARA"
}
```

### 3. cURL Örneği
```bash
curl -X POST https://api.egitimiste.com/api/v1/schools/search/by-names \
  -H "Content-Type: application/json" \
  -d '{
    "institutionTypeName": "LİSE",
    "provinceName": "ANKARA",
    "minRating": 4.0,
    "page": 0,
    "size": 12
  }'
```

---

## 📞 Sorular?

- **Backend:** Ceyhun
- **Endpoint:** `/api/v1/schools/search/by-names`
- **Test Ortamı:** [URL eklenecek]

---

## ✅ Checklist (AI Ekibi İçin)

- [ ] Zorunlu alanlar (`institutionTypeName`, `provinceName`) mutlaka gönderiliyor
- [ ] Boş string'ler gönderilmiyor (null veya hiç gönderme)
- [ ] Özellik isimleri tam display name olarak gönderiliyor
- [ ] Pagination parametreleri set ediliyor (`page`, `size`)
- [ ] Sort parametreleri geçerli değerler ("rating", "price", "name", "created")
- [ ] Case-insensitive olduğu biliniyor
- [ ] Response'daki tüm alanlar parse ediliyor
- [ ] Error handling yapılıyor (400, 500)
