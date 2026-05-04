package com.teachsync.services.feign;

import com.teachsync.interaction.feign.clients.UserClient;
import com.teachsync.interaction.feign.requests.TeacherRequest;
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

    private final UserClient userClient;
    private final Duration ttl;
    private final ConcurrentHashMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    public ReferenceDataCacheService(UserClient userClient,
                                     @Value("${teachsync.cache.reference.ttl-seconds:600}") long ttlSeconds) {
        this.userClient = userClient;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public TeacherRequest getTeacher(Long id) {
        return cached("teacher:" + id, () -> userClient.getTeacher(id), TeacherRequest.class);
    }

    public List<TeacherRequest> getTeachersByIds(List<Long> ids) {
        List<TeacherRequest> teachers = cachedList("teachers:batch:" + ids, () -> userClient.getTeachersByIds(ids));
        teachers.forEach(teacher -> put("teacher:" + teacher.id(), teacher));
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
