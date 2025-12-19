package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.*;
import com.genixo.education.search.enumaration.OrderStatus;
import com.genixo.education.search.enumaration.QuotationStatus;
import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final OrderRepository orderRepository;
    private final RFQRepository rfqRepository;
    private final QuotationRepository quotationRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplierRatingRepository supplierRatingRepository;
    private final OrderItemRepository orderItemRepository;
    private final CampusRepository campusRepository;

    // ================================ COMPANY DASHBOARD ================================

    public CompanySummaryDto getCompanySummary(Long companyId) {
        log.info("Fetching company summary for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        // Total spending (from delivered orders)
        BigDecimal totalSpending = orderRepository.sumTotalAmountByCompanyId(companyId) != null 
                ? orderRepository.sumTotalAmountByCompanyId(companyId) 
                : BigDecimal.ZERO;

        // Order counts
        Long activeOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.CONFIRMED) +
                           orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.PREPARING) +
                           orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.SHIPPED);
        Long pendingOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.PENDING);
        Long completedOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.DELIVERED);

        // RFQ counts
        Long totalRFQs = rfqRepository.countByCompanyId(companyId);
        Long publishedRFQs = rfqRepository.countByCompanyIdAndStatus(companyId, RFQStatus.PUBLISHED);

        // Quotation counts
        Long totalQuotations = quotationRepository.countByCompanyId(companyId);
        Long acceptedQuotations = quotationRepository.countByCompanyIdAndStatus(companyId, QuotationStatus.ACCEPTED);

        // Supplier counts
        Long totalSuppliers = supplierRepository.countDistinctByCompanyId(companyId);
        Long activeSuppliers = supplierRepository.countActiveDistinctByCompanyId(companyId);

        return CompanySummaryDto.builder()
                .totalSpending(totalSpending)
                .activeOrders(activeOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .totalRFQs(totalRFQs)
                .publishedRFQs(publishedRFQs)
                .totalQuotations(totalQuotations)
                .acceptedQuotations(acceptedQuotations)
                .totalSuppliers(totalSuppliers)
                .activeSuppliers(activeSuppliers)
                .build();
    }

    public SpendingAnalyticsDto getCompanySpendingAnalytics(Long companyId) {
        log.info("Fetching spending analytics for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        BigDecimal totalSpending = orderRepository.sumTotalAmountByCompanyId(companyId) != null 
                ? orderRepository.sumTotalAmountByCompanyId(companyId) 
                : BigDecimal.ZERO;

        // Monthly spending (last 30 days)
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        BigDecimal monthlySpending = orderRepository.sumTotalAmountByCompanyIdAndDateAfter(companyId, thirtyDaysAgo) != null
                ? orderRepository.sumTotalAmountByCompanyIdAndDateAfter(companyId, thirtyDaysAgo)
                : BigDecimal.ZERO;

        BigDecimal averageOrderValue = orderRepository.avgTotalAmountByCompanyId(companyId) != null
                ? orderRepository.avgTotalAmountByCompanyId(companyId)
                : BigDecimal.ZERO;

        // Spending by period (last 6 months)
        List<SpendingAnalyticsDto.SpendingDataPoint> spendingByPeriod = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            java.time.LocalDateTime monthStartDateTime = monthStart.atStartOfDay();
            java.time.LocalDateTime monthEndDateTime = monthEnd.atStartOfDay();
            BigDecimal monthSpending = orderRepository.sumTotalAmountByCompanyIdAndDateRange(companyId, monthStartDateTime, monthEndDateTime) != null
                    ? orderRepository.sumTotalAmountByCompanyIdAndDateRange(companyId, monthStartDateTime, monthEndDateTime)
                    : BigDecimal.ZERO;
            spendingByPeriod.add(SpendingAnalyticsDto.SpendingDataPoint.builder()
                    .period(monthStart)
                    .amount(monthSpending)
                    .build());
        }

        // Spending by category
        List<SpendingAnalyticsDto.SpendingByCategory> spendingByCategory = 
                orderRepository.getSpendingByCategoryForCompany(companyId);

        return SpendingAnalyticsDto.builder()
                .totalSpending(totalSpending)
                .monthlySpending(monthlySpending)
                .averageOrderValue(averageOrderValue)
                .spendingByPeriod(spendingByPeriod)
                .spendingByCategory(spendingByCategory)
                .build();
    }

    public List<TopSupplierDto> getCompanyTopSuppliers(Long companyId, int limit) {
        log.info("Fetching top suppliers for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        return orderRepository.getTopSuppliersByCompanyId(companyId, PageRequest.of(0, limit));
    }

    public List<TopProductDto> getCompanyTopProducts(Long companyId, int limit) {
        log.info("Fetching top products for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        return orderItemRepository.getTopProductsByCompanyId(companyId, PageRequest.of(0, limit));
    }

    public RFQStatisticsDto getCompanyRFQStatistics(Long companyId) {
        log.info("Fetching RFQ statistics for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        Long totalRFQs = rfqRepository.countByCompanyId(companyId);
        Long draftRFQs = rfqRepository.countByCompanyIdAndStatus(companyId, RFQStatus.DRAFT);
        Long publishedRFQs = rfqRepository.countByCompanyIdAndStatus(companyId, RFQStatus.PUBLISHED);
        Long closedRFQs = rfqRepository.countByCompanyIdAndStatus(companyId, RFQStatus.CLOSED);

        Long totalQuotations = quotationRepository.countByCompanyId(companyId);
        Long acceptedQuotations = quotationRepository.countByCompanyIdAndStatus(companyId, QuotationStatus.ACCEPTED);

        Double quotationAcceptanceRate = totalQuotations > 0 
                ? (acceptedQuotations.doubleValue() / totalQuotations.doubleValue()) * 100 
                : 0.0;

        Long averageQuotationsPerRFQ = totalRFQs > 0 
                ? totalQuotations / totalRFQs 
                : 0L;

        return RFQStatisticsDto.builder()
                .totalRFQs(totalRFQs)
                .draftRFQs(draftRFQs)
                .publishedRFQs(publishedRFQs)
                .closedRFQs(closedRFQs)
                .totalQuotations(totalQuotations)
                .acceptedQuotations(acceptedQuotations)
                .quotationAcceptanceRate(quotationAcceptanceRate)
                .averageQuotationsPerRFQ(averageQuotationsPerRFQ)
                .build();
    }

    public OrderStatisticsDto getCompanyOrderStatistics(Long companyId) {
        log.info("Fetching order statistics for company ID: {}", companyId);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        Long totalOrders = orderRepository.countByCompanyId(companyId);
        Long pendingOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.PENDING);
        Long confirmedOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.CONFIRMED);
        Long preparingOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.PREPARING);
        Long shippedOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.SHIPPED);
        Long deliveredOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.DELIVERED);
        Long cancelledOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.CANCELLED);

        BigDecimal totalSpending = orderRepository.sumTotalAmountByCompanyId(companyId) != null
                ? orderRepository.sumTotalAmountByCompanyId(companyId)
                : BigDecimal.ZERO;

        BigDecimal averageOrderValue = orderRepository.avgTotalAmountByCompanyId(companyId) != null
                ? orderRepository.avgTotalAmountByCompanyId(companyId)
                : BigDecimal.ZERO;

        Long averageDeliveryDays = orderRepository.avgDeliveryDaysByCompanyId(companyId);

        return OrderStatisticsDto.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .preparingOrders(preparingOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalSpending(totalSpending)
                .averageOrderValue(averageOrderValue)
                .averageDeliveryDays(averageDeliveryDays)
                .build();
    }

    // ================================ SUPPLIER DASHBOARD ================================

    public SupplierSummaryDto getSupplierSummary(Long supplierId) {
        log.info("Fetching supplier summary for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

        BigDecimal totalSales = orderRepository.sumTotalAmountBySupplierId(supplierId) != null
                ? orderRepository.sumTotalAmountBySupplierId(supplierId)
                : BigDecimal.ZERO;

        Long activeQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.SUBMITTED) +
                               quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.UNDER_REVIEW);
        Long pendingQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.DRAFT);
        Long submittedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.SUBMITTED);
        Long acceptedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.ACCEPTED);

        Long totalOrders = orderRepository.countBySupplierId(supplierId);
        Long pendingOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.PENDING);
        Long completedOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.DELIVERED);

        Long totalProducts = productRepository.countBySupplierId(supplierId);
        Long activeProducts = productRepository.countActiveBySupplierId(supplierId);

        Long totalRatings = supplierRatingRepository.countBySupplierId(supplierId);

        return SupplierSummaryDto.builder()
                .totalSales(totalSales)
                .activeQuotations(activeQuotations)
                .pendingQuotations(pendingQuotations)
                .submittedQuotations(submittedQuotations)
                .acceptedQuotations(acceptedQuotations)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .averageRating(supplier.getAverageRating())
                .totalRatings(totalRatings)
                .build();
    }

    public SalesAnalyticsDto getSupplierSalesAnalytics(Long supplierId) {
        log.info("Fetching sales analytics for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        BigDecimal totalSales = orderRepository.sumTotalAmountBySupplierId(supplierId) != null
                ? orderRepository.sumTotalAmountBySupplierId(supplierId)
                : BigDecimal.ZERO;

        // Monthly sales (last 30 days)
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        BigDecimal monthlySales = orderRepository.sumTotalAmountBySupplierIdAndDateAfter(supplierId, thirtyDaysAgo) != null
                ? orderRepository.sumTotalAmountBySupplierIdAndDateAfter(supplierId, thirtyDaysAgo)
                : BigDecimal.ZERO;

        BigDecimal averageOrderValue = orderRepository.avgTotalAmountBySupplierId(supplierId) != null
                ? orderRepository.avgTotalAmountBySupplierId(supplierId)
                : BigDecimal.ZERO;

        // Sales by period (last 6 months)
        List<SalesAnalyticsDto.SalesDataPoint> salesByPeriod = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            java.time.LocalDateTime monthStartDateTime = monthStart.atStartOfDay();
            java.time.LocalDateTime monthEndDateTime = monthEnd.atStartOfDay();
            BigDecimal monthSales = orderRepository.sumTotalAmountBySupplierIdAndDateRange(supplierId, monthStartDateTime, monthEndDateTime) != null
                    ? orderRepository.sumTotalAmountBySupplierIdAndDateRange(supplierId, monthStartDateTime, monthEndDateTime)
                    : BigDecimal.ZERO;
            salesByPeriod.add(SalesAnalyticsDto.SalesDataPoint.builder()
                    .period(monthStart)
                    .amount(monthSales)
                    .build());
        }

        // Sales by category
        List<SalesAnalyticsDto.SalesByCategory> salesByCategory = 
                orderRepository.getSalesByCategoryForSupplier(supplierId);

        return SalesAnalyticsDto.builder()
                .totalSales(totalSales)
                .monthlySales(monthlySales)
                .averageOrderValue(averageOrderValue)
                .salesByPeriod(salesByPeriod)
                .salesByCategory(salesByCategory)
                .build();
    }

    public QuotationStatisticsDto getSupplierQuotationStatistics(Long supplierId) {
        log.info("Fetching quotation statistics for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Long totalQuotations = quotationRepository.countBySupplierId(supplierId);
        Long draftQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.DRAFT);
        Long submittedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.SUBMITTED);
        Long acceptedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.ACCEPTED);
        Long rejectedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.REJECTED);

        BigDecimal acceptanceRate = totalQuotations > 0
                ? BigDecimal.valueOf(acceptedQuotations)
                        .divide(BigDecimal.valueOf(totalQuotations), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal averageQuotationValue = quotationRepository.avgTotalAmountBySupplierId(supplierId) != null
                ? quotationRepository.avgTotalAmountBySupplierId(supplierId)
                : BigDecimal.ZERO;

        Long averageResponseTimeDays = quotationRepository.avgResponseTimeDaysBySupplierId(supplierId);

        return QuotationStatisticsDto.builder()
                .totalQuotations(totalQuotations)
                .draftQuotations(draftQuotations)
                .submittedQuotations(submittedQuotations)
                .acceptedQuotations(acceptedQuotations)
                .rejectedQuotations(rejectedQuotations)
                .acceptanceRate(acceptanceRate)
                .averageQuotationValue(averageQuotationValue)
                .averageResponseTimeDays(averageResponseTimeDays)
                .build();
    }

    public List<TopProductDto> getSupplierTopProducts(Long supplierId, int limit) {
        log.info("Fetching top products for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        return orderItemRepository.getTopProductsBySupplierId(supplierId, PageRequest.of(0, limit));
    }

    public List<TopCustomerDto> getSupplierTopCustomers(Long supplierId, int limit) {
        log.info("Fetching top customers for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        return orderRepository.getTopCustomersBySupplierId(supplierId, PageRequest.of(0, limit));
    }

    public SupplierPerformanceDto getSupplierPerformance(Long supplierId) {
        log.info("Fetching performance metrics for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

        Long totalRatings = supplierRatingRepository.countBySupplierId(supplierId);

        // Average ratings by category
        BigDecimal deliveryRating = supplierRatingRepository.avgDeliveryRatingBySupplierId(supplierId) != null
                ? supplierRatingRepository.avgDeliveryRatingBySupplierId(supplierId)
                : BigDecimal.ZERO;
        BigDecimal qualityRating = supplierRatingRepository.avgQualityRatingBySupplierId(supplierId) != null
                ? supplierRatingRepository.avgQualityRatingBySupplierId(supplierId)
                : BigDecimal.ZERO;
        BigDecimal communicationRating = supplierRatingRepository.avgCommunicationRatingBySupplierId(supplierId) != null
                ? supplierRatingRepository.avgCommunicationRatingBySupplierId(supplierId)
                : BigDecimal.ZERO;

        // On-time delivery rate
        Long onTimeDeliveries = orderRepository.countOnTimeDeliveriesBySupplierId(supplierId);
        Long totalDeliveredOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.DELIVERED);
        Integer onTimeDeliveryRate = totalDeliveredOrders > 0 && onTimeDeliveries != null
                ? (int) ((onTimeDeliveries * 100) / totalDeliveredOrders)
                : 0;

        // Average response time
        BigDecimal averageResponseTimeHours = quotationRepository.avgResponseTimeHoursBySupplierId(supplierId) != null
                ? quotationRepository.avgResponseTimeHoursBySupplierId(supplierId)
                : BigDecimal.ZERO;

        // Order completion rate
        Long totalOrders = orderRepository.countBySupplierId(supplierId);
        Long completedOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.DELIVERED);
        BigDecimal orderCompletionRate = totalOrders > 0
                ? BigDecimal.valueOf(completedOrders)
                        .divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return SupplierPerformanceDto.builder()
                .averageRating(supplier.getAverageRating())
                .totalRatings(totalRatings)
                .deliveryRating(deliveryRating)
                .qualityRating(qualityRating)
                .communicationRating(communicationRating)
                .onTimeDeliveryRate(onTimeDeliveryRate.intValue())
                .averageResponseTimeHours(averageResponseTimeHours)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}

