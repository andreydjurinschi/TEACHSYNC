package com.teachsync.repository;

import com.teachsync.domain.ReplacementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplacementResponseRepository extends JpaRepository<ReplacementResponse, Long> { }
