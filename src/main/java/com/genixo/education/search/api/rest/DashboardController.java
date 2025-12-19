package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.service.supply.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/supply/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard & Statistics", description = "APIs for dashboard and statistics in the supply system")
public class DashboardController {

    private final DashboardService dashboardService;

    // ================================ COMPANY DASHBOARD ================================

    @GetMapping("/company/summary")
    @Operation(summary = "Get company dashboard summary", description = "Get general summary for company (total spending, active orders, etc.)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<CompanySummaryDto>> getCompanySummary(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            HttpServletRequest request) {

        CompanySummaryDto summary = dashboardService.getCompanySummary(companyId);

        ApiResponse<CompanySummaryDto> response = ApiResponse.success(summary, "Summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/spending-analytics")
    @Operation(summary = "Get company spending analytics", description = "Get spending analytics for company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<SpendingAnalyticsDto>> getCompanySpendingAnalytics(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            HttpServletRequest request) {

        SpendingAnalyticsDto analytics = dashboardService.getCompanySpendingAnalytics(companyId);

        ApiResponse<SpendingAnalyticsDto> response = ApiResponse.success(analytics, "Analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/top-suppliers")
    @Operation(summary = "Get top suppliers for company", description = "Get top suppliers by spending for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top suppliers retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<List<TopSupplierDto>>> getCompanyTopSuppliers(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        List<TopSupplierDto> topSuppliers = dashboardService.getCompanyTopSuppliers(companyId, limit);

        ApiResponse<List<TopSupplierDto>> response = ApiResponse.success(topSuppliers, "Top suppliers retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/top-products")
    @Operation(summary = "Get top products for company", description = "Get top products by order count for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<List<TopProductDto>>> getCompanyTopProducts(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        List<TopProductDto> topProducts = dashboardService.getCompanyTopProducts(companyId, limit);

        ApiResponse<List<TopProductDto>> response = ApiResponse.success(topProducts, "Top products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/rfq-statistics")
    @Operation(summary = "Get RFQ statistics for company", description = "Get RFQ statistics for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<RFQStatisticsDto>> getCompanyRFQStatistics(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            HttpServletRequest request) {

        RFQStatisticsDto statistics = dashboardService.getCompanyRFQStatistics(companyId);

        ApiResponse<RFQStatisticsDto> response = ApiResponse.success(statistics, "RFQ statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/order-statistics")
    @Operation(summary = "Get order statistics for company", description = "Get order statistics for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<OrderStatisticsDto>> getCompanyOrderStatistics(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            HttpServletRequest request) {

        OrderStatisticsDto statistics = dashboardService.getCompanyOrderStatistics(companyId);

        ApiResponse<OrderStatisticsDto> response = ApiResponse.success(statistics, "Order statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SUPPLIER DASHBOARD ================================

    @GetMapping("/supplier/summary")
    @Operation(summary = "Get supplier dashboard summary", description = "Get general summary for supplier (total sales, active quotations, etc.)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierSummaryDto>> getSupplierSummary(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            HttpServletRequest request) {

        SupplierSummaryDto summary = dashboardService.getSupplierSummary(supplierId);

        ApiResponse<SupplierSummaryDto> response = ApiResponse.success(summary, "Summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/sales-analytics")
    @Operation(summary = "Get supplier sales analytics", description = "Get sales analytics for supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SalesAnalyticsDto>> getSupplierSalesAnalytics(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            HttpServletRequest request) {

        SalesAnalyticsDto analytics = dashboardService.getSupplierSalesAnalytics(supplierId);

        ApiResponse<SalesAnalyticsDto> response = ApiResponse.success(analytics, "Analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/quotation-statistics")
    @Operation(summary = "Get quotation statistics for supplier", description = "Get quotation statistics including acceptance rate for supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<QuotationStatisticsDto>> getSupplierQuotationStatistics(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            HttpServletRequest request) {

        QuotationStatisticsDto statistics = dashboardService.getSupplierQuotationStatistics(supplierId);

        ApiResponse<QuotationStatisticsDto> response = ApiResponse.success(statistics, "Quotation statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/top-products")
    @Operation(summary = "Get top products for supplier", description = "Get top selling products for a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<List<TopProductDto>>> getSupplierTopProducts(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        List<TopProductDto> topProducts = dashboardService.getSupplierTopProducts(supplierId, limit);

        ApiResponse<List<TopProductDto>> response = ApiResponse.success(topProducts, "Top products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/top-customers")
    @Operation(summary = "Get top customers for supplier", description = "Get top customers by spending for a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top customers retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<List<TopCustomerDto>>> getSupplierTopCustomers(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        List<TopCustomerDto> topCustomers = dashboardService.getSupplierTopCustomers(supplierId, limit);

        ApiResponse<List<TopCustomerDto>> response = ApiResponse.success(topCustomers, "Top customers retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/performance")
    @Operation(summary = "Get supplier performance metrics", description = "Get performance metrics for a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Performance metrics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierPerformanceDto>> getSupplierPerformance(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            HttpServletRequest request) {

        SupplierPerformanceDto performance = dashboardService.getSupplierPerformance(supplierId);

        ApiResponse<SupplierPerformanceDto> response = ApiResponse.success(performance, "Performance metrics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

