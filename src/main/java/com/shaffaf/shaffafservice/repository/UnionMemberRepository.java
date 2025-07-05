package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.UnionMember;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UnionMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UnionMemberRepository extends JpaRepository<UnionMember, Long> {
    /**
     * Find all union members who are union heads.
     */
    List<UnionMember> findByIsUnionHeadTrue();

    /**
     * Find all union members who are not union heads.
     */
    List<UnionMember> findByIsUnionHeadFalse();

    /**
     * Find union members by project ID and union head status.
     */
    List<UnionMember> findByProjectIdAndIsUnionHead(Long projectId, Boolean isUnionHead);
}
