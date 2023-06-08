package com.example.licenta;

import java.util.ArrayList;

public class Reminder {
    public static ArrayList<Reminder> reminders=new ArrayList<>();
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

    public Reminder(String message, long dateTime) {
        this.message = message;
        this.dateTime = dateTime;
    }
}
