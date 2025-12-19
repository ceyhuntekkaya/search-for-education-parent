package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.ConversationCreateDto;
import com.genixo.education.search.dto.supply.ConversationDto;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductConversationService {

    private final ProductConversationRepository conversationRepository;
    private final ProductMessageRepository messageRepository;
    private final CampusRepository campusRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final QuotationRepository quotationRepository;
    private final OrderRepository orderRepository;

    // ================================ CONVERSATION CRUD ================================

    @Transactional
    public ConversationDto createConversation(Long userId, ConversationCreateDto createDto) {
        log.info("Creating new conversation for user ID: {}", userId);

        // Validate company
        Campus company = campusRepository.findById(createDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", createDto.getCompanyId()));

        // Validate supplier
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", createDto.getSupplierId()));

        if (!supplier.getIsActive()) {
            throw new BusinessException("Cannot create conversation with inactive supplier");
        }

        // Create conversation
        ProductConversation conversation = new ProductConversation();
        conversation.setCompany(company);
        conversation.setSupplier(supplier);

        if (createDto.getProductId() != null) {
            Product product = productRepository.findById(createDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", createDto.getProductId()));
            conversation.setProduct(product);
        }

        if (createDto.getQuotationId() != null) {
            Quotation quotation = quotationRepository.findById(createDto.getQuotationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quotation", createDto.getQuotationId()));
            conversation.setQuotation(quotation);
        }

        if (createDto.getOrderId() != null) {
            Order order = orderRepository.findById(createDto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order", createDto.getOrderId()));
            conversation.setOrder(order);
        }

        conversation.setMessageType(createDto.getMessageType());
        conversation.setSubject(createDto.getSubject());
        conversation.setCreatedAt(LocalDateTime.now());

        ProductConversation saved = conversationRepository.save(conversation);
        log.info("Conversation created successfully with ID: {}", saved.getId());

        return mapToDto(saved, userId);
    }

    public Page<ConversationDto> getUserConversations(Long userId, Pageable pageable) {
        log.info("Fetching conversations for user ID: {}", userId);

        // Get conversations where user is either company or supplier
        Page<ProductConversation> conversations = conversationRepository.findByCompanyIdOrSupplierId(
                userId, userId, pageable);
        return conversations.map(c -> mapToDto(c, userId));
    }

    public ConversationDto getConversationById(Long id, Long userId) {
        log.info("Fetching conversation with ID: {} for user ID: {}", id, userId);

        ProductConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", id));

        return mapToDto(conversation, userId);
    }

    public Page<ConversationDto> getConversationsByCompany(Long companyId, Pageable pageable) {
        log.info("Fetching conversations for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        Page<ProductConversation> conversations = conversationRepository.findByCompanyId(companyId, pageable);
        return conversations.map(c -> mapToDto(c, null));
    }

    public Page<ConversationDto> getConversationsBySupplier(Long supplierId, Pageable pageable) {
        log.info("Fetching conversations for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<ProductConversation> conversations = conversationRepository.findBySupplierId(supplierId, pageable);
        return conversations.map(c -> mapToDto(c, null));
    }

    public Page<ConversationDto> getConversationsByProduct(Long productId, Pageable pageable) {
        log.info("Fetching conversations for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        Page<ProductConversation> conversations = conversationRepository.findByProductId(productId, pageable);
        return conversations.map(c -> mapToDto(c, null));
    }

    public Page<ConversationDto> getConversationsByQuotation(Long quotationId, Pageable pageable) {
        log.info("Fetching conversations for quotation ID: {}", quotationId);

        if (!quotationRepository.existsById(quotationId)) {
            throw new ResourceNotFoundException("Quotation", quotationId);
        }

        Page<ProductConversation> conversations = conversationRepository.findByQuotationId(quotationId, pageable);
        return conversations.map(c -> mapToDto(c, null));
    }

    public Page<ConversationDto> getConversationsByOrder(Long orderId, Pageable pageable) {
        log.info("Fetching conversations for order ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }

        Page<ProductConversation> conversations = conversationRepository.findByOrderId(orderId, pageable);
        return conversations.map(c -> mapToDto(c, null));
    }

    public Long getUnreadConversationCount(Long userId) {
        log.info("Fetching unread conversation count for user ID: {}", userId);

        Long count = conversationRepository.countUnreadConversationsByUserId(userId);
        return count != null ? count : 0L;
    }

    @Transactional
    public void deleteConversation(Long id, Long userId) {
        log.info("Deleting conversation with ID: {} by user ID: {}", id, userId);

        ProductConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", id));

        // Verify user has access (either company or supplier side)
        boolean hasAccess = conversation.getCompany().getId().equals(userId) ||
                           conversation.getSupplier().getId().equals(userId);
        
        if (!hasAccess) {
            throw new BusinessException("You don't have permission to delete this conversation");
        }

        conversationRepository.delete(conversation);
        log.info("Conversation deleted successfully with ID: {}", id);
    }

    // ================================ HELPER METHODS ================================

    private ConversationDto mapToDto(ProductConversation conversation, Long userId) {
        Long unreadCount = userId != null 
                ? conversationRepository.countUnreadMessagesByConversationIdAndUserId(conversation.getId(), userId)
                : 0L;
        Long messageCount = messageRepository.countByConversationId(conversation.getId());

        // Get last message time
        Page<ProductMessage> messagesPage = messageRepository.findByConversationIdOrderByCreatedAtAsc(
                conversation.getId(), 
                org.springframework.data.domain.PageRequest.of(0, 1, 
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
        );
        LocalDateTime lastMessageAt = messagesPage.getContent().isEmpty() ? null : messagesPage.getContent().get(0).getCreatedAt();

        return ConversationDto.builder()
                .id(conversation.getId())
                .companyId(conversation.getCompany().getId())
                .companyName(conversation.getCompany().getName())
                .supplierId(conversation.getSupplier().getId())
                .supplierCompanyName(conversation.getSupplier().getCompanyName())
                .productId(conversation.getProduct() != null ? conversation.getProduct().getId() : null)
                .productName(conversation.getProduct() != null ? conversation.getProduct().getName() : null)
                .quotationId(conversation.getQuotation() != null ? conversation.getQuotation().getId() : null)
                .orderId(conversation.getOrder() != null ? conversation.getOrder().getId() : null)
                .orderNumber(conversation.getOrder() != null ? conversation.getOrder().getOrderNumber() : null)
                .messageType(conversation.getMessageType())
                .subject(conversation.getSubject())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .unreadCount(unreadCount)
                .messageCount(messageCount)
                .lastMessageAt(lastMessageAt)
                .build();
    }
}

