package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.ComplainComment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ComplainComment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplainCommentRepository extends JpaRepository<ComplainComment, Long> {}
