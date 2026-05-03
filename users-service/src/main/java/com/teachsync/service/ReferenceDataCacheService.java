package com.teachsync.service;

import com.teachsync.interaction.clients.CourseClient;
import com.teachsync.interaction.requests.CourseBaseDto;
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

    private final CourseClient courseClient;
    private final Duration ttl;
    private final ConcurrentHashMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    public ReferenceDataCacheService(CourseClient courseClient,
                                     @Value("${teachsync.cache.reference.ttl-seconds:600}") long ttlSeconds) {
        this.courseClient = courseClient;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public List<CourseBaseDto> getCoursesForTeacher(Long teacherId) {
        return cachedList("teacherCourses:" + teacherId, () -> courseClient.requestForCourseInfo(teacherId));
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
