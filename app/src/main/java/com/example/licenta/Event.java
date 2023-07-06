package com.example.licenta;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class Event implements Parcelable {
    public static ArrayList<Event> eventsList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if(event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }
    public static ArrayList<Event> eventsForWeek(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate stopDate=startDate.plusWeeks(1);

        for(Event event : eventsList)
        {
            if (event.getDate().isBefore(stopDate)&&!event.getDate().isBefore(startDate)){
                events.add(event);
                Log.d("Event.eventsForWeek",event.getDate().toString());
            }
        }

        return events;
    }

    private String name;
    private LocalDate date;
    private LocalTime timeStart,timeFinal;
    private StatusEv status;
    private Categories category;

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalTime getTimeFinal() {
        return timeFinal;
    }

    public void setTimeFinal(LocalTime timeFinal) {
        this.timeFinal = timeFinal;
    }

    public Event(String name, LocalDate date, LocalTime timeStart,LocalTime timeFinal,StatusEv status,Categories category)
    {
        this.name = name;
        this.date = date;
        this.timeStart = timeStart;
        this.timeFinal = timeFinal;
        this.status = status;
        this.category=category;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public StatusEv getStatus() {
        return status;
    }

    public void setStatus(StatusEv status) {
        this.status = status;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", timeStart=" + timeStart +
                ", timeFinal=" + timeFinal +
                ", status=" + status +
                ", category=" + category +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(date.toString());
        dest.writeString(timeStart.toString());
        dest.writeString(timeFinal.toString());
        dest.writeString(status.toString());
        dest.writeString(category.name());
    }
}
