package com.teachsync.notificationservice.service;

import com.teachsync.notificationservice.dto.NotificationDto;
import com.teachsync.notificationservice.enums.TargetRole;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationSseService {

    private static final long TIMEOUT = 30L * 60L * 1000L;

    private final ConcurrentHashMap<Long, Set<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TargetRole, Set<ClientConnection>> roleEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId, TargetRole role) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        ClientConnection connection = new ClientConnection(userId, emitter);

        userEmitters.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(emitter);
        roleEmitters.computeIfAbsent(role, key -> ConcurrentHashMap.newKeySet()).add(connection);

        Runnable cleanup = () -> {
            Set<SseEmitter> emitters = userEmitters.get(userId);
            if (emitters != null) {
                emitters.remove(emitter);
            }
            Set<ClientConnection> connections = roleEmitters.get(role);
            if (connections != null) {
                connections.remove(connection);
            }
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name("connected").data("connected"));
        } catch (IOException e) {
            cleanup.run();
        }

        return emitter;
    }

    public void sendToUser(Long userId, NotificationDto notification) {
        Set<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters == null) {
            return;
        }
        emitters.forEach(emitter -> send(emitter, notification));
    }

    public void sendToRole(TargetRole role, NotificationDto notification, NotificationPreferenceService preferenceService) {
        Set<ClientConnection> connections = roleEmitters.get(role);
        if (connections == null) {
            return;
        }
        connections.forEach(connection -> {
            if (preferenceService.shouldPushToUser(connection.userId(), notification.getTargetSubject())) {
                send(connection.emitter(), notification);
            }
        });
    }

    private void send(SseEmitter emitter, NotificationDto notification) {
        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private record ClientConnection(Long userId, SseEmitter emitter) {
    }
}
