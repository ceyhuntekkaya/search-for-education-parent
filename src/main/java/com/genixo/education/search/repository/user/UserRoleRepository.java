package com.genixo.education.search.repository.user;

import com.genixo.education.search.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Boolean existsByUserIdAndRoleIdAndIsActiveTrue(Long userId, Long roleId);
    Optional<UserRole> findByUserIdAndRoleIdAndIsActiveTrue(Long userId, Long roleId);
    List<UserRole> findByUserIdAndIsActiveTrueOrderByGrantedAtDesc(Long userId);
}
