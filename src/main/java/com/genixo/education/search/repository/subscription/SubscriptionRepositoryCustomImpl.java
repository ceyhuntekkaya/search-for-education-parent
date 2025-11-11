package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.dto.subscription.SubscriptionFilterDto;
import com.genixo.education.search.entity.subscription.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryCustomImpl implements SubscriptionRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<Subscription> findByFilters(SubscriptionFilterDto filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Subscription> query = cb.createQuery(Subscription.class);
        Root<Subscription> root = query.from(Subscription.class);

        List<Predicate> predicates = new ArrayList<>();

        // Base predicate - only active subscriptions
        predicates.add(cb.equal(root.get("isActive"), true));

        // Filter by status
        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        // Filter by subscription plan
        if (filter.getPlanId() != null) {
            predicates.add(cb.equal(root.get("subscriptionPlan").get("id"), filter.getPlanId()));
        }

        // Filter by campus name
        if (StringUtils.hasText(filter.getCampusName())) {
            predicates.add(cb.like(
                    cb.lower(root.get("campus").get("name")),
                    "%" + filter.getCampusName().toLowerCase() + "%"
            ));
        }

        // Filter by date range
        if (filter.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDate()));
        }

        if (filter.getEndDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getEndDate()));
        }

        // Filter by auto renew
        if (filter.getAutoRenew() != null) {
            predicates.add(cb.equal(root.get("autoRenew"), filter.getAutoRenew()));
        }

        // Filter by price range
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
        }

        // Apply predicates
        query.where(predicates.toArray(new Predicate[0]));

        // Apply sorting
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    orders.add(cb.asc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(order.getProperty())));
                }
            });
            query.orderBy(orders);
        }

        // Execute query with pagination
        TypedQuery<Subscription> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Subscription> subscriptions = typedQuery.getResultList();

        // Count total results
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Subscription> countRoot = countQuery.from(Subscription.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(subscriptions, pageable, total);
    }
}