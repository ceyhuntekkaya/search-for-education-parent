package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.enumaration.RFQType;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RFQService {

    private final RFQRepository rfqRepository;
    private final RFQItemRepository rfqItemRepository;
    private final RFQInvitationRepository rfqInvitationRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final CampusRepository campusRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    // ================================ RFQ CRUD ================================

    @Transactional
    public RFQDto createRFQ(RFQCreateDto createDto) {
        log.info("Creating new RFQ: {}", createDto.getTitle());

        // Validate company
        Campus company = campusRepository.findById(createDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", createDto.getCompanyId()));

        RFQ rfq = new RFQ();
        rfq.setCompany(company);
        rfq.setTitle(createDto.getTitle());
        rfq.setDescription(createDto.getDescription());
        rfq.setRfqType(createDto.getRfqType() != null ? createDto.getRfqType() : RFQType.OPEN);
        rfq.setStatus(RFQStatus.DRAFT);
        rfq.setSubmissionDeadline(createDto.getSubmissionDeadline());
        rfq.setExpectedDeliveryDate(createDto.getExpectedDeliveryDate());
        rfq.setPaymentTerms(createDto.getPaymentTerms());
        rfq.setEvaluationCriteria(createDto.getEvaluationCriteria());
        rfq.setTechnicalRequirements(createDto.getTechnicalRequirements());
        rfq.setCreatedAt(LocalDateTime.now());

        RFQ saved = rfqRepository.save(rfq);
        log.info("RFQ created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    public Page<RFQDto> getAllRFQs(String searchTerm, Long companyId, RFQStatus status, RFQType rfqType, Pageable pageable) {
        log.info("Fetching RFQs with filters");

        Page<RFQ> rfqs = rfqRepository.searchRFQs(searchTerm, companyId, status, rfqType, pageable);
        return rfqs.map(this::mapToDto);
    }

    public RFQDto getRFQById(Long id) {
        log.info("Fetching RFQ with ID: {}", id);

        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));

        return mapToDto(rfq);
    }

    public Page<RFQDto> getRFQsByCompany(Long companyId, Pageable pageable) {
        log.info("Fetching RFQs for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        Page<RFQ> rfqs = rfqRepository.findByCompanyId(companyId, pageable);
        return rfqs.map(this::mapToDto);
    }

    public List<RFQDto> getActiveRFQs() {
        log.info("Fetching active RFQs");

        List<RFQ> rfqs = rfqRepository.findActiveRFQs(LocalDate.now());
        return rfqs.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RFQDto updateRFQ(Long id, RFQUpdateDto updateDto) {
        log.info("Updating RFQ with ID: {}", id);

        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));

        // Cannot update published/closed/cancelled RFQs
        if (rfq.getStatus() != RFQStatus.DRAFT) {
            throw new BusinessException("Cannot update RFQ that is not in DRAFT status");
        }

        if (updateDto.getTitle() != null) {
            rfq.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            rfq.setDescription(updateDto.getDescription());
        }
        if (updateDto.getRfqType() != null) {
            rfq.setRfqType(updateDto.getRfqType());
        }
        if (updateDto.getSubmissionDeadline() != null) {
            rfq.setSubmissionDeadline(updateDto.getSubmissionDeadline());
        }
        if (updateDto.getExpectedDeliveryDate() != null) {
            rfq.setExpectedDeliveryDate(updateDto.getExpectedDeliveryDate());
        }
        if (updateDto.getPaymentTerms() != null) {
            rfq.setPaymentTerms(updateDto.getPaymentTerms());
        }
        if (updateDto.getEvaluationCriteria() != null) {
            rfq.setEvaluationCriteria(updateDto.getEvaluationCriteria());
        }
        if (updateDto.getTechnicalRequirements() != null) {
            rfq.setTechnicalRequirements(updateDto.getTechnicalRequirements());
        }

        rfq.setUpdatedAt(LocalDateTime.now());
        RFQ updated = rfqRepository.save(rfq);
        log.info("RFQ updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public RFQDto publishRFQ(Long id) {
        log.info("Publishing RFQ with ID: {}", id);

        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));

        if (rfq.getStatus() != RFQStatus.DRAFT) {
            throw new BusinessException("Only DRAFT RFQs can be published");
        }

        // Check if RFQ has items
        List<RFQItem> items = rfqItemRepository.findByRfqId(id);
        if (items.isEmpty()) {
            throw new BusinessException("Cannot publish RFQ without items");
        }

        rfq.setStatus(RFQStatus.PUBLISHED);
        rfq.setUpdatedAt(LocalDateTime.now());
        RFQ updated = rfqRepository.save(rfq);
        log.info("RFQ published successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public RFQDto closeRFQ(Long id) {
        log.info("Closing RFQ with ID: {}", id);

        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));

        if (rfq.getStatus() != RFQStatus.PUBLISHED) {
            throw new BusinessException("Only PUBLISHED RFQs can be closed");
        }

        rfq.setStatus(RFQStatus.CLOSED);
        rfq.setUpdatedAt(LocalDateTime.now());
        RFQ updated = rfqRepository.save(rfq);
        log.info("RFQ closed successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public RFQDto cancelRFQ(Long id) {
        log.info("Cancelling RFQ with ID: {}", id);

        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));

        if (rfq.getStatus() == RFQStatus.CLOSED) {
            throw new BusinessException("Cannot cancel CLOSED RFQ");
        }

        rfq.setStatus(RFQStatus.CANCELLED);
        rfq.setUpdatedAt(LocalDateTime.now());
        RFQ updated = rfqRepository.save(rfq);
        log.info("RFQ cancelled successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public void deleteRFQ(Long id) {
        log.info("Deleting RFQ with ID: {}", id);

        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));

        // Cannot delete if has quotations
        List<Quotation> quotations = quotationRepository.findByRfqId(id);
        if (!quotations.isEmpty()) {
            throw BusinessException.resourceInUse("RFQ", quotations.size() + " quotations");
        }

        rfqRepository.delete(rfq);
        log.info("RFQ deleted successfully with ID: {}", id);
    }

    // ================================ RFQ ITEMS ================================

    public List<RFQItemDto> getRFQItems(Long rfqId) {
        log.info("Fetching items for RFQ ID: {}", rfqId);

        if (!rfqRepository.existsById(rfqId)) {
            throw new ResourceNotFoundException("RFQ", rfqId);
        }

        List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);
        return items.stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RFQItemDto createRFQItem(Long rfqId, RFQItemCreateDto createDto) {
        log.info("Creating item for RFQ ID: {}", rfqId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", rfqId));

        // Cannot add items to published/closed/cancelled RFQs
        if (rfq.getStatus() != RFQStatus.DRAFT) {
            throw new BusinessException("Cannot add items to RFQ that is not in DRAFT status");
        }

        RFQItem item = new RFQItem();
        item.setRfq(rfq);
        
        if (createDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(createDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", createDto.getCategoryId()));
            item.setCategory(category);
        }
        
        item.setItemName(createDto.getItemName());
        item.setSpecifications(createDto.getSpecifications());
        item.setQuantity(createDto.getQuantity());
        item.setUnit(createDto.getUnit());

        RFQItem saved = rfqItemRepository.save(item);
        log.info("RFQ item created successfully with ID: {}", saved.getId());

        return mapToItemDto(saved);
    }

    @Transactional
    public RFQItemDto updateRFQItem(Long rfqId, Long itemId, RFQItemUpdateDto updateDto) {
        log.info("Updating item ID: {} for RFQ ID: {}", itemId, rfqId);

        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ Item", itemId));

        if (!item.getRfq().getId().equals(rfqId)) {
            throw new ResourceNotFoundException("RFQ Item", itemId);
        }

        RFQ rfq = item.getRfq();
        if (rfq.getStatus() != RFQStatus.DRAFT) {
            throw new BusinessException("Cannot update items in RFQ that is not in DRAFT status");
        }

        if (updateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", updateDto.getCategoryId()));
            item.setCategory(category);
        }
        if (updateDto.getItemName() != null) {
            item.setItemName(updateDto.getItemName());
        }
        if (updateDto.getSpecifications() != null) {
            item.setSpecifications(updateDto.getSpecifications());
        }
        if (updateDto.getQuantity() != null) {
            item.setQuantity(updateDto.getQuantity());
        }
        if (updateDto.getUnit() != null) {
            item.setUnit(updateDto.getUnit());
        }

        RFQItem updated = rfqItemRepository.save(item);
        log.info("RFQ item updated successfully with ID: {}", itemId);

        return mapToItemDto(updated);
    }

    @Transactional
    public void deleteRFQItem(Long rfqId, Long itemId) {
        log.info("Deleting item ID: {} for RFQ ID: {}", itemId, rfqId);

        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ Item", itemId));

        if (!item.getRfq().getId().equals(rfqId)) {
            throw new ResourceNotFoundException("RFQ Item", itemId);
        }

        RFQ rfq = item.getRfq();
        if (rfq.getStatus() != RFQStatus.DRAFT) {
            throw new BusinessException("Cannot delete items from RFQ that is not in DRAFT status");
        }

        rfqItemRepository.delete(item);
        log.info("RFQ item deleted successfully with ID: {}", itemId);
    }

    // ================================ RFQ INVITATIONS ================================

    public List<RFQInvitationDto> getRFQInvitations(Long rfqId) {
        log.info("Fetching invitations for RFQ ID: {}", rfqId);

        if (!rfqRepository.existsById(rfqId)) {
            throw new ResourceNotFoundException("RFQ", rfqId);
        }

        List<RFQInvitation> invitations = rfqInvitationRepository.findByRfqId(rfqId);
        return invitations.stream()
                .map(this::mapToInvitationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RFQInvitationDto createRFQInvitation(Long rfqId, RFQInvitationCreateDto createDto) {
        log.info("Creating invitation for RFQ ID: {}", rfqId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", rfqId));

        if (rfq.getRfqType() != RFQType.INVITED) {
            throw new BusinessException("Can only invite suppliers to INVITED type RFQs");
        }

        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", createDto.getSupplierId()));

        // Check if already invited
        if (rfqInvitationRepository.existsByRfqIdAndSupplierId(rfqId, createDto.getSupplierId())) {
            throw BusinessException.duplicateResource("RFQ Invitation", "supplier", createDto.getSupplierId());
        }

        RFQInvitation invitation = new RFQInvitation();
        invitation.setRfq(rfq);
        invitation.setSupplier(supplier);
        invitation.setInvitedAt(LocalDateTime.now());

        RFQInvitation saved = rfqInvitationRepository.save(invitation);
        log.info("RFQ invitation created successfully with ID: {}", saved.getId());

        return mapToInvitationDto(saved);
    }

    @Transactional
    public List<RFQInvitationDto> createRFQInvitationsBulk(Long rfqId, RFQInvitationBulkCreateDto createDto) {
        log.info("Creating bulk invitations for RFQ ID: {}", rfqId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", rfqId));

        if (rfq.getRfqType() != RFQType.INVITED) {
            throw new BusinessException("Can only invite suppliers to INVITED type RFQs");
        }

        List<RFQInvitationDto> createdInvitations = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long supplierId : createDto.getSupplierIds()) {
            // Skip if already invited
            if (rfqInvitationRepository.existsByRfqIdAndSupplierId(rfqId, supplierId)) {
                continue;
            }

            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

            RFQInvitation invitation = new RFQInvitation();
            invitation.setRfq(rfq);
            invitation.setSupplier(supplier);
            invitation.setInvitedAt(now);

            RFQInvitation saved = rfqInvitationRepository.save(invitation);
            createdInvitations.add(mapToInvitationDto(saved));
        }

        log.info("Created {} invitations for RFQ ID: {}", createdInvitations.size(), rfqId);
        return createdInvitations;
    }

    @Transactional
    public void deleteRFQInvitation(Long rfqId, Long invitationId) {
        log.info("Deleting invitation ID: {} for RFQ ID: {}", invitationId, rfqId);

        RFQInvitation invitation = rfqInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ Invitation", invitationId));

        if (!invitation.getRfq().getId().equals(rfqId)) {
            throw new ResourceNotFoundException("RFQ Invitation", invitationId);
        }

        rfqInvitationRepository.delete(invitation);
        log.info("RFQ invitation deleted successfully with ID: {}", invitationId);
    }

    // ================================ QUOTATIONS ================================

    public List<QuotationComparisonDto> getRFQQuotations(Long rfqId) {
        log.info("Fetching quotations for RFQ ID: {}", rfqId);

        if (!rfqRepository.existsById(rfqId)) {
            throw new ResourceNotFoundException("RFQ", rfqId);
        }

        List<Quotation> quotations = quotationRepository.findByRfqId(rfqId);
        return quotations.stream()
                .map(quotation -> mapToQuotationComparisonDto(quotation, rfqId))
                .collect(Collectors.toList());
    }

    public List<QuotationComparisonDto> getRFQComparison(Long rfqId) {
        log.info("Generating comparison for RFQ ID: {}", rfqId);

        if (!rfqRepository.existsById(rfqId)) {
            throw new ResourceNotFoundException("RFQ", rfqId);
        }

        List<Quotation> quotations = quotationRepository.findByRfqId(rfqId);
        return quotations.stream()
                .map(quotation -> mapToQuotationComparisonDto(quotation, rfqId))
                .collect(Collectors.toList());
    }

    // ================================ MAPPERS ================================

    private RFQDto mapToDto(RFQ rfq) {
        Long itemCount = (long) rfqItemRepository.findByRfqId(rfq.getId()).size();
        Long quotationCount = (long) quotationRepository.findByRfqId(rfq.getId()).size();
        Long invitationCount = (long) rfqInvitationRepository.findByRfqId(rfq.getId()).size();

        return RFQDto.builder()
                .id(rfq.getId())
                .companyId(rfq.getCompany().getId())
                .companyName(rfq.getCompany().getName())
                .title(rfq.getTitle())
                .description(rfq.getDescription())
                .rfqType(rfq.getRfqType())
                .status(rfq.getStatus())
                .submissionDeadline(rfq.getSubmissionDeadline())
                .expectedDeliveryDate(rfq.getExpectedDeliveryDate())
                .paymentTerms(rfq.getPaymentTerms())
                .evaluationCriteria(rfq.getEvaluationCriteria())
                .technicalRequirements(rfq.getTechnicalRequirements())
                .createdAt(rfq.getCreatedAt())
                .updatedAt(rfq.getUpdatedAt())
                .itemCount(itemCount.intValue())
                .quotationCount(quotationCount.intValue())
                .invitationCount(invitationCount.intValue())
                .build();
    }

    private RFQItemDto mapToItemDto(RFQItem item) {
        return RFQItemDto.builder()
                .id(item.getId())
                .rfqId(item.getRfq().getId())
                .rfqTitle(item.getRfq().getTitle())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .itemName(item.getItemName())
                .specifications(item.getSpecifications())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .build();
    }

    private RFQInvitationDto mapToInvitationDto(RFQInvitation invitation) {
        return RFQInvitationDto.builder()
                .id(invitation.getId())
                .rfqId(invitation.getRfq().getId())
                .rfqTitle(invitation.getRfq().getTitle())
                .supplierId(invitation.getSupplier().getId())
                .supplierCompanyName(invitation.getSupplier().getCompanyName())
                .invitedAt(invitation.getInvitedAt())
                .build();
    }

    private QuotationComparisonDto mapToQuotationComparisonDto(Quotation quotation, Long rfqId) {
        List<QuotationItem> items = quotationItemRepository.findByQuotationId(quotation.getId());
        List<QuotationItemComparisonDto> itemDtos = items.stream()
                .map(this::mapToItemComparisonDto)
                .collect(Collectors.toList());

        return QuotationComparisonDto.builder()
                .quotationId(quotation.getId())
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
                .items(itemDtos)
                .build();
    }

    private QuotationItemComparisonDto mapToItemComparisonDto(QuotationItem item) {
        String unit = null;
        if (item.getRfqItem() != null && item.getRfqItem().getUnit() != null) {
            unit = item.getRfqItem().getUnit();
        }
        
        return QuotationItemComparisonDto.builder()
                .rfqItemId(item.getRfqItem() != null ? item.getRfqItem().getId() : null)
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .unit(unit)
                .unitPrice(item.getUnitPrice())
                .discountAmount(item.getDiscountAmount())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}

