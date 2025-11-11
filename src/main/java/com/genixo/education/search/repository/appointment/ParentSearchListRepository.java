package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.ParentSearchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentSearchListRepository extends JpaRepository<ParentSearchList, Long> {
    List<ParentSearchList> findByParentId(Long parentId);
}
