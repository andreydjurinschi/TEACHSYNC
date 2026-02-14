package com.teachsync.courseservice.dto_s.groups;

import java.time.LocalDate;

public class GroupBaseDto {
    private String name;
    private LocalDate date;
    private int capacity;

    public GroupBaseDto(String name, LocalDate date, int capacity) {
        this.name = name;
        this.date = date;
        this.capacity = capacity;
    }

    public GroupBaseDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
