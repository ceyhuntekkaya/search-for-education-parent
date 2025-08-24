package com.genixo.education.search.repository.user;

import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.enumaration.UserType;
import com.genixo.education.search.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);


    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    Boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPhoneAndPhoneVerificationCode(String phone, String code);

    Optional<User> findByEmailOrPhone(String email, String phone);

    Optional<User> findByPasswordResetToken(String token);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    LEFT JOIN u.userRoles ur
    LEFT JOIN ur.role r
    LEFT JOIN u.institutionAccess ia
    WHERE (:searchTerm IS NULL OR 
           LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
           LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
           LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
           LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
      AND (:userType IS NULL OR u.userType = :userType)
      AND (:isActive IS NULL OR u.isActive = :isActive)
      AND (:roleId IS NULL OR r.id = :roleId)
      AND (:roleLevel IS NULL OR r.roleLevel = :roleLevel)
      AND (:institutionId IS NULL OR ia.entityId = :institutionId)
      AND (:accessType IS NULL OR ia.accessType = :accessType)
      AND (:createdAfter IS NULL OR u.createdAt >= :createdAfter)
      AND (:createdBefore IS NULL OR u.createdAt <= :createdBefore)
      AND (:lastLoginAfter IS NULL OR u.lastLoginAt >= :lastLoginAfter)
      AND (:lastLoginBefore IS NULL OR u.lastLoginAt <= :lastLoginBefore)
      AND (:countryId IS NULL OR u.country.id = :countryId)
      AND (:provinceId IS NULL OR u.province.id = :provinceId)
      AND (:districtId IS NULL OR u.district.id = :districtId)
      AND (:neighborhoodId IS NULL OR u.neighborhood.id = :neighborhoodId)
""")
    Page<User> searchUsers(String searchTerm, UserType userType, Boolean isActive, Long roleId,
                           RoleLevel roleLevel, Long institutionId, AccessType accessType,
                           LocalDateTime createdAfter, LocalDateTime createdBefore,
                           LocalDateTime lastLoginAfter, LocalDateTime lastLoginBefore,
                           Long countryId, Long provinceId, Long districtId, Long neighborhoodId,
                           Pageable pageable);

    Long countByIsActive(Boolean isActive);

    Long countByUserType(UserType userType);

    Long countByCreatedAtAfter(LocalDateTime date);

    Long countByLastLoginAtAfter(LocalDateTime date);

    Long countByIsEmailVerified(Boolean isVerified);

    Long countByIsPhoneVerified(Boolean isVerified);

}
