package com.genixo.education.search.service;


import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.subscription.*;
import com.genixo.education.search.entity.subscription.*;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.subscription.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.SubscriptionConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CampusRepository campusRepository;
    private final SubscriptionConverterService converterService;
    private final JwtService jwtService;
    private final PaymentGatewayService paymentGatewayService;
    private final EmailService emailService;
    private final InvoiceService invoiceService;

    // ================================ SUBSCRIPTION PLAN OPERATIONS ================================

    @Cacheable(value = "subscription_plans")
    public List<SubscriptionPlanDto> getAllSubscriptionPlans() {
        log.info("Fetching all subscription plans");

        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAllByIsVisibleTrueOrderBySortOrderAsc();
        return plans.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "subscription_plan", key = "#id")
    public SubscriptionPlanDto getSubscriptionPlanById(Long id) {
        log.info("Fetching subscription plan with ID: {}", id);

        SubscriptionPlan plan = subscriptionPlanRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + id));

        return converterService.mapToDto(plan);
    }

    // ================================ SUBSCRIPTION OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"subscriptions", "campus_subscription"}, allEntries = true)
    public SubscriptionDto createSubscription(SubscriptionCreateDto createDto, HttpServletRequest request) {
        log.info("Creating subscription for campus: {} with plan: {}", createDto.getCampusId(), createDto.getSubscriptionPlanId());

        User user = jwtService.getUser(request);
        validateUserCanManageCampus(user, createDto.getCampusId());

        // Validate campus and plan
        Campus campus = campusRepository.findByIdAndIsActiveTrue(createDto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + createDto.getCampusId()));

        SubscriptionPlan plan = subscriptionPlanRepository.findByIdAndIsActiveTrue(createDto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + createDto.getSubscriptionPlanId()));

        // Check if campus already has an active subscription
        if (subscriptionRepository.existsByCampusIdAndStatusIn(createDto.getCampusId(),
                List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))) {
            throw new BusinessException("Campus already has an active subscription");
        }

        // Calculate subscription dates
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime trialEndDate = plan.getTrialDays() > 0 ?
                startDate.plusDays(plan.getTrialDays()) : null;
        LocalDateTime endDate = calculateEndDate(startDate, plan.getBillingPeriod());
        LocalDateTime nextBillingDate = trialEndDate != null ? trialEndDate :
                calculateNextBillingDate(startDate, plan.getBillingPeriod());

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setCampus(campus);
        subscription.setSubscriptionPlan(plan);
        subscription.setStatus(plan.getTrialDays() > 0 ? SubscriptionStatus.TRIAL : SubscriptionStatus.ACTIVE);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setTrialEndDate(trialEndDate);
        subscription.setNextBillingDate(nextBillingDate);
        subscription.setPrice(plan.getPrice());
        subscription.setCurrency(createDto.getCurrency() != null ? createDto.getCurrency() : "TRY");
        subscription.setAutoRenew(createDto.getAutoRenew() != null ? createDto.getAutoRenew() : true);
        subscription.setBillingName(createDto.getBillingName());
        subscription.setBillingEmail(createDto.getBillingEmail());
        subscription.setBillingPhone(createDto.getBillingPhone());
        subscription.setBillingAddress(createDto.getBillingAddress());
        subscription.setTaxNumber(createDto.getTaxNumber());
        subscription.setTaxOffice(createDto.getTaxOffice());
        subscription.setCreatedBy(user.getId());

        // Apply discount if provided
        if (createDto.getCouponCode() != null) {
            applyDiscount(subscription, createDto.getCouponCode());
        }

        subscription = subscriptionRepository.save(subscription);

        // Update campus subscription status
        campus.setIsSubscribed(true);
        campusRepository.save(campus);

        // Create initial payment if not trial
        if (plan.getTrialDays() == 0) {
            createInitialPayment(subscription, createDto.getPaymentMethod());
        }

        // Send welcome email
        emailService.sendSubscriptionWelcomeEmail(subscription);

        log.info("Subscription created successfully with ID: {}", subscription.getId());
        return converterService.mapToDto(subscription);
    }

    @Cacheable(value = "campus_subscription", key = "#campusId")
    public SubscriptionDto getSubscriptionByCampusId(Long campusId, HttpServletRequest request) {
        log.info("Fetching subscription for campus: {}", campusId);

        User user = jwtService.getUser(request);
        validateUserCanAccessCampus(user, campusId);

        Subscription subscription = subscriptionRepository.findByCampusIdAndIsActiveTrue(campusId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found for campus: " + campusId));

        return converterService.mapToDto(subscription);
    }

    @Transactional
    @CacheEvict(value = {"subscriptions", "campus_subscription"}, allEntries = true)
    public SubscriptionDto updateSubscription(Long subscriptionId, SubscriptionUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating subscription: {}", subscriptionId);

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanManageCampus(user, subscription.getCampus().getId());

        // Update billing information
        if (updateDto.getBillingName() != null) subscription.setBillingName(updateDto.getBillingName());
        if (updateDto.getBillingEmail() != null) subscription.setBillingEmail(updateDto.getBillingEmail());
        if (updateDto.getBillingPhone() != null) subscription.setBillingPhone(updateDto.getBillingPhone());
        if (updateDto.getBillingAddress() != null) subscription.setBillingAddress(updateDto.getBillingAddress());
        if (updateDto.getTaxNumber() != null) subscription.setTaxNumber(updateDto.getTaxNumber());
        if (updateDto.getTaxOffice() != null) subscription.setTaxOffice(updateDto.getTaxOffice());
        if (updateDto.getAutoRenew() != null) subscription.setAutoRenew(updateDto.getAutoRenew());

        subscription.setUpdatedBy(user.getId());
        subscription = subscriptionRepository.save(subscription);

        log.info("Subscription updated successfully: {}", subscriptionId);
        return converterService.mapToDto(subscription);
    }

    @Transactional
    @CacheEvict(value = {"subscriptions", "campus_subscription"}, allEntries = true)
    public void cancelSubscription(Long subscriptionId, SubscriptionCancellationDto cancelDto, HttpServletRequest request) {
        log.info("Canceling subscription: {}", subscriptionId);

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanManageCampus(user, subscription.getCampus().getId());

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new BusinessException("Subscription is already canceled");
        }

        // Set cancellation details
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setCanceledAt(LocalDateTime.now());
        subscription.setCancellationReason(cancelDto.getCancellationReason());
        subscription.setAutoRenew(false);

        // Set grace period if immediate cancellation is not requested
        if (!cancelDto.getImmediateCancel()) {
            subscription.setGracePeriodEnd(subscription.getEndDate());
        } else {
            subscription.setGracePeriodEnd(LocalDateTime.now());
            subscription.getCampus().setIsSubscribed(false);
            campusRepository.save(subscription.getCampus());
        }

        subscription.setUpdatedBy(user.getId());
        subscriptionRepository.save(subscription);

        // Send cancellation email
        emailService.sendSubscriptionCancellationEmail(subscription);

        log.info("Subscription canceled successfully: {}", subscriptionId);
    }

    @Transactional
    @CacheEvict(value = {"subscriptions", "campus_subscription"}, allEntries = true)
    public SubscriptionDto changeSubscriptionPlan(Long subscriptionId, ChangeSubscriptionPlanDto changeDto, HttpServletRequest request) {
        log.info("Changing subscription plan for subscription: {} to plan: {}", subscriptionId, changeDto.getNewPlanId());

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanManageCampus(user, subscription.getCampus().getId());

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessException("Can only change plan for active subscriptions");
        }

        SubscriptionPlan newPlan = subscriptionPlanRepository.findByIdAndIsActiveTrue(changeDto.getNewPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + changeDto.getNewPlanId()));

        if (subscription.getSubscriptionPlan().getId().equals(newPlan.getId())) {
            throw new BusinessException("New plan is the same as current plan");
        }

        // Calculate prorated amount
        ProratedAmount proratedAmount = calculateProratedAmount(subscription, newPlan);

        // Update subscription
        subscription.setSubscriptionPlan(newPlan);
        subscription.setPrice(newPlan.getPrice());
        subscription.setEndDate(calculateEndDate(LocalDateTime.now(), newPlan.getBillingPeriod()));
        subscription.setNextBillingDate(calculateNextBillingDate(LocalDateTime.now(), newPlan.getBillingPeriod()));
        subscription.setUpdatedBy(user.getId());

        subscription = subscriptionRepository.save(subscription);

        // Create payment for prorated amount if needed
        if (proratedAmount.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            createProratedPayment(subscription, proratedAmount, changeDto.getPaymentMethod());
        } else if (proratedAmount.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            // Create credit for downgrade
            createCredit(subscription, proratedAmount.getAmount().negate());
        }

        // Send plan change email
        emailService.sendSubscriptionPlanChangeEmail(subscription, proratedAmount);

        log.info("Subscription plan changed successfully for subscription: {}", subscriptionId);
        return converterService.mapToDto(subscription);
    }

    // ================================ PAYMENT OPERATIONS ================================


    private void createProratedPayment(Subscription subscription, ProratedAmount proratedAmount, PaymentMethod paymentMethod) {
        log.info("Creating prorated payment for subscription: {}", subscription.getId());

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setPaymentReference(generatePaymentReference());
        payment.setAmount(proratedAmount.getAmount());
        payment.setCurrency(subscription.getCurrency());
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Default
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDescription("Prorated payment for plan change");
        payment.setPeriodStart(LocalDateTime.now());
        payment.setPeriodEnd(subscription.getEndDate());

        paymentRepository.save(payment);
    }

    private void createCredit(Subscription subscription, BigDecimal creditAmount) {
        log.info("Creating credit for subscription: {}, amount: {}", subscription.getId(), creditAmount);
        // Implementation for creating credit/refund
    }

    private void updateSubscriptionAfterPayment(Subscription subscription) {
        log.info("Updating subscription after successful payment: {}", subscription.getId());

        // Update subscription status if it was past due
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.getCampus().setIsSubscribed(true);
            campusRepository.save(subscription.getCampus());
        }

        // Update next billing date
        subscription.setNextBillingDate(calculateNextBillingDate(
                subscription.getNextBillingDate(),
                subscription.getSubscriptionPlan().getBillingPeriod()));

        subscriptionRepository.save(subscription);
    }

    private void processRecurringBilling(Subscription subscription) {
        log.info("Processing recurring billing for subscription: {}", subscription.getId());

        try {
            // Create payment record
            Payment payment = new Payment();
            payment.setSubscription(subscription);
            payment.setPaymentReference(generatePaymentReference());
            payment.setAmount(subscription.getPrice());
            payment.setCurrency(subscription.getCurrency());
            payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Use stored payment method
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setDescription("Recurring subscription payment");
            payment.setDueDate(subscription.getNextBillingDate());
            payment.setPeriodStart(subscription.getNextBillingDate().minusMonths(1));
            payment.setPeriodEnd(subscription.getNextBillingDate());

            payment = paymentRepository.save(payment);

            // Process payment through gateway
            PaymentGatewayResponse gatewayResponse = paymentGatewayService.processRecurringPayment(payment);

            if (gatewayResponse.isSuccessful()) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setExternalPaymentId(gatewayResponse.getTransactionId());
                payment.setGatewayResponse(gatewayResponse.getRawResponse());

                // Update subscription
                updateSubscriptionAfterPayment(subscription);

                // Create invoice
                Invoice invoice = invoiceService.createInvoiceForPayment(payment);
                payment.setInvoice(invoice);

                log.info("Recurring payment processed successfully: {}", payment.getId());

                // Send payment confirmation
                emailService.sendPaymentConfirmationEmail(payment);

            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setFailureReason(gatewayResponse.getErrorMessage());

                // Handle failed payment
                handleFailedRecurringPayment(subscription, payment);

                log.warn("Recurring payment failed for subscription: {} - {}",
                        subscription.getId(), gatewayResponse.getErrorMessage());
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Error processing recurring billing for subscription: {}", subscription.getId(), e);
            handleRecurringBillingError(subscription, e);
        }
    }

    private void handleFailedRecurringPayment(Subscription subscription, Payment failedPayment) {
        log.info("Handling failed recurring payment for subscription: {}", subscription.getId());

        // Set subscription to past due
        subscription.setStatus(SubscriptionStatus.PAST_DUE);

        // Set grace period (e.g., 7 days)
        subscription.setGracePeriodEnd(LocalDateTime.now().plusDays(7));

        subscriptionRepository.save(subscription);

        // Send payment failed notification
        emailService.sendPaymentFailedEmail(failedPayment);

        // Schedule retry attempts
        schedulePaymentRetry(subscription, 1);
    }

    private void schedulePaymentRetry(Subscription subscription, int attemptNumber) {
        // Implementation for scheduling payment retry
        log.info("Scheduling payment retry #{} for subscription: {}", attemptNumber, subscription.getId());
    }

    private void handleRecurringBillingError(Subscription subscription, Exception error) {
        log.error("Recurring billing error for subscription: {}", subscription.getId(), error);

        // Set subscription status to pending if not already past due
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscriptionRepository.save(subscription);
        }

        // Notify administrators
        emailService.sendBillingErrorNotification(subscription, error);
    }

    private void handleExpiredSubscription(Subscription subscription) {
        log.info("Handling expired subscription: {}", subscription.getId());

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            // For canceled subscriptions, check if grace period has ended
            if (subscription.getGracePeriodEnd() != null &&
                    subscription.getGracePeriodEnd().isBefore(LocalDateTime.now())) {

                // Disable campus subscription
                subscription.getCampus().setIsSubscribed(false);
                campusRepository.save(subscription.getCampus());

                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);

                log.info("Subscription expired and campus disabled: {}", subscription.getId());
            }
        } else if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            // For past due subscriptions, check if grace period has ended
            if (subscription.getGracePeriodEnd() != null &&
                    subscription.getGracePeriodEnd().isBefore(LocalDateTime.now())) {

                // Suspend subscription
                subscription.setStatus(SubscriptionStatus.SUSPENDED);
                subscription.getCampus().setIsSubscribed(false);
                campusRepository.save(subscription.getCampus());
                subscriptionRepository.save(subscription);

                // Send suspension notification
                emailService.sendSubscriptionSuspensionEmail(subscription);

                log.info("Subscription suspended due to non-payment: {}", subscription.getId());
            }
        }
    }

    // ================================ INVOICE OPERATIONS ================================

    public Page<InvoiceDto> getInvoices(Long subscriptionId, int page, int size, HttpServletRequest request) {
        log.info("Fetching invoices for subscription: {}", subscriptionId);

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanAccessCampus(user, subscription.getCampus().getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDate"));
        Page<Invoice> invoices = invoiceRepository.findBySubscriptionIdOrderByInvoiceDateDesc(subscriptionId, pageable);

        return invoices.map(converterService::mapToDto);
    }

    public byte[] downloadInvoice(Long invoiceId, HttpServletRequest request) {
        log.info("Downloading invoice: {}", invoiceId);

        User user = jwtService.getUser(request);
        Invoice invoice = invoiceRepository.findByIdAndIsActiveTrue(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        validateUserCanAccessCampus(user, invoice.getSubscription().getCampus().getId());

        // Generate PDF if not exists
        if (invoice.getPdfFileUrl() == null) {
            invoiceService.generateInvoicePdf(invoice);
        }

        return invoiceService.downloadInvoicePdf(invoice);
    }

    // ================================ SUBSCRIPTION ANALYTICS ================================

    @Cacheable(value = "subscription_analytics", key = "#subscriptionId")
    public SubscriptionAnalyticsDto getSubscriptionAnalytics(Long subscriptionId, HttpServletRequest request) {
        log.info("Fetching analytics for subscription: {}", subscriptionId);

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanAccessCampus(user, subscription.getCampus().getId());

        // Calculate analytics
        List<Payment> payments = paymentRepository.findBySubscriptionIdAndPaymentStatus(
                subscriptionId, PaymentStatus.COMPLETED);

        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long paymentCount = payments.size();
        BigDecimal averagePayment = paymentCount > 0 ?
                totalPaid.divide(BigDecimal.valueOf(paymentCount), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

        List<Payment> failedPayments = paymentRepository.findBySubscriptionIdAndPaymentStatus(
                subscriptionId, PaymentStatus.FAILED);

        double paymentSuccessRate = (paymentCount + failedPayments.size()) > 0 ?
                (double) paymentCount / (paymentCount + failedPayments.size()) * 100 : 100.0;

        // Usage analytics
        SubscriptionPlan plan = subscription.getSubscriptionPlan();

        return SubscriptionAnalyticsDto.builder()
                .subscriptionId(subscriptionId)
                .totalPayments(paymentCount)
                .totalAmountPaid(totalPaid)
                .averagePaymentAmount(averagePayment)
                .paymentSuccessRate(paymentSuccessRate)
                .failedPaymentCount((long) failedPayments.size())
                .schoolsUsagePercentage(calculateUsagePercentage(subscription.getCurrentSchoolsCount(), plan.getMaxSchools()))
                .usersUsagePercentage(calculateUsagePercentage(subscription.getCurrentUsersCount(), plan.getMaxUsers()))
                .appointmentsUsagePercentage(calculateUsagePercentage(subscription.getCurrentMonthAppointments(), plan.getMaxAppointmentsPerMonth()))
                .storageUsagePercentage(calculateStorageUsagePercentage(subscription.getStorageUsedMb(), plan.getStorageGb()))
                .daysSinceStart(ChronoUnit.DAYS.between(subscription.getStartDate(), LocalDateTime.now()))
                .daysUntilRenewal(ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getNextBillingDate()))
                .build();
    }

    // ================================ ADMIN OPERATIONS ================================

    public Page<SubscriptionDto> getAllSubscriptions(SubscriptionFilterDto filter, int page, int size, HttpServletRequest request) {
        log.info("Admin fetching all subscriptions");

        User user = jwtService.getUser(request);
        validateUserIsSystemAdmin(user);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Subscription> subscriptions = subscriptionRepository.findAll(
                SubscriptionSpecifications.withFilters(filter), pageable
        );

        return subscriptions.map(converterService::mapToDto);
    }

    @Transactional
    @CacheEvict(value = "subscription_plans", allEntries = true)
    public SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanCreateDto createDto, HttpServletRequest request) {
        log.info("Creating subscription plan: {}", createDto.getName());

        User user = jwtService.getUser(request);
        validateUserIsSystemAdmin(user);

        if (subscriptionPlanRepository.existsByName(createDto.getName())) {
            throw new BusinessException("Subscription plan name already exists: " + createDto.getName());
        }

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(createDto.getName());
        plan.setDisplayName(createDto.getDisplayName());
        plan.setDescription(createDto.getDescription());
        plan.setPrice(createDto.getPrice());
        plan.setBillingPeriod(createDto.getBillingPeriod());
        plan.setTrialDays(createDto.getTrialDays());
        plan.setMaxSchools(createDto.getMaxSchools());
        plan.setMaxUsers(createDto.getMaxUsers());
        plan.setMaxAppointmentsPerMonth(createDto.getMaxAppointmentsPerMonth());
        plan.setMaxGalleryItems(createDto.getMaxGalleryItems());
        plan.setMaxPostsPerMonth(createDto.getMaxPostsPerMonth());
        plan.setHasAnalytics(createDto.getHasAnalytics());
        plan.setHasCustomDomain(createDto.getHasCustomDomain());
        plan.setHasApiAccess(createDto.getHasApiAccess());
        plan.setHasPrioritySupport(createDto.getHasPrioritySupport());
        plan.setHasWhiteLabel(createDto.getHasWhiteLabel());
        plan.setStorageGb(createDto.getStorageGb());
        plan.setIsPopular(createDto.getIsPopular());
        plan.setSortOrder(createDto.getSortOrder());
        plan.setIsVisible(createDto.getIsVisible());
        plan.setFeatures(createDto.getFeatures());
        plan.setCreatedBy(user.getId());

        plan = subscriptionPlanRepository.save(plan);
        log.info("Subscription plan created with ID: {}", plan.getId());

        return converterService.mapToDto(plan);
    }

    public SubscriptionStatisticsDto getSubscriptionStatistics(HttpServletRequest request) {
        log.info("Fetching subscription statistics");

        User user = jwtService.getUser(request);
        validateUserIsSystemAdmin(user);
 // ceyhun return subscriptionRepository.getSubscriptionStatistics();
        return null;
    }

    // ================================ HELPER METHODS ================================

    private double calculateUsagePercentage(Integer current, Integer limit) {
        if (limit == null || limit == 0) return 0.0;
        return Math.min(100.0, (double) current / limit * 100);
    }

    private double calculateStorageUsagePercentage(Long usedMb, Integer limitGb) {
        if (limitGb == null || limitGb == 0) return 0.0;
        long limitMb = limitGb * 1024L;
        return Math.min(100.0, (double) usedMb / limitMb * 100);
    }

    private void validateUserIsSystemAdmin(User user) {
        boolean isSystemAdmin = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getRoleLevel() == RoleLevel.SYSTEM);

        if (!isSystemAdmin) {
            throw new BusinessException("System administrator access required");
        }
    }

    // ================================ WEBHOOK HANDLERS ================================

    @Transactional
    public void handlePaymentWebhook(PaymentWebhookDto webhookDto) {
        log.info("Processing payment webhook: {}", webhookDto.getEventType());

        try {
            Payment payment = paymentRepository.findByExternalPaymentId(webhookDto.getTransactionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction ID: " + webhookDto.getTransactionId()));

            switch (webhookDto.getEventType().toUpperCase()) {
                case "PAYMENT_SUCCESS":
                    handleSuccessfulPaymentWebhook(payment, webhookDto);
                    break;
                case "PAYMENT_FAILED":
                    handleFailedPaymentWebhook(payment, webhookDto);
                    break;
                case "PAYMENT_REFUNDED":
                    handleRefundedPaymentWebhook(payment, webhookDto);
                    break;
                default:
                    log.warn("Unknown webhook event type: {}", webhookDto.getEventType());
            }

        } catch (Exception e) {
            log.error("Error processing payment webhook", e);
        }
    }

    private void handleSuccessfulPaymentWebhook(Payment payment, PaymentWebhookDto webhookDto) {
        log.info("Processing successful payment webhook for payment: {}", payment.getId());

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setGatewayResponse(webhookDto.getRawData());

            updateSubscriptionAfterPayment(payment.getSubscription());

            // Create invoice if not exists
            if (payment.getInvoice() == null) {
                Invoice invoice = invoiceService.createInvoiceForPayment(payment);
                payment.setInvoice(invoice);
            }

            paymentRepository.save(payment);

            // Send confirmation email
            emailService.sendPaymentConfirmationEmail(payment);
        }
    }

    private void handleFailedPaymentWebhook(Payment payment, PaymentWebhookDto webhookDto) {
        log.info("Processing failed payment webhook for payment: {}", payment.getId());

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(webhookDto.getErrorMessage());
        payment.setGatewayResponse(webhookDto.getRawData());

        paymentRepository.save(payment);

        // Handle subscription status
        handleFailedRecurringPayment(payment.getSubscription(), payment);
    }

    private void handleRefundedPaymentWebhook(Payment payment, PaymentWebhookDto webhookDto) {
        log.info("Processing refunded payment webhook for payment: {}", payment.getId());

        BigDecimal refundAmount = webhookDto.getRefundAmount();
        payment.setRefundAmount(refundAmount);
        payment.setRefundDate(LocalDateTime.now());
        payment.setRefundReason(webhookDto.getRefundReason());

        paymentRepository.save(payment);

        // Send refund notification
        emailService.sendRefundNotificationEmail(payment);
    }


    public Page<PaymentDto> getPaymentHistory(Long subscriptionId, int page, int size, HttpServletRequest request) {
        log.info("Fetching payment history for subscription: {}", subscriptionId);

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanAccessCampus(user, subscription.getCampus().getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> payments = paymentRepository.findBySubscriptionIdOrderByCreatedAtDesc(subscriptionId, pageable);

        return payments.map(converterService::mapToDto);
    }

// ================================ USAGE TRACKING ================================

    public UsageLimitsDto checkUsageLimits(Long campusId, HttpServletRequest request) {
        log.info("Checking usage limits for campus: {}", campusId);

        User user = jwtService.getUser(request);
        validateUserCanAccessCampus(user, campusId);

        Subscription subscription = subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                        List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))
                .orElseThrow(() -> new ResourceNotFoundException("Active subscription not found for campus: " + campusId));

        SubscriptionPlan plan = subscription.getSubscriptionPlan();

        return UsageLimitsDto.builder()
                .schoolsUsed(subscription.getCurrentSchoolsCount())
                .schoolsLimit(plan.getMaxSchools())
                .usersUsed(subscription.getCurrentUsersCount())
                .usersLimit(plan.getMaxUsers())
                .appointmentsThisMonth(subscription.getCurrentMonthAppointments())
                .appointmentsLimit(plan.getMaxAppointmentsPerMonth())
                .galleryItemsUsed(subscription.getCurrentGalleryItems())
                .galleryItemsLimit(plan.getMaxGalleryItems())
                .postsThisMonth(subscription.getCurrentMonthPosts())
                .postsLimit(plan.getMaxPostsPerMonth())
                .storageUsedMb(subscription.getStorageUsedMb())
                .storageLimit(plan.getStorageGb() != null ? plan.getStorageGb() * 1024 : null)
                .build();
    }

    @Transactional
    public void updateUsageCounters(Long campusId, UsageUpdateDto usageUpdate) {
        log.info("Updating usage counters for campus: {}", campusId);

        Subscription subscription = subscriptionRepository.findByCampusIdAndStatusIn(campusId,
                        List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL))
                .orElse(null);

        if (subscription == null) {
            log.warn("No active subscription found for campus: {}", campusId);
            return;
        }

        // Update counters
        if (usageUpdate.getSchoolCountDelta() != null) {
            subscription.setCurrentSchoolsCount(
                    Math.max(0, subscription.getCurrentSchoolsCount() + usageUpdate.getSchoolCountDelta()));
        }

        if (usageUpdate.getUserCountDelta() != null) {
            subscription.setCurrentUsersCount(
                    Math.max(0, subscription.getCurrentUsersCount() + usageUpdate.getUserCountDelta()));
        }

        if (usageUpdate.getAppointmentCountDelta() != null) {
            subscription.setCurrentMonthAppointments(
                    Math.max(0, subscription.getCurrentMonthAppointments() + usageUpdate.getAppointmentCountDelta()));
        }

        if (usageUpdate.getGalleryItemCountDelta() != null) {
            subscription.setCurrentGalleryItems(
                    Math.max(0, subscription.getCurrentGalleryItems() + usageUpdate.getGalleryItemCountDelta()));
        }

        if (usageUpdate.getPostCountDelta() != null) {
            subscription.setCurrentMonthPosts(
                    Math.max(0, subscription.getCurrentMonthPosts() + usageUpdate.getPostCountDelta()));
        }

        if (usageUpdate.getStorageDeltaMb() != null) {
            subscription.setStorageUsedMb(
                    Math.max(0L, subscription.getStorageUsedMb() + usageUpdate.getStorageDeltaMb()));
        }

        subscriptionRepository.save(subscription);
    }


