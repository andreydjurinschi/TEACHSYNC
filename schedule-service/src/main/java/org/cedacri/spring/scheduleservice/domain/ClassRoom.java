package org.cedacri.spring.scheduleservice.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "class_rooms")
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Integer capacity;
    @Column(name = "photo_url")
    private String photoUrl;
    // relations
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classRoom")
    private Set<Schedule> schedules = new HashSet<>();


    public ClassRoom(String name, Integer capacity, String photoUrl) {
        this.name = name;
        this.capacity = capacity;
        this.photoUrl = photoUrl;
    }

    public ClassRoom() {

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

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
