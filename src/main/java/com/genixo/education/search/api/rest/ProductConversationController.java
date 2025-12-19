package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.ConversationCreateDto;
import com.genixo.education.search.dto.supply.ConversationDto;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.supply.ProductConversationService;
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
@RequestMapping("/supply/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Conversation Management", description = "APIs for managing conversations in the supply system")
public class ProductConversationController {

    private final ProductConversationService conversationService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Create new conversation", description = "Start a new conversation between company and supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Conversation created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid conversation data or supplier inactive"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company, supplier, product, quotation or order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ConversationDto>> createConversation(
            @Valid @RequestBody ConversationCreateDto createDto,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        ConversationDto conversationDto = conversationService.createConversation(user.getId(), createDto);

        ApiResponse<ConversationDto> response = ApiResponse.success(conversationDto, "Conversation created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get user conversations", description = "Get all conversations for the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversations retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getUserConversations(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getUserConversations(user.getId(), pageable);

        ApiResponse<Page<ConversationDto>> response = ApiResponse.success(conversations, "Conversations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conversation by ID", description = "Get conversation details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversation retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    public ResponseEntity<ApiResponse<ConversationDto>> getConversationById(
            @Parameter(description = "Conversation ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        ConversationDto conversationDto = conversationService.getConversationById(id, user.getId());

        ApiResponse<ConversationDto> response = ApiResponse.success(conversationDto, "Conversation retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "Get conversations by company", description = "Get all conversations for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getConversationsByCompany(
            @Parameter(description = "Company ID") @PathVariable Long companyId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByCompany(companyId, pageable);

        ApiResponse<Page<ConversationDto>> response = ApiResponse.success(conversations, "Conversations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-supplier/{supplierId}")
    @Operation(summary = "Get conversations by supplier", description = "Get all conversations for a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getConversationsBySupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long supplierId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsBySupplier(supplierId, pageable);

        ApiResponse<Page<ConversationDto>> response = ApiResponse.success(conversations, "Conversations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Get conversations by product", description = "Get all conversations for a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getConversationsByProduct(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByProduct(productId, pageable);

        ApiResponse<Page<ConversationDto>> response = ApiResponse.success(conversations, "Conversations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-quotation/{quotationId}")
    @Operation(summary = "Get conversations by quotation", description = "Get all conversations for a quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getConversationsByQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long quotationId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByQuotation(quotationId, pageable);

        ApiResponse<Page<ConversationDto>> response = ApiResponse.success(conversations, "Conversations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Get conversations by order", description = "Get all conversations for an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getConversationsByOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByOrder(orderId, pageable);

        ApiResponse<Page<ConversationDto>> response = ApiResponse.success(conversations, "Conversations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread conversation count", description = "Get count of unread conversations for the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Long>> getUnreadConversationCount(
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        Long count = conversationService.getUnreadConversationCount(user.getId());

        ApiResponse<Long> response = ApiResponse.success(count, "Unread count retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete conversation", description = "Delete a conversation (only if user has access)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversation deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "You don't have permission to delete this conversation")
    })
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @Parameter(description = "Conversation ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        conversationService.deleteConversation(id, user.getId());

        ApiResponse<Void> response = ApiResponse.success(null, "Conversation deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

