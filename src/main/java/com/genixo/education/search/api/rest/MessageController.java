package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.MessageCreateDto;
import com.genixo.education.search.dto.supply.MessageDto;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.supply.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/supply")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message Management", description = "APIs for managing messages in conversations")
public class MessageController {

    private final MessageService messageService;
    private final JwtService jwtService;

    @PostMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Send message", description = "Send a message to a conversation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "You don't have permission to send messages to this conversation"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversation or user not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(
            @Parameter(description = "Conversation ID") @PathVariable Long conversationId,
            @Valid @RequestBody MessageCreateDto createDto,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        MessageDto messageDto = messageService.sendMessage(conversationId, user.getId(), createDto);

        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message sent successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get conversation messages", description = "Get all messages in a conversation with pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    public ResponseEntity<ApiResponse<Page<MessageDto>>> getConversationMessages(
            @Parameter(description = "Conversation ID") @PathVariable Long conversationId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = messageService.getConversationMessages(conversationId, pageable);

        ApiResponse<Page<MessageDto>> response = ApiResponse.success(messages, "Messages retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages/{id}")
    @Operation(summary = "Get message by ID", description = "Get message details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found")
    })
    public ResponseEntity<ApiResponse<MessageDto>> getMessageById(
            @Parameter(description = "Message ID") @PathVariable Long id,
            HttpServletRequest request) {

        MessageDto messageDto = messageService.getMessageById(id);

        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/messages/{id}/read")
    @Operation(summary = "Mark message as read", description = "Mark a message as read")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message marked as read successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found")
    })
    public ResponseEntity<ApiResponse<MessageDto>> markMessageAsRead(
            @Parameter(description = "Message ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        MessageDto messageDto = messageService.markMessageAsRead(id, user.getId());

        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message marked as read successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/conversations/{conversationId}/messages/mark-all-read")
    @Operation(summary = "Mark all messages as read", description = "Mark all messages in a conversation as read")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All messages marked as read successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    public ResponseEntity<ApiResponse<Void>> markAllMessagesAsRead(
            @Parameter(description = "Conversation ID") @PathVariable Long conversationId,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        messageService.markAllMessagesAsRead(conversationId, user.getId());

        ApiResponse<Void> response = ApiResponse.success(null, "All messages marked as read successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/messages/{id}")
    @Operation(summary = "Delete message", description = "Delete a message (only sender can delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "You can only delete your own messages")
    })
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @Parameter(description = "Message ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        messageService.deleteMessage(id, user.getId());

        ApiResponse<Void> response = ApiResponse.success(null, "Message deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

