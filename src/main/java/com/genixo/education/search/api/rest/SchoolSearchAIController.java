package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.ai.*;
import com.genixo.education.search.service.ai.ConversationStats;
import com.genixo.education.search.service.ai.SchoolSearchAIOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/school-search/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "School Search AI", description = "AI destekli okul arama API'leri")
public class SchoolSearchAIController {

    private final SchoolSearchAIOrchestrationService orchestrationService;

    /**
     * Kullanıcı mesajı gönder ve AI yanıtı al
     */
    @PostMapping("/chat")
    @Operation(summary = "AI ile sohbet et", description = "Kullanıcı mesajı gönder ve AI yanıtı al")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            Authentication authentication
    ) {
        try {
            log.info("Received chat message from user: {}", request.getUserId());

            // Authentication'dan user ID'yi al (security varsa)
            if (authentication != null && authentication.isAuthenticated()) {
                // TODO: Authentication'dan gerçek user ID'yi al
                // Long userId = ((UserDetails) authentication.getPrincipal()).getId();
                // request.setUserId(userId);
            }

            ChatMessageResponse response = orchestrationService.processUserMessage(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing chat message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatMessageResponse.builder()
                            .content("Üzgünüm, bir hata oluştu. Lütfen daha sonra tekrar deneyin.")
                            .role("ASSISTANT")
                            .timestamp(java.time.LocalDateTime.now())
                            .isFormComplete(false)
                            .build());
        }
    }

    /**
     * Conversation geçmişini getir
     */
    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Conversation geçmişini getir", description = "Belirli bir conversation'ın tüm mesajlarını getir")
    public ResponseEntity<ConversationDTO> getConversation(
            @PathVariable Long conversationId
    ) {
        try {
            log.info("Fetching conversation: {}", conversationId);
            ConversationDTO conversation = orchestrationService.getConversationHistory(conversationId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Error fetching conversation: {}", conversationId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Kullanıcının tüm conversation'larını getir
     */
    @GetMapping("/conversations/user/{userId}")
    @Operation(summary = "Kullanıcının conversation'larını getir", description = "Kullanıcının tüm conversation geçmişini listele")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(
            @PathVariable Long userId
    ) {
        try {
            log.info("Fetching conversations for user: {}", userId);
            List<ConversationDTO> conversations = orchestrationService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error fetching user conversations: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Conversation'ı sıfırla (yeni arama başlat)
     */
    @PostMapping("/conversations/{conversationId}/reset")
    @Operation(summary = "Conversation'ı sıfırla", description = "Mevcut conversation'ı tamamla ve yeni arama başlat")
    public ResponseEntity<MessageResponse> resetConversation(
            @PathVariable Long conversationId
    ) {
        try {
            log.info("Resetting conversation: {}", conversationId);
            orchestrationService.resetConversation(conversationId);
            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Conversation sıfırlandı. Yeni bir arama başlatabilirsiniz.")
                    .success(true)
                    .build());
        } catch (Exception e) {
            log.error("Error resetting conversation: {}", conversationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .message("Conversation sıfırlanırken bir hata oluştu.")
                            .success(false)
                            .build());
        }
    }

    /**
     * Form özetini getir
     */
    @GetMapping("/conversations/{conversationId}/form-summary")
    @Operation(summary = "Form özetini getir", description = "Conversation'daki form verilerinin özetini getir")
    public ResponseEntity<FormSummaryResponse> getFormSummary(
            @PathVariable Long conversationId
    ) {
        try {
            log.info("Fetching form summary for conversation: {}", conversationId);
            String summary = orchestrationService.getFormSummary(conversationId);
            return ResponseEntity.ok(FormSummaryResponse.builder()
                    .conversationId(conversationId)
                    .summary(summary)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching form summary: {}", conversationId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Conversation istatistikleri
     */
    @GetMapping("/conversations/{conversationId}/stats")
    @Operation(summary = "Conversation istatistikleri", description = "Conversation'a ait istatistikleri getir")
    public ResponseEntity<ConversationStats> getConversationStats(
            @PathVariable Long conversationId
    ) {
        try {
            log.info("Fetching stats for conversation: {}", conversationId);
            var stats = orchestrationService.getConversationStats(conversationId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching conversation stats: {}", conversationId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Conversation'ı export et
     */
    @GetMapping("/conversations/{conversationId}/export")
    @Operation(summary = "Conversation'ı export et", description = "Conversation'ı JSON formatında export et")
    public ResponseEntity<SchoolSearchAIOrchestrationService.ConversationExportDTO> exportConversation(
            @PathVariable Long conversationId
    ) {
        try {
            log.info("Exporting conversation: {}", conversationId);
            var export = orchestrationService.exportConversation(conversationId);
            return ResponseEntity.ok(export);
        } catch (Exception e) {
            log.error("Error exporting conversation: {}", conversationId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * AI sağlık kontrolü
     */
    @GetMapping("/health")
    @Operation(summary = "AI sağlık kontrolü", description = "Ollama servisinin sağlık durumunu kontrol et")
    public ResponseEntity<HealthCheckResponse> checkHealth() {
        try {
            boolean isHealthy = orchestrationService.checkAIHealth();
            return ResponseEntity.ok(HealthCheckResponse.builder()
                    .healthy(isHealthy)
                    .service("Ollama AI")
                    .message(isHealthy ? "Service is running" : "Service is down")
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Health check failed", e);
            return ResponseEntity.ok(HealthCheckResponse.builder()
                    .healthy(false)
                    .service("Ollama AI")
                    .message("Health check failed: " + e.getMessage())
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }
    }

    /**
     * Yeni conversation başlat (opsiyonel - otomatik de oluşur)
     */
    /**
     * Yeni conversation başlat (opsiyonel - otomatik de oluşur)
     */
    @PostMapping("/conversations/start")
    @Operation(summary = "Yeni conversation başlat", description = "Kullanıcı için yeni bir conversation başlat")
    public ResponseEntity<ConversationStartResponse> startConversation(
            @RequestBody ConversationStartRequest request
    ) {
        try {
            log.info("Starting new conversation for user: {}", request.getUserId());

            // İlk "Merhaba" mesajını gönder
            ChatMessageRequest chatRequest = ChatMessageRequest.builder()
                    .userId(request.getUserId())
                    .message("Merhaba") // Bu ilk mesaj olarak algılanacak
                    .conversationId(null) // Yeni conversation
                    .build();

            ChatMessageResponse response = orchestrationService.processUserMessage(chatRequest);

            ResponseEntity<ConversationStartResponse> result =  ResponseEntity.ok(ConversationStartResponse.builder()
                    .conversationId(response.getConversationId())
                    .welcomeMessage(response.getContent())
                    .formData(response.getExtractedFormData())
                    .success(true)
                    .build());

            return result;
        } catch (Exception e) {
            log.error("Error starting conversation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ConversationStartResponse.builder()
                            .success(false)
                            .welcomeMessage("Conversation başlatılırken bir hata oluştu.")
                            .build());
        }
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ConversationStartResponse {
        private Long conversationId;
        private String welcomeMessage;
        private FormDataDTO formData;
        private Boolean success;
    }

    // ============= Response DTOs =============

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MessageResponse {
        private String message;
        private Boolean success;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FormSummaryResponse {
        private Long conversationId;
        private String summary;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class HealthCheckResponse {
        private Boolean healthy;
        private String service;
        private String message;
        private java.time.LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ConversationStartRequest {
        private Long userId;
    }


}
