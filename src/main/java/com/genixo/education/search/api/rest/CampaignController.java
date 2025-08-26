package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.campaign.*;
import com.genixo.education.search.enumaration.CampaignContentType;
import com.genixo.education.search.service.CampaignService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Campaign Management", description = "APIs for managing marketing campaigns, discounts, and promotions")
public class CampaignController {

    private final CampaignService campaignService;
    private final JwtService jwtService;

    // ================================ CAMPAIGN CRUD OPERATIONS ================================

    @PostMapping
    @Operation(summary = "Create campaign", description = "Create a new marketing campaign with specified details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Campaign created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid campaign data or promo code already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> createCampaign(
            @Valid @RequestBody CampaignCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create campaign request: {}", createDto.getTitle());

        CampaignDto campaignDto = campaignService.createCampaign(createDto, request);

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Campaign created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get campaign by ID", description = "Retrieve campaign details by campaign ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> getCampaignById(
            @Parameter(description = "Campaign ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get campaign request: {}", id);

        CampaignDto campaignDto = campaignService.getCampaignById(id, request);

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Campaign retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get campaign by slug", description = "Retrieve campaign details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> getCampaignBySlug(
            @Parameter(description = "Campaign slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get campaign by slug request: {}", slug);

        CampaignDto campaignDto = campaignService.getCampaignBySlug(slug);

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Campaign retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update campaign", description = "Update campaign details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> updateCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long id,
            @Valid @RequestBody CampaignUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update campaign request: {}", id);

        CampaignDto campaignDto = campaignService.updateCampaign(id, updateDto, request);

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Campaign updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete campaign", description = "Soft delete a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Campaign has active usages")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete campaign request: {}", id);

        campaignService.deleteCampaign(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Campaign deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SEARCH AND LISTING ================================

    @PostMapping("/search")
    @Operation(summary = "Search campaigns", description = "Search campaigns with various filters and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<CampaignDto>>> searchCampaigns(
            @Valid @RequestBody CampaignSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search campaigns request");

        Page<CampaignDto> campaigns = campaignService.searchCampaigns(searchDto, request);

        ApiResponse<Page<CampaignDto>> response = ApiResponse.success(campaigns, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active campaigns", description = "Get all currently active campaigns")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active campaigns retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<CampaignSummaryDto>>> getActiveCampaigns(
            HttpServletRequest request) {

        log.debug("Get active campaigns request");

        List<CampaignSummaryDto> campaigns = campaignService.getActiveCampaigns();

        ApiResponse<List<CampaignSummaryDto>> response = ApiResponse.success(campaigns, "Active campaigns retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}")
    @Operation(summary = "Get campaigns by school", description = "Get all campaigns assigned to a specific school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School campaigns retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<List<CampaignDto>>> getCampaignsBySchool(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get campaigns by school request: {}", schoolId);

        List<CampaignDto> campaigns = campaignService.getCampaignsBySchool(schoolId, request);

        ApiResponse<List<CampaignDto>> response = ApiResponse.success(campaigns, "School campaigns retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CAMPAIGN SCHOOL MANAGEMENT ================================

    @PostMapping("/{campaignId}/schools/assign")
    @Operation(summary = "Assign schools to campaign", description = "Assign multiple schools to a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Schools assigned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BulkCampaignResultDto>> assignSchoolsToCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            @Valid @RequestBody SchoolAssignmentRequestDto assignmentRequest,
            HttpServletRequest request) {

        log.info("Assign schools to campaign request: {}", campaignId);

        BulkCampaignResultDto result = campaignService.assignSchoolsToCampaign(
                campaignId, assignmentRequest.getSchoolIds(), request);

        ApiResponse<BulkCampaignResultDto> response = ApiResponse.success(result, "Schools assigned successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{campaignId}/schools/{schoolId}")
    @Operation(summary = "Update campaign school assignment", description = "Update campaign assignment details for a specific school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assignment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign or assignment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignSchoolDto>> updateCampaignSchoolAssignment(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Valid @RequestBody CampaignSchoolAssignDto assignDto,
            HttpServletRequest request) {

        log.info("Update campaign school assignment: campaign={}, school={}", campaignId, schoolId);

        CampaignSchoolDto campaignSchoolDto = campaignService.updateCampaignSchoolAssignment(
                campaignId, schoolId, assignDto, request);

        ApiResponse<CampaignSchoolDto> response = ApiResponse.success(campaignSchoolDto, "Assignment updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{campaignId}/schools/{schoolId}")
    @Operation(summary = "Remove school from campaign", description = "Remove a school from campaign assignment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign or assignment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Campaign has active usages for this school")
    })
    public ResponseEntity<ApiResponse<Void>> removeSchoolFromCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.info("Remove school from campaign: campaign={}, school={}", campaignId, schoolId);

        campaignService.removeSchoolFromCampaign(campaignId, schoolId, request);

        ApiResponse<Void> response = ApiResponse.success(null, "School removed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CAMPAIGN USAGE MANAGEMENT ================================

    @PostMapping("/usages")
    @Operation(summary = "Create campaign usage", description = "Create a new campaign usage record when a user applies a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Campaign usage created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid usage data or limits exceeded"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign or school not found")
    })
    public ResponseEntity<ApiResponse<CampaignUsageDto>> createCampaignUsage(
            @Valid @RequestBody CampaignUsageCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create campaign usage request for campaign: {}, school: {}",
                createDto.getCampaignId(), createDto.getSchoolId());

        CampaignUsageDto usageDto = campaignService.createCampaignUsage(createDto, request);

        ApiResponse<CampaignUsageDto> response = ApiResponse.success(usageDto, "Campaign usage created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{campaignId}/usages")
    @Operation(summary = "Get campaign usages", description = "Get all usage records for a specific campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign usages retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<CampaignUsageDto>>> getCampaignUsages(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            Pageable pageable,
            HttpServletRequest request) {

        log.debug("Get campaign usages request: {}", campaignId);

        Page<CampaignUsageDto> usages = campaignService.getCampaignUsages(campaignId, pageable, request);

        ApiResponse<Page<CampaignUsageDto>> response = ApiResponse.success(usages, "Campaign usages retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ANALYTICS AND REPORTING ================================

    @GetMapping("/{campaignId}/analytics")
    @Operation(summary = "Get campaign analytics", description = "Get comprehensive analytics for a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignAnalyticsDto>> getCampaignAnalytics(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            HttpServletRequest request) {

        log.debug("Get campaign analytics request: {}", campaignId);

        CampaignAnalyticsDto analytics = campaignService.getCampaignAnalytics(campaignId, request);

        ApiResponse<CampaignAnalyticsDto> response = ApiResponse.success(analytics, "Analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate campaign report", description = "Generate comprehensive campaign performance report")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid report parameters")
    })
    public ResponseEntity<ApiResponse<CampaignReportDto>> generateCampaignReport(
            @Valid @RequestBody CampaignReportRequestDto reportRequest,
            HttpServletRequest request) {

        log.info("Generate campaign report request for {} campaigns", reportRequest.getCampaignIds().size());

        CampaignReportDto report = campaignService.generateCampaignReport(
                reportRequest.getCampaignIds(),
                reportRequest.getStartDate(),
                reportRequest.getEndDate(),
                reportRequest.getReportType(),
                request
        );

        ApiResponse<CampaignReportDto> response = ApiResponse.success(report, "Report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/bulk")
    @Operation(summary = "Bulk campaign operations", description = "Perform bulk operations on multiple campaigns")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk operation completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid bulk operation data")
    })
    public ResponseEntity<ApiResponse<BulkCampaignResultDto>> bulkCampaignOperation(
            @Valid @RequestBody BulkCampaignOperationDto operationDto,
            HttpServletRequest request) {

        log.info("Bulk campaign operation request: {} on {} campaigns",
                operationDto.getOperation(), operationDto.getCampaignIds().size());

        BulkCampaignResultDto result = campaignService.bulkCampaignOperation(operationDto, request);

        ApiResponse<BulkCampaignResultDto> response = ApiResponse.success(result, "Bulk operation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PUBLIC ENDPOINTS (NO AUTH REQUIRED) ================================

    @GetMapping("/public/active")
    @Operation(summary = "Get public active campaigns", description = "Get active campaigns visible to public (no authentication required)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public active campaigns retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<CampaignSummaryDto>>> getPublicActiveCampaigns(
            HttpServletRequest request) {

        log.debug("Get public active campaigns request");

        List<CampaignSummaryDto> campaigns = campaignService.getPublicActiveCampaigns();

        ApiResponse<List<CampaignSummaryDto>> response = ApiResponse.success(campaigns, "Public active campaigns retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/schools/{schoolId}")
    @Operation(summary = "Get public campaigns by school", description = "Get active campaigns for a specific school (public view)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public school campaigns retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<List<CampaignSummaryDto>>> getPublicCampaignsBySchool(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get public campaigns by school request: {}", schoolId);

        List<CampaignSummaryDto> campaigns = campaignService.getPublicCampaignsBySchool(schoolId);

        ApiResponse<List<CampaignSummaryDto>> response = ApiResponse.success(campaigns, "Public school campaigns retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/validate-promo")
    @Operation(summary = "Validate promo code", description = "Validate promo code and return campaign details (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promo code validated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired promo code"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Promo code not found")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> validatePromoCode(
            @Valid @RequestBody PromoCodeValidationRequestDto validationRequest,
            HttpServletRequest request) {

        log.info("Validate promo code request: {} for school: {}",
                validationRequest.getPromoCode(), validationRequest.getSchoolId());

        CampaignDto campaignDto = campaignService.validatePromoCode(
                validationRequest.getPromoCode(), validationRequest.getSchoolId());

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Promo code validated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CAMPAIGN STATUS MANAGEMENT ================================

    @PostMapping("/{campaignId}/activate")
    @Operation(summary = "Activate campaign", description = "Activate a campaign to make it live")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Campaign cannot be activated")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> activateCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            HttpServletRequest request) {

        log.info("Activate campaign request: {}", campaignId);

        CampaignUpdateDto updateDto = new CampaignUpdateDto();
        updateDto.setStatus(com.genixo.education.search.enumaration.CampaignStatus.ACTIVE);

        CampaignDto campaignDto = campaignService.updateCampaign(campaignId, updateDto, request);

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Campaign activated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{campaignId}/pause")
    @Operation(summary = "Pause campaign", description = "Pause an active campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign paused successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> pauseCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            HttpServletRequest request) {

        log.info("Pause campaign request: {}", campaignId);

        CampaignUpdateDto updateDto = new CampaignUpdateDto();
        updateDto.setStatus(com.genixo.education.search.enumaration.CampaignStatus.PAUSED);

        CampaignDto campaignDto = campaignService.updateCampaign(campaignId, updateDto, request);

        ApiResponse<CampaignDto> response = ApiResponse.success(campaignDto, "Campaign paused successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{campaignId}/duplicate")
    @Operation(summary = "Duplicate campaign", description = "Create a copy of an existing campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Campaign duplicated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignDto>> duplicateCampaign(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            @Valid @RequestBody CampaignDuplicateRequestDto duplicateRequest,
            HttpServletRequest request) {

        log.info("Duplicate campaign request: {}", campaignId);

        // This would be implemented in the service
        // For now, return a placeholder response
        CampaignDto originalCampaign = campaignService.getCampaignById(campaignId, request);

        ApiResponse<CampaignDto> response = ApiResponse.success(originalCampaign, "Campaign duplicated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ HELPER REQUEST DTOs ================================

    public static class SchoolAssignmentRequestDto {
        private List<Long> schoolIds;

        public List<Long> getSchoolIds() { return schoolIds; }
        public void setSchoolIds(List<Long> schoolIds) { this.schoolIds = schoolIds; }
    }

    public static class CampaignReportRequestDto {
        private List<Long> campaignIds;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reportType;

        public List<Long> getCampaignIds() { return campaignIds; }
        public void setCampaignIds(List<Long> campaignIds) { this.campaignIds = campaignIds; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
    }

    public static class PromoCodeValidationRequestDto {
        private String promoCode;
        private Long schoolId;

        public String getPromoCode() { return promoCode; }
        public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    }

    public static class CampaignDuplicateRequestDto {
        private String newTitle;
        private LocalDate newStartDate;
        private LocalDate newEndDate;
        private Boolean copySchoolAssignments;
        private Boolean copyContent;

        public String getNewTitle() { return newTitle; }
        public void setNewTitle(String newTitle) { this.newTitle = newTitle; }

        public LocalDate getNewStartDate() { return newStartDate; }
        public void setNewStartDate(LocalDate newStartDate) { this.newStartDate = newStartDate; }

        public LocalDate getNewEndDate() { return newEndDate; }
        public void setNewEndDate(LocalDate newEndDate) { this.newEndDate = newEndDate; }

        public Boolean getCopySchoolAssignments() { return copySchoolAssignments; }
        public void setCopySchoolAssignments(Boolean copySchoolAssignments) { this.copySchoolAssignments = copySchoolAssignments; }

        public Boolean getCopyContent() { return copyContent; }
        public void setCopyContent(Boolean copyContent) { this.copyContent = copyContent; }
    }

    // ================================ CAMPAIGN CONTENT MANAGEMENT ================================

    @GetMapping("/{campaignId}/content")
    @Operation(summary = "Get campaign content", description = "Get all content associated with a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campaign content retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<CampaignContentDto>>> getCampaignContent(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            HttpServletRequest request) {

        log.debug("Get campaign content request: {}", campaignId);

        // This would be implemented in the service
        List<CampaignContentDto> content = List.of(); // Placeholder

        ApiResponse<List<CampaignContentDto>> response = ApiResponse.success(content, "Campaign content retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{campaignId}/content")
    @Operation(summary = "Add campaign content", description = "Add new content to a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Campaign content added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignContentDto>> addCampaignContent(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            @Valid @RequestBody CampaignContentCreateDto createDto,
            HttpServletRequest request) {

        log.info("Add campaign content request: {}", campaignId);

        // This would be implemented in the service

        CampaignContentDto content = new CampaignContentDto();
        /* ceyhun
        CampaignContentDto content = CampaignContentDto.builder()
                .id(System.currentTimeMillis()) // Mock ID
                .campaignId(campaignId)
                .contentType(createDto.getContentType())
                .title(createDto.getTitle())
                .content(createDto.getContent())
                .mediaUrl(createDto.getMediaUrl())
                .ctaText(createDto.getCtaText())
                .ctaUrl(createDto.getCtaUrl())
                .isActive(true)
                .sortOrder(createDto.getSortOrder())
                .build();

         */

        ApiResponse<CampaignContentDto> response = ApiResponse.success(content, "Campaign content added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ CAMPAIGN STATISTICS ================================

    @GetMapping("/{campaignId}/statistics")
    @Operation(summary = "Get campaign statistics", description = "Get detailed statistics for a campaign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campaign not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampaignAnalyticsDto>> getCampaignStatistics(
            @Parameter(description = "Campaign ID") @PathVariable Long campaignId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        log.debug("Get campaign statistics request: {}", campaignId);

        // This would be implemented in the service  ceyhun
        CampaignAnalyticsDto statistics = new CampaignAnalyticsDto();
        /*
        CampaignAnalyticsDto statistics = CampaignAnalyticsDto.builder()
                .campaignId(campaignId)
                .totalViews(1250L)
                .totalClicks(89L)
                .totalApplications(23L)
                .totalUsages(15L)
                .clickThroughRate(7.12)
                .conversionRate(2.58)
                .applicationRate(1.84)
                .totalDiscountGiven(java.math.BigDecimal.valueOf(45000))
                .averageDiscountAmount(java.math.BigDecimal.valueOf(3000))
                .build();

         */




        ApiResponse<CampaignAnalyticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }



    // ================================ ADDITIONAL HELPER DTOs ================================
@Getter
@Setter
    public static class CampaignContentCreateDto {
        private CampaignContentType contentType;
        private String title;
        private String content;
        private String mediaUrl;
        private String ctaText;
        private String ctaUrl;
        private Integer sortOrder;


    }

    @Setter
    @Getter
    public static class CampaignValidationRequestDto {
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String promoCode;
        private List<Long> schoolIds;

    }
}
