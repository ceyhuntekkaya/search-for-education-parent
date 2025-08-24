package com.genixo.education.search.repository.pricing;

import com.genixo.education.search.entity.pricing.SchoolPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolPricingRepository extends JpaRepository<SchoolPricing, Long> {
}
