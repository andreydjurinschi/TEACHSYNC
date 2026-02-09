package org.cedacri.spring.scheduleservice.dto_s.domain.class_room;

import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleBaseDto;

import java.util.Set;

public class ClassRoomDetailedDto {
    private Long id;
    private String name;
    private Integer capacity;
    private String photoUrl;
    private Set<ScheduleBaseDto> schedules;

    public ClassRoomDetailedDto(Long id, String name, Integer capacity, String photoUrl, Set<ScheduleBaseDto> schedules) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.photoUrl = photoUrl;
        this.schedules = schedules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Set<ScheduleBaseDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<ScheduleBaseDto> schedules) {
        this.schedules = schedules;
    }
}