// ================================ SCHEDULED TASKS ================================

    @Scheduled(cron = "0 0 2 * * *") // Every day at 2 AM
    @Transactional
    public void processRecurringBillings() {
        log.info("Processing recurring billings");

        LocalDateTime today = LocalDateTime.now();
        List<Subscription> subscriptionsForBilling = subscriptionRepository
                .findSubscriptionsForBilling(today.toLocalDate());

        for (Subscription subscription : subscriptionsForBilling) {
            try {
                processRecurringBilling(subscription);
            } catch (Exception e) {
                log.error("Failed to process recurring billing for subscription: {}", subscription.getId(), e);
            }
        }

        log.info("Processed {} recurring billings", subscriptionsForBilling.size());
    }

    @Scheduled(cron = "0 30 2 * * *") // Every day at 2:30 AM
    @Transactional
    public void handleExpiredSubscriptions() {
        log.info("Handling expired subscriptions");

        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredSubscriptions(now);

        for (Subscription subscription : expiredSubscriptions) {
            try {
                handleExpiredSubscription(subscription);
            } catch (Exception e) {
                log.error("Failed to handle expired subscription: {}", subscription.getId(), e);
            }
        }

        log.info("Handled {} expired subscriptions", expiredSubscriptions.size());
    }

    @Scheduled(cron = "0 0 1 * * *") // First day of every month at 1 AM
    @Transactional
    public void resetMonthlyUsageCounters() {
        log.info("Resetting monthly usage counters");

        int updatedSubscriptions = subscriptionRepository.resetMonthlyCounters();
        log.info("Reset monthly counters for {} subscriptions", updatedSubscriptions);
    }

