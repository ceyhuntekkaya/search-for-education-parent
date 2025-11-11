package com.genixo.education.search.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private Boolean success;
    private String message;
    private T data;
    private String timestamp;
    private Integer status;
}
