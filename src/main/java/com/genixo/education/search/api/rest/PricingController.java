package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.pricing.*;
import com.genixo.education.search.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pricing Management", description = "APIs for managing school pricing, fees, and payment terms")
public class PricingController {

    private final PricingService pricingService;

    // ================================ SCHOOL PRICING OPERATIONS ================================

    @PostMapping("/school-pricing")
    @Operation(summary = "Create school pricing", description = "Create pricing structure for a school grade and academic year")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School pricing created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid pricing data or pricing already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolPricingDto>> createSchoolPricing(
            @Valid @RequestBody SchoolPricingCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create school pricing request for school ID: {}, grade: {}, year: {}",
                createDto.getSchoolId(), createDto.getGradeLevel(), createDto.getAcademicYear());

        SchoolPricingDto pricing = pricingService.createSchoolPricing(createDto, request);

        ApiResponse<SchoolPricingDto> response = ApiResponse.success(pricing, "School pricing created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/school-pricing/{id}")
    @Operation(summary = "Get school pricing by ID", description = "Get detailed school pricing information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School pricing retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolPricingDto>> getSchoolPricingById(
            @Parameter(description = "School pricing ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get school pricing request: {}", id);

        SchoolPricingDto pricing = pricingService.getSchoolPricingById(id, request);

        ApiResponse<SchoolPricingDto> response = ApiResponse.success(pricing, "School pricing retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/school-pricing/current")
    @Operation(summary = "Get current school pricing", description = "Get current active pricing for specific school, grade, and academic year")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Current pricing retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No current pricing found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolPricingDto>> getCurrentSchoolPricing(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Grade level") @RequestParam String gradeLevel,
            @Parameter(description = "Academic year") @RequestParam String academicYear,
            HttpServletRequest request) {

        log.debug("Get current pricing request - School: {}, Grade: {}, Year: {}",
                schoolId, gradeLevel, academicYear);

        SchoolPricingDto pricing = pricingService.getCurrentSchoolPricing(schoolId, gradeLevel, academicYear, request);

        ApiResponse<SchoolPricingDto> response = ApiResponse.success(pricing, "Current pricing retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/school-pricing/school/{schoolId}")
    @Operation(summary = "Get all school pricings", description = "Get all pricing structures for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School pricings retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<SchoolPricingDto>>> getAllSchoolPricings(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get all pricings for school: {}", schoolId);

        List<SchoolPricingDto> pricings = pricingService.getAllSchoolPricings(schoolId, request);

        ApiResponse<List<SchoolPricingDto>> response = ApiResponse.success(pricings,
                "School pricings retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/school-pricing/{id}")
    @Operation(summary = "Update school pricing", description = "Update existing school pricing structure")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School pricing updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid pricing data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolPricingDto>> updateSchoolPricing(
            @Parameter(description = "School pricing ID") @PathVariable Long id,
            @Valid @RequestBody SchoolPricingUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update school pricing request: {}", id);

        SchoolPricingDto pricing = pricingService.updateSchoolPricing(id, updateDto, request);

        ApiResponse<SchoolPricingDto> response = ApiResponse.success(pricing, "School pricing updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/school-pricing/{id}/approve")
    @Operation(summary = "Approve school pricing", description = "Approve and activate school pricing structure")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School pricing approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status for approval"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<SchoolPricingDto>> approveSchoolPricing(
            @Parameter(description = "School pricing ID") @PathVariable Long id,
            @RequestBody ApprovalRequest approvalRequest,
            HttpServletRequest request) {

        log.info("Approve school pricing request: {}", id);

        SchoolPricingDto pricing = pricingService.approveSchoolPricing(id,
                approvalRequest.getApprovalNotes(), request);

        ApiResponse<SchoolPricingDto> response = ApiResponse.success(pricing, "School pricing approved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/school-pricing/compare")
    @Operation(summary = "Compare school pricing", description = "Compare pricing structures across multiple schools")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pricing comparison completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid comparison criteria")
    })
    public ResponseEntity<ApiResponse<PricingComparisonDto>> comparePricing(
            @Valid @RequestBody PricingComparisonRequest comparisonRequest,
            HttpServletRequest request) {

        log.info("Compare pricing request for {} schools", comparisonRequest.getSchoolIds().size());

        PricingComparisonDto comparison = pricingService.comparePricing(
                comparisonRequest.getSchoolIds(),
                comparisonRequest.getGradeLevel(),
                comparisonRequest.getAcademicYear());

        ApiResponse<PricingComparisonDto> response = ApiResponse.success(comparison,
                "Pricing comparison completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CUSTOM FEE OPERATIONS ================================

    @PostMapping("/custom-fees")
    @Operation(summary = "Create custom fee", description = "Add a custom fee to school pricing")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Custom fee created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid fee data or fee name exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CustomFeeDto>> createCustomFee(
            @Valid @RequestBody CustomFeeCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create custom fee request: {} for pricing ID: {}",
                createDto.getFeeName(), createDto.getSchoolId());

        CustomFeeDto customFee = pricingService.createCustomFee(createDto, request);

        ApiResponse<CustomFeeDto> response = ApiResponse.success(customFee, "Custom fee created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/custom-fees/school/{schoolId}")
    @Operation(summary = "Get custom fees by pricing", description = "Get all custom fees for a pricing structure")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Custom fees retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<CustomFeeDto>>> getCustomFeesByPricing(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get custom fees for school: {}", schoolId);

        List<CustomFeeDto> customFees = pricingService.getCustomFeesBySchool(schoolId, request);

        ApiResponse<List<CustomFeeDto>> response = ApiResponse.success(customFees,
                "Custom fees retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }







    @GetMapping("/custom-fees/{feeId}")
    @Operation(summary = "Get custom fees by pricing", description = "Get all custom fees for a pricing structure")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Custom fees retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CustomFeeDto>> getCustomFeesById(
            @Parameter(description = "School ID") @PathVariable Long feeId,
            HttpServletRequest request) {

        log.debug("Get custom fees for school: {}", feeId);

        CustomFeeDto customFees = pricingService.getCustomFeesById(feeId, request);

        ApiResponse<CustomFeeDto> response = ApiResponse.success(customFees,
                "Custom fees retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/custom-fees/{id}")
    @Operation(summary = "Update custom fee", description = "Update existing custom fee")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Custom fee updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Custom fee not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid fee data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CustomFeeDto>> updateCustomFee(
            @Parameter(description = "Custom fee ID") @PathVariable Long id,
            @Valid @RequestBody CustomFeeCreateDto updateDto,
            HttpServletRequest request) {

        log.info("Update custom fee request: {}", id);

        CustomFeeDto customFee = pricingService.updateCustomFee(id, updateDto, request);

        ApiResponse<CustomFeeDto> response = ApiResponse.success(customFee, "Custom fee updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/custom-fees/{id}")
    @Operation(summary = "Delete custom fee", description = "Remove custom fee from pricing structure")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Custom fee deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Custom fee not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCustomFee(
            @Parameter(description = "Custom fee ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete custom fee request: {}", id);

        pricingService.deleteCustomFee(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Custom fee deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRICE HISTORY OPERATIONS ================================

    @GetMapping("/price-history/{pricingId}")
    @Operation(summary = "Get price history", description = "Get price change history for a pricing structure")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Price history retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<PriceHistoryDto>>> getPriceHistory(
            @Parameter(description = "School pricing ID") @PathVariable Long pricingId,
            HttpServletRequest request) {

        log.debug("Get price history for pricing: {}", pricingId);

        List<PriceHistoryDto> history = pricingService.getPriceHistory(pricingId, request);

        ApiResponse<List<PriceHistoryDto>> response = ApiResponse.success(history,
                "Price history retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/price-trends/{schoolId}")
    @Operation(summary = "Get price trends", description = "Get price trend analysis for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Price trends retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PriceTrendsDto>> getSchoolPriceTrends(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Grade level") @RequestParam(required = false) String gradeLevel,
            @Parameter(description = "Start date") @RequestParam LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam LocalDateTime endDate,
            HttpServletRequest request) {

        log.debug("Get price trends for school: {}, grade: {} between {} and {}",
                schoolId, gradeLevel, startDate, endDate);

        PriceTrendsDto trends = pricingService.getSchoolPriceTrends(schoolId, gradeLevel, startDate, endDate, request);

        ApiResponse<PriceTrendsDto> response = ApiResponse.success(trends, "Price trends retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ANALYTICS OPERATIONS ================================

    @GetMapping("/analytics/{schoolId}")
    @Operation(summary = "Get pricing analytics", description = "Get comprehensive pricing analytics for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pricing analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PricingAnalyticsDto>> getPricingAnalytics(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get pricing analytics for school: {}", schoolId);

        PricingAnalyticsDto analytics = pricingService.getPricingAnalytics(schoolId, request);

        ApiResponse<PricingAnalyticsDto> response = ApiResponse.success(analytics,
                "Pricing analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/market-comparison/{schoolId}")
    @Operation(summary = "Get market comparison", description = "Compare school pricing with market averages")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Market comparison retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or pricing not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<MarketComparisonDto>> getMarketComparison(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Grade level") @RequestParam String gradeLevel,
            @Parameter(description = "Academic year") @RequestParam String academicYear,
            HttpServletRequest request) {

        log.debug("Get market comparison for school: {}, grade: {}, year: {}",
                schoolId, gradeLevel, academicYear);

        MarketComparisonDto comparison = pricingService.getMarketComparison(schoolId, gradeLevel, academicYear, request);

        ApiResponse<MarketComparisonDto> response = ApiResponse.success(comparison,
                "Market comparison retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CALCULATION OPERATIONS ================================

    @PostMapping("/calculate-cost")
    @Operation(summary = "Calculate total cost", description = "Calculate total cost including discounts and payment plans")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cost calculation completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pricing not found for criteria"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid calculation criteria")
    })
    public ResponseEntity<ApiResponse<PricingCalculationDto>> calculateTotalCost(
            @Valid @RequestBody PricingCalculationRequestDto calculationRequest,
            HttpServletRequest request) {

        log.info("Calculate total cost request for school: {}, grade: {}",
                calculationRequest.getSchoolId(), calculationRequest.getGradeLevel());

        PricingCalculationDto calculation = pricingService.calculateTotalCost(calculationRequest);

        ApiResponse<PricingCalculationDto> response = ApiResponse.success(calculation,
                "Cost calculation completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/bulk-update")
    @Operation(summary = "Bulk update pricing", description = "Apply bulk updates to multiple pricing structures")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk update completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "207", description = "Bulk update completed with some errors"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid bulk update data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BulkPricingResultDto>> bulkUpdatePricing(
            @Valid @RequestBody BulkPricingUpdateDto bulkDto,
            HttpServletRequest request) {

        log.info("Bulk update pricing request for {} items", bulkDto.getPricingUpdates().size());

        BulkPricingResultDto result = pricingService.bulkUpdatePricing(bulkDto, request);

        ApiResponse<BulkPricingResultDto> response = ApiResponse.success(result,
                "Bulk update completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        HttpStatus status = result.getFailedOperations() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    // ================================ REPORTING OPERATIONS ================================

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate pricing report", description = "Generate comprehensive pricing report")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid report criteria"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PricingReportDto>> generatePricingReport(
            @Valid @RequestBody PricingReportRequestDto reportRequest,
            HttpServletRequest request) {

        log.info("Generate pricing report request");

        PricingReportDto report = pricingService.generatePricingReport(reportRequest, request);

        ApiResponse<PricingReportDto> response = ApiResponse.success(report, "Report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/export")
    @Operation(summary = "Export pricing data", description = "Export pricing data in various formats")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Data exported successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid export criteria"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<byte[]> exportPricingData(
            @Valid @RequestBody PricingExportRequestDto exportRequest,
            HttpServletRequest request) {

        log.info("Export pricing data request in format: {}", exportRequest.getFormat());

        byte[] exportData = pricingService.exportPricingData(exportRequest, request);

        String filename = "pricing_data." + exportRequest.getFormat().toLowerCase();
        String contentType = exportRequest.getFormat().equalsIgnoreCase("xlsx") ?
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/csv";

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", contentType)
                .body(exportData);
    }

    // ================================ PUBLIC PRICING OPERATIONS ================================

    @GetMapping("/public/school/{schoolSlug}")
    @Operation(summary = "Get public school pricing", description = "Get public pricing information for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public pricing retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or pricing not available")
    })
    public ResponseEntity<ApiResponse<SchoolPricingDto>> getPublicSchoolPricing(
            @Parameter(description = "School slug") @PathVariable String schoolSlug,
            @Parameter(description = "Grade level") @RequestParam String gradeLevel,
            @Parameter(description = "Academic year") @RequestParam String academicYear,
            HttpServletRequest request) {

        log.debug("Get public pricing for school: {}, grade: {}", schoolSlug, gradeLevel);

        SchoolPricingDto pricing = pricingService.getPublicSchoolPricing(schoolSlug, gradeLevel, academicYear);

        ApiResponse<SchoolPricingDto> response = ApiResponse.success(pricing,
                "Public pricing retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/school/{schoolSlug}/summary")
    @Operation(summary = "Get public pricing summary", description = "Get public pricing summary for all grades")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public pricing summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or pricing not available")
    })
    public ResponseEntity<ApiResponse<List<PricingSummaryDto>>> getPublicPricingSummary(
            @Parameter(description = "School slug") @PathVariable String schoolSlug,
            HttpServletRequest request) {

        log.debug("Get public pricing summary for school: {}", schoolSlug);

        List<PricingSummaryDto> summary = pricingService.getPublicPricingSummary(schoolSlug);

        ApiResponse<List<PricingSummaryDto>> response = ApiResponse.success(summary,
                "Public pricing summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ TRENDING AND ANALYTICS FOR AUTHENTICATED USERS ================================

    @GetMapping("/trends/{schoolId}")
    @Operation(summary = "Get pricing trends", description = "Get pricing trend analysis for authenticated users")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pricing trends retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PriceTrendsDto>> getPricingTrends(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Period (1year, 2years, 3years, 5years)") @RequestParam(defaultValue = "3years") String period,
            HttpServletRequest request) {

        log.debug("Get pricing trends for school: {}, period: {}", schoolId, period);

        PriceTrendsDto trends = pricingService.getPricingTrends(schoolId, period, request);

        ApiResponse<PriceTrendsDto> response = ApiResponse.success(trends, "Pricing trends retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ REQUEST/RESPONSE DTOs ================================

    public static class ApprovalRequest {
        private String approvalNotes;

        public String getApprovalNotes() { return approvalNotes; }
        public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }
    }

    public static class PricingComparisonRequest {
        private List<Long> schoolIds;
        private String gradeLevel;
        private String academicYear;

        public List<Long> getSchoolIds() { return schoolIds; }
        public void setSchoolIds(List<Long> schoolIds) { this.schoolIds = schoolIds; }

        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }

        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    }
}