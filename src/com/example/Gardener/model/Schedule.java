package com.example.Gardener.model;

/**
 * Represents a schedule of schedule items
 */
public interface Schedule {

    String getName();

    void setName(String name);

    String toEncodedString();

    void addItem(ScheduleItem item);

    void removeItem(ScheduleItem item);

    /**
     * Replace item at the same time as the one given, with the one given
     */
    void replaceItem(ScheduleItem item);
}
