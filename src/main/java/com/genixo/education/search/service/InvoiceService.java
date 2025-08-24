package com.genixo.education.search.service;

import com.genixo.education.search.entity.subscription.Invoice;
import com.genixo.education.search.entity.subscription.Payment;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    public Invoice createInvoiceForPayment(Payment payment) {
        return null;
    }

    public void generateInvoicePdf(Invoice invoice) {
    }

    public byte[] downloadInvoicePdf(Invoice invoice) {
        return null;
    }
}
