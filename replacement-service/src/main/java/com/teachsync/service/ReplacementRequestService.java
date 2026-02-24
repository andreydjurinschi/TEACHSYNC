package com.teachsync.service;

import com.teachsync.domain.ReplacementRequest;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestBaseDto;
import com.teachsync.interaction.feign.clients.groupCourse.GroupCourseClient;
import com.teachsync.interaction.feign.clients.schedule.ScheduleClient;
import com.teachsync.interaction.feign.clients.users.UserClient;
import com.teachsync.interaction.requests.ScheduleBaseDtoRequest;
import com.teachsync.interaction.requests.nested.GroupCourseBaseInfoRequest;
import com.teachsync.interaction.requests.nested.TeacherBaseInfoRequest;
import com.teachsync.mappers.ReplacementMapper;
import com.teachsync.repository.ReplacementRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ReplacementRequestService {

    private final ReplacementRequestRepository repository;
    private final ScheduleClient scheduleClient;
    private final GroupCourseClient groupCourseClient;
    private final UserClient userClient;

    public ReplacementRequestService(ReplacementRequestRepository repository, ScheduleClient scheduleClient, GroupCourseClient groupCourseClient, UserClient userClient) {
        this.repository = repository;
        this.scheduleClient = scheduleClient;
        this.groupCourseClient = groupCourseClient;
        this.userClient = userClient;
    }

    public ReplacementRequestBaseDto getRequestById(Long id){
        ReplacementRequest replacementRequest = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(""));
        ReplacementRequestBaseDto result = ReplacementMapper.mapToBaseDto(replacementRequest);

        TeacherBaseInfoRequest requested = userClient.getTeacher(replacementRequest.getTeacherRequested());
        TeacherBaseInfoRequest approved = null;
        if (replacementRequest.getApprovedById() != null) {
            approved = userClient.getTeacher(replacementRequest.getApprovedById());
        }
        ScheduleBaseDtoRequest scheduleRequest = scheduleClient.getSchedule(replacementRequest.getScheduleId());
        GroupCourseBaseInfoRequest groupCourseBaseInfoRequest = groupCourseClient.groupCourseBaseInfoRequest(replacementRequest.getGroupCourseId());

        result.setTeacherBaseInfoRequest(requested);
        result.setApprovedByTeacherBaseInfoRequest(approved);
        result.setScheduleBaseDtoRequest(scheduleRequest);
        result.setGroupCourseBaseInfoRequest(groupCourseBaseInfoRequest);

        return result;
    }


}
