package com.genixo.education.search.service;

import com.genixo.education.search.dto.subscription.ProratedAmount;
import com.genixo.education.search.entity.subscription.Payment;
import com.genixo.education.search.entity.subscription.Subscription;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendPaymentConfirmationEmail(Payment payment) {
    }

    public void sendPaymentFailedEmail(Payment failedPayment) {
    }

    public void sendBillingErrorNotification(Subscription subscription, Exception error) {
    }

    public void sendSubscriptionSuspensionEmail(Subscription subscription) {
    }

    public void sendRefundNotificationEmail(Payment payment) {

    }

    public void sendSubscriptionWelcomeEmail(Subscription subscription) {
    }

    public void sendSubscriptionCancellationEmail(Subscription subscription) {
    }

    public void sendSubscriptionPlanChangeEmail(Subscription subscription, ProratedAmount proratedAmount) {

    }
}
