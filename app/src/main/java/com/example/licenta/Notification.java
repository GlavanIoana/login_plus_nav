package com.example.licenta;

import java.util.ArrayList;

public class Notification {
    public static ArrayList<Notification> reminders=new ArrayList<>();
    private String message;
    private long dateTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "message='" + message + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public Notification(String message, long dateTime) {
        this.message = message;
        this.dateTime = dateTime;
    }
}
