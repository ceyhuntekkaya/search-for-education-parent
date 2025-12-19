package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.enumaration.OrderStatus;
import com.genixo.education.search.enumaration.QuotationStatus;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final CampusRepository campusRepository;
    private final SupplierRepository supplierRepository;

    // ================================ ORDER CRUD ================================

    @Transactional
    public OrderDto createOrder(OrderCreateDto createDto) {
        log.info("Creating new order from quotation ID: {}", createDto.getQuotationId());

        // Validate quotation
        Quotation quotation = quotationRepository.findById(createDto.getQuotationId())
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", createDto.getQuotationId()));

        if (quotation.getStatus() != QuotationStatus.ACCEPTED) {
            throw new BusinessException("Can only create order from ACCEPTED quotation");
        }

        // Validate company
        Campus company = campusRepository.findById(createDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", createDto.getCompanyId()));

        // Get supplier from quotation
        Supplier supplier = quotation.getSupplier();
        if (supplier == null || !supplier.getIsActive()) {
            throw new BusinessException("Supplier is not active");
        }

        // Create order
        Order order = new Order();
        order.setQuotation(quotation);
        order.setCompany(company);
        order.setSupplier(supplier);
        order.setStatus(OrderStatus.PENDING);
        order.setCurrency(quotation.getCurrency());
        order.setDeliveryAddress(createDto.getDeliveryAddress());
        order.setExpectedDeliveryDate(createDto.getExpectedDeliveryDate());
        order.setNotes(createDto.getNotes());
        order.setCreatedAt(LocalDateTime.now());

        // Copy items from quotation and calculate totals
        List<QuotationItem> quotationItems = quotationItemRepository.findByQuotationId(quotation.getId());
        if (quotationItems.isEmpty()) {
            throw new BusinessException("Cannot create order from quotation without items");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (QuotationItem quotationItem : quotationItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(quotationItem.getProduct());
            orderItem.setItemName(quotationItem.getItemName());
            orderItem.setQuantity(quotationItem.getQuantity());
            orderItem.setUnitPrice(quotationItem.getUnitPrice());
            orderItem.setTotalPrice(quotationItem.getTotalPrice());
            orderItemRepository.save(orderItem);
            subtotal = subtotal.add(quotationItem.getTotalPrice());
        }

        // Calculate tax (use tax rate from first product or default 20%)
        BigDecimal taxRate = BigDecimal.valueOf(20.00); // Default 20% tax
        if (!quotationItems.isEmpty() && quotationItems.get(0).getProduct() != null) {
            Product firstProduct = quotationItems.get(0).getProduct();
            if (firstProduct.getTaxRate() != null) {
                taxRate = firstProduct.getTaxRate(); // taxRate is already in percentage (e.g., 20.00 for 20%)
            }
        }
        // Convert percentage to decimal for calculation
        BigDecimal taxRateDecimal = taxRate.divide(BigDecimal.valueOf(100));
        BigDecimal taxAmount = subtotal.multiply(taxRateDecimal);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        order.setSubtotal(subtotal);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        log.info("Order created successfully with ID: {} and order number: {}", saved.getId(), saved.getOrderNumber());

        return mapToDto(saved);
    }

    public Page<OrderDto> getAllOrders(String searchTerm, Long companyId, Long supplierId, 
                                       OrderStatus status, Long quotationId, Pageable pageable) {
        log.info("Fetching orders with filters");

        Page<Order> orders = orderRepository.searchOrders(
                searchTerm, companyId, supplierId, status, quotationId, pageable);
        return orders.map(this::mapToDto);
    }

    public OrderDto getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return mapToDto(order);
    }

    public Page<OrderDto> getOrdersByCompany(Long companyId, Pageable pageable) {
        log.info("Fetching orders for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        Page<Order> orders = orderRepository.findByCompanyId(companyId, pageable);
        return orders.map(this::mapToDto);
    }

    public Page<OrderDto> getOrdersBySupplier(Long supplierId, Pageable pageable) {
        log.info("Fetching orders for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Page<Order> orders = orderRepository.findBySupplierId(supplierId, pageable);
        return orders.map(this::mapToDto);
    }

    @Transactional
    public OrderDto updateOrder(Long id, OrderUpdateDto updateDto) {
        log.info("Updating order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        // Cannot update cancelled or delivered orders
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot update order that is CANCELLED or DELIVERED");
        }

        if (updateDto.getDeliveryAddress() != null) {
            order.setDeliveryAddress(updateDto.getDeliveryAddress());
        }
        if (updateDto.getExpectedDeliveryDate() != null) {
            order.setExpectedDeliveryDate(updateDto.getExpectedDeliveryDate());
        }
        if (updateDto.getNotes() != null) {
            order.setNotes(updateDto.getNotes());
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatusUpdateDto statusDto) {
        log.info("Updating order status with ID: {} to {}", id, statusDto.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        OrderStatus newStatus = statusDto.getStatus();
        OrderStatus currentStatus = order.getStatus();

        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);

        order.setStatus(newStatus);

        // Set actual delivery date if status is DELIVERED
        if (newStatus == OrderStatus.DELIVERED && order.getActualDeliveryDate() == null) {
            order.setActualDeliveryDate(java.time.LocalDate.now());
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order status updated successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public OrderDto confirmOrder(Long id) {
        log.info("Confirming order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be confirmed");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order confirmed successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public OrderDto cancelOrder(Long id) {
        log.info("Cancelling order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel order that is DELIVERED or already CANCELLED");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order cancelled successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public OrderDto shipOrder(Long id) {
        log.info("Shipping order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PREPARING) {
            throw new BusinessException("Only CONFIRMED or PREPARING orders can be shipped");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order shipped successfully with ID: {}", id);

        return mapToDto(updated);
    }

    @Transactional
    public OrderDto deliverOrder(Long id) {
        log.info("Delivering order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("Only SHIPPED orders can be delivered");
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setActualDeliveryDate(java.time.LocalDate.now());
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order delivered successfully with ID: {}", id);

        return mapToDto(updated);
    }

    // ================================ ORDER TRACKING ================================

    public OrderTrackingDto getOrderTracking(Long id) {
        log.info("Fetching tracking information for order ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return OrderTrackingDto.builder()
                .trackingNumber(order.getTrackingNumber())
                .build();
    }

    @Transactional
    public OrderTrackingDto updateOrderTracking(Long id, OrderTrackingDto trackingDto) {
        log.info("Updating tracking number for order ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot update tracking for CANCELLED or DELIVERED order");
        }

        order.setTrackingNumber(trackingDto.getTrackingNumber());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("Tracking number updated successfully for order ID: {}", id);
        return OrderTrackingDto.builder()
                .trackingNumber(order.getTrackingNumber())
                .build();
    }

    // ================================ ORDER INVOICE ================================

    public OrderInvoiceDto getOrderInvoice(Long id) {
        log.info("Fetching invoice information for order ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return OrderInvoiceDto.builder()
                .invoiceNumber(order.getInvoiceNumber())
                .build();
    }

    @Transactional
    public OrderInvoiceDto updateOrderInvoice(Long id, OrderInvoiceDto invoiceDto) {
        log.info("Updating invoice number for order ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        order.setInvoiceNumber(invoiceDto.getInvoiceNumber());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("Invoice number updated successfully for order ID: {}", id);
        return OrderInvoiceDto.builder()
                .invoiceNumber(order.getInvoiceNumber())
                .build();
    }

    // ================================ ORDER ITEMS ================================

    public List<OrderItemDto> getOrderItems(Long orderId) {
        log.info("Fetching items for order ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());
    }

    // ================================ HELPER METHODS ================================

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("PENDING orders can only transition to CONFIRMED or CANCELLED");
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("CONFIRMED orders can only transition to PREPARING, SHIPPED, or CANCELLED");
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessException("PREPARING orders can only transition to SHIPPED or CANCELLED");
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.RETURNED) {
                    throw new BusinessException("SHIPPED orders can only transition to DELIVERED or RETURNED");
                }
                break;
            case DELIVERED:
                if (newStatus != OrderStatus.RETURNED) {
                    throw new BusinessException("DELIVERED orders can only transition to RETURNED");
                }
                break;
            case CANCELLED:
            case RETURNED:
                throw new BusinessException("Cannot change status of " + currentStatus + " order");
            default:
                throw new BusinessException("Invalid status transition");
        }
    }

    private OrderDto mapToDto(Order order) {
        Long itemCount = (long) orderItemRepository.findByOrderId(order.getId()).size();

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .quotationId(order.getQuotation() != null ? order.getQuotation().getId() : null)
                .companyId(order.getCompany().getId())
                .companyName(order.getCompany().getName())
                .supplierId(order.getSupplier().getId())
                .supplierCompanyName(order.getSupplier().getCompanyName())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .deliveryAddress(order.getDeliveryAddress())
                .expectedDeliveryDate(order.getExpectedDeliveryDate())
                .actualDeliveryDate(order.getActualDeliveryDate())
                .notes(order.getNotes())
                .invoiceNumber(order.getInvoiceNumber())
                .trackingNumber(order.getTrackingNumber())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .itemCount(itemCount.intValue())
                .build();
    }

    private OrderItemDto mapToItemDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .productVariantId(item.getProductVariant() != null ? item.getProductVariant().getId() : null)
                .productVariantName(item.getProductVariant() != null ? item.getProductVariant().getVariantName() : null)
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}