// ================================ PRIVATE HELPER METHODS ================================

    private void validateUserCanAccessCampus(User user, Long campusId) {
        // Implementation similar to InstitutionService
    }

    private void validateUserCanManageCampus(User user, Long campusId) {
        // Implementation similar to InstitutionService
    }

    private LocalDateTime calculateEndDate(LocalDateTime startDate, BillingPeriod billingPeriod) {
        return switch (billingPeriod) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case YEARLY -> startDate.plusYears(1);
            case ONETIME -> startDate.plusYears(100); // Lifetime
        };
    }

    private LocalDateTime calculateNextBillingDate(LocalDateTime startDate, BillingPeriod billingPeriod) {
        return calculateEndDate(startDate, billingPeriod);
    }

    private void applyDiscount(Subscription subscription, String couponCode) {
        // Implementation for coupon/discount logic
        log.info("Applying discount with coupon: {}", couponCode);
    }

    private void createInitialPayment(Subscription subscription, PaymentMethod paymentMethod) {
        log.info("Creating initial payment for subscription: {}", subscription.getId());

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setPaymentReference(generatePaymentReference());
        payment.setAmount(subscription.getPrice());
        payment.setCurrency(subscription.getCurrency());
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Default
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDescription("Initial subscription payment");
        payment.setPeriodStart(subscription.getStartDate());
        payment.setPeriodEnd(subscription.getEndDate());

        paymentRepository.save(payment);
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ProratedAmount calculateProratedAmount(Subscription subscription, SubscriptionPlan newPlan) {
        LocalDateTime now = LocalDateTime.now();
        long daysRemaining = ChronoUnit.DAYS.between(now, subscription.getEndDate());
        long totalDays = ChronoUnit.DAYS.between(subscription.getStartDate(), subscription.getEndDate());

        BigDecimal currentPlanDailyRate = subscription.getPrice().divide(BigDecimal.valueOf(totalDays), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal newPlanDailyRate = newPlan.getPrice().divide(BigDecimal.valueOf(totalDays), 2, BigDecimal.ROUND_HALF_UP);

        BigDecimal refundAmount = currentPlanDailyRate.multiply(BigDecimal.valueOf(daysRemaining));
        BigDecimal newChargeAmount = newPlanDailyRate.multiply(BigDecimal.valueOf(daysRemaining));

        BigDecimal proratedAmount = newChargeAmount.subtract(refundAmount);

        return ProratedAmount.builder()
                .amount(proratedAmount)
                .refundAmount(refundAmount)
                .chargeAmount(newChargeAmount)
                .daysRemaining(daysRemaining)
                .build();
    }


    @Transactional
    public PaymentDto processPayment(Long subscriptionId, PaymentCreateDto paymentDto, HttpServletRequest request) {
        log.info("Processing payment for subscription: {}", subscriptionId);

        User user = jwtService.getUser(request);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        validateUserCanManageCampus(user, subscription.getCampus().getId());

        // Create payment record
        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setPaymentReference(generatePaymentReference());
        payment.setAmount(paymentDto.getAmount());
        payment.setCurrency(subscription.getCurrency());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDescription(paymentDto.getDescription());
        payment.setDueDate(LocalDateTime.now().plusDays(7));
        payment.setPeriodStart(subscription.getNextBillingDate().minusMonths(1));
        payment.setPeriodEnd(subscription.getNextBillingDate());
        payment.setCreatedBy(user.getId());

        payment = paymentRepository.save(payment);

        try {
// Process payment through gateway
            PaymentGatewayResponse gatewayResponse = paymentGatewayService.processPayment(payment, paymentDto);

// Update payment with gateway response
            payment.setExternalPaymentId(gatewayResponse.getTransactionId());
            payment.setGatewayName(gatewayResponse.getGatewayName());
            payment.setGatewayTransactionId(gatewayResponse.getTransactionId());
            payment.setGatewayResponse(gatewayResponse.getRawResponse());

            if (gatewayResponse.isSuccessful()) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());

// Update subscription
                updateSubscriptionAfterPayment(subscription);

// Create invoice
                Invoice invoice = invoiceService.createInvoiceForPayment(payment);
                payment.setInvoice(invoice);

                log.info("Payment processed successfully: {}", payment.getId());
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setFailureReason(gatewayResponse.getErrorMessage());
                log.warn("Payment failed: {}", gatewayResponse.getErrorMessage());
            }

        } catch (
                Exception e) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            log.error("Payment processing failed", e);
        }

        payment = paymentRepository.save(payment);

// Send payment confirmation email
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            emailService.sendPaymentConfirmationEmail(payment);
        } else {
            emailService.sendPaymentFailedEmail(payment);
        }

        return converterService.mapToDto(payment);
    } // ceyhun
}