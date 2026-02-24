package com.teachsync.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "replacement_responses")
public class ReplacementResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ReplacementRequest replacementRequest;
    @Enumerated(EnumType.STRING)
    private ResponseStatus responseStatus;
    private Long teacherResponse;

    public ReplacementResponse(ReplacementRequest replacementRequest, ResponseStatus responseStatus, Long teacherResponse) {
        this.replacementRequest = replacementRequest;
        this.responseStatus = responseStatus;
        this.teacherResponse = teacherResponse;
    }

    public ReplacementResponse() {

    }

    public ReplacementRequest getReplacementRequest() {
        return replacementRequest;
    }

    public void setReplacementRequest(ReplacementRequest replacementRequest) {
        this.replacementRequest = replacementRequest;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Long getTeacherResponse() {
        return teacherResponse;
    }

    public void setTeacherResponse(Long teacherResponse) {
        this.teacherResponse = teacherResponse;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
