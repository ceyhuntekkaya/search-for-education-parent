package com.genixo.education.search.repository.ai;

import com.genixo.education.search.entity.ai.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmission, Long> {

    // Conversation'a ait form submission
    Optional<FormSubmission> findByConversationIdAndIsActiveTrue(Long conversationId);

    // Kullanıcının tüm form submission'ları
    List<FormSubmission> findByUserIdAndIsActiveTrueOrderBySubmittedAtDesc(Long userId);

    // Şehre göre submission'lar
    List<FormSubmission> findByCityAndIsActiveTrueOrderBySubmittedAtDesc(String city);

    // Belirli bir tarih aralığındaki submission'lar
    List<FormSubmission> findBySubmittedAtBetweenAndIsActiveTrue(
            LocalDateTime start,
            LocalDateTime end
    );

    // Kullanıcının son submission'ı
    Optional<FormSubmission> findFirstByUserIdAndIsActiveTrueOrderBySubmittedAtDesc(Long userId);

    // İnstitution type'a göre
    List<FormSubmission> findByInstitutionTypeAndIsActiveTrueOrderBySubmittedAtDesc(String institutionType);

    // Fiyat aralığına göre
    @Query("SELECT f FROM FormSubmission f WHERE f.minPrice >= :minPrice AND f.maxPrice <= :maxPrice AND f.isActive = true ORDER BY f.submittedAt DESC")
    List<FormSubmission> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // İstatistikler için - en popüler şehirler
    @Query("SELECT f.city, COUNT(f) as count FROM FormSubmission f WHERE f.isActive = true GROUP BY f.city ORDER BY count DESC")
    List<Object[]> getMostPopularCities();

    // İstatistikler için - en popüler institution type
    @Query("SELECT f.institutionType, COUNT(f) as count FROM FormSubmission f WHERE f.isActive = true GROUP BY f.institutionType ORDER BY count DESC")
    List<Object[]> getMostPopularInstitutionTypes();

    // Kullanıcının toplam form sayısı
    Long countByUserIdAndIsActiveTrue(Long userId);
}