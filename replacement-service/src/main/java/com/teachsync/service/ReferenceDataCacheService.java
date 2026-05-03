package com.teachsync.service;

import com.teachsync.interaction.feign.clients.groupCourse.GroupCourseClient;
import com.teachsync.interaction.feign.clients.schedule.ScheduleClient;
import com.teachsync.interaction.feign.clients.users.UserClient;
import com.teachsync.interaction.requests.ScheduleBaseDtoRequest;
import com.teachsync.interaction.requests.nested.GroupCourseBaseInfoRequest;
import com.teachsync.interaction.requests.nested.TeacherBaseInfoRequest;
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

    private final ScheduleClient scheduleClient;
    private final GroupCourseClient groupCourseClient;
    private final UserClient userClient;
    private final Duration ttl;
    private final ConcurrentHashMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    public ReferenceDataCacheService(ScheduleClient scheduleClient,
                                     GroupCourseClient groupCourseClient,
                                     UserClient userClient,
                                     @Value("${teachsync.cache.reference.ttl-seconds:600}") long ttlSeconds) {
        this.scheduleClient = scheduleClient;
        this.groupCourseClient = groupCourseClient;
        this.userClient = userClient;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public ScheduleBaseDtoRequest getSchedule(Long id) {
        return cached("schedule:" + id, () -> scheduleClient.getSchedule(id), ScheduleBaseDtoRequest.class);
    }

    public GroupCourseBaseInfoRequest getGroupCourse(Long id) {
        return cached("groupCourse:" + id, () -> groupCourseClient.groupCourseBaseInfoRequest(id), GroupCourseBaseInfoRequest.class);
    }

    public TeacherBaseInfoRequest getTeacher(Long id) {
        return cached("teacher:" + id, () -> userClient.getTeacher(id), TeacherBaseInfoRequest.class);
    }

    public List<TeacherBaseInfoRequest> getTeachersByIds(List<Long> ids) {
        List<TeacherBaseInfoRequest> teachers = cachedList("teachers:batch:" + ids, () -> userClient.getByIds(ids));
        teachers.forEach(teacher -> put("teacher:" + teacher.getId(), teacher));
        return teachers;
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
