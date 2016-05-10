package com.example.Gardener.model;

import java.util.Set;

/**
 * Represents a schedule of schedule items
 */
public interface Schedule {

    String getName();

    void setName(String name);

    void addItem(ScheduleItem item);

    void removeItem(ScheduleItem item);

    void replaceItem(ScheduleItem item);

    String toEncodedString();
}
