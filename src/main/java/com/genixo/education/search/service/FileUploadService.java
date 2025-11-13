package com.genixo.education.search.service;


import com.genixo.education.search.entity.FileUpload;
import com.genixo.education.search.enumaration.MediaType;
import com.genixo.education.search.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    @Value("${storage.base:/app/uploads/}")
    private String basePath;

    private final FileUploadRepository fileUploadRepository;

    /**
     * Dosyayı yükler ve veritabanına kaydeder
     */
    public FileUpload uploadFile(MultipartFile file, Long userId, Long schoolId, String type) throws IOException {
        // Dosya adını temizle
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = sanitizeFileName(originalFileName);





        String relativePath = basePath + "/" + schoolId+ "/" + type;
        Path uploadPath = Paths.get(relativePath);

        // Klasör yoksa oluştur
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Benzersiz dosya adı oluştur
        String fileExtension = getFileExtension(sanitizedFileName);
        String uniqueFileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;
        Path filePath = uploadPath.resolve(uniqueFileName);

        // Dosyayı kaydet
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // MIME type'ı belirle
        String mimeType = file.getContentType();
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = Files.probeContentType(filePath);
        }

        // MediaType'ı belirle
        MediaType mediaType = determineMediaType(mimeType);

        // Entity oluştur
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(uniqueFileName);
        fileUpload.setOriginalFileName(originalFileName);
        fileUpload.setFileUrl( schoolId+ "/" + type + "/" + uniqueFileName);
        fileUpload.setThumbnailUrl(null); // Şimdilik null
        fileUpload.setFileSizeBytes(file.getSize());
        fileUpload.setMimeType(mimeType);
        fileUpload.setMediaType(mediaType);
        fileUpload.setWidth(null); // Şimdilik null
        fileUpload.setHeight(null); // Şimdilik null
        fileUpload.setDurationSeconds(null); // Şimdilik null
        fileUpload.setUploadId(UUID.randomUUID().toString());
        fileUpload.setIsProcessed(true); // Processing yok, direkt true
        fileUpload.setProcessingError(null);
        fileUpload.setCreatedBy(userId);
        fileUpload.setUpdatedBy(userId);


        // Veritabanına kaydet
        FileUpload savedFile = fileUploadRepository.save(fileUpload);


        return savedFile;
    }


    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "file";
        }

        // Dosya uzantısını ayır
        String nameWithoutExtension;
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            nameWithoutExtension = fileName.substring(0, lastDotIndex);
            extension = fileName.substring(lastDotIndex);
        } else {
            nameWithoutExtension = fileName;
        }

        // Türkçe karakterleri normalize et
        String normalized = Normalizer.normalize(nameWithoutExtension, Normalizer.Form.NFD);

        // Türkçe karakterleri manuel olarak değiştir
        normalized = normalized
                .replace('ı', 'i')
                .replace('İ', 'I')
                .replace('ğ', 'g')
                .replace('Ğ', 'G')
                .replace('ü', 'u')
                .replace('Ü', 'U')
                .replace('ş', 's')
                .replace('Ş', 'S')
                .replace('ö', 'o')
                .replace('Ö', 'O')
                .replace('ç', 'c')
                .replace('Ç', 'C');

        // Özel karakterleri kaldır, sadece alfanumerik ve tire/alt çizgi bırak
        normalized = normalized.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Birden fazla alt çizgiyi tek alt çizgiye indir
        normalized = normalized.replaceAll("_{2,}", "_");

        // Baş ve sondaki alt çizgileri kaldır
        normalized = normalized.replaceAll("^_+|_+$", "");

        // Boşsa default isim ver
        if (normalized.isEmpty()) {
            normalized = "file";
        }

        return normalized + extension;
    }

    /**
     * Dosya uzantısını al
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * MIME type'a göre MediaType belirle
     */
    private MediaType determineMediaType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            return MediaType.OTHER;
        }

        String type = mimeType.toLowerCase();

        if (type.startsWith("image/")) {
            return MediaType.IMAGE;
        } else if (type.startsWith("video/")) {
            return MediaType.VIDEO;
        } else if (type.startsWith("audio/")) {
            return MediaType.AUDIO;
        } else if (type.equals("application/pdf") ||
                type.contains("document") ||
                type.contains("text") ||
                type.contains("word") ||
                type.contains("excel") ||
                type.contains("powerpoint") ||
                type.contains("msword") ||
                type.contains("ms-excel") ||
                type.contains("ms-powerpoint") ||
                type.contains("openxmlformats")) {
            return MediaType.DOCUMENT;
        } else if (type.contains("zip") ||
                type.contains("rar") ||
                type.contains("tar") ||
                type.contains("7z") ||
                type.contains("compress")) {
            return MediaType.ARCHIVE;
        }

        return MediaType.OTHER;
    }

    /**
     * ID'ye göre dosya getir
     */
    public FileUpload getFileById(Long id) {
        return fileUploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }

    /**
     * Kullanıcıya ait dosyaları getir
     */
    public java.util.List<FileUpload> getFilesByUserId(Long userId) {
        return fileUploadRepository.findByCreatedBy(userId);
    }
}