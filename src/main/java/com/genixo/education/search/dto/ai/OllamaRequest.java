package com.genixo.education.search.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaRequest {
    private String model;
    private List<OllamaMessage> messages;
    private Boolean stream;
    private OllamaOptions options;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaMessage {
        private String role; // system, user, assistant
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaOptions {
        private Double temperature;
        private Integer num_predict;
        private Integer top_k;
        private Double top_p;
    }
}