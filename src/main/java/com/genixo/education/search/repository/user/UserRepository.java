package com.genixo.education.search.repository.user;

import com.genixo.education.search.dto.appointment.AppointmentAvailabilityDto;
import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.enumaration.UserType;
import com.genixo.education.search.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

/* ceyhun
    Page<User> searchUsers(String searchTerm, UserType userType, Boolean isActive, Long roleId,
                           RoleLevel roleLevel, Long institutionId, AccessType accessType,
                           LocalDateTime createdAfter, LocalDateTime createdBefore,
                           LocalDateTime lastLoginAfter, LocalDateTime lastLoginBefore,
                           Long countryId, Long provinceId, Long districtId, Long neighborhoodId,
                           Pageable pageable);

 */

    Long countByIsActive(Boolean isActive);

    Long countByUserType(UserType userType);

    Long countByCreatedAtAfter(LocalDateTime date);

    Long countByLastLoginAtAfter(LocalDateTime date);

    Long countByIsEmailVerified(Boolean isVerified);

    Long countByIsPhoneVerified(Boolean isVerified);



    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.id = :id")
    Optional<User> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailAndIsActiveTrue(@Param("email") String email);


    @Query("SELECT u.user FROM UserInstitutionAccess u WHERE (u.accessType = 'CAMPUS' AND u.entityId = :campusId) OR (u.accessType = 'SCHOOL' AND u.entityId IN :schoolIds)")
    List<User> findByCampus(@Param("campusId") Long campusId, @Param("schoolIds") Set<Long> schoolIds);

}
