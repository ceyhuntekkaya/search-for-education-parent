package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    List<ProductAttribute> findByProductId(Long productId);

    Optional<ProductAttribute> findByProductIdAndAttributeName(Long productId, String attributeName);

    Boolean existsByProductIdAndAttributeName(Long productId, String attributeName);

    Boolean existsByProductIdAndAttributeNameAndIdNot(Long productId, String attributeName, Long id);
}

