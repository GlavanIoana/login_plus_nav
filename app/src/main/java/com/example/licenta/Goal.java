package com.example.licenta;

import java.util.ArrayList;
import java.util.List;

public class Goal {
    public static List<Goal> goalsList= new ArrayList<>();
    private String name;
    private Categories category;
    private long duration;//in minutes
    private int frequency;
    private String typeFrequency;
    private String intervalPref;
    private List<Event> events;

    public Goal(String name, Categories category, long duration, int frequency, String typeFrequency,String intervalPref) {
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.frequency = frequency;
        this.typeFrequency = typeFrequency;
        this.intervalPref=intervalPref;
        events=new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getTypeFrequency() {
        return typeFrequency;
    }

    public void setTypeFrequency(String typeFrequency) {
        this.typeFrequency = typeFrequency;
    }

    public List<Event> getEvents() {
        return events;
    }

    public String getIntervalPref() {
        return intervalPref;
    }

    public void setIntervalPref(String intervalPref) {
        this.intervalPref = intervalPref;
    }


}
