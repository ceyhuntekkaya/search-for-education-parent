package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.dto.institution.SchoolDto;
import com.genixo.education.search.dto.institution.SchoolSearchResultDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.appointment.ParentSearchList;
import com.genixo.education.search.service.ParentListService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/parent/school-lists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Parent School List Management", description = "APIs for managing parent school lists, notes, and favorites")
public class ParentListController {

    private final ParentListService parentListService;
    private final JwtService jwtService;

    // ================================ SCHOOL LIST OPERATIONS ================================

    @PostMapping
    @Operation(summary = "Create school list", description = "Create a new school list for the parent")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School list created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid list data or list name already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListResponse>> createList(
            @Valid @RequestBody CreateParentSchoolListRequest createRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListResponse listResponse = parentListService.createList(parentUserId, createRequest);

        ApiResponse<ParentSchoolListResponse> response = ApiResponse.success(listResponse, "School list created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{listId}")
    @Operation(summary = "Update school list", description = "Update an existing school list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School list updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid list data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list not found")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListResponse>> updateList(
            @Parameter(description = "School list ID") @PathVariable Long listId,
            @Valid @RequestBody UpdateParentSchoolListRequest updateRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListResponse listResponse = parentListService.updateList(parentUserId, listId, updateRequest);

        ApiResponse<ParentSchoolListResponse> response = ApiResponse.success(listResponse, "School list updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{listId}")
    @Operation(summary = "Delete school list", description = "Delete a school list (soft delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "School list deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteList(
            @Parameter(description = "School list ID") @PathVariable Long listId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        parentListService.deleteList(parentUserId, listId);

        ApiResponse<Void> response = ApiResponse.success(null, "School list deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get parent school lists", description = "Get all school lists for the current parent")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School lists retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ParentSchoolListResponse>>> getParentLists(HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        List<ParentSchoolListResponse> lists = parentListService.getParentLists(parentUserId);

        ApiResponse<List<ParentSchoolListResponse>> response = ApiResponse.success(lists, "School lists retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get parent school lists summary", description = "Get summary of all school lists for the current parent")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School lists summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ParentSchoolListSummaryResponse>>> getParentListsSummary(HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        List<ParentSchoolListSummaryResponse> listsSummary = parentListService.getParentListsSummary(parentUserId);

        ApiResponse<List<ParentSchoolListSummaryResponse>> response = ApiResponse.success(listsSummary, "School lists summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listId}")
    @Operation(summary = "Get school list details", description = "Get detailed information about a specific school list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School list details retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list not found")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListResponse>> getListById(
            @Parameter(description = "School list ID") @PathVariable Long listId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListResponse listResponse = parentListService.getListById(parentUserId, listId);

        ApiResponse<ParentSchoolListResponse> response = ApiResponse.success(listResponse, "School list details retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SCHOOL LIST ITEM OPERATIONS ================================

    @PostMapping("/schools")
    @Operation(summary = "Add school to list", description = "Add a school to a specific list or default list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School added to list successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "School already exists in the list"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or list not found")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListItemResponse>> addSchoolToList(
            @Valid @RequestBody AddSchoolToListRequest addRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListItemResponse itemResponse = parentListService.addSchoolToList(parentUserId, addRequest);

        ApiResponse<ParentSchoolListItemResponse> response = ApiResponse.success(itemResponse, "School added to list successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/schools/quick-add")
    @Operation(summary = "Quick add schools", description = "Quickly add multiple schools to a list from search results")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Schools added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ParentSchoolListItemResponse>>> quickAddSchools(
            @Valid @RequestBody QuickAddSchoolRequest quickAddRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        List<ParentSchoolListItemResponse> itemResponses = parentListService.quickAddSchools(parentUserId, quickAddRequest);

        ApiResponse<List<ParentSchoolListItemResponse>> response = ApiResponse.success(itemResponses, "Schools added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/schools/{itemId}")
    @Operation(summary = "Update school in list", description = "Update school information within a list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list item not found")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListItemResponse>> updateSchoolInList(
            @Parameter(description = "School list item ID") @PathVariable Long itemId,
            @Valid @RequestBody UpdateSchoolInListRequest updateRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListItemResponse itemResponse = parentListService.updateSchoolInList(parentUserId, itemId, updateRequest);

        ApiResponse<ParentSchoolListItemResponse> response = ApiResponse.success(itemResponse, "School updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/schools/{itemId}")
    @Operation(summary = "Remove school from list", description = "Remove a school from the list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "School removed from list successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list item not found")
    })
    public ResponseEntity<ApiResponse<Void>> removeSchoolFromList(
            @Parameter(description = "School list item ID") @PathVariable Long itemId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        parentListService.removeSchoolFromList(parentUserId, itemId);

        ApiResponse<Void> response = ApiResponse.success(null, "School removed from list successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listId}/schools")
    @Operation(summary = "Get list items", description = "Get schools in a specific list with pagination and filtering")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List items retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list not found")
    })
    public ResponseEntity<ApiResponse<List<SchoolSearchResultDto>>> getListItems(
            @Parameter(description = "School list ID") @PathVariable Long listId,
            HttpServletRequest request) {


        List<SchoolSearchResultDto> items = parentListService.getListSchools(listId);

        ApiResponse<List<SchoolSearchResultDto>> response = ApiResponse.success(items, "List items retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }


// ================================ BULK OPERATIONS ================================

    @PostMapping("/schools/bulk-operations")
    @Operation(summary = "Perform bulk operations", description = "Perform bulk operations on multiple schools (favorite, block, remove, etc.)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk operation completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid operation or school IDs"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> performBulkOperation(
            @Valid @RequestBody BulkSchoolListOperationRequest bulkRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        parentListService.performBulkOperation(parentUserId, bulkRequest);

        ApiResponse<Void> response = ApiResponse.success(null, "Bulk operation completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

// ================================ SCHOOL NOTE OPERATIONS ================================

    @PostMapping("/schools/notes")
    @Operation(summary = "Create school note", description = "Create a note for a specific school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School note created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid note data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<ParentSchoolNoteResponse>> createSchoolNote(
            @Valid @RequestBody CreateParentSchoolNoteRequest createRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolNoteResponse noteResponse = parentListService.createSchoolNote(parentUserId, createRequest);

        ApiResponse<ParentSchoolNoteResponse> response = ApiResponse.success(noteResponse, "School note created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/schools/notes/{noteId}")
    @Operation(summary = "Update school note", description = "Update an existing school note")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School note updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid note data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<ApiResponse<ParentSchoolNoteResponse>> updateSchoolNote(
            @Parameter(description = "Note ID") @PathVariable Long noteId,
            @Valid @RequestBody UpdateParentSchoolNoteRequest updateRequest,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolNoteResponse noteResponse = parentListService.updateSchoolNote(parentUserId, noteId, updateRequest);

        ApiResponse<ParentSchoolNoteResponse> response = ApiResponse.success(noteResponse, "School note updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/schools/notes/{noteId}")
    @Operation(summary = "Delete school note", description = "Delete a school note")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "School note deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSchoolNote(
            @Parameter(description = "Note ID") @PathVariable Long noteId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        parentListService.deleteSchoolNote(parentUserId, noteId);

        ApiResponse<Void> response = ApiResponse.success(null, "School note deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/notes")
    @Operation(summary = "Get school notes", description = "Get all notes for a specific school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School notes retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ParentSchoolNoteResponse>>> getSchoolNotes(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        List<ParentSchoolNoteResponse> notes = parentListService.getSchoolNotes(parentUserId, schoolId);

        ApiResponse<List<ParentSchoolNoteResponse>> response = ApiResponse.success(notes, "School notes retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

// ================================ LIST NOTE OPERATIONS ================================

    @PostMapping("/{listId}/notes")
    @Operation(summary = "Create list note", description = "Create a note for a specific list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "List note created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid note data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "List not found")
    })
    public ResponseEntity<ApiResponse<ParentListNoteResponse>> createListNote(
            @Parameter(description = "List ID") @PathVariable Long listId,
            @Valid @RequestBody CreateParentListNoteRequest createRequest,
            HttpServletRequest request) {


        createRequest.setParentSchoolListId(listId);
        Long parentUserId = jwtService.getUser(request).getId();
        ParentListNoteResponse noteResponse = parentListService.createListNote(parentUserId, createRequest);

        ApiResponse<ParentListNoteResponse> response = ApiResponse.success(noteResponse, "List note created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/notes/{noteId}")
    @Operation(summary = "Delete list note", description = "Delete a list note")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "List note deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteListNote(
            @Parameter(description = "Note ID") @PathVariable Long noteId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        parentListService.deleteListNote(parentUserId, noteId);

        ApiResponse<Void> response = ApiResponse.success(null, "List note deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listId}/notes")
    @Operation(summary = "Get list notes", description = "Get all notes for a specific list")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List notes retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "List not found")
    })
    public ResponseEntity<ApiResponse<List<ParentListNoteResponse>>> getListNotes(
            @Parameter(description = "List ID") @PathVariable Long listId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        List<ParentListNoteResponse> notes = parentListService.getListNotes(parentUserId, listId);

        ApiResponse<List<ParentListNoteResponse>> response = ApiResponse.success(notes, "List notes retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

// ================================ DASHBOARD & UTILITIES ================================

    @GetMapping("/dashboard")
    @Operation(summary = "Get parent dashboard", description = "Get comprehensive dashboard data for the parent")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListDashboardResponse>> getDashboard(HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListDashboardResponse dashboard = parentListService.getDashboard(parentUserId);

        ApiResponse<ParentSchoolListDashboardResponse> response = ApiResponse.success(dashboard, "Dashboard data retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/search")
    @Operation(summary = "Search all schools", description = "Search and filter schools across all lists")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Schools retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<ParentSchoolListItemResponse>>> searchAllSchools(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "false") Boolean onlyFavorites,
            @RequestParam(defaultValue = "false") Boolean onlyBlocked,
            @RequestParam(required = false) Integer minStarRating,
            @RequestParam(required = false) Integer maxStarRating,
            @RequestParam(defaultValue = "priorityOrder") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();

        ParentSchoolListSearchRequest searchRequest = ParentSchoolListSearchRequest.builder()
                .keyword(keyword)
                .onlyFavorites(onlyFavorites)
                .onlyBlocked(onlyBlocked)
                .minStarRating(minStarRating)
                .maxStarRating(maxStarRating)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();

        Page<ParentSchoolListItemResponse> schools = parentListService.searchAllSchools(parentUserId, searchRequest);

        ApiResponse<Page<ParentSchoolListItemResponse>> response = ApiResponse.success(schools, "Schools retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reminders")
    @Operation(summary = "Get upcoming reminders", description = "Get upcoming reminder notes within specified days")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reminders retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ParentSchoolNoteResponse>>> getUpcomingReminders(
            @Parameter(description = "Number of days ahead to check") @RequestParam(defaultValue = "7") Integer days,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        List<ParentSchoolNoteResponse> reminders = parentListService.getUpcomingReminders(parentUserId, days);

        ApiResponse<List<ParentSchoolNoteResponse>> response = ApiResponse.success(reminders, "Reminders retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/status")
    @Operation(summary = "Check school status", description = "Check if school exists in any list and get list information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School status retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolStatusResponse>> getSchoolStatus(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        boolean isInAnyList = parentListService.isSchoolInAnyList(parentUserId, schoolId);
        List<ParentSchoolListSummaryResponse> schoolLists = parentListService.getSchoolLists(parentUserId, schoolId);

        SchoolStatusResponse schoolStatus = SchoolStatusResponse.builder()
                .schoolId(schoolId)
                .isInAnyList(isInAnyList)
                .lists(schoolLists)
                .build();

        ApiResponse<SchoolStatusResponse> response = ApiResponse.success(schoolStatus, "School status retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/default")
    @Operation(summary = "Create default list", description = "Create a default list for first-time users")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Default list created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Default list already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<ParentSchoolListResponse>> createDefaultList(HttpServletRequest request) {


        Long parentUserId = jwtService.getUser(request).getId();
        ParentSchoolListResponse defaultList = parentListService.createDefaultList(parentUserId);

        ApiResponse<ParentSchoolListResponse> response = ApiResponse.success(defaultList, "Default list created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }






    @PostMapping("/search-list/")
    @Operation(summary = "Create school list", description = "Create a new school list for the parent")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School list created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid list data or list name already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<ParentSearchList>> createList(
            @Valid @RequestBody ParentSearchList createSearch,
            HttpServletRequest request) {

        ParentSearchList listResponse = parentListService.createSearch(createSearch);

        ApiResponse<ParentSearchList> response = ApiResponse.success(listResponse, "School list created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/search-list/{parentId}")
    @Operation(summary = "Check school status", description = "Check if school exists in any list and get list information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School status retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ParentSearchList>>> getParentSearchList(
            @Parameter(description = "School ID") @PathVariable Long parentId,
            HttpServletRequest request) {

        List<ParentSearchList> schoolLists = parentListService.getParentSearchList(parentId);

        ApiResponse<List<ParentSearchList>> response = ApiResponse.success(schoolLists, "School status retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/search-list/{listId}")
    @Operation(summary = "Delete school list", description = "Delete a school list (soft delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "School list deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School list not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSearchList(
            @Parameter(description = "School list ID") @PathVariable Long listId,
            HttpServletRequest request) {

        parentListService.deleteSearchList(listId);
        ApiResponse<Void> response = ApiResponse.success(null, "Search list deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }


}
