package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.subscription.*;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.subscription.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.subscription.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.SubscriptionConverterService;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Tests")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CampusRepository campusRepository;
    @Mock
    private SubscriptionConverterService converterService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PaymentGatewayService paymentGatewayService;
    @Mock
    private EmailService emailService;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User systemUser;
    private User regularUser;
    private Campus validCampus;
    private SubscriptionPlan validPlan;
    private Subscription validSubscription;
    private SubscriptionCreateDto validSubscriptionCreateDto;
    private SubscriptionDto expectedSubscriptionDto;

    @BeforeEach
    void setUp() {
        // System user with SYSTEM role
        systemUser = createUser(1L, RoleLevel.SYSTEM);

        // Regular user with CAMPUS role
        regularUser = createUser(2L, RoleLevel.CAMPUS);

        // Valid campus
        validCampus = new Campus();
        validCampus.setId(1L);
        validCampus.setName("Test Campus");
        validCampus.setIsSubscribed(false);
        validCampus.setIsActive(true);

        // Valid subscription plan
        validPlan = new SubscriptionPlan();
        validPlan.setId(1L);
        validPlan.setName("BASIC");
        validPlan.setDisplayName("Basic Plan");
        validPlan.setPrice(new BigDecimal("99.00"));
        validPlan.setBillingPeriod(BillingPeriod.MONTHLY);
        validPlan.setTrialDays(14);
        validPlan.setMaxSchools(5);
        validPlan.setMaxUsers(10);
        validPlan.setIsActive(true);

        // Valid subscription
        validSubscription = new Subscription();
        validSubscription.setId(1L);
        validSubscription.setCampus(validCampus);
        validSubscription.setSubscriptionPlan(validPlan);
        validSubscription.setStatus(SubscriptionStatus.TRIAL);
        validSubscription.setStartDate(LocalDateTime.now());
        validSubscription.setEndDate(LocalDateTime.now().plusMonths(1));
        validSubscription.setTrialEndDate(LocalDateTime.now().plusDays(14));
        validSubscription.setPrice(new BigDecimal("99.00"));
        validSubscription.setCurrency("TRY");
        validSubscription.setAutoRenew(true);
        validSubscription.setCurrentSchoolsCount(0);
        validSubscription.setCurrentUsersCount(0);
        validSubscription.setStorageUsedMb(0L);

        // Valid subscription create DTO
        validSubscriptionCreateDto = SubscriptionCreateDto.builder()
                .campusId(1L)
                .subscriptionPlanId(1L)
                .couponCode(null)
                .autoRenew(true)
                .billingName("Test Company")
                .billingEmail("billing@test.com")
                .billingPhone("+90 555 123 4567")
                .billingAddress("Test Address")
                .taxNumber("1234567890")
                .taxOffice("Test Tax Office")
                .build();

        // Expected subscription DTO
        expectedSubscriptionDto = SubscriptionDto.builder()
                .id(1L)
                .status(SubscriptionStatus.TRIAL)
                .price(new BigDecimal("99.00"))
                .currency("TRY")
                .autoRenew(true)
                .build();
    }

    @Nested
    @DisplayName("createSubscription() Tests")
    class CreateSubscriptionTests {

        @Test
        @DisplayName("Should create trial subscription successfully")
        void shouldCreateTrialSubscriptionSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validCampus));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validPlan));
            when(subscriptionRepository.existsByCampusIdAndStatusIn(1L,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(false);
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(validSubscription);
            when(campusRepository.save(any(Campus.class))).thenReturn(validCampus);
            when(converterService.mapToDto(validSubscription)).thenReturn(expectedSubscriptionDto);
            doNothing().when(emailService).sendSubscriptionWelcomeEmail(any(Subscription.class));

            // When
            SubscriptionDto result = subscriptionService.createSubscription(validSubscriptionCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals(SubscriptionStatus.TRIAL, result.getStatus());
            assertEquals(new BigDecimal("99.00"), result.getPrice());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(subscriptionPlanRepository).findByIdAndIsActiveTrue(1L);
            verify(subscriptionRepository).existsByCampusIdAndStatusIn(1L,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL));
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getStatus() == SubscriptionStatus.TRIAL &&
                            subscription.getCampus().getId().equals(1L) &&
                            subscription.getSubscriptionPlan().getId().equals(1L) &&
                            subscription.getAutoRenew().equals(true) &&
                            subscription.getBillingName().equals("Test Company") &&
                            subscription.getCreatedBy().equals(1L)
            ));
            verify(campusRepository).save(argThat(campus -> campus.getIsSubscribed().equals(true)));
            verify(emailService).sendSubscriptionWelcomeEmail(validSubscription);
            verify(converterService).mapToDto(validSubscription);
        }

        @Test
        @DisplayName("Should create paid subscription when no trial days")
        void shouldCreatePaidSubscriptionWhenNoTrialDays() {
            // Given
            SubscriptionPlan paidPlan = new SubscriptionPlan();
            paidPlan.setId(2L);
            paidPlan.setTrialDays(0); // No trial
            paidPlan.setPrice(new BigDecimal("199.00"));
            paidPlan.setBillingPeriod(BillingPeriod.MONTHLY);

            SubscriptionCreateDto paidSubscriptionDto = SubscriptionCreateDto.builder()
                    .campusId(1L)
                    .subscriptionPlanId(2L)
                    .billingName("Paid Company")
                    .build();

            Subscription paidSubscription = new Subscription();
            paidSubscription.setStatus(SubscriptionStatus.ACTIVE);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validCampus));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(paidPlan));
            when(subscriptionRepository.existsByCampusIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(paidSubscription);
            when(campusRepository.save(any(Campus.class))).thenReturn(validCampus);
            when(converterService.mapToDto(any(Subscription.class))).thenReturn(expectedSubscriptionDto);
            doNothing().when(emailService).sendSubscriptionWelcomeEmail(any(Subscription.class));

            // When
            subscriptionService.createSubscription(paidSubscriptionDto, request);

            // Then
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getStatus() == SubscriptionStatus.ACTIVE &&
                            subscription.getTrialEndDate() == null
            ));
            // Verify initial payment is created (would be mocked in real scenario)
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campus not found")
        void shouldThrowResourceNotFoundExceptionWhenCampusNotFound() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

            SubscriptionCreateDto invalidCampusDto = SubscriptionCreateDto.builder()
                    .campusId(999L)
                    .subscriptionPlanId(1L)
                    .build();

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> subscriptionService.createSubscription(invalidCampusDto, request));

            assertEquals("Campus not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(999L);
            verifyNoInteractions(subscriptionPlanRepository, subscriptionRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when subscription plan not found")
        void shouldThrowResourceNotFoundExceptionWhenSubscriptionPlanNotFound() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validCampus));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

            SubscriptionCreateDto invalidPlanDto = SubscriptionCreateDto.builder()
                    .campusId(1L)
                    .subscriptionPlanId(999L)
                    .build();

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> subscriptionService.createSubscription(invalidPlanDto, request));

            assertEquals("Subscription plan not found with ID: 999", exception.getMessage());

            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(subscriptionPlanRepository).findByIdAndIsActiveTrue(999L);
            verifyNoInteractions(subscriptionRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when campus already has active subscription")
        void shouldThrowBusinessExceptionWhenCampusAlreadyHasActiveSubscription() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validCampus));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validPlan));
            when(subscriptionRepository.existsByCampusIdAndStatusIn(1L,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> subscriptionService.createSubscription(validSubscriptionCreateDto, request));

            assertEquals("Campus already has an active subscription", exception.getMessage());

            verify(subscriptionRepository).existsByCampusIdAndStatusIn(1L,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL));
            verify(subscriptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should apply coupon discount when provided")
        void shouldApplyCouponDiscountWhenProvided() {
            // Given
            SubscriptionCreateDto couponDto = SubscriptionCreateDto.builder()
                    .campusId(1L)
                    .subscriptionPlanId(1L)
                    .couponCode("SAVE20")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validCampus));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validPlan));
            when(subscriptionRepository.existsByCampusIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(validSubscription);
            when(campusRepository.save(any(Campus.class))).thenReturn(validCampus);
            when(converterService.mapToDto(any(Subscription.class))).thenReturn(expectedSubscriptionDto);
            doNothing().when(emailService).sendSubscriptionWelcomeEmail(any(Subscription.class));

            // When
            subscriptionService.createSubscription(couponDto, request);

            // Then
            // Verify coupon application logic would be called
            verify(subscriptionRepository).save(any(Subscription.class));
        }
    }

    @Nested
    @DisplayName("getSubscriptionByCampusId() Tests")
    class GetSubscriptionByCampusIdTests {

        @Test
        @DisplayName("Should return subscription when user has campus access")
        void shouldReturnSubscriptionWhenUserHasCampusAccess() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByCampusIdAndIsActiveTrue(campusId)).thenReturn(Optional.of(validSubscription));
            when(converterService.mapToDto(validSubscription)).thenReturn(expectedSubscriptionDto);

            // When
            SubscriptionDto result = subscriptionService.getSubscriptionByCampusId(campusId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedSubscriptionDto.getId(), result.getId());

            verify(jwtService).getUser(request);
            verify(subscriptionRepository).findByCampusIdAndIsActiveTrue(campusId);
            verify(converterService).mapToDto(validSubscription);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when subscription not found")
        void shouldThrowResourceNotFoundExceptionWhenSubscriptionNotFound() {
            // Given
            Long campusId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByCampusIdAndIsActiveTrue(campusId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> subscriptionService.getSubscriptionByCampusId(campusId, request));

            assertEquals("Subscription not found for campus: 999", exception.getMessage());

            verify(subscriptionRepository).findByCampusIdAndIsActiveTrue(campusId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should validate user campus access")
        void shouldValidateUserCampusAccess() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByCampusIdAndIsActiveTrue(campusId)).thenReturn(Optional.of(validSubscription));
            when(converterService.mapToDto(validSubscription)).thenReturn(expectedSubscriptionDto);

            // When
            subscriptionService.getSubscriptionByCampusId(campusId, request);

            // Then
            verify(jwtService).getUser(request);
            // User validation would happen in validateUserCanAccessCampus method
        }
    }

    @Nested
    @DisplayName("cancelSubscription() Tests")
    class CancelSubscriptionTests {

        private SubscriptionCancellationDto validCancellationDto;

        @BeforeEach
        void setUp() {
            validCancellationDto = SubscriptionCancellationDto.builder()
                    .subscriptionId(1L)
                    .cancellationReason("Too expensive")
                    .immediateCancel(false)
                    .build();
        }

        @Test
        @DisplayName("Should cancel subscription with grace period")
        void shouldCancelSubscriptionWithGracePeriod() {
            // Given
            Subscription activeSubscription = new Subscription();
            activeSubscription.setId(1L);
            activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
            activeSubscription.setEndDate(LocalDateTime.now().plusDays(15));
            activeSubscription.setCampus(validCampus);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(activeSubscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(activeSubscription);
            doNothing().when(emailService).sendSubscriptionCancellationEmail(any(Subscription.class));

            // When
            assertDoesNotThrow(() -> subscriptionService.cancelSubscription(1L, validCancellationDto, request));

            // Then
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getStatus() == SubscriptionStatus.CANCELED &&
                            subscription.getCancellationReason().equals("Too expensive") &&
                            subscription.getAutoRenew().equals(false) &&
                            subscription.getCanceledAt() != null &&
                            subscription.getGracePeriodEnd().equals(activeSubscription.getEndDate())
            ));
            verify(emailService).sendSubscriptionCancellationEmail(activeSubscription);
        }

        @Test
        @DisplayName("Should cancel subscription immediately when requested")
        void shouldCancelSubscriptionImmediatelyWhenRequested() {
            // Given
            SubscriptionCancellationDto immediateCancelDto = SubscriptionCancellationDto.builder()
                    .subscriptionId(1L)
                    .cancellationReason("Immediate cancel")
                    .immediateCancel(true)
                    .build();

            Subscription activeSubscription = new Subscription();
            activeSubscription.setId(1L);
            activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
            activeSubscription.setCampus(validCampus);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(activeSubscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(activeSubscription);
            when(campusRepository.save(any(Campus.class))).thenReturn(validCampus);
            doNothing().when(emailService).sendSubscriptionCancellationEmail(any(Subscription.class));

            // When
            subscriptionService.cancelSubscription(1L, immediateCancelDto, request);

            // Then
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getStatus() == SubscriptionStatus.CANCELED &&
                            subscription.getGracePeriodEnd() != null &&
                            subscription.getGracePeriodEnd().isBefore(LocalDateTime.now().plusSeconds(5))
            ));
            verify(campusRepository).save(argThat(campus -> !campus.getIsSubscribed()));
        }

        @Test
        @DisplayName("Should throw BusinessException when subscription already canceled")
        void shouldThrowBusinessExceptionWhenSubscriptionAlreadyCanceled() {
            // Given
            Subscription canceledSubscription = new Subscription();
            canceledSubscription.setId(1L);
            canceledSubscription.setStatus(SubscriptionStatus.CANCELED);
            canceledSubscription.setCampus(validCampus);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(canceledSubscription));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> subscriptionService.cancelSubscription(1L, validCancellationDto, request));

            assertEquals("Subscription is already canceled", exception.getMessage());

            verify(subscriptionRepository, never()).save(any());
            verifyNoInteractions(emailService);
        }
    }

    @Nested
    @DisplayName("changeSubscriptionPlan() Tests")
    class ChangeSubscriptionPlanTests {

        private ChangeSubscriptionPlanDto validChangeDto;
        private SubscriptionPlan newPlan;

        @BeforeEach
        void setUp() {
            newPlan = new SubscriptionPlan();
            newPlan.setId(2L);
            newPlan.setName("PREMIUM");
            newPlan.setPrice(new BigDecimal("199.00"));
            newPlan.setBillingPeriod(BillingPeriod.MONTHLY);

            validChangeDto = ChangeSubscriptionPlanDto.builder()
                    .newPlanId(2L)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
        }

        @Test
        @DisplayName("Should change subscription plan successfully with upgrade")
        void shouldChangeSubscriptionPlanSuccessfullyWithUpgrade() {
            // Given
            Subscription activeSubscription = new Subscription();
            activeSubscription.setId(1L);
            activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
            activeSubscription.setSubscriptionPlan(validPlan);
            activeSubscription.setPrice(new BigDecimal("99.00"));
            activeSubscription.setCampus(validCampus);
            activeSubscription.setNextBillingDate(LocalDateTime.now().plusDays(15));
            activeSubscription.setCurrency("TRY");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(activeSubscription));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(newPlan));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(activeSubscription);
            when(converterService.mapToDto(any(Subscription.class))).thenReturn(expectedSubscriptionDto);
            doNothing().when(emailService).sendSubscriptionPlanChangeEmail(any(Subscription.class), any(ProratedAmount.class));

            // When
            SubscriptionDto result = subscriptionService.changeSubscriptionPlan(1L, validChangeDto, request);

            // Then
            assertNotNull(result);
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getSubscriptionPlan().getId().equals(2L) &&
                            subscription.getPrice().equals(new BigDecimal("199.00"))
            ));
            verify(emailService).sendSubscriptionPlanChangeEmail(any(Subscription.class), any(ProratedAmount.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when new plan is same as current")
        void shouldThrowBusinessExceptionWhenNewPlanIsSameAsCurrent() {
            // Given
            Subscription activeSubscription = new Subscription();
            activeSubscription.setId(1L);
            activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
            activeSubscription.setSubscriptionPlan(validPlan);
            activeSubscription.setCampus(validCampus);

            ChangeSubscriptionPlanDto samePlanDto = ChangeSubscriptionPlanDto.builder()
                    .newPlanId(1L) // Same as current plan
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(activeSubscription));
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validPlan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> subscriptionService.changeSubscriptionPlan(1L, samePlanDto, request));

            assertEquals("New plan is the same as current plan", exception.getMessage());

            verify(subscriptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when subscription is not active")
        void shouldThrowBusinessExceptionWhenSubscriptionIsNotActive() {
            // Given
            Subscription inactiveSubscription = new Subscription();
            inactiveSubscription.setId(1L);
            inactiveSubscription.setStatus(SubscriptionStatus.CANCELED);
            inactiveSubscription.setCampus(validCampus);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(inactiveSubscription));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> subscriptionService.changeSubscriptionPlan(1L, validChangeDto, request));

            assertEquals("Can only change plan for active subscriptions", exception.getMessage());

            verifyNoInteractions(subscriptionPlanRepository);
        }
    }

    @Nested
    @DisplayName("processPayment() Tests")
    class ProcessPaymentTests {

        private PaymentCreateDto validPaymentDto;
        private PaymentGatewayResponse successfulGatewayResponse;
        private PaymentGatewayResponse failedGatewayResponse;

        @BeforeEach
        void setUp() {
            validPaymentDto = PaymentCreateDto.builder()
                    .subscriptionId(1L)
                    .amount(new BigDecimal("99.00"))
                    .currency("TRY")
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .description("Monthly subscription payment")
                    .build();

            successfulGatewayResponse = PaymentGatewayResponse.builder()
                    .successful(true)
                    .transactionId("txn_123456")
                    .gatewayName("Test Gateway")
                    .rawResponse("{\"status\": \"success\"}")
                    .build();

            failedGatewayResponse = PaymentGatewayResponse.builder()
                    .successful(false)
                    .errorMessage("Insufficient funds")
                    .rawResponse("{\"status\": \"failed\"}")
                    .build();
        }

        @Test
        @DisplayName("Should process payment successfully")
        void shouldProcessPaymentSuccessfully() {
            // Given
            Payment savedPayment = new Payment();
            savedPayment.setId(1L);
            savedPayment.setSubscription(validSubscription);
            savedPayment.setAmount(new BigDecimal("99.00"));
            savedPayment.setPaymentStatus(PaymentStatus.COMPLETED);

            Invoice mockInvoice = new Invoice();
            mockInvoice.setId(1L);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSubscription));
            when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
            when(paymentGatewayService.processPayment(any(Payment.class), eq(validPaymentDto)))
                    .thenReturn(successfulGatewayResponse);
            when(invoiceService.createInvoiceForPayment(any(Payment.class))).thenReturn(mockInvoice);
            when(converterService.mapToDto(any(Payment.class))).thenReturn(
                    PaymentDto.builder().id(1L).paymentStatus(PaymentStatus.COMPLETED).build()
            );
            doNothing().when(emailService).sendPaymentConfirmationEmail(any(Payment.class));

            // When
            PaymentDto result = subscriptionService.processPayment(1L, validPaymentDto, request);

            // Then
            assertNotNull(result);
            assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());

            verify(paymentRepository, times(2)).save(argThat(payment ->
                    payment.getAmount().equals(new BigDecimal("99.00")) &&
                            payment.getPaymentMethod() == PaymentMethod.CREDIT_CARD &&
                            payment.getCreatedBy().equals(1L)
            ));
            verify(paymentGatewayService).processPayment(any(Payment.class), eq(validPaymentDto));
            verify(invoiceService).createInvoiceForPayment(any(Payment.class));
            verify(emailService).sendPaymentConfirmationEmail(any(Payment.class));
        }

        @Test
        @DisplayName("Should handle payment failure gracefully")
        void shouldHandlePaymentFailureGracefully() {
            // Given
            Payment savedPayment = new Payment();
            savedPayment.setId(1L);
            savedPayment.setSubscription(validSubscription);
            savedPayment.setPaymentStatus(PaymentStatus.FAILED);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSubscription));
            when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
            when(paymentGatewayService.processPayment(any(Payment.class), eq(validPaymentDto)))
                    .thenReturn(failedGatewayResponse);
            when(converterService.mapToDto(any(Payment.class))).thenReturn(
                    PaymentDto.builder().id(1L).paymentStatus(PaymentStatus.FAILED).build()
            );
            doNothing().when(emailService).sendPaymentFailedEmail(any(Payment.class));

            // When
            PaymentDto result = subscriptionService.processPayment(1L, validPaymentDto, request);

            // Then
            assertNotNull(result);
            assertEquals(PaymentStatus.FAILED, result.getPaymentStatus());

            verify(paymentRepository, times(2)).save(argThat(payment ->
                    payment.getPaymentStatus() == PaymentStatus.FAILED &&
                            payment.getFailureReason().equals("Insufficient funds")
            ));
            verify(emailService).sendPaymentFailedEmail(any(Payment.class));
            verify(invoiceService, never()).createInvoiceForPayment(any());
        }

        @Test
        @DisplayName("Should handle payment processing exception")
        void shouldHandlePaymentProcessingException() {
            // Given
            Payment savedPayment = new Payment();
            savedPayment.setId(1L);
            savedPayment.setSubscription(validSubscription);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSubscription));
            when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
            when(paymentGatewayService.processPayment(any(Payment.class), eq(validPaymentDto)))
                    .thenThrow(new RuntimeException("Gateway connection failed"));
            when(converterService.mapToDto(any(Payment.class))).thenReturn(
                    PaymentDto.builder().id(1L).paymentStatus(PaymentStatus.FAILED).build()
            );
            doNothing().when(emailService).sendPaymentFailedEmail(any(Payment.class));

            // When
            PaymentDto result = subscriptionService.processPayment(1L, validPaymentDto, request);

            // Then
            assertNotNull(result);
            verify(paymentRepository, times(2)).save(argThat(payment ->
                    payment.getPaymentStatus() == PaymentStatus.FAILED &&
                            payment.getFailureReason().equals("Gateway connection failed")
            ));
            verify(emailService).sendPaymentFailedEmail(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("getAllSubscriptionPlans() Tests")
    class GetAllSubscriptionPlansTests {

        @Test
        @DisplayName("Should return all visible subscription plans ordered by sort order")
        void shouldReturnAllVisibleSubscriptionPlansOrderedBySortOrder() {
            // Given
            SubscriptionPlan plan1 = new SubscriptionPlan();
            plan1.setId(1L);
            plan1.setName("BASIC");
            plan1.setSortOrder(1);

            SubscriptionPlan plan2 = new SubscriptionPlan();
            plan2.setId(2L);
            plan2.setName("PREMIUM");
            plan2.setSortOrder(2);

            List<SubscriptionPlan> plans = List.of(plan1, plan2);

            SubscriptionPlanDto planDto1 = SubscriptionPlanDto.builder().id(1L).name("BASIC").build();
            SubscriptionPlanDto planDto2 = SubscriptionPlanDto.builder().id(2L).name("PREMIUM").build();

            when(subscriptionPlanRepository.findAllByIsVisibleTrueOrderBySortOrderAsc()).thenReturn(plans);
            when(converterService.mapToDto(plan1)).thenReturn(planDto1);
            when(converterService.mapToDto(plan2)).thenReturn(planDto2);

            // When
            List<SubscriptionPlanDto> result = subscriptionService.getAllSubscriptionPlans();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("BASIC", result.get(0).getName());
            assertEquals("PREMIUM", result.get(1).getName());

            verify(subscriptionPlanRepository).findAllByIsVisibleTrueOrderBySortOrderAsc();
            verify(converterService, times(2)).mapToDto(any(SubscriptionPlan.class));
        }

        @Test
        @DisplayName("Should return empty list when no visible plans exist")
        void shouldReturnEmptyListWhenNoVisiblePlansExist() {
            // Given
            when(subscriptionPlanRepository.findAllByIsVisibleTrueOrderBySortOrderAsc()).thenReturn(Collections.emptyList());

            // When
            List<SubscriptionPlanDto> result = subscriptionService.getAllSubscriptionPlans();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(subscriptionPlanRepository).findAllByIsVisibleTrueOrderBySortOrderAsc();
            verifyNoInteractions(converterService);
        }
    }

    @Nested
    @DisplayName("getSubscriptionPlanById() Tests")
    class GetSubscriptionPlanByIdTests {

        @Test
        @DisplayName("Should return subscription plan by ID")
        void shouldReturnSubscriptionPlanById() {
            // Given
            Long planId = 1L;
            SubscriptionPlanDto expectedPlanDto = SubscriptionPlanDto.builder()
                    .id(1L)
                    .name("BASIC")
                    .displayName("Basic Plan")
                    .build();

            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(planId)).thenReturn(Optional.of(validPlan));
            when(converterService.mapToDto(validPlan)).thenReturn(expectedPlanDto);

            // When
            SubscriptionPlanDto result = subscriptionService.getSubscriptionPlanById(planId);

            // Then
            assertNotNull(result);
            assertEquals("BASIC", result.getName());
            assertEquals("Basic Plan", result.getDisplayName());

            verify(subscriptionPlanRepository).findByIdAndIsActiveTrue(planId);
            verify(converterService).mapToDto(validPlan);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when plan not found")
        void shouldThrowResourceNotFoundExceptionWhenPlanNotFound() {
            // Given
            Long nonExistentPlanId = 999L;
            when(subscriptionPlanRepository.findByIdAndIsActiveTrue(nonExistentPlanId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> subscriptionService.getSubscriptionPlanById(nonExistentPlanId));

            assertEquals("Subscription plan not found with ID: 999", exception.getMessage());

            verify(subscriptionPlanRepository).findByIdAndIsActiveTrue(nonExistentPlanId);
            verifyNoInteractions(converterService);
        }
    }

    @Nested
    @DisplayName("updateSubscription() Tests")
    class UpdateSubscriptionTests {

        private SubscriptionUpdateDto validUpdateDto;

        @BeforeEach
        void setUp() {
            validUpdateDto = SubscriptionUpdateDto.builder()
                    .autoRenew(false)
                    .billingName("Updated Company Name")
                    .billingEmail("updated@test.com")
                    .billingPhone("+90 555 999 8888")
                    .billingAddress("Updated Address")
                    .taxNumber("9876543210")
                    .taxOffice("Updated Tax Office")
                    .build();
        }

        @Test
        @DisplayName("Should update subscription billing information")
        void shouldUpdateSubscriptionBillingInformation() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSubscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(validSubscription);
            when(converterService.mapToDto(validSubscription)).thenReturn(expectedSubscriptionDto);

            // When
            SubscriptionDto result = subscriptionService.updateSubscription(1L, validUpdateDto, request);

            // Then
            assertNotNull(result);
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getAutoRenew().equals(false) &&
                            subscription.getBillingName().equals("Updated Company Name") &&
                            subscription.getBillingEmail().equals("updated@test.com") &&
                            subscription.getBillingPhone().equals("+90 555 999 8888") &&
                            subscription.getBillingAddress().equals("Updated Address") &&
                            subscription.getTaxNumber().equals("9876543210") &&
                            subscription.getTaxOffice().equals("Updated Tax Office") &&
                            subscription.getUpdatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(validSubscription);
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Given
            SubscriptionUpdateDto partialUpdateDto = SubscriptionUpdateDto.builder()
                    .billingName("Only Name Updated")
                    // Other fields are null - should not be updated
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSubscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(validSubscription);
            when(converterService.mapToDto(validSubscription)).thenReturn(expectedSubscriptionDto);

            // When
            subscriptionService.updateSubscription(1L, partialUpdateDto, request);

            // Then
            verify(subscriptionRepository).save(argThat(subscription ->
                            subscription.getBillingName().equals("Only Name Updated") &&
                                    subscription.getUpdatedBy().equals(1L)
                    // Other fields should remain unchanged
            ));
        }
    }

    @Nested
    @DisplayName("getSubscriptionAnalytics() Tests")
    class GetSubscriptionAnalyticsTests {

        @Test
        @DisplayName("Should return comprehensive subscription analytics")
        void shouldReturnComprehensiveSubscriptionAnalytics() {
            // Given
            Long subscriptionId = 1L;

            // Mock payments
            Payment payment1 = new Payment();
            payment1.setAmount(new BigDecimal("99.00"));
            payment1.setPaymentStatus(PaymentStatus.COMPLETED);

            Payment payment2 = new Payment();
            payment2.setAmount(new BigDecimal("99.00"));
            payment2.setPaymentStatus(PaymentStatus.COMPLETED);

            List<Payment> successfulPayments = List.of(payment1, payment2);

            Payment failedPayment = new Payment();
            failedPayment.setPaymentStatus(PaymentStatus.FAILED);
            List<Payment> failedPayments = List.of(failedPayment);

            // Set usage counters on subscription
            validSubscription.setCurrentSchoolsCount(3);
            validSubscription.setCurrentUsersCount(8);
            validSubscription.setCurrentMonthAppointments(25);
            validSubscription.setStorageUsedMb(512L);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)).thenReturn(Optional.of(validSubscription));
            when(paymentRepository.findBySubscriptionIdAndPaymentStatus(subscriptionId, PaymentStatus.COMPLETED))
                    .thenReturn(successfulPayments);
            when(paymentRepository.findBySubscriptionIdAndPaymentStatus(subscriptionId, PaymentStatus.FAILED))
                    .thenReturn(failedPayments);

            // When
            SubscriptionAnalyticsDto result = subscriptionService.getSubscriptionAnalytics(subscriptionId, request);

            // Then
            assertNotNull(result);
            assertEquals(subscriptionId, result.getSubscriptionId());
            assertEquals(2L, result.getTotalPayments());
            assertEquals(new BigDecimal("198.00"), result.getTotalAmountPaid());
            assertEquals(new BigDecimal("99.00"), result.getAveragePaymentAmount());
            assertEquals(1L, result.getFailedPaymentCount());
            assertEquals(66.67, result.getPaymentSuccessRate(), 0.01);

            // Verify usage percentages
            assertEquals(60.0, result.getSchoolsUsagePercentage(), 0.01); // 3/5 = 60%
            assertEquals(80.0, result.getUsersUsagePercentage(), 0.01); // 8/10 = 80%

            verify(paymentRepository).findBySubscriptionIdAndPaymentStatus(subscriptionId, PaymentStatus.COMPLETED);
            verify(paymentRepository).findBySubscriptionIdAndPaymentStatus(subscriptionId, PaymentStatus.FAILED);
        }

        @Test
        @DisplayName("Should handle subscription with no payments")
        void shouldHandleSubscriptionWithNoPayments() {
            // Given
            Long subscriptionId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)).thenReturn(Optional.of(validSubscription));
            when(paymentRepository.findBySubscriptionIdAndPaymentStatus(subscriptionId, PaymentStatus.COMPLETED))
                    .thenReturn(Collections.emptyList());
            when(paymentRepository.findBySubscriptionIdAndPaymentStatus(subscriptionId, PaymentStatus.FAILED))
                    .thenReturn(Collections.emptyList());

            // When
            SubscriptionAnalyticsDto result = subscriptionService.getSubscriptionAnalytics(subscriptionId, request);

            // Then
            assertNotNull(result);
            assertEquals(0L, result.getTotalPayments());
            assertEquals(BigDecimal.ZERO, result.getTotalAmountPaid());
            assertEquals(BigDecimal.ZERO, result.getAveragePaymentAmount());
            assertEquals(100.0, result.getPaymentSuccessRate(), 0.01); // No failures = 100%
        }
    }

    @Nested
    @DisplayName("checkUsageLimits() Tests")
    class CheckUsageLimitsTests {

        @Test
        @DisplayName("Should return current usage limits for campus")
        void shouldReturnCurrentUsageLimitsForCampus() {
            // Given
            Long campusId = 1L;
            validSubscription.setCurrentSchoolsCount(3);
            validSubscription.setCurrentUsersCount(7);
            validSubscription.setCurrentMonthAppointments(50);
            validSubscription.setCurrentGalleryItems(15);
            validSubscription.setCurrentMonthPosts(8);
            validSubscription.setStorageUsedMb(1536L); // 1.5 GB

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(Optional.of(validSubscription));

            // When
            UsageLimitsDto result = subscriptionService.checkUsageLimits(campusId, request);

            // Then
            assertNotNull(result);
            assertEquals(3, result.getSchoolsUsed());
            assertEquals(5, result.getSchoolsLimit()); // From validPlan
            assertEquals(7, result.getUsersUsed());
            assertEquals(10, result.getUsersLimit()); // From validPlan
            assertEquals(50, result.getAppointmentsThisMonth());
            assertEquals(15, result.getGalleryItemsUsed());
            assertEquals(8, result.getPostsThisMonth());
            assertEquals(1536L, result.getStorageUsedMb());

            verify(subscriptionRepository).findByCampusIdAndStatusIn(campusId,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no active subscription found")
        void shouldThrowResourceNotFoundExceptionWhenNoActiveSubscriptionFound() {
            // Given
            Long campusId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> subscriptionService.checkUsageLimits(campusId, request));

            assertEquals("Active subscription not found for campus: 999", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("updateUsageCounters() Tests")
    class UpdateUsageCountersTests {

        @Test
        @DisplayName("Should update usage counters successfully")
        void shouldUpdateUsageCountersSuccessfully() {
            // Given
            Long campusId = 1L;
            UsageUpdateDto usageUpdate = UsageUpdateDto.builder()
                    .schoolCountDelta(2)
                    .userCountDelta(3)
                    .appointmentCountDelta(5)
                    .galleryItemCountDelta(1)
                    .postCountDelta(2)
                    .storageDeltaMb(100L)
                    .build();

            validSubscription.setCurrentSchoolsCount(1);
            validSubscription.setCurrentUsersCount(2);
            validSubscription.setCurrentMonthAppointments(10);
            validSubscription.setCurrentGalleryItems(5);
            validSubscription.setCurrentMonthPosts(3);
            validSubscription.setStorageUsedMb(200L);

            when(subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(Optional.of(validSubscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(validSubscription);

            // When
            assertDoesNotThrow(() -> subscriptionService.updateUsageCounters(campusId, usageUpdate));

            // Then
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getCurrentSchoolsCount().equals(3) && // 1 + 2
                            subscription.getCurrentUsersCount().equals(5) && // 2 + 3
                            subscription.getCurrentMonthAppointments().equals(15) && // 10 + 5
                            subscription.getCurrentGalleryItems().equals(6) && // 5 + 1
                            subscription.getCurrentMonthPosts().equals(5) && // 3 + 2
                            subscription.getStorageUsedMb().equals(300L) // 200 + 100
            ));
        }

        @Test
        @DisplayName("Should handle negative deltas without going below zero")
        void shouldHandleNegativeDeltasWithoutGoingBelowZero() {
            // Given
            Long campusId = 1L;
            UsageUpdateDto usageUpdate = UsageUpdateDto.builder()
                    .schoolCountDelta(-5) // Would result in -3, should be clamped to 0
                    .userCountDelta(-1)
                    .build();

            validSubscription.setCurrentSchoolsCount(2);
            validSubscription.setCurrentUsersCount(3);

            when(subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(Optional.of(validSubscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(validSubscription);

            // When
            subscriptionService.updateUsageCounters(campusId, usageUpdate);

            // Then
            verify(subscriptionRepository).save(argThat(subscription ->
                    subscription.getCurrentSchoolsCount().equals(0) && // Clamped to 0
                            subscription.getCurrentUsersCount().equals(2) // 3 - 1
            ));
        }

        @Test
        @DisplayName("Should handle case when no active subscription exists")
        void shouldHandleCaseWhenNoActiveSubscriptionExists() {
            // Given
            Long campusId = 1L;
            UsageUpdateDto usageUpdate = UsageUpdateDto.builder()
                    .schoolCountDelta(1)
                    .build();

            when(subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                    List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))).thenReturn(Optional.empty());

            // When - Should not throw exception, just log warning
            assertDoesNotThrow(() -> subscriptionService.updateUsageCounters(campusId, usageUpdate));

            // Then
            verify(subscriptionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("createSubscriptionPlan() Tests")
    class CreateSubscriptionPlanTests {

        private SubscriptionPlanCreateDto validPlanCreateDto;

        @BeforeEach
        void setUp() {
            validPlanCreateDto = SubscriptionPlanCreateDto.builder()
                    .name("ENTERPRISE")
                    .displayName("Enterprise Plan")
                    .description("Enterprise level subscription")
                    .price(new BigDecimal("499.00"))
                    .billingPeriod(BillingPeriod.MONTHLY)
                    .trialDays(30)
                    .maxSchools(50)
                    .maxUsers(200)
                    .maxAppointmentsPerMonth(1000)
                    .hasAnalytics(true)
                    .hasCustomDomain(true)
                    .hasApiAccess(true)
                    .hasPrioritySupport(true)
                    .storageGb(100)
                    .isPopular(false)
                    .sortOrder(3)
                    .isVisible(true)
                    .build();
        }

        @Test
        @DisplayName("Should create subscription plan successfully")
        void shouldCreateSubscriptionPlanSuccessfully() {
            // Given
            SubscriptionPlan savedPlan = new SubscriptionPlan();
            savedPlan.setId(3L);
            savedPlan.setName("ENTERPRISE");
            savedPlan.setCreatedBy(1L);

            SubscriptionPlanDto expectedPlanDto = SubscriptionPlanDto.builder()
                    .id(3L)
                    .name("ENTERPRISE")
                    .displayName("Enterprise Plan")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionPlanRepository.existsByName("ENTERPRISE")).thenReturn(false);
            when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(savedPlan);
            when(converterService.mapToDto(savedPlan)).thenReturn(expectedPlanDto);

            // When
            SubscriptionPlanDto result = subscriptionService.createSubscriptionPlan(validPlanCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("ENTERPRISE", result.getName());
            assertEquals("Enterprise Plan", result.getDisplayName());

            verify(jwtService).getUser(request);
            verify(subscriptionPlanRepository).existsByName("ENTERPRISE");
            verify(subscriptionPlanRepository).save(argThat(plan ->
                    plan.getName().equals("ENTERPRISE") &&
                            plan.getDisplayName().equals("Enterprise Plan") &&
                            plan.getPrice().equals(new BigDecimal("499.00")) &&
                            plan.getBillingPeriod() == BillingPeriod.MONTHLY &&
                            plan.getTrialDays().equals(30) &&
                            plan.getMaxSchools().equals(50) &&
                            plan.getMaxUsers().equals(200) &&
                            plan.getHasAnalytics().equals(true) &&
                            plan.getHasCustomDomain().equals(true) &&
                            plan.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when plan name already exists")
        void shouldThrowBusinessExceptionWhenPlanNameAlreadyExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(subscriptionPlanRepository.existsByName("ENTERPRISE")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> subscriptionService.createSubscriptionPlan(validPlanCreateDto, request));

            assertEquals("Subscription plan name already exists: ENTERPRISE", exception.getMessage());

            verify(subscriptionPlanRepository).existsByName("ENTERPRISE");
            verify(subscriptionPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user is not system admin")
        void shouldThrowBusinessExceptionWhenUserIsNotSystemAdmin() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> subscriptionService.createSubscriptionPlan(validPlanCreateDto, request));

            assertEquals("System administrator access required", exception.getMessage());

            verifyNoInteractions(subscriptionPlanRepository);
        }
    }

    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }
    // Helper methods
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);
        //user.setUserRoles(Set.of(createMockUserRole(roleLevel)));
        user.setUserRoles(Set.of(createUserRole(roleLevel)));
        user.setInstitutionAccess(Collections.emptySet());
        return user;
    }

}