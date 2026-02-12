# Webinar / Etkinlik Modülü API Dokümantasyonu

Frontend geliştiriciler için Webinar/Etkinlik modülü endpoint'leri, parametreleri ve nesne yapıları.

**Base URL:** `{API_BASE_URL}/webinar`

---

## Genel Response Yapısı

Tüm endpoint'ler `ApiResponse<T>` wrapper'ı ile döner:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: string[] | null;
  timestamp: string;  // ISO 8601 (ör: "2025-02-12T10:30:00")
  path?: string;
}
```

---

## Ortak Nesneler (Nested DTO'lar)

```typescript
interface EventOrganizerSummaryDto {
  id: number;
  name: string;
  slug: string;
  type: string;        // OrganizerType enum
  logoUrl?: string;
}

interface EventCategorySummaryDto {
  id: number;
  name: string;
  slug: string;
  icon?: string;
}

interface EventSummaryDto {
  id: number;
  title: string;
  eventType: string;
  startDateTime: string;   // ISO 8601
  endDateTime: string;     // ISO 8601
  status: string;
  organizer?: EventOrganizerSummaryDto;
}
```

---

## Sayfalama (Page)

Liste dönen endpoint'ler Spring `Page` yapısı kullanır:

```typescript
interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;        // 0-based sayfa numarası
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}
```

---

## Enum Değerleri

### OrganizerType (Düzenleyen Tipi)
| Değer | Açıklama |
|-------|----------|
| `UNIVERSITY` | Üniversite |
| `EDUCATION_COMPANY` | Eğitim şirketi |
| `ASSOCIATION` | Dernek |
| `GOVERNMENT` | Devlet kurumu (MEB vb.) |
| `INDIVIDUAL_TRAINER` | Bireysel eğitmen |
| `PLATFORM` | Platform (Genixo) |
| `OTHER` | Diğer |

### EventType (Etkinlik Tipi)
| Değer | Açıklama |
|-------|----------|
| `WEBINAR` | Webinar |
| `SEMINAR` | Seminer |
| `TRAINING` | Eğitim |
| `WORKSHOP` | Atölye |

### DeliveryFormat (Gerçekleşme Formatı)
| Değer | Açıklama |
|-------|----------|
| `ONLINE` | Online |
| `IN_PERSON` | Yüz yüze |
| `HYBRID` | Hibrit |

### EventStatus (Etkinlik Durumu)
| Değer | Açıklama |
|-------|----------|
| `DRAFT` | Taslak |
| `PUBLISHED` | Yayında |
| `COMPLETED` | Tamamlandı |
| `CANCELLED` | İptal edildi |

### RegistrationStatus (Kayıt Durumu)
| Değer | Açıklama |
|-------|----------|
| `PENDING` | Onay bekliyor |
| `APPROVED` | Onaylandı |
| `REJECTED` | Reddedildi |
| `CANCELLED` | İptal edildi |

---

# 1. ETKİNLİK DÜZENLEYENLERİ (Event Organizers)

**Base Path:** `/webinar/organizers`

---

## 1.1 Yeni Düzenleyen Oluştur

`POST /webinar/organizers`

**Auth:** Oturum açmış kullanıcı gerekli (JWT)

**Request Body:** `EventOrganizerCreateDto`
```typescript
{
  name: string;              // Zorunlu, max 200
  type: string;              // Zorunlu - OrganizerType enum (örn: "UNIVERSITY")
  description?: string;      // max 5000
  logoUrl?: string;          // max 500
  website?: string;          // max 300
  email?: string;            // max 100
  phone?: string;            // max 20
  address?: string;          // max 500
  city?: string;             // max 50
  socialMediaLinks?: string; // max 2000 (JSON formatında)
  isVerified?: boolean;      // default: false
  isActive?: boolean;        // default: true
  slug?: string;             // Opsiyonel - verilmezse name'den otomatik üretilir
}
```

**Response:** `ApiResponse<EventOrganizerDto>` (201 Created)

**EventOrganizerDto:**
```typescript
{
  id: number;
  name: string;
  slug: string;
  type: string;
  description?: string;
  logoUrl?: string;
  website?: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  socialMediaLinks?: string;
  isVerified: boolean;
  isActive: boolean;
  createdByUserId?: number;
  createdByUserEmail?: string;
  eventCount: number;
  createdAt: string;   // ISO 8601
  updatedAt: string;  // ISO 8601
}
```

---

## 1.2 Düzenleyenleri Listele / Ara

`GET /webinar/organizers`

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| `type` | string | Hayır | - | OrganizerType enum filtresi |
| `searchTerm` | string | Hayır | - | İsim, açıklama veya slug'da arama |
| `isActive` | boolean | Hayır | - | Aktiflik filtresi |
| `page` | number | Hayır | 0 | Sayfa numarası |
| `size` | number | Hayır | 20 | Sayfa boyutu |
| `sortBy` | string | Hayır | "createdAt" | Sıralama alanı |
| `sortDir` | string | Hayır | "DESC" | "ASC" veya "DESC" |

**Response:** `ApiResponse<Page<EventOrganizerDto>>`

---

## 1.3 Aktif Düzenleyenler

`GET /webinar/organizers/active`

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan |
|-----------|-----|---------|------------|
| `page` | number | Hayır | 0 |
| `size` | number | Hayır | 20 |

**Response:** `ApiResponse<Page<EventOrganizerDto>>`

---

## 1.4 Düzenleyen Detayı (ID ile)

`GET /webinar/organizers/{id}`

**Path Parametreleri:** `id` - Düzenleyen ID

**Response:** `ApiResponse<EventOrganizerDto>`

---

## 1.5 Düzenleyen Detayı (slug ile)

`GET /webinar/organizers/by-slug/{slug}`

**Path Parametreleri:** `slug` - URL-friendly düzenleyen adı

**Response:** `ApiResponse<EventOrganizerDto>`

---

## 1.6 Düzenleyen Güncelle

`PUT /webinar/organizers/{id}`

**Path Parametreleri:** `id` - Düzenleyen ID

**Request Body:** `EventOrganizerUpdateDto` (tüm alanlar opsiyonel - sadece güncellenecek alanları gönderin)
```typescript
{
  name?: string;           // max 200
  type?: string;           // OrganizerType enum
  description?: string;    // max 5000
  logoUrl?: string;        // max 500
  website?: string;        // max 300
  email?: string;          // max 100
  phone?: string;          // max 20
  address?: string;        // max 500
  city?: string;           // max 50
  socialMediaLinks?: string; // max 2000
  isVerified?: boolean;
  isActive?: boolean;
  slug?: string;
}
```

**Response:** `ApiResponse<EventOrganizerDto>`

---

## 1.7 Düzenleyen Sil

`DELETE /webinar/organizers/{id}`

**Path Parametreleri:** `id` - Düzenleyen ID

**Not:** Etkinliği olan düzenleyen silinemez (400 Bad Request).

**Response:** `ApiResponse<null>`

---

# 2. ETKİNLİKLER (Events)

**Base Path:** `/webinar/events`

---

## 2.1 Yeni Etkinlik Oluştur

`POST /webinar/events`

**Auth:** Oturum açmış kullanıcı gerekli (JWT)

**Request Body:** `EventCreateDto`
```typescript
{
  organizerId: number;           // Zorunlu
  title: string;                 // Zorunlu, max 200
  description?: string;          // max 5000
  eventType: string;             // Zorunlu - EventType enum (WEBINAR, SEMINAR, TRAINING, WORKSHOP)
  deliveryFormat: string;        // Zorunlu - DeliveryFormat enum (ONLINE, IN_PERSON, HYBRID)
  startDateTime: string;         // Zorunlu - ISO 8601 (örn: "2025-03-15T14:00:00")
  endDateTime: string;           // Zorunlu - ISO 8601
  maxCapacity?: number;          // null = sınırsız
  location?: string;             // Yüz yüze etkinlik için adres
  onlineLink?: string;           // Zoom, Teams vb.
  targetAudience?: string;       // Hedef kitle (örn: "Matematik öğretmenleri")
  speakerName?: string;
  speakerBio?: string;
  coverImageUrl?: string;
  registrationDeadline?: string; // ISO 8601
  categoryId?: number;
  autoApproveRegistration?: boolean;  // default: true
  certificateEnabled?: boolean;       // default: false
  certificateTemplateUrl?: string;
  status?: string;               // default: "DRAFT" - EventStatus enum
}
```

**Response:** `ApiResponse<EventDto>` (201 Created)

**EventDto:**
```typescript
{
  id: number;
  organizerId: number;
  organizer: EventOrganizerSummaryDto;
  createdByUserId?: number;
  createdByUserEmail?: string;
  title: string;
  description?: string;
  eventType: string;
  deliveryFormat: string;
  startDateTime: string;   // ISO 8601
  endDateTime: string;     // ISO 8601
  maxCapacity?: number;
  remainingCapacity?: number;   // null = sınırsız
  location?: string;
  onlineLink?: string;
  targetAudience?: string;
  speakerName?: string;
  speakerBio?: string;
  coverImageUrl?: string;
  registrationDeadline?: string;
  status: string;
  autoApproveRegistration: boolean;
  certificateEnabled: boolean;
  certificateTemplateUrl?: string;
  categoryId?: number;
  category?: EventCategorySummaryDto;
  registrationCount: number;
  createdAt: string;
  updatedAt: string;
}
```

---

## 2.2 Etkinlikleri Listele / Ara

`GET /webinar/events`

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| `organizerId` | number | Hayır | - | Düzenleyen ID filtresi |
| `categoryId` | number | Hayır | - | Kategori ID filtresi |
| `eventType` | string | Hayır | - | EventType enum filtresi |
| `status` | string | Hayır | - | EventStatus enum filtresi |
| `searchTerm` | string | Hayır | - | Başlık, açıklama, hedef kitle, konuşmacıda arama |
| `startDateFrom` | string | Hayır | - | ISO 8601 - Başlangıç tarihinden |
| `startDateTo` | string | Hayır | - | ISO 8601 - Başlangıç tarihine |
| `page` | number | Hayır | 0 | Sayfa numarası |
| `size` | number | Hayır | 20 | Sayfa boyutu |
| `sortBy` | string | Hayır | "startDateTime" | Sıralama alanı |
| `sortDir` | string | Hayır | "ASC" | "ASC" veya "DESC" |

**Response:** `ApiResponse<Page<EventDto>>`

---

## 2.3 Yayındaki Etkinlikler

`GET /webinar/events/published`

Başlangıç tarihi bugünden sonra olan, yayında (PUBLISHED) etkinlikleri listeler.

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan |
|-----------|-----|---------|------------|
| `page` | number | Hayır | 0 |
| `size` | number | Hayır | 20 |

**Response:** `ApiResponse<Page<EventDto>>`

---

## 2.4 Etkinlik Detayı

`GET /webinar/events/{id}`

**Path Parametreleri:** `id` - Etkinlik ID

**Response:** `ApiResponse<EventDto>`

---

## 2.5 Düzenleyenin Etkinlikleri

`GET /webinar/events/by-organizer/{organizerId}`

**Path Parametreleri:** `organizerId` - Düzenleyen ID

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan |
|-----------|-----|---------|------------|
| `page` | number | Hayır | 0 |
| `size` | number | Hayır | 20 |

**Response:** `ApiResponse<Page<EventDto>>`

---

## 2.6 Etkinlik Güncelle

`PUT /webinar/events/{id}`

**Path Parametreleri:** `id` - Etkinlik ID

**Request Body:** `EventUpdateDto` (tüm alanlar opsiyonel)
```typescript
{
  organizerId?: number;
  title?: string;
  description?: string;
  eventType?: string;
  deliveryFormat?: string;
  startDateTime?: string;   // ISO 8601
  endDateTime?: string;    // ISO 8601
  maxCapacity?: number;
  location?: string;
  onlineLink?: string;
  targetAudience?: string;
  speakerName?: string;
  speakerBio?: string;
  coverImageUrl?: string;
  registrationDeadline?: string;
  categoryId?: number;
  autoApproveRegistration?: boolean;
  certificateEnabled?: boolean;
  certificateTemplateUrl?: string;
  status?: string;         // EventStatus enum
}
```

**Response:** `ApiResponse<EventDto>`

---

## 2.7 Etkinlik Sil

`DELETE /webinar/events/{id}`

**Path Parametreleri:** `id` - Etkinlik ID

**Response:** `ApiResponse<null>`

---

# 3. ETKİNLİK KAYITLARI (Event Registrations)

**Base Path:** `/webinar/registrations`

---

## 3.1 Etkinliğe Kayıt Ol

`POST /webinar/registrations`

**Request Body:** `EventRegistrationCreateDto`
```typescript
{
  eventId: number;         // Zorunlu
  teacherId: number;       // Zorunlu
  registrationNote?: string;  // max 1000
}
```

**Not:** 
- `autoApproveRegistration=true` ise kayıt otomatik APPROVED olur
- `autoApproveRegistration=false` ise PENDING olur
- Kontenjan doluysa 400 Bad Request döner
- Tekrar kayıt (aynı eventId + teacherId) 400 Bad Request döner

**Response:** `ApiResponse<EventRegistrationDto>` (201 Created)

**EventRegistrationDto:**
```typescript
{
  id: number;
  eventId: number;
  event: EventSummaryDto;
  teacherId: number;
  teacherEmail?: string;
  teacherName?: string;
  registrationNote?: string;
  status: string;           // RegistrationStatus enum
  attended: boolean;
  attendanceMarkedAt?: string;  // ISO 8601
  attendanceMarkedByUserId?: number;
  certificateUrl?: string;
  certificateGeneratedAt?: string;
  createdAt: string;
  updatedAt: string;
}
```

---

## 3.2 Kayıtları Listele / Ara

`GET /webinar/registrations`

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| `eventId` | number | Hayır | - | Etkinlik ID filtresi |
| `teacherId` | number | Hayır | - | Öğretmen ID filtresi |
| `status` | string | Hayır | - | RegistrationStatus enum filtresi |
| `page` | number | Hayır | 0 | Sayfa numarası |
| `size` | number | Hayır | 20 | Sayfa boyutu |
| `sortBy` | string | Hayır | "createdAt" | Sıralama alanı |
| `sortDir` | string | Hayır | "DESC" | "ASC" veya "DESC" |

**Response:** `ApiResponse<Page<EventRegistrationDto>>`

---

## 3.3 Kayıt Detayı

`GET /webinar/registrations/{id}`

**Path Parametreleri:** `id` - Kayıt ID

**Response:** `ApiResponse<EventRegistrationDto>`

---

## 3.4 Etkinliğin Kayıtları

`GET /webinar/registrations/by-event/{eventId}`

**Path Parametreleri:** `eventId` - Etkinlik ID

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan |
|-----------|-----|---------|------------|
| `page` | number | Hayır | 0 |
| `size` | number | Hayır | 20 |

**Response:** `ApiResponse<Page<EventRegistrationDto>>`

---

## 3.5 Öğretmenin Kayıtları

`GET /webinar/registrations/by-teacher/{teacherId}`

**Path Parametreleri:** `teacherId` - Öğretmen (User) ID

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Varsayılan |
|-----------|-----|---------|------------|
| `page` | number | Hayır | 0 |
| `size` | number | Hayır | 20 |

**Response:** `ApiResponse<Page<EventRegistrationDto>>`

---

## 3.6 Kayıt Durumunu Güncelle

`PUT /webinar/registrations/{id}/status`

**Path Parametreleri:** `id` - Kayıt ID

**Request Body:** `EventRegistrationStatusUpdateDto`
```typescript
{
  status: string;  // Zorunlu - PENDING | APPROVED | REJECTED | CANCELLED
}
```

**Response:** `ApiResponse<EventRegistrationDto>`

---

## 3.7 Katılım İşaretle

`PUT /webinar/registrations/{id}/attendance?attended={true|false}`

**Path Parametreleri:** `id` - Kayıt ID

**Query Parametreleri:**
| Parametre | Tip | Zorunlu | Açıklama |
|-----------|-----|---------|----------|
| `attended` | boolean | Evet | true = katıldı, false = katılmadı |

**Auth:** Oturum açmış kullanıcı gerekli (JWT)

**Response:** `ApiResponse<EventRegistrationDto>`

---

## 3.8 Kayıt Sil

`DELETE /webinar/registrations/{id}`

**Path Parametreleri:** `id` - Kayıt ID

**Response:** `ApiResponse<null>`

---

# Hata Kodları ve Mesajları

| HTTP | Durum | Açıklama |
|------|-------|----------|
| 200 | OK | Başarılı |
| 201 | Created | Oluşturuldu |
| 400 | Bad Request | İş kuralı ihlali (kontenjan dolu, tekrar kayıt vb.) |
| 404 | Not Found | Kaynak bulunamadı |
| 422 | Unprocessable Entity | Validasyon hatası |

**Hata Response Örneği (400/422):**
```typescript
{
  success: false;
  message: string;
  data: null;
  errors?: string[];   // Validasyon hatalarında
  timestamp: string;
  path: string;
}
```

---

# Tarih Formatı

Tüm tarih alanları **ISO 8601** formatında kullanılmalıdır:

- **Date-Time:** `"2025-03-15T14:00:00"` veya `"2025-03-15T14:00:00.000Z"`
- **Date only:** `"2025-03-15"`

---

# Örnek İstek Akışları

## Etkinlik Oluşturma ve Kayıt Akışı

1. `POST /webinar/organizers` → Düzenleyen oluştur
2. `POST /webinar/events` → Etkinlik oluştur (organizerId ile)
3. `PUT /webinar/events/{id}` → status: "PUBLISHED" ile yayınla
4. `POST /webinar/registrations` → Öğretmen kayıt olur
5. `PUT /webinar/registrations/{id}/status` → Onay/Red (otomatik onay kapalıysa)
6. `PUT /webinar/registrations/{id}/attendance?attended=true` → Katılım işaretle

## Public Etkinlik Listesi (Anasayfa)

1. `GET /webinar/events/published` → Yayındaki etkinlikleri listele
2. `GET /webinar/events/{id}` → Detay sayfası

## Öğretmen Kayıtlarım

1. `GET /webinar/registrations/by-teacher/{teacherId}` → Öğretmenin tüm kayıtları
