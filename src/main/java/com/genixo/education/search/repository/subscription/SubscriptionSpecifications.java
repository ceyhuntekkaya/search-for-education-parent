package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.dto.subscription.SubscriptionFilterDto;
import com.genixo.education.search.entity.subscription.Subscription;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionSpecifications {

    public static Specification<Subscription> withFilters(SubscriptionFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // isActive her zaman true
            predicates.add(cb.isTrue(root.get("isActive")));

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getPlanId() != null) {
                predicates.add(cb.equal(root.get("subscriptionPlan").get("id"), filter.getPlanId()));
            }

            if (filter.getCampusName() != null) {
                predicates.add(cb.like(cb.lower(root.get("campus").get("name")),
                        "%" + filter.getCampusName().toLowerCase() + "%"));
            }

            if (filter.getBrandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), filter.getBrandId()));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate()));
            }

            if (filter.getAutoRenew() != null) {
                predicates.add(cb.equal(root.get("autoRenew"), filter.getAutoRenew()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            if (filter.getCurrency() != null) {
                predicates.add(cb.equal(root.get("currency"), filter.getCurrency()));
            }

            if (filter.getHasTrial() != null) {
                predicates.add(cb.equal(root.get("hasTrial"), filter.getHasTrial()));
            }

            if (filter.getBillingCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("billingCity")),
                        "%" + filter.getBillingCity().toLowerCase() + "%"));
            }

            if (filter.getTaxNumber() != null) {
                predicates.add(cb.like(cb.lower(root.get("taxNumber")),
                        "%" + filter.getTaxNumber().toLowerCase() + "%"));
            }

            if (filter.getSearchTerm() != null) {
                Predicate billingName = cb.like(cb.lower(root.get("billingName")),
                        "%" + filter.getSearchTerm().toLowerCase() + "%");
                Predicate billingEmail = cb.like(cb.lower(root.get("billingEmail")),
                        "%" + filter.getSearchTerm().toLowerCase() + "%");
                predicates.add(cb.or(billingName, billingEmail));
            }

            // diğer boolean ve date alanları benzer şekilde eklenebilir…

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
