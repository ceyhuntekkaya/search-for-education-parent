package com.genixo.education.search.entity.appointment;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "parent_search_list")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParentSearchList extends BaseEntity {

    private Long parentId;
    private String name;
    private String data;
}
