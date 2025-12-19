package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {

    private Long id;
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String thumbnailUrl;
    private Long fileSizeBytes;
    private String mimeType;
    private MediaType mediaType;
    private Integer width;
    private Integer height;
    private Integer durationSeconds;
    private String uploadId;
    private Boolean isProcessed;
    private String processingError;
}

