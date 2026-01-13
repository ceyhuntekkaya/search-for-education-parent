package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.content.FileUploadDto;
import com.genixo.education.search.entity.FileUpload;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.FileUploadService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;



import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Upload Management", description = "APIs for uploading data")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final JwtService jwtService;


    @Value("${storage.base:/app/uploads/}")
    private String storageBase;

    /**
     * Çoklu dosya yükleme endpoint'i
     *
     * @param files Yüklenecek dosyalar
     * @param request HTTP request (JWT token için)
     * @return Yüklenen dosyaların bilgileri
     */
    @PostMapping("/{schoolId}/{type}")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Document Type") @PathVariable String type,
            HttpServletRequest request) {

        try {
            // Kullanıcıyı JWT'den al


            // Dosya kontrolü
            if (files == null || files.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No files provided");
            }

            List<FileUploadDto> uploadedFiles = new ArrayList<>();

            // Her dosyayı yükle
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    log.warn("Empty file skipped: {}", file.getOriginalFilename());
                    continue;
                }

                try {
                    // Dosyayı yükle ve kaydet
                    FileUpload savedFile = fileUploadService.uploadFile(file, 1L, schoolId, type);

                    // DTO'ya dönüştür
                    FileUploadDto dto = new FileUploadDto();
                    dto.setFileName(savedFile.getFileName());
                    dto.setFileUrl(savedFile.getFileUrl());
                    dto.setOriginalFileName(savedFile.getOriginalFileName());
                    dto.setThumbnailUrl(savedFile.getThumbnailUrl());
                    dto.setFileSizeBytes(savedFile.getFileSizeBytes());
                    dto.setMimeType(savedFile.getMimeType());
                    dto.setMediaType(savedFile.getMediaType());
                    dto.setWidth(savedFile.getWidth());
                    dto.setHeight(savedFile.getHeight());
                    dto.setDurationSeconds(savedFile.getDurationSeconds());
                    dto.setUploadId(savedFile.getUploadId());
                    dto.setIsProcessed(savedFile.getIsProcessed());
                    dto.setProcessingError(savedFile.getProcessingError());
                    dto.setId(savedFile.getId());
                    uploadedFiles.add(dto);

                } catch (Exception e) {
                    log.error("Error uploading file: {}", file.getOriginalFilename(), e);
                    // Hata durumunda diğer dosyaları yüklemeye devam et
                }
            }

            // Hiç dosya yüklenememişse hata dön
            if (uploadedFiles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to upload any files");
            }

            return ResponseEntity.ok(uploadedFiles);

        } catch (Exception e) {
            log.error("Error in file upload endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Kullanıcının dosyalarını listele
     */
    @GetMapping("/files")
    public ResponseEntity<?> getUserFiles(HttpServletRequest request) {
        try {
            User user = jwtService.getUser(request);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized: User not found");
            }

            List<FileUpload> files = fileUploadService.getFilesByUserId(user.getId());

            List<FileUploadDto> dtos = new ArrayList<>();
            for (FileUpload file : files) {
                FileUploadDto dto = new FileUploadDto();
                dto.setFileName(file.getFileName());
                dto.setFileUrl(file.getFileUrl());
                dtos.add(dto);
            }





            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            log.error("Error fetching user files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch files");
        }
    }

    /**
     * Dosya detaylarını getir
     */
    @GetMapping("/files/{id}")
    public ResponseEntity<?> getFileById(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            User user = jwtService.getUser(request);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized: User not found");
            }

            FileUpload file = fileUploadService.getFileById(id);

            // Dosya kullanıcıya ait mi kontrol et
            if (!file.getCreatedBy().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied");
            }

            FileUploadDto dto = new FileUploadDto();
            dto.setFileName(file.getFileName());
            dto.setFileUrl(file.getFileUrl());
            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        } catch (Exception e) {
            log.error("Error fetching file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch file");
        }
    }

    @GetMapping("/serve/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        try {
            // Path'i al
            String requestPath = extractPath(request);

            // Güvenlik kontrolü - path traversal saldırısını önle
            if (requestPath.contains("..")) {
                log.warn("Path traversal attempt: {}", requestPath);
                return ResponseEntity.badRequest().build();
            }

            // Başındaki "/" varsa kaldır
            if (requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }

            // Dosya yolu oluştur
            Path basePath = Paths.get(storageBase);
            Path filePath = basePath.resolve(requestPath).normalize();

            // Güvenlik: dosya yolu base path içinde mi kontrol et
            if (!filePath.startsWith(basePath)) {
                log.warn("Attempt to access file outside base path: {}", requestPath);
                return ResponseEntity.badRequest().build();
            }


            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not found or not readable: {}", filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // MIME type'ı belirle
            String contentType = determineContentType(filePath.toString());


            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error serving file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String extractPath(HttpServletRequest request) {
        String requestUri =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest().getRequestURI();
;//api/v1/storage/serve/Project/banner/0558c0a5-cad9-4e8a-ac24-7dcc7c842c59_images.jpeg
        // DÜZELTME: /api/upload/serve/ olmalı
        return requestUri.substring("/api/upload/serve/".length());
    }


    private String determineContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        } else if (filename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (filename.endsWith(".mp4")) {
            return "video/mp4";
        }
        return "application/octet-stream";
    }
}
