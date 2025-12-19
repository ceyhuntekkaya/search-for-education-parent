package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.*;
import com.genixo.education.search.entity.supply.Supplier;
import com.genixo.education.search.enumaration.OrderStatus;
import com.genixo.education.search.enumaration.QuotationStatus;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.supply.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {

    private final OrderRepository orderRepository;
    private final RFQRepository rfqRepository;
    private final QuotationRepository quotationRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierRatingRepository supplierRatingRepository;
    private final OrderItemRepository orderItemRepository;
    private final CampusRepository campusRepository;

    // ================================ COMPANY REPORTS ================================

    public ProcurementReportDto generateProcurementReport(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating procurement report for company ID: {} from {} to {}", companyId, startDate, endDate);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        var company = campusRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        // Calculate totals
        BigDecimal totalSpending = orderRepository.sumTotalAmountByCompanyId(companyId) != null
                ? orderRepository.sumTotalAmountByCompanyId(companyId)
                : BigDecimal.ZERO;

        Long totalOrders = orderRepository.countByCompanyId(companyId);
        Long totalRFQs = rfqRepository.countByCompanyId(companyId);
        Long totalQuotations = quotationRepository.countByCompanyId(companyId);
        Long totalSuppliers = supplierRepository.countDistinctByCompanyId(companyId);

        // Orders by status
        Long pendingOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.PENDING);
        Long confirmedOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.CONFIRMED);
        Long deliveredOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.DELIVERED);
        Long cancelledOrders = orderRepository.countByCompanyIdAndStatus(companyId, OrderStatus.CANCELLED);

        // Spending breakdown
        List<ProcurementReportDto.SpendingByCategory> spendingByCategory = getSpendingByCategory(companyId, totalSpending);
        List<ProcurementReportDto.SpendingBySupplier> spendingBySupplier = getSpendingBySupplier(companyId, totalSpending);
        List<ProcurementReportDto.SpendingByMonth> spendingByMonth = getSpendingByMonth(companyId, startDate, endDate);

        // Top items
        List<TopProductDto> topProducts = orderItemRepository.getTopProductsByCompanyId(companyId, PageRequest.of(0, 10));
        List<TopSupplierDto> topSuppliers = orderRepository.getTopSuppliersByCompanyId(companyId, PageRequest.of(0, 10));

        return ProcurementReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .periodStart(startDate)
                .periodEnd(endDate)
                .companyId(companyId)
                .companyName(company.getName())
                .totalSpending(totalSpending)
                .totalOrders(totalOrders)
                .totalRFQs(totalRFQs)
                .totalQuotations(totalQuotations)
                .totalSuppliers(totalSuppliers)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .spendingByCategory(spendingByCategory)
                .spendingBySupplier(spendingBySupplier)
                .spendingByMonth(spendingByMonth)
                .topProducts(topProducts)
                .topSuppliers(topSuppliers)
                .build();
    }

    public SpendingByCategoryReportDto generateSpendingByCategoryReport(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating spending by category report for company ID: {} from {} to {}", companyId, startDate, endDate);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        var company = campusRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        BigDecimal totalSpending = orderRepository.sumTotalAmountByCompanyId(companyId) != null
                ? orderRepository.sumTotalAmountByCompanyId(companyId)
                : BigDecimal.ZERO;

        List<SpendingAnalyticsDto.SpendingByCategory> categorySpendings = 
                orderRepository.getSpendingByCategoryForCompany(companyId);

        List<SpendingByCategoryReportDto.CategorySpending> categorySpendingList = categorySpendings.stream()
                .map(cs -> {
                    Double percentage = totalSpending.compareTo(BigDecimal.ZERO) > 0
                            ? cs.getAmount().divide(totalSpending, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return SpendingByCategoryReportDto.CategorySpending.builder()
                            .categoryId(cs.getCategoryId())
                            .categoryName(cs.getCategoryName())
                            .amount(cs.getAmount())
                            .orderCount(cs.getOrderCount())
                            .productCount(0L) // Can be calculated if needed
                            .percentage(percentage)
                            .averageOrderValue(cs.getOrderCount() > 0
                                    ? cs.getAmount().divide(BigDecimal.valueOf(cs.getOrderCount()), 2, RoundingMode.HALF_UP)
                                    : BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());

        return SpendingByCategoryReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .periodStart(startDate)
                .periodEnd(endDate)
                .companyId(companyId)
                .companyName(company.getName())
                .totalSpending(totalSpending)
                .categorySpendings(categorySpendingList)
                .build();
    }

    public SpendingBySupplierReportDto generateSpendingBySupplierReport(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating spending by supplier report for company ID: {} from {} to {}", companyId, startDate, endDate);

        if (!campusRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }

        var company = campusRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        BigDecimal totalSpending = orderRepository.sumTotalAmountByCompanyId(companyId) != null
                ? orderRepository.sumTotalAmountByCompanyId(companyId)
                : BigDecimal.ZERO;

        List<TopSupplierDto> topSuppliers = orderRepository.getTopSuppliersByCompanyId(companyId, PageRequest.of(0, 100));

        List<SpendingBySupplierReportDto.SupplierSpending> supplierSpendings = topSuppliers.stream()
                .map(ts -> {
                    Supplier supplier = supplierRepository.findById(ts.getSupplierId())
                            .orElse(null);
                    
                    Double percentage = totalSpending.compareTo(BigDecimal.ZERO) > 0
                            ? ts.getTotalSpending().divide(totalSpending, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;

                    Long totalRatings = supplier != null
                            ? supplierRatingRepository.countBySupplierId(ts.getSupplierId())
                            : 0L;

                    return SpendingBySupplierReportDto.SupplierSpending.builder()
                            .supplierId(ts.getSupplierId())
                            .supplierCompanyName(ts.getSupplierCompanyName())
                            .supplierEmail(supplier != null ? supplier.getEmail() : null)
                            .supplierPhone(supplier != null ? supplier.getPhone() : null)
                            .amount(ts.getTotalSpending())
                            .orderCount(ts.getOrderCount())
                            .percentage(percentage)
                            .averageOrderValue(ts.getAverageOrderValue())
                            .averageRating(ts.getAverageRating())
                            .totalRatings(totalRatings)
                            .build();
                })
                .collect(Collectors.toList());

        return SpendingBySupplierReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .periodStart(startDate)
                .periodEnd(endDate)
                .companyId(companyId)
                .companyName(company.getName())
                .totalSpending(totalSpending)
                .supplierSpendings(supplierSpendings)
                .build();
    }

    // ================================ SUPPLIER REPORTS ================================

    public SalesReportDto generateSalesReport(Long supplierId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating sales report for supplier ID: {} from {} to {}", supplierId, startDate, endDate);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

        BigDecimal totalSales = orderRepository.sumTotalAmountBySupplierId(supplierId) != null
                ? orderRepository.sumTotalAmountBySupplierId(supplierId)
                : BigDecimal.ZERO;

        Long totalOrders = orderRepository.countBySupplierId(supplierId);
        Long totalQuotations = quotationRepository.countBySupplierId(supplierId);
        Long totalCustomers = orderRepository.countDistinctCustomersBySupplierId(supplierId);

        // Orders by status
        Long pendingOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.PENDING);
        Long confirmedOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.CONFIRMED);
        Long deliveredOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.DELIVERED);
        Long cancelledOrders = orderRepository.countBySupplierIdAndStatus(supplierId, OrderStatus.CANCELLED);

        // Sales breakdown
        List<SalesReportDto.SalesByCategory> salesByCategory = getSalesByCategory(supplierId, totalSales);
        List<SalesReportDto.SalesByCustomer> salesByCustomer = getSalesByCustomer(supplierId, totalSales);
        List<SalesReportDto.SalesByMonth> salesByMonth = getSalesByMonth(supplierId, startDate, endDate);

        // Top items
        List<TopProductDto> topProducts = orderItemRepository.getTopProductsBySupplierId(supplierId, PageRequest.of(0, 10));
        List<TopCustomerDto> topCustomers = orderRepository.getTopCustomersBySupplierId(supplierId, PageRequest.of(0, 10));

        return SalesReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .periodStart(startDate)
                .periodEnd(endDate)
                .supplierId(supplierId)
                .supplierCompanyName(supplier.getCompanyName())
                .totalSales(totalSales)
                .totalOrders(totalOrders)
                .totalQuotations(totalQuotations)
                .totalCustomers(totalCustomers)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .salesByCategory(salesByCategory)
                .salesByCustomer(salesByCustomer)
                .salesByMonth(salesByMonth)
                .topProducts(topProducts)
                .topCustomers(topCustomers)
                .build();
    }

    public QuotationPerformanceReportDto generateQuotationPerformanceReport(Long supplierId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating quotation performance report for supplier ID: {} from {} to {}", supplierId, startDate, endDate);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", supplierId);
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

        Long totalQuotations = quotationRepository.countBySupplierId(supplierId);
        Long draftQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.DRAFT);
        Long submittedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.SUBMITTED);
        Long acceptedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.ACCEPTED);
        Long rejectedQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.REJECTED);
        Long expiredQuotations = quotationRepository.countBySupplierIdAndStatus(supplierId, QuotationStatus.EXPIRED);

        BigDecimal acceptanceRate = totalQuotations > 0
                ? BigDecimal.valueOf(acceptedQuotations)
                        .divide(BigDecimal.valueOf(totalQuotations), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal averageQuotationValue = quotationRepository.avgTotalAmountBySupplierId(supplierId) != null
                ? quotationRepository.avgTotalAmountBySupplierId(supplierId)
                : BigDecimal.ZERO;

        BigDecimal averageResponseTimeHours = quotationRepository.avgResponseTimeHoursBySupplierId(supplierId) != null
                ? quotationRepository.avgResponseTimeHoursBySupplierId(supplierId)
                : BigDecimal.ZERO;

        Long averageResponseTimeDays = quotationRepository.avgResponseTimeDaysBySupplierId(supplierId);

        // Quotations by status
        List<QuotationPerformanceReportDto.QuotationByStatus> quotationsByStatus = new ArrayList<>();
        if (totalQuotations > 0) {
            quotationsByStatus.add(createQuotationByStatus("DRAFT", draftQuotations, totalQuotations));
            quotationsByStatus.add(createQuotationByStatus("SUBMITTED", submittedQuotations, totalQuotations));
            quotationsByStatus.add(createQuotationByStatus("ACCEPTED", acceptedQuotations, totalQuotations));
            quotationsByStatus.add(createQuotationByStatus("REJECTED", rejectedQuotations, totalQuotations));
            quotationsByStatus.add(createQuotationByStatus("EXPIRED", expiredQuotations, totalQuotations));
        }

        // Performance by month
        List<QuotationPerformanceReportDto.PerformanceByMonth> performanceByMonth = 
                getQuotationPerformanceByMonth(supplierId, startDate, endDate);

        return QuotationPerformanceReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .periodStart(startDate)
                .periodEnd(endDate)
                .supplierId(supplierId)
                .supplierCompanyName(supplier.getCompanyName())
                .totalQuotations(totalQuotations)
                .draftQuotations(draftQuotations)
                .submittedQuotations(submittedQuotations)
                .acceptedQuotations(acceptedQuotations)
                .rejectedQuotations(rejectedQuotations)
                .expiredQuotations(expiredQuotations)
                .acceptanceRate(acceptanceRate)
                .averageQuotationValue(averageQuotationValue)
                .averageResponseTimeHours(averageResponseTimeHours)
                .averageResponseTimeDays(averageResponseTimeDays)
                .quotationsByStatus(quotationsByStatus)
                .performanceByMonth(performanceByMonth)
                .topRFQs(new ArrayList<>()) // Can be implemented if needed
                .build();
    }

    // ================================ HELPER METHODS ================================

    private List<ProcurementReportDto.SpendingByCategory> getSpendingByCategory(Long companyId, BigDecimal totalSpending) {
        List<SpendingAnalyticsDto.SpendingByCategory> categorySpendings = 
                orderRepository.getSpendingByCategoryForCompany(companyId);

        return categorySpendings.stream()
                .map(cs -> {
                    Double percentage = totalSpending.compareTo(BigDecimal.ZERO) > 0
                            ? cs.getAmount().divide(totalSpending, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return ProcurementReportDto.SpendingByCategory.builder()
                            .categoryId(cs.getCategoryId())
                            .categoryName(cs.getCategoryName())
                            .amount(cs.getAmount())
                            .orderCount(cs.getOrderCount())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ProcurementReportDto.SpendingBySupplier> getSpendingBySupplier(Long companyId, BigDecimal totalSpending) {
        List<TopSupplierDto> topSuppliers = orderRepository.getTopSuppliersByCompanyId(companyId, PageRequest.of(0, 100));

        return topSuppliers.stream()
                .map(ts -> {
                    Double percentage = totalSpending.compareTo(BigDecimal.ZERO) > 0
                            ? ts.getTotalSpending().divide(totalSpending, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return ProcurementReportDto.SpendingBySupplier.builder()
                            .supplierId(ts.getSupplierId())
                            .supplierCompanyName(ts.getSupplierCompanyName())
                            .amount(ts.getTotalSpending())
                            .orderCount(ts.getOrderCount())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ProcurementReportDto.SpendingByMonth> getSpendingByMonth(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<ProcurementReportDto.SpendingByMonth> spendingByMonth = new ArrayList<>();
        
        LocalDate current = startDate.withDayOfMonth(1);
        while (!current.isAfter(endDate)) {
            LocalDate monthEnd = current.plusMonths(1).minusDays(1);
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }
            
            java.time.LocalDateTime monthStartDateTime = current.atStartOfDay();
            java.time.LocalDateTime monthEndDateTime = monthEnd.plusDays(1).atStartOfDay();
            
            BigDecimal monthSpending = orderRepository.sumTotalAmountByCompanyIdAndDateRange(
                    companyId, monthStartDateTime, monthEndDateTime) != null
                    ? orderRepository.sumTotalAmountByCompanyIdAndDateRange(companyId, monthStartDateTime, monthEndDateTime)
                    : BigDecimal.ZERO;

            Long monthOrderCount = orderRepository.countByCompanyIdAndDateRange(companyId, monthStartDateTime, monthEndDateTime);

            spendingByMonth.add(ProcurementReportDto.SpendingByMonth.builder()
                    .month(current)
                    .amount(monthSpending)
                    .orderCount(monthOrderCount)
                    .build());

            current = current.plusMonths(1);
        }

        return spendingByMonth;
    }

    private List<SalesReportDto.SalesByCategory> getSalesByCategory(Long supplierId, BigDecimal totalSales) {
        List<SalesAnalyticsDto.SalesByCategory> categorySales = 
                orderRepository.getSalesByCategoryForSupplier(supplierId);

        return categorySales.stream()
                .map(cs -> {
                    Double percentage = totalSales.compareTo(BigDecimal.ZERO) > 0
                            ? cs.getAmount().divide(totalSales, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return SalesReportDto.SalesByCategory.builder()
                            .categoryId(cs.getCategoryId())
                            .categoryName(cs.getCategoryName())
                            .amount(cs.getAmount())
                            .orderCount(cs.getOrderCount())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<SalesReportDto.SalesByCustomer> getSalesByCustomer(Long supplierId, BigDecimal totalSales) {
        List<TopCustomerDto> topCustomers = orderRepository.getTopCustomersBySupplierId(supplierId, PageRequest.of(0, 100));

        return topCustomers.stream()
                .map(tc -> {
                    Double percentage = totalSales.compareTo(BigDecimal.ZERO) > 0
                            ? tc.getTotalSpending().divide(totalSales, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return SalesReportDto.SalesByCustomer.builder()
                            .companyId(tc.getCompanyId())
                            .companyName(tc.getCompanyName())
                            .amount(tc.getTotalSpending())
                            .orderCount(tc.getOrderCount())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<SalesReportDto.SalesByMonth> getSalesByMonth(Long supplierId, LocalDate startDate, LocalDate endDate) {
        List<SalesReportDto.SalesByMonth> salesByMonth = new ArrayList<>();
        
        LocalDate current = startDate.withDayOfMonth(1);
        while (!current.isAfter(endDate)) {
            LocalDate monthEnd = current.plusMonths(1).minusDays(1);
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }
            
            java.time.LocalDateTime monthStartDateTime = current.atStartOfDay();
            java.time.LocalDateTime monthEndDateTime = monthEnd.plusDays(1).atStartOfDay();
            
            BigDecimal monthSales = orderRepository.sumTotalAmountBySupplierIdAndDateRange(
                    supplierId, monthStartDateTime, monthEndDateTime) != null
                    ? orderRepository.sumTotalAmountBySupplierIdAndDateRange(supplierId, monthStartDateTime, monthEndDateTime)
                    : BigDecimal.ZERO;

            Long monthOrderCount = orderRepository.countBySupplierIdAndDateRange(supplierId, monthStartDateTime, monthEndDateTime);

            salesByMonth.add(SalesReportDto.SalesByMonth.builder()
                    .month(current)
                    .amount(monthSales)
                    .orderCount(monthOrderCount)
                    .build());

            current = current.plusMonths(1);
        }

        return salesByMonth;
    }

    private List<QuotationPerformanceReportDto.PerformanceByMonth> getQuotationPerformanceByMonth(
            Long supplierId, LocalDate startDate, LocalDate endDate) {
        // This would require additional repository methods
        // For now, return empty list
        return new ArrayList<>();
    }

    private QuotationPerformanceReportDto.QuotationByStatus createQuotationByStatus(
            String status, Long count, Long total) {
        Double percentage = total > 0 ? (count.doubleValue() / total.doubleValue()) * 100 : 0.0;
        return QuotationPerformanceReportDto.QuotationByStatus.builder()
                .status(status)
                .count(count)
                .totalValue(BigDecimal.ZERO) // Can be calculated if needed
                .percentage(percentage)
                .build();
    }
}

