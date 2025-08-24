package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.dto.subscription.SubscriptionFilterDto;
import com.genixo.education.search.entity.subscription.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionRepositoryCustom {
    Page<Subscription> findByFilters(SubscriptionFilterDto filter, Pageable pageable);
}