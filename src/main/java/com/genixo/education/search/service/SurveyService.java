package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.survey.*;
import com.genixo.education.search.entity.survey.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.appointment.Appointment;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.appointment.AppointmentRepository;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.survey.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.SurveyConverterService;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyQuestionResponseRepository surveyQuestionResponseRepository;
    private final SurveyRatingRepository surveyRatingRepository;
    private final SchoolRepository schoolRepository;
    private final AppointmentRepository appointmentRepository;
    private final SurveyConverterService converterService;
    private final UserService userService;
    private final JwtService jwtService;



    @Transactional
    @CacheEvict(value = {"surveys", "survey_analytics"}, allEntries = true)
    public SurveyDto createSurvey(SurveyCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new survey: {}", createDto.getTitle());

        User user = jwtService.getUser(request);
        validateUserCanManageSurveys(user);

        if (!StringUtils.hasText(createDto.getTitle())) {
            throw new BusinessException("Survey title is required");
        }

        Survey survey = new Survey();
        survey.setTitle(createDto.getTitle());
        survey.setDescription(createDto.getDescription());
        survey.setSurveyType(createDto.getSurveyType() != null ? createDto.getSurveyType() : SurveyType.GENERAL_FEEDBACK);
        survey.setTriggerEvent(createDto.getTriggerEvent());
        survey.setIsActive(createDto.getIsActive() != null ? createDto.getIsActive() : true);
        survey.setIsAnonymous(createDto.getIsAnonymous() != null ? createDto.getIsAnonymous() : false);
        survey.setIsMandatory(createDto.getIsMandatory() != null ? createDto.getIsMandatory() : false);
        survey.setShowResultsToPublic(createDto.getShowResultsToPublic() != null ? createDto.getShowResultsToPublic() : false);
        survey.setSendDelayHours(createDto.getSendDelayHours() != null ? createDto.getSendDelayHours() : 24);
        survey.setReminderDelayHours(createDto.getReminderDelayHours() != null ? createDto.getReminderDelayHours() : 72);
        survey.setMaxReminders(createDto.getMaxReminders() != null ? createDto.getMaxReminders() : 2);
        survey.setExpiresAfterDays(createDto.getExpiresAfterDays() != null ? createDto.getExpiresAfterDays() : 7);
        survey.setPrimaryColor(createDto.getPrimaryColor() != null ? createDto.getPrimaryColor() : "#007bff");
        survey.setLogoUrl(createDto.getLogoUrl());
        survey.setHeaderImageUrl(createDto.getHeaderImageUrl());
        survey.setCustomCss(createDto.getCustomCss());
        survey.setWelcomeMessage(createDto.getWelcomeMessage());
        survey.setThankYouMessage(createDto.getThankYouMessage());
        survey.setCompletionRedirectUrl(createDto.getCompletionRedirectUrl());
        survey.setEmailSubject(createDto.getEmailSubject());
        survey.setEmailBody(createDto.getEmailBody());
        survey.setEmailTemplateId(createDto.getEmailTemplateId());
        survey.setCreatedBy(user.getId());

        survey = surveyRepository.save(survey);

        // Create questions if provided
        if (createDto.getQuestions() != null && !createDto.getQuestions().isEmpty()) {
            createSurveyQuestions(survey, createDto.getQuestions(), user.getId());
        }

        log.info("Survey created successfully with ID: {}", survey.getId());
        return converterService.mapToDto(survey);
    }

    @Cacheable(value = "surveys", key = "#id")
    public SurveyDto getSurveyById(Long id, HttpServletRequest request) {
        log.info("Fetching survey with ID: {}", id);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + id));

        validateUserCanAccessSurvey(user, survey);

        return converterService.mapToDto(survey);
    }

    @Transactional
    @CacheEvict(value = {"surveys", "survey_analytics"}, allEntries = true)
    public SurveyDto updateSurvey(Long id, SurveyCreateDto updateDto, HttpServletRequest request) {
        log.info("Updating survey with ID: {}", id);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + id));

        validateUserCanManageSurvey(user, survey);

        // Check if survey has responses
        if (surveyResponseRepository.countBySurveyIdAndIsActiveTrue(id) > 0 &&
                !updateDto.getTitle().equals(survey.getTitle())) {
            throw new BusinessException("Cannot change survey title after receiving responses");
        }

        survey.setTitle(updateDto.getTitle());
        survey.setDescription(updateDto.getDescription());
        survey.setSurveyType(updateDto.getSurveyType());
        survey.setTriggerEvent(updateDto.getTriggerEvent());
        survey.setIsActive(updateDto.getIsActive());
        survey.setIsAnonymous(updateDto.getIsAnonymous());
        survey.setIsMandatory(updateDto.getIsMandatory());
        survey.setShowResultsToPublic(updateDto.getShowResultsToPublic());
        survey.setSendDelayHours(updateDto.getSendDelayHours());
        survey.setReminderDelayHours(updateDto.getReminderDelayHours());
        survey.setMaxReminders(updateDto.getMaxReminders());
        survey.setExpiresAfterDays(updateDto.getExpiresAfterDays());
        survey.setPrimaryColor(updateDto.getPrimaryColor());
        survey.setLogoUrl(updateDto.getLogoUrl());
        survey.setHeaderImageUrl(updateDto.getHeaderImageUrl());
        survey.setCustomCss(updateDto.getCustomCss());
        survey.setWelcomeMessage(updateDto.getWelcomeMessage());
        survey.setThankYouMessage(updateDto.getThankYouMessage());
        survey.setCompletionRedirectUrl(updateDto.getCompletionRedirectUrl());
        survey.setEmailSubject(updateDto.getEmailSubject());
        survey.setEmailBody(updateDto.getEmailBody());
        survey.setEmailTemplateId(updateDto.getEmailTemplateId());
        survey.setUpdatedBy(user.getId());

        survey = surveyRepository.save(survey);
        log.info("Survey updated successfully with ID: {}", survey.getId());

        return converterService.mapToDto(survey);
    }

    @Transactional
    @CacheEvict(value = {"surveys", "survey_analytics"}, allEntries = true)
    public void deleteSurvey(Long id, HttpServletRequest request) {
        log.info("Deleting survey with ID: {}", id);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + id));

        validateUserCanManageSurvey(user, survey);

        // Check if survey has responses
        long responseCount = surveyResponseRepository.countBySurveyIdAndIsActiveTrue(id);
        if (responseCount > 0) {
            throw new BusinessException("Cannot delete survey with existing responses. Found " + responseCount + " responses.");
        }

        survey.setIsActive(false);
        survey.setUpdatedBy(user.getId());
        surveyRepository.save(survey);

        log.info("Survey deleted successfully with ID: {}", id);
    }

    public Page<SurveyDto> searchSurveys(SurveySearchDto searchDto, HttpServletRequest request) {
        log.info("Searching surveys with criteria: {}", searchDto.getSearchTerm());

        User user = jwtService.getUser(request);

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createSurveySort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        Page<Survey> surveys = surveyRepository.searchSurveys(
                searchDto.getSearchTerm(),
                searchDto.getSurveyTypes(),
                searchDto.getTriggerEvents(),
                searchDto.getIsActive(),
                searchDto.getIsAnonymous(),
                searchDto.getIsMandatory(),
                searchDto.getShowResultsToPublic(),
                searchDto.getCreatedFrom(),
                searchDto.getCreatedTo(),
                searchDto.getMinResponses(),
                searchDto.getMaxResponses(),
                searchDto.getMinCompletionRate(),
                searchDto.getMaxCompletionRate(),
                searchDto.getMinAverageRating(),
                searchDto.getMaxAverageRating(),
                getUserAccessibleSchoolIds(user),
                pageable
        );

        return surveys.map(converterService::mapToDto);
    }

    // ================================ SURVEY QUESTION OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"surveys"}, key = "#surveyId")
    public SurveyQuestionDto addQuestionToSurvey(Long surveyId, SurveyQuestionCreateDto createDto, HttpServletRequest request) {
        log.info("Adding question to survey: {}", surveyId);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + surveyId));

        validateUserCanManageSurvey(user, survey);

        if (!StringUtils.hasText(createDto.getQuestionText())) {
            throw new BusinessException("Question text is required");
        }

        SurveyQuestion question = new SurveyQuestion();
        question.setSurvey(survey);
        question.setQuestionText(createDto.getQuestionText());
        question.setDescription(createDto.getDescription());
        question.setQuestionType(createDto.getQuestionType() != null ? createDto.getQuestionType() : QuestionType.TEXT_SHORT);
        question.setRatingCategory(createDto.getRatingCategory());
        question.setIsRequired(createDto.getIsRequired() != null ? createDto.getIsRequired() : false);
        question.setSortOrder(createDto.getSortOrder() != null ? createDto.getSortOrder() : getNextQuestionSortOrder(surveyId));
        question.setIsActive(true);
        question.setOptions(createDto.getOptions());
        question.setAllowOtherOption(createDto.getAllowOtherOption() != null ? createDto.getAllowOtherOption() : false);
        question.setOtherOptionLabel(createDto.getOtherOptionLabel());
        question.setRatingScaleMin(createDto.getRatingScaleMin() != null ? createDto.getRatingScaleMin() : 1);
        question.setRatingScaleMax(createDto.getRatingScaleMax() != null ? createDto.getRatingScaleMax() : 5);
        question.setRatingScaleStep(createDto.getRatingScaleStep() != null ? createDto.getRatingScaleStep() : 1);
        question.setRatingLabels(createDto.getRatingLabels());
        question.setTextMinLength(createDto.getTextMinLength());
        question.setTextMaxLength(createDto.getTextMaxLength());
        question.setPlaceholderText(createDto.getPlaceholderText());
        question.setShowIfQuestionId(createDto.getShowIfQuestionId());
        question.setShowIfAnswer(createDto.getShowIfAnswer());
        question.setShowIfCondition(createDto.getShowIfCondition());
        question.setCustomCssClass(createDto.getCustomCssClass());
        question.setHelpText(createDto.getHelpText());
        question.setImageUrl(createDto.getImageUrl());
        question.setCreatedBy(user.getId());

        question = surveyQuestionRepository.save(question);
        log.info("Question added to survey with ID: {}", question.getId());

        return converterService.mapQuestionToDto(question);
    }

    @Transactional
    @CacheEvict(value = {"surveys"}, allEntries = true)
    public void deleteQuestion(Long questionId, HttpServletRequest request) {
        log.info("Deleting survey question with ID: {}", questionId);

        User user = jwtService.getUser(request);
        SurveyQuestion question = surveyQuestionRepository.findByIdAndIsActiveTrue(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey question not found with ID: " + questionId));

        validateUserCanManageSurvey(user, question.getSurvey());

        // Check if question has responses
        long responseCount = surveyQuestionResponseRepository.countByQuestionIdAndIsActiveTrue(questionId);
        if (responseCount > 0) {
            throw new BusinessException("Cannot delete question with existing responses. Found " + responseCount + " responses.");
        }

        question.setIsActive(false);
        question.setUpdatedBy(user.getId());
        surveyQuestionRepository.save(question);

        log.info("Survey question deleted successfully with ID: {}", questionId);
    }

    // ================================ SURVEY RESPONSE OPERATIONS ================================

    @Transactional
    public SurveyResponseDto createSurveyResponse(SurveyResponseCreateDto createDto) {
        log.info("Creating survey response for survey: {}", createDto.getSurveyId());

        Survey survey = surveyRepository.findByIdAndIsActiveTrue(createDto.getSurveyId())
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + createDto.getSurveyId()));

        School school = null;
        if (createDto.getSchoolId() != null) {
            school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + createDto.getSchoolId()));
        }

        Appointment appointment = null;
        if (createDto.getAppointmentId() != null) {
            appointment = appointmentRepository.findByIdAndIsActiveTrue(createDto.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + createDto.getAppointmentId()));
        }

        // Check if response already exists for this appointment/user
        if (appointment != null && surveyResponseRepository.existsByAppointmentIdAndIsActiveTrue(appointment.getId())) {
            throw new BusinessException("Survey response already exists for this appointment");
        }

        SurveyResponse response = new SurveyResponse();
        response.setSurvey(survey);
        response.setSchool(school);
        response.setAppointment(appointment);
        response.setResponseToken(UUID.randomUUID().toString());
        response.setStatus(ResponseStatus.STARTED);
        response.setStartedAt(LocalDateTime.now());
        response.setRespondentName(createDto.getRespondentName());
        response.setRespondentEmail(createDto.getRespondentEmail());
        response.setRespondentPhone(createDto.getRespondentPhone());
        response.setIpAddress(createDto.getIpAddress());
        response.setUserAgent(createDto.getUserAgent());
        response.setBrowserInfo(createDto.getBrowserInfo());
        response.setDeviceInfo(createDto.getDeviceInfo());
        response.setGeneralFeedback(createDto.getGeneralFeedback());
        response.setSuggestions(createDto.getSuggestions());
        response.setComplaints(createDto.getComplaints());
        response.setWouldRecommend(createDto.getWouldRecommend());
        response.setLikelihoodToEnroll(createDto.getLikelihoodToEnroll());

        if (createDto.getRespondentUserId() != null) {
            // Set user if provided - this would need User repository
            response.setRespondentUser(null); // Would set actual user here
        }

        response = surveyResponseRepository.save(response);

        // Create question responses
        if (createDto.getQuestionResponses() != null && !createDto.getQuestionResponses().isEmpty()) {
            createQuestionResponses(response, createDto.getQuestionResponses());

            // Mark as completed if all required questions are answered
            if (isResponseComplete(response)) {
                response.setStatus(ResponseStatus.COMPLETED);
                response.setCompletedAt(LocalDateTime.now());
                response.setCompletionTimeSeconds(calculateCompletionTime(response.getStartedAt(), response.getCompletedAt()));
                response = surveyResponseRepository.save(response);

                // Create ratings based on responses
                createSurveyRatings(response);
            }
        }

        log.info("Survey response created with ID: {}", response.getId());
        return converterService.mapResponseToDto(response);
    }

    @Transactional
    public SurveyResponseDto submitSurveyResponse(String responseToken, HttpServletRequest request) {
        log.info("Submitting survey response with token: {}", responseToken);

        SurveyResponse response = surveyResponseRepository.findByResponseTokenAndIsActiveTrue(responseToken)
                .orElseThrow(() -> new ResourceNotFoundException("Survey response not found with token: " + responseToken));

        if (response.getStatus() == ResponseStatus.SUBMITTED) {
            throw new BusinessException("Survey response already submitted");
        }

        if (response.getStatus() != ResponseStatus.COMPLETED) {
            throw new BusinessException("Survey response must be completed before submission");
        }

        response.setStatus(ResponseStatus.SUBMITTED);
        response.setSubmittedAt(LocalDateTime.now());
        response = surveyResponseRepository.save(response);

        // Update survey statistics
        updateSurveyStatistics(response.getSurvey());

        log.info("Survey response submitted successfully with ID: {}", response.getId());
        return converterService.mapResponseToDto(response);
    }

    public SurveyResponseDto getSurveyResponseByToken(String responseToken) {
        log.info("Fetching survey response with token: {}", responseToken);

        SurveyResponse response = surveyResponseRepository.findByResponseTokenAndIsActiveTrue(responseToken)
                .orElseThrow(() -> new ResourceNotFoundException("Survey response not found with token: " + responseToken));

        return converterService.mapResponseToDto(response);
    }

    public Page<SurveyResponseDto> getSurveyResponses(Long surveyId, Pageable pageable, HttpServletRequest request) {
        log.info("Fetching responses for survey: {}", surveyId);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + surveyId));

        validateUserCanAccessSurvey(user, survey);

        Page<SurveyResponse> responses = surveyResponseRepository.findBySurveyIdAndIsActiveTrueOrderByCreatedAtDesc(surveyId, pageable);
        return responses.map(converterService::mapResponseToDto);
    }

    // ================================ SURVEY ANALYTICS ================================

    @Cacheable(value = "survey_analytics", key = "#surveyId")
    public SurveyAnalyticsDto getSurveyAnalytics(Long surveyId, HttpServletRequest request) {
        log.info("Fetching analytics for survey: {}", surveyId);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + surveyId));

        validateUserCanAccessSurvey(user, survey);

        return surveyRepository.getSurveyAnalytics(surveyId);
    }

    public List<SatisfactionTrendDto> getSatisfactionTrends(Long schoolId, LocalDateTime fromDate, LocalDateTime toDate, HttpServletRequest request) {
        log.info("Fetching satisfaction trends for school: {} from {} to {}", schoolId, fromDate, toDate);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        return surveyRatingRepository.getSatisfactionTrends(schoolId, fromDate, toDate);
    }

    public List<SchoolSurveyPerformanceDto> getSchoolPerformanceComparison(List<Long> schoolIds, HttpServletRequest request) {
        log.info("Fetching school performance comparison for {} schools", schoolIds.size());

        User user = jwtService.getUser(request);

        // Validate access to all schools
        for (Long schoolId : schoolIds) {
            validateUserCanAccessSchool(user, schoolId);
        }
// ceyhun  return surveyResponseRepository.getSchoolPerformanceComparison(schoolIds);
        return null;
    }

    // ================================ BULK OPERATIONS ================================

    @Transactional
    public BulkSurveyResultDto bulkSurveyOperation(BulkSurveyOperationDto bulkDto, HttpServletRequest request) {
        log.info("Performing bulk survey operation: {}", bulkDto.getOperation());

        User user = jwtService.getUser(request);

        BulkSurveyResultDto result = BulkSurveyResultDto.builder()
                .operationDate(LocalDateTime.now())
                .operationId(UUID.randomUUID().toString())
                .totalRecords(bulkDto.getSurveyIds().size())
                .successfulOperations(0)
                .failedOperations(0)
                .errors(new java.util.ArrayList<>())
                .warnings(new java.util.ArrayList<>())
                .affectedSurveyIds(new java.util.ArrayList<>())
                .build();

        for (Long surveyId : bulkDto.getSurveyIds()) {
            try {
                Survey survey = surveyRepository.findByIdAndIsActiveTrue(surveyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + surveyId));

                validateUserCanManageSurvey(user, survey);

                switch (bulkDto.getOperation().toUpperCase()) {
                    case "SEND_INVITATIONS":
                        int sentCount = sendSurveyInvitations(survey, bulkDto.getRecipientEmails(), bulkDto.getCustomMessage());
                        result.setInvitationsSent(result.getInvitationsSent() != null ? result.getInvitationsSent() + sentCount : sentCount);
                        break;
                    case "SEND_REMINDERS":
                        sendSurveyReminders(survey, bulkDto.getCustomMessage());
                        break;
                    case "CLOSE_SURVEY":
                        survey.setIsActive(false);
                        survey.setUpdatedBy(user.getId());
                        surveyRepository.save(survey);
                        break;
                    case "EXPORT_RESPONSES":
                        String exportUrl = exportSurveyResponses(survey, bulkDto.getExportFormat(), bulkDto.getIncludePersonalData());
                        result.setDownloadUrl(exportUrl);
                        break;
                    default:
                        throw new BusinessException("Unsupported bulk operation: " + bulkDto.getOperation());
                }

                result.getAffectedSurveyIds().add(surveyId);
                result.setSuccessfulOperations(result.getSuccessfulOperations() + 1);

            } catch (Exception e) {
                result.setFailedOperations(result.getFailedOperations() + 1);
                result.getErrors().add("Survey ID " + surveyId + ": " + e.getMessage());
            }
        }

        result.setSuccess(result.getFailedOperations() == 0);
        return result;
    }

    // ================================ HELPER METHODS ================================

    private void createSurveyQuestions(Survey survey, List<SurveyQuestionCreateDto> questionDtos, Long userId) {
        for (int i = 0; i < questionDtos.size(); i++) {
            SurveyQuestionCreateDto dto = questionDtos.get(i);

            SurveyQuestion question = new SurveyQuestion();
            question.setSurvey(survey);
            question.setQuestionText(dto.getQuestionText());
            question.setDescription(dto.getDescription());
            question.setQuestionType(dto.getQuestionType() != null ? dto.getQuestionType() : QuestionType.TEXT_SHORT);
            question.setRatingCategory(dto.getRatingCategory());
            question.setIsRequired(dto.getIsRequired() != null ? dto.getIsRequired() : false);
            question.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : i + 1);
            question.setIsActive(true);
            question.setOptions(dto.getOptions());
            question.setAllowOtherOption(dto.getAllowOtherOption() != null ? dto.getAllowOtherOption() : false);
            question.setOtherOptionLabel(dto.getOtherOptionLabel());
            question.setRatingScaleMin(dto.getRatingScaleMin() != null ? dto.getRatingScaleMin() : 1);
            question.setRatingScaleMax(dto.getRatingScaleMax() != null ? dto.getRatingScaleMax() : 5);
            question.setRatingScaleStep(dto.getRatingScaleStep() != null ? dto.getRatingScaleStep() : 1);
            question.setRatingLabels(dto.getRatingLabels());
            question.setTextMinLength(dto.getTextMinLength());
            question.setTextMaxLength(dto.getTextMaxLength());
            question.setPlaceholderText(dto.getPlaceholderText());
            question.setShowIfQuestionId(dto.getShowIfQuestionId());
            question.setShowIfAnswer(dto.getShowIfAnswer());
            question.setShowIfCondition(dto.getShowIfCondition());
            question.setCustomCssClass(dto.getCustomCssClass());
            question.setHelpText(dto.getHelpText());
            question.setImageUrl(dto.getImageUrl());
            question.setCreatedBy(userId);

            surveyQuestionRepository.save(question);
        }
    }

    private void createQuestionResponses(SurveyResponse response, List<SurveyQuestionResponseCreateDto> responseDtos) {
        for (SurveyQuestionResponseCreateDto dto : responseDtos) {
            SurveyQuestion question = surveyQuestionRepository.findByIdAndIsActiveTrue(dto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Survey question not found with ID: " + dto.getQuestionId()));

            SurveyQuestionResponse questionResponse = new SurveyQuestionResponse();
            questionResponse.setSurveyResponse(response);
            questionResponse.setQuestion(question);
            questionResponse.setTextResponse(dto.getTextResponse());
            questionResponse.setNumberResponse(dto.getNumberResponse());
            questionResponse.setDateResponse(dto.getDateResponse() != null ? dto.getDateResponse() : null);
            questionResponse.setTimeResponse(dto.getTimeResponse() != null ? dto.getTimeResponse() : null);
            questionResponse.setDatetimeResponse(dto.getDatetimeResponse() != null ? dto.getDatetimeResponse() : null);
            questionResponse.setBooleanResponse(dto.getBooleanResponse());
            questionResponse.setRatingResponse(dto.getRatingResponse());
            questionResponse.setChoiceResponses(dto.getChoiceResponses());
            questionResponse.setMatrixResponses(dto.getMatrixResponses());
            questionResponse.setFileUrl(dto.getFileUrl());
            questionResponse.setFileName(dto.getFileName());
            questionResponse.setFileSize(dto.getFileSize());
            questionResponse.setFileType(dto.getFileType());
            questionResponse.setOtherText(dto.getOtherText());
            questionResponse.setResponseTimeSeconds(dto.getResponseTimeSeconds());
            questionResponse.setWasSkipped(dto.getWasSkipped() != null ? dto.getWasSkipped() : false);
            questionResponse.setSkipReason(dto.getSkipReason());
            questionResponse.setConfidenceLevel(dto.getConfidenceLevel());
            questionResponse.setCreatedBy(response.getCreatedBy());

            surveyQuestionResponseRepository.save(questionResponse);
        }
    }

    private void createSurveyRatings(SurveyResponse response) {
        // Create ratings based on rating questions in the response
        List<SurveyQuestionResponse> ratingResponses = surveyQuestionResponseRepository
                .findByResponseIdAndQuestionTypeAndIsActiveTrue(response.getId(), QuestionType.RATING_STAR);

        for (SurveyQuestionResponse questionResponse : ratingResponses) {
            if (questionResponse.getRatingResponse() != null && questionResponse.getQuestion().getRatingCategory() != null) {
                SurveyRating rating = new SurveyRating();
                rating.setSchool(response.getSchool());
                rating.setSurveyResponse(response);
                rating.setRatingCategory(questionResponse.getQuestion().getRatingCategory());
                rating.setRatingValue(questionResponse.getRatingResponse());
                rating.setRatingText(questionResponse.getTextResponse());
                rating.setWeight(1.0);
                rating.setIsVerified(false);
                rating.setIsPublic(true);
                rating.setCreatedBy(response.getCreatedBy());

                surveyRatingRepository.save(rating);
            }
        }

        // Update overall ratings in response
        updateResponseRatings(response);
    }

    private void updateResponseRatings(SurveyResponse response) {
        Map<RatingCategory, Double> categoryRatings = surveyRatingRepository
                .getAverageRatingsByResponseAndCategory(response.getId());

        response.setOverallRating(categoryRatings.get(RatingCategory.OVERALL_SATISFACTION));
        response.setCleanlinessRating(categoryRatings.get(RatingCategory.CLEANLINESS));
        response.setStaffRating(categoryRatings.get(RatingCategory.STAFF_FRIENDLINESS));
        response.setFacilitiesRating(categoryRatings.get(RatingCategory.FACILITIES));
        response.setCommunicationRating(categoryRatings.get(RatingCategory.COMMUNICATION));

        surveyResponseRepository.save(response);
    }

    private boolean isResponseComplete(SurveyResponse response) {
        // Check if all required questions are answered
        List<SurveyQuestion> requiredQuestions = surveyQuestionRepository
                .findBySurveyIdAndIsRequiredTrueAndIsActiveTrue(response.getSurvey().getId());

        List<Long> answeredQuestionIds = surveyQuestionResponseRepository
                .findAnsweredQuestionIdsByResponseId(response.getId());

        return new HashSet<>(answeredQuestionIds).containsAll(
                requiredQuestions.stream().map(SurveyQuestion::getId).toList()
        );
    }

    private Integer calculateCompletionTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) return null;
        return (int) java.time.Duration.between(startTime, endTime).toSeconds();
    }

    private Integer getNextQuestionSortOrder(Long surveyId) {
        return surveyQuestionRepository.getMaxSortOrderBySurveyId(surveyId) + 1;
    }

    private void updateSurveyStatistics(Survey survey) {
        Long totalSent = surveyResponseRepository.countBySurveyIdAndIsActiveTrue(survey.getId());
        Long totalStarted = surveyResponseRepository.countBySurveyIdAndStatusInAndIsActiveTrue(
                survey.getId(),
                List.of(ResponseStatus.STARTED, ResponseStatus.IN_PROGRESS, ResponseStatus.COMPLETED, ResponseStatus.SUBMITTED)
        );
        Long totalCompleted = surveyResponseRepository.countBySurveyIdAndStatusInAndIsActiveTrue(
                survey.getId(),
                List.of(ResponseStatus.COMPLETED, ResponseStatus.SUBMITTED)
        );

        Double averageRating = surveyResponseRepository.getAverageOverallRatingBySurveyId(survey.getId());
        Integer averageCompletionTime = surveyResponseRepository.getAverageCompletionTimeBySurveyId(survey.getId());

        survey.setTotalSent(totalSent);
        survey.setTotalStarted(totalStarted);
        survey.setTotalCompleted(totalCompleted);
        survey.setAverageRating(averageRating);
        survey.setAverageCompletionTimeSeconds(averageCompletionTime);

        surveyRepository.save(survey);
    }

    private int sendSurveyInvitations(Survey survey, List<String> recipientEmails, String customMessage) {
        // This would integrate with email service
        // For now, just return the count
        log.info("Sending survey invitations to {} recipients for survey: {}", recipientEmails.size(), survey.getId());
        return recipientEmails.size();
    }

    private void sendSurveyReminders(Survey survey, String customMessage) {
        // Find incomplete responses that need reminders
        List<SurveyResponse> incompleteResponses = surveyResponseRepository
                .findIncompleteResponsesNeedingReminders(survey.getId(), survey.getMaxReminders());

        log.info("Sending reminders to {} incomplete responses for survey: {}", incompleteResponses.size(), survey.getId());

        // This would integrate with email/SMS service
        for (SurveyResponse response : incompleteResponses) {
            // Send reminder logic here
            response.setReminderCount(response.getReminderCount() + 1);
            response.setLastReminderSentAt(LocalDateTime.now());
            surveyResponseRepository.save(response);
        }
    }

    private String exportSurveyResponses(Survey survey, String format, Boolean includePersonalData) {
        // This would generate and return download URL for exported data
        log.info("Exporting survey responses for survey: {} in format: {}", survey.getId(), format);

        String filename = "survey_" + survey.getId() + "_responses." + format.toLowerCase();
        String downloadUrl = "/api/surveys/" + survey.getId() + "/export/" + filename;

        // Export logic would go here
        return downloadUrl;
    }

    private Sort createSurveySort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "created_date") {
            case "title" -> "title";
            case "response_count" -> "totalCompleted";
            case "completion_rate" -> "totalCompleted"; // Would need calculated field
            case "average_rating" -> "averageRating";
            default -> "createdAt";
        };

        return Sort.by(direction, sortField);
    }

    // ================================ VALIDATION METHODS ================================

    private void validateUserCanManageSurveys(User user) {
        if (!hasSystemRole(user) && !hasCampusOrSchoolAccess(user)) {
            throw new BusinessException("User does not have permission to manage surveys");
        }
    }

    private void validateUserCanAccessSurvey(User user, Survey survey) {
        if (!hasSystemRole(user) && !canAccessSurveySchools(user, survey)) {
            throw new BusinessException("User does not have access to this survey");
        }
    }

    private void validateUserCanManageSurvey(User user, Survey survey) {
        if (!hasSystemRole(user) && !canManageSurveySchools(user, survey)) {
            throw new BusinessException("User does not have manage permission for this survey");
        }
    }

    private void validateUserCanAccessSchool(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have access to this school");
        }
    }

    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);
    }

    private boolean hasCampusOrSchoolAccess(User user) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> (access.getAccessType() == AccessType.CAMPUS ||
                        access.getAccessType() == AccessType.SCHOOL) &&
                        (access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now())));
    }

    private boolean canAccessSurveySchools(User user, Survey survey) {
        // For now, allow access if user has any school access
        // This could be more specific based on survey scope
        return hasCampusOrSchoolAccess(user);
    }

    private boolean canManageSurveySchools(User user, Survey survey) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.CAMPUS ||
                        userRole.getRoleLevel() == RoleLevel.SCHOOL) &&
                canAccessSurveySchools(user, survey);
    }

    private boolean hasAccessToSchool(User user, Long schoolId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> {
                    if (access.getExpiresAt() != null && access.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    switch (access.getAccessType()) {
                        case SCHOOL:
                            return access.getEntityId().equals(schoolId);
                        case CAMPUS:
                            return schoolRepository.existsByIdAndCampusId(schoolId, access.getEntityId());
                        case BRAND:
                            return schoolRepository.existsByIdAndCampusBrandId(schoolId, access.getEntityId());
                        default:
                            return false;
                    }
                });
    }

    private List<Long> getUserAccessibleSchoolIds(User user) {
        if (hasSystemRole(user)) {
            return schoolRepository.findAllActiveSchoolIds();
        }

        return user.getInstitutionAccess().stream()
                .filter(access -> access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now()))
                .flatMap(access -> {
                    switch (access.getAccessType()) {
                        case SCHOOL:
                            return List.of(access.getEntityId()).stream();
                        case CAMPUS:
                            return schoolRepository.findIdsByCampusId(access.getEntityId()).stream();
                        case BRAND:
                            return schoolRepository.findIdsByBrandId(access.getEntityId()).stream();
                        default:
                            return List.<Long>of().stream();
                    }
                })
                .distinct()
                .collect(Collectors.toList());
    }

    // ================================ PUBLIC SURVEY METHODS (NO AUTH REQUIRED) ================================

    public SurveyDto getPublicSurvey(Long surveyId) {
        log.info("Public access to survey: {}", surveyId);

        Survey survey = surveyRepository.findByIdAndIsActiveTrueAndShowResultsToPublicTrue(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found or not available for public access"));

        return converterService.mapToDto(survey);
    }

    public SurveyDto getPublicSurveyByToken(String responseToken) {
        log.info("Getting survey by response token: {}", responseToken);

        SurveyResponse response = surveyResponseRepository.findByResponseTokenAndIsActiveTrue(responseToken)
                .orElseThrow(() -> new ResourceNotFoundException("Survey response not found with token: " + responseToken));

        return converterService.mapToDto(response.getSurvey());
    }

    public List<SurveyRatingDto> getPublicSchoolRatings(Long schoolId, RatingCategory category, Pageable pageable) {
        log.info("Getting public ratings for school: {} category: {}", schoolId, category);

        School school = schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available"));

        List<SurveyRating> ratings = surveyRatingRepository.findPublicRatingsBySchoolAndCategory(
                schoolId, category, pageable);

        return ratings.stream()
                .map(converterService::mapRatingToDto)
                .collect(Collectors.toList());
    }

    public Map<RatingCategory, Double> getPublicSchoolAverageRatings(Long schoolId) {
        log.info("Getting average ratings for school: {}", schoolId);

        School school = schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available"));

        return surveyRatingRepository.getAverageRatingsBySchoolAndCategory(schoolId);
    }

    // ================================ ADMIN METHODS ================================

    @Transactional
    public void moderateRating(Long ratingId, Boolean isPublic, String moderatorNotes, HttpServletRequest request) {
        log.info("Moderating rating: {}", ratingId);

        User user = jwtService.getUser(request);
        SurveyRating rating = surveyRatingRepository.findByIdAndIsActiveTrue(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));

        validateUserCanAccessSchool(user, rating.getSchool().getId());

        rating.setIsPublic(isPublic);
        rating.setModeratorNotes(moderatorNotes);
        rating.setUpdatedBy(user.getId());

        surveyRatingRepository.save(rating);
        log.info("Rating moderated successfully: {}", ratingId);
    }

    @Transactional
    public void flagRating(Long ratingId, String flagReason, HttpServletRequest request) {
        log.info("Flagging rating: {} with reason: {}", ratingId, flagReason);

        User user = jwtService.getUser(request);
        SurveyRating rating = surveyRatingRepository.findByIdAndIsActiveTrue(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));

        rating.setIsFlagged(true);
        rating.setFlagReason(flagReason);
        rating.setFlaggedBy(user.getId());
        rating.setFlaggedAt(LocalDateTime.now());

        surveyRatingRepository.save(rating);
        log.info("Rating flagged successfully: {}", ratingId);
    }

    public SurveyReportDto generateSurveyReport(Long surveyId, String reportType, LocalDate periodStart, LocalDate periodEnd, HttpServletRequest request) {
        log.info("Generating survey report for survey: {} type: {}", surveyId, reportType);

        User user = jwtService.getUser(request);
        Survey survey = surveyRepository.findByIdAndIsActiveTrue(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + surveyId));

        validateUserCanAccessSurvey(user, survey);

        SurveyReportDto report = SurveyReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .reportType(reportType)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .surveyId(surveyId)
                .surveyTitle(survey.getTitle())
                .analytics(getSurveyAnalytics(surveyId, request))
                .build();

        // Generate specific report content based on type
        switch (reportType.toUpperCase()) {
            case "SUMMARY":
                generateSummaryReport(report, survey);
                break;
            case "DETAILED":
                generateDetailedReport(report, survey, periodStart, periodEnd);
                break;
            case "COMPARISON":
                generateComparisonReport(report, survey);
                break;
            case "TREND_ANALYSIS":
                generateTrendAnalysisReport(report, survey, periodStart, periodEnd);
                break;
            default:
                throw new BusinessException("Unsupported report type: " + reportType);
        }

        return report;
    }

    private void generateSummaryReport(SurveyReportDto report, Survey survey) {
        // Generate summary insights
        List<String> insights = new java.util.ArrayList<>();
        insights.add("Survey completed by " + survey.getTotalCompleted() + " respondents");

        if (survey.getAverageRating() != null) {
            insights.add("Average overall rating: " + String.format("%.1f", survey.getAverageRating()) + "/5");
        }

        report.setKeyInsights(insights);
    }

    private void generateDetailedReport(SurveyReportDto report, Survey survey, LocalDate periodStart, LocalDate periodEnd) {
        // Get detailed responses for the period
        List<SurveyResponse> responses = surveyResponseRepository
                .findBySurveyIdAndPeriodAndIsActiveTrue(survey.getId(),
                        periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay());

        List<SurveyResponseDto> responseDtos = responses.stream()
                .map(converterService::mapResponseToDto)
                .collect(Collectors.toList());

        report.setResponses(responseDtos);
    }

    private void generateComparisonReport(SurveyReportDto report, Survey survey) {
        // Industry benchmarks would come from external data or configuration
        Map<String, Double> benchmarks = new java.util.HashMap<>();
        benchmarks.put("Industry Average Rating", 4.0);
        benchmarks.put("Industry Completion Rate", 0.65);

        report.setIndustryBenchmarks(benchmarks);
    }

    private void generateTrendAnalysisReport(SurveyReportDto report, Survey survey, LocalDate periodStart, LocalDate periodEnd) {
        // Get trend data for the period
        // This would analyze rating trends over time
        List<String> trends = new java.util.ArrayList<>();
        trends.add("Overall satisfaction trend: Stable");
        trends.add("Response rate trend: Increasing");

        report.setKeyInsights(trends);
    }

    @Transactional
    public SurveyResponseDto assignmentSurveyToUser(@Valid SurveyAssignmentDto assignmentDto) {

        Survey survey = surveyRepository.findById(assignmentDto.getSurveyId()).orElse(null);
        User attend = userService.findUserById(assignmentDto.getAttendId());
        School school = schoolRepository.findById(assignmentDto.getSchoolId()).orElse(null);
        Appointment appointment = appointmentRepository.findById(assignmentDto.getAppointmentId()).orElse(null);

        if (survey != null && attend != null && school != null && appointment != null) {
            List<SurveyQuestion> surveyQuestions = surveyQuestionRepository.findRatingQuestionsBySurveyId(survey.getId());
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.setSurvey(survey);
            surveyResponse.setAppointment(appointment);
            surveyResponse.setSchool(school);
            surveyResponse.setStatus(ResponseStatus.INVITED);
            surveyResponse.setStartedAt(LocalDateTime.now());
            UUID uuid = UUID.randomUUID();
            surveyResponse.setResponseToken(uuid.toString());


            surveyResponse.setRespondentUser(attend);
            SurveyResponse createdSurveyResponse = surveyResponseRepository.saveAndFlush(surveyResponse);
            Set<SurveyQuestionResponse> surveyQuestionResponses = new HashSet<>();

            for (SurveyQuestion surveyQuestion : surveyQuestions) {
                SurveyQuestionResponse surveyQuestionResponse = new SurveyQuestionResponse();
                surveyQuestionResponse.setQuestion(surveyQuestion);
                surveyQuestionResponse.setSurveyResponse(createdSurveyResponse);
                SurveyQuestionResponse createdSurveyQuestionResponse = surveyQuestionResponseRepository.saveAndFlush(surveyQuestionResponse);
                surveyQuestionResponses.add(createdSurveyQuestionResponse);
            }
            createdSurveyResponse.setQuestionResponses(surveyQuestionResponses);
            surveyResponseRepository.saveAndFlush(surveyResponse);
            return converterService.mapResponseToDto(surveyResponse);
        }

        return null;


    }

    public List<SurveyResponseDto> getSurveyToUser(Long attendId) {
        List<SurveyResponse> byRespondentId = surveyResponseRepository.findByRespondentId(attendId);
        return byRespondentId.stream()
                .map(converterService::mapResponseToDto)
                .collect(Collectors.toList());
    }

    public List<SurveyResponseDto> getSurveyToSchool(Long schoolId) {
        List<SurveyResponse> byRespondentId = surveyResponseRepository.findBySchoolId(schoolId);
        return byRespondentId.stream()
                .map(converterService::mapResponseToDto)
                .collect(Collectors.toList());
    }

    public List<SurveyDto> getAllSurveys() {
        List<Survey> surveys = surveyRepository.findAll();
        return converterService.mapToDto(surveys);
    }

    @Transactional
    public String updateServerResponse(Long surveyResponseId, SurveyResponseDto surveyResponseData) {

        SurveyResponse surveyResponse = surveyResponseRepository.findById(surveyResponseId).orElse(null);
        if (surveyResponse == null) {
            return "OBJECT NOT FOUND";
        }

        try {
            List<SurveyQuestionResponseDto> questionResponseDtos = surveyResponseData.getQuestionResponses();
            Set<SurveyQuestionResponse> questionResponses = surveyResponse.getQuestionResponses();

            Map<Long, SurveyQuestionResponseDto> dtoMap = questionResponseDtos.stream()
                    .collect(Collectors.toMap(SurveyQuestionResponseDto::getId, Function.identity()));

            for (SurveyQuestionResponse surveyQuestionResponse : questionResponses) {
                SurveyQuestionResponseDto dto = dtoMap.get(surveyQuestionResponse.getId());
                if (dto != null && dto.getRatingResponse() != null) {
                    surveyQuestionResponse.setRatingResponse(dto.getRatingResponse());
                }
            }
            surveyQuestionResponseRepository.saveAll(questionResponses);
            return "SUCCESS";
        } catch (Exception e) {
            return "EROOR";
        }

    }
}
