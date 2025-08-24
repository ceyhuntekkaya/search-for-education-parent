package com.genixo.education.search.service.converter;


import com.genixo.education.search.dto.survey.*;
import com.genixo.education.search.entity.survey.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurveyConverterService {

    @Autowired
    private UserConverterService userConverterService;

    @Autowired
    private InstitutionConverterService institutionConverterService;


    public SurveyDto mapToDto(Survey entity) {
        if (entity == null) {
            return null;
        }

        SurveyDto dto = new SurveyDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setSurveyType(entity.getSurveyType());
        dto.setTriggerEvent(entity.getTriggerEvent());
        dto.setIsActive(entity.getIsActive());
        dto.setIsAnonymous(entity.getIsAnonymous());
        dto.setIsMandatory(entity.getIsMandatory());
        dto.setShowResultsToPublic(entity.getShowResultsToPublic());

        // Timing settings
        dto.setSendDelayHours(entity.getSendDelayHours());
        dto.setReminderDelayHours(entity.getReminderDelayHours());
        dto.setMaxReminders(entity.getMaxReminders());
        dto.setExpiresAfterDays(entity.getExpiresAfterDays());

        // Styling and branding
        dto.setPrimaryColor(entity.getPrimaryColor());
        dto.setLogoUrl(entity.getLogoUrl());
        dto.setHeaderImageUrl(entity.getHeaderImageUrl());
        dto.setCustomCss(entity.getCustomCss());

        // Messages
        dto.setWelcomeMessage(entity.getWelcomeMessage());
        dto.setThankYouMessage(entity.getThankYouMessage());
        dto.setCompletionRedirectUrl(entity.getCompletionRedirectUrl());

        // Email template
        dto.setEmailSubject(entity.getEmailSubject());
        dto.setEmailBody(entity.getEmailBody());
        dto.setEmailTemplateId(entity.getEmailTemplateId());

        // Analytics
        dto.setTotalSent(entity.getTotalSent());
        dto.setTotalStarted(entity.getTotalStarted());
        dto.setTotalCompleted(entity.getTotalCompleted());
        dto.setAverageCompletionTimeSeconds(entity.getAverageCompletionTimeSeconds());
        dto.setAverageRating(entity.getAverageRating());

        // Calculated fields
        calculateSurveyMetrics(dto);

        // Questions
        if (entity.getQuestions() != null && !entity.getQuestions().isEmpty()) {
            List<SurveyQuestionDto> questionDtos = entity.getQuestions().stream()
                    .sorted(Comparator.comparing(SurveyQuestion::getSortOrder))
                    .map(this::mapQuestionToDto)
                    .collect(Collectors.toList());
            dto.setQuestions(questionDtos);
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public Survey mapToEntity(SurveyCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Survey entity = new Survey();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setSurveyType(dto.getSurveyType());
        entity.setTriggerEvent(dto.getTriggerEvent());
        entity.setIsActive(ConversionUtils.defaultIfNull(dto.getIsActive(), true));
        entity.setIsAnonymous(ConversionUtils.defaultIfNull(dto.getIsAnonymous(), false));
        entity.setIsMandatory(ConversionUtils.defaultIfNull(dto.getIsMandatory(), false));
        entity.setShowResultsToPublic(ConversionUtils.defaultIfNull(dto.getShowResultsToPublic(), false));

        // Timing settings
        entity.setSendDelayHours(ConversionUtils.defaultIfNull(dto.getSendDelayHours(), 24));
        entity.setReminderDelayHours(ConversionUtils.defaultIfNull(dto.getReminderDelayHours(), 72));
        entity.setMaxReminders(ConversionUtils.defaultIfNull(dto.getMaxReminders(), 2));
        entity.setExpiresAfterDays(ConversionUtils.defaultIfNull(dto.getExpiresAfterDays(), 7));

        // Styling and branding
        entity.setPrimaryColor(ConversionUtils.defaultIfEmpty(dto.getPrimaryColor(), "#007bff"));
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setHeaderImageUrl(dto.getHeaderImageUrl());
        entity.setCustomCss(dto.getCustomCss());

        // Messages
        entity.setWelcomeMessage(dto.getWelcomeMessage());
        entity.setThankYouMessage(dto.getThankYouMessage());
        entity.setCompletionRedirectUrl(dto.getCompletionRedirectUrl());

        // Email template
        entity.setEmailSubject(dto.getEmailSubject());
        entity.setEmailBody(dto.getEmailBody());
        entity.setEmailTemplateId(dto.getEmailTemplateId());

        // Initialize analytics with defaults
        entity.setTotalSent(0L);
        entity.setTotalStarted(0L);
        entity.setTotalCompleted(0L);
        entity.setAverageRating(0.0);

        return entity;
    }

    public void updateEntity(SurveyUpdateDto dto, Survey entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getSurveyType() != null) {
            entity.setSurveyType(dto.getSurveyType());
        }
        if (dto.getTriggerEvent() != null) {
            entity.setTriggerEvent(dto.getTriggerEvent());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getIsAnonymous() != null) {
            entity.setIsAnonymous(dto.getIsAnonymous());
        }
        if (dto.getIsMandatory() != null) {
            entity.setIsMandatory(dto.getIsMandatory());
        }
        if (dto.getShowResultsToPublic() != null) {
            entity.setShowResultsToPublic(dto.getShowResultsToPublic());
        }

        // Timing settings
        if (dto.getSendDelayHours() != null) {
            entity.setSendDelayHours(dto.getSendDelayHours());
        }
        if (dto.getReminderDelayHours() != null) {
            entity.setReminderDelayHours(dto.getReminderDelayHours());
        }
        if (dto.getMaxReminders() != null) {
            entity.setMaxReminders(dto.getMaxReminders());
        }
        if (dto.getExpiresAfterDays() != null) {
            entity.setExpiresAfterDays(dto.getExpiresAfterDays());
        }

        // Styling and branding
        if (StringUtils.hasText(dto.getPrimaryColor())) {
            entity.setPrimaryColor(dto.getPrimaryColor());
        }
        if (StringUtils.hasText(dto.getLogoUrl())) {
            entity.setLogoUrl(dto.getLogoUrl());
        }
        if (StringUtils.hasText(dto.getHeaderImageUrl())) {
            entity.setHeaderImageUrl(dto.getHeaderImageUrl());
        }
        if (StringUtils.hasText(dto.getCustomCss())) {
            entity.setCustomCss(dto.getCustomCss());
        }

        // Messages
        if (StringUtils.hasText(dto.getWelcomeMessage())) {
            entity.setWelcomeMessage(dto.getWelcomeMessage());
        }
        if (StringUtils.hasText(dto.getThankYouMessage())) {
            entity.setThankYouMessage(dto.getThankYouMessage());
        }
        if (StringUtils.hasText(dto.getCompletionRedirectUrl())) {
            entity.setCompletionRedirectUrl(dto.getCompletionRedirectUrl());
        }

        // Email template
        if (StringUtils.hasText(dto.getEmailSubject())) {
            entity.setEmailSubject(dto.getEmailSubject());
        }
        if (StringUtils.hasText(dto.getEmailBody())) {
            entity.setEmailBody(dto.getEmailBody());
        }
        if (StringUtils.hasText(dto.getEmailTemplateId())) {
            entity.setEmailTemplateId(dto.getEmailTemplateId());
        }
    }

    // =================== SURVEY QUESTION CONVERSION ===================

    public SurveyQuestionDto mapQuestionToDto(SurveyQuestion entity) {
        if (entity == null) {
            return null;
        }

        SurveyQuestionDto dto = new SurveyQuestionDto();
        dto.setId(entity.getId());
        dto.setSurveyId(entity.getSurvey() != null ? entity.getSurvey().getId() : null);
        dto.setQuestionText(entity.getQuestionText());
        dto.setDescription(entity.getDescription());
        dto.setQuestionType(entity.getQuestionType());
        dto.setRatingCategory(entity.getRatingCategory());
        dto.setIsRequired(entity.getIsRequired());
        dto.setSortOrder(entity.getSortOrder());
        dto.setIsActive(entity.getIsActive());

        // Choice questions
        dto.setOptions(entity.getOptions());
        dto.setAllowOtherOption(entity.getAllowOtherOption());
        dto.setOtherOptionLabel(entity.getOtherOptionLabel());

        // Rating questions
        dto.setRatingScaleMin(entity.getRatingScaleMin());
        dto.setRatingScaleMax(entity.getRatingScaleMax());
        dto.setRatingScaleStep(entity.getRatingScaleStep());
        dto.setRatingLabels(entity.getRatingLabels());

        // Text questions
        dto.setTextMinLength(entity.getTextMinLength());
        dto.setTextMaxLength(entity.getTextMaxLength());
        dto.setPlaceholderText(entity.getPlaceholderText());

        // Conditional logic
        dto.setShowIfQuestionId(entity.getShowIfQuestionId());
        dto.setShowIfAnswer(entity.getShowIfAnswer());
        dto.setShowIfCondition(entity.getShowIfCondition());

        // Styling
        dto.setCustomCssClass(entity.getCustomCssClass());
        dto.setHelpText(entity.getHelpText());
        dto.setImageUrl(entity.getImageUrl());

        // Analytics
        dto.setTotalResponses(entity.getTotalResponses());
        dto.setAverageRating(entity.getAverageRating());
        dto.setSkipCount(entity.getSkipCount());

        // Calculated fields
        calculateQuestionMetrics(dto);

        return dto;
    }

    public SurveyQuestion mapQuestionToEntity(SurveyQuestionCreateDto dto) {
        if (dto == null) {
            return null;
        }

        SurveyQuestion entity = new SurveyQuestion();
        entity.setQuestionText(dto.getQuestionText());
        entity.setDescription(dto.getDescription());
        entity.setQuestionType(dto.getQuestionType());
        entity.setRatingCategory(dto.getRatingCategory());
        entity.setIsRequired(ConversionUtils.defaultIfNull(dto.getIsRequired(), false));
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));
        entity.setIsActive(true);

        // Choice questions
        entity.setOptions(dto.getOptions());
        entity.setAllowOtherOption(ConversionUtils.defaultIfNull(dto.getAllowOtherOption(), false));
        entity.setOtherOptionLabel(ConversionUtils.defaultIfEmpty(dto.getOtherOptionLabel(), "Diğer"));

        // Rating questions
        entity.setRatingScaleMin(ConversionUtils.defaultIfNull(dto.getRatingScaleMin(), 1));
        entity.setRatingScaleMax(ConversionUtils.defaultIfNull(dto.getRatingScaleMax(), 5));
        entity.setRatingScaleStep(ConversionUtils.defaultIfNull(dto.getRatingScaleStep(), 1));
        entity.setRatingLabels(dto.getRatingLabels());

        // Text questions
        entity.setTextMinLength(dto.getTextMinLength());
        entity.setTextMaxLength(dto.getTextMaxLength());
        entity.setPlaceholderText(dto.getPlaceholderText());

        // Conditional logic
        entity.setShowIfQuestionId(dto.getShowIfQuestionId());
        entity.setShowIfAnswer(dto.getShowIfAnswer());
        entity.setShowIfCondition(dto.getShowIfCondition());

        // Styling
        entity.setCustomCssClass(dto.getCustomCssClass());
        entity.setHelpText(dto.getHelpText());
        entity.setImageUrl(dto.getImageUrl());

        // Initialize analytics
        entity.setTotalResponses(0L);
        entity.setAverageRating(0.0);
        entity.setSkipCount(0L);

        return entity;
    }

    // =================== SURVEY RESPONSE CONVERSION ===================

    public SurveyResponseDto mapResponseToDto(SurveyResponse entity) {
        if (entity == null) {
            return null;
        }

        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(entity.getId());
        dto.setSurveyId(entity.getSurvey() != null ? entity.getSurvey().getId() : null);
        dto.setSurveyTitle(entity.getSurvey() != null ? entity.getSurvey().getTitle() : null);
        dto.setRespondentUserId(entity.getRespondentUser() != null ? entity.getRespondentUser().getId() : null);
        dto.setRespondentUserName(entity.getRespondentUser() != null ?
                entity.getRespondentUser().getFirstName() + " " + entity.getRespondentUser().getLastName() : null);
        dto.setSchoolId(entity.getSchool() != null ? entity.getSchool().getId() : null);
        dto.setSchoolName(entity.getSchool() != null ? entity.getSchool().getName() : null);
        dto.setAppointmentId(entity.getAppointment() != null ? entity.getAppointment().getId() : null);
        dto.setResponseToken(entity.getResponseToken());
        dto.setStatus(entity.getStatus());
        dto.setStartedAt(entity.getStartedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setCompletionTimeSeconds(entity.getCompletionTimeSeconds());

        // Contact information
        dto.setRespondentName(entity.getRespondentName());
        dto.setRespondentEmail(entity.getRespondentEmail());
        dto.setRespondentPhone(entity.getRespondentPhone());

        // Technical information
        dto.setIpAddress(entity.getIpAddress());
        dto.setUserAgent(entity.getUserAgent());
        dto.setBrowserInfo(entity.getBrowserInfo());
        dto.setDeviceInfo(entity.getDeviceInfo());

        // Survey invitation tracking
        dto.setInvitationSentAt(entity.getInvitationSentAt());
        dto.setInvitationOpenedAt(entity.getInvitationOpenedAt());
        dto.setReminderCount(entity.getReminderCount());
        dto.setLastReminderSentAt(entity.getLastReminderSentAt());

        // Overall ratings
        dto.setOverallRating(entity.getOverallRating());
        dto.setCleanlinessRating(entity.getCleanlinessRating());
        dto.setStaffRating(entity.getStaffRating());
        dto.setFacilitiesRating(entity.getFacilitiesRating());
        dto.setCommunicationRating(entity.getCommunicationRating());

        // Additional feedback
        dto.setGeneralFeedback(entity.getGeneralFeedback());
        dto.setSuggestions(entity.getSuggestions());
        dto.setComplaints(entity.getComplaints());
        dto.setWouldRecommend(entity.getWouldRecommend());
        dto.setLikelihoodToEnroll(entity.getLikelihoodToEnroll());

        // Calculated fields
        calculateResponseMetrics(dto);

        // Question responses
        if (entity.getQuestionResponses() != null && !entity.getQuestionResponses().isEmpty()) {
            List<SurveyQuestionResponseDto> questionResponseDtos = entity.getQuestionResponses().stream()
                    .map(this::mapQuestionResponseToDto)
                    .collect(Collectors.toList());
            dto.setQuestionResponses(questionResponseDtos);
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public SurveyResponse mapResponseToEntity(SurveyResponseCreateDto dto) {
        if (dto == null) {
            return null;
        }

        SurveyResponse entity = new SurveyResponse();
        entity.setResponseToken(generateResponseToken());
        entity.setStartedAt(LocalDateTime.now());

        // Contact information
        entity.setRespondentName(dto.getRespondentName());
        entity.setRespondentEmail(dto.getRespondentEmail());
        entity.setRespondentPhone(dto.getRespondentPhone());

        // Technical information
        entity.setIpAddress(dto.getIpAddress());
        entity.setUserAgent(dto.getUserAgent());
        entity.setBrowserInfo(dto.getBrowserInfo());
        entity.setDeviceInfo(dto.getDeviceInfo());

        // Additional feedback
        entity.setGeneralFeedback(dto.getGeneralFeedback());
        entity.setSuggestions(dto.getSuggestions());
        entity.setComplaints(dto.getComplaints());
        entity.setWouldRecommend(dto.getWouldRecommend());
        entity.setLikelihoodToEnroll(dto.getLikelihoodToEnroll());

        return entity;
    }

    // =================== SURVEY QUESTION RESPONSE CONVERSION ===================

    public SurveyQuestionResponseDto mapQuestionResponseToDto(SurveyQuestionResponse entity) {
        if (entity == null) {
            return null;
        }

        SurveyQuestionResponseDto dto = new SurveyQuestionResponseDto();
        dto.setId(entity.getId());
        dto.setSurveyResponseId(entity.getSurveyResponse() != null ? entity.getSurveyResponse().getId() : null);
        dto.setQuestionId(entity.getQuestion() != null ? entity.getQuestion().getId() : null);
        dto.setQuestionText(entity.getQuestion() != null ? entity.getQuestion().getQuestionText() : null);
        dto.setQuestionType(entity.getQuestion() != null ? entity.getQuestion().getQuestionType() : null);
        dto.setRatingCategory(entity.getQuestion() != null ? entity.getQuestion().getRatingCategory() : null);

        // Response values
        dto.setTextResponse(entity.getTextResponse());
        dto.setNumberResponse(entity.getNumberResponse());
        dto.setDateResponse(entity.getDateResponse());
        dto.setTimeResponse(entity.getTimeResponse());
        dto.setDatetimeResponse(entity.getDatetimeResponse());
        dto.setBooleanResponse(entity.getBooleanResponse());
        dto.setRatingResponse(entity.getRatingResponse());
        dto.setChoiceResponses(entity.getChoiceResponses());
        dto.setMatrixResponses(entity.getMatrixResponses());

        // File uploads
        dto.setFileUrl(entity.getFileUrl());
        dto.setFileName(entity.getFileName());
        dto.setFileSize(entity.getFileSize());
        dto.setFileType(entity.getFileType());

        // Other option text
        dto.setOtherText(entity.getOtherText());

        // Response metadata
        dto.setResponseTimeSeconds(entity.getResponseTimeSeconds());
        dto.setWasSkipped(entity.getWasSkipped());
        dto.setSkipReason(entity.getSkipReason());
        dto.setResponseOrder(entity.getResponseOrder());
        dto.setRevisionCount(entity.getRevisionCount());
        dto.setConfidenceLevel(entity.getConfidenceLevel());

        // Calculated fields
        calculateQuestionResponseMetrics(dto);

        return dto;
    }

    public SurveyQuestionResponse mapQuestionResponseToEntity(SurveyQuestionResponseCreateDto dto) {
        if (dto == null) {
            return null;
        }

        SurveyQuestionResponse entity = new SurveyQuestionResponse();

        // Response values
        entity.setTextResponse(dto.getTextResponse());
        entity.setNumberResponse(dto.getNumberResponse());
        entity.setDateResponse(dto.getDateResponse());
        entity.setTimeResponse(dto.getTimeResponse());
        entity.setDatetimeResponse(dto.getDatetimeResponse());
        entity.setBooleanResponse(dto.getBooleanResponse());
        entity.setRatingResponse(dto.getRatingResponse());
        entity.setChoiceResponses(dto.getChoiceResponses());
        entity.setMatrixResponses(dto.getMatrixResponses());

        // File uploads
        entity.setFileUrl(dto.getFileUrl());
        entity.setFileName(dto.getFileName());
        entity.setFileSize(dto.getFileSize());
        entity.setFileType(dto.getFileType());

        // Other option text
        entity.setOtherText(dto.getOtherText());

        // Response metadata
        entity.setResponseTimeSeconds(dto.getResponseTimeSeconds());
        entity.setWasSkipped(ConversionUtils.defaultIfNull(dto.getWasSkipped(), false));
        entity.setSkipReason(dto.getSkipReason());
        entity.setRevisionCount(ConversionUtils.defaultIfNull(dto.getRevisionCount(), 0));
        entity.setConfidenceLevel(dto.getConfidenceLevel());

        return entity;
    }

    // =================== SURVEY RATING CONVERSION ===================

    public SurveyRatingDto mapRatingToDto(SurveyRating entity) {
        if (entity == null) {
            return null;
        }

        SurveyRatingDto dto = new SurveyRatingDto();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchool() != null ? entity.getSchool().getId() : null);
        dto.setSchoolName(entity.getSchool() != null ? entity.getSchool().getName() : null);
        dto.setSurveyResponseId(entity.getSurveyResponse() != null ? entity.getSurveyResponse().getId() : null);
        dto.setRatingCategory(entity.getRatingCategory());
        dto.setRatingValue(entity.getRatingValue());
        dto.setRatingText(entity.getRatingText());
        dto.setWeight(entity.getWeight());

        // Verification
        dto.setIsVerified(entity.getIsVerified());
        dto.setVerifiedByUserName(entity.getVerifiedBy() != null ? "Sistem Yöneticisi" : null); // Can be enhanced
        dto.setVerifiedAt(entity.getVerifiedAt());

        // Moderation
        dto.setIsFlagged(entity.getIsFlagged());
        dto.setFlagReason(entity.getFlagReason());
        dto.setFlaggedByUserName(entity.getFlaggedBy() != null ? "Moderatör" : null); // Can be enhanced
        dto.setFlaggedAt(entity.getFlaggedAt());
        dto.setIsPublic(entity.getIsPublic());
        dto.setModeratorNotes(entity.getModeratorNotes());

        // Calculated fields
        calculateRatingMetrics(dto);

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    // =================== COLLECTION MAPPERS ===================

    public List<SurveyDto> mapToDto(List<Survey> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<SurveyQuestionDto> mapQuestionsToDto(List<SurveyQuestion> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .sorted(Comparator.comparing(SurveyQuestion::getSortOrder))
                .map(this::mapQuestionToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<SurveyResponseDto> mapResponsesToDto(List<SurveyResponse> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapResponseToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<SurveyRatingDto> mapRatingsToDto(List<SurveyRating> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapRatingToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // =================== PRIVATE HELPER METHODS ===================

    private void calculateSurveyMetrics(SurveyDto dto) {
        // Calculate rates
        if (dto.getTotalSent() != null && dto.getTotalSent() > 0) {
            dto.setStartRate(ConversionUtils.calculatePercentage(dto.getTotalStarted(), dto.getTotalSent()));
        } else {
            dto.setStartRate(0.0);
        }

        if (dto.getTotalStarted() != null && dto.getTotalStarted() > 0) {
            dto.setCompletionRate(ConversionUtils.calculatePercentage(dto.getTotalCompleted(), dto.getTotalStarted()));
        } else {
            dto.setCompletionRate(0.0);
        }

        // Question count - will be set after questions are loaded
        dto.setQuestionCount(ConversionUtils.safeSize(dto.getQuestions()));

        // Estimated duration
        if (dto.getAverageCompletionTimeSeconds() != null && dto.getAverageCompletionTimeSeconds() > 0) {
            dto.setEstimatedDuration(ConversionUtils.formatDurationInWords(dto.getAverageCompletionTimeSeconds()));
        } else {
            // Default estimate based on question count
            int estimatedSeconds = dto.getQuestionCount() * 30; // 30 seconds per question
            dto.setEstimatedDuration(ConversionUtils.formatDurationInWords(estimatedSeconds));
        }

        // Check if has rating questions
        dto.setHasRatingQuestions(dto.getQuestions() != null &&
                dto.getQuestions().stream().anyMatch(q ->
                        q.getQuestionType().name().contains("RATING") ||
                                q.getRatingCategory() != null));
    }

    private void calculateQuestionMetrics(SurveyQuestionDto dto) {
        // Question type display name
        dto.setQuestionTypeDisplayName(ConversionUtils.getDisplayName(dto.getQuestionType()));

        // Rating category display name
        if (dto.getRatingCategory() != null) {
            dto.setRatingCategoryDisplayName(ConversionUtils.getDisplayName(dto.getRatingCategory()));
        }

        // Parse options list
        if (StringUtils.hasText(dto.getOptions())) {
            try {
                // Assuming JSON array format: ["Option1", "Option2"]
                dto.setOptionsList(Arrays.asList(dto.getOptions().split(",")));
            } catch (Exception e) {
                dto.setOptionsList(new ArrayList<>());
            }
        }

        // Parse rating labels map
        if (StringUtils.hasText(dto.getRatingLabels())) {
            try {
                // Assuming JSON object format: {"1": "Poor", "5": "Excellent"}
                dto.setRatingLabelsMap(new HashMap<>());
            } catch (Exception e) {
                dto.setRatingLabelsMap(new HashMap<>());
            }
        }

        // Calculate response rate
        Long totalResponses = ConversionUtils.defaultIfNull(dto.getTotalResponses(), 0L);
        Long skipCount = ConversionUtils.defaultIfNull(dto.getSkipCount(), 0L);
        Long totalAttempts = totalResponses + skipCount;

        if (totalAttempts > 0) {
            dto.setResponseRate(ConversionUtils.calculatePercentage(totalResponses, totalAttempts));
            dto.setSkipRate(ConversionUtils.calculatePercentage(skipCount, totalAttempts));
        } else {
            dto.setResponseRate(0.0);
            dto.setSkipRate(0.0);
        }
    }

    private void calculateResponseMetrics(SurveyResponseDto dto) {
        // Formatted completion time
        if (dto.getCompletionTimeSeconds() != null) {
            dto.setFormattedCompletionTime(ConversionUtils.formatDurationInWords(dto.getCompletionTimeSeconds()));
        }

        // Status display name
        if (dto.getStatus() != null) {
            dto.setStatusDisplayName(ConversionUtils.getDisplayName(dto.getStatus()));
        }

        // Progress percentage - calculated based on completed questions
        if (dto.getQuestionResponses() != null) {
            long answeredQuestions = dto.getQuestionResponses().stream()
                    .filter(qr -> !ConversionUtils.defaultIfNull(qr.getWasSkipped(), false))
                    .count();
            long totalQuestions = dto.getQuestionResponses().size();

            if (totalQuestions > 0) {
                dto.setProgressPercentage(ConversionUtils.calculatePercentage(answeredQuestions, totalQuestions));
            } else {
                dto.setProgressPercentage(0.0);
            }
        }

        // Is complete check
        dto.setIsComplete(dto.getStatus() != null &&
                (dto.getStatus().name().equals("COMPLETED") || dto.getStatus().name().equals("SUBMITTED")));

        // Is expired check
        dto.setIsExpired(dto.getStatus() != null && dto.getStatus().name().equals("EXPIRED"));
    }

    private void calculateQuestionResponseMetrics(SurveyQuestionResponseDto dto) {
        // Display value - formatted version of the response
        if (dto.getTextResponse() != null) {
            dto.setDisplayValue(dto.getTextResponse());
        } else if (dto.getNumberResponse() != null) {
            dto.setDisplayValue(ConversionUtils.formatNumber(dto.getNumberResponse().longValue()));
        } else if (dto.getBooleanResponse() != null) {
            dto.setDisplayValue(dto.getBooleanResponse() ? "Evet" : "Hayır");
        } else if (dto.getRatingResponse() != null) {
            dto.setDisplayValue(dto.getRatingResponse() + " yıldız");
        } else if (dto.getDateResponse() != null) {
            dto.setDisplayValue(ConversionUtils.formatDate(dto.getDateResponse()));
        } else if (dto.getTimeResponse() != null) {
            dto.setDisplayValue(ConversionUtils.formatTime(dto.getTimeResponse()));
        } else if (dto.getDatetimeResponse() != null) {
            dto.setDisplayValue(ConversionUtils.formatDateTime(dto.getDatetimeResponse()));
        }

        // Formatted response - more detailed formatting
        dto.setFormattedResponse(dto.getDisplayValue());

        // Parse choice responses list
        if (StringUtils.hasText(dto.getChoiceResponses())) {
            try {
                // Assuming JSON array format
                String[] choices = dto.getChoiceResponses().replaceAll("[\\[\\]\"]", "").split(",");
                dto.setChoiceResponsesList(Arrays.asList(choices));
            } catch (Exception e) {
                dto.setChoiceResponsesList(new ArrayList<>());
            }
        }

        // Parse matrix responses map
        if (StringUtils.hasText(dto.getMatrixResponses())) {
            try {
                // Assuming JSON object format
                dto.setMatrixResponsesMap(new HashMap<>());
            } catch (Exception e) {
                dto.setMatrixResponsesMap(new HashMap<>());
            }
        }
    }

    private void calculateRatingMetrics(SurveyRatingDto dto) {
        // Rating category display name
        if (dto.getRatingCategory() != null) {
            dto.setRatingCategoryDisplayName(ConversionUtils.getDisplayName(dto.getRatingCategory()));
        }

        // Rating display text
        if (dto.getRatingValue() != null) {
            String[] ratingTexts = {"Çok Kötü", "Kötü", "Orta", "İyi", "Mükemmel"};
            if (dto.getRatingValue() >= 1 && dto.getRatingValue() <= 5) {
                dto.setRatingDisplayText(ratingTexts[dto.getRatingValue() - 1]);
            }
        }

        // Star display
        if (dto.getRatingValue() != null) {
            StringBuilder stars = new StringBuilder();
            for (int i = 1; i <= 5; i++) {
                if (i <= dto.getRatingValue()) {
                    stars.append("★");
                } else {
                    stars.append("☆");
                }
            }
            dto.setStarDisplay(stars.toString());
        }
    }

    private String generateResponseToken() {
        return "RESP_" + System.currentTimeMillis() + "_" +
                (int)(Math.random() * 10000);
    }

    // =================== SUMMARY CONVERTERS ===================

    public SurveyAnalyticsDto mapToAnalyticsDto(Survey entity, LocalDateTime periodStart, LocalDateTime periodEnd) {
        if (entity == null) {
            return null;
        }

        SurveyAnalyticsDto dto = new SurveyAnalyticsDto();
        dto.setSurveyId(entity.getId());
        dto.setSurveyTitle(entity.getTitle());
        dto.setSurveyType(entity.getSurveyType());
        dto.setPeriodStart(periodStart);
        dto.setPeriodEnd(periodEnd);

        // Response metrics - these would be calculated from actual data
        dto.setTotalInvitationsSent(entity.getTotalSent());
        dto.setTotalStarted(entity.getTotalStarted());
        dto.setTotalCompleted(entity.getTotalCompleted());
        dto.setTotalSubmitted(entity.getTotalCompleted()); // Assuming completed = submitted
        dto.setTotalAbandoned(entity.getTotalStarted() - entity.getTotalCompleted());

        // Calculate rates
        if (dto.getTotalInvitationsSent() != null && dto.getTotalInvitationsSent() > 0) {
            dto.setStartRate(ConversionUtils.calculatePercentage(dto.getTotalStarted(), dto.getTotalInvitationsSent()));
        }

        if (dto.getTotalStarted() != null && dto.getTotalStarted() > 0) {
            dto.setCompletionRate(ConversionUtils.calculatePercentage(dto.getTotalCompleted(), dto.getTotalStarted()));
            dto.setSubmissionRate(dto.getCompletionRate()); // Same as completion rate
            dto.setAbandonmentRate(100.0 - dto.getCompletionRate());
        }

        // Time metrics
        dto.setAverageCompletionTimeSeconds(entity.getAverageCompletionTimeSeconds());
        dto.setMedianCompletionTimeSeconds(entity.getAverageCompletionTimeSeconds()); // Simplified
        dto.setFastestCompletionTimeSeconds(entity.getAverageCompletionTimeSeconds() / 2); // Estimated
        dto.setSlowestCompletionTimeSeconds(entity.getAverageCompletionTimeSeconds() * 2); // Estimated

        // Rating metrics
        dto.setOverallAverageRating(entity.getAverageRating());

        // Initialize other collections
        dto.setCategoryAverageRatings(new HashMap<>());
        dto.setCategoryResponseCounts(new HashMap<>());
        dto.setRatingDistribution(new HashMap<>());
        dto.setQuestionAnalytics(new ArrayList<>());
        dto.setDailyStats(new ArrayList<>());
        dto.setDeviceTypeDistribution(new HashMap<>());
        dto.setBrowserDistribution(new HashMap<>());
        dto.setChannelCompletionRates(new HashMap<>());
        dto.setSatisfactionTrends(new ArrayList<>());

        return dto;
    }

    public QuestionAnalyticsDto mapToQuestionAnalyticsDto(SurveyQuestion entity) {
        if (entity == null) {
            return null;
        }

        QuestionAnalyticsDto dto = new QuestionAnalyticsDto();
        dto.setQuestionId(entity.getId());
        dto.setQuestionText(entity.getQuestionText());
        dto.setQuestionType(entity.getQuestionType());
        dto.setRatingCategory(entity.getRatingCategory());
        dto.setTotalResponses(entity.getTotalResponses());
        dto.setSkipCount(entity.getSkipCount());

        // Calculate response rate
        Long totalResponses = ConversionUtils.defaultIfNull(entity.getTotalResponses(), 0L);
        Long skipCount = ConversionUtils.defaultIfNull(entity.getSkipCount(), 0L);
        Long totalAttempts = totalResponses + skipCount;

        if (totalAttempts > 0) {
            dto.setResponseRate(ConversionUtils.calculatePercentage(totalResponses, totalAttempts));
            dto.setSkipRate(ConversionUtils.calculatePercentage(skipCount, totalAttempts));
        }

        dto.setAverageResponseTimeSeconds(120); // Default estimate
        dto.setAverageRating(entity.getAverageRating());

        // Initialize collections
        dto.setChoiceDistribution(new HashMap<>());
        dto.setRatingDistribution(new HashMap<>());
        dto.setTopTextResponses(new ArrayList<>());
        dto.setInsights(new ArrayList<>());

        return dto;
    }

    public SchoolSurveyPerformanceDto mapToSchoolPerformanceDto(School school, List<SurveyResponse> responses) {
        if (school == null) {
            return null;
        }

        SchoolSurveyPerformanceDto dto = new SchoolSurveyPerformanceDto();
        dto.setSchoolId(school.getId());
        dto.setSchoolName(school.getName());
        dto.setSchoolName(school.getCampus() != null ? school.getCampus().getName() : null);

        if (responses != null && !responses.isEmpty()) {
            dto.setTotalResponses((long) responses.size());

            // Calculate completion rate
            long completedResponses = responses.stream()
                    .filter(r -> r.getStatus().name().equals("COMPLETED") || r.getStatus().name().equals("SUBMITTED"))
                    .count();
            dto.setCompletionRate(ConversionUtils.calculatePercentage(completedResponses, dto.getTotalResponses()));

            // Calculate average ratings
            OptionalDouble overallAvg = responses.stream()
                    .filter(r -> r.getOverallRating() != null)
                    .mapToDouble(SurveyResponse::getOverallRating)
                    .average();
            dto.setOverallRating(overallAvg.orElse(0.0));

            OptionalDouble cleanlinessAvg = responses.stream()
                    .filter(r -> r.getCleanlinessRating() != null)
                    .mapToDouble(SurveyResponse::getCleanlinessRating)
                    .average();
            dto.setCleanlinessRating(cleanlinessAvg.orElse(0.0));

            OptionalDouble staffAvg = responses.stream()
                    .filter(r -> r.getStaffRating() != null)
                    .mapToDouble(SurveyResponse::getStaffRating)
                    .average();
            dto.setStaffRating(staffAvg.orElse(0.0));

            OptionalDouble facilitiesAvg = responses.stream()
                    .filter(r -> r.getFacilitiesRating() != null)
                    .mapToDouble(SurveyResponse::getFacilitiesRating)
                    .average();
            dto.setFacilitiesRating(facilitiesAvg.orElse(0.0));

            OptionalDouble communicationAvg = responses.stream()
                    .filter(r -> r.getCommunicationRating() != null)
                    .mapToDouble(SurveyResponse::getCommunicationRating)
                    .average();
            dto.setCommunicationRating(communicationAvg.orElse(0.0));

            // Calculate recommendation rate
            long recommendCount = responses.stream()
                    .filter(r -> ConversionUtils.defaultIfNull(r.getWouldRecommend(), false))
                    .count();
            dto.setRecommendationRate(ConversionUtils.calculatePercentage(recommendCount, dto.getTotalResponses()));

            // Calculate average likelihood to enroll
            OptionalDouble enrollmentAvg = responses.stream()
                    .filter(r -> r.getLikelihoodToEnroll() != null)
                    .mapToInt(SurveyResponse::getLikelihoodToEnroll)
                    .average();
            dto.setAverageLikelihoodToEnroll(enrollmentAvg.orElse(0.0));
        }

        // Set response rate (this would need additional data)
        dto.setResponseRate(75.0); // Default

        // Determine performance level
        if (dto.getOverallRating() >= 4.5) {
            dto.setPerformanceLevel("EXCELLENT");
        } else if (dto.getOverallRating() >= 4.0) {
            dto.setPerformanceLevel("GOOD");
        } else if (dto.getOverallRating() >= 3.0) {
            dto.setPerformanceLevel("AVERAGE");
        } else if (dto.getOverallRating() >= 2.0) {
            dto.setPerformanceLevel("BELOW_AVERAGE");
        } else {
            dto.setPerformanceLevel("POOR");
        }

        // Initialize collections
        dto.setTopStrengths(new ArrayList<>());
        dto.setImprovementAreas(new ArrayList<>());
        dto.setOverallTrend("STABLE");

        return dto;
    }

    // =================== UTILITY METHODS ===================

    public boolean isValidSurveyForCreation(SurveyCreateDto dto) {
        return dto != null &&
                StringUtils.hasText(dto.getTitle()) &&
                dto.getSurveyType() != null;
    }

    public boolean isValidQuestionForCreation(SurveyQuestionCreateDto dto) {
        return dto != null &&
                StringUtils.hasText(dto.getQuestionText()) &&
                dto.getQuestionType() != null;
    }

    public boolean isValidResponseForCreation(SurveyResponseCreateDto dto) {
        return dto != null &&
                dto.getSurveyId() != null &&
                dto.getSchoolId() != null;
    }
}