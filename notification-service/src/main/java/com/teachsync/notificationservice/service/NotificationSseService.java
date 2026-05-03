package com.teachsync.notificationservice.service;

import com.teachsync.notificationservice.dto.NotificationDto;
import com.teachsync.notificationservice.enums.TargetRole;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class NotificationSseService {

    private static final long TIMEOUT = 30L * 60L * 1000L;

    private final ConcurrentHashMap<Long, Set<ClientConnection>> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TargetRole, Set<ClientConnection>> roleEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId, TargetRole role) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        ClientConnection connection = new ClientConnection(userId, role, emitter);

        userEmitters.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(connection);
        roleEmitters.computeIfAbsent(role, key -> ConcurrentHashMap.newKeySet()).add(connection);

        Runnable cleanup = () -> removeConnection(connection);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());

        if (!sendEvent(connection, SseEmitter.event().name("connected").data("connected"))) {
            removeConnection(connection);
        }

        return emitter;
    }

    public void sendToUser(Long userId, NotificationDto notification) {
        Set<ClientConnection> connections = userEmitters.get(userId);
        if (connections == null) {
            return;
        }
        connections.forEach(connection -> {
            if (!sendNotification(connection, notification)) {
                removeConnection(connection);
            }
        });
    }

    public void sendToRole(TargetRole role, NotificationDto notification, NotificationPreferenceService preferenceService) {
        Set<ClientConnection> connections = roleEmitters.get(role);
        if (connections == null) {
            return;
        }
        connections.forEach(connection -> {
            if (preferenceService.shouldPushToUser(connection.userId(), notification.getTargetSubject())) {
                if (!sendNotification(connection, notification)) {
                    removeConnection(connection);
                }
            }
        });
    }

    @Scheduled(fixedDelay = 15000)
    public void heartbeat() {
        userEmitters.values().forEach(connections ->
                connections.forEach(connection -> {
                    if (!sendEvent(connection, SseEmitter.event().comment("heartbeat"))) {
                        removeConnection(connection);
                    }
                })
        );
    }

    private boolean sendNotification(ClientConnection connection, NotificationDto notification) {
        return sendEvent(connection, SseEmitter.event()
                .name("notification")
                .data(notification));
    }

    private boolean sendEvent(ClientConnection connection, SseEmitter.SseEventBuilder event) {
        if (!connection.active().get()) {
            return false;
        }
        try {
            synchronized (connection.emitter()) {
                if (!connection.active().get()) {
                    return false;
                }
                connection.emitter().send(event);
            }
            return true;
        } catch (Exception e) {
            connection.active().set(false);
            return false;
        }
    }

    private void removeConnection(ClientConnection connection) {
        connection.active().set(false);

        Set<ClientConnection> userConnections = userEmitters.get(connection.userId());
        if (userConnections != null) {
            userConnections.remove(connection);
            if (userConnections.isEmpty()) {
                userEmitters.remove(connection.userId(), userConnections);
            }
        }

        Set<ClientConnection> roleConnections = roleEmitters.get(connection.role());
        if (roleConnections != null) {
            roleConnections.remove(connection);
            if (roleConnections.isEmpty()) {
                roleEmitters.remove(connection.role(), roleConnections);
            }
        }
    }

    private record ClientConnection(Long userId, TargetRole role, SseEmitter emitter, AtomicBoolean active) {
        private ClientConnection(Long userId, TargetRole role, SseEmitter emitter) {
            this(userId, role, emitter, new AtomicBoolean(true));
        }
    }
}
