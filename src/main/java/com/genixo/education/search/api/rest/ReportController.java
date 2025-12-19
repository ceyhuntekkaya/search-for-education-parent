package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.service.supply.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/supply/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Management", description = "APIs for generating reports in the supply system")
public class ReportController {

    private final ReportService reportService;

    // ================================ COMPANY REPORTS ================================

    @GetMapping("/company/procurement")
    @Operation(summary = "Generate procurement report", description = "Generate comprehensive procurement report for a company (can be exported to Excel/PDF)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<ProcurementReportDto>> generateProcurementReport(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        // Default to last 12 months if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(12);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        ProcurementReportDto report = reportService.generateProcurementReport(companyId, startDate, endDate);

        ApiResponse<ProcurementReportDto> response = ApiResponse.success(report, "Procurement report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/spending-by-category")
    @Operation(summary = "Generate spending by category report", description = "Generate report showing spending breakdown by category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<SpendingByCategoryReportDto>> generateSpendingByCategoryReport(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        // Default to last 12 months if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(12);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        SpendingByCategoryReportDto report = reportService.generateSpendingByCategoryReport(companyId, startDate, endDate);

        ApiResponse<SpendingByCategoryReportDto> response = ApiResponse.success(report, "Spending by category report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/spending-by-supplier")
    @Operation(summary = "Generate spending by supplier report", description = "Generate report showing spending breakdown by supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<SpendingBySupplierReportDto>> generateSpendingBySupplierReport(
            @Parameter(description = "Company ID") @RequestParam Long companyId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        // Default to last 12 months if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(12);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        SpendingBySupplierReportDto report = reportService.generateSpendingBySupplierReport(companyId, startDate, endDate);

        ApiResponse<SpendingBySupplierReportDto> response = ApiResponse.success(report, "Spending by supplier report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SUPPLIER REPORTS ================================

    @GetMapping("/supplier/sales")
    @Operation(summary = "Generate sales report", description = "Generate comprehensive sales report for a supplier (can be exported to Excel/PDF)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SalesReportDto>> generateSalesReport(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        // Default to last 12 months if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(12);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        SalesReportDto report = reportService.generateSalesReport(supplierId, startDate, endDate);

        ApiResponse<SalesReportDto> response = ApiResponse.success(report, "Sales report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/quotation-performance")
    @Operation(summary = "Generate quotation performance report", description = "Generate report showing quotation performance metrics for a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<QuotationPerformanceReportDto>> generateQuotationPerformanceReport(
            @Parameter(description = "Supplier ID") @RequestParam Long supplierId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        // Default to last 12 months if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(12);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        QuotationPerformanceReportDto report = reportService.generateQuotationPerformanceReport(supplierId, startDate, endDate);

        ApiResponse<QuotationPerformanceReportDto> response = ApiResponse.success(report, "Quotation performance report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

