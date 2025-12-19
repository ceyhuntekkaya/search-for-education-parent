package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.service.supply.CategoryService;
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
@RequestMapping("/supply/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing product categories in the supply system")
public class CategoryController {

    private final CategoryService categoryService;

    // ================================ CREATE ================================

    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new category (root or subcategory)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Category created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid category data or duplicate name"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CategoryCreateDto createDto,
            HttpServletRequest request) {

        CategoryDto categoryDto = categoryService.createCategory(createDto);

        ApiResponse<CategoryDto> response = ApiResponse.success(categoryDto, "Category created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ READ ================================

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all categories with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<CategoryDto>>> getAllCategories(
            @Parameter(description = "Search term (name, description)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Filter by parent category ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "displayOrder") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CategoryDto> categories = categoryService.getAllCategories(searchTerm, isActive, parentId, pageable);

        ApiResponse<Page<CategoryDto>> response = ApiResponse.success(categories, "Categories retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tree")
    @Operation(summary = "Get category tree", description = "Get all categories in tree structure (hierarchical)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category tree retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> getCategoryTree(
            @Parameter(description = "Include inactive categories") @RequestParam(required = false) Boolean includeInactive,
            HttpServletRequest request) {

        List<CategoryTreeDto> tree = categoryService.getCategoryTree(includeInactive);

        ApiResponse<List<CategoryTreeDto>> response = ApiResponse.success(tree, "Category tree retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable Long id,
            HttpServletRequest request) {

        CategoryDto categoryDto = categoryService.getCategoryById(id);

        ApiResponse<CategoryDto> response = ApiResponse.success(categoryDto, "Category retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Get subcategories", description = "Get all subcategories of a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategories retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getSubCategories(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Parameter(description = "Include inactive subcategories") @RequestParam(required = false) Boolean includeInactive,
            HttpServletRequest request) {

        List<CategoryDto> subCategories = categoryService.getSubCategories(id, includeInactive);

        ApiResponse<List<CategoryDto>> response = ApiResponse.success(subCategories, "Subcategories retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ UPDATE ================================

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update category information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid category data or circular reference")
    })
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDto updateDto,
            HttpServletRequest request) {

        CategoryDto categoryDto = categoryService.updateCategory(id, updateDto);

        ApiResponse<CategoryDto> response = ApiResponse.success(categoryDto, "Category updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ACTIVATE/DEACTIVATE ================================

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate category", description = "Activate a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<CategoryDto>> activateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            HttpServletRequest request) {

        CategoryDto categoryDto = categoryService.activateCategory(id);

        ApiResponse<CategoryDto> response = ApiResponse.success(categoryDto, "Category activated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate category", description = "Deactivate a category (only if no active subcategories)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot deactivate category with active subcategories")
    })
    public ResponseEntity<ApiResponse<CategoryDto>> deactivateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            HttpServletRequest request) {

        CategoryDto categoryDto = categoryService.deactivateCategory(id);

        ApiResponse<CategoryDto> response = ApiResponse.success(categoryDto, "Category deactivated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ REORDER ================================

    @PatchMapping("/{id}/reorder")
    @Operation(summary = "Reorder category", description = "Change the display order of a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category reordered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<CategoryDto>> reorderCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Valid @RequestBody CategoryReorderDto reorderDto,
            HttpServletRequest request) {

        CategoryDto categoryDto = categoryService.reorderCategory(id, reorderDto);

        ApiResponse<CategoryDto> response = ApiResponse.success(categoryDto, "Category reordered successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ DELETE ================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category (only if no subcategories or products exist)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete category with subcategories or products")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            HttpServletRequest request) {

        categoryService.deleteCategory(id);

        ApiResponse<Void> response = ApiResponse.success(null, "Category deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ATTRIBUTES ================================

    @GetMapping("/{id}/attributes")
    @Operation(summary = "Get category attributes", description = "Get all attributes of a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attributes retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<List<CategoryAttributeDto>>> getCategoryAttributes(
            @Parameter(description = "Category ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<CategoryAttributeDto> attributes = categoryService.getCategoryAttributes(id);

        ApiResponse<List<CategoryAttributeDto>> response = ApiResponse.success(attributes, "Attributes retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/attributes")
    @Operation(summary = "Create category attribute", description = "Add a new attribute to a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Attribute created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid attribute data or duplicate attribute name"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<CategoryAttributeDto>> createCategoryAttribute(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Valid @RequestBody CategoryAttributeCreateDto createDto,
            HttpServletRequest request) {

        CategoryAttributeDto attributeDto = categoryService.createCategoryAttribute(id, createDto);

        ApiResponse<CategoryAttributeDto> response = ApiResponse.success(attributeDto, "Attribute created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/attributes/{attributeId}")
    @Operation(summary = "Update category attribute", description = "Update an attribute of a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribute updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category or attribute not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid attribute data or duplicate attribute name")
    })
    public ResponseEntity<ApiResponse<CategoryAttributeDto>> updateCategoryAttribute(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Parameter(description = "Attribute ID") @PathVariable Long attributeId,
            @Valid @RequestBody CategoryAttributeUpdateDto updateDto,
            HttpServletRequest request) {

        CategoryAttributeDto attributeDto = categoryService.updateCategoryAttribute(id, attributeId, updateDto);

        ApiResponse<CategoryAttributeDto> response = ApiResponse.success(attributeDto, "Attribute updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/attributes/{attributeId}")
    @Operation(summary = "Delete category attribute", description = "Delete an attribute from a category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribute deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category or attribute not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCategoryAttribute(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Parameter(description = "Attribute ID") @PathVariable Long attributeId,
            HttpServletRequest request) {

        categoryService.deleteCategoryAttribute(id, attributeId);

        ApiResponse<Void> response = ApiResponse.success(null, "Attribute deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

