# Kurum Metadata API Dokümantasyonu

Frontend geliştiriciler için kurum tipi hiyerarşisi (InstitutionTypeGroup → InstitutionType → PropertyGroupType → PropertyType → InstitutionProperty) ve ilgili admin CRUD endpoint'leri.

**Base URL:** `{API_BASE_URL}/api/institutions`

**Swagger:** `{API_BASE_URL}/api/swagger-ui/index.html` → *Institution Management*

---

## İçindekiler

1. [Hiyerarşi ve Kavramlar](#1-hiyerarşi-ve-kavramlar)
2. [Kimlik Doğrulama ve Yetki](#2-kimlik-doğrulama-ve-yetki)
3. [Genel Response Yapısı](#3-genel-response-yapısı)
4. [TypeScript Modelleri](#4-typescript-modelleri)
5. [Önerilen Admin Akışı](#5-önerilen-admin-akışı)
6. [Endpoint'ler](#6-endpointler)
   - [6.1 InstitutionTypeGroup](#61-institutiontypegroup)
   - [6.2 InstitutionType](#62-institutiontype)
   - [6.3 PropertyGroupType](#63-propertygrouptype)
   - [6.4 PropertyType](#64-propertytype)
   - [6.5 InstitutionProperty](#65-institutionproperty)
7. [Hata Kodları ve İş Kuralları](#7-hata-kodları-ve-iş-kuralları)
8. [Örnek Senaryolar](#8-örnek-senaryolar)

---

## 1. Hiyerarşi ve Kavramlar

### 1.1 Varlık Ağacı

```
InstitutionTypeGroup          (ör: "Özel Okul", "Kurs Merkezi")
  └── InstitutionType         (ör: "Anaokulu", "İlkokul", "Lise")
        ├── PropertyGroupType (ör: "Akademik", "Sosyal", "Altyapı")
        │     └── PropertyType (ör: "Yabancı Dil", "Spor Salonu", "Yüzme Havuzu")
        └── InstitutionProperty (okul/kampüs için gerçek özellik tanımı; PropertyType'a bağlı)
```

### 1.2 Her Katmanın Rolü

| Entity | Açıklama | Örnek |
|--------|----------|-------|
| **InstitutionTypeGroup** | Kurum tiplerini gruplayan üst kategori | "Özel Eğitim Kurumları" |
| **InstitutionType** | Bir okulun/kurumun tipi; `School` entity'sine bağlanır | "Anaokulu", "Lise" |
| **PropertyGroupType** | Bir kurum tipine ait özellik grupları | "Akademik Özellikler" |
| **PropertyType** | Grup altındaki seçilebilir özellik şablonu | "İngilizce Eğitimi", "Fransızca Eğitimi" |
| **InstitutionProperty** | Kurum tipine özel, veri tipi ve validasyon kuralları olan özellik tanımı; okul/kampüs `InstitutionPropertyValue` kayıtları buna bağlanır | `dataType: BOOLEAN`, `name: "ingilizce_egitimi"` |

### 1.3 İlişki Özeti

- `InstitutionType.group` → `InstitutionTypeGroup` (opsiyonel)
- `PropertyGroupType.institutionType` → `InstitutionType` (zorunlu)
- `PropertyType.propertyGroupType` → `PropertyGroupType` (zorunlu)
- `InstitutionProperty.institutionType` → `InstitutionType` (zorunlu)
- `InstitutionProperty.propertyType` → `PropertyType` (zorunlu, DB seviyesinde `nullable = false`)

> **Not:** `PropertyType` şablon özelliği tanımlar; `InstitutionProperty` ise o şablona bağlı, kurum tipine özel detaylı özellik kaydıdır (veri tipi, arama/filtre bayrakları, validasyon vb.).

### 1.4 Soft Delete

Tüm silme işlemleri **fiziksel silme yapmaz**; kayıt `isActive = false` yapılır. Liste ve detay endpoint'leri yalnızca `isActive = true` kayıtları döner.

---

## 2. Kimlik Doğrulama ve Yetki

| İşlem | JWT Gerekli mi? | Rol |
|-------|-----------------|-----|
| GET (tüm listeleme/detay) | Hayır (gateway açık) | Herkes okuyabilir |
| POST / PUT / DELETE | **Evet** | `SYSTEM` rolü zorunlu |

**Header (yazma işlemleri için):**

```
Authorization: Bearer {access_token}
Content-Type: application/json
```

JWT yoksa veya `SYSTEM` rolü yoksa yazma işlemleri `403` / business exception ile reddedilir:

```json
{
  "success": false,
  "message": "User does not have permission to manage institution types"
}
```

---

## 3. Genel Response Yapısı

Tüm endpoint'ler `ApiResponse<T>` wrapper'ı ile döner:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: string[] | null;
  timestamp: string;  // ISO 8601, ör: "2026-06-19T14:30:00"
  path?: string;      // İstek path'i
}
```

**Başarılı örnek:**

```json
{
  "success": true,
  "message": "Institution type created successfully",
  "data": { "id": 1, "name": "anaokulu", "displayName": "Anaokulu" },
  "errors": null,
  "timestamp": "2026-06-19T14:30:00",
  "path": "/api/institutions/institution-types"
}
```

---

## 4. TypeScript Modelleri

### 4.1 Ortak Enum

```typescript
type PropertyDataType =
  | 'TEXT'
  | 'TEXTAREA'
  | 'NUMBER'
  | 'DECIMAL'
  | 'BOOLEAN'
  | 'SELECT'
  | 'MULTISELECT'
  | 'DATE'
  | 'TIME'
  | 'DATETIME'
  | 'URL'
  | 'EMAIL'
  | 'PHONE'
  | 'FILE'
  | 'IMAGE';
```

### 4.2 InstitutionTypeGroupDto

```typescript
interface InstitutionTypeGroupDto {
  id?: number;
  name: string;              // Benzersiz (aktif kayıtlar arasında)
  displayName: string;
  description?: string | null;
  iconUrl?: string | null;
  colorCode?: string | null; // ör: "#FF5733"
  sortOrder?: number;        // Varsayılan: 0
  defaultProperties?: string | null; // JSON string (serbest format)
  isActive?: boolean;        // Response'ta gelir
  createdAt?: string;        // Response'ta gelir
}
```

### 4.3 InstitutionTypeDto

```typescript
interface InstitutionTypeDto {
  id?: number;
  name: string;              // Benzersiz (aktif kayıtlar arasında)
  displayName: string;
  description?: string | null;
  iconUrl?: string | null;
  colorCode?: string | null;
  sortOrder?: number;
  defaultProperties?: string | null;
  groupId?: number | null;   // InstitutionTypeGroup ID (create/update'te gönderilir)
  groupName?: string | null; // Sadece response (group adı)
  properties?: InstitutionPropertyDto[]; // Detay response'ta dolu gelebilir
  isActive?: boolean;
  createdAt?: string;
}
```

### 4.4 PropertyGroupTypeDto

```typescript
interface PropertyGroupTypeDto {
  id?: number;
  name: string;              // Aynı institutionType içinde benzersiz
  displayName: string;
  institutionTypeId: number; // Zorunlu (create)
  sortOrder?: number;
  isMultiple?: boolean;      // Varsayılan: true — birden fazla PropertyType seçilebilir mi
  propertyTypes?: PropertyTypeDto[]; // Nested list endpoint'lerinde dolu
  isActive?: boolean;
  createdAt?: string;
}
```

### 4.5 PropertyTypeDto

```typescript
interface PropertyTypeDto {
  id?: number;
  name: string;              // Aynı propertyGroupType içinde benzersiz
  displayName: string;
  propertyGroupTypeId: number; // Zorunlu (create)
  sortOrder?: number;
  isActive?: boolean;
  createdAt?: string;
}
```

### 4.6 InstitutionPropertyCreateDto (Create & Update body)

```typescript
interface InstitutionPropertyCreateDto {
  institutionTypeId: number;
  propertyTypeId?: number;   // Önerilir (DB'de zorunlu alan)
  name: string;              // Aynı institutionType içinde benzersiz
  displayName: string;
  description?: string | null;
  dataType: PropertyDataType;
  isRequired?: boolean;      // Varsayılan: false
  isSearchable?: boolean;
  isFilterable?: boolean;
  showInCard?: boolean;
  showInProfile?: boolean;    // Varsayılan: true
  sortOrder?: number;
  options?: string | null;    // SELECT/MULTISELECT için seçenekler (virgülle ayrılmış string)
  defaultValue?: string | null;

  // Validasyon kuralları
  minValue?: number | null;
  maxValue?: number | null;
  minLength?: number | null;
  maxLength?: number | null;
  regexPattern?: string | null;
}
```

### 4.7 InstitutionPropertyDto (Response)

```typescript
interface InstitutionTypeSummaryDto {
  id: number;
  name: string;
  displayName: string;
  iconUrl?: string | null;
  colorCode?: string | null;
  schoolCount?: number;
}

interface InstitutionPropertyDto {
  id: number;
  name: string;
  displayName: string;
  description?: string | null;
  dataType: PropertyDataType;
  isRequired?: boolean;
  isSearchable?: boolean;
  isFilterable?: boolean;
  showInCard?: boolean;
  showInProfile?: boolean;
  sortOrder?: number;
  options?: string | null;
  defaultValue?: string | null;
  minValue?: number | null;
  maxValue?: number | null;
  minLength?: number | null;
  maxLength?: number | null;
  regexPattern?: string | null;
  institutionType?: InstitutionTypeSummaryDto;
  propertyTypeId?: number | null;
  isActive?: boolean;
  createdAt?: string;
}
```

### 4.8 Hiyerarşik Liste (Okuma için)

Admin panelinde tam ağaç göstermek için:

```typescript
interface InstitutionTypeListDto {
  institutionTypeDto: InstitutionTypeDto;
  propertyGroupTypeDtos: PropertyGroupTypeDto[]; // Her birinin içinde propertyTypes[] dolu
}
```

---

## 5. Önerilen Admin Akışı

Yeni bir kurum tipi ve özellik seti tanımlarken sıra:

```
1. InstitutionTypeGroup oluştur (opsiyonel ama önerilir)
2. InstitutionType oluştur (groupId ile bağla)
3. PropertyGroupType oluştur (institutionTypeId ile)
4. PropertyType oluştur (propertyGroupTypeId ile)
5. InstitutionProperty oluştur (institutionTypeId + propertyTypeId ile)
```

**UI önerisi:** Sol panelde grup → tip → özellik grubu → özellik tipi ağacı; sağ panelde seçili düğümün CRUD formu.

**Tam ağaç tek istekle:** `GET /institution-types/admin` veya `GET /institution-types/properties` → `InstitutionTypeListDto[]` döner.

---

## 6. Endpoint'ler

Tüm path'ler `{BASE}/api/institutions` altındadır.

---

### 6.1 InstitutionTypeGroup

| Method | Path | Auth | Açıklama |
|--------|------|------|----------|
| GET | `/institution-type-groups` | Hayır | Tüm aktif gruplar |
| GET | `/institution-type-groups/{id}` | Hayır | Grup detayı |
| POST | `/institution-type-groups` | SYSTEM | Yeni grup |
| PUT | `/institution-type-groups/{id}` | SYSTEM | Güncelle |
| DELETE | `/institution-type-groups/{id}` | SYSTEM | Soft delete |

#### POST / PUT Body

`InstitutionTypeGroupDto` (id create'te gönderilmez)

```json
{
  "name": "ozel_egitim",
  "displayName": "Özel Eğitim Kurumları",
  "description": "Özel okul ve kurs merkezleri",
  "iconUrl": "https://cdn.example.com/icons/school.svg",
  "colorCode": "#3B82F6",
  "sortOrder": 1,
  "defaultProperties": null
}
```

#### Response `data`

`InstitutionTypeGroupDto`

#### DELETE kısıtı

Aktif `InstitutionType` kaydı bu gruba bağlıysa silinemez → `409` / business error:
`"Cannot delete institution type group with active institution types"`

---

### 6.2 InstitutionType

| Method | Path | Auth | Açıklama |
|--------|------|------|----------|
| GET | `/institution-types` | Hayır | Tipler + özellik grupları (sadece kullanımda olanlar) |
| GET | `/institution-types/admin` | Hayır | Tüm aktif tipler + tam özellik ağacı |
| GET | `/institution-types/properties` | Hayır | Admin ile aynı (tüm aktif + ağaç) |
| GET | `/institution-types/summaries` | Hayır | Özet liste (schoolCount ile) |
| GET | `/institution-types/{id}` | Hayır | Tek tip detayı |
| POST | `/institution-types` | SYSTEM | Yeni tip |
| PUT | `/institution-types/{id}` | SYSTEM | Güncelle |
| DELETE | `/institution-types/{id}` | SYSTEM | Soft delete |

#### POST / PUT Body

`InstitutionTypeDto`

```json
{
  "name": "anaokulu",
  "displayName": "Anaokulu",
  "description": "3-6 yaş anaokulu kurumları",
  "iconUrl": "https://cdn.example.com/icons/kindergarten.svg",
  "colorCode": "#F59E0B",
  "sortOrder": 2,
  "defaultProperties": null,
  "groupId": 1
}
```

#### Response `data`

- CRUD: `InstitutionTypeDto`
- `/institution-types`, `/admin`, `/properties`: `InstitutionTypeListDto[]`
- `/summaries`: `InstitutionTypeSummaryDto[]`

```typescript
interface InstitutionTypeSummaryDto {
  id: number;
  name: string;
  displayName: string;
  iconUrl?: string;
  colorCode?: string;
  schoolCount: number;
}
```

#### DELETE kısıtı

Bu tipe bağlı aktif `School` varsa silinemez:
`"Cannot delete institution type with active schools"`

---

### 6.3 PropertyGroupType

| Method | Path | Auth | Açıklama |
|--------|------|------|----------|
| GET | `/property-group-types` | Hayır | Tüm aktif gruplar |
| GET | `/institution-types/{institutionTypeId}/property-group-types` | Hayır | Tipe göre filtre |
| GET | `/property-group-types/{id}` | Hayır | Detay |
| POST | `/property-group-types` | SYSTEM | Yeni grup |
| PUT | `/property-group-types/{id}` | SYSTEM | Güncelle |
| DELETE | `/property-group-types/{id}` | SYSTEM | Soft delete |

#### POST / PUT Body

`PropertyGroupTypeDto`

```json
{
  "name": "akademik",
  "displayName": "Akademik Özellikler",
  "institutionTypeId": 3,
  "sortOrder": 1,
  "isMultiple": true
}
```

#### DELETE kısıtı

Altında aktif `PropertyType` varsa silinemez:
`"Cannot delete property group type with active property types"`

---

### 6.4 PropertyType

| Method | Path | Auth | Açıklama |
|--------|------|------|----------|
| GET | `/property-types` | Hayır | Tüm aktif tipler |
| GET | `/property-group-types/{propertyGroupTypeId}/property-types` | Hayır | Gruba göre filtre |
| GET | `/property-types/{id}` | Hayır | Detay |
| POST | `/property-types` | SYSTEM | Yeni tip |
| PUT | `/property-types/{id}` | SYSTEM | Güncelle |
| DELETE | `/property-types/{id}` | SYSTEM | Soft delete |

#### POST / PUT Body

`PropertyTypeDto`

```json
{
  "name": "ingilizce_egitimi",
  "displayName": "İngilizce Eğitimi",
  "propertyGroupTypeId": 5,
  "sortOrder": 1
}
```

#### DELETE kısıtı

Bu tipe bağlı aktif `InstitutionProperty` varsa silinemez:
`"Cannot delete property type with active institution properties"`

---

### 6.5 InstitutionProperty

| Method | Path | Auth | Açıklama |
|--------|------|------|----------|
| GET | `/institution-types/{institutionTypeId}/properties` | Hayır | Tipe göre özellikler |
| GET | `/institution-properties/{id}` | Hayır | Detay |
| POST | `/institution-properties` | SYSTEM | Yeni özellik |
| PUT | `/institution-properties/{id}` | SYSTEM | Güncelle |
| DELETE | `/institution-properties/{id}` | SYSTEM | Soft delete |

#### POST / PUT Body

`InstitutionPropertyCreateDto`

```json
{
  "institutionTypeId": 3,
  "propertyTypeId": 12,
  "name": "ingilizce_egitimi",
  "displayName": "İngilizce Eğitimi",
  "description": "Kurumda İngilizce eğitimi verilip verilmediği",
  "dataType": "BOOLEAN",
  "isRequired": false,
  "isSearchable": true,
  "isFilterable": true,
  "showInCard": true,
  "showInProfile": true,
  "sortOrder": 1,
  "options": null,
  "defaultValue": "false"
}
```

**SELECT örneği:**

```json
{
  "institutionTypeId": 3,
  "propertyTypeId": 15,
  "name": "yabanci_dil",
  "displayName": "Yabancı Dil",
  "dataType": "SELECT",
  "options": "İngilizce,Almanca,Fransızca,İspanyolca",
  "isSearchable": true,
  "isFilterable": true,
  "sortOrder": 2
}
```

#### Response `data`

`InstitutionPropertyDto`

#### DELETE kısıtı

Bu özelliğe bağlı aktif `InstitutionPropertyValue` (okul/kampüs değerleri) varsa silinemez:
`"Cannot delete institution property with active property values"`

---

## 7. Hata Kodları ve İş Kuralları

| HTTP | Durum | Açıklama |
|------|-------|----------|
| 200 | OK | Başarılı GET / PUT / DELETE |
| 201 | Created | Başarılı POST |
| 400 | Bad Request | Geçersiz body / validasyon |
| 403 | Forbidden | JWT yok veya SYSTEM rolü yok |
| 404 | Not Found | Kayıt bulunamadı veya `isActive = false` |
| 409 | Conflict | İsim çakışması veya silme kısıtı |

### Benzersizlik Kuralları

| Entity | Benzersizlik kapsamı |
|--------|---------------------|
| InstitutionTypeGroup | `name` (global, aktif kayıtlar) |
| InstitutionType | `name` (global, aktif kayıtlar) |
| PropertyGroupType | `name` + `institutionTypeId` |
| PropertyType | `name` + `propertyGroupTypeId` |
| InstitutionProperty | `name` + `institutionTypeId` |

İsim karşılaştırması **case-insensitive** (büyük/küçük harf duyarsız).

### Form Validasyon Önerileri (Frontend)

| Alan | Kural |
|------|-------|
| `name` | Zorunlu; slug benzeri, boşluksuz/küçük harf önerilir (`anaokulu`, `ingilizce_egitimi`) |
| `displayName` | Zorunlu; kullanıcıya gösterilen metin |
| `institutionTypeId` | PropertyGroupType ve InstitutionProperty create'te zorunlu |
| `propertyGroupTypeId` | PropertyType create'te zorunlu |
| `propertyTypeId` | InstitutionProperty create'te önerilir (DB zorunlu) |
| `dataType` | InstitutionProperty'de zorunlu |
| `options` | `SELECT` / `MULTISELECT` seçildiğinde doldurulmalı |
| `minValue` / `maxValue` | `NUMBER` / `DECIMAL` için anlamlı |
| `minLength` / `maxLength` | `TEXT` / `TEXTAREA` için anlamlı |

---

## 8. Örnek Senaryolar

### 8.1 Tam Ağaç Yükleme (Admin Dashboard)

```http
GET /api/institutions/institution-types/admin
```

Response `data`: `InstitutionTypeListDto[]` — her tipin altında `propertyGroupTypeDtos[]`, onların altında `propertyTypes[]`.

### 8.2 Yeni Kurum Tipi + Özellik Seti Oluşturma

```http
# 1. Grup
POST /api/institutions/institution-type-groups
{ "name": "ozel_okul", "displayName": "Özel Okul", "sortOrder": 1 }

# 2. Tip (groupId = 1)
POST /api/institutions/institution-types
{ "name": "lise", "displayName": "Lise", "groupId": 1, "sortOrder": 1 }

# 3. Özellik grubu (institutionTypeId = dönen id)
POST /api/institutions/property-group-types
{ "name": "akademik", "displayName": "Akademik", "institutionTypeId": 10, "isMultiple": true }

# 4. Özellik tipi
POST /api/institutions/property-types
{ "name": "yabanci_dil", "displayName": "Yabancı Dil", "propertyGroupTypeId": 20 }

# 5. Kurum özelliği
POST /api/institutions/institution-properties
{
  "institutionTypeId": 10,
  "propertyTypeId": 30,
  "name": "ingilizce",
  "displayName": "İngilizce",
  "dataType": "BOOLEAN",
  "isSearchable": true,
  "isFilterable": true
}
```

### 8.3 Kademeli Silme

Silme işlemi **yapraktan köke** doğru yapılmalı:

```
InstitutionProperty → PropertyType → PropertyGroupType → InstitutionType → InstitutionTypeGroup
```

Örneğin `PropertyGroupType` silinmeden önce altındaki tüm `PropertyType` ve onlara bağlı `InstitutionProperty` kayıtları silinmeli (veya soft-delete edilmeli).

### 8.4 Okul Oluşturma Ekranı İçin

Kurum tipi seçim dropdown'u:

```http
GET /api/institutions/institution-types/summaries
```

Seçilen tipe göre özellik formu:

```http
GET /api/institutions/institution-types/{institutionTypeId}/properties
```

veya checkbox listesi için özellik grupları:

```http
GET /api/institutions/institution-types/{institutionTypeId}/property-group-types
GET /api/institutions/property-group-types/{propertyGroupTypeId}/property-types
```

---

## Ek Notlar

- **Cache:** Okuma endpoint'leri backend'de cache'lenebilir; admin CRUD sonrası veriler kısa sürede güncellenir (cache evict uygulanır).
- **InstitutionPropertyValue:** Okul/kampüs üzerindeki gerçek değerler bu dokümanın kapsamı dışındadır; ilgili endpoint'ler `/schools/{id}/properties`, `/campuses/{id}/properties` altındadır.
- **Swagger:** Tüm endpoint'ler `Institution Management` tag'i altında test edilebilir.

---

*Son güncelleme: Haziran 2026*
