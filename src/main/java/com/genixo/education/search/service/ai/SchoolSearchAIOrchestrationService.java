package com.genixo.education.search.service.ai;

import com.genixo.education.search.dto.ai.*;
import com.genixo.education.search.entity.ai.Conversation;
import com.genixo.education.search.entity.ai.ConversationMessage;
import com.genixo.education.search.repository.ai.ConversationMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolSearchAIOrchestrationService {

    private final OllamaClientService ollamaClientService;
    private final ConversationService conversationService;
    private final RAGContextService ragContextService;
    private final FormParserService formParserService;
    private final ConversationMessageRepository conversationMessageRepository;

    private static final int MAX_CONTEXT_MESSAGES = 10; // Son 10 mesajı context'e al

    /**
     * Kullanıcı mesajını işle ve AI yanıtı döndür
     */












    /**
     * Kullanıcı mesajını işle ve AI yanıtı döndür
     */
    @Transactional
    public ChatMessageResponse processUserMessage(ChatMessageRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Processing user message for user: {}", request.getUserId());

        try {
            // 1. Conversation'ı al veya oluştur
            Conversation conversation = getOrCreateConversation(request);
            log.debug("Using conversation: {}", conversation.getId());

            // 2. Kullanıcı mesajını kaydet
            ConversationMessage userMessage = conversationService.addMessage(
                    conversation.getId(),
                    ConversationMessage.MessageRole.USER,
                    request.getMessage(),
                    null,
                    null
            );

            // 3. ✅ İlk mesaj mı kontrol et (form data üzerinden)
            FormDataDTO currentFormData = conversationService.getConversationFormData(conversation.getId());
            boolean isFirstMessage = (currentFormData == null ||
                    currentFormData.getNextStep() == null ||
                    currentFormData.getNextStep().isEmpty());

            log.info("Current form data nextStep: {}, isFirstMessage: {}",
                    currentFormData != null ? currentFormData.getNextStep() : "null",
                    isFirstMessage);

            // 4. Eğer ilk mesajsa, direkt hoş geldin mesajı gönder (AI'ya sorma)
            if (isFirstMessage) {
                log.info("✅ First message detected, sending welcome message");
                return handleFirstMessage(conversation, startTime);
            }

            log.info("Proceeding with AI processing...");

            // 5. RAG Context oluştur
            String ragContext = buildRAGContext(currentFormData);

            // 7. System prompt oluştur
            String systemPrompt = buildSystemPrompt(ragContext, currentFormData);

            // 8. Conversation history'yi hazırla
            List<OllamaRequest.OllamaMessage> conversationHistory = buildConversationHistory(conversation.getId());

            // 9. Ollama'ya istek gönder
            OllamaResponse ollamaResponse = ollamaClientService.sendMessage(
                    systemPrompt,
                    request.getMessage(),
                    conversationHistory
            );

            long aiProcessingTime = System.currentTimeMillis() - startTime;

            // 10. AI yanıtını parse et
            String aiContent = ollamaResponse.getMessage().getContent();
            FormDataDTO extractedFormData = formParserService.parseAIResponse(aiContent);

            // 11. Mevcut form data ile merge et
            FormDataDTO mergedFormData = formParserService.mergeFormData(currentFormData, extractedFormData);

            // 12. Form data'yı validate et
            FormValidationDTO validation = formParserService.validateFormData(mergedFormData);
            if (!validation.getIsValid()) {
                log.warn("Form validation failed: {} errors", validation.getErrors().size());
                // Validation hatalarını AI'a bildir ve düzeltme iste
                mergedFormData = handleValidationErrors(validation, mergedFormData);
            }

            // 13. AI yanıtını kaydet
            ConversationMessage assistantMessage = conversationService.addMessage(
                    conversation.getId(),
                    ConversationMessage.MessageRole.ASSISTANT,
                    aiContent,
                    mergedFormData,
                    aiProcessingTime
            );

            // 14. Conversation'ın form data'sını güncelle
            conversationService.updateConversationFormData(conversation, mergedFormData);

            // 15. Eğer form tamamsa, conversation'ı complete et
            if ("complete".equalsIgnoreCase(mergedFormData.getNextStep()) &&
                    Boolean.TRUE.equals(mergedFormData.getMeetsMinimumRequirements())) {
                log.info("Form completed for conversation: {}", conversation.getId());
                conversationService.completeConversation(conversation.getId());
            }

            // 16. Response oluştur
            return ChatMessageResponse.builder()
                    .conversationId(conversation.getId())
                    .messageId(assistantMessage.getId())
                    .role(assistantMessage.getRole().name())
                    .content(mergedFormData.getUserMessage() != null ? mergedFormData.getUserMessage() : aiContent)
                    .extractedFormData(mergedFormData)
                    .timestamp(assistantMessage.getCreatedAt())
                    .processingTimeMs(aiProcessingTime)
                    .isFormComplete(Boolean.TRUE.equals(mergedFormData.getMeetsMinimumRequirements()))
                    .build();

        } catch (Exception e) {
            log.error("Error processing user message", e);

            // Hata durumunda kullanıcıya bilgi ver
            return ChatMessageResponse.builder()
                    .conversationId(request.getConversationId())
                    .role("ASSISTANT")
                    .content("Üzgünüm, bir hata oluştu. Lütfen tekrar deneyin veya farklı bir şekilde ifade edin.")
                    .timestamp(java.time.LocalDateTime.now())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .isFormComplete(false)
                    .build();
        }
    }

    /**
     * İlk mesaj için özel handler (hoş geldin mesajı)
     */
    private ChatMessageResponse handleFirstMessage(Conversation conversation, long startTime) {
        log.info("Handling first message for conversation: {}", conversation.getId());

        String welcomeMessage = """
            Merhaba! Size okul aramada yardımcı olacağım. 🎓
            
            Sizin için en uygun okulu bulmak adına birkaç soru soracağım. Hazırsanız başlayalım!
            
            İlk olarak: Hangi şehirde okul arıyorsunuz?
            """;

        // Boş form data oluştur
        FormDataDTO initialFormData = FormDataDTO.builder()
                .nextStep("city")
                .userMessage(welcomeMessage)
                .missingFields(List.of("city", "institutionTypeGroup", "institutionType"))
                .completionPercentage(0)
                .meetsMinimumRequirements(false)
                .cityFilled(false)
                .districtFilled(false)
                .institutionTypeGroupFilled(false)
                .institutionTypeFilled(false)
                .propertyGroupFilled(false)
                .propertiesFilled(false)
                .priceFilled(false)
                .build();

        // Welcome mesajını kaydet
        ConversationMessage welcomeMsg = conversationService.addMessage(
                conversation.getId(),
                ConversationMessage.MessageRole.ASSISTANT,
                welcomeMessage,
                initialFormData,
                System.currentTimeMillis() - startTime
        );

        // Form data'yı kaydet
        conversationService.updateConversationFormData(conversation, initialFormData);

        return ChatMessageResponse.builder()
                .conversationId(conversation.getId())
                .messageId(welcomeMsg.getId())
                .role("ASSISTANT")
                .content(welcomeMessage)
                .extractedFormData(initialFormData)
                .timestamp(welcomeMsg.getCreatedAt())
                .processingTimeMs(System.currentTimeMillis() - startTime)
                .isFormComplete(false)
                .build();
    }
















    /**
     * Conversation al veya oluştur
     */
    private Conversation getOrCreateConversation(ChatMessageRequest request) {
        if (request.getConversationId() != null) {
            // Mevcut conversation'ı kullan
            return conversationService.getConversationMessages(request.getConversationId())
                    .stream()
                    .findFirst()
                    .map(ConversationMessage::getConversation)
                    .orElseGet(() -> conversationService.createConversation(
                            request.getUserId(),
                            Conversation.ConversationType.OKUL_FORMU
                    ));
        } else {
            // Aktif conversation'ı bul veya yeni oluştur
            return conversationService.getOrCreateActiveConversation(
                    request.getUserId(),
                    Conversation.ConversationType.OKUL_FORMU
            );
        }
    }

    /**
     * RAG Context oluştur
     */
    private String buildRAGContext(FormDataDTO currentFormData) {
        StringBuilder context = new StringBuilder();

        // Temel bilgiler
        context.append(ragContextService.buildInitialSystemPrompt()).append("\n\n");

        // Mevcut seçimlere göre dinamik context
        if (currentFormData != null) {
            if (currentFormData.getCity() != null) {
                context.append("KULLANICININ SEÇİMLERİ:\n");
                context.append("- Şehir: ").append(currentFormData.getCity()).append("\n");

                // Şehir seçildiyse, ilçeleri göster
                List<String> districts = ragContextService.getDistrictsByCity(currentFormData.getCity());
                if (!districts.isEmpty()) {
                    context.append("- Bu şehirdeki ilçeler: ").append(String.join(", ", districts)).append("\n");
                }
            }

            if (currentFormData.getInstitutionTypeGroup() != null) {
                context.append("- Okul Türü Grubu: ").append(currentFormData.getInstitutionTypeGroup()).append("\n");

                // Grup seçildiyse, o gruptaki türleri göster
                List<String> types = ragContextService.getInstitutionTypes(currentFormData.getInstitutionTypeGroup());
                if (!types.isEmpty()) {
                    context.append("- Bu gruptaki okul türleri: ").append(String.join(", ", types)).append("\n");
                }
            }

            if (currentFormData.getInstitutionType() != null) {
                context.append("- Okul Türü: ").append(currentFormData.getInstitutionType()).append("\n");

                // Tür seçildiyse, özellikleri göster
                var propertyGroups = ragContextService.getSchoolPropertyGroups(currentFormData.getInstitutionType());
                if (!propertyGroups.isEmpty()) {
                    context.append("- Mevcut özellik kategorileri: ").append(String.join(", ", propertyGroups.values())).append("\n");
                }
            }

            context.append("\n");
        }

        // Tüm mevcut seçenekleri listele
        context.append("TÜM MEVCUT SEÇENEKLER:\n");
        context.append("Şehirler: ").append(String.join(", ", ragContextService.getAvailableCities())).append("\n");
        context.append("Okul Türü Grupları: ").append(String.join(", ", ragContextService.getInstitutionTypeGroups())).append("\n");

        return context.toString();
    }

    /**
     * System prompt oluştur
     */
    private String buildSystemPrompt(String ragContext, FormDataDTO currentFormData) {
        StringBuilder prompt = new StringBuilder();

        prompt.append(ragContext).append("\n\n");

        // Mevcut form durumunu ekle
        if (currentFormData != null && currentFormData.getCompletionPercentage() != null) {
            prompt.append("MEVCUT FORM DURUMU:\n");
            prompt.append("Tamamlanma: %").append(currentFormData.getCompletionPercentage()).append("\n");

            if (currentFormData.getMissingFields() != null && !currentFormData.getMissingFields().isEmpty()) {
                prompt.append("Eksik alanlar: ").append(String.join(", ", currentFormData.getMissingFields())).append("\n");
            }

            prompt.append("\n");
        }

        prompt.append("ŞİMDİ NE YAPMAN GEREKİYOR:\n");
        prompt.append("1. Kullanıcının mesajını anla\n");
        prompt.append("2. Hangi form alanını doldurduğunu tespit et\n");
        prompt.append("3. Verilen bilgiyi doğrula (mevcut seçeneklerle karşılaştır)\n");
        prompt.append("4. Sıradaki adımı belirle\n");
        prompt.append("5. Kullanıcıya dostça ve yönlendirici bir mesaj yaz\n");
        prompt.append("6. Tüm bilgileri JSON formatında döndür\n\n");

        prompt.append("UNUTMA: Yanıtın SADECE JSON olmalı, başka açıklama ekleme!\n");

        return prompt.toString();
    }

    /**
     * Conversation history oluştur (Ollama için)
     */
    private List<OllamaRequest.OllamaMessage> buildConversationHistory(Long conversationId) {
        List<ConversationMessage> messages = conversationService.getLastNMessages(
                conversationId,
                MAX_CONTEXT_MESSAGES
        );

        return messages.stream()
                .map(msg -> OllamaRequest.OllamaMessage.builder()
                        .role(msg.getRole() == ConversationMessage.MessageRole.USER ? "user" : "assistant")
                        .content(msg.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Validation hatalarını handle et
     */
    private FormDataDTO handleValidationErrors(FormValidationDTO validation, FormDataDTO formData) {
        log.debug("Handling validation errors");

        // Hataları kullanıcı mesajına ekle
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Bir sorun var gibi görünüyor:\n");

        for (FormValidationDTO.ValidationError error : validation.getErrors()) {
            errorMessage.append("- ").append(error.getMessage());
            if (error.getSuggestedValue() != null) {
                errorMessage.append(" (Belki şunu mu demek istediniz: ").append(error.getSuggestedValue()).append(")");
            }
            errorMessage.append("\n");
        }

        errorMessage.append("\nLütfen tekrar deneyin.");

        formData.setUserMessage(errorMessage.toString());
        return formData;
    }

    /**
     * Conversation'ı sıfırla (yeni arama başlat)
     */
    @Transactional
    public void resetConversation(Long conversationId) {
        log.info("Resetting conversation: {}", conversationId);

        // Mevcut conversation'ı complete et
        conversationService.completeConversation(conversationId);

        // Not: Yeni conversation kullanıcı yeni mesaj gönderdiğinde otomatik oluşacak
    }

    /**
     * Conversation geçmişini getir
     */
    public ConversationDTO getConversationHistory(Long conversationId) {
        Conversation conversation = conversationService.getConversationMessages(conversationId)
                .stream()
                .findFirst()
                .map(ConversationMessage::getConversation)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        return conversationService.convertToDTO(conversation);
    }

    /**
     * Kullanıcının tüm conversation'larını getir
     */
    public List<ConversationDTO> getUserConversations(Long userId) {
        return conversationService.getUserConversations(userId);
    }

    /**
     * Form özeti oluştur
     */
    public String getFormSummary(Long conversationId) {
        FormDataDTO formData = conversationService.getConversationFormData(conversationId);
        return formParserService.generateFormSummary(formData);
    }

    /**
     * Conversation istatistikleri
     */
    public ConversationStats getConversationStats(Long conversationId) {
        return conversationService.getConversationStats(conversationId);
    }

    /**
     * AI sağlık kontrolü
     */
    public boolean checkAIHealth() {
        try {
            return ollamaClientService.isOllamaHealthy();
        } catch (Exception e) {
            log.error("AI health check failed", e);
            return false;
        }
    }

    /**
     * Conversation'ı export et (PDF, JSON, vb. için)
     */
    public ConversationExportDTO exportConversation(Long conversationId) {
        ConversationDTO conversation = getConversationHistory(conversationId);
        String formSummary = getFormSummary(conversationId);
        ConversationStats stats = getConversationStats(conversationId);

        return ConversationExportDTO.builder()
                .conversation(conversation)
                .formSummary(formSummary)
                .stats(stats)
                .exportedAt(java.time.LocalDateTime.now())
                .build();
    }

    /**
     * Conversation Export DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class ConversationExportDTO {
        private ConversationDTO conversation;
        private String formSummary;
        private ConversationStats stats;
        private java.time.LocalDateTime exportedAt;
    }
}
