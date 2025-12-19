package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.Order;
import com.genixo.education.search.entity.supply.ProductPayment;
import com.genixo.education.search.enumaration.PaymentStatus;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.supply.OrderRepository;
import com.genixo.education.search.supply.ProductPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final ProductPaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CampusRepository campusRepository;

    // ================================ PAYMENT CRUD ================================

    @Transactional
    public PaymentDto createPayment(PaymentCreateDto createDto) {
        log.info("Creating new payment for order ID: {}", createDto.getOrderId());

        // Validate order
        Order order = orderRepository.findById(createDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", createDto.getOrderId()));

        // Check if payment already exists for this order
        Optional<ProductPayment> existingPayment = paymentRepository.findByOrderId(createDto.getOrderId());
        if (existingPayment.isPresent()) {
            throw new BusinessException("Payment already exists for this order");
        }

        // Validate amount (should match order total or be less)
        if (createDto.getAmount().compareTo(order.getTotalAmount()) > 0) {
            throw new BusinessException("Payment amount cannot exceed order total amount");
        }

        // Create payment
        ProductPayment payment = new ProductPayment();
        payment.setOrder(order);
        payment.setPaymentMethod(createDto.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(createDto.getAmount());
        payment.setCurrency(createDto.getCurrency() != null ? createDto.getCurrency() : order.getCurrency());
        payment.setTransactionId(createDto.getTransactionId());
        payment.setNotes(createDto.getNotes());
        payment.setCreatedAt(LocalDateTime.now());

        ProductPayment saved = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    public PaymentDto getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);

        ProductPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        return mapToDto(payment);
    }

    public PaymentDto getPaymentByOrder(Long orderId) {
        log.info("Fetching payment for order ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }

        ProductPayment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));

        return mapToDto(payment);
    }

    public Page<PaymentDto> getPaymentsByCompany(Long companyId, Pageable pageable) {
        log.info("Fetching payments for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        Page<ProductPayment> payments = paymentRepository.findByCompanyId(companyId, pageable);
        return payments.map(this::mapToDto);
    }

    public Page<PaymentDto> getAllPayments(String searchTerm, Long companyId, Long orderId, 
                                           PaymentStatus status, Pageable pageable) {
        log.info("Fetching payments with filters");

        Page<ProductPayment> payments = paymentRepository.searchPayments(
                searchTerm, companyId, orderId, status, pageable);
        return payments.map(this::mapToDto);
    }

    @Transactional
    public PaymentDto updatePayment(Long id, PaymentUpdateDto updateDto) {
        log.info("Updating payment with ID: {}", id);

        ProductPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        // Cannot update completed, refunded, or failed payments
        if (payment.getStatus() == PaymentStatus.COMPLETED || 
            payment.getStatus() == PaymentStatus.REFUNDED ||
            payment.getStatus() == PaymentStatus.FAILED) {
            throw new BusinessException("Cannot update payment that is COMPLETED, REFUNDED, or FAILED");
        }

        if (updateDto.getTransactionId() != null) {
            payment.setTransactionId(updateDto.getTransactionId());
        }
        if (updateDto.getNotes() != null) {
            payment.setNotes(updateDto.getNotes());
        }

        ProductPayment updated = paymentRepository.save(payment);
        log.info("Payment updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public PaymentDto updatePaymentStatus(Long id, PaymentStatusUpdateDto statusDto) {
        log.info("Updating payment status with ID: {} to {}", id, statusDto.getStatus());

        ProductPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        PaymentStatus newStatus = statusDto.getStatus();
        PaymentStatus currentStatus = payment.getStatus();

        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);

        payment.setStatus(newStatus);

        // Set paidAt if status is COMPLETED
        if (newStatus == PaymentStatus.COMPLETED && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }

        ProductPayment updated = paymentRepository.save(payment);
        log.info("Payment status updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public PaymentDto confirmPayment(Long id) {
        log.info("Confirming payment with ID: {}", id);

        ProductPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new BusinessException("Only PENDING or PROCESSING payments can be confirmed");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        ProductPayment updated = paymentRepository.save(payment);
        log.info("Payment confirmed successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public PaymentDto failPayment(Long id) {
        log.info("Failing payment with ID: {}", id);

        ProductPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Cannot fail payment that is COMPLETED or REFUNDED");
        }

        payment.setStatus(PaymentStatus.FAILED);
        ProductPayment updated = paymentRepository.save(payment);
        log.info("Payment failed successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public PaymentDto refundPayment(Long id, PaymentRefundDto refundDto) {
        log.info("Refunding payment with ID: {}", id);

        ProductPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException("Only COMPLETED payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        String refundNote = "Refunded: " + refundDto.getRefundReason();
        if (payment.getNotes() != null && !payment.getNotes().isEmpty()) {
            payment.setNotes(payment.getNotes() + "\n" + refundNote);
        } else {
            payment.setNotes(refundNote);
        }

        ProductPayment updated = paymentRepository.save(payment);
        log.info("Payment refunded successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ HELPER METHODS ================================

    private void validateStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != PaymentStatus.PROCESSING && 
                    newStatus != PaymentStatus.COMPLETED && 
                    newStatus != PaymentStatus.FAILED &&
                    newStatus != PaymentStatus.CANCELED &&
                    newStatus != PaymentStatus.EXPIRED) {
                    throw new BusinessException("PENDING payments can only transition to PROCESSING, COMPLETED, FAILED, CANCELED, or EXPIRED");
                }
                break;
            case PROCESSING:
                if (newStatus != PaymentStatus.COMPLETED && 
                    newStatus != PaymentStatus.FAILED &&
                    newStatus != PaymentStatus.CANCELED) {
                    throw new BusinessException("PROCESSING payments can only transition to COMPLETED, FAILED, or CANCELED");
                }
                break;
            case COMPLETED:
                if (newStatus != PaymentStatus.REFUNDED && 
                    newStatus != PaymentStatus.PARTIALLY_REFUNDED) {
                    throw new BusinessException("COMPLETED payments can only transition to REFUNDED or PARTIALLY_REFUNDED");
                }
                break;
            case FAILED:
            case CANCELED:
            case EXPIRED:
            case REFUNDED:
            case PARTIALLY_REFUNDED:
            case DISPUTED:
                throw new BusinessException("Cannot change status of " + currentStatus + " payment");
            default:
                throw new BusinessException("Invalid status transition");
        }
    }

    private PaymentDto mapToDto(ProductPayment payment) {
        Order order = payment.getOrder();

        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .companyId(order.getCompany().getId())
                .companyName(order.getCompany().getName())
                .supplierId(order.getSupplier().getId())
                .supplierCompanyName(order.getSupplier().getCompanyName())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

