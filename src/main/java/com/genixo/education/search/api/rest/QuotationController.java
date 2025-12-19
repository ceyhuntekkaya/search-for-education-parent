package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.enumaration.QuotationStatus;
import com.genixo.education.search.service.supply.QuotationService;
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
import java.util.List;

@RestController
@RequestMapping("/supply/quotations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quotation Management", description = "APIs for managing quotations in the supply system")
public class QuotationController {

    private final QuotationService quotationService;

    // ================================ QUOTATION CRUD ================================

    @PostMapping
    @Operation(summary = "Create new quotation", description = "Create a new quotation for an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Quotation created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid quotation data or RFQ not published"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or supplier not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> createQuotation(
            @Valid @RequestBody QuotationCreateDto createDto,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.createQuotation(createDto);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all quotations", description = "Get all quotations with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotations retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<QuotationDto>>> getAllQuotations(
            @Parameter(description = "Search term (notes)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by RFQ ID") @RequestParam(required = false) Long rfqId,
            @Parameter(description = "Filter by supplier ID") @RequestParam(required = false) Long supplierId,
            @Parameter(description = "Filter by company ID") @RequestParam(required = false) Long companyId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) QuotationStatus status,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<QuotationDto> quotations = quotationService.getAllQuotations(searchTerm, rfqId, supplierId, companyId, status, pageable);

        ApiResponse<Page<QuotationDto>> response = ApiResponse.success(quotations, "Quotations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quotation by ID", description = "Get quotation details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> getQuotationById(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.getQuotationById(id);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-rfq/{rfqId}")
    @Operation(summary = "Get quotations by RFQ", description = "Get all quotations for an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found")
    })
    public ResponseEntity<ApiResponse<Page<QuotationDto>>> getQuotationsByRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long rfqId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<QuotationDto> quotations = quotationService.getQuotationsByRFQ(rfqId, pageable);

        ApiResponse<Page<QuotationDto>> response = ApiResponse.success(quotations, "Quotations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-supplier/{supplierId}")
    @Operation(summary = "Get quotations by supplier", description = "Get all quotations from a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<QuotationDto>>> getQuotationsBySupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long supplierId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<QuotationDto> quotations = quotationService.getQuotationsBySupplier(supplierId, pageable);

        ApiResponse<Page<QuotationDto>> response = ApiResponse.success(quotations, "Quotations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "Get quotations by company", description = "Get all quotations for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotations retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<QuotationDto>>> getQuotationsByCompany(
            @Parameter(description = "Company ID") @PathVariable Long companyId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<QuotationDto> quotations = quotationService.getQuotationsByCompany(companyId, pageable);

        ApiResponse<Page<QuotationDto>> response = ApiResponse.success(quotations, "Quotations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quotation", description = "Update quotation information (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update quotation that is not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> updateQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            @Valid @RequestBody QuotationUpdateDto updateDto,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.updateQuotation(id, updateDto);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/submit")
    @Operation(summary = "Submit quotation", description = "Submit a DRAFT quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot submit quotation without items or not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> submitQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.submitQuotation(id);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation submitted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/accept")
    @Operation(summary = "Accept quotation", description = "Accept a SUBMITTED or UNDER_REVIEW quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation accepted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot accept quotation that is not SUBMITTED or UNDER_REVIEW")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> acceptQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.acceptQuotation(id);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation accepted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject quotation", description = "Reject a SUBMITTED or UNDER_REVIEW quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot reject quotation that is not SUBMITTED or UNDER_REVIEW")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> rejectQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.rejectQuotation(id);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation rejected successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quotation", description = "Delete a quotation (cannot delete ACCEPTED quotations)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotation deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete ACCEPTED quotation")
    })
    public ResponseEntity<ApiResponse<Void>> deleteQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        quotationService.deleteQuotation(id);

        ApiResponse<Void> response = ApiResponse.success(null, "Quotation deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get quotation versions", description = "Get all versions of a quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Versions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    public ResponseEntity<ApiResponse<List<QuotationVersionDto>>> getQuotationVersions(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<QuotationVersionDto> versions = quotationService.getQuotationVersions(id);

        ApiResponse<List<QuotationVersionDto>> response = ApiResponse.success(versions, "Versions retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate quotation", description = "Create a new version of a quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Quotation duplicated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    public ResponseEntity<ApiResponse<QuotationDto>> duplicateQuotation(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        QuotationDto quotationDto = quotationService.duplicateQuotation(id);

        ApiResponse<QuotationDto> response = ApiResponse.success(quotationDto, "Quotation duplicated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ QUOTATION ITEMS ================================

    @GetMapping("/{id}/items")
    @Operation(summary = "Get quotation items", description = "Get all items of a quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    public ResponseEntity<ApiResponse<List<QuotationItemDto>>> getQuotationItems(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<QuotationItemDto> items = quotationService.getQuotationItems(id);

        ApiResponse<List<QuotationItemDto>> response = ApiResponse.success(items, "Items retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add quotation item", description = "Add a new item to a quotation (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation, RFQ item or product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot add items to quotation that is not in DRAFT status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<QuotationItemDto>> createQuotationItem(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            @Valid @RequestBody QuotationItemCreateDto createDto,
            HttpServletRequest request) {

        QuotationItemDto itemDto = quotationService.createQuotationItem(id, createDto);

        ApiResponse<QuotationItemDto> response = ApiResponse.success(itemDto, "Item added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/items/{itemId}")
    @Operation(summary = "Update quotation item", description = "Update an item of a quotation (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation or item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update items in quotation that is not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<QuotationItemDto>> updateQuotationItem(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            @Valid @RequestBody QuotationItemUpdateDto updateDto,
            HttpServletRequest request) {

        QuotationItemDto itemDto = quotationService.updateQuotationItem(id, itemId, updateDto);

        ApiResponse<QuotationItemDto> response = ApiResponse.success(itemDto, "Item updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Delete quotation item", description = "Delete an item from a quotation (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation or item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete items from quotation that is not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<Void>> deleteQuotationItem(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            HttpServletRequest request) {

        quotationService.deleteQuotationItem(id, itemId);

        ApiResponse<Void> response = ApiResponse.success(null, "Item deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/items/{itemId}/discount")
    @Operation(summary = "Apply item discount", description = "Apply discount to a quotation item (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Discount applied successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation or item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot apply discount to items in quotation that is not in DRAFT status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<QuotationItemDto>> applyItemDiscount(
            @Parameter(description = "Quotation ID") @PathVariable Long id,
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            @Valid @RequestBody QuotationItemDiscountDto discountDto,
            HttpServletRequest request) {

        QuotationItemDto itemDto = quotationService.applyItemDiscount(id, itemId, discountDto);

        ApiResponse<QuotationItemDto> response = ApiResponse.success(itemDto, "Discount applied successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

