# HR Modülü API Dokümantasyonu

Frontend geliştiriciler için İnsan Kaynakları (HR) modülü endpoint'leri, parametreleri ve nesne yapıları.

**Base URL:** `{API_BASE_URL}/hr`

**Tüm endpoint'ler (JWT gerektirenler hariç) için `Authorization: Bearer {token}` header'ı gerekebilir.**

---

## Genel Response Yapısı

Tüm endpoint'ler `ApiResponse<T>` wrapper'ı ile döner:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: string[] | null;
  timestamp: string;  // ISO 8601
  path?: string;
}
```

---

## Ortak Nesneler (Nested DTO'lar)

```typescript
interface ProvinceSummaryDto {
  id: number;
  name: string;
  code: string;
}

interface CampusSummaryDto {
  id: number;
  name: string;
  slug: string;
  email: string;
}

interface JobPostingSummaryDto {
  id: number;
  positionTitle: string;
  branch: string;
  employmentType: string;
  applicationDeadline: string;  // "YYYY-MM-DD"
  status: string;
  campus: CampusSummaryDto;
}

interface TeacherProfileSummaryDto {
  id: number;
  fullName: string;
  email: string;
  branch: string;
  profilePhotoUrl: string;
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

# 1. İŞ İLANLARI (Job Postings)

**Base Path:** `/hr/job-postings`

---

## 1.1 Yeni İlan Oluştur

`POST /hr/job-postings`

**Auth:** Gerekli

**Request Body:** `JobPostingCreateDto`
```typescript
{
  schoolId: number;              // Zorunlu
  positionTitle: string;         // Zorunlu, max 200
  branch: string;                // Zorunlu, max 100
  employmentType?: string;       // max 50
  startDate?: string;             // "YYYY-MM-DD"
  contractDuration?: string;      // max 100
  requiredExperienceYears?: number;
  requiredEducationLevel?: string; // max 50
  salaryMin?: number;
  salaryMax?: number;
  showSalary?: boolean;          // default: false
  description?: string;
  applicationDeadline?: string;   // "YYYY-MM-DD"
  status?: string;                // default: "DRAFT"
  isPublic?: boolean;             // default: true
  provinceIds?: number[];         // İl ID listesi
}
```

**Response:** `ApiResponse<JobPostingDto>`

**JobPostingDto:**
```typescript
{
  id: number;
  schoolId: number;
  campus: CampusSummaryDto;
  positionTitle: string;
  branch: string;
  employmentType: string;
  startDate: string;
  contractDuration: string;
  requiredExperienceYears: number;
  requiredEducationLevel: string;
  salaryMin: number;
  salaryMax: number;
  showSalary: boolean;
  description: string;
  applicationDeadline: string;
  status: string;                 // DRAFT | PUBLISHED | CLOSED | COMPLETED
  isPublic: boolean;
  provinces: ProvinceSummaryDto[];
  applicationCount: number;
  createdAt: string;
  updatedAt: string;
}
```

---

## 1.2 İlanları Listele / Ara

`GET /hr/job-postings`

**Query Params:**
| Param | Tip | Zorunlu | Açıklama |
|-------|-----|---------|----------|
| schoolId | number | Hayır | Okul ID filtresi |
| branch | string | Hayır | Branş filtresi |
| status | string | Hayır | DRAFT, PUBLISHED, CLOSED, COMPLETED |
| searchTerm | string | Hayır | Arama terimi |
| page | number | Hayır | Sayfa (default: 0) |
| size | number | Hayır | Sayfa boyutu (default: 20) |
| sortBy | string | Hayır | Sıralama alanı (default: createdAt) |
| sortDir | string | Hayır | ASC | DESC (default: DESC) |

**Response:** `ApiResponse<Page<JobPostingDto>>`

---

## 1.3 İlan Detayı

`GET /hr/job-postings/{id}`

**Response:** `ApiResponse<JobPostingDto>`

---

## 1.4 Okulun İlanları

`GET /hr/job-postings/by-school/{schoolId}`

**Query Params:** `page` (default: 0), `size` (default: 20)

**Response:** `ApiResponse<Page<JobPostingDto>>`

---

## 1.5 İlan Güncelle

`PUT /hr/job-postings/{id}`

**Request Body:** `JobPostingUpdateDto` (tüm alanlar opsiyonel)
```typescript
{
  positionTitle?: string;
  branch?: string;
  employmentType?: string;
  startDate?: string;
  contractDuration?: string;
  requiredExperienceYears?: number;
  requiredEducationLevel?: string;
  salaryMin?: number;
  salaryMax?: number;
  showSalary?: boolean;
  description?: string;
  applicationDeadline?: string;
  status?: string;
  isPublic?: boolean;
  provinceIds?: number[];
}
```

**Response:** `ApiResponse<JobPostingDto>`

---

## 1.6 İlan Sil

`DELETE /hr/job-postings/{id}`

**Response:** `ApiResponse<null>`

---

# 2. ÖĞRETMEN PROFİLLERİ (Teacher Profiles)

**Base Path:** `/hr/teacher-profiles`

---

## 2.1 Öğretmen Profili Oluştur

`POST /hr/teacher-profiles`

**Auth:** Gerekli – **Sadece TEACHER rolü**

**Request Body:** `TeacherProfileCreateDto`
```typescript
{
  fullName: string;         // Zorunlu, max 100
  email: string;            // Zorunlu, geçerli email, max 100
  phone?: string;           // max 20
  city?: string;            // max 50
  branch?: string;          // max 100
  educationLevel?: string;   // max 50
  experienceYears?: number;
  bio?: string;
  profilePhotoUrl?: string;  // max 500
  videoUrl?: string;        // Tanıtım videosu linki, max 500
  cvUrl?: string;           // max 500
  isActive?: boolean;       // default: true
  provinceIds?: number[];   // Çalışmak istediği iller
}
```

> **Not:** userId JWT'den alınır, kullanıcı kendi profili oluşturur.

**Response:** `ApiResponse<TeacherProfileDto>`

**TeacherProfileDto:**
```typescript
{
  id: number;
  userId: number;
  fullName: string;
  email: string;
  phone: string;
  city: string;
  branch: string;
  educationLevel: string;
  experienceYears: number;
  bio: string;
  profilePhotoUrl: string;
  videoUrl: string;
  cvUrl: string;
  isActive: boolean;
  provinces: ProvinceSummaryDto[];
  createdAt: string;
  updatedAt: string;
}
```

---

## 2.2 Profilleri Listele / Ara

`GET /hr/teacher-profiles`

**Query Params:**
| Param | Tip | Zorunlu | Açıklama |
|-------|-----|---------|----------|
| branch | string | Hayır | Branş filtresi |
| searchTerm | string | Hayır | Ad, email araması |
| page | number | Hayır | default: 0 |
| size | number | Hayır | default: 20 |
| sortBy | string | Hayır | default: createdAt |
| sortDir | string | Hayır | ASC | DESC |

**Response:** `ApiResponse<Page<TeacherProfileDto>>`

---

## 2.3 Profil Detayı

`GET /hr/teacher-profiles/{id}`

**Response:** `ApiResponse<TeacherProfileDto>`

---

## 2.4 Kullanıcı ID ile Profil Getir

`GET /hr/teacher-profiles/by-user/{userId}`

**Response:** `ApiResponse<TeacherProfileDto>`

---

## 2.5 Profil Güncelle

`PUT /hr/teacher-profiles/{id}`

**Request Body:** `TeacherProfileUpdateDto` (tüm alanlar opsiyonel)
```typescript
{
  fullName?: string;
  email?: string;
  phone?: string;
  city?: string;
  branch?: string;
  educationLevel?: string;
  experienceYears?: number;
  bio?: string;
  profilePhotoUrl?: string;
  videoUrl?: string;
  cvUrl?: string;
  isActive?: boolean;
  provinceIds?: number[];
}
```

**Response:** `ApiResponse<TeacherProfileDto>`

---

## 2.6 Profil Sil

`DELETE /hr/teacher-profiles/{id}`

**Response:** `ApiResponse<null>`

---

# 3. BAŞVURULAR (Applications)

**Base Path:** `/hr/applications`

---

## 3.1 Yeni Başvuru Oluştur

`POST /hr/applications`

**Auth:** Gerekli

**Request Body:** `ApplicationCreateDto`
```typescript
{
  jobPostingId: number;         // Zorunlu
  teacherProfileId: number;     // Zorunlu - Başvuran öğretmenin profil ID'si
  coverLetter?: string;         // Ön yazı
  documents?: ApplicationDocumentCreateDto[];  // İsteğe bağlı belgeler
}
```

**ApplicationDocumentCreateDto:**
```typescript
{
  documentName: string;   // Zorunlu, max 200
  documentUrl: string;    // Zorunlu, max 500
  documentType?: string;  // max 50 (örn: diploma, cv, sertifika)
  fileSize?: number;      // bytes
}
```

**Response:** `ApiResponse<ApplicationDto>`

**ApplicationDto:**
```typescript
{
  id: number;
  jobPostingId: number;
  jobPosting: JobPostingSummaryDto;
  teacherId: number;
  teacher: TeacherProfileSummaryDto;
  coverLetter: string;
  status: string;         // RECEIVED | UNDER_REVIEW | INTERVIEW_SCHEDULED | OFFER_MADE | ACCEPTED | REJECTED
  isWithdrawn: boolean;
  notes: ApplicationNoteDto[];
  documents: ApplicationDocumentDto[];
  createdAt: string;
  updatedAt: string;
}
```

---

## 3.2 Başvuruları Listele / Ara

`GET /hr/applications`

**Query Params:**
| Param | Tip | Zorunlu | Açıklama |
|-------|-----|---------|----------|
| jobPostingId | number | Hayır | İlan filtresi |
| teacherId | number | Hayır | Öğretmen filtresi |
| status | string | Hayır | Durum filtresi |
| page | number | Hayır | default: 0 |
| size | number | Hayır | default: 20 |
| sortBy | string | Hayır | default: createdAt |
| sortDir | string | Hayır | ASC | DESC |

**Response:** `ApiResponse<Page<ApplicationDto>>`

---

## 3.3 Başvuru Detayı

`GET /hr/applications/{id}`

**Response:** `ApiResponse<ApplicationDto>`

---

## 3.4 İlanın Başvuruları

`GET /hr/applications/by-job-posting/{jobPostingId}`

**Query Params:** `page`, `size`

**Response:** `ApiResponse<Page<ApplicationDto>>`

---

## 3.5 Öğretmenin Başvuruları

`GET /hr/applications/by-teacher/{teacherId}`

**Query Params:** `page`, `size`

**Response:** `ApiResponse<Page<ApplicationDto>>`

---

## 3.6 Okulun Tüm Başvuruları

`GET /hr/applications/by-campus/{campusId}`

**Query Params:** `page`, `size`

**Response:** `ApiResponse<Page<ApplicationDto>>`

---

## 3.7 Başvuru Durumunu Güncelle

`PATCH /hr/applications/{id}/status`

**Auth:** Gerekli

**Request Body:** `ApplicationStatusUpdateDto`
```typescript
{
  status: string;  // Zorunlu: RECEIVED | UNDER_REVIEW | INTERVIEW_SCHEDULED | OFFER_MADE | ACCEPTED | REJECTED
}
```

**Response:** `ApiResponse<ApplicationDto>`

---

## 3.8 Başvuruyu Geri Çek

`POST /hr/applications/{id}/withdraw`

**Auth:** Gerekli – Sadece başvuru sahibi (öğretmen)

**Response:** `ApiResponse<ApplicationDto>`

---

## 3.9 Başvuru Notları

`GET /hr/applications/{id}/notes`

**Response:** `ApiResponse<ApplicationNoteDto[]>`

**ApplicationNoteDto:**
```typescript
{
  id: number;
  applicationId: number;
  createdByUserId: number;
  createdByUserName: string;
  noteText: string;
  createdAt: string;
}
```

---

## 3.10 Başvuruya Not Ekle

`POST /hr/applications/{id}/notes`

**Auth:** Gerekli

**Request Body:**
```typescript
{
  noteText: string;  // Zorunlu
}
```

**Response:** `ApiResponse<ApplicationNoteDto>`

---

## 3.11 Başvuru Belgeleri

`GET /hr/applications/{id}/documents`

**Response:** `ApiResponse<ApplicationDocumentDto[]>`

**ApplicationDocumentDto:**
```typescript
{
  id: number;
  applicationId: number;
  documentName: string;
  documentUrl: string;
  documentType: string;
  fileSize: number;
  createdAt: string;
}
```

---

## 3.12 Başvuruya Belge Ekle

`POST /hr/applications/{id}/documents`

**Auth:** Gerekli

**Request Body:** `ApplicationDocumentCreateDto`
```typescript
{
  documentName: string;   // Zorunlu, max 200
  documentUrl: string;    // Zorunlu, max 500
  documentType?: string;  // max 50
  fileSize?: number;
}
```

**Response:** `ApiResponse<ApplicationDocumentDto>`

---

## 3.13 Başvuru Belgesini Sil

`DELETE /hr/applications/{id}/documents/{documentId}`

**Response:** `ApiResponse<null>`

---

# 4. BİLDİRİMLER (Notifications)

**Base Path:** `/hr/notifications`

**Tüm endpoint'ler JWT gerektirir (giriş yapmış kullanıcının bildirimleri)**

---

## 4.1 Kullanıcının Bildirimleri

`GET /hr/notifications`

**Query Params:** `page` (default: 0), `size` (default: 20)

**Response:** `ApiResponse<Page<HrNotificationDto>>`

**HrNotificationDto:**
```typescript
{
  id: number;
  userId: number;
  applicationId: number | null;
  title: string;
  message: string;
  type: string;  // APPLICATION_RECEIVED | STATUS_UPDATED | NEW_JOB_POSTED | APPLICATION_WITHDRAWN
  isRead: boolean;
  createdAt: string;
}
```

---

## 4.2 Okunmamış Bildirim Sayısı

`GET /hr/notifications/unread-count`

**Response:** `ApiResponse<{ unreadCount: number }>`

---

## 4.3 Bildirimi Okundu İşaretle

`PATCH /hr/notifications/{id}/read`

**Response:** `ApiResponse<null>`

---

## 4.4 Tüm Bildirimleri Okundu İşaretle

`PATCH /hr/notifications/mark-all-read`

**Response:** `ApiResponse<null>`

---

# Özet Enum Değerleri

| Alan | Değerler |
|------|----------|
| **JobPosting.status** | `DRAFT`, `PUBLISHED`, `CLOSED`, `COMPLETED` |
| **Application.status** | `RECEIVED`, `UNDER_REVIEW`, `INTERVIEW_SCHEDULED`, `OFFER_MADE`, `ACCEPTED`, `REJECTED` |
| **Notification.type** | `APPLICATION_RECEIVED`, `STATUS_UPDATED`, `NEW_JOB_POSTED`, `APPLICATION_WITHDRAWN` |

---

# Notlar

- Tarih formatı: `YYYY-MM-DD` (LocalDate)
- DateTime formatı: ISO 8601 (örn: `2026-02-12T08:40:47.300+03:00`)
- JWT: `Authorization: Bearer <token>` header'ı ile gönderilir
- 403: Yetkisiz erişim (örn: TEACHER rolü olmadan profil oluşturma)
- 404: Kaynak bulunamadı
- 422: Validasyon hatası
