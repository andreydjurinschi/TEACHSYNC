package com.teachsync.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "schedule_days_info")
public class ScheduleDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private WeekDays weekday;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Schedule schedule;

    public ScheduleDay(WeekDays weekday, Schedule schedule) {
        this.weekday = weekday;
        this.schedule = schedule;
    }

    public ScheduleDay() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WeekDays getWeekday() {
        return weekday;
    }

    public void setWeekday(WeekDays weekday) {
        this.weekday = weekday;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
