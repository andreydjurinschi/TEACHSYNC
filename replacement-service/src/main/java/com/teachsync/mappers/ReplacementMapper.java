package com.teachsync.mappers;

import com.teachsync.domain.ReplacementRequest;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestBaseDto;

public class ReplacementMapper {

    public static ReplacementRequestBaseDto mapToBaseDto(ReplacementRequest replacementRequest){
        return new ReplacementRequestBaseDto(
                null, null, null,
                replacementRequest.getRequestedAt(), replacementRequest.getLessonDate(),
                null, replacementRequest.getReason(), replacementRequest.getStatus()
        );
    }
}
