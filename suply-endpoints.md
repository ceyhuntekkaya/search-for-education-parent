TEDARIK PORTALI - TÜM ENDPOINT LİSTESİ
🏢 SUPPLIER ENDPOINTS
Tedarikçi Yönetimi

POST /supply/suppliers - Yeni tedarikçi kaydı
GET /supply/suppliers - Tüm tedarikçileri listele (filtreleme, pagination)
GET /supply/suppliers/{id} - Tedarikçi detayı
PUT /supply/suppliers/{id} - Tedarikçi güncelle
PATCH /supply/suppliers/{id}/activate - Tedarikçiyi aktif yap
PATCH /supply/suppliers/{id}/deactivate - Tedarikçiyi pasif yap
DELETE /supply/suppliers/{id} - Tedarikçi sil
GET /supply/suppliers/{id}/products - Tedarikçinin ürünleri
GET /supply/suppliers/{id}/ratings - Tedarikçi değerlendirmeleri
GET /supply/suppliers/{id}/statistics - Tedarikçi istatistikleri (satış, teklif kazanma oranı)


📦 CATEGORY ENDPOINTS
Kategori Yönetimi

POST /supply/categories - Yeni kategori oluştur
GET /supply/categories - Tüm kategorileri listele (tree yapısında)
GET /supply/categories/{id} - Kategori detayı
PUT /supply/categories/{id} - Kategori güncelle
DELETE /supply/categories/{id} - Kategori sil
GET /supply/categories/{id}/subcategories - Alt kategorileri getir
GET /supply/categories/{id}/attributes - Kategoriye ait özellikler
PATCH /supply/categories/{id}/activate - Kategoriyi aktif yap
PATCH /supply/categories/{id}/deactivate - Kategoriyi pasif yap
PATCH /supply/categories/{id}/reorder - Kategori sırasını değiştir

Kategori Özellik Yönetimi

POST /supply/categories/{id}/attributes - Kategoriye özellik ekle
PUT /supply/categories/{id}/attributes/{attributeId} - Özellik güncelle
DELETE /supply/categories/{id}/attributes/{attributeId} - Özellik sil


🛍️ PRODUCT ENDPOINTS
Ürün Yönetimi

POST /supply/products - Yeni ürün ekle
GET /supply/products - Tüm ürünleri listele (filtreleme, arama, pagination)
GET /supply/products/{id} - Ürün detayı
PUT /supply/products/{id} - Ürün güncelle
DELETE /supply/products/{id} - Ürün sil
PATCH /supply/products/{id}/status - Ürün durumunu değiştir (aktif/pasif/stokta yok)
GET /supply/products/search - Ürün ara (kategori, fiyat aralığı, tedarikçi vb.)
GET /supply/products/by-category/{categoryId} - Kategoriye göre ürünler
GET /supply/products/by-supplier/{supplierId} - Tedarikçiye göre ürünler
PATCH /supply/products/{id}/stock - Stok güncelle

Ürün Görselleri

POST /supply/products/{id}/images - Ürüne görsel ekle
GET /supply/products/{id}/images - Ürün görsellerini listele
DELETE /supply/products/{id}/images/{imageId} - Görsel sil
PATCH /supply/products/{id}/images/{imageId}/set-main - Ana görsel yap
PATCH /supply/products/{id}/images/reorder - Görsel sırasını değiştir

Ürün Özellikleri

POST /supply/products/{id}/attributes - Ürüne özellik ekle
GET /supply/products/{id}/attributes - Ürün özelliklerini listele
PUT /supply/products/{id}/attributes/{attributeId} - Özellik güncelle
DELETE /supply/products/{id}/attributes/{attributeId} - Özellik sil

Ürün Varyantları

POST /supply/products/{id}/variants - Ürüne varyant ekle
GET /supply/products/{id}/variants - Ürün varyantlarını listele
GET /supply/products/{id}/variants/{variantId} - Varyant detayı
PUT /supply/products/{id}/variants/{variantId} - Varyant güncelle
DELETE /supply/products/{id}/variants/{variantId} - Varyant sil
PATCH /supply/products/{id}/variants/{variantId}/activate - Varyant aktif yap
PATCH /supply/products/{id}/variants/{variantId}/deactivate - Varyant pasif yap
PATCH /supply/products/{id}/variants/{variantId}/stock - Varyant stoğu güncelle

Ürün İndirimleri

POST /supply/products/{id}/discounts - Ürüne indirim ekle
GET /supply/products/{id}/discounts - Ürün indirimlerini listele
GET /supply/products/{id}/discounts/{discountId} - İndirim detayı
PUT /supply/products/{id}/discounts/{discountId} - İndirim güncelle
DELETE /supply/products/{id}/discounts/{discountId} - İndirim sil
PATCH /supply/products/{id}/discounts/{discountId}/activate - İndirim aktif yap
PATCH /supply/products/{id}/discounts/{discountId}/deactivate - İndirim pasif yap
GET /supply/products/{id}/effective-price - Geçerli fiyat hesapla (indirimlerle)

Ürün Dökümanları

