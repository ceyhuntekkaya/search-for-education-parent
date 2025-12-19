package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.enumaration.ProductStatus;
import com.genixo.education.search.service.supply.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/supply/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products in the supply system")
public class ProductController {

    private final ProductService productService;

    // ================================ PRODUCT CRUD ================================

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid product data or duplicate SKU"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier or category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductCreateDto createDto,
            HttpServletRequest request) {

        ProductDto productDto = productService.createProduct(createDto);

        ApiResponse<ProductDto> response = ApiResponse.success(productDto, "Product created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Get all products with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(
            @Parameter(description = "Search term (name, SKU, description)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by supplier ID") @RequestParam(required = false) Long supplierId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDto> products = productService.getAllProducts(searchTerm, categoryId, supplierId, status, minPrice, maxPrice, pageable);

        ApiResponse<Page<ProductDto>> response = ApiResponse.success(products, "Products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products with advanced filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<ProductDto>>> searchProducts(
            @Parameter(description = "Search term") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Supplier ID") @RequestParam(required = false) Long supplierId,
            @Parameter(description = "Status") @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getAllProducts(searchTerm, categoryId, supplierId, status, minPrice, maxPrice, pageable);

        ApiResponse<Page<ProductDto>> response = ApiResponse.success(products, "Products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get product details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        ProductDto productDto = productService.getProductById(id);

        ApiResponse<ProductDto> response = ApiResponse.success(productDto, "Product retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Get all products in a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProductsByCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByCategory(categoryId, pageable);

        ApiResponse<Page<ProductDto>> response = ApiResponse.success(products, "Products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-supplier/{supplierId}")
    @Operation(summary = "Get products by supplier", description = "Get all products from a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProductsBySupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long supplierId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsBySupplier(supplierId, pageable);

        ApiResponse<Page<ProductDto>> response = ApiResponse.success(products, "Products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update product information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid product data or duplicate SKU")
    })
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto updateDto,
            HttpServletRequest request) {

        ProductDto productDto = productService.updateProduct(id, updateDto);

        ApiResponse<ProductDto> response = ApiResponse.success(productDto, "Product updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update product status", description = "Update product status (active/passive/out of stock)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductDto>> updateProductStatus(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductStatusUpdateDto updateDto,
            HttpServletRequest request) {

        ProductDto productDto = productService.updateProductStatus(id, updateDto);

        ApiResponse<ProductDto> response = ApiResponse.success(productDto, "Product status updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock", description = "Update product stock quantity")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product stock updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductDto>> updateProductStock(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductStockUpdateDto updateDto,
            HttpServletRequest request) {

        ProductDto productDto = productService.updateProductStock(id, updateDto);

        ApiResponse<ProductDto> response = ApiResponse.success(productDto, "Product stock updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        productService.deleteProduct(id);

        ApiResponse<Void> response = ApiResponse.success(null, "Product deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRODUCT IMAGES ================================

    @GetMapping("/{id}/images")
    @Operation(summary = "Get product images", description = "Get all images of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ProductImageDto>>> getProductImages(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<ProductImageDto> images = productService.getProductImages(id);

        ApiResponse<List<ProductImageDto>> response = ApiResponse.success(images, "Images retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Add product image", description = "Add a new image to a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Image added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductImageDto>> createProductImage(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductImageCreateDto createDto,
            HttpServletRequest request) {

        ProductImageDto imageDto = productService.createProductImage(id, createDto);

        ApiResponse<ProductImageDto> response = ApiResponse.success(imageDto, "Image added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(summary = "Delete product image", description = "Delete an image from a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            HttpServletRequest request) {

        productService.deleteProductImage(id, imageId);

        ApiResponse<Void> response = ApiResponse.success(null, "Image deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/images/{imageId}/set-main")
    @Operation(summary = "Set main image", description = "Set an image as the main product image")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Main image set successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    public ResponseEntity<ApiResponse<ProductImageDto>> setMainImage(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            HttpServletRequest request) {

        ProductImageDto imageDto = productService.setMainImage(id, imageId);

        ApiResponse<ProductImageDto> response = ApiResponse.success(imageDto, "Main image set successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/images/reorder")
    @Operation(summary = "Reorder product images", description = "Change the display order of product images")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images reordered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<Void>> reorderProductImages(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductImageReorderDto reorderDto,
            HttpServletRequest request) {

        productService.reorderProductImages(id, reorderDto);

        ApiResponse<Void> response = ApiResponse.success(null, "Images reordered successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRODUCT ATTRIBUTES ================================

    @GetMapping("/{id}/attributes")
    @Operation(summary = "Get product attributes", description = "Get all attributes of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attributes retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ProductAttributeDto>>> getProductAttributes(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<ProductAttributeDto> attributes = productService.getProductAttributes(id);

        ApiResponse<List<ProductAttributeDto>> response = ApiResponse.success(attributes, "Attributes retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/attributes")
    @Operation(summary = "Add product attribute", description = "Add a new attribute to a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Attribute added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Duplicate attribute name"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductAttributeDto>> createProductAttribute(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductAttributeCreateDto createDto,
            HttpServletRequest request) {

        ProductAttributeDto attributeDto = productService.createProductAttribute(id, createDto);

        ApiResponse<ProductAttributeDto> response = ApiResponse.success(attributeDto, "Attribute added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/attributes/{attributeId}")
    @Operation(summary = "Update product attribute", description = "Update an attribute of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribute updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or attribute not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Duplicate attribute name")
    })
    public ResponseEntity<ApiResponse<ProductAttributeDto>> updateProductAttribute(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Attribute ID") @PathVariable Long attributeId,
            @Valid @RequestBody ProductAttributeUpdateDto updateDto,
            HttpServletRequest request) {

        ProductAttributeDto attributeDto = productService.updateProductAttribute(id, attributeId, updateDto);

        ApiResponse<ProductAttributeDto> response = ApiResponse.success(attributeDto, "Attribute updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/attributes/{attributeId}")
    @Operation(summary = "Delete product attribute", description = "Delete an attribute from a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribute deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or attribute not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProductAttribute(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Attribute ID") @PathVariable Long attributeId,
            HttpServletRequest request) {

        productService.deleteProductAttribute(id, attributeId);

        ApiResponse<Void> response = ApiResponse.success(null, "Attribute deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRODUCT VARIANTS ================================

    @GetMapping("/{id}/variants")
    @Operation(summary = "Get product variants", description = "Get all variants of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variants retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ProductVariantDto>>> getProductVariants(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<ProductVariantDto> variants = productService.getProductVariants(id);

        ApiResponse<List<ProductVariantDto>> response = ApiResponse.success(variants, "Variants retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Get product variant", description = "Get variant details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or variant not found")
    })
    public ResponseEntity<ApiResponse<ProductVariantDto>> getProductVariant(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Variant ID") @PathVariable Long variantId,
            HttpServletRequest request) {

        ProductVariantDto variantDto = productService.getProductVariant(id, variantId);

        ApiResponse<ProductVariantDto> response = ApiResponse.success(variantDto, "Variant retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/variants")
    @Operation(summary = "Add product variant", description = "Add a new variant to a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Variant added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Duplicate SKU"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductVariantDto>> createProductVariant(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductVariantCreateDto createDto,
            HttpServletRequest request) {

        ProductVariantDto variantDto = productService.createProductVariant(id, createDto);

        ApiResponse<ProductVariantDto> response = ApiResponse.success(variantDto, "Variant added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Update product variant", description = "Update a variant of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or variant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Duplicate SKU")
    })
    public ResponseEntity<ApiResponse<ProductVariantDto>> updateProductVariant(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Variant ID") @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantUpdateDto updateDto,
            HttpServletRequest request) {

        ProductVariantDto variantDto = productService.updateProductVariant(id, variantId, updateDto);

        ApiResponse<ProductVariantDto> response = ApiResponse.success(variantDto, "Variant updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Delete product variant", description = "Delete a variant from a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or variant not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProductVariant(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Variant ID") @PathVariable Long variantId,
            HttpServletRequest request) {

        productService.deleteProductVariant(id, variantId);

        ApiResponse<Void> response = ApiResponse.success(null, "Variant deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/variants/{variantId}/activate")
    @Operation(summary = "Activate variant", description = "Activate a product variant")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or variant not found")
    })
    public ResponseEntity<ApiResponse<ProductVariantDto>> activateVariant(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Variant ID") @PathVariable Long variantId,
            HttpServletRequest request) {

        ProductVariantDto variantDto = productService.activateVariant(id, variantId);

        ApiResponse<ProductVariantDto> response = ApiResponse.success(variantDto, "Variant activated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/variants/{variantId}/deactivate")
    @Operation(summary = "Deactivate variant", description = "Deactivate a product variant")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or variant not found")
    })
    public ResponseEntity<ApiResponse<ProductVariantDto>> deactivateVariant(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Variant ID") @PathVariable Long variantId,
            HttpServletRequest request) {

        ProductVariantDto variantDto = productService.deactivateVariant(id, variantId);

        ApiResponse<ProductVariantDto> response = ApiResponse.success(variantDto, "Variant deactivated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/variants/{variantId}/stock")
    @Operation(summary = "Update variant stock", description = "Update stock quantity of a variant")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant stock updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or variant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductVariantDto>> updateVariantStock(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Variant ID") @PathVariable Long variantId,
            @Valid @RequestBody ProductStockUpdateDto updateDto,
            HttpServletRequest request) {

        ProductVariantDto variantDto = productService.updateVariantStock(id, variantId, updateDto);

        ApiResponse<ProductVariantDto> response = ApiResponse.success(variantDto, "Variant stock updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRODUCT DISCOUNTS ================================

    @GetMapping("/{id}/discounts")
    @Operation(summary = "Get product discounts", description = "Get all discounts of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discounts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ProductDiscountDto>>> getProductDiscounts(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<ProductDiscountDto> discounts = productService.getProductDiscounts(id);

        ApiResponse<List<ProductDiscountDto>> response = ApiResponse.success(discounts, "Discounts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/discounts/{discountId}")
    @Operation(summary = "Get product discount", description = "Get discount details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discount retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or discount not found")
    })
    public ResponseEntity<ApiResponse<ProductDiscountDto>> getProductDiscount(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Discount ID") @PathVariable Long discountId,
            HttpServletRequest request) {

        ProductDiscountDto discountDto = productService.getProductDiscount(id, discountId);

        ApiResponse<ProductDiscountDto> response = ApiResponse.success(discountDto, "Discount retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/discounts")
    @Operation(summary = "Add product discount", description = "Add a new discount to a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Discount added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductDiscountDto>> createProductDiscount(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductDiscountCreateDto createDto,
            HttpServletRequest request) {

        ProductDiscountDto discountDto = productService.createProductDiscount(id, createDto);

        ApiResponse<ProductDiscountDto> response = ApiResponse.success(discountDto, "Discount added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/discounts/{discountId}")
    @Operation(summary = "Update product discount", description = "Update a discount of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discount updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or discount not found")
    })
    public ResponseEntity<ApiResponse<ProductDiscountDto>> updateProductDiscount(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Discount ID") @PathVariable Long discountId,
            @Valid @RequestBody ProductDiscountUpdateDto updateDto,
            HttpServletRequest request) {

        ProductDiscountDto discountDto = productService.updateProductDiscount(id, discountId, updateDto);

        ApiResponse<ProductDiscountDto> response = ApiResponse.success(discountDto, "Discount updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/discounts/{discountId}")
    @Operation(summary = "Delete product discount", description = "Delete a discount from a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discount deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or discount not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProductDiscount(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Discount ID") @PathVariable Long discountId,
            HttpServletRequest request) {

        productService.deleteProductDiscount(id, discountId);

        ApiResponse<Void> response = ApiResponse.success(null, "Discount deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/discounts/{discountId}/activate")
    @Operation(summary = "Activate discount", description = "Activate a product discount")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discount activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or discount not found")
    })
    public ResponseEntity<ApiResponse<ProductDiscountDto>> activateDiscount(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Discount ID") @PathVariable Long discountId,
            HttpServletRequest request) {

        ProductDiscountDto discountDto = productService.activateDiscount(id, discountId);

        ApiResponse<ProductDiscountDto> response = ApiResponse.success(discountDto, "Discount activated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/discounts/{discountId}/deactivate")
    @Operation(summary = "Deactivate discount", description = "Deactivate a product discount")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discount deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or discount not found")
    })
    public ResponseEntity<ApiResponse<ProductDiscountDto>> deactivateDiscount(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Discount ID") @PathVariable Long discountId,
            HttpServletRequest request) {

        ProductDiscountDto discountDto = productService.deactivateDiscount(id, discountId);

        ApiResponse<ProductDiscountDto> response = ApiResponse.success(discountDto, "Discount deactivated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/effective-price")
    @Operation(summary = "Get effective price", description = "Calculate effective price with discounts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Effective price calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<EffectivePriceDto>> getEffectivePrice(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Quantity for discount calculation") @RequestParam(required = false) Integer quantity,
            HttpServletRequest request) {

        EffectivePriceDto priceDto = productService.getEffectivePrice(id, quantity);

        ApiResponse<EffectivePriceDto> response = ApiResponse.success(priceDto, "Effective price calculated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRODUCT DOCUMENTS ================================

    @GetMapping("/{id}/documents")
    @Operation(summary = "Get product documents", description = "Get all documents of a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ProductDocumentDto>>> getProductDocuments(
            @Parameter(description = "Product ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<ProductDocumentDto> documents = productService.getProductDocuments(id);

        ApiResponse<List<ProductDocumentDto>> response = ApiResponse.success(documents, "Documents retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/documents")
    @Operation(summary = "Add product document", description = "Add a new document to a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Document added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<ProductDocumentDto>> createProductDocument(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductDocumentCreateDto createDto,
            HttpServletRequest request) {

        ProductDocumentDto documentDto = productService.createProductDocument(id, createDto);

        ApiResponse<ProductDocumentDto> response = ApiResponse.success(documentDto, "Document added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @Operation(summary = "Delete product document", description = "Delete a document from a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Document deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or document not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProductDocument(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Document ID") @PathVariable Long documentId,
            HttpServletRequest request) {

        productService.deleteProductDocument(id, documentId);

        ApiResponse<Void> response = ApiResponse.success(null, "Document deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

