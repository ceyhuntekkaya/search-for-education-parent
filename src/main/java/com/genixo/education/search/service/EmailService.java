package com.genixo.education.search.service;

import com.genixo.education.search.dto.subscription.ProratedAmount;
import com.genixo.education.search.entity.subscription.Payment;
import com.genixo.education.search.entity.subscription.Subscription;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sendgrid.*;
import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    public void sendMail() throws Exception {
        Email from = new Email("noreply@egitimiste.com");
        String subject = "EğitimIste'ye Hoş Geldin!";
        String templateId = "d-61c6975e48ee48429d27b4fe0f583874";
        Email to = new Email("ceyhun.tekkaya@gmail.com");

        // Dynamic template kullanıyorsanız Content gerekli değil
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        // Doğru verilerle dynamic template parametrelerini set edin
        personalization.addDynamicTemplateData("first_name", "Ferhat");
        personalization.addDynamicTemplateData("email", "ferhatskokdemir@gmail.com");
        personalization.addDynamicTemplateData("dashboard_url", "https://egitimiste.com/dashboard");
        personalization.addDynamicTemplateData("company_name", "EğitimIste");
        personalization.addDynamicTemplateData("social_facebook", "https://facebook.com/egitimiste");
        personalization.addDynamicTemplateData("social_twitter", "https://twitter.com/egitimiste");
        personalization.addDynamicTemplateData("social_instagram", "https://instagram.com/egitimiste");
        personalization.addDynamicTemplateData("social_linkedin", "https://linkedin.com/company/egitimiste");
        personalization.addDynamicTemplateData("unsubscribe", "https://egitimiste.com/unsubscribe");

        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid("");
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());

        } catch (IOException ex) {
            System.err.println("E-posta gönderme hatası: " + ex.getMessage());
            throw ex;
        }
    }



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












    public void sendCode(String code, String mailAddress, String name, String lastName) throws Exception {
        Email from = new Email("noreply@egitimiste.com");
        String subject = "EğitimIste Kayıt Kodu!";
        String templateId = "d-8b6bed400b9f4a3096e28af94a1b0474";
        Email to = new Email(mailAddress);
        Email to2 = new Email("ceyhun.tekkaya@gmail.com");

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        personalization.addTo(to2);

        // Doğru verilerle dynamic template parametrelerini set edin
        personalization.addDynamicTemplateData("firstName", name);
        personalization.addDynamicTemplateData("lastName", lastName);
        personalization.addDynamicTemplateData("verificationCode", code);

        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());

        } catch (IOException ex) {
            System.err.println("E-posta gönderme hatası: " + ex.getMessage());
            throw ex;
        }
    }
}
