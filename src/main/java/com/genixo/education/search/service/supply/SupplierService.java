package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.Product;
import com.genixo.education.search.entity.supply.Supplier;
import com.genixo.education.search.entity.supply.SupplierRating;
import com.genixo.education.search.enumaration.OrderStatus;
import com.genixo.education.search.enumaration.QuotationStatus;
import com.genixo.education.search.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierRatingRepository supplierRatingRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final QuotationRepository quotationRepository;

    // ================================ CREATE ================================

    @Transactional
    public SupplierDto createSupplier(SupplierCreateDto createDto) {
        log.info("Creating new supplier: {}", createDto.getCompanyName());

        // Check if tax number already exists
        if (supplierRepository.existsByTaxNumber(createDto.getTaxNumber())) {
            throw BusinessException.duplicateResource("Supplier", "taxNumber", createDto.getTaxNumber());
        }

        // Check if email already exists
        if (supplierRepository.existsByEmail(createDto.getEmail())) {
            throw BusinessException.duplicateResource("Supplier", "email", createDto.getEmail());
        }

        Supplier supplier = new Supplier();
        supplier.setCompanyName(createDto.getCompanyName());
        supplier.setTaxNumber(createDto.getTaxNumber());
        supplier.setEmail(createDto.getEmail());
        supplier.setPhone(createDto.getPhone());
        supplier.setAddress(createDto.getAddress());
        supplier.setDescription(createDto.getDescription());
        supplier.setIsActive(createDto.getIsActive() != null ? createDto.getIsActive() : true);
        supplier.setAverageRating(BigDecimal.ZERO);
        supplier.setCreatedAt(LocalDateTime.now());

        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    // ================================ READ ================================

    public Page<SupplierDto> getAllSuppliers(String searchTerm, Boolean isActive, Pageable pageable) {
        log.info("Fetching suppliers with searchTerm: {}, isActive: {}", searchTerm, isActive);

        Page<Supplier> suppliers = supplierRepository.searchSuppliers(searchTerm, isActive, pageable);
        return suppliers.map(this::mapToDto);
    }

    public SupplierDto getSupplierById(Long id) {
        log.info("Fetching supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        return mapToDto(supplier);
    }

    // ================================ UPDATE ================================

    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierUpdateDto updateDto) {
        log.info("Updating supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        // Check tax number uniqueness if changed
        if (updateDto.getTaxNumber() != null && !updateDto.getTaxNumber().equals(supplier.getTaxNumber())) {
            if (supplierRepository.existsByTaxNumberAndIdNot(updateDto.getTaxNumber(), id)) {
                throw BusinessException.duplicateResource("Supplier", "taxNumber", updateDto.getTaxNumber());
            }
            supplier.setTaxNumber(updateDto.getTaxNumber());
        }

        // Check email uniqueness if changed
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.existsByEmailAndIdNot(updateDto.getEmail(), id)) {
                throw BusinessException.duplicateResource("Supplier", "email", updateDto.getEmail());
            }
            supplier.setEmail(updateDto.getEmail());
        }

        if (updateDto.getCompanyName() != null) {
            supplier.setCompanyName(updateDto.getCompanyName());
        }
        if (updateDto.getPhone() != null) {
            supplier.setPhone(updateDto.getPhone());
        }
        if (updateDto.getAddress() != null) {
            supplier.setAddress(updateDto.getAddress());
        }
        if (updateDto.getDescription() != null) {
            supplier.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIsActive() != null) {
            supplier.setIsActive(updateDto.getIsActive());
        }

        supplier.setUpdatedAt(LocalDateTime.now());
        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ ACTIVATE/DEACTIVATE ================================

    @Transactional
    public SupplierDto activateSupplier(Long id) {
        log.info("Activating supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        supplier.setIsActive(true);
        supplier.setUpdatedAt(LocalDateTime.now());
        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier activated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public SupplierDto deactivateSupplier(Long id) {
        log.info("Deactivating supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        supplier.setIsActive(false);
        supplier.setUpdatedAt(LocalDateTime.now());
        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier deactivated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ DELETE ================================

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        // Check if supplier has products
        Long productCount = productRepository.countBySupplierId(id);
        if (productCount > 0) {
            throw BusinessException.resourceInUse("Supplier", productCount + " products");
        }

        // Check if supplier has orders
        Long orderCount = orderRepository.countBySupplierId(id);
        if (orderCount > 0) {
            throw BusinessException.resourceInUse("Supplier", orderCount + " orders");
        }

        supplierRepository.delete(supplier);
        log.info("Supplier deleted successfully with ID: {}", id);
    }

    // ================================ PRODUCTS ================================

    public Page<ProductSummaryDto> getSupplierProducts(Long supplierId, Pageable pageable) {
        log.info("Fetching products for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<Product> products = productRepository.findBySupplierId(supplierId, pageable);
        return products.map(this::mapToProductSummaryDto);
    }

    // ================================ RATINGS ================================

    public Page<SupplierRatingDto> getSupplierRatings(Long supplierId, Pageable pageable) {
        log.info("Fetching ratings for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<SupplierRating> ratings = supplierRatingRepository.findBySupplierId(supplierId, pageable);
        return ratings.map(this::mapToRatingDto);
    }

    // ================================ STATISTICS ================================

    public SupplierStatisticsDto getSupplierStatistics(Long supplierId) {
        log.info("Fetching statistics for supplier ID: {}", supplierId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

        SupplierStatisticsDto statistics = SupplierStatisticsDto.builder()
                .totalProducts(productRepository.countBySupplierId(supplierId))
                .activeProducts(productRepository.countActiveBySupplierId(supplierId))
                .totalOrders(orderRepository.countBySupplierId(supplierId))
                .completedOrders(orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.DELIVERED))
                .pendingOrders(orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.PENDING))
                .totalSales(orderRepository.sumTotalAmountBySupplierId(supplierId))
                .averageOrderValue(orderRepository.avgTotalAmountBySupplierId(supplierId))
                .totalQuotations(quotationRepository.countBySupplierId(supplierId))
                .acceptedQuotations(quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.ACCEPTED))
                .totalRatings(supplierRatingRepository.countBySupplierId(supplierId))
                .averageRating(supplier.getAverageRating())
                .build();

        // Calculate quotation acceptance rate
        Long totalQuotations = statistics.getTotalQuotations();
        Long acceptedQuotations = statistics.getAcceptedQuotations();
        if (totalQuotations > 0) {
            BigDecimal rate = BigDecimal.valueOf(acceptedQuotations)
                    .divide(BigDecimal.valueOf(totalQuotations), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            statistics.setQuotationAcceptanceRate(rate);
        } else {
            statistics.setQuotationAcceptanceRate(BigDecimal.ZERO);
        }

        return statistics;
    }

    // ================================ MAPPERS ================================

    private SupplierDto mapToDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .companyName(supplier.getCompanyName())
                .taxNumber(supplier.getTaxNumber())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .isActive(supplier.getIsActive())
                .description(supplier.getDescription())
                .averageRating(supplier.getAverageRating())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    private ProductSummaryDto mapToProductSummaryDto(Product product) {
        return ProductSummaryDto.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .status(product.getStatus())
                .basePrice(product.getBasePrice())
                .currency(product.getCurrency())
                .mainImageUrl(product.getMainImageUrl())
                .stockQuantity(product.getStockQuantity())
                .build();
    }

    private SupplierRatingDto mapToRatingDto(SupplierRating rating) {
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

