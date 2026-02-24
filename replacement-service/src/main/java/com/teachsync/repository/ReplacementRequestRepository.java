package com.teachsync.repository;

import com.teachsync.domain.ReplacementRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplacementRequestRepository extends JpaRepository<ReplacementRequest, Long> { }
