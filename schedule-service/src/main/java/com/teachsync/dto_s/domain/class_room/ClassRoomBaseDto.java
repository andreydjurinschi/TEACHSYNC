package com.teachsync.dto_s.domain.class_room;

public class ClassRoomBaseDto {
    private Long id;
    private String name;
    private Integer capacity;
    private String photoUrl;

    public ClassRoomBaseDto(Long id, String name, Integer capacity) {
        this(id, name, Integer.valueOf(capacity), null);
    }

    public ClassRoomBaseDto(Long id, String name, Integer capacity, String photoUrl) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.photoUrl = photoUrl;
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

}
