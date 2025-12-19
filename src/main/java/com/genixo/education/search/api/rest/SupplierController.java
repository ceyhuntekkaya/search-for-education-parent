package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.service.supply.SupplierService;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/supply/suppliers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supplier Management", description = "APIs for managing suppliers in the supply system")
public class SupplierController {

    private final SupplierService supplierService;

    // ================================ CREATE ================================

    @PostMapping
    @Operation(summary = "Create new supplier", description = "Create a new supplier record")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid supplier data or duplicate tax number/email"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<SupplierDto>> createSupplier(
            @Valid @RequestBody SupplierCreateDto createDto,
            HttpServletRequest request) {

        SupplierDto supplierDto = supplierService.createSupplier(createDto);

        ApiResponse<SupplierDto> response = ApiResponse.success(supplierDto, "Supplier created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ READ ================================

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Get all suppliers with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<SupplierDto>>> getAllSuppliers(
            @Parameter(description = "Search term (company name, tax number, email)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SupplierDto> suppliers = supplierService.getAllSuppliers(searchTerm, isActive, pageable);

        ApiResponse<Page<SupplierDto>> response = ApiResponse.success(suppliers, "Suppliers retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Get supplier details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierDto>> getSupplierById(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            HttpServletRequest request) {

        SupplierDto supplierDto = supplierService.getSupplierById(id);

        ApiResponse<SupplierDto> response = ApiResponse.success(supplierDto, "Supplier retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ UPDATE ================================

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Update supplier information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid supplier data or duplicate tax number/email")
    })
    public ResponseEntity<ApiResponse<SupplierDto>> updateSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @Valid @RequestBody SupplierUpdateDto updateDto,
            HttpServletRequest request) {

        SupplierDto supplierDto = supplierService.updateSupplier(id, updateDto);

        ApiResponse<SupplierDto> response = ApiResponse.success(supplierDto, "Supplier updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ACTIVATE/DEACTIVATE ================================

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate supplier", description = "Activate a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierDto>> activateSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            HttpServletRequest request) {

        SupplierDto supplierDto = supplierService.activateSupplier(id);

        ApiResponse<SupplierDto> response = ApiResponse.success(supplierDto, "Supplier activated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate supplier", description = "Deactivate a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierDto>> deactivateSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            HttpServletRequest request) {

        SupplierDto supplierDto = supplierService.deactivateSupplier(id);

        ApiResponse<SupplierDto> response = ApiResponse.success(supplierDto, "Supplier deactivated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ DELETE ================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier", description = "Delete a supplier (only if no products or orders exist)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete supplier with associated products or orders")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            HttpServletRequest request) {

        supplierService.deleteSupplier(id);

        ApiResponse<Void> response = ApiResponse.success(null, "Supplier deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PRODUCTS ================================

    @GetMapping("/{id}/products")
    @Operation(summary = "Get supplier products", description = "Get all products of a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<ProductSummaryDto>>> getSupplierProducts(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryDto> products = supplierService.getSupplierProducts(id, pageable);

        ApiResponse<Page<ProductSummaryDto>> response = ApiResponse.success(products, "Products retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ RATINGS ================================

    @GetMapping("/{id}/ratings")
    @Operation(summary = "Get supplier ratings", description = "Get all ratings of a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<SupplierRatingDto>>> getSupplierRatings(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierRatingDto> ratings = supplierService.getSupplierRatings(id, pageable);

        ApiResponse<Page<SupplierRatingDto>> response = ApiResponse.success(ratings, "Ratings retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ STATISTICS ================================

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get supplier statistics", description = "Get statistics for a supplier (sales, orders, quotations, ratings)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierStatisticsDto>> getSupplierStatistics(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            HttpServletRequest request) {

        SupplierStatisticsDto statistics = supplierService.getSupplierStatistics(id);

        ApiResponse<SupplierStatisticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

