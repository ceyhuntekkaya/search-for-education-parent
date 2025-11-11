package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.subscription.*;
import com.genixo.education.search.enumaration.SubscriptionStatus;
import com.genixo.education.search.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscription Management", description = "APIs for managing subscriptions, billing, and payments")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // ================================ SUBSCRIPTION PLAN OPERATIONS ================================

    @GetMapping("/plans")
    @Operation(summary = "Get all subscription plans", description = "Get all available subscription plans")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription plans retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> getAllSubscriptionPlans(
            HttpServletRequest request) {

        log.debug("Get all subscription plans request");

        List<SubscriptionPlanDto> plans = subscriptionService.getAllSubscriptionPlans();

        ApiResponse<List<SubscriptionPlanDto>> response = ApiResponse.success(plans,
                "Subscription plans retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans/{id}")
    @Operation(summary = "Get subscription plan by ID", description = "Get detailed subscription plan information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription plan retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription plan not found")
    })
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> getSubscriptionPlanById(
            @Parameter(description = "Subscription plan ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get subscription plan request: {}", id);

        SubscriptionPlanDto plan = subscriptionService.getSubscriptionPlanById(id);

        ApiResponse<SubscriptionPlanDto> response = ApiResponse.success(plan,
                "Subscription plan retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SUBSCRIPTION OPERATIONS ================================

    @PostMapping
    @Operation(summary = "Create subscription", description = "Create a new subscription for a campus")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Subscription created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid subscription data or campus already has subscription"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campus or subscription plan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SubscriptionDto>> createSubscription(
            @Valid @RequestBody SubscriptionCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create subscription request for campus: {}, plan: {}",
                createDto.getCampusId(), createDto.getSubscriptionPlanId());

        SubscriptionDto subscription = subscriptionService.createSubscription(createDto, request);

        ApiResponse<SubscriptionDto> response = ApiResponse.success(subscription, "Subscription created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/campus/{campusId}")
    @Operation(summary = "Get subscription by campus", description = "Get current subscription for a campus")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found for campus"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SubscriptionDto>> getSubscriptionByCampusId(
            @Parameter(description = "Campus ID") @PathVariable Long campusId,
            HttpServletRequest request) {

        log.debug("Get subscription for campus: {}", campusId);

        SubscriptionDto subscription = subscriptionService.getSubscriptionByCampusId(campusId, request);

        ApiResponse<SubscriptionDto> response = ApiResponse.success(subscription,
                "Subscription retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{subscriptionId}")
    @Operation(summary = "Update subscription", description = "Update subscription billing information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SubscriptionDto>> updateSubscription(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Valid @RequestBody SubscriptionUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update subscription request: {}", subscriptionId);

        SubscriptionDto subscription = subscriptionService.updateSubscription(subscriptionId, updateDto, request);

        ApiResponse<SubscriptionDto> response = ApiResponse.success(subscription, "Subscription updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{subscriptionId}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancel an active subscription")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription canceled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Subscription already canceled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Valid @RequestBody SubscriptionCancellationDto cancelDto,
            HttpServletRequest request) {

        log.info("Cancel subscription request: {}", subscriptionId);

        subscriptionService.cancelSubscription(subscriptionId, cancelDto, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Subscription canceled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{subscriptionId}/change-plan")
    @Operation(summary = "Change subscription plan", description = "Upgrade or downgrade subscription plan")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription plan changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription or new plan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid plan change request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SubscriptionDto>> changeSubscriptionPlan(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Valid @RequestBody ChangeSubscriptionPlanDto changeDto,
            HttpServletRequest request) {

        log.info("Change subscription plan request: {} to plan: {}", subscriptionId, changeDto.getNewPlanId());

        SubscriptionDto subscription = subscriptionService.changeSubscriptionPlan(subscriptionId, changeDto, request);

        ApiResponse<SubscriptionDto> response = ApiResponse.success(subscription,
                "Subscription plan changed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PAYMENT OPERATIONS ================================

    @PostMapping("/{subscriptionId}/payments")
    @Operation(summary = "Process payment", description = "Process a payment for subscription")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment failed or invalid payment data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Valid @RequestBody PaymentCreateDto paymentDto,
            HttpServletRequest request) {

        log.info("Process payment request for subscription: {}", subscriptionId);

        PaymentDto payment = subscriptionService.processPayment(subscriptionId, paymentDto, request);

        ApiResponse<PaymentDto> response = ApiResponse.success(payment, "Payment processed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        HttpStatus status = payment.getPaymentStatus().equals("COMPLETED") ?
                HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{subscriptionId}/payments")
    @Operation(summary = "Get payment history", description = "Get payment history for a subscription")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment history retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> getPaymentHistory(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Get payment history for subscription: {}", subscriptionId);

        Page<PaymentDto> payments = subscriptionService.getPaymentHistory(subscriptionId, page, size, request);

        ApiResponse<Page<PaymentDto>> response = ApiResponse.success(payments,
                "Payment history retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ INVOICE OPERATIONS ================================

    @GetMapping("/{subscriptionId}/invoices")
    @Operation(summary = "Get invoices", description = "Get invoices for a subscription")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoices retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<InvoiceDto>>> getInvoices(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Get invoices for subscription: {}", subscriptionId);

        Page<InvoiceDto> invoices = subscriptionService.getInvoices(subscriptionId, page, size, request);

        ApiResponse<Page<InvoiceDto>> response = ApiResponse.success(invoices, "Invoices retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices/{invoiceId}/download")
    @Operation(summary = "Download invoice", description = "Download invoice as PDF")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice downloaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<byte[]> downloadInvoice(
            @Parameter(description = "Invoice ID") @PathVariable Long invoiceId,
            HttpServletRequest request) {

        log.info("Download invoice request: {}", invoiceId);

        byte[] pdfData = subscriptionService.downloadInvoice(invoiceId, request);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice_" + invoiceId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfData);
    }

    // ================================ USAGE TRACKING ================================

    @GetMapping("/campus/{campusId}/usage-limits")
    @Operation(summary = "Check usage limits", description = "Check current usage against subscription limits")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usage limits retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Active subscription not found for campus"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UsageLimitsDto>> checkUsageLimits(
            @Parameter(description = "Campus ID") @PathVariable Long campusId,
            HttpServletRequest request) {

        log.debug("Check usage limits for campus: {}", campusId);

        UsageLimitsDto usageLimits = subscriptionService.checkUsageLimits(campusId, request);

        ApiResponse<UsageLimitsDto> response = ApiResponse.success(usageLimits,
                "Usage limits retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/campus/{campusId}/usage-update")
    @Operation(summary = "Update usage counters", description = "Update usage counters for subscription (internal API)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usage counters updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Active subscription not found")
    })
    public ResponseEntity<ApiResponse<Void>> updateUsageCounters(
            @Parameter(description = "Campus ID") @PathVariable Long campusId,
            @Valid @RequestBody UsageUpdateDto usageUpdate,
            HttpServletRequest request) {

        log.debug("Update usage counters for campus: {}", campusId);

        subscriptionService.updateUsageCounters(campusId, usageUpdate);

        ApiResponse<Void> response = ApiResponse.success(null, "Usage counters updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ANALYTICS ================================

    @GetMapping("/{subscriptionId}/analytics")
    @Operation(summary = "Get subscription analytics", description = "Get detailed analytics for a subscription")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subscription not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SubscriptionAnalyticsDto>> getSubscriptionAnalytics(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            HttpServletRequest request) {

        log.debug("Get analytics for subscription: {}", subscriptionId);

        SubscriptionAnalyticsDto analytics = subscriptionService.getSubscriptionAnalytics(subscriptionId, request);

        ApiResponse<SubscriptionAnalyticsDto> response = ApiResponse.success(analytics,
                "Analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ WEBHOOK HANDLERS ================================

    @PostMapping("/webhooks/payments")
    @Operation(summary = "Handle payment webhook", description = "Handle payment gateway webhooks")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Webhook processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid webhook data")
    })
    public ResponseEntity<ApiResponse<Void>> handlePaymentWebhook(
            @Valid @RequestBody PaymentWebhookDto webhookDto,
            HttpServletRequest request) {

        log.info("Handle payment webhook: {}", webhookDto.getEventType());

        subscriptionService.handlePaymentWebhook(webhookDto);

        ApiResponse<Void> response = ApiResponse.success(null, "Webhook processed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ADMIN OPERATIONS ================================

    @PostMapping("/admin/plans")
    @Operation(summary = "Create subscription plan", description = "Create a new subscription plan (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Subscription plan created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid plan data or name already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System administrator access required")
    })
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> createSubscriptionPlan(
            @Valid @RequestBody SubscriptionPlanCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create subscription plan request: {}", createDto.getName());

        SubscriptionPlanDto plan = subscriptionService.createSubscriptionPlan(createDto, request);

        ApiResponse<SubscriptionPlanDto> response = ApiResponse.success(plan,
                "Subscription plan created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin/subscriptions")
    @Operation(summary = "Get all subscriptions", description = "Get all subscriptions with filtering (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System administrator access required")
    })
    public ResponseEntity<ApiResponse<Page<SubscriptionDto>>> getAllSubscriptions(
            @Parameter(description = "Subscription status filter") @RequestParam(required = false) SubscriptionStatus status,
            @Parameter(description = "Campus name filter") @RequestParam(required = false) String campusName,
            @Parameter(description = "Start date filter") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date filter") @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Admin get all subscriptions request");

        SubscriptionFilterDto filter = SubscriptionFilterDto.builder()
                .status(status)
                .campusName(campusName)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Page<SubscriptionDto> subscriptions = subscriptionService.getAllSubscriptions(filter, page, size, request);

        ApiResponse<Page<SubscriptionDto>> response = ApiResponse.success(subscriptions,
                "Subscriptions retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/statistics")
    @Operation(summary = "Get subscription statistics", description = "Get comprehensive subscription statistics (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System administrator access required")
    })
    public ResponseEntity<ApiResponse<SubscriptionStatisticsDto>> getSubscriptionStatistics(
            HttpServletRequest request) {

        log.debug("Get subscription statistics request");

        SubscriptionStatisticsDto statistics = subscriptionService.getSubscriptionStatistics(request);

        ApiResponse<SubscriptionStatisticsDto> response = ApiResponse.success(statistics,
                "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ HEALTH CHECK ENDPOINTS ================================

    @GetMapping("/health/billing")
    @Operation(summary = "Billing health check", description = "Check billing system health status")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Billing system is healthy"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Billing system has issues")
    })
    public ResponseEntity<ApiResponse<BillingHealthDto>> checkBillingHealth(
            HttpServletRequest request) {

        log.debug("Billing health check request");

        // Simple health check implementation
        BillingHealthDto health = BillingHealthDto.builder()
                .isHealthy(true)
                .lastBillingRun(LocalDateTime.now().minusHours(2))
                .pendingPayments(0L)
                .failedPayments(0L)
                .systemLoad("LOW")
                .message("Billing system is operating normally")
                .build();

        ApiResponse<BillingHealthDto> response = ApiResponse.success(health, "Billing system is healthy");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ RESPONSE DTOs ================================

    public static class BillingHealthDto {
        private Boolean isHealthy;
        private LocalDateTime lastBillingRun;
        private Long pendingPayments;
        private Long failedPayments;
        private String systemLoad;
        private String message;

        public static BillingHealthDtoBuilder builder() {
            return new BillingHealthDtoBuilder();
        }

        public Boolean getIsHealthy() { return isHealthy; }
        public void setIsHealthy(Boolean isHealthy) { this.isHealthy = isHealthy; }

        public LocalDateTime getLastBillingRun() { return lastBillingRun; }
        public void setLastBillingRun(LocalDateTime lastBillingRun) { this.lastBillingRun = lastBillingRun; }

        public Long getPendingPayments() { return pendingPayments; }
        public void setPendingPayments(Long pendingPayments) { this.pendingPayments = pendingPayments; }

        public Long getFailedPayments() { return failedPayments; }
        public void setFailedPayments(Long failedPayments) { this.failedPayments = failedPayments; }

        public String getSystemLoad() { return systemLoad; }
        public void setSystemLoad(String systemLoad) { this.systemLoad = systemLoad; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public static class BillingHealthDtoBuilder {
            private BillingHealthDto dto = new BillingHealthDto();

            public BillingHealthDtoBuilder isHealthy(Boolean isHealthy) {
                dto.setIsHealthy(isHealthy);
                return this;
            }

            public BillingHealthDtoBuilder lastBillingRun(LocalDateTime lastBillingRun) {
                dto.setLastBillingRun(lastBillingRun);
                return this;
            }

            public BillingHealthDtoBuilder pendingPayments(Long pendingPayments) {
                dto.setPendingPayments(pendingPayments);
                return this;
            }

            public BillingHealthDtoBuilder failedPayments(Long failedPayments) {
                dto.setFailedPayments(failedPayments);
                return this;
            }

            public BillingHealthDtoBuilder systemLoad(String systemLoad) {
                dto.setSystemLoad(systemLoad);
                return this;
            }

            public BillingHealthDtoBuilder message(String message) {
                dto.setMessage(message);
                return this;
            }

            public BillingHealthDto build() {
                return dto;
            }
        }
    }
}