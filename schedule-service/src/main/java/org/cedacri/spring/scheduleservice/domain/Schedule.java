package org.cedacri.spring.scheduleservice.domain;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "schedule_days_info",
            joinColumns = @JoinColumn(name = "schedule_id")
    )
    @Column(name = "weekday", nullable = false)
    private Set<WeekDays> weekDays = new HashSet<>();

    // relations
    @Column(name = "group_course_id", nullable = false)
    private Long groupCourseId;
    @Column(name = "teacher_id")
    private Long teacherId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_room_id")
    private ClassRoom classRoom;

    public Schedule(LocalTime startTime, LocalTime endTime, Set<WeekDays> weekDays, Long groupCourseId, Long teacherId, ClassRoom classRoom) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.groupCourseId = groupCourseId;
        this.teacherId = teacherId;
        this.classRoom = classRoom;
    }

    public Schedule() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Set<WeekDays> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(Set<WeekDays> weekDay) {
        this.weekDays = weekDay;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }

    public Long getGroupCourseId() {
        return groupCourseId;
    }

    public void setGroupCourseId(Long groupCourseId) {
        this.groupCourseId = groupCourseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}