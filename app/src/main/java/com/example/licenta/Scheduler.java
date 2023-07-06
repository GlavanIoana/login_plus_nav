package com.example.licenta;

import android.content.Context;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    public static final int numWeeksToScheduleEventsAhead=3;

    public static boolean checkForOverlaps(Event eventFromPopup) {
        List<Event> dayEvents = getEventsForDate(eventFromPopup.getDate());

        if (isStartingHourNotOverlapping(eventFromPopup,dayEvents) && isFinishingHourNotOverlapping(eventFromPopup,dayEvents)){
            return !isEventScheduledBetween(eventFromPopup, dayEvents);
        }
        return false;
    }

    private static boolean isEventScheduledBetween(Event eventFromPopup, List<Event> dayEvents) {
        for (Event event : dayEvents) {
            if (event.getTimeStart().isAfter(eventFromPopup.getTimeStart()) &&
                    event.getTimeFinal().isBefore(eventFromPopup.getTimeFinal())) {
                return true; // Event scheduled between the starting and finishing hours of eventFromPopup
            }
        }
        return false; // No event scheduled between the starting and finishing hours of eventFromPopup
    }

    private static boolean isFinishingHourNotOverlapping(Event eventFromPopup, List<Event> dayEvents) {
        Event firstEventAfterPopup = null;

        for (Event event : dayEvents) {
            if (event.getTimeStart()
                    .isAfter(eventFromPopup.getTimeStart())) {
                firstEventAfterPopup = event;
                break;
            }
        }

        if (firstEventAfterPopup != null ){
            return !firstEventAfterPopup.getTimeStart()
                    .isBefore(eventFromPopup.getTimeFinal());
        }
        return true;
    }

    private static boolean isStartingHourNotOverlapping(Event eventFromPopup, List<Event> dayEvents) {
        Event lastEventBeforePopup = null;

        for (Event event : dayEvents) {
            if (event.getTimeStart()
                    .isBefore(eventFromPopup.getTimeStart())) {
                lastEventBeforePopup = event;
            } else {
                break;
            }
        }

        if (lastEventBeforePopup != null ){
            return !lastEventBeforePopup.getTimeFinal()
                    .isAfter(eventFromPopup.getTimeStart());
        }
        return true;
    }

    public static Event scheduleEvent(String eventName, LocalDate dataEv, Categories strCategory,long duration,LocalTime intervalStart, LocalTime intervalEnd) {
//        LocalTime startTime = LocalTime.of(8, 0); // Starting time: 8am

        List<Event> eventsForDate = getEventsForDate(dataEv);
        // Check if there is at least one hour available before the first event
        if (eventsForDate.isEmpty() || intervalStart.plusMinutes(duration).isBefore(eventsForDate.get(0).getTimeStart())) {
            // Schedule the event using the specified start and end times
            return new Event(eventName, dataEv, intervalStart, intervalStart.plusMinutes(duration), StatusEv.NEINCEPUT, strCategory);
        }

        // Iterate over the events to find a free hour between the events
        for (int i = 0; i < eventsForDate.size() - 1; i++) {
            Event currentEvent = eventsForDate.get(i);
            Event nextEvent = eventsForDate.get(i + 1);

            if (currentEvent.getTimeStart().isBefore(intervalStart)){
                if (currentEvent.getTimeFinal().isAfter(intervalEnd)){
                    break;
                }else if (currentEvent.getTimeFinal().isAfter(intervalStart)){
                    if (currentEvent.getTimeFinal().plusMinutes(duration).isBefore(nextEvent.getTimeStart())){
                        return new Event(eventName, dataEv, currentEvent.getTimeFinal(), currentEvent.getTimeFinal().plusMinutes(duration), StatusEv.NEINCEPUT, strCategory);
                    }
                }else {
                    if (nextEvent.getTimeStart().isAfter(intervalStart)) {
                        if (intervalStart.plusMinutes(duration).isBefore(nextEvent.getTimeStart())){
                            return new Event(eventName, dataEv, intervalStart, intervalStart.plusMinutes(duration), StatusEv.NEINCEPUT, strCategory);
                        }
                    }
                }
            }else if (intervalStart.plusMinutes(duration).isBefore(currentEvent.getTimeStart())){
                return new Event(eventName, dataEv, intervalStart, intervalStart.plusMinutes(duration), StatusEv.NEINCEPUT, strCategory);
            }
        }
        // Check if there is at least one hour available after the last event
        Event lastEvent = eventsForDate.get(eventsForDate.size() - 1);
        LocalTime lastEventEndTime = lastEvent.getTimeFinal();
        if (lastEvent.getTimeFinal().isBefore(intervalStart)){
            return new Event(eventName, dataEv, intervalStart, intervalStart.plusMinutes(duration), StatusEv.NEINCEPUT, strCategory);
        }else if (lastEventEndTime.isBefore(intervalEnd.minusMinutes(duration-1))) {
            // Schedule the event using the specified start and end times
            return new Event(eventName, dataEv, lastEventEndTime, lastEventEndTime.plusMinutes(duration), StatusEv.NEINCEPUT, strCategory);
        }
        System.out.println("Nu se poate programa in intervalul "+intervalStart+"-"+
                        intervalEnd+" pe data de " + dataEv);
//        Toast.makeText(context, "Ziua este plina! Nu se poate programa pe data de " + dataEv, Toast.LENGTH_SHORT).show();
        return null;
    }

    private static List<Event> getEventsForDate(LocalDate dataEv) {
        List<Event> eventsForDate=Event.eventsForDate(dataEv);

        eventsForDate.sort((event1, event2) -> {
            LocalTime startTime1 = event1.getTimeStart();
            LocalTime startTime2 = event2.getTimeStart();
            return startTime1.compareTo(startTime2);
        });
        return eventsForDate;
    }

    public static List<Event> scheduleEventsForGoal(Context context,Goal goal,LocalTime intervalStart,LocalTime intervalEnd) {
//        LocalDateTime startTime,endTime;
//        if (LocalDate.now().getDayOfWeek()!=DayOfWeek.SUNDAY){
//            startTime = LocalDateTime.now();
//            endTime = startTime.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
//        }else {
//            startTime = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)); // Start from next Monday
//            endTime = startTime.plusWeeks(1);
//        }
        LocalDateTime startTime = LocalDateTime.now().minusDays(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY)); // Start from next Monday
        LocalDateTime endTime = startTime.plusWeeks(1);
        List<Event> availableIntervals = new ArrayList<>();

        for (int i = 0; i < numWeeksToScheduleEventsAhead; i++) {
            List<Event> weekIntervals = findAvailableIntervals(startTime, endTime, goal,intervalStart,intervalEnd);
            availableIntervals.addAll(weekIntervals);
            startTime = startTime.plusWeeks(1);
            endTime = endTime.plusWeeks(1);
        }

        return availableIntervals;
    }

    private static List<Event> findAvailableIntervals(LocalDateTime startTime, LocalDateTime endTime, Goal goal,LocalTime intervalStart,LocalTime intervalEnd) {
        List<Event> availableIntervals = new ArrayList<>();
        int numOfIntervals = 0;

        while (startTime.isBefore(endTime) && numOfIntervals < goal.getFrequency()) {
            LocalDate currentDate = startTime.toLocalDate();

            Event event = scheduleEvent(goal.getName(), currentDate, goal.getCategory(), goal.getDuration(),intervalStart,intervalEnd);

            if (event != null) {
                availableIntervals.add(event);
                numOfIntervals++;
            }

            startTime = startTime.plusDays(1);
        }

        return availableIntervals;
    }

}
