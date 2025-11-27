package com.genixo.education.search.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResponse {
    private String model;

    @JsonProperty("created_at")
    private String createdAt;

    private OllamaMessage message;

    @JsonProperty("done_reason")
    private String doneReason;

    private Boolean done;

    @JsonProperty("total_duration")
    private Long totalDuration;

    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;

    @JsonProperty("eval_count")
    private Integer evalCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OllamaMessage {
        private String role;
        private String content;
    }
}
