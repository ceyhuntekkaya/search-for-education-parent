package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.enumaration.PaymentStatus;
import com.genixo.education.search.service.supply.PaymentService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/supply/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "APIs for managing payments in the supply system")
public class PaymentController {

    private final PaymentService paymentService;

    // ================================ PAYMENT CRUD ================================

    @PostMapping
    @Operation(summary = "Create new payment", description = "Create a new payment record for an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payment data or payment already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(
            @Valid @RequestBody PaymentCreateDto createDto,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.createPayment(createDto);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Get payment details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.getPaymentById(id);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Get payment for a specific order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order or payment not found")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.getPaymentByOrder(orderId);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "Get payments by company", description = "Get all payments for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> getPaymentsByCompany(
            @Parameter(description = "Company ID") @PathVariable Long companyId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDto> payments = paymentService.getPaymentsByCompany(companyId, pageable);

        ApiResponse<Page<PaymentDto>> response = ApiResponse.success(payments, "Payments retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Get all payments with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> getAllPayments(
            @Parameter(description = "Search term (transaction ID, notes)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by company ID") @RequestParam(required = false) Long companyId,
            @Parameter(description = "Filter by order ID") @RequestParam(required = false) Long orderId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) PaymentStatus status,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaymentDto> payments = paymentService.getAllPayments(searchTerm, companyId, orderId, status, pageable);

        ApiResponse<Page<PaymentDto>> response = ApiResponse.success(payments, "Payments retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment", description = "Update payment information (cannot update COMPLETED, REFUNDED, or FAILED payments)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update COMPLETED, REFUNDED, or FAILED payment")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> updatePayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Valid @RequestBody PaymentUpdateDto updateDto,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.updatePayment(id, updateDto);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Update payment status with validation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> updatePaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Valid @RequestBody PaymentStatusUpdateDto statusDto,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.updatePaymentStatus(id, statusDto);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment status updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm payment", description = "Confirm a PENDING or PROCESSING payment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment confirmed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Only PENDING or PROCESSING payments can be confirmed")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> confirmPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.confirmPayment(id);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment confirmed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/fail")
    @Operation(summary = "Fail payment", description = "Mark payment as failed (cannot fail COMPLETED or REFUNDED payments)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment failed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot fail COMPLETED or REFUNDED payment")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> failPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.failPayment(id);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment failed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment", description = "Refund a COMPLETED payment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment refunded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Only COMPLETED payments can be refunded"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> refundPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Valid @RequestBody PaymentRefundDto refundDto,
            HttpServletRequest request) {

        PaymentDto paymentDto = paymentService.refundPayment(id, refundDto);

        ApiResponse<PaymentDto> response = ApiResponse.success(paymentDto, "Payment refunded successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

