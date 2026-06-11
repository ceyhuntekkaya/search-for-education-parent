package com.genixo.education.search.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to,
                                                @RequestParam String username,
                                                @RequestParam String code) {
        emailService.sendWelcomeEmail(to, username, code);
        return ResponseEntity.ok("Mail gönderildi: " + to);
    }
}

// http://localhost:8080/api/mail/send-email?to=ceyhun@genixo.ai&username=Ceyhun&code=123456