package com.genixo.education.search.service.ai;


import com.genixo.education.search.dto.ai.OllamaRequest;
import com.genixo.education.search.dto.ai.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OllamaClientService {

    private final RestTemplate restTemplate;
    private final String ollamaApiUrl;
    private final String ollamaModel;

    public OllamaClientService(
            RestTemplate restTemplate,
            @Value("${ollama.api.url:https://chat.studyscore.ai/ollama/api/chat}") String ollamaApiUrl,
            @Value("${ollama.model:qwen2.5:7b}") String ollamaModel
    ) {
        this.restTemplate = restTemplate;
        this.ollamaApiUrl = ollamaApiUrl;
        this.ollamaModel = ollamaModel;
    }

    /**
     * Ollama'ya mesaj gönder ve yanıt al
     */
    public OllamaResponse sendMessage(String systemPrompt, String userMessage, List<OllamaRequest.OllamaMessage> conversationHistory) {
        try {
            long startTime = System.currentTimeMillis();

            // Mesaj listesini oluştur
            List<OllamaRequest.OllamaMessage> messages = new ArrayList<>();

            // System prompt ekle
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(OllamaRequest.OllamaMessage.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build());
            }

            // Conversation history ekle (varsa)
            if (conversationHistory != null && !conversationHistory.isEmpty()) {
                messages.addAll(conversationHistory);
            }

            // Kullanıcı mesajını ekle
            messages.add(OllamaRequest.OllamaMessage.builder()
                    .role("user")
                    .content(userMessage)
                    .build());

            // Request oluştur
            OllamaRequest request = OllamaRequest.builder()
                    .model(ollamaModel)
                    .messages(messages)
                    .stream(false)
                    .options(OllamaRequest.OllamaOptions.builder()
                            .temperature(0.7)
                            .num_predict(2000)
                            .top_k(40)
                            .top_p(0.9)
                            .build())
                    .build();

            // HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

            // API çağrısı
            log.info("Sending request to Ollama API: {}", ollamaApiUrl);
            ResponseEntity<OllamaResponse> response = restTemplate.exchange(
                    ollamaApiUrl,
                    HttpMethod.POST,
                    entity,
                    OllamaResponse.class
            );

            long endTime = System.currentTimeMillis();
            log.info("Ollama response received in {} ms", (endTime - startTime));

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Ollama API returned non-OK status: {}", response.getStatusCode());
                throw new RuntimeException("Ollama API error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error calling Ollama API", e);
            throw new RuntimeException("Failed to communicate with Ollama: " + e.getMessage(), e);
        }
    }

    /**
     * Sadece tek mesaj gönder (conversation history olmadan)
     */
    public OllamaResponse sendSimpleMessage(String systemPrompt, String userMessage) {
        return sendMessage(systemPrompt, userMessage, null);
    }

    /**
     * Streaming desteği için (gelecekte kullanılabilir)
     */
    public OllamaResponse sendStreamingMessage(String systemPrompt, String userMessage, List<OllamaRequest.OllamaMessage> conversationHistory) {
        // TODO: WebSocket veya SSE ile streaming implementasyonu
        throw new UnsupportedOperationException("Streaming not yet implemented");
    }

    /**
     * Model sağlık kontrolü
     */
    public boolean isOllamaHealthy() {
        try {
            // Basit bir test mesajı gönder
            OllamaResponse response = sendSimpleMessage(
                    "You are a helpful assistant.",
                    "Hello"
            );
            return response != null && response.getMessage() != null;
        } catch (Exception e) {
            log.error("Ollama health check failed", e);
            return false;
        }
    }

    /**
     * Conversation history'yi OllamaMessage formatına çevir
     */
    public List<OllamaRequest.OllamaMessage> buildConversationHistory(List<String> userMessages, List<String> assistantMessages) {
        List<OllamaRequest.OllamaMessage> history = new ArrayList<>();

        int minSize = Math.min(userMessages.size(), assistantMessages.size());

        for (int i = 0; i < minSize; i++) {
            history.add(OllamaRequest.OllamaMessage.builder()
                    .role("user")
                    .content(userMessages.get(i))
                    .build());

            history.add(OllamaRequest.OllamaMessage.builder()
                    .role("assistant")
                    .content(assistantMessages.get(i))
                    .build());
        }

        return history;
    }

    /**
     * Token sayısını tahmin et (yaklaşık)
     */
    public int estimateTokenCount(String text) {
        // Basit tahmin: ortalama 1 token = 4 karakter
        return text.length() / 4;
    }

    /**
     * Context window'u kontrol et (qwen2.5:7b için ~32k token)
     */
    public boolean isWithinContextLimit(List<OllamaRequest.OllamaMessage> messages, int maxTokens) {
        int totalTokens = messages.stream()
                .mapToInt(msg -> estimateTokenCount(msg.getContent()))
                .sum();

        return totalTokens <= maxTokens;
    }

    /**
     * Eski mesajları temizle (context window aşıldığında)
     */
    public List<OllamaRequest.OllamaMessage> trimConversationHistory(
            List<OllamaRequest.OllamaMessage> messages,
            int maxTokens
    ) {
        List<OllamaRequest.OllamaMessage> trimmed = new ArrayList<>();
        int currentTokens = 0;

        // Sondan başa doğru git (en yeni mesajlar önemli)
        for (int i = messages.size() - 1; i >= 0; i--) {
            OllamaRequest.OllamaMessage msg = messages.get(i);
            int msgTokens = estimateTokenCount(msg.getContent());

            if (currentTokens + msgTokens > maxTokens) {
                break;
            }

            trimmed.add(0, msg); // Başa ekle
            currentTokens += msgTokens;
        }

        log.info("Trimmed conversation history from {} to {} messages", messages.size(), trimmed.size());
        return trimmed;
    }
}