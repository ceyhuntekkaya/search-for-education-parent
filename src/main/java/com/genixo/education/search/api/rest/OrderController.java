package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.enumaration.OrderStatus;
import com.genixo.education.search.service.supply.OrderService;
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
@RequestMapping("/supply/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders in the supply system")
public class OrderController {

    private final OrderService orderService;

    // ================================ ORDER CRUD ================================

    @PostMapping
    @Operation(summary = "Create new order", description = "Create a new order from an ACCEPTED quotation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid order data or quotation not accepted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quotation or company not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @Valid @RequestBody OrderCreateDto createDto,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.createOrder(createDto);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Get all orders with filtering, searching and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getAllOrders(
            @Parameter(description = "Search term (order number, notes, invoice, tracking)") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by company ID") @RequestParam(required = false) Long companyId,
            @Parameter(description = "Filter by supplier ID") @RequestParam(required = false) Long supplierId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Filter by quotation ID") @RequestParam(required = false) Long quotationId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderDto> orders = orderService.getAllOrders(searchTerm, companyId, supplierId, status, quotationId, pageable);

        ApiResponse<Page<OrderDto>> response = ApiResponse.success(orders, "Orders retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get order details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.getOrderById(id);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "Get orders by company", description = "Get all orders for a company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrdersByCompany(
            @Parameter(description = "Company ID") @PathVariable Long companyId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.getOrdersByCompany(companyId, pageable);

        ApiResponse<Page<OrderDto>> response = ApiResponse.success(orders, "Orders retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-supplier/{supplierId}")
    @Operation(summary = "Get orders by supplier", description = "Get all orders from a supplier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrdersBySupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long supplierId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.getOrdersBySupplier(supplierId, pageable);

        ApiResponse<Page<OrderDto>> response = ApiResponse.success(orders, "Orders retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update order information (cannot update CANCELLED or DELIVERED orders)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update CANCELLED or DELIVERED order")
    })
    public ResponseEntity<ApiResponse<OrderDto>> updateOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDto updateDto,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.updateOrder(id, updateDto);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update order status with validation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDto statusDto,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.updateOrderStatus(id, statusDto);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order status updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm order", description = "Confirm a PENDING order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order confirmed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Only PENDING orders can be confirmed")
    })
    public ResponseEntity<ApiResponse<OrderDto>> confirmOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.confirmOrder(id);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order confirmed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order (cannot cancel DELIVERED or already CANCELLED orders)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot cancel DELIVERED or already CANCELLED order")
    })
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.cancelOrder(id);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order cancelled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ship")
    @Operation(summary = "Ship order", description = "Mark order as shipped (only CONFIRMED or PREPARING orders)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order shipped successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Only CONFIRMED or PREPARING orders can be shipped")
    })
    public ResponseEntity<ApiResponse<OrderDto>> shipOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.shipOrder(id);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order shipped successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deliver")
    @Operation(summary = "Deliver order", description = "Mark order as delivered (only SHIPPED orders)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order delivered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Only SHIPPED orders can be delivered")
    })
    public ResponseEntity<ApiResponse<OrderDto>> deliverOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderDto orderDto = orderService.deliverOrder(id);

        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order delivered successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ORDER TRACKING ================================

    @GetMapping("/{id}/tracking")
    @Operation(summary = "Get order tracking", description = "Get tracking information for an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tracking information retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderTrackingDto>> getOrderTracking(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderTrackingDto trackingDto = orderService.getOrderTracking(id);

        ApiResponse<OrderTrackingDto> response = ApiResponse.success(trackingDto, "Tracking information retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/tracking")
    @Operation(summary = "Update order tracking", description = "Update tracking number for an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tracking number updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update tracking for CANCELLED or DELIVERED order")
    })
    public ResponseEntity<ApiResponse<OrderTrackingDto>> updateOrderTracking(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody OrderTrackingDto trackingDto,
            HttpServletRequest request) {

        OrderTrackingDto updated = orderService.updateOrderTracking(id, trackingDto);

        ApiResponse<OrderTrackingDto> response = ApiResponse.success(updated, "Tracking number updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ORDER INVOICE ================================

    @GetMapping("/{id}/invoice")
    @Operation(summary = "Get order invoice", description = "Get invoice information for an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice information retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderInvoiceDto>> getOrderInvoice(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        OrderInvoiceDto invoiceDto = orderService.getOrderInvoice(id);

        ApiResponse<OrderInvoiceDto> response = ApiResponse.success(invoiceDto, "Invoice information retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/invoice")
    @Operation(summary = "Update order invoice", description = "Update invoice number for an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice number updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderInvoiceDto>> updateOrderInvoice(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody OrderInvoiceDto invoiceDto,
            HttpServletRequest request) {

        OrderInvoiceDto updated = orderService.updateOrderInvoice(id, invoiceDto);

        ApiResponse<OrderInvoiceDto> response = ApiResponse.success(updated, "Invoice number updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ORDER ITEMS ================================

    @GetMapping("/{id}/items")
    @Operation(summary = "Get order items", description = "Get all items of an order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<List<OrderItemDto>>> getOrderItems(
            @Parameter(description = "Order ID") @PathVariable Long id,
            HttpServletRequest request) {

        List<OrderItemDto> items = orderService.getOrderItems(id);

        ApiResponse<List<OrderItemDto>> response = ApiResponse.success(items, "Items retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

