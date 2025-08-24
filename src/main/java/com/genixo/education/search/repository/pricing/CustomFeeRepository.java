package com.genixo.education.search.repository.pricing;

import com.genixo.education.search.entity.pricing.CustomFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFeeRepository extends JpaRepository<CustomFee, Long> {
}
