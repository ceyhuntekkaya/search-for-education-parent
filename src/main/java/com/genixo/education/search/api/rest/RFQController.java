package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.enumaration.RFQType;
import com.genixo.education.search.service.supply.RFQService;
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
@RequestMapping("/supply/rfqs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "RFQ Management", description = "APIs for managing Request for Quotation (RFQ) in the supply system")
public class RFQController {

    private final RFQService rfqService;

    // ================================ RFQ CRUD ================================

    @PostMapping
    @Operation(summary = "Create new RFQ", description = "Create a new Request for Quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "RFQ created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid RFQ data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<RFQDto>> createRFQ(
            @Valid @RequestBody RFQCreateDto createDto,
            HttpServletRequest request) {

        RFQDto rfqDto = rfqService.createRFQ(createDto);

        ApiResponse<RFQDto> response = ApiResponse.success(rfqDto, "RFQ created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all RFQs", description = "Get all RFQs with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQs retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<RFQDto>>> getAllRFQs(
            @Parameter(description = "Search term (title, description)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by company ID") @RequestParam(required = false) Long companyId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) RFQStatus status,
            @Parameter(description = "Filter by RFQ type") @RequestParam(required = false) RFQType rfqType,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RFQDto> rfqs = rfqService.getAllRFQs(searchTerm, companyId, status, rfqType, pageable);

        ApiResponse<Page<RFQDto>> response = ApiResponse.success(rfqs, "RFQs retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get RFQ by ID", description = "Get RFQ details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found")
    })
    public ResponseEntity<ApiResponse<RFQDto>> getRFQById(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        RFQDto rfqDto = rfqService.getRFQById(id);

        ApiResponse<RFQDto> response = ApiResponse.success(rfqDto, "RFQ retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "Get RFQs by company", description = "Get all RFQs for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQs retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<Page<RFQDto>>> getRFQsByCompany(
            @Parameter(description = "Company ID") @PathVariable Long companyId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RFQDto> rfqs = rfqService.getRFQsByCompany(companyId, pageable);

        ApiResponse<Page<RFQDto>> response = ApiResponse.success(rfqs, "RFQs retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active RFQs", description = "Get all active (published) RFQs")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active RFQs retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<RFQDto>>> getActiveRFQs(HttpServletRequest request) {

        List<RFQDto> rfqs = rfqService.getActiveRFQs();

        ApiResponse<List<RFQDto>> response = ApiResponse.success(rfqs, "Active RFQs retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update RFQ", description = "Update RFQ information (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update RFQ that is not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<RFQDto>> updateRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Valid @RequestBody RFQUpdateDto updateDto,
            HttpServletRequest request) {

        RFQDto rfqDto = rfqService.updateRFQ(id, updateDto);

        ApiResponse<RFQDto> response = ApiResponse.success(rfqDto, "RFQ updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish RFQ", description = "Publish a DRAFT RFQ (make it available for quotations)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ published successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot publish RFQ without items or not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<RFQDto>> publishRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        RFQDto rfqDto = rfqService.publishRFQ(id);

        ApiResponse<RFQDto> response = ApiResponse.success(rfqDto, "RFQ published successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close RFQ", description = "Close a PUBLISHED RFQ (stop accepting quotations)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ closed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot close RFQ that is not PUBLISHED")
    })
    public ResponseEntity<ApiResponse<RFQDto>> closeRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        RFQDto rfqDto = rfqService.closeRFQ(id);

        ApiResponse<RFQDto> response = ApiResponse.success(rfqDto, "RFQ closed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel RFQ", description = "Cancel an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot cancel CLOSED RFQ")
    })
    public ResponseEntity<ApiResponse<RFQDto>> cancelRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        RFQDto rfqDto = rfqService.cancelRFQ(id);

        ApiResponse<RFQDto> response = ApiResponse.success(rfqDto, "RFQ cancelled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete RFQ", description = "Delete an RFQ (only if no quotations exist)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "RFQ deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete RFQ with quotations")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        rfqService.deleteRFQ(id);

        ApiResponse<Void> response = ApiResponse.success(null, "RFQ deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ RFQ ITEMS ================================

    @GetMapping("/{id}/items")
    @Operation(summary = "Get RFQ items", description = "Get all items of an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found")
    })
    public ResponseEntity<ApiResponse<List<RFQItemDto>>> getRFQItems(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<RFQItemDto> items = rfqService.getRFQItems(id);

        ApiResponse<List<RFQItemDto>> response = ApiResponse.success(items, "Items retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add RFQ item", description = "Add a new item to an RFQ (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot add items to RFQ that is not in DRAFT status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<RFQItemDto>> createRFQItem(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Valid @RequestBody RFQItemCreateDto createDto,
            HttpServletRequest request) {

        RFQItemDto itemDto = rfqService.createRFQItem(id, createDto);

        ApiResponse<RFQItemDto> response = ApiResponse.success(itemDto, "Item added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/items/{itemId}")
    @Operation(summary = "Update RFQ item", description = "Update an item of an RFQ (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update items in RFQ that is not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<RFQItemDto>> updateRFQItem(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            @Valid @RequestBody RFQItemUpdateDto updateDto,
            HttpServletRequest request) {

        RFQItemDto itemDto = rfqService.updateRFQItem(id, itemId, updateDto);

        ApiResponse<RFQItemDto> response = ApiResponse.success(itemDto, "Item updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Delete RFQ item", description = "Delete an item from an RFQ (only DRAFT status)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete items from RFQ that is not in DRAFT status")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRFQItem(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            HttpServletRequest request) {

        rfqService.deleteRFQItem(id, itemId);

        ApiResponse<Void> response = ApiResponse.success(null, "Item deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ RFQ INVITATIONS ================================

    @GetMapping("/{id}/invitations")
    @Operation(summary = "Get RFQ invitations", description = "Get all invitations for an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invitations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found")
    })
    public ResponseEntity<ApiResponse<List<RFQInvitationDto>>> getRFQInvitations(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<RFQInvitationDto> invitations = rfqService.getRFQInvitations(id);

        ApiResponse<List<RFQInvitationDto>> response = ApiResponse.success(invitations, "Invitations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/invitations")
    @Operation(summary = "Invite supplier to RFQ", description = "Invite a supplier to an INVITED type RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Invitation created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or supplier not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Can only invite to INVITED type RFQs or supplier already invited"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<RFQInvitationDto>> createRFQInvitation(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Valid @RequestBody RFQInvitationCreateDto createDto,
            HttpServletRequest request) {

        RFQInvitationDto invitationDto = rfqService.createRFQInvitation(id, createDto);

        ApiResponse<RFQInvitationDto> response = ApiResponse.success(invitationDto, "Invitation created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/invitations/bulk")
    @Operation(summary = "Bulk invite suppliers", description = "Invite multiple suppliers to an INVITED type RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Invitations created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or supplier not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Can only invite to INVITED type RFQs"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<List<RFQInvitationDto>>> createRFQInvitationsBulk(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Valid @RequestBody RFQInvitationBulkCreateDto createDto,
            HttpServletRequest request) {

        List<RFQInvitationDto> invitations = rfqService.createRFQInvitationsBulk(id, createDto);

        ApiResponse<List<RFQInvitationDto>> response = ApiResponse.success(invitations, "Invitations created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/invitations/{invitationId}")
    @Operation(summary = "Cancel RFQ invitation", description = "Cancel an invitation to an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invitation cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ or invitation not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRFQInvitation(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            @Parameter(description = "Invitation ID") @PathVariable Long invitationId,
            HttpServletRequest request) {

        rfqService.deleteRFQInvitation(id, invitationId);

        ApiResponse<Void> response = ApiResponse.success(null, "Invitation cancelled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ QUOTATIONS ================================

    @GetMapping("/{id}/quotations")
    @Operation(summary = "Get RFQ quotations", description = "Get all quotations for an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quotations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found")
    })
    public ResponseEntity<ApiResponse<List<QuotationComparisonDto>>> getRFQQuotations(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<QuotationComparisonDto> quotations = rfqService.getRFQQuotations(id);

        ApiResponse<List<QuotationComparisonDto>> response = ApiResponse.success(quotations, "Quotations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/comparison")
    @Operation(summary = "Get RFQ comparison", description = "Get quotation comparison table for an RFQ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comparison retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "RFQ not found")
    })
    public ResponseEntity<ApiResponse<List<QuotationComparisonDto>>> getRFQComparison(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<QuotationComparisonDto> comparison = rfqService.getRFQComparison(id);

        ApiResponse<List<QuotationComparisonDto>> response = ApiResponse.success(comparison, "Comparison retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

