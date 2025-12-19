package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.RFQItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RFQItemRepository extends JpaRepository<RFQItem, Long> {

    List<RFQItem> findByRfqId(Long rfqId);
}

