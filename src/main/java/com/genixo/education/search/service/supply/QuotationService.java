package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.enumaration.QuotationStatus;
import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.enumaration.RFQType;
import com.genixo.education.search.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final RFQRepository rfqRepository;
    private final RFQItemRepository rfqItemRepository;
    private final RFQInvitationRepository rfqInvitationRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    // ================================ QUOTATION CRUD ================================

    @Transactional
    public QuotationDto createQuotation(QuotationCreateDto createDto) {
        log.info("Creating new quotation for RFQ ID: {}", createDto.getRfqId());

        // Validate RFQ
        RFQ rfq = rfqRepository.findById(createDto.getRfqId())
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", createDto.getRfqId()));

        if (rfq.getStatus() != RFQStatus.PUBLISHED) {
            throw new BusinessException("Can only create quotations for PUBLISHED RFQs");
        }

        // Check if RFQ is INVITED type and supplier is invited
        if (rfq.getRfqType() == RFQType.INVITED) {
            if (!rfqInvitationRepository.existsByRfqIdAndSupplierId(createDto.getRfqId(), createDto.getSupplierId())) {
                throw new BusinessException("Supplier must be invited to create quotation for INVITED type RFQ");
            }
        }

        // Validate supplier
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", createDto.getSupplierId()));

        if (!supplier.getIsActive()) {
            throw new BusinessException("Cannot create quotation for inactive supplier");
        }

        // Check if supplier already has a quotation for this RFQ
        List<Quotation> existingQuotations = quotationRepository.findByRfqIdAndSupplierIdOrderByVersionNumberDesc(
                createDto.getRfqId(), createDto.getSupplierId());
        Integer nextVersion = existingQuotations.isEmpty() ? 1 
                : (existingQuotations.get(0).getVersionNumber() != null ? existingQuotations.get(0).getVersionNumber() + 1 : 1);

        Quotation quotation = new Quotation();
        quotation.setRfq(rfq);
        quotation.setSupplier(supplier);
        quotation.setStatus(QuotationStatus.DRAFT);
        quotation.setTotalAmount(createDto.getTotalAmount());
        quotation.setCurrency(createDto.getCurrency() != null ? createDto.getCurrency() : com.genixo.education.search.enumaration.Currency.TRY);
        quotation.setValidUntil(createDto.getValidUntil());
        quotation.setDeliveryDays(createDto.getDeliveryDays());
        quotation.setPaymentTerms(createDto.getPaymentTerms());
        quotation.setWarrantyTerms(createDto.getWarrantyTerms());
        quotation.setNotes(createDto.getNotes());
        quotation.setVersionNumber(nextVersion);
        quotation.setCreatedAt(LocalDateTime.now());

        Quotation saved = quotationRepository.save(quotation);
        log.info("Quotation created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    public Page<QuotationDto> getAllQuotations(String searchTerm, Long rfqId, Long supplierId, 
                                               Long companyId, QuotationStatus status, Pageable pageable) {
        log.info("Fetching quotations with filters");

        Page<Quotation> quotations = quotationRepository.searchQuotations(
                searchTerm, rfqId, supplierId, companyId, status, pageable);
        return quotations.map(this::mapToDto);
    }

    public QuotationDto getQuotationById(Long id) {
        log.info("Fetching quotation with ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        return mapToDto(quotation);
    }

    public Page<QuotationDto> getQuotationsByRFQ(Long rfqId, Pageable pageable) {
        log.info("Fetching quotations for RFQ ID: {}", rfqId);

        if (!rfqRepository.existsById(rfqId)) {
            throw new ResourceNotFoundException("RFQ", rfqId);
        }

        // Use search with rfqId filter
        Page<Quotation> quotations = quotationRepository.searchQuotations(
                null, rfqId, null, null, null, pageable);
        return quotations.map(this::mapToDto);
    }

    public Page<QuotationDto> getQuotationsBySupplier(Long supplierId, Pageable pageable) {
        log.info("Fetching quotations for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<Quotation> quotations = quotationRepository.findBySupplierId(supplierId, pageable);
        return quotations.map(this::mapToDto);
    }

    public Page<QuotationDto> getQuotationsByCompany(Long companyId, Pageable pageable) {
        log.info("Fetching quotations for company ID: {}", companyId);

        Page<Quotation> quotations = quotationRepository.findByCompanyId(companyId, pageable);
        return quotations.map(this::mapToDto);
    }

    @Transactional
    public QuotationDto updateQuotation(Long id, QuotationUpdateDto updateDto) {
        log.info("Updating quotation with ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        // Cannot update submitted/accepted/rejected quotations
        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new BusinessException("Cannot update quotation that is not in DRAFT status");
        }

        if (updateDto.getTotalAmount() != null) {
            quotation.setTotalAmount(updateDto.getTotalAmount());
        }
        if (updateDto.getCurrency() != null) {
            quotation.setCurrency(updateDto.getCurrency());
        }
        if (updateDto.getValidUntil() != null) {
            quotation.setValidUntil(updateDto.getValidUntil());
        }
        if (updateDto.getDeliveryDays() != null) {
            quotation.setDeliveryDays(updateDto.getDeliveryDays());
        }
        if (updateDto.getPaymentTerms() != null) {
            quotation.setPaymentTerms(updateDto.getPaymentTerms());
        }
        if (updateDto.getWarrantyTerms() != null) {
            quotation.setWarrantyTerms(updateDto.getWarrantyTerms());
        }
        if (updateDto.getNotes() != null) {
            quotation.setNotes(updateDto.getNotes());
        }

        // Recalculate total amount from items if not explicitly set
        if (updateDto.getTotalAmount() == null) {
            recalculateTotalAmount(quotation);
        }

        quotation.setUpdatedAt(LocalDateTime.now());
        Quotation updated = quotationRepository.save(quotation);
        log.info("Quotation updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public QuotationDto submitQuotation(Long id) {
        log.info("Submitting quotation with ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new BusinessException("Only DRAFT quotations can be submitted");
        }

        // Check if quotation has items
        List<QuotationItem> items = quotationItemRepository.findByQuotationId(id);
        if (items.isEmpty()) {
            throw new BusinessException("Cannot submit quotation without items");
        }

        // Recalculate total amount
        recalculateTotalAmount(quotation);

        quotation.setStatus(QuotationStatus.SUBMITTED);
        quotation.setUpdatedAt(LocalDateTime.now());
        Quotation updated = quotationRepository.save(quotation);
        log.info("Quotation submitted successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public QuotationDto acceptQuotation(Long id) {
        log.info("Accepting quotation with ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        if (quotation.getStatus() != QuotationStatus.SUBMITTED && 
            quotation.getStatus() != QuotationStatus.UNDER_REVIEW) {
            throw new BusinessException("Only SUBMITTED or UNDER_REVIEW quotations can be accepted");
        }

        quotation.setStatus(QuotationStatus.ACCEPTED);
        quotation.setUpdatedAt(LocalDateTime.now());
        Quotation updated = quotationRepository.save(quotation);
        log.info("Quotation accepted successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public QuotationDto rejectQuotation(Long id) {
        log.info("Rejecting quotation with ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        if (quotation.getStatus() != QuotationStatus.SUBMITTED && 
            quotation.getStatus() != QuotationStatus.UNDER_REVIEW) {
            throw new BusinessException("Only SUBMITTED or UNDER_REVIEW quotations can be rejected");
        }

        quotation.setStatus(QuotationStatus.REJECTED);
        quotation.setUpdatedAt(LocalDateTime.now());
        Quotation updated = quotationRepository.save(quotation);
        log.info("Quotation rejected successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public void deleteQuotation(Long id) {
        log.info("Deleting quotation with ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        // Cannot delete submitted/accepted quotations
        if (quotation.getStatus() == QuotationStatus.ACCEPTED) {
            throw new BusinessException("Cannot delete ACCEPTED quotation");
        }

        quotationRepository.delete(quotation);
        log.info("Quotation deleted successfully with ID: {}", id);
    }

    public List<QuotationVersionDto> getQuotationVersions(Long id) {
        log.info("Fetching versions for quotation ID: {}", id);

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        List<Quotation> versions = quotationRepository.findByRfqIdAndSupplierIdOrderByVersionNumberDesc(
                quotation.getRfq().getId(), quotation.getSupplier().getId());

        return versions.stream()
                .map(this::mapToVersionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuotationDto duplicateQuotation(Long id) {
        log.info("Duplicating quotation with ID: {} (creating new version)", id);

        Quotation original = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        // Get next version number
        Integer nextVersion = quotationRepository.findMaxVersionNumberByRfqIdAndSupplierId(
                original.getRfq().getId(), original.getSupplier().getId());
        nextVersion = nextVersion != null ? nextVersion + 1 : 1;

        // Create new quotation
        Quotation newQuotation = new Quotation();
        newQuotation.setRfq(original.getRfq());
        newQuotation.setSupplier(original.getSupplier());
        newQuotation.setStatus(QuotationStatus.DRAFT);
        newQuotation.setTotalAmount(original.getTotalAmount());
        newQuotation.setCurrency(original.getCurrency());
        newQuotation.setValidUntil(original.getValidUntil());
        newQuotation.setDeliveryDays(original.getDeliveryDays());
        newQuotation.setPaymentTerms(original.getPaymentTerms());
        newQuotation.setWarrantyTerms(original.getWarrantyTerms());
        newQuotation.setNotes(original.getNotes());
        newQuotation.setVersionNumber(nextVersion);
        newQuotation.setCreatedAt(LocalDateTime.now());

        Quotation saved = quotationRepository.save(newQuotation);

        // Copy items
        List<QuotationItem> originalItems = quotationItemRepository.findByQuotationId(id);
        for (QuotationItem originalItem : originalItems) {
            QuotationItem newItem = new QuotationItem();
            newItem.setQuotation(saved);
            newItem.setRfqItem(originalItem.getRfqItem());
            newItem.setProduct(originalItem.getProduct());
            newItem.setItemName(originalItem.getItemName());
            newItem.setQuantity(originalItem.getQuantity());
            newItem.setUnitPrice(originalItem.getUnitPrice());
            newItem.setDiscountAmount(originalItem.getDiscountAmount());
            newItem.setTotalPrice(originalItem.getTotalPrice());
            quotationItemRepository.save(newItem);
        }

        log.info("Quotation duplicated successfully. New quotation ID: {}", saved.getId());
        return mapToDto(saved);
    }

    // ================================ QUOTATION ITEMS ================================

    public List<QuotationItemDto> getQuotationItems(Long quotationId) {
        log.info("Fetching items for quotation ID: {}", quotationId);

        if (!quotationRepository.existsById(quotationId)) {
            throw new ResourceNotFoundException("Quotation", quotationId);
        }

        List<QuotationItem> items = quotationItemRepository.findByQuotationId(quotationId);
        return items.stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuotationItemDto createQuotationItem(Long quotationId, QuotationItemCreateDto createDto) {
        log.info("Creating item for quotation ID: {}", quotationId);

        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", quotationId));

        // Cannot add items to submitted/accepted/rejected quotations
        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new BusinessException("Cannot add items to quotation that is not in DRAFT status");
        }

        QuotationItem item = new QuotationItem();
        item.setQuotation(quotation);

        if (createDto.getRfqItemId() != null) {
            RFQItem rfqItem = rfqItemRepository.findById(createDto.getRfqItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("RFQ Item", createDto.getRfqItemId()));
            item.setRfqItem(rfqItem);
        }

        if (createDto.getProductId() != null) {
            Product product = productRepository.findById(createDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", createDto.getProductId()));
            item.setProduct(product);
        }

        item.setItemName(createDto.getItemName());
        item.setQuantity(createDto.getQuantity());
        item.setUnitPrice(createDto.getUnitPrice());
        item.setDiscountAmount(createDto.getDiscountAmount() != null ? createDto.getDiscountAmount() : BigDecimal.ZERO);
        
        // Calculate total price
        BigDecimal totalPrice = createDto.getUnitPrice()
                .multiply(BigDecimal.valueOf(createDto.getQuantity()))
                .subtract(item.getDiscountAmount());
        item.setTotalPrice(totalPrice);

        QuotationItem saved = quotationItemRepository.save(item);

        // Recalculate quotation total
        recalculateTotalAmount(quotation);

        log.info("Quotation item created successfully with ID: {}", saved.getId());
        return mapToItemDto(saved);
    }

    @Transactional
    public QuotationItemDto updateQuotationItem(Long quotationId, Long itemId, QuotationItemUpdateDto updateDto) {
        log.info("Updating item ID: {} for quotation ID: {}", itemId, quotationId);

        QuotationItem item = quotationItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation Item", itemId));

        if (!item.getQuotation().getId().equals(quotationId)) {
            throw new ResourceNotFoundException("Quotation Item", itemId);
        }

        Quotation quotation = item.getQuotation();
        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new BusinessException("Cannot update items in quotation that is not in DRAFT status");
        }

        if (updateDto.getItemName() != null) {
            item.setItemName(updateDto.getItemName());
        }
        if (updateDto.getQuantity() != null) {
            item.setQuantity(updateDto.getQuantity());
        }
        if (updateDto.getUnitPrice() != null) {
            item.setUnitPrice(updateDto.getUnitPrice());
        }
        if (updateDto.getDiscountAmount() != null) {
            item.setDiscountAmount(updateDto.getDiscountAmount());
        }

        // Recalculate total price
        BigDecimal totalPrice = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .subtract(item.getDiscountAmount());
        item.setTotalPrice(totalPrice);

        QuotationItem updated = quotationItemRepository.save(item);

        // Recalculate quotation total
        recalculateTotalAmount(quotation);

        log.info("Quotation item updated successfully with ID: {}", itemId);
        return mapToItemDto(updated);
    }

    @Transactional
    public void deleteQuotationItem(Long quotationId, Long itemId) {
        log.info("Deleting item ID: {} for quotation ID: {}", itemId, quotationId);

        QuotationItem item = quotationItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation Item", itemId));

        if (!item.getQuotation().getId().equals(quotationId)) {
            throw new ResourceNotFoundException("Quotation Item", itemId);
        }

        Quotation quotation = item.getQuotation();
        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new BusinessException("Cannot delete items from quotation that is not in DRAFT status");
        }

        quotationItemRepository.delete(item);

        // Recalculate quotation total
        recalculateTotalAmount(quotation);

        log.info("Quotation item deleted successfully with ID: {}", itemId);
    }

    @Transactional
    public QuotationItemDto applyItemDiscount(Long quotationId, Long itemId, QuotationItemDiscountDto discountDto) {
        log.info("Applying discount to item ID: {} for quotation ID: {}", itemId, quotationId);

        QuotationItem item = quotationItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation Item", itemId));

        if (!item.getQuotation().getId().equals(quotationId)) {
            throw new ResourceNotFoundException("Quotation Item", itemId);
        }

        Quotation quotation = item.getQuotation();
        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new BusinessException("Cannot apply discount to items in quotation that is not in DRAFT status");
        }

        item.setDiscountAmount(discountDto.getDiscountAmount());

        // Recalculate total price
        BigDecimal totalPrice = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .subtract(item.getDiscountAmount());
        item.setTotalPrice(totalPrice);

        QuotationItem updated = quotationItemRepository.save(item);

        // Recalculate quotation total
        recalculateTotalAmount(quotation);

        log.info("Discount applied successfully to item ID: {}", itemId);
        return mapToItemDto(updated);
    }

    // ================================ HELPER METHODS ================================

    private void recalculateTotalAmount(Quotation quotation) {
        List<QuotationItem> items = quotationItemRepository.findByQuotationId(quotation.getId());
        BigDecimal total = items.stream()
                .map(QuotationItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        quotation.setTotalAmount(total);
        quotationRepository.save(quotation);
    }

    private QuotationDto mapToDto(Quotation quotation) {
        Long itemCount = (long) quotationItemRepository.findByQuotationId(quotation.getId()).size();

        return QuotationDto.builder()
                .id(quotation.getId())
                .rfqId(quotation.getRfq().getId())
                .rfqTitle(quotation.getRfq().getTitle())
                .supplierId(quotation.getSupplier().getId())
                .supplierCompanyName(quotation.getSupplier().getCompanyName())
                .averageRating(quotation.getSupplier().getAverageRating())
                .status(quotation.getStatus())
                .totalAmount(quotation.getTotalAmount())
                .currency(quotation.getCurrency())
                .validUntil(quotation.getValidUntil())
                .deliveryDays(quotation.getDeliveryDays())
                .paymentTerms(quotation.getPaymentTerms())
                .warrantyTerms(quotation.getWarrantyTerms())
                .notes(quotation.getNotes())
                .versionNumber(quotation.getVersionNumber())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .itemCount(itemCount.intValue())
                .build();
    }

    private QuotationVersionDto mapToVersionDto(Quotation quotation) {
        return QuotationVersionDto.builder()
                .id(quotation.getId())
                .versionNumber(quotation.getVersionNumber())
                .status(quotation.getStatus())
                .totalAmount(quotation.getTotalAmount())
                .currency(quotation.getCurrency())
                .validUntil(quotation.getValidUntil())
                .deliveryDays(quotation.getDeliveryDays())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .build();
    }

    private QuotationItemDto mapToItemDto(QuotationItem item) {
        return QuotationItemDto.builder()
                .id(item.getId())
                .quotationId(item.getQuotation().getId())
                .rfqItemId(item.getRfqItem() != null ? item.getRfqItem().getId() : null)
                .rfqItemName(item.getRfqItem() != null ? item.getRfqItem().getItemName() : null)
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountAmount(item.getDiscountAmount())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}

