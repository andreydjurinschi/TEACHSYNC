package org.cedacri.spring.scheduleservice.validator;

import org.cedacri.spring.scheduleservice.exceptions.InvalidTimeRangeException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class CustomTimeValidator {


    private static final Duration SHORTENED_LESSON = Duration.ofMinutes(45);
    private static final Duration STANDARD_LESSON  = Duration.ofMinutes(90);

    public void checkTime(LocalTime start, LocalTime end) throws InvalidTimeRangeException {
        checkStartTimeRange(start, end);
        checkScheduleCourseDuration(start, end);
    }

    private void checkStartTimeRange(LocalTime start, LocalTime end) throws InvalidTimeRangeException {
        if(start.isAfter(end)){
            throw new InvalidTimeRangeException("start time cannot be after end time");
        }
    }

    private void checkScheduleCourseDuration(LocalTime start, LocalTime end) throws InvalidTimeRangeException {
        Duration duration = Duration.between(start, end);
        if(duration.equals(SHORTENED_LESSON) || duration.equals(STANDARD_LESSON)){
            throw new InvalidTimeRangeException("invalid lesson duration");
        }
    }
}