POST /supply/products/{id}/documents - Ürüne döküman ekle
GET /supply/products/{id}/documents - Ürün dökümanlarını listele
DELETE /supply/products/{id}/documents/{documentId} - Döküman sil


📋 RFQ (ALIM İLANI) ENDPOINTS
RFQ Yönetimi

POST /supply/rfqs - Yeni alım ilanı oluştur
GET /supply/rfqs - Tüm ilanları listele (filtreleme, pagination)
GET /supply/rfqs/{id} - İlan detayı
PUT /supply/rfqs/{id} - İlan güncelle
DELETE /supply/rfqs/{id} - İlan sil
PATCH /supply/rfqs/{id}/publish - İlanı yayınla
PATCH /supply/rfqs/{id}/close - İlanı kapat
PATCH /supply/rfqs/{id}/cancel - İlanı iptal et
GET /supply/rfqs/by-company/{companyId} - Şirkete göre ilanlar
GET /supply/rfqs/active - Aktif ilanlar
GET /supply/rfqs/{id}/quotations - İlana gelen teklifler
GET /supply/rfqs/{id}/comparison - Teklif karşılaştırma tablosu

RFQ Items (İlan Kalemleri)

POST /supply/rfqs/{id}/items - İlana kalem ekle
GET /supply/rfqs/{id}/items - İlan kalemlerini listele
PUT /supply/rfqs/{id}/items/{itemId} - Kalem güncelle
DELETE /supply/rfqs/{id}/items/{itemId} - Kalem sil

RFQ Invitations (Davetiye İlanlar İçin)

POST /supply/rfqs/{id}/invitations - Tedarikçi davet et
GET /supply/rfqs/{id}/invitations - Davet edilen tedarikçiler
DELETE /supply/rfqs/{id}/invitations/{invitationId} - Daveti iptal et
POST /supply/rfqs/{id}/invitations/bulk - Toplu tedarikçi davet et


💰 QUOTATION (TEKLİF) ENDPOINTS
Teklif Yönetimi

POST /supply/quotations - Yeni teklif oluştur
GET /supply/quotations - Tüm teklifleri listele (filtreleme, pagination)
GET /supply/quotations/{id} - Teklif detayı
PUT /supply/quotations/{id} - Teklif güncelle
DELETE /supply/quotations/{id} - Teklif sil
PATCH /supply/quotations/{id}/submit - Teklifi gönder
PATCH /supply/quotations/{id}/accept - Teklifi kabul et
PATCH /supply/quotations/{id}/reject - Teklifi reddet
GET /supply/quotations/by-rfq/{rfqId} - İlana göre teklifler
GET /supply/quotations/by-supplier/{supplierId} - Tedarikçiye göre teklifler
GET /supply/quotations/by-company/{companyId} - Şirkete göre teklifler
GET /supply/quotations/{id}/versions - Teklif versiyonları
POST /supply/quotations/{id}/duplicate - Teklifi kopyala (yeni versiyon)

Teklif Kalemleri

POST /supply/quotations/{id}/items - Teklife kalem ekle
GET /supply/quotations/{id}/items - Teklif kalemlerini listele
PUT /supply/quotations/{id}/items/{itemId} - Kalem güncelle
DELETE /supply/quotations/{id}/items/{itemId} - Kalem sil
PATCH /supply/quotations/{id}/items/{itemId}/discount - Kaleme özel indirim uygula


📦 ORDER (SİPARİŞ) ENDPOINTS
Sipariş Yönetimi

POST /supply/orders - Yeni sipariş oluştur (teklif onayından)
GET /supply/orders - Tüm siparişleri listele (filtreleme, pagination)
GET /supply/orders/{id} - Sipariş detayı
PUT /supply/orders/{id} - Sipariş güncelle
PATCH /supply/orders/{id}/status - Sipariş durumu güncelle
PATCH /supply/orders/{id}/confirm - Siparişi onayla
PATCH /supply/orders/{id}/cancel - Siparişi iptal et
PATCH /supply/orders/{id}/ship - Kargoya verildi işaretle
PATCH /supply/orders/{id}/deliver - Teslim edildi işaretle
GET /supply/orders/by-company/{companyId} - Şirkete göre siparişler
GET /supply/orders/by-supplier/{supplierId} - Tedarikçiye göre siparişler
GET /supply/orders/{id}/tracking - Kargo takip bilgisi
PATCH /supply/orders/{id}/tracking - Kargo takip numarası güncelle
GET /supply/orders/{id}/invoice - Fatura bilgisi
PATCH /supply/orders/{id}/invoice - Fatura numarası ekle

Sipariş Kalemleri

GET /supply/orders/{id}/items - Sipariş kalemlerini listele


💳 PAYMENT (ÖDEME) ENDPOINTS
Ödeme Yönetimi

POST /supply/payments - Yeni ödeme kaydı oluştur
GET /supply/payments/{id} - Ödeme detayı
GET /supply/payments/by-order/{orderId} - Siparişe ait ödeme
PATCH /supply/payments/{id}/status - Ödeme durumu güncelle
PATCH /supply/payments/{id}/confirm - Ödeme onayı
PATCH /supply/payments/{id}/fail - Ödeme başarısız işaretle
POST /supply/payments/{id}/refund - Ödeme iadesi
GET /supply/payments/by-company/{companyId} - Şirkete göre ödemeler


