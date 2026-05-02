package com.teachsync.repository;

import com.teachsync.domain.ReplacementRequest;
import com.teachsync.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ReplacementRequestRepository extends JpaRepository<ReplacementRequest, Long> {
    @Query("""
            select distinct rr
            from ReplacementRequest rr
            left join rr.replacementResponses resp
            where rr.teacherRequested = :teacherId
               or rr.approvedById = :teacherId
               or resp.teacherResponse = :teacherId
            order by rr.lessonDate desc, rr.requestedAt desc
            """)
    List<ReplacementRequest> findVisibleForTeacher(@Param("teacherId") Long teacherId);

    boolean existsByScheduleIdAndLessonDateAndStatusIn(Long scheduleId, LocalDate lessonDate, Set<Status> statuses);

    List<ReplacementRequest> findByStatus(Status status);

    long countByStatus(Status status);

    long countByTeacherRequested(Long teacherRequested);

    long countByApprovedById(Long approvedById);

    @Query("""
            select rr
            from ReplacementRequest rr
            where rr.status in :statuses
            order by rr.lessonDate asc, rr.requestedAt asc
            """)
    List<ReplacementRequest> findByStatusInOrderByPriority(@Param("statuses") Set<Status> statuses);
}
