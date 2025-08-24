package com.genixo.education.search.repository.user;

import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInstitutionAccessRepository extends JpaRepository<UserInstitutionAccess, Long> {


    Boolean existsByUserIdAndAccessTypeAndEntityIdAndIsActiveTrue(Long userId, AccessType accessType, Long entityId);
    Optional<UserInstitutionAccess> findByUserIdAndAccessTypeAndEntityIdAndIsActiveTrue(Long userId, AccessType accessType, Long entityId);
    List<UserInstitutionAccess> findByUserIdAndIsActiveTrueOrderByGrantedAtDesc(Long userId);
}
