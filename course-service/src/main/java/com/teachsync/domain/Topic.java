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

    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private TopicTag tag;

    public Topic(String name) {
        this.name = name;
    }

    public Topic() {

    }

    // relations

    @ManyToMany(mappedBy = "topics")
    private Set<Course> courses;

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public TopicTag getTag() {
        return tag;
    }

    public void setTag(TopicTag tag) {
        this.tag = tag;
    }

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
