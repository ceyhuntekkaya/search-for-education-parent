package com.genixo.education.search.pos;

import jakarta.validation.constraints.NotBlank;

public record PaymentInitRequest(
        @NotBlank String islemId,        // sizin sipariş UUID'niz
        @NotBlank String adSoyad,        // "e|" = kullanıcı düzenleyebilir
        @NotBlank String tutar,          // "100,00" formatında
        String aciklama,                 // opsiyonel
        String gsm                       // opsiyonel
) {}