package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InstitutionTypeGroupRepository extends JpaRepository<InstitutionPropertyValue, Long> {



}
