package com.example.Gardener.model;

import com.example.Gardener.model.exception.ExistingItemException;
import com.example.Gardener.model.exception.IllegalScheduleException;

import java.util.Collections;
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
        schedules = new HashSet();
    }

    public void addDailySchedule(DailySchedule schedule) throws ExistingItemException, IllegalScheduleException {
        if(schedules.contains(schedule)) {
            throw new ExistingItemException();
        } else if (schedule.getName() == null){
            throw new IllegalScheduleException();
        } else {
            schedules.add(schedule);
        }
    }

    public DailySchedule getScheduleOnDay(Day d){
        for(DailySchedule next: schedules){
            if(next.getDay() == d){
                return next;
            }
        }
        return null;
    }

    public void removeDailySchedule(DailySchedule schedule){
        schedules.remove(schedule);
    }

    public void replaceDailySchedule(DailySchedule schedule){
        removeDailySchedule(getScheduleOnDay(schedule.getDay()));
        schedules.add(schedule);
    }

    public Iterator<DailySchedule> iterator(){
        return schedules.iterator();
    }

    @Override
    public void addItem(ScheduleItem item) {

    }

    @Override
    public void removeItem(ScheduleItem item) {

    }

    @Override
    public void replaceItem(ScheduleItem item) {

    }

    public String toEncodedString() {
        StringBuilder sb = new StringBuilder("W");
        for(DailySchedule d: schedules){
            sb.append(d.toEncodedString());
        }
        return sb.toString();
    }
}
