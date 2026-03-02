package com.example.quanlynhahang.model;

public class ServiceRequest {

    public enum Status {
        PROCESSING,
        DONE
    }

    private final String content;
    private final String sentTime;
    private Status status;

    public ServiceRequest(String content, String sentTime, Status status) {
        this.content = content;
        this.sentTime = sentTime;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public String getSentTime() {
        return sentTime;
    }

    public Status getStatus() {
        return status;
    }

    public void markDone() {
        status = Status.DONE;
    }
}
