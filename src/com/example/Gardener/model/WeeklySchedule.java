package com.example.Gardener.model;

import com.example.Gardener.model.exception.ExistingItemException;
import com.example.Gardener.model.exception.IllegalScheduleException;
import com.example.Gardener.util.ArduinoMessages;

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
        schedules = new HashSet<>();
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

    /**
     * Returns a concatenation of contained daily schedules encoded into strings, in the format:
     * WEEKDaDbDc, where WEEK is a symbol defined in ArduinoMessages, and DaDbDc are the daily schedules
     */
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder(ArduinoMessages.WEEK);
        for(DailySchedule d: schedules){
            sb.append(d.toEncodedString());
        }
        sb.append(ArduinoMessages.DELIM);
        return sb.toString();
    }
}
