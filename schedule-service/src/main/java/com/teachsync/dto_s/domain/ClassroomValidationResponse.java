package com.teachsync.dto_s.domain;

public record ClassroomValidationResponse(boolean fits, int roomCapacity, int groupSize) {
}
