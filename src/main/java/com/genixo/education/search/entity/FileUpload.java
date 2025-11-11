package com.genixo.education.search.entity;


import com.genixo.education.search.enumaration.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_uploads")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload extends BaseEntity {

        @Column(nullable = false)
        private String fileName;

        @Column(nullable = false)
        private String originalFileName;

        @Column(nullable = false)
        private String fileUrl;

        @Column
        private String thumbnailUrl;

        @Column(nullable = false)
        private Long fileSizeBytes;

        @Column(nullable = false)
        private String mimeType;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private MediaType mediaType;

        @Column
        private Integer width;

        @Column
        private Integer height;

        @Column
        private Integer durationSeconds;

        @Column
        private String uploadId;

        @Column(nullable = false)
        private Boolean isProcessed = false;

        @Column
        private String processingError;



}
