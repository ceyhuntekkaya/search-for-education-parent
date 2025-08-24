package com.genixo.education.search.repository.user;

import com.genixo.education.search.enumaration.PermissionCategory;
import com.genixo.education.search.entity.user.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findAllByIsActiveTrueOrderByCategoryAscDisplayNameAsc();
    List<Permission> findByCategoryAndIsActiveTrueOrderByDisplayNameAsc(PermissionCategory category);

    List<Permission> findByUserIdThroughRoles(Long userId);


}
