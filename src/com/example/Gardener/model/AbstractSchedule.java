package com.example.Gardener.model;

import java.util.Set;

/**
 * Represents an abstract named schedule of events
 */
public abstract class AbstractSchedule implements Comparable<Schedule>, Schedule {

    protected String name;
    protected Set<ScheduleItem> items;

    public AbstractSchedule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Abstract schedules are ordered alphabetically
     */
    public int compareTo(Schedule as){
        return name.compareTo(as.getName());
    }

    /**
     * Schedules are equal if they have the same name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSchedule that = (AbstractSchedule) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
