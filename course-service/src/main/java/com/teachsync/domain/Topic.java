package com.teachsync.domain;

import jakarta.persistence.*;

import java.util.Set;

@Table(name = "topics")
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Topic(String name) {
        this.name = name;
    }

    public Topic() {

    }

    // relations

    @ManyToMany(mappedBy = "topics")
    private Set<Course> courses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
