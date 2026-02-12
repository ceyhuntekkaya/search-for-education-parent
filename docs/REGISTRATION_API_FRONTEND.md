# Öğretmen & Eğitmen Kayıt API Dokümantasyonu

Frontend geliştiriciler için Öğretmen (TEACHER) ve Eğitmen (INSTRUCTOR) rolüyle kullanıcı kaydı endpoint'leri.

**Base URL:** `{API_BASE_URL}/users`

**Auth:** Bu endpoint'ler **kayıt** için kullanıldığından **JWT gerekmez** (public).

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

## Ortak Request DTO: UserRegistrationDto

Hem öğretmen hem eğitmen kaydı için aynı request body kullanılır:

```typescript
interface UserRegistrationDto {
  // Zorunlu alanlar
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword: string;

  // Opsiyonel
  phone?: string;
  userType?: string;   // "INSTITUTION_USER" - gönderilmezse backend otomatik atar

  // Konum (opsiyonel)
  countryId?: number;
  provinceId?: number;
  districtId?: number;
  neighborhoodId?: number;
  addressLine1?: string;
  addressLine2?: string;
  postalCode?: string;

  // KVKK / Onaylar (opsiyonel)
  acceptTerms?: boolean;
  acceptPrivacy?: boolean;
  acceptMarketing?: boolean;
}
```

### Validasyon Kuralları

| Alan | Kural |
|------|-------|
| `email` | Zorunlu, geçerli e-posta formatı (@ ve . içermeli) |
| `firstName` | Zorunlu |
| `lastName` | Zorunlu |
| `password` | Zorunlu, min 6 karakter |
| `confirmPassword` | `password` ile aynı olmalı |
| `userType` | Opsiyonel; null/undefined ise backend `INSTITUTION_USER` atar |

### UserType Enum

| Değer | Açıklama |
|-------|----------|
| `INSTITUTION_USER` | Kurum kullanıcısı (öğretmen/eğitmen için bu kullanılır) |
| `PARENT` | Veli |

---

## Response: UserDto

Başarılı kayıtta dönen kullanıcı nesnesi:

```typescript
interface UserDto {
  id: number;
  email: string;
  phone?: string;
  firstName: string;
  lastName: string;
  fullName: string;      // firstName + lastName birleşimi
  userType: string;      // "INSTITUTION_USER"
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  lastLoginAt?: string;  // ISO 8601
  profileImageUrl?: string;
  isActive: boolean;
  createdAt: string;     // ISO 8601

  // Konum
  country?: CountrySummaryDto;
  province?: ProvinceSummaryDto;
  district?: DistrictSummaryDto;
  neighborhood?: NeighborhoodSummaryDto;
  addressLine1?: string;
  addressLine2?: string;
  postalCode?: string;

  // Roller (TEACHER veya INSTRUCTOR burada görünür)
  userRoles?: UserRoleDto[];
  roles?: string[];      // ["TEACHER"] veya ["INSTRUCTOR"]
  authorities?: string[];
}
```

---

# 1. ÖĞRETMEN KAYDI (TEACHER)

`POST /users/register/teacher`

Öğretmen rolü ile yeni hesap oluşturur. Kayıt sonrası kullanıcı giriş yapıp **TeacherProfile** oluşturabilir (bkz. HR modülü).

**Auth:** Gerekmez

**Request Body:** `UserRegistrationDto`

```typescript
// Örnek
{
  "email": "ogretmen@example.com",
  "password": "123456",
  "confirmPassword": "123456",
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "phone": "5551234567"
}
```

**Response:** `ApiResponse<UserDto>` (201 Created)

**Başarılı:** `success: true`, `message: "Öğretmen kaydı başarılı. Profil oluşturmak için giriş yapabilirsiniz."`

**Hata Senaryoları (400):**
- `"User with this email already exists"` - E-posta zaten kayıtlı
- `"User with this phone number already exists"` - Telefon zaten kayıtlı

**Validasyon Hataları (422):**
- `"Email is required"`
- `"Invalid email format"`
- `"First name is required"`
- `"Last name is required"`
- `"Password is required"`
- `"Password must be at least 6 characters long"`
- `"Password and confirmation do not match"`

---

# 2. EĞİTMEN KAYDI (INSTRUCTOR)

`POST /users/register/instructor`

Eğitmen rolü ile yeni hesap oluşturur.

**Auth:** Gerekmez

**Request Body:** `UserRegistrationDto`

Aynı yapı öğretmen kaydı ile aynı (yukarıdaki örnek geçerli).

**Response:** `ApiResponse<UserDto>` (201 Created)

**Başarılı:** `success: true`, `message: "Eğitmen kaydı başarılı. Giriş yapabilirsiniz."`

**Hata Senaryoları:** Öğretmen kaydı ile aynı.

---

## Öğretmen Kaydı Sonrası Akış (TeacherProfile)

1. Kullanıcı `POST /users/register/teacher` ile kayıt olur
2. Giriş yapar (`POST /auth/login` veya benzeri)
3. JWT token alır
4. `POST /hr/teacher-profiles` ile öğretmen profilini oluşturur (TEACHER rolü gerekli)

Detaylar için `docs/HR_API_FRONTEND.md` dosyasına bakınız.

---

## Örnek Frontend Akışı (React/Vue Örneği)

```typescript
// Öğretmen kayıt formu
async function registerTeacher(formData: UserRegistrationDto) {
  const response = await fetch(`${API_BASE_URL}/users/register/teacher`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(formData),
  });

  const result = await response.json();

  if (!result.success) {
    throw new Error(result.message || 'Kayıt başarısız');
  }

  // result.data = UserDto
  return result.data;
}
```

```typescript
// Eğitmen kayıt formu
async function registerInstructor(formData: UserRegistrationDto) {
  const response = await fetch(`${API_BASE_URL}/users/register/instructor`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(formData),
  });

  const result = await response.json();

  if (!result.success) {
    throw new Error(result.message || 'Kayıt başarısız');
  }

  return result.data;
}
```

---

## Özet Tablo

| Endpoint | Rol | Açıklama |
|----------|-----|----------|
| `POST /users/register/teacher` | TEACHER | Öğretmen kaydı |
| `POST /users/register/instructor` | INSTRUCTOR | Eğitmen kaydı |
