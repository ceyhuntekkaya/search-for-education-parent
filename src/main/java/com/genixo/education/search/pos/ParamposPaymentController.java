package com.genixo.education.search.pos;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class ParamposPaymentController {

    private final ParamposPaymentService paymentService;

    // Next.js'ten çağrılır → iFrame URL döner
    @PostMapping("/init")
    public ResponseEntity<PaymentInitResponse> init(
            @RequestBody @Valid PaymentInitRequest request) {

        PaymentInitResponse response = paymentService.initIframePayment(request);

        if (response.success()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    // Parampos'tan POST callback gelir
    @PostMapping(value = "/callback",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> callback(@RequestParam Map<String, String> params) {
        log.info("Parampos callback: {}", params);

        String sonuc    = params.get("TURKPOS_RETVAL_Sonuc");
        String sonucStr = params.get("TURKPOS_RETVAL_Sonuc_Str");
        String islemId  = params.get("TURKPOS_RETVAL_Islem_ID");
        String dekontId = params.get("TURKPOS_RETVAL_Dekont_ID");

        if (sonuc != null && Integer.parseInt(sonuc) > 0) {
            log.info("✅ Ödeme başarılı. islemId={} dekontId={}", islemId, dekontId);
            // TODO: veritabanında ödeme durumunu güncelle
        } else {
            log.warn("❌ Ödeme başarısız. sonucStr={}", sonucStr);
        }

        // Parampos callback'e 200 dönmek yeterli
        return ResponseEntity.ok().build();
    }
}
