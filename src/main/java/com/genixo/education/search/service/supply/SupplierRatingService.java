package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.SupplierRatingCreateDto;
import com.genixo.education.search.dto.supply.SupplierRatingDto;
import com.genixo.education.search.dto.supply.SupplierRatingUpdateDto;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.supply.Order;
import com.genixo.education.search.entity.supply.Supplier;
import com.genixo.education.search.entity.supply.SupplierRating;
import com.genixo.education.search.enumaration.OrderStatus;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.supply.OrderRepository;
import com.genixo.education.search.repository.supply.SupplierRatingRepository;
import com.genixo.education.search.repository.supply.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplierRatingService {

    private final SupplierRatingRepository ratingRepository;
    private final SupplierRepository supplierRepository;
    private final CampusRepository campusRepository;
    private final OrderRepository orderRepository;

    // ================================ RATING CRUD ================================

    @Transactional
    public SupplierRatingDto createRating(SupplierRatingCreateDto createDto) {
        log.info("Creating new rating for supplier ID: {}", createDto.getSupplierId());

        // Validate supplier
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", createDto.getSupplierId()));

        // Validate company
        Campus company = campusRepository.findById(createDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", createDto.getCompanyId()));

        // Validate order
        Order order = orderRepository.findById(createDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", createDto.getOrderId()));

        // Check if order is delivered
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("Can only rate supplier for DELIVERED orders");
        }

        // Check if rating already exists for this order
        if (ratingRepository.findByOrderId(createDto.getOrderId()).isPresent()) {
            throw new BusinessException("Rating already exists for this order");
        }

        // Verify order belongs to company and supplier
        if (!order.getCompany().getId().equals(createDto.getCompanyId())) {
            throw new BusinessException("Order does not belong to the specified company");
        }

        if (!order.getSupplier().getId().equals(createDto.getSupplierId())) {
            throw new BusinessException("Order does not belong to the specified supplier");
        }

        // Create rating
        SupplierRating rating = new SupplierRating();
        rating.setSupplier(supplier);
        rating.setCompany(company);
        rating.setOrder(order);
        rating.setDeliveryRating(createDto.getDeliveryRating());
        rating.setQualityRating(createDto.getQualityRating());
        rating.setCommunicationRating(createDto.getCommunicationRating());
        rating.setComment(createDto.getComment());
        rating.setCreatedAt(LocalDateTime.now());

        SupplierRating saved = ratingRepository.save(rating);

        // Update supplier average rating
        updateSupplierAverageRating(supplier.getId());

        log.info("Rating created successfully with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    public Page<SupplierRatingDto> getRatingsBySupplier(Long supplierId, Pageable pageable) {
        log.info("Fetching ratings for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<SupplierRating> ratings = ratingRepository.findBySupplierId(supplierId, pageable);
        return ratings.map(this::mapToDto);
    }

    public SupplierRatingDto getRatingByOrder(Long orderId) {
        log.info("Fetching rating for order ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }

        SupplierRating rating = ratingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "orderId", orderId));

        return mapToDto(rating);
    }

    public SupplierRatingDto getRatingById(Long id) {
        log.info("Fetching rating with ID: {}", id);

        SupplierRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", id));

        return mapToDto(rating);
    }

    @Transactional
    public SupplierRatingDto updateRating(Long id, SupplierRatingUpdateDto updateDto) {
        log.info("Updating rating with ID: {}", id);

        SupplierRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", id));

        if (updateDto.getDeliveryRating() != null) {
            rating.setDeliveryRating(updateDto.getDeliveryRating());
        }
        if (updateDto.getQualityRating() != null) {
            rating.setQualityRating(updateDto.getQualityRating());
        }
        if (updateDto.getCommunicationRating() != null) {
            rating.setCommunicationRating(updateDto.getCommunicationRating());
        }
        if (updateDto.getComment() != null) {
            rating.setComment(updateDto.getComment());
        }

        SupplierRating updated = ratingRepository.save(rating);

        // Update supplier average rating
        updateSupplierAverageRating(rating.getSupplier().getId());

        log.info("Rating updated successfully with ID: {}", id);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteRating(Long id) {
        log.info("Deleting rating with ID: {}", id);

        SupplierRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", id));

        Long supplierId = rating.getSupplier().getId();
        ratingRepository.delete(rating);

        // Update supplier average rating
        updateSupplierAverageRating(supplierId);

        log.info("Rating deleted successfully with ID: {}", id);
    }

    // ================================ HELPER METHODS ================================

    @Transactional
    private void updateSupplierAverageRating(Long supplierId) {
        Double average = ratingRepository.calculateAverageRating(supplierId);
        if (average != null) {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));
            supplier.setAverageRating(BigDecimal.valueOf(average).setScale(1, java.math.RoundingMode.HALF_UP));
            supplierRepository.save(supplier);
        }
    }

    private SupplierRatingDto mapToDto(SupplierRating rating) {
        return SupplierRatingDto.builder()
                .id(rating.getId())
                .supplierId(rating.getSupplier().getId())
                .supplierCompanyName(rating.getSupplier().getCompanyName())
                .companyId(rating.getCompany().getId())
                .companyName(rating.getCompany().getName())
                .orderId(rating.getOrder().getId())
                .orderNumber(rating.getOrder().getOrderNumber())
                .deliveryRating(rating.getDeliveryRating())
                .qualityRating(rating.getQualityRating())
                .communicationRating(rating.getCommunicationRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}

