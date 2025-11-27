package com.genixo.education.search.service.ai;

import com.genixo.education.search.dto.ai.ConversationDTO;
import com.genixo.education.search.dto.ai.ConversationMessageDTO;
import com.genixo.education.search.dto.ai.FormDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genixo.education.search.entity.ai.Conversation;
import com.genixo.education.search.entity.ai.ConversationMessage;
import com.genixo.education.search.repository.ai.ConversationMessageRepository;
import com.genixo.education.search.repository.ai.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;
    private final ObjectMapper objectMapper;

    /**
     * Yeni conversation başlat
     */
    @Transactional
    public Conversation createConversation(Long userId, Conversation.ConversationType type) {
        log.info("Creating new conversation for user: {}, type: {}", userId, type);

        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setConversationType(type);
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        conversation.setIsFormSubmitted(false);
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setCreatedBy(userId);

        return conversationRepository.save(conversation);
    }

    /**
     * Kullanıcının aktif conversation'ını getir veya yeni oluştur
     */
    @Transactional
    public Conversation getOrCreateActiveConversation(Long userId, Conversation.ConversationType type) {
        return conversationRepository
                .findByUserIdAndStatusAndIsActiveTrue(userId, Conversation.ConversationStatus.ACTIVE)
                .orElseGet(() -> createConversation(userId, type));
    }

    /**
     * Conversation'a mesaj ekle
     */
    @Transactional
    public ConversationMessage addMessage(
            Long conversationId,
            ConversationMessage.MessageRole role,
            String content,
            FormDataDTO extractedData,
            Long processingTimeMs
    ) {
        log.info("Adding message to conversation: {}, role: {}", conversationId, role);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        ConversationMessage message = new ConversationMessage();
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        message.setProcessingTimeMs(processingTimeMs);

        // Extracted data'yı JSON olarak sakla
        if (extractedData != null) {
            try {
                String extractedDataJson = objectMapper.writeValueAsString(extractedData);
                message.setExtractedData(extractedDataJson);
            } catch (JsonProcessingException e) {
                log.error("Error serializing extracted data", e);
            }
        }

        // Token count tahmin et
        message.setTokenCount(estimateTokenCount(content));

        ConversationMessage savedMessage = conversationMessageRepository.save(message);

        // Conversation'ın lastMessageAt'ini güncelle
        conversation.setLastMessageAt(LocalDateTime.now());

        // Eğer extracted data varsa, form data'yı güncelle
        if (extractedData != null) {
            updateConversationFormData(conversation, extractedData);
        }

        conversationRepository.save(conversation);

        return savedMessage;
    }

    /**
     * Conversation'ın form data'sını güncelle
     */
    @Transactional
    public void updateConversationFormData(Conversation conversation, FormDataDTO formData) {
        try {
            String formDataJson = objectMapper.writeValueAsString(formData);
            conversation.setFormData(formDataJson);
            conversationRepository.save(conversation);
            log.info("Updated form data for conversation: {}", conversation.getId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing form data", e);
        }
    }

    /**
     * Conversation'ın mevcut form data'sını getir
     */
    public FormDataDTO getConversationFormData(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        if (conversation.getFormData() == null || conversation.getFormData().isEmpty()) {
            return new FormDataDTO();
        }

        try {
            return objectMapper.readValue(conversation.getFormData(), FormDataDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing form data", e);
            return new FormDataDTO();
        }
    }

    /**
     * Conversation'ın tüm mesajlarını getir
     */
    public List<ConversationMessage> getConversationMessages(Long conversationId) {
        return conversationMessageRepository.findByConversationIdAndIsActiveTrueOrderByCreatedAtAsc(conversationId);
    }

    /**
     * Son N mesajı getir (context window için)
     */
    public List<ConversationMessage> getLastNMessages(Long conversationId, int limit) {
        List<ConversationMessage> messages = conversationMessageRepository
                .findLastNMessages(conversationId, limit);

        // Ters çevir (en eski mesaj başta olsun)
        return messages.stream()
                .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Conversation'ı tamamla
     */
    @Transactional
    public void completeConversation(Long conversationId) {
        log.info("Completing conversation: {}", conversationId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.setStatus(Conversation.ConversationStatus.COMPLETED);
        conversation.setCompletedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    /**
     * Conversation'ı terk edilmiş olarak işaretle
     */
    @Transactional
    public void markConversationAsAbandoned(Long conversationId) {
        log.info("Marking conversation as abandoned: {}", conversationId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.setStatus(Conversation.ConversationStatus.ABANDONED);
        conversationRepository.save(conversation);
    }

    /**
     * Form gönderildi olarak işaretle
     */
    @Transactional
    public void markFormAsSubmitted(Long conversationId) {
        log.info("Marking form as submitted for conversation: {}", conversationId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        conversation.setIsFormSubmitted(true);
        conversation.setStatus(Conversation.ConversationStatus.COMPLETED);
        conversation.setCompletedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    /**
     * Kullanıcının tüm conversation'larını getir
     */
    public List<ConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository
                .findByUserIdAndIsActiveTrueOrderByLastMessageAtDesc(userId);

        return conversations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Conversation'ı DTO'ya çevir
     */
    public ConversationDTO convertToDTO(Conversation conversation) {
        FormDataDTO formData = null;
        if (conversation.getFormData() != null) {
            try {
                formData = objectMapper.readValue(conversation.getFormData(), FormDataDTO.class);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing form data for conversation: {}", conversation.getId());
            }
        }

        Long messageCount = conversationMessageRepository.countByConversationIdAndIsActiveTrue(conversation.getId());

        // Son 5 mesajı getir
        List<ConversationMessage> recentMessages = conversationMessageRepository
                .findLastNMessages(conversation.getId(), 5);

        List<ConversationMessageDTO> recentMessageDTOs = recentMessages.stream()
                .map(this::convertMessageToDTO)
                .collect(Collectors.toList());

        return ConversationDTO.builder()
                .id(conversation.getId())
                .userId(conversation.getUserId())
                .conversationType(conversation.getConversationType().name())
                .status(conversation.getStatus().name())
                .currentFormData(formData)
                .isFormSubmitted(conversation.getIsFormSubmitted())
                .lastMessageAt(conversation.getLastMessageAt())
                .completedAt(conversation.getCompletedAt())
                .messageCount(messageCount.intValue())
                .recentMessages(recentMessageDTOs)
                .build();
    }

    /**
     * ConversationMessage'ı DTO'ya çevir
     */
    public ConversationMessageDTO convertMessageToDTO(ConversationMessage message) {
        FormDataDTO extractedData = null;
        if (message.getExtractedData() != null) {
            try {
                extractedData = objectMapper.readValue(message.getExtractedData(), FormDataDTO.class);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing extracted data for message: {}", message.getId());
            }
        }

        return ConversationMessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .role(message.getRole().name())
                .content(message.getContent())
                .extractedData(extractedData)
                .createdAt(message.getCreatedAt())
                .processingTimeMs(message.getProcessingTimeMs())
                .build();
    }

    /**
     * Terk edilmiş conversation'ları temizle (arka plan işi için)
     */
    @Transactional
    public int cleanupAbandonedConversations(int hoursThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hoursThreshold);
        List<Conversation> abandoned = conversationRepository.findAbandonedConversations(threshold);

        abandoned.forEach(conv -> {
            conv.setStatus(Conversation.ConversationStatus.ABANDONED);
            conversationRepository.save(conv);
        });

        log.info("Marked {} conversations as abandoned", abandoned.size());
        return abandoned.size();
    }

    /**
     * Conversation istatistikleri
     */
    public ConversationStats getConversationStats(Long conversationId) {
        Long totalTokens = conversationMessageRepository.getTotalTokenCount(conversationId);
        Double avgProcessingTime = conversationMessageRepository.getAverageProcessingTime(conversationId);
        Long messageCount = conversationMessageRepository.countByConversationIdAndIsActiveTrue(conversationId);

        return ConversationStats.builder()
                .conversationId(conversationId)
                .totalMessages(messageCount)
                .totalTokens(totalTokens)
                .averageProcessingTimeMs(avgProcessingTime != null ? avgProcessingTime : 0.0)
                .build();
    }

    /**
     * Token sayısını tahmin et
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Türkçe için ortalama: 1 token ≈ 3.5 karakter
        return (int) Math.ceil(text.length() / 3.5);
    }

    /**
     * Conversation Stats DTO
     */

}
