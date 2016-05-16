package com.example.Gardener.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represent a week of daily schedules
 */
public class WeeklySchedule extends AbstractSchedule implements Iterable {

    private Set<DailySchedule> schedules;

    public WeeklySchedule(String name) {
        super(name);
        schedules = new HashSet<>();
    }

    public void addSchedule(DailySchedule schedule){
        //if(getScheduleOnDay(schedule.getDay())!=null) {
        //    throw new ExistingItemException();
        //} else {
            schedules.add(schedule);
        //}
    }

    public DailySchedule getScheduleOnDay(Day d){
        for(DailySchedule next: schedules){
            if(next.getDay() == d){
                return next;
            }
        }
        return null;
    }

    public void removeSchedule(DailySchedule schedule){
        schedules.remove(schedule);
    }

    public void replaceSchedule(DailySchedule schedule){
        removeSchedule(getScheduleOnDay(schedule.getDay()));
        schedules.add(schedule);
    }

    public Iterator<DailySchedule> iterator(){
        return schedules.iterator();
    }

    /**
     * Returns a concatenation of contained daily schedules encoded into strings, in the format:
     * WEEKDaDbDc, where WEEK is a symbol defined in ArduinoMessages, and DaDbDc are the daily schedules
     */
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder();
        for(DailySchedule d: schedules){
            sb.append(d.toEncodedString());
        }
        return sb.toString();
    }

    @Override
    public void addItem(ScheduleItem item) {
        DailySchedule d = (DailySchedule) item;
        addSchedule(d);
    }

    @Override
    public void removeItem(ScheduleItem item) {
        DailySchedule d = (DailySchedule) item;
        removeSchedule(d);
    }

    @Override
    public void replaceItem(ScheduleItem item) {
        DailySchedule d = (DailySchedule) item;
        replaceSchedule(d);
    }
}
