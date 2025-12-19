package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.SupplierRatingCreateDto;
import com.genixo.education.search.dto.supply.SupplierRatingDto;
import com.genixo.education.search.dto.supply.SupplierRatingUpdateDto;
import com.genixo.education.search.service.supply.SupplierRatingService;
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
@RequestMapping("/supply/supplier-ratings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supplier Rating Management", description = "APIs for managing supplier ratings in the supply system")
public class SupplierRatingController {

    private final SupplierRatingService ratingService;

    @PostMapping
    @Operation(summary = "Create supplier rating", description = "Rate a supplier for a delivered order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid rating data, order not delivered, or rating already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier, company or order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<SupplierRatingDto>> createRating(
            @Valid @RequestBody SupplierRatingCreateDto createDto,
            HttpServletRequest request) {

        SupplierRatingDto ratingDto = ratingService.createRating(createDto);

        ApiResponse<SupplierRatingDto> response = ApiResponse.success(ratingDto, "Rating created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID", description = "Get rating details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public ResponseEntity<ApiResponse<SupplierRatingDto>> getRatingById(
            @Parameter(description = "Rating ID") @PathVariable Long id,
            HttpServletRequest request) {

        SupplierRatingDto ratingDto = ratingService.getRatingById(id);

        ApiResponse<SupplierRatingDto> response = ApiResponse.success(ratingDto, "Rating retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-supplier/{supplierId}")
    @Operation(summary = "Get ratings by supplier", description = "Get all ratings for a supplier with pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<SupplierRatingDto>>> getRatingsBySupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long supplierId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SupplierRatingDto> ratings = ratingService.getRatingsBySupplier(supplierId, pageable);

        ApiResponse<Page<SupplierRatingDto>> response = ApiResponse.success(ratings, "Ratings retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Get rating by order", description = "Get rating for a specific order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order or rating not found")
    })
    public ResponseEntity<ApiResponse<SupplierRatingDto>> getRatingByOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            HttpServletRequest request) {

        SupplierRatingDto ratingDto = ratingService.getRatingByOrder(orderId);

        ApiResponse<SupplierRatingDto> response = ApiResponse.success(ratingDto, "Rating retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rating", description = "Update a supplier rating")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rating not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<SupplierRatingDto>> updateRating(
            @Parameter(description = "Rating ID") @PathVariable Long id,
            @Valid @RequestBody SupplierRatingUpdateDto updateDto,
            HttpServletRequest request) {

        SupplierRatingDto ratingDto = ratingService.updateRating(id, updateDto);

        ApiResponse<SupplierRatingDto> response = ApiResponse.success(ratingDto, "Rating updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rating", description = "Delete a supplier rating")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRating(
            @Parameter(description = "Rating ID") @PathVariable Long id,
            HttpServletRequest request) {

        ratingService.deleteRating(id);

        ApiResponse<Void> response = ApiResponse.success(null, "Rating deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

