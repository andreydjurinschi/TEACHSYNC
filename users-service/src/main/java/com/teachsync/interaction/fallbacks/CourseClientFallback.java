package com.teachsync.interaction.fallbacks;

import com.teachsync.interaction.clients.CourseClient;
import com.teachsync.interaction.requests.CourseBaseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseClientFallback implements CourseClient {

    @Override
    public List<CourseBaseDto> requestForCourseInfo(Long id) {
        return List.of();
    }
}
