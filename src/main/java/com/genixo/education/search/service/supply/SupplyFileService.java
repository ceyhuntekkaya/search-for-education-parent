package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.FileDto;
import com.genixo.education.search.entity.FileUpload;
import com.genixo.education.search.repository.FileUploadRepository;
import com.genixo.education.search.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplyFileService {

    private final FileUploadService fileUploadService;
    private final FileUploadRepository fileUploadRepository;

    @Value("${storage.base:/app/uploads/}")
    private String basePath;

    // ================================ FILE UPLOAD ================================

    @Transactional
    public FileDto uploadFile(MultipartFile file, Long userId) throws IOException {
        log.info("Uploading file for user ID: {}", userId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        // Supply klasörüne yükle
        FileUpload uploadedFile = fileUploadService.uploadFile(file, userId, null, "supply");

        log.info("File uploaded successfully with ID: {}", uploadedFile.getId());
        return mapToDto(uploadedFile);
    }

    public FileDto getFileById(Long fileId) {
        log.info("Fetching file with ID: {}", fileId);

        FileUpload file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", fileId));

        return mapToDto(file);
    }

    public Resource downloadFile(Long fileId) throws IOException {
        log.info("Downloading file with ID: {}", fileId);

        FileUpload file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", fileId));

        // Dosya yolunu oluştur
        Path filePath = Paths.get(basePath).resolve(file.getFileUrl()).normalize();
        
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File", "File not found on disk");
        }

        Resource resource = new UrlResource(filePath.toUri());
        
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("File", "File is not readable");
        }

        return resource;
    }

    @Transactional
    public void deleteFile(Long fileId, Long userId) {
        log.info("Deleting file ID: {} by user ID: {}", fileId, userId);

        FileUpload file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", fileId));

        // Verify ownership (optional - can be removed if files are shared)
        if (file.getCreatedBy() != null && !file.getCreatedBy().equals(userId)) {
            throw new ResourceNotFoundException("File", "You don't have permission to delete this file");
        }

        // Delete physical file
        try {
            Path filePath = Paths.get(basePath).resolve(file.getFileUrl()).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            log.error("Error deleting physical file: {}", file.getFileUrl(), e);
            // Continue with database deletion even if physical file deletion fails
        }

        // Delete from database
        fileUploadRepository.delete(file);
        log.info("File deleted successfully with ID: {}", fileId);
    }

    // ================================ HELPER METHODS ================================

    private FileDto mapToDto(FileUpload file) {
        return FileDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .originalFileName(file.getOriginalFileName())
                .fileUrl(file.getFileUrl())
                .thumbnailUrl(file.getThumbnailUrl())
                .fileSizeBytes(file.getFileSizeBytes())
                .mimeType(file.getMimeType())
                .mediaType(file.getMediaType())
                .width(file.getWidth())
                .height(file.getHeight())
                .durationSeconds(file.getDurationSeconds())
                .uploadId(file.getUploadId())
                .isProcessed(file.getIsProcessed())
                .processingError(file.getProcessingError())
                .build();
    }
}