❤️ WISHLIST (FAVORİLER) ENDPOINTS
Favori Yönetimi

POST /supply/wishlists - Favorilere ekle
GET /supply/wishlists - Kullanıcının favorilerini listele
DELETE /supply/wishlists/{id} - Favorilerden çıkar
GET /supply/wishlists/check/{productId} - Ürün favoride mi kontrol et
DELETE /supply/wishlists/by-product/{productId} - Ürünü favorilerden çıkar


💬 CONVERSATION & MESSAGE ENDPOINTS
Konuşma Yönetimi

POST /supply/conversations - Yeni konuşma başlat
GET /supply/conversations - Kullanıcının konuşmalarını listele
GET /supply/conversations/{id} - Konuşma detayı
DELETE /supply/conversations/{id} - Konuşmayı sil
GET /supply/conversations/by-company/{companyId} - Şirkete göre konuşmalar
GET /supply/conversations/by-supplier/{supplierId} - Tedarikçiye göre konuşmalar
GET /supply/conversations/by-product/{productId} - Ürüne göre konuşmalar
GET /supply/conversations/by-quotation/{quotationId} - Teklife göre konuşmalar
GET /supply/conversations/by-order/{orderId} - Siparişe göre konuşmalar
GET /supply/conversations/unread-count - Okunmamış konuşma sayısı

Mesaj Yönetimi

POST /supply/conversations/{id}/messages - Mesaj gönder
GET /supply/conversations/{id}/messages - Konuşma mesajlarını listele (pagination)
GET /supply/messages/{id} - Mesaj detayı
PATCH /supply/messages/{id}/read - Mesajı okundu işaretle
PATCH /supply/conversations/{id}/messages/mark-all-read - Tüm mesajları okundu işaretle
DELETE /supply/messages/{id} - Mesaj sil


⭐ SUPPLIER RATING (DEĞERLENDİRME) ENDPOINTS
Tedarikçi Değerlendirme

POST /supply/supplier-ratings - Tedarikçi değerlendir
GET /supply/supplier-ratings/by-supplier/{supplierId} - Tedarikçi değerlendirmeleri
GET /supply/supplier-ratings/by-order/{orderId} - Siparişe göre değerlendirme
GET /supply/supplier-ratings/{id} - Değerlendirme detayı
PUT /supply/supplier-ratings/{id} - Değerlendirme güncelle
DELETE /supply/supplier-ratings/{id} - Değerlendirme sil


📊 DASHBOARD & STATISTICS ENDPOINTS
Company Dashboard

GET /supply/dashboard/company/summary - Genel özet (toplam harcama, aktif siparişler vb.)
GET /supply/dashboard/company/spending-analytics - Harcama analitiği
GET /supply/dashboard/company/top-suppliers - En çok alım yapılan tedarikçiler
GET /supply/dashboard/company/top-products - En çok alınan ürünler
GET /supply/dashboard/company/rfq-statistics - İlan istatistikleri
GET /supply/dashboard/company/order-statistics - Sipariş istatistikleri

Supplier Dashboard

GET /supply/dashboard/supplier/summary - Genel özet (toplam satış, aktif teklifler vb.)
GET /supply/dashboard/supplier/sales-analytics - Satış analitiği
GET /supply/dashboard/supplier/quotation-statistics - Teklif istatistikleri (kazanma oranı)
GET /supply/dashboard/supplier/top-products - En çok satan ürünler
GET /supply/dashboard/supplier/top-customers - En çok alım yapan müşteriler
GET /supply/dashboard/supplier/performance - Performans metrikleri


🔔 NOTIFICATION ENDPOINTS
Bildirim Yönetimi

GET /supply/notifications - Kullanıcı bildirimlerini listele
GET /supply/notifications/unread-count - Okunmamış bildirim sayısı
PATCH /supply/notifications/{id}/read - Bildirimi okundu işaretle
PATCH /supply/notifications/mark-all-read - Tüm bildirimleri okundu işaretle
DELETE /supply/notifications/{id} - Bildirim sil


📁 FILE UPLOAD ENDPOINTS
Dosya Yükleme

POST /supply/files/upload - Dosya yükle (görsel, döküman vb.)
DELETE /supply/files/{fileId} - Dosya sil
GET /supply/files/{fileId} - Dosya indir


📈 REPORT ENDPOINTS
Raporlar

GET /supply/reports/company/procurement - Satın alma raporu (Excel/PDF)
GET /supply/reports/company/spending-by-category - Kategoriye göre harcama
GET /supply/reports/company/spending-by-supplier - Tedarikçiye göre harcama
GET /supply/reports/supplier/sales - Satış raporu (Excel/PDF)
GET /supply/reports/supplier/quotation-performance - Teklif performans raporu


🔍 SEARCH & FILTER ENDPOINTS
Genel Arama

GET /supply/search - Global arama (ürün, tedarikçi, ilan vb.)
GET /supply/search/suggestions - Arama önerileri (autocomplete)


