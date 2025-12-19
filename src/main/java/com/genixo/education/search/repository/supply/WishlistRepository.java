package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END " +
           "FROM Wishlist w WHERE w.user.id = :userId AND w.product.id = :productId")
    Boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}

