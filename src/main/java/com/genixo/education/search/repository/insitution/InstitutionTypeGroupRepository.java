package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionTypeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InstitutionTypeGroupRepository extends JpaRepository<InstitutionTypeGroup, Long> {


    @Query("SELECT g FROM InstitutionTypeGroup g where g.name= :name")
    List<InstitutionTypeGroup> checkIfExist(@Param("name") String name);
}
