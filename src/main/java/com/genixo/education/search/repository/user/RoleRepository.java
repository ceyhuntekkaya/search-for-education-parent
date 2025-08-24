package com.genixo.education.search.repository.user;

import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Boolean existsByName(String name);

    List<Role> findAllByIsActiveTrueOrderByDisplayName();

    List<Role> findByRoleLevelAndIsActiveTrueOrderByDisplayName(RoleLevel roleLevel);
}
