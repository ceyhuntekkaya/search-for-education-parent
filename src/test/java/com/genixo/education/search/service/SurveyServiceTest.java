package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.survey.*;
import com.genixo.education.search.entity.survey.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.appointment.Appointment;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.appointment.AppointmentRepository;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.survey.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.SurveyConverterService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SurveyService Tests")
class SurveyServiceTest {

    @Mock private SurveyRepository surveyRepository;
    @Mock private SurveyQuestionRepository surveyQuestionRepository;
    @Mock private SurveyResponseRepository surveyResponseRepository;
    @Mock private SurveyQuestionResponseRepository surveyQuestionResponseRepository;
    @Mock private SurveyRatingRepository surveyRatingRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private SurveyConverterService converterService;
    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private SurveyService surveyService;

    private User systemUser;
    private User campusUser;
    private User regularUser;
    private SurveyCreateDto validSurveyCreateDto;
    private Survey savedSurvey;
    private SurveyDto expectedSurveyDto;

    @BeforeEach
    void setUp() {
        // Users with different access levels
        systemUser = createUser(1L, RoleLevel.SYSTEM);
        campusUser = createUser(2L, RoleLevel.CAMPUS);
        regularUser = createUser(3L, RoleLevel.BRAND);

        // Valid survey create DTO
        validSurveyCreateDto = SurveyCreateDto.builder()
                .title("Customer Satisfaction Survey")
                .description("Survey to measure customer satisfaction")
                .surveyType(SurveyType.APPOINTMENT_FEEDBACK)
                .triggerEvent(SurveyTriggerEvent.APPOINTMENT_COMPLETED)
                .isActive(true)
                .isAnonymous(false)
                .isMandatory(true)
                .showResultsToPublic(false)
                .sendDelayHours(24)
                .reminderDelayHours(72)
                .maxReminders(2)
                .expiresAfterDays(7)
                .primaryColor("#007bff")
                .welcomeMessage("Welcome to our survey")
                .thankYouMessage("Thank you for your feedback")
                .emailSubject("Please share your feedback")
                .emailBody("We value your opinion...")
                .build();

        // Saved survey entity
        savedSurvey = new Survey();
        savedSurvey.setId(1L);
        savedSurvey.setTitle("Customer Satisfaction Survey");
        savedSurvey.setDescription("Survey to measure customer satisfaction");
        savedSurvey.setSurveyType(SurveyType.APPOINTMENT_FEEDBACK);
        savedSurvey.setTriggerEvent(SurveyTriggerEvent.APPOINTMENT_COMPLETED);
        savedSurvey.setIsActive(true);
        savedSurvey.setCreatedBy(1L);
        savedSurvey.setCreatedAt(LocalDateTime.now());

        // Expected DTO response
        expectedSurveyDto = SurveyDto.builder()
                .id(1L)
                .title("Customer Satisfaction Survey")
                .description("Survey to measure customer satisfaction")
                .surveyType(SurveyType.APPOINTMENT_FEEDBACK)
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("createSurvey() Tests")
    class CreateSurveyTests {

        @Test
        @DisplayName("Should create survey successfully with valid data")
        void shouldCreateSurveySuccessfullyWithValidData() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
            when(converterService.mapToDto(savedSurvey)).thenReturn(expectedSurveyDto);

            // When
            SurveyDto result = surveyService.createSurvey(validSurveyCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Customer Satisfaction Survey", result.getTitle());
            assertEquals("Survey to measure customer satisfaction", result.getDescription());
            assertEquals(SurveyType.APPOINTMENT_FEEDBACK, result.getSurveyType());

            verify(jwtService).getUser(request);
            verify(surveyRepository).save(argThat(survey ->
                    survey.getTitle().equals("Customer Satisfaction Survey") &&
                            survey.getDescription().equals("Survey to measure customer satisfaction") &&
                            survey.getSurveyType() == SurveyType.APPOINTMENT_FEEDBACK &&
                            survey.getTriggerEvent() == SurveyTriggerEvent.APPOINTMENT_COMPLETED &&
                            survey.getIsActive().equals(true) &&
                            survey.getIsAnonymous().equals(false) &&
                            survey.getIsMandatory().equals(true) &&
                            survey.getShowResultsToPublic().equals(false) &&
                            survey.getSendDelayHours().equals(24) &&
                            survey.getReminderDelayHours().equals(72) &&
                            survey.getMaxReminders().equals(2) &&
                            survey.getExpiresAfterDays().equals(7) &&
                            survey.getPrimaryColor().equals("#007bff") &&
                            survey.getWelcomeMessage().equals("Welcome to our survey") &&
                            survey.getThankYouMessage().equals("Thank you for your feedback") &&
                            survey.getEmailSubject().equals("Please share your feedback") &&
                            survey.getEmailBody().equals("We value your opinion...") &&
                            survey.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(savedSurvey);
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage surveys")
        void shouldThrowBusinessExceptionWhenUserCannotManageSurveys() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> surveyService.createSurvey(validSurveyCreateDto, request));

            assertEquals("User does not have permission to manage surveys", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(surveyRepository, converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when survey title is empty")
        void shouldThrowBusinessExceptionWhenSurveyTitleIsEmpty() {
            // Given
            SurveyCreateDto emptyTitleDto = SurveyCreateDto.builder()
                    .title("") // Empty title
                    .description("Valid description")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> surveyService.createSurvey(emptyTitleDto, request));

            assertEquals("Survey title is required", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(surveyRepository, converterService);
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            SurveyCreateDto minimalDto = SurveyCreateDto.builder()
                    .title("Minimal Survey")
                    .description("Basic survey")
                    // All optional fields are null
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
            when(converterService.mapToDto(any(Survey.class))).thenReturn(expectedSurveyDto);

            // When
            surveyService.createSurvey(minimalDto, request);

            // Then
            verify(surveyRepository).save(argThat(survey ->
                    survey.getTitle().equals("Minimal Survey") &&
                            survey.getDescription().equals("Basic survey") &&
                            survey.getSurveyType() == SurveyType.GENERAL_FEEDBACK && // Default
                            survey.getIsActive().equals(true) && // Default
                            survey.getIsAnonymous().equals(false) && // Default
                            survey.getIsMandatory().equals(false) && // Default
                            survey.getShowResultsToPublic().equals(false) && // Default
                            survey.getSendDelayHours().equals(24) && // Default
                            survey.getReminderDelayHours().equals(72) && // Default
                            survey.getMaxReminders().equals(2) && // Default
                            survey.getExpiresAfterDays().equals(7) && // Default
                            survey.getPrimaryColor().equals("#007bff") // Default
            ));
        }

        @Test
        @DisplayName("Should create survey with questions when provided")
        void shouldCreateSurveyWithQuestionsWhenProvided() {
            // Given
            SurveyQuestionCreateDto questionDto = SurveyQuestionCreateDto.builder()
                    .questionText("How satisfied are you?")
                    .questionType(QuestionType.RATING_STAR)
                    .ratingCategory(RatingCategory.OVERALL_SATISFACTION)
                    .isRequired(true)
                    .build();

            SurveyCreateDto surveyWithQuestions = SurveyCreateDto.builder()
                    .title("Survey with Questions")
                    .description("Survey with initial questions")
                    .questions(List.of(questionDto))
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
            when(surveyQuestionRepository.save(any(SurveyQuestion.class))).thenReturn(new SurveyQuestion());
            when(converterService.mapToDto(any(Survey.class))).thenReturn(expectedSurveyDto);

            // When
            surveyService.createSurvey(surveyWithQuestions, request);

            // Then
            verify(surveyRepository).save(any(Survey.class));
            verify(surveyQuestionRepository).save(argThat(question ->
                    question.getQuestionText().equals("How satisfied are you?") &&
                            question.getQuestionType() == QuestionType.RATING_STAR &&
                            question.getRatingCategory() == RatingCategory.OVERALL_SATISFACTION &&
                            question.getIsRequired().equals(true) &&
                            question.getSortOrder().equals(1) && // First question
                            question.getIsActive().equals(true)
            ));
        }

        @Test
        @DisplayName("Should allow campus user to create surveys")
        void shouldAllowCampusUserToCreateSurveys() {
            // Given
            when(jwtService.getUser(request)).thenReturn(campusUser);
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
            when(converterService.mapToDto(any(Survey.class))).thenReturn(expectedSurveyDto);

            // When
            SurveyDto result = surveyService.createSurvey(validSurveyCreateDto, request);

            // Then
            assertNotNull(result);
            verify(surveyRepository).save(argThat(survey ->
                    survey.getCreatedBy().equals(2L) // Campus user ID
            ));
        }
    }

    @Nested
    @DisplayName("getSurveyById() Tests")
    class GetSurveyByIdTests {

        @Test
        @DisplayName("Should return survey successfully when user has access")
        void shouldReturnSurveySuccessfullyWhenUserHasAccess() {
            // Given
            Long surveyId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));
            when(converterService.mapToDto(savedSurvey)).thenReturn(expectedSurveyDto);

            // When
            SurveyDto result = surveyService.getSurveyById(surveyId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedSurveyDto.getTitle(), result.getTitle());
            assertEquals(expectedSurveyDto.getSurveyType(), result.getSurveyType());

            verify(jwtService).getUser(request);
            verify(surveyRepository).findByIdAndIsActiveTrue(surveyId);
            verify(converterService).mapToDto(savedSurvey);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when survey not found")
        void shouldThrowResourceNotFoundExceptionWhenSurveyNotFound() {
            // Given
            Long nonExistentSurveyId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(nonExistentSurveyId)).thenReturn(java.util.Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> surveyService.getSurveyById(nonExistentSurveyId, request));

            assertEquals("Survey not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(surveyRepository).findByIdAndIsActiveTrue(nonExistentSurveyId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to survey")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToSurvey() {
            // Given
            Long surveyId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> surveyService.getSurveyById(surveyId, request));

            assertEquals("User does not have access to this survey", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(surveyRepository).findByIdAndIsActiveTrue(surveyId);
            verifyNoInteractions(converterService);
        }
    }

    @Nested
    @DisplayName("updateSurvey() Tests")
    class UpdateSurveyTests {

        @Test
        @DisplayName("Should update survey successfully with valid data")
        void shouldUpdateSurveySuccessfullyWithValidData() {
            // Given
            Long surveyId = 1L;
            SurveyCreateDto updateDto = SurveyCreateDto.builder()
                    .title("Updated Survey Title")
                    .description("Updated description")
                    .isActive(false)
                    .primaryColor("#ff0000")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));
            when(surveyResponseRepository.countBySurveyIdAndIsActiveTrue(surveyId)).thenReturn(0L);
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
            when(converterService.mapToDto(any(Survey.class))).thenReturn(expectedSurveyDto);

            // When
            SurveyDto result = surveyService.updateSurvey(surveyId, updateDto, request);

            // Then
            assertNotNull(result);
            verify(surveyRepository).save(argThat(survey ->
                    survey.getTitle().equals("Updated Survey Title") &&
                            survey.getDescription().equals("Updated description") &&
                            survey.getIsActive().equals(false) &&
                            survey.getPrimaryColor().equals("#ff0000") &&
                            survey.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when survey not found")
        void shouldThrowBusinessExceptionWhenSurveyNotFound() {
            // Given
            Long nonExistentSurveyId = 999L;
            SurveyCreateDto updateDto = SurveyCreateDto.builder()
                    .title("Updated Title")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(nonExistentSurveyId)).thenReturn(java.util.Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> surveyService.updateSurvey(nonExistentSurveyId, updateDto, request));

            assertEquals("Survey not found with ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw BusinessException when trying to change title of survey with responses")
        void shouldThrowBusinessExceptionWhenTryingToChangeTitleOfSurveyWithResponses() {
            // Given
            Long surveyId = 1L;
            SurveyCreateDto updateDto = SurveyCreateDto.builder()
                    .title("New Title") // Different from original
                    .build();

            savedSurvey.setTitle("Original Title");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));
            when(surveyResponseRepository.countBySurveyIdAndIsActiveTrue(surveyId)).thenReturn(5L); // Has responses

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> surveyService.updateSurvey(surveyId, updateDto, request));

            assertEquals("Cannot change survey title after receiving responses", exception.getMessage());
            verify(surveyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow updating with same title when survey has responses")
        void shouldAllowUpdatingWithSameTitleWhenSurveyHasResponses() {
            // Given
            Long surveyId = 1L;
            SurveyCreateDto updateDto = SurveyCreateDto.builder()
                    .title("Customer Satisfaction Survey") // Same as original
                    .description("Updated description")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));
            when(surveyResponseRepository.countBySurveyIdAndIsActiveTrue(surveyId)).thenReturn(5L); // Has responses
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
            when(converterService.mapToDto(any(Survey.class))).thenReturn(expectedSurveyDto);

            // When
            SurveyDto result = surveyService.updateSurvey(surveyId, updateDto, request);

            // Then
            assertNotNull(result);
            verify(surveyRepository).save(argThat(survey ->
                    survey.getTitle().equals("Customer Satisfaction Survey") &&
                            survey.getDescription().equals("Updated description")
            ));
        }
    }

    @Nested
    @DisplayName("deleteSurvey() Tests")
    class DeleteSurveyTests {

        @Test
        @DisplayName("Should delete survey successfully when no responses exist")
        void shouldDeleteSurveySuccessfullyWhenNoResponsesExist() {
            // Given
            Long surveyId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));
            when(surveyResponseRepository.countBySurveyIdAndIsActiveTrue(surveyId)).thenReturn(0L);
            when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);

            // When
            assertDoesNotThrow(() -> surveyService.deleteSurvey(surveyId, request));

            // Then
            verify(surveyRepository).save(argThat(survey ->
                    !survey.getIsActive() &&
                            survey.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when survey has existing responses")
        void shouldThrowBusinessExceptionWhenSurveyHasExistingResponses() {
            // Given
            Long surveyId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(surveyId)).thenReturn(java.util.Optional.of(savedSurvey));
            when(surveyResponseRepository.countBySurveyIdAndIsActiveTrue(surveyId)).thenReturn(10L); // Has responses

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> surveyService.deleteSurvey(surveyId, request));

            assertEquals("Cannot delete survey with existing responses. Found 10 responses.", exception.getMessage());
            verify(surveyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when survey not found")
        void shouldThrowResourceNotFoundExceptionWhenSurveyNotFound() {
            // Given
            Long nonExistentSurveyId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(surveyRepository.findByIdAndIsActiveTrue(nonExistentSurveyId)).thenReturn(java.util.Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> surveyService.deleteSurvey(nonExistentSurveyId, request));

            assertEquals("Survey not found with ID: 999", exception.getMessage());
        }
    }

    // Helper methods
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);
        user.setUserRoles(Set.of(createUserRole(roleLevel)));
        user.setInstitutionAccess(Collections.emptySet());
        return user;
    }
    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }

    private List<MockUserRole> createMockUserRoles(RoleLevel roleLevel) {
        MockUserRole mockRole = new MockUserRole();
        mockRole.setRoleLevel(roleLevel);
        return List.of(mockRole);
    }

    // Mock inner class for UserRole
    @Setter
    @Getter
    private static class MockUserRole {
        private RoleLevel roleLevel;

    }
}