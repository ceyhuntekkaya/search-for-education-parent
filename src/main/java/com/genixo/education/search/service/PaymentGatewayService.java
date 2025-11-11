package com.genixo.education.search.service;

import com.genixo.education.search.dto.subscription.PaymentCreateDto;
import com.genixo.education.search.dto.subscription.PaymentGatewayResponse;
import com.genixo.education.search.entity.subscription.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

    public PaymentGatewayResponse processRecurringPayment(Payment payment) {
        // Simulate payment processing logic
        boolean paymentSuccess = true; // This would be determined by actual payment gateway response
// ceyhun
        if (paymentSuccess) {
            return PaymentGatewayResponse.builder()
                    .successful(true)
                    .transactionId("TXN123456")
                    .rawResponse("Payment processed successfully.")
                    .build();
        } else {
            return PaymentGatewayResponse.builder()
                    .successful(false)
                    .transactionId(null)
                    .rawResponse("Payment failed due to insufficient funds.")
                    .build();
        }
    }

    public PaymentGatewayResponse processPayment(Payment payment, PaymentCreateDto paymentDto) {
        // Simulate payment processing logic
        boolean paymentSuccess = true; // This would be determined by actual payment gateway response

        if (paymentSuccess) {
            return PaymentGatewayResponse.builder()
                    .successful(true)
                    .transactionId("TXN654321")
                    .rawResponse("Payment processed successfully.")
                    .build();
        } else {
            return PaymentGatewayResponse.builder()
                    .successful(false)
                    .transactionId(null)
                    .rawResponse("Payment failed due to insufficient funds.")
                    .build();
        }
    }
}
