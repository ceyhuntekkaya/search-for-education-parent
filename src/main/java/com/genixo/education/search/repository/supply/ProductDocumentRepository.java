package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.ProductDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDocumentRepository extends JpaRepository<ProductDocument, Long> {

    List<ProductDocument> findByProductId(Long productId);
}

