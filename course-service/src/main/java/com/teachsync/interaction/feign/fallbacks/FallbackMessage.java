package com.teachsync.interaction.feign.fallbacks;

import org.springframework.http.HttpStatus;

public record FallbackMessage(HttpStatus status, String message, String fallCase, String serviceFrom,
                              String serviceTo) {
}
