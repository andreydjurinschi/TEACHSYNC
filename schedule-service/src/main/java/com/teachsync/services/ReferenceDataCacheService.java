package com.teachsync.services;

import com.teachsync.dto_s.feign.GroupCourseDto;
import com.teachsync.interation.feign.Role;
import com.teachsync.interation.feign.clients.GroupCourseClient;
import com.teachsync.interation.feign.clients.TeacherClient;
import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class ReferenceDataCacheService {

    private final TeacherClient teacherClient;
    private final GroupCourseClient groupCourseClient;
    private final Duration ttl;
    private final ConcurrentHashMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    public ReferenceDataCacheService(TeacherClient teacherClient,
                                     GroupCourseClient groupCourseClient,
                                     @Value("${teachsync.cache.reference.ttl-seconds:600}") long ttlSeconds) {
        this.teacherClient = teacherClient;
        this.groupCourseClient = groupCourseClient;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public TeacherBaseInfoRequest getTeacher(Long userId) {
        return cached("teacher:" + userId, () -> teacherClient.requestForUserFromUserService(userId), TeacherBaseInfoRequest.class);
    }

    public List<TeacherBaseInfoRequest> getAllTeachers(Role role) {
        return cachedList("teachers:role:" + role, () -> teacherClient.getAllTeachers(role));
    }

    public List<TeacherBaseInfoRequest> getTeachersByIds(List<Long> ids) {
        List<TeacherBaseInfoRequest> teachers = cachedList("teachers:batch:" + ids, () -> teacherClient.getTeachersByIds(ids));
        teachers.forEach(teacher -> put("teacher:" + teacher.getId(), teacher));
        return teachers;
    }

    public GroupCourseBaseInfoRequest getGroupCourse(Long groupCourseId) {
        return cached("groupCourse:" + groupCourseId, () -> groupCourseClient.groupCourseBaseInfoRequest(groupCourseId), GroupCourseBaseInfoRequest.class);
    }

    public List<GroupCourseBaseInfoRequest> getAllGroupCourses() {
        List<GroupCourseBaseInfoRequest> groupCourses = cachedList("groupCourses:all", groupCourseClient::getAllGroupCourses);
        groupCourses.forEach(groupCourse -> put("groupCourse:" + groupCourse.getId(), groupCourse));
        return groupCourses;
    }

    public List<GroupCourseBaseInfoRequest> getGroupCoursesByIds(List<Long> ids) {
        List<GroupCourseBaseInfoRequest> groupCourses = cachedList("groupCourses:batch:" + ids, () -> groupCourseClient.getGroupCoursesByIds(ids));
        groupCourses.forEach(groupCourse -> put("groupCourse:" + groupCourse.getId(), groupCourse));
        return groupCourses;
    }

    public GroupCourseDto getGroupSizeInformation(Long groupCourseId) {
        return cached("groupCourseSize:" + groupCourseId, () -> groupCourseClient.getGroupSizeInformation(groupCourseId), GroupCourseDto.class);
    }

    private <T> T cached(String key, Supplier<T> liveCall, Class<T> type) {
        try {
            T value = liveCall.get();
            put(key, value);
            return value;
        } catch (RuntimeException ex) {
            return getFresh(key, type).orElseThrow(() -> ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> cachedList(String key, Supplier<List<T>> liveCall) {
        try {
            List<T> value = liveCall.get();
            put(key, value);
            return value;
        } catch (RuntimeException ex) {
            return getFresh(key, List.class)
                    .map(value -> (List<T>) value)
                    .orElseThrow(() -> ex);
        }
    }

    private void put(String key, Object value) {
        if (value != null) {
            cache.put(key, new CacheEntry<>(value, Instant.now()));
        }
    }

    private <T> Optional<T> getFresh(String key, Class<T> type) {
        CacheEntry<?> entry = cache.get(key);
        if (entry == null || entry.createdAt().plus(ttl).isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(type.cast(entry.value()));
    }

    private record CacheEntry<T>(T value, Instant createdAt) {
    }
}
