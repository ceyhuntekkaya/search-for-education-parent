# Postman İstek Örnekleri - GetAllProducts Endpoint

## Endpoint Bilgileri
- **Method:** GET
- **Base URL:** `http://localhost:8080/api/supply/products`
- **Content-Type:** application/json (isteğe bağlı, query parametreler kullanılıyor)

---

## Örnek 1: Basit İstek (Varsayılan Parametrelerle)

### URL
```
GET http://localhost:8080/api/supply/products
```

### Query Parameters
(Hiç parametre göndermeye gerek yok, varsayılanlar kullanılır)
- `page` = 0 (varsayılan)
- `size` = 20 (varsayılan)
- `sortBy` = id (varsayılan)
- `sortDir` = DESC (varsayılan)

### Postman'de Nasıl Kullanılır:
1. Method: **GET** seçin
2. URL: `http://localhost:8080/api/supply/products`
3. Query parametreleri boş bırakabilirsiniz

---

## Örnek 2: Arama Terimi ile

### URL
```
GET http://localhost:8080/api/supply/products?searchTerm=laptop
```

### Query Parameters
| Key | Value |
|-----|-------|
| searchTerm | laptop |

### Postman'de:
1. Method: **GET**
2. URL: `http://localhost:8080/api/supply/products`
3. **Params** sekmesine gidin:
   - Key: `searchTerm`, Value: `laptop`

---

## Örnek 3: Kategori ve Tedarikçi Filtresi ile

### URL
```
GET http://localhost:8080/api/supply/products?categoryId=1&supplierId=5
```

### Query Parameters
| Key | Value |
|-----|-------|
| categoryId | 1 |
| supplierId | 5 |

---

## Örnek 4: Durum (Status) Filtresi ile

### URL
```
GET http://localhost:8080/api/supply/products?status=ACTIVE
```

### Query Parameters
| Key | Value |
|-----|-------|
| status | ACTIVE |

### Status Değerleri:
- `ACTIVE` - Aktif
- `PASSIVE` - Pasif
- `OUT_OF_STOCK` - Stokta Yok
- `DISCONTINUED` - Üretimi Durdu

---

## Örnek 5: Fiyat Aralığı ile Filtreleme

### URL
```
GET http://localhost:8080/api/supply/products?minPrice=100&maxPrice=1000
```

### Query Parameters
| Key | Value |
|-----|-------|
| minPrice | 100 |
| maxPrice | 1000 |

---

## Örnek 6: Tüm Filtrelerle (Kapsamlı Örnek)

### URL
```
GET http://localhost:8080/api/supply/products?searchTerm=laptop&categoryId=2&supplierId=3&status=ACTIVE&minPrice=500&maxPrice=2000&page=0&size=10&sortBy=name&sortDir=ASC
```

### Query Parameters
| Key | Value | Açıklama |
|-----|-------|----------|
| searchTerm | laptop | Arama terimi (isim, SKU, açıklama) |
| categoryId | 2 | Kategori ID |
| supplierId | 3 | Tedarikçi ID |
| status | ACTIVE | Ürün durumu |
| minPrice | 500 | Minimum fiyat |
| maxPrice | 2000 | Maksimum fiyat |
| page | 0 | Sayfa numarası (0'dan başlar) |
| size | 10 | Sayfa boyutu |
| sortBy | name | Sıralama alanı (id, name, price, createdDate, vb.) |
| sortDir | ASC | Sıralama yönü (ASC veya DESC) |

---

## Örnek 7: Pagination ile

### URL
```
GET http://localhost:8080/api/supply/products?page=1&size=50&sortBy=createdDate&sortDir=DESC
```

### Query Parameters
| Key | Value |
|-----|-------|
| page | 1 |
| size | 50 |
| sortBy | createdDate |
| sortDir | DESC |

---

## cURL Örnekleri

### Basit İstek
```bash
curl -X GET "http://localhost:8080/api/supply/products"
```

### Arama ile
```bash
curl -X GET "http://localhost:8080/api/supply/products?searchTerm=laptop&categoryId=1&status=ACTIVE"
```

### Tüm Filtrelerle
```bash
curl -X GET "http://localhost:8080/api/supply/products?searchTerm=laptop&categoryId=2&supplierId=3&status=ACTIVE&minPrice=500&maxPrice=2000&page=0&size=10&sortBy=name&sortDir=ASC"
```

---

## Postman Collection Önerisi

Postman'de test ederken:

1. **Yeni Request oluşturun**
   - Method: GET
   - URL: `http://localhost:8080/api/supply/products`

2. **Query Parameters eklemek için:**
   - URL'in altındaki **Params** sekmesine tıklayın
   - Key-Value çiftleri ekleyin
   - Postman otomatik olarak URL'e ekleyecektir

3. **Headers (isteğe bağlı):**
   - `Content-Type: application/json` (GET isteğinde genelde gerekmez)

4. **Send** butonuna tıklayın

---

## Beklenen Response Formatı

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Ürün Adı",
        "sku": "SKU123",
        // ... diğer ürün alanları
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 100,
    "totalPages": 5,
    "number": 0,
    "size": 20
  },
  "path": "/api/supply/products",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Notlar

- Tüm query parametreleri **opsiyonel**dir (required = false)
- `page` varsayılan değeri: **0**
- `size` varsayılan değeri: **20**
- `sortBy` varsayılan değeri: **id**
- `sortDir` varsayılan değeri: **DESC**
- `status` parametresi enum değerleri kabul eder: ACTIVE, PASSIVE, OUT_OF_STOCK, DISCONTINUED
- Server port'u farklıysa (varsayılan 8080) URL'yi buna göre değiştirin
