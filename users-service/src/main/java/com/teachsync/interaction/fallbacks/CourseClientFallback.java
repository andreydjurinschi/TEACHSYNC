package com.teachsync.interaction.fallbacks;

import com.teachsync.interaction.clients.CourseClient;
import com.teachsync.interaction.requests.CourseBaseInfoRequest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseClientFallback implements CourseClient {

    @Override
    public List<CourseBaseInfoRequest> requestForCourseInfo(Long id) {
        return List.of();
    }
}
