package org.cedacri.spring.scheduleservice.dto_s.domain.class_room;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClassRoomCreateUpdateDto {
    @NotBlank
    @Size(min=2, max=50)
    private String name;
    @NotNull
    @Min(value = 5, message = "min classroom capacity size needs to be 5")
    @Max(value = 80, message = "max classroom capacity size needs to be 80")
    private Integer capacity;
    private String photoUrl;

    public ClassRoomCreateUpdateDto(String name, Integer capacity, String photoUrl) {
        this.name = name;
        this.capacity = capacity;
        this.photoUrl = photoUrl;
    }

    public ClassRoomCreateUpdateDto() {
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
