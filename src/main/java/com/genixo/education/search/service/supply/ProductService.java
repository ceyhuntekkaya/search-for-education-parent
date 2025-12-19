package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.enumaration.DiscountType;
import com.genixo.education.search.enumaration.ProductStatus;
import com.genixo.education.search.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ProductDocumentRepository productDocumentRepository;

    // ================================ PRODUCT CRUD ================================

    @Transactional
    public ProductDto createProduct(ProductCreateDto createDto) {
        log.info("Creating new product: {}", createDto.getName());

        // Validate supplier
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", createDto.getSupplierId()));

        if (!supplier.getIsActive()) {
            throw new BusinessException("Cannot create product for inactive supplier");
        }

        // Validate category
        Category category = categoryRepository.findById(createDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", createDto.getCategoryId()));

        if (!category.getIsActive()) {
            throw new BusinessException("Cannot create product in inactive category");
        }

        // Check SKU uniqueness
        if (createDto.getSku() != null && productRepository.existsBySku(createDto.getSku())) {
            throw BusinessException.duplicateResource("Product", "sku", createDto.getSku());
        }

        Product product = new Product();
        product.setSupplier(supplier);
        product.setCategory(category);
        product.setName(createDto.getName());
        product.setSku(createDto.getSku());
        product.setDescription(createDto.getDescription());
        product.setTechnicalSpecs(createDto.getTechnicalSpecs());
        product.setStatus(createDto.getStatus() != null ? createDto.getStatus() : ProductStatus.ACTIVE);
        product.setStockTrackingType(createDto.getStockTrackingType() != null ? createDto.getStockTrackingType() : com.genixo.education.search.enumaration.StockTrackingType.LIMITED);
        product.setStockQuantity(createDto.getStockQuantity() != null ? createDto.getStockQuantity() : 0);
        product.setMinStockLevel(createDto.getMinStockLevel() != null ? createDto.getMinStockLevel() : 0);
        product.setBasePrice(createDto.getBasePrice());
        product.setCurrency(createDto.getCurrency() != null ? createDto.getCurrency() : com.genixo.education.search.enumaration.Currency.TRY);
        product.setTaxRate(createDto.getTaxRate() != null ? createDto.getTaxRate() : new BigDecimal("20.00"));
        product.setMinOrderQuantity(createDto.getMinOrderQuantity() != null ? createDto.getMinOrderQuantity() : 1);
        product.setDeliveryDays(createDto.getDeliveryDays() != null ? createDto.getDeliveryDays() : 7);
        product.setMainImageUrl(createDto.getMainImageUrl());
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        log.info("Product created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    public Page<ProductDto> getAllProducts(String searchTerm, Long categoryId, Long supplierId, 
                                          ProductStatus status, BigDecimal minPrice, BigDecimal maxPrice, 
                                          Pageable pageable) {
        log.info("Fetching products with filters");

        Page<Product> products = productRepository.searchProducts(searchTerm, categoryId, supplierId, status, minPrice, maxPrice, pageable);
        return products.map(this::mapToDto);
    }

    public ProductDto getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        return mapToDto(product);
    }

    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching products for category ID: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }

        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::mapToDto);
    }

    public Page<ProductDto> getProductsBySupplier(Long supplierId, Pageable pageable) {
        log.info("Fetching products for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<Product> products = productRepository.findBySupplierId(supplierId, pageable);
        return products.map(this::mapToDto);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductUpdateDto updateDto) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        // Check SKU uniqueness if changed
        if (updateDto.getSku() != null && !updateDto.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(updateDto.getSku(), id)) {
                throw BusinessException.duplicateResource("Product", "sku", updateDto.getSku());
            }
            product.setSku(updateDto.getSku());
        }

        if (updateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", updateDto.getCategoryId()));
            product.setCategory(category);
        }

        if (updateDto.getName() != null) {
            product.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            product.setDescription(updateDto.getDescription());
        }
        if (updateDto.getTechnicalSpecs() != null) {
            product.setTechnicalSpecs(updateDto.getTechnicalSpecs());
        }
        if (updateDto.getStatus() != null) {
            product.setStatus(updateDto.getStatus());
        }
        if (updateDto.getStockTrackingType() != null) {
            product.setStockTrackingType(updateDto.getStockTrackingType());
        }
        if (updateDto.getStockQuantity() != null) {
            product.setStockQuantity(updateDto.getStockQuantity());
        }
        if (updateDto.getMinStockLevel() != null) {
            product.setMinStockLevel(updateDto.getMinStockLevel());
        }
        if (updateDto.getBasePrice() != null) {
            product.setBasePrice(updateDto.getBasePrice());
        }
        if (updateDto.getCurrency() != null) {
            product.setCurrency(updateDto.getCurrency());
        }
        if (updateDto.getTaxRate() != null) {
            product.setTaxRate(updateDto.getTaxRate());
        }
        if (updateDto.getMinOrderQuantity() != null) {
            product.setMinOrderQuantity(updateDto.getMinOrderQuantity());
        }
        if (updateDto.getDeliveryDays() != null) {
            product.setDeliveryDays(updateDto.getDeliveryDays());
        }
        if (updateDto.getMainImageUrl() != null) {
            product.setMainImageUrl(updateDto.getMainImageUrl());
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updated = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public ProductDto updateProductStatus(Long id, ProductStatusUpdateDto updateDto) {
        log.info("Updating product status for ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setStatus(updateDto.getStatus());
        product.setUpdatedAt(LocalDateTime.now());
        Product updated = productRepository.save(product);
        log.info("Product status updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public ProductDto updateProductStock(Long id, ProductStockUpdateDto updateDto) {
        log.info("Updating product stock for ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setStockQuantity(updateDto.getStockQuantity());
        product.setUpdatedAt(LocalDateTime.now());
        Product updated = productRepository.save(product);
        log.info("Product stock updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        // Check if product has orders (this would require OrderItem repository)
        // For now, we'll just delete the product

        productRepository.delete(product);
        log.info("Product deleted successfully with ID: {}", id);
    }

    // ================================ PRODUCT IMAGES ================================

    public List<ProductImageDto> getProductImages(Long productId) {
        log.info("Fetching images for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        return images.stream()
                .map(this::mapToImageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductImageDto createProductImage(Long productId, ProductImageCreateDto createDto) {
        log.info("Creating image for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(createDto.getImageUrl());
        
        if (createDto.getDisplayOrder() != null) {
            image.setDisplayOrder(createDto.getDisplayOrder());
        } else {
            Integer maxOrder = productImageRepository.findMaxDisplayOrderByProductId(productId);
            image.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 0);
        }
        
        image.setCreatedAt(LocalDateTime.now());

        ProductImage saved = productImageRepository.save(image);
        log.info("Product image created successfully with ID: {}", saved.getId());

        return mapToImageDto(saved);
    }

    @Transactional
    public void deleteProductImage(Long productId, Long imageId) {
        log.info("Deleting image ID: {} for product ID: {}", imageId, productId);

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Image", imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Image", imageId);
        }

        productImageRepository.delete(image);
        log.info("Product image deleted successfully with ID: {}", imageId);
    }

    @Transactional
    public ProductImageDto setMainImage(Long productId, Long imageId) {
        log.info("Setting main image for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Image", imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Image", imageId);
        }

        product.setMainImageUrl(image.getImageUrl());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Main image set successfully for product ID: {}", productId);
        return mapToImageDto(image);
    }

    @Transactional
    public void reorderProductImages(Long productId, ProductImageReorderDto reorderDto) {
        log.info("Reordering images for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        List<Long> imageIds = reorderDto.getImageIds();
        for (int i = 0; i < imageIds.size(); i++) {
            final int displayOrder = i;
            final Long imageId = imageIds.get(i);
            ProductImage image = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product Image", imageId));
            
            if (!image.getProduct().getId().equals(productId)) {
                throw new BusinessException("Image does not belong to this product");
            }
            
            image.setDisplayOrder(displayOrder);
            productImageRepository.save(image);
        }

        log.info("Product images reordered successfully for product ID: {}", productId);
    }

    // ================================ PRODUCT ATTRIBUTES ================================

    public List<ProductAttributeDto> getProductAttributes(Long productId) {
        log.info("Fetching attributes for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        List<ProductAttribute> attributes = productAttributeRepository.findByProductId(productId);
        return attributes.stream()
                .map(this::mapToAttributeDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductAttributeDto createProductAttribute(Long productId, ProductAttributeCreateDto createDto) {
        log.info("Creating attribute for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        // Check attribute name uniqueness
        if (productAttributeRepository.existsByProductIdAndAttributeName(productId, createDto.getAttributeName())) {
            throw BusinessException.duplicateResource("Product Attribute", "attributeName", createDto.getAttributeName());
        }

        ProductAttribute attribute = new ProductAttribute();
        attribute.setProduct(product);
        attribute.setAttributeName(createDto.getAttributeName());
        attribute.setAttributeValue(createDto.getAttributeValue());

        ProductAttribute saved = productAttributeRepository.save(attribute);
        log.info("Product attribute created successfully with ID: {}", saved.getId());

        return mapToAttributeDto(saved);
    }

    @Transactional
    public ProductAttributeDto updateProductAttribute(Long productId, Long attributeId, ProductAttributeUpdateDto updateDto) {
        log.info("Updating attribute ID: {} for product ID: {}", attributeId, productId);

        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Attribute", attributeId));

        if (!attribute.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Attribute", attributeId);
        }

        // Check attribute name uniqueness if changed
        if (updateDto.getAttributeName() != null && !updateDto.getAttributeName().equals(attribute.getAttributeName())) {
            if (productAttributeRepository.existsByProductIdAndAttributeNameAndIdNot(productId, updateDto.getAttributeName(), attributeId)) {
                throw BusinessException.duplicateResource("Product Attribute", "attributeName", updateDto.getAttributeName());
            }
            attribute.setAttributeName(updateDto.getAttributeName());
        }

        if (updateDto.getAttributeValue() != null) {
            attribute.setAttributeValue(updateDto.getAttributeValue());
        }

        ProductAttribute updated = productAttributeRepository.save(attribute);
        log.info("Product attribute updated successfully with ID: {}", attributeId);

        return mapToAttributeDto(updated);
    }

    @Transactional
    public void deleteProductAttribute(Long productId, Long attributeId) {
        log.info("Deleting attribute ID: {} for product ID: {}", attributeId, productId);

        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Attribute", attributeId));

        if (!attribute.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Attribute", attributeId);
        }

        productAttributeRepository.delete(attribute);
        log.info("Product attribute deleted successfully with ID: {}", attributeId);
    }

    // ================================ PRODUCT VARIANTS ================================

    public List<ProductVariantDto> getProductVariants(Long productId) {
        log.info("Fetching variants for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        return variants.stream()
                .map(this::mapToVariantDto)
                .collect(Collectors.toList());
    }

    public ProductVariantDto getProductVariant(Long productId, Long variantId) {
        log.info("Fetching variant ID: {} for product ID: {}", variantId, productId);

        ProductVariant variant = productVariantRepository.findByProductIdAndId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", variantId));

        return mapToVariantDto(variant);
    }

    @Transactional
    public ProductVariantDto createProductVariant(Long productId, ProductVariantCreateDto createDto) {
        log.info("Creating variant for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        // Check SKU uniqueness if provided
        if (createDto.getSku() != null && productVariantRepository.existsBySku(createDto.getSku())) {
            throw BusinessException.duplicateResource("Product Variant", "sku", createDto.getSku());
        }

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setVariantName(createDto.getVariantName());
        variant.setSku(createDto.getSku());
        variant.setPriceAdjustment(createDto.getPriceAdjustment() != null ? createDto.getPriceAdjustment() : BigDecimal.ZERO);
        variant.setStockQuantity(createDto.getStockQuantity() != null ? createDto.getStockQuantity() : 0);
        variant.setIsActive(createDto.getIsActive() != null ? createDto.getIsActive() : true);

        ProductVariant saved = productVariantRepository.save(variant);
        log.info("Product variant created successfully with ID: {}", saved.getId());

        return mapToVariantDto(saved);
    }

    @Transactional
    public ProductVariantDto updateProductVariant(Long productId, Long variantId, ProductVariantUpdateDto updateDto) {
        log.info("Updating variant ID: {} for product ID: {}", variantId, productId);

        ProductVariant variant = productVariantRepository.findByProductIdAndId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", variantId));

        // Check SKU uniqueness if changed
        if (updateDto.getSku() != null && !updateDto.getSku().equals(variant.getSku())) {
            if (productVariantRepository.existsBySkuAndIdNot(updateDto.getSku(), variantId)) {
                throw BusinessException.duplicateResource("Product Variant", "sku", updateDto.getSku());
            }
            variant.setSku(updateDto.getSku());
        }

        if (updateDto.getVariantName() != null) {
            variant.setVariantName(updateDto.getVariantName());
        }
        if (updateDto.getPriceAdjustment() != null) {
            variant.setPriceAdjustment(updateDto.getPriceAdjustment());
        }
        if (updateDto.getStockQuantity() != null) {
            variant.setStockQuantity(updateDto.getStockQuantity());
        }
        if (updateDto.getIsActive() != null) {
            variant.setIsActive(updateDto.getIsActive());
        }

        ProductVariant updated = productVariantRepository.save(variant);
        log.info("Product variant updated successfully with ID: {}", variantId);

        return mapToVariantDto(updated);
    }

    @Transactional
    public void deleteProductVariant(Long productId, Long variantId) {
        log.info("Deleting variant ID: {} for product ID: {}", variantId, productId);

        ProductVariant variant = productVariantRepository.findByProductIdAndId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", variantId));

        productVariantRepository.delete(variant);
        log.info("Product variant deleted successfully with ID: {}", variantId);
    }

    @Transactional
    public ProductVariantDto activateVariant(Long productId, Long variantId) {
        log.info("Activating variant ID: {} for product ID: {}", variantId, productId);

        ProductVariant variant = productVariantRepository.findByProductIdAndId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", variantId));

        variant.setIsActive(true);
        ProductVariant updated = productVariantRepository.save(variant);
        log.info("Product variant activated successfully with ID: {}", variantId);

        return mapToVariantDto(updated);
    }

    @Transactional
    public ProductVariantDto deactivateVariant(Long productId, Long variantId) {
        log.info("Deactivating variant ID: {} for product ID: {}", variantId, productId);

        ProductVariant variant = productVariantRepository.findByProductIdAndId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", variantId));

        variant.setIsActive(false);
        ProductVariant updated = productVariantRepository.save(variant);
        log.info("Product variant deactivated successfully with ID: {}", variantId);

        return mapToVariantDto(updated);
    }

    @Transactional
    public ProductVariantDto updateVariantStock(Long productId, Long variantId, ProductStockUpdateDto updateDto) {
        log.info("Updating variant stock for ID: {}", variantId);

        ProductVariant variant = productVariantRepository.findByProductIdAndId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", variantId));

        variant.setStockQuantity(updateDto.getStockQuantity());
        ProductVariant updated = productVariantRepository.save(variant);
        log.info("Product variant stock updated successfully with ID: {}", variantId);

        return mapToVariantDto(updated);
    }

    // ================================ PRODUCT DISCOUNTS ================================

    public List<ProductDiscountDto> getProductDiscounts(Long productId) {
        log.info("Fetching discounts for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        List<ProductDiscount> discounts = productDiscountRepository.findByProductId(productId);
        return discounts.stream()
                .map(this::mapToDiscountDto)
                .collect(Collectors.toList());
    }

    public ProductDiscountDto getProductDiscount(Long productId, Long discountId) {
        log.info("Fetching discount ID: {} for product ID: {}", discountId, productId);

        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Discount", discountId));

        if (!discount.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Discount", discountId);
        }

        return mapToDiscountDto(discount);
    }

    @Transactional
    public ProductDiscountDto createProductDiscount(Long productId, ProductDiscountCreateDto createDto) {
        log.info("Creating discount for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        ProductDiscount discount = new ProductDiscount();
        discount.setProduct(product);
        discount.setDiscountName(createDto.getDiscountName());
        discount.setDiscountType(createDto.getDiscountType());
        discount.setDiscountValue(createDto.getDiscountValue());
        discount.setMinQuantity(createDto.getMinQuantity());
        discount.setMaxQuantity(createDto.getMaxQuantity());
        discount.setStartDate(createDto.getStartDate());
        discount.setEndDate(createDto.getEndDate());
        discount.setIsActive(createDto.getIsActive() != null ? createDto.getIsActive() : true);
        discount.setCreatedAt(LocalDateTime.now());

        ProductDiscount saved = productDiscountRepository.save(discount);
        log.info("Product discount created successfully with ID: {}", saved.getId());

        return mapToDiscountDto(saved);
    }

    @Transactional
    public ProductDiscountDto updateProductDiscount(Long productId, Long discountId, ProductDiscountUpdateDto updateDto) {
        log.info("Updating discount ID: {} for product ID: {}", discountId, productId);

        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Discount", discountId));

        if (!discount.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Discount", discountId);
        }

        if (updateDto.getDiscountName() != null) {
            discount.setDiscountName(updateDto.getDiscountName());
        }
        if (updateDto.getDiscountType() != null) {
            discount.setDiscountType(updateDto.getDiscountType());
        }
        if (updateDto.getDiscountValue() != null) {
            discount.setDiscountValue(updateDto.getDiscountValue());
        }
        if (updateDto.getMinQuantity() != null) {
            discount.setMinQuantity(updateDto.getMinQuantity());
        }
        if (updateDto.getMaxQuantity() != null) {
            discount.setMaxQuantity(updateDto.getMaxQuantity());
        }
        if (updateDto.getStartDate() != null) {
            discount.setStartDate(updateDto.getStartDate());
        }
        if (updateDto.getEndDate() != null) {
            discount.setEndDate(updateDto.getEndDate());
        }
        if (updateDto.getIsActive() != null) {
            discount.setIsActive(updateDto.getIsActive());
        }

        ProductDiscount updated = productDiscountRepository.save(discount);
        log.info("Product discount updated successfully with ID: {}", discountId);

        return mapToDiscountDto(updated);
    }

    @Transactional
    public void deleteProductDiscount(Long productId, Long discountId) {
        log.info("Deleting discount ID: {} for product ID: {}", discountId, productId);

        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Discount", discountId));

        if (!discount.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Discount", discountId);
        }

        productDiscountRepository.delete(discount);
        log.info("Product discount deleted successfully with ID: {}", discountId);
    }

    @Transactional
    public ProductDiscountDto activateDiscount(Long productId, Long discountId) {
        log.info("Activating discount ID: {} for product ID: {}", discountId, productId);

        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Discount", discountId));

        if (!discount.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Discount", discountId);
        }

        discount.setIsActive(true);
        ProductDiscount updated = productDiscountRepository.save(discount);
        log.info("Product discount activated successfully with ID: {}", discountId);

        return mapToDiscountDto(updated);
    }

    @Transactional
    public ProductDiscountDto deactivateDiscount(Long productId, Long discountId) {
        log.info("Deactivating discount ID: {} for product ID: {}", discountId, productId);

        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Discount", discountId));

        if (!discount.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Discount", discountId);
        }

        discount.setIsActive(false);
        ProductDiscount updated = productDiscountRepository.save(discount);
        log.info("Product discount deactivated successfully with ID: {}", discountId);

        return mapToDiscountDto(updated);
    }

    public EffectivePriceDto getEffectivePrice(Long productId, Integer quantity) {
        log.info("Calculating effective price for product ID: {} with quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        BigDecimal basePrice = product.getBasePrice();
        BigDecimal discountAmount = BigDecimal.ZERO;
        ProductDiscount appliedDiscount = null;

        // Find applicable discount
        if (quantity != null && quantity > 0) {
            List<ProductDiscount> activeDiscounts = productDiscountRepository.findActiveDiscountsByProductId(
                    productId, LocalDate.now());

            for (ProductDiscount discount : activeDiscounts) {
                if (discount.getMinQuantity() != null && quantity < discount.getMinQuantity()) {
                    continue;
                }
                if (discount.getMaxQuantity() != null && quantity > discount.getMaxQuantity()) {
                    continue;
                }

                BigDecimal calculatedDiscount = BigDecimal.ZERO;
                if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
                    calculatedDiscount = basePrice.multiply(discount.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else if (discount.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                    calculatedDiscount = discount.getDiscountValue();
                }

                if (calculatedDiscount.compareTo(discountAmount) > 0) {
                    discountAmount = calculatedDiscount;
                    appliedDiscount = discount;
                }
            }
        }

        BigDecimal finalPrice = basePrice.subtract(discountAmount);
        BigDecimal taxAmount = finalPrice.multiply(product.getTaxRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = finalPrice.add(taxAmount);

        return EffectivePriceDto.builder()
                .basePrice(basePrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .taxAmount(taxAmount)
                .totalPrice(totalPrice)
                .currency(product.getCurrency())
                .appliedDiscountId(appliedDiscount != null ? appliedDiscount.getId() : null)
                .appliedDiscountName(appliedDiscount != null ? appliedDiscount.getDiscountName() : null)
                .build();
    }

    // ================================ PRODUCT DOCUMENTS ================================

    public List<ProductDocumentDto> getProductDocuments(Long productId) {
        log.info("Fetching documents for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        List<ProductDocument> documents = productDocumentRepository.findByProductId(productId);
        return documents.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDocumentDto createProductDocument(Long productId, ProductDocumentCreateDto createDto) {
        log.info("Creating document for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        ProductDocument document = new ProductDocument();
        document.setProduct(product);
        document.setDocumentName(createDto.getDocumentName());
        document.setDocumentUrl(createDto.getDocumentUrl());
        document.setDocumentType(createDto.getDocumentType());
        document.setCreatedAt(LocalDateTime.now());

        ProductDocument saved = productDocumentRepository.save(document);
        log.info("Product document created successfully with ID: {}", saved.getId());

        return mapToDocumentDto(saved);
    }

    @Transactional
    public void deleteProductDocument(Long productId, Long documentId) {
        log.info("Deleting document ID: {} for product ID: {}", documentId, productId);

        ProductDocument document = productDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Document", documentId));

        if (!document.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Product Document", documentId);
        }

        productDocumentRepository.delete(document);
        log.info("Product document deleted successfully with ID: {}", documentId);
    }

    // ================================ MAPPERS ================================

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .supplierId(product.getSupplier().getId())
                .supplierCompanyName(product.getSupplier().getCompanyName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .technicalSpecs(product.getTechnicalSpecs())
                .status(product.getStatus())
                .stockTrackingType(product.getStockTrackingType())
                .stockQuantity(product.getStockQuantity())
                .minStockLevel(product.getMinStockLevel())
                .basePrice(product.getBasePrice())
                .currency(product.getCurrency())
                .taxRate(product.getTaxRate())
                .minOrderQuantity(product.getMinOrderQuantity())
                .deliveryDays(product.getDeliveryDays())
                .mainImageUrl(product.getMainImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductImageDto mapToImageDto(ProductImage image) {
        return ProductImageDto.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private ProductAttributeDto mapToAttributeDto(ProductAttribute attribute) {
        return ProductAttributeDto.builder()
                .id(attribute.getId())
                .productId(attribute.getProduct().getId())
                .productName(attribute.getProduct().getName())
                .attributeName(attribute.getAttributeName())
                .attributeValue(attribute.getAttributeValue())
                .build();
    }

    private ProductVariantDto mapToVariantDto(ProductVariant variant) {
        return ProductVariantDto.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .variantName(variant.getVariantName())
                .sku(variant.getSku())
                .priceAdjustment(variant.getPriceAdjustment())
                .stockQuantity(variant.getStockQuantity())
                .isActive(variant.getIsActive())
                .build();
    }

    private ProductDiscountDto mapToDiscountDto(ProductDiscount discount) {
        return ProductDiscountDto.builder()
                .id(discount.getId())
                .productId(discount.getProduct().getId())
                .productName(discount.getProduct().getName())
                .discountName(discount.getDiscountName())
                .discountType(discount.getDiscountType())
                .discountValue(discount.getDiscountValue())
                .minQuantity(discount.getMinQuantity())
                .maxQuantity(discount.getMaxQuantity())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(discount.getIsActive())
                .createdAt(discount.getCreatedAt())
                .build();
    }

    private ProductDocumentDto mapToDocumentDto(ProductDocument document) {
        return ProductDocumentDto.builder()
                .id(document.getId())
                .productId(document.getProduct().getId())
                .productName(document.getProduct().getName())
                .documentName(document.getDocumentName())
                .documentUrl(document.getDocumentUrl())
                .documentType(document.getDocumentType())
                .createdAt(document.getCreatedAt())
                .build();
    }
}

