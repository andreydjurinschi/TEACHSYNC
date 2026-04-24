package com.teachsync.interaction.feign.requests;

public class SpecializationsBaseDto {
    private Long id;
    private String name;

    public SpecializationsBaseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SpecializationsBaseDto() {}

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
}
