package org.cedacri.spring.scheduleservice.exceptions;

public class InvalidTimeRangeException extends Exception {
    public InvalidTimeRangeException(String message){
        super(message);
    }
}
