package org.cedacri.spring.scheduleservice.dto_s.domain.class_room;

// TODO class room base dto fields
public class ClassRoomBaseDto {
    private Long id;
    private String name;
    private Integer capacity;

    public ClassRoomBaseDto(Long id, String name, Integer capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
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

}
