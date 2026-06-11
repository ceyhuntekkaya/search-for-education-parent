package com.genixo.education.search.pos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParamposPaymentService {

    private final RestTemplate paramposRestTemplate;
    private final ParamposProperties props;

    private static final String SOAP_ACTION =
            "http://turkpos.com.tr/To_Pre_Encrypting_OOS";

    public PaymentInitResponse initIframePayment(PaymentInitRequest req) {
        String soap = buildSoapEnvelope(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.set("SOAPAction", "\"" + SOAP_ACTION + "\"");

        try {
            ResponseEntity<String> response = paramposRestTemplate.exchange(
                    props.getBaseUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(soap, headers),
                    String.class
            );

            String token = parseToken(response.getBody());
            log.info("Parampos iFrame token alındı. islemId={}", req.islemId());

            String iframeUrl = props.getIframeBaseUrl() + "?s=" + token;
            return new PaymentInitResponse(true, iframeUrl, "OK");

        } catch (Exception e) {
            log.error("Parampos iFrame init hatası: {}", e.getMessage(), e);
            return new PaymentInitResponse(false, null, e.getMessage());
        }
    }

    private String buildSoapEnvelope(PaymentInitRequest req) {
        String aciklama = req.aciklama() != null ? "r|" + req.aciklama() : "r|Ödeme";
        String adSoyad  = "e|" + (req.adSoyad() != null ? req.adSoyad() : "");
        String gsm      = req.gsm() != null ? req.gsm() : "";

        return """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope
                xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:tns="http://turkpos.com.tr/">
              <soap:Body>
                <tns:To_Pre_Encrypting_OOS>
                  <tns:G>
                    <tns:CLIENT_CODE>%s</tns:CLIENT_CODE>
                    <tns:CLIENT_USERNAME>%s</tns:CLIENT_USERNAME>
                    <tns:CLIENT_PASSWORD>%s</tns:CLIENT_PASSWORD>
                  </tns:G>
                  <tns:GUID>%s</tns:GUID>
                  <tns:Borclu_Kisi_TC></tns:Borclu_Kisi_TC>
                  <tns:Borclu_Aciklama>%s</tns:Borclu_Aciklama>
                  <tns:Borclu_Tutar>%s</tns:Borclu_Tutar>
                  <tns:Borclu_GSM>%s</tns:Borclu_GSM>
                  <tns:Borclu_Odeme_Tip>r|Diğer</tns:Borclu_Odeme_Tip>
                  <tns:Borclu_AdSoyad>%s</tns:Borclu_AdSoyad>
                  <tns:Return_URL>%s</tns:Return_URL>
                  <tns:Islem_ID>%s</tns:Islem_ID>
                  <tns:Taksit>0</tns:Taksit>
                  <tns:Terminal_ID>%s</tns:Terminal_ID>
                </tns:To_Pre_Encrypting_OOS>
              </soap:Body>
            </soap:Envelope>
            """.formatted(
                props.getClientCode(),
                props.getClientUsername(),
                props.getClientPassword(),
                props.getGuid(),
                aciklama,
                req.tutar(),
                gsm,
                adSoyad,
                props.getCallbackUrl(),
                req.islemId(),
                props.getTerminalId()
        );
    }

    private String parseToken(String xml) {
        // <To_Pre_Encrypting_OOSResult>TOKEN</To_Pre_Encrypting_OOSResult>
        String open  = "<To_Pre_Encrypting_OOSResult>";
        String close = "</To_Pre_Encrypting_OOSResult>";
        int start = xml.indexOf(open);
        int end   = xml.indexOf(close);
        if (start == -1 || end == -1) {
            throw new RuntimeException("Token parse edilemedi. XML: " + xml);
        }
        return xml.substring(start + open.length(), end).trim();
    }
}
