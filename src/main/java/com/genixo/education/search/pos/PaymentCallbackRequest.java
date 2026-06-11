package com.genixo.education.search.pos;

import org.springframework.web.bind.annotation.RequestParam;

public record PaymentCallbackRequest(
        @RequestParam("TURKPOS_RETVAL_Sonuc")     String sonuc,
        @RequestParam("TURKPOS_RETVAL_Sonuc_Str") String sonucStr,
        @RequestParam("TURKPOS_RETVAL_Islem_ID")  String islemId,
        @RequestParam("TURKPOS_RETVAL_Dekont_ID") String dekontId,
        @RequestParam("TURKPOS_RETVAL_GUID")      String guid
) {}
