package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.content.PostDto;
import com.genixo.education.search.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Content Management", description = "APIs for managing posts, galleries, messages and other content")

public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send")
    @Operation(summary = "Get post by ID", description = "Get post details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Email not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<String>> getPostById(
            HttpServletRequest request) throws Exception {

        emailService.sendMail();

        ApiResponse<String> response = ApiResponse.success("ok.", "Email retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
