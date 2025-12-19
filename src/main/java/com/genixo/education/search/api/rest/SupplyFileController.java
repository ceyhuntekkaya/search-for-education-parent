package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.FileDto;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.supply.SupplyFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/supply/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload Management", description = "APIs for uploading, downloading and managing files in the supply system")
public class SupplyFileController {

    private final SupplyFileService fileService;
    private final JwtService jwtService;

    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Upload a file (image, document, etc.) for supply system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "File uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or file is empty"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "File upload failed")
    })
    public ResponseEntity<ApiResponse<FileDto>> uploadFile(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        try {
            User user = jwtService.getUser(request);
            FileDto fileDto = fileService.uploadFile(file, user.getId());

            ApiResponse<FileDto> response = ApiResponse.success(fileDto, "File uploaded successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Error uploading file", e);
            ApiResponse<FileDto> response = ApiResponse.error("File upload failed: " + e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Download file", description = "Download a file by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "File ID") @PathVariable Long fileId,
            HttpServletRequest request) {

        try {
            Resource resource = fileService.downloadFile(fileId);
            FileDto fileDto = fileService.getFileById(fileId);

            // Determine content type
            String contentType = fileDto.getMimeType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getOriginalFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("Error downloading file", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{fileId}/info")
    @Operation(summary = "Get file information", description = "Get file information by ID without downloading")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File information retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<ApiResponse<FileDto>> getFileInfo(
            @Parameter(description = "File ID") @PathVariable Long fileId,
            HttpServletRequest request) {

        FileDto fileDto = fileService.getFileById(fileId);

        ApiResponse<FileDto> response = ApiResponse.success(fileDto, "File information retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Delete a file (only owner can delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @Parameter(description = "File ID") @PathVariable Long fileId,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        fileService.deleteFile(fileId, user.getId());

        ApiResponse<Void> response = ApiResponse.success(null, "File deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

