package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.UnionMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UnionMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UnionMemberRepository extends JpaRepository<UnionMember, Long> {}
