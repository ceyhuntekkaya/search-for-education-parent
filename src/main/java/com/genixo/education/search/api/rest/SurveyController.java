package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.survey.*;
import com.genixo.education.search.service.SurveyService;
import com.genixo.education.search.enumaration.RatingCategory;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Survey Management", description = "APIs for managing surveys, feedback collection, and analytics")
public class SurveyController {

    private final SurveyService surveyService;

    // ================================ SURVEY OPERATIONS ================================

    @PostMapping
    @Operation(summary = "Create survey", description = "Create a new survey with optional questions")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Survey created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid survey data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SurveyDto>> createSurvey(
            @Valid @RequestBody SurveyCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create survey request: {}", createDto.getTitle());

        SurveyDto survey = surveyService.createSurvey(createDto, request);

        ApiResponse<SurveyDto> response = ApiResponse.success(survey, "Survey created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get survey by ID", description = "Get detailed survey information including questions")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SurveyDto>> getSurveyById(
            @Parameter(description = "Survey ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get survey request: {}", id);

        SurveyDto survey = surveyService.getSurveyById(id, request);

        ApiResponse<SurveyDto> response = ApiResponse.success(survey, "Survey retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }



    @GetMapping("/")
    @Operation(summary = "Get survey by ID", description = "Get detailed survey information including questions")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<SurveyDto>>> getAllSurveyList(
            HttpServletRequest request) {
        List<SurveyDto> surveyList = surveyService.getAllSurveys();
        ApiResponse<List<SurveyDto>> response = ApiResponse.success(surveyList, "Survey retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update survey", description = "Update existing survey details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid survey data or survey has responses"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SurveyDto>> updateSurvey(
            @Parameter(description = "Survey ID") @PathVariable Long id,
            @Valid @RequestBody SurveyCreateDto updateDto,
            HttpServletRequest request) {

        log.info("Update survey request: {}", id);

        SurveyDto survey = surveyService.updateSurvey(id, updateDto, request);

        ApiResponse<SurveyDto> response = ApiResponse.success(survey, "Survey updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete survey", description = "Delete survey (must have no responses)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Survey has existing responses"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSurvey(
            @Parameter(description = "Survey ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete survey request: {}", id);

        surveyService.deleteSurvey(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Survey deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @Operation(summary = "Search surveys", description = "Search surveys with various filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<SurveyDto>>> searchSurveys(
            @Valid @RequestBody SurveySearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search surveys request");

        Page<SurveyDto> surveys = surveyService.searchSurveys(searchDto, request);

        ApiResponse<Page<SurveyDto>> response = ApiResponse.success(surveys, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SURVEY QUESTION OPERATIONS ================================

    @PostMapping("/{surveyId}/questions")
    @Operation(summary = "Add question to survey", description = "Add a new question to existing survey")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Question added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid question data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SurveyQuestionDto>> addQuestionToSurvey(
            @Parameter(description = "Survey ID") @PathVariable Long surveyId,
            @Valid @RequestBody SurveyQuestionCreateDto createDto,
            HttpServletRequest request) {

        log.info("Add question to survey: {}", surveyId);

        SurveyQuestionDto question = surveyService.addQuestionToSurvey(surveyId, createDto, request);

        ApiResponse<SurveyQuestionDto> response = ApiResponse.success(question, "Question added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "Delete survey question", description = "Remove question from survey (must have no responses)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Question not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Question has existing responses"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "Question ID") @PathVariable Long questionId,
            HttpServletRequest request) {

        log.info("Delete survey question: {}", questionId);

        surveyService.deleteQuestion(questionId, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Question deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SURVEY RESPONSE OPERATIONS ================================

    @PostMapping("/responses")
    @Operation(summary = "Create survey response", description = "Start a new survey response (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Survey response created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid response data or response already exists")
    })
    public ResponseEntity<ApiResponse<SurveyResponseDto>> createSurveyResponse(
            @Valid @RequestBody SurveyResponseCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create survey response for survey: {}", createDto.getSurveyId());

        SurveyResponseDto response = surveyService.createSurveyResponse(createDto);

        ApiResponse<SurveyResponseDto> apiResponse = ApiResponse.success(response, "Survey response created successfully");
        apiResponse.setPath(request.getRequestURI());
        apiResponse.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/responses/{responseToken}/submit")
    @Operation(summary = "Submit survey response", description = "Submit completed survey response (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey response submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey response not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Response already submitted or not complete")
    })
    public ResponseEntity<ApiResponse<SurveyResponseDto>> submitSurveyResponse(
            @Parameter(description = "Response token") @PathVariable String responseToken,
            HttpServletRequest request) {

        log.info("Submit survey response: {}", responseToken);

        SurveyResponseDto response = surveyService.submitSurveyResponse(responseToken, request);

        ApiResponse<SurveyResponseDto> apiResponse = ApiResponse.success(response, "Survey response submitted successfully");
        apiResponse.setPath(request.getRequestURI());
        apiResponse.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }





    @PostMapping("/user/assignment")
    @Operation(summary = "Submit survey response", description = "Submit completed survey response (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey response submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey response not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Response already submitted or not complete")
    })
    public ResponseEntity<ApiResponse<SurveyResponseDto>> assignmentSurveyToUser(
            @Valid @RequestBody SurveyAssignmentDto assignmentDto,
            HttpServletRequest request) {
        SurveyResponseDto response = surveyService.assignmentSurveyToUser(assignmentDto);
        ApiResponse<SurveyResponseDto> apiResponse = ApiResponse.success(response, "Survey response submitted successfully");
        apiResponse.setPath(request.getRequestURI());
        apiResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }




    @GetMapping("/user/assignment/{attendId}")
    @Operation(summary = "Submit survey response", description = "Submit completed survey response (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey response submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey response not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Response already submitted or not complete")
    })
    public ResponseEntity<ApiResponse<List<SurveyResponseDto>>> assignmentSurveyToUser(
            @Parameter(description = "User Id") @PathVariable Long attendId,
            HttpServletRequest request) {
        List<SurveyResponseDto> response = surveyService.getSurveyToUser(attendId);
        ApiResponse<List<SurveyResponseDto>> apiResponse = ApiResponse.success(response, "Survey response submitted successfully");
        apiResponse.setPath(request.getRequestURI());
        apiResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }


    @PutMapping("/user/assignment/{surveyResponseId}")
    @Operation(summary = "Update survey response", description = "Update existing survey response details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey response updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey response not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid survey data or survey has responses"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<String>> saveSurveyResult(
            @Parameter(description = "Survey Response ID") @PathVariable Long surveyResponseId,
            @RequestBody SurveyResponseDto surveyResponseData,
            HttpServletRequest request) {

        log.info("Update survey response request: {}", surveyResponseId);

        String result = surveyService.updateServerResponse(surveyResponseId, surveyResponseData);

        ApiResponse<String> response = ApiResponse.success(result, "Survey updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }




    @GetMapping("/school/assignment/{schoolId}")
    @Operation(summary = "Submit survey response", description = "Submit completed survey response (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey response submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey response not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Response already submitted or not complete")
    })
    public ResponseEntity<ApiResponse<List<SurveyResponseDto>>> assignmentSurveyToSchool(
            @Parameter(description = "schoolId Id") @PathVariable Long schoolId,
            HttpServletRequest request) {
        List<SurveyResponseDto> response = surveyService.getSurveyToSchool(schoolId);
        ApiResponse<List<SurveyResponseDto>> apiResponse = ApiResponse.success(response, "Survey response submitted successfully");
        apiResponse.setPath(request.getRequestURI());
        apiResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/responses/{responseToken}")
    @Operation(summary = "Get survey response by token", description = "Get survey response details by token (public endpoint)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey response retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey response not found")
    })
    public ResponseEntity<ApiResponse<SurveyResponseDto>> getSurveyResponseByToken(
            @Parameter(description = "Response token") @PathVariable String responseToken,
            HttpServletRequest request) {

        log.debug("Get survey response by token: {}", responseToken);

        SurveyResponseDto response = surveyService.getSurveyResponseByToken(responseToken);

        ApiResponse<SurveyResponseDto> apiResponse = ApiResponse.success(response, "Survey response retrieved successfully");
        apiResponse.setPath(request.getRequestURI());
        apiResponse.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{surveyId}/responses")
    @Operation(summary = "Get survey responses", description = "Get all responses for a survey with pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey responses retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<SurveyResponseDto>>> getSurveyResponses(
            @Parameter(description = "Survey ID") @PathVariable Long surveyId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request) {

        log.debug("Get responses for survey: {}", surveyId);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

        Page<SurveyResponseDto> responses = surveyService.getSurveyResponses(surveyId, pageable, request);

        ApiResponse<Page<SurveyResponseDto>> response = ApiResponse.success(responses,
                "Survey responses retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ANALYTICS OPERATIONS ================================

    @GetMapping("/{surveyId}/analytics")
    @Operation(summary = "Get survey analytics", description = "Get comprehensive analytics for a survey")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SurveyAnalyticsDto>> getSurveyAnalytics(
            @Parameter(description = "Survey ID") @PathVariable Long surveyId,
            HttpServletRequest request) {

        log.debug("Get analytics for survey: {}", surveyId);

        SurveyAnalyticsDto analytics = surveyService.getSurveyAnalytics(surveyId, request);

        ApiResponse<SurveyAnalyticsDto> response = ApiResponse.success(analytics, "Analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/satisfaction-trends/{schoolId}")
    @Operation(summary = "Get satisfaction trends", description = "Get satisfaction trends for a school over time")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Satisfaction trends retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<SatisfactionTrendDto>>> getSatisfactionTrends(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "From date") @RequestParam LocalDateTime fromDate,
            @Parameter(description = "To date") @RequestParam LocalDateTime toDate,
            HttpServletRequest request) {

        log.debug("Get satisfaction trends for school: {} from {} to {}", schoolId, fromDate, toDate);

        List<SatisfactionTrendDto> trends = surveyService.getSatisfactionTrends(schoolId, fromDate, toDate, request);

        ApiResponse<List<SatisfactionTrendDto>> response = ApiResponse.success(trends,
                "Satisfaction trends retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/analytics/school-performance-comparison")
    @Operation(summary = "Compare school performance", description = "Compare survey performance across multiple schools")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Performance comparison completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid school IDs"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<SchoolSurveyPerformanceDto>>> getSchoolPerformanceComparison(
            @Valid @RequestBody SchoolComparisonRequest comparisonRequest,
            HttpServletRequest request) {

        log.info("Get school performance comparison for {} schools", comparisonRequest.getSchoolIds().size());

        List<SchoolSurveyPerformanceDto> performance = surveyService.getSchoolPerformanceComparison(
                comparisonRequest.getSchoolIds(), request);

        ApiResponse<List<SchoolSurveyPerformanceDto>> response = ApiResponse.success(performance,
                "Performance comparison completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/bulk-operations")
    @Operation(summary = "Bulk survey operations", description = "Perform bulk operations on multiple surveys")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk operation completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "207", description = "Bulk operation completed with some errors"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid bulk operation data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BulkSurveyResultDto>> bulkSurveyOperation(
            @Valid @RequestBody BulkSurveyOperationDto bulkDto,
            HttpServletRequest request) {

        log.info("Bulk survey operation: {} for {} surveys", bulkDto.getOperation(), bulkDto.getSurveyIds().size());

        BulkSurveyResultDto result = surveyService.bulkSurveyOperation(bulkDto, request);

        ApiResponse<BulkSurveyResultDto> response = ApiResponse.success(result, "Bulk operation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        HttpStatus status = result.getFailedOperations() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    // ================================ REPORT GENERATION ================================

    @PostMapping("/{surveyId}/reports")
    @Operation(summary = "Generate survey report", description = "Generate comprehensive survey report")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid report criteria"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SurveyReportDto>> generateSurveyReport(
            @Parameter(description = "Survey ID") @PathVariable Long surveyId,
            @Valid @RequestBody SurveyReportRequest reportRequest,
            HttpServletRequest request) {

        log.info("Generate survey report for survey: {}, type: {}", surveyId, reportRequest.getReportType());

        SurveyReportDto report = surveyService.generateSurveyReport(
                surveyId,
                reportRequest.getReportType(),
                reportRequest.getPeriodStart(),
                reportRequest.getPeriodEnd(),
                request);

        ApiResponse<SurveyReportDto> response = ApiResponse.success(report, "Report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PUBLIC SURVEY ACCESS ================================

    @GetMapping("/public/{surveyId}")
    @Operation(summary = "Get public survey", description = "Get survey for public access (no authentication required)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public survey retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Survey not found or not available for public access")
    })
    public ResponseEntity<ApiResponse<SurveyDto>> getPublicSurvey(
            @Parameter(description = "Survey ID") @PathVariable Long surveyId,
            HttpServletRequest request) {

        log.debug("Get public survey: {}", surveyId);

        SurveyDto survey = surveyService.getPublicSurvey(surveyId);

        ApiResponse<SurveyDto> response = ApiResponse.success(survey, "Public survey retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/by-token/{responseToken}")
    @Operation(summary = "Get survey by response token", description = "Get survey using response token (no authentication required)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Response token not found")
    })
    public ResponseEntity<ApiResponse<SurveyDto>> getPublicSurveyByToken(
            @Parameter(description = "Response token") @PathVariable String responseToken,
            HttpServletRequest request) {

        log.debug("Get survey by token: {}", responseToken);

        SurveyDto survey = surveyService.getPublicSurveyByToken(responseToken);

        ApiResponse<SurveyDto> response = ApiResponse.success(survey, "Survey retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PUBLIC RATING OPERATIONS ================================

    @GetMapping("/public/schools/{schoolId}/ratings")
    @Operation(summary = "Get public school ratings", description = "Get public ratings for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School ratings retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or not available")
    })
    public ResponseEntity<ApiResponse<List<SurveyRatingDto>>> getPublicSchoolRatings(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Rating category") @RequestParam(required = false) RatingCategory category,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Get public ratings for school: {}, category: {}", schoolId, category);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<SurveyRatingDto> ratings = surveyService.getPublicSchoolRatings(schoolId, category, pageable);

        ApiResponse<List<SurveyRatingDto>> response = ApiResponse.success(ratings,
                "School ratings retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/schools/{schoolId}/average-ratings")
    @Operation(summary = "Get school average ratings", description = "Get average ratings by category for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Average ratings retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or not available")
    })
    public ResponseEntity<ApiResponse<Map<RatingCategory, Double>>> getPublicSchoolAverageRatings(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get average ratings for school: {}", schoolId);

        Map<RatingCategory, Double> averageRatings = surveyService.getPublicSchoolAverageRatings(schoolId);

        ApiResponse<Map<RatingCategory, Double>> response = ApiResponse.success(averageRatings,
                "Average ratings retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ MODERATION OPERATIONS ================================

    @PostMapping("/ratings/{ratingId}/moderate")
    @Operation(summary = "Moderate rating", description = "Moderate a rating for public visibility")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating moderated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rating not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> moderateRating(
            @Parameter(description = "Rating ID") @PathVariable Long ratingId,
            @Valid @RequestBody ModerationRequest moderationRequest,
            HttpServletRequest request) {

        log.info("Moderate rating: {}", ratingId);

        surveyService.moderateRating(ratingId, moderationRequest.getIsPublic(),
                moderationRequest.getModeratorNotes(), request);

        ApiResponse<Void> response = ApiResponse.success(null, "Rating moderated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ratings/{ratingId}/flag")
    @Operation(summary = "Flag rating", description = "Flag a rating for review")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating flagged successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rating not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> flagRating(
            @Parameter(description = "Rating ID") @PathVariable Long ratingId,
            @Valid @RequestBody FlagRequest flagRequest,
            HttpServletRequest request) {

        log.info("Flag rating: {} with reason: {}", ratingId, flagRequest.getFlagReason());

        surveyService.flagRating(ratingId, flagRequest.getFlagReason(), request);

        ApiResponse<Void> response = ApiResponse.success(null, "Rating flagged successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ REQUEST DTOs ================================

    public static class SchoolComparisonRequest {
        private List<Long> schoolIds;

        public List<Long> getSchoolIds() { return schoolIds; }
        public void setSchoolIds(List<Long> schoolIds) { this.schoolIds = schoolIds; }
    }

    public static class SurveyReportRequest {
        private String reportType;
        private LocalDate periodStart;
        private LocalDate periodEnd;

        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }

        public LocalDate getPeriodStart() { return periodStart; }
        public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

        public LocalDate getPeriodEnd() { return periodEnd; }
        public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    }

    public static class ModerationRequest {
        private Boolean isPublic;
        private String moderatorNotes;

        public Boolean getIsPublic() { return isPublic; }
        public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

        public String getModeratorNotes() { return moderatorNotes; }
        public void setModeratorNotes(String moderatorNotes) { this.moderatorNotes = moderatorNotes; }
    }

    public static class FlagRequest {
        private String flagReason;

        public String getFlagReason() { return flagReason; }
        public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    }
}