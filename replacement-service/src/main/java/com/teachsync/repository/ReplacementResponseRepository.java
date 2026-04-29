package com.teachsync.repository;

import com.teachsync.domain.ReplacementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplacementResponseRepository extends JpaRepository<ReplacementResponse, Long> {
    Optional<ReplacementResponse> findByReplacementRequestIdAndTeacherResponse(Long replacementRequestId, Long teacherResponse);
    List<ReplacementResponse> findByReplacementRequestId(Long replacementRequestId);
    List<ReplacementResponse> findByTeacherResponse(Long teacherResponse);
}
