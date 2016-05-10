package com.example.Gardener.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a schedule of hydrations for a given day
 */
public class DailySchedule extends AbstractSchedule implements Iterable {

    private Day day;
    private Set<Hydration> hydrations;

    private final static int DEF_FIRST_HYDRATION_HOUR = 8;

    public DailySchedule(String name) {
        super(name);
        day = null;
        hydrations = new HashSet<>();
    }

    /**
     * @return unsorted, unmodifiable set of scheduled hydration events
     */
    public Set<Hydration> getHydrations() {
        return Collections.unmodifiableSet(hydrations);
    }

    public Day getDay() {
        return day;
    }

    public int getDayNum() {
        if(day==null) {
            return 0;
        }
        return day.getDayNum();
    }

    public void setDay(Day day) {
        this.day = day;
    }

    /**
     * @param h: hour
     * @return a hydration object at hour = h
     */
    public Hydration getHydrationAtHour(int h){
        for(Hydration next: hydrations){
            if(next.getHour() == h) {
                return next;
            }
        }
        return null;
    }

    /**
     * Adds a new hydration to this schedule at given hour if none are scheduled,
     * otherwise replaces existing hydration with given
     * @param h: hydration event
     */
    public void addHydration(Hydration h) {
        if(hydrations.contains(h)) {
            replaceHydration(h);
        } else {
            hydrations.add(h);
        }
    }

    public void removeHydration(Hydration hydration){
        hydrations.remove(hydration);
    }

    /**
     * Replaces hydration at the same hour as the one given, with the one given
     * @param hydration
     */
    public void replaceHydration(Hydration hydration){
        Hydration h = getHydrationAtHour(hydration.getHour());
        if(h != null) {
            hydrations.remove(getHydrationAtHour(hydration.getHour()));
            hydrations.add(hydration);
        }
    }

    /**
     * populates this schedule with hydration events, each with the same
     * target moisture level, at hourly intervals defined by chosen frequency,
     * starting with a predefined hour (default is 8am)
     * @param f: frequency
     * @param lvl: desired target moisture level for all hydrations
     */
    public void populate(int f, int lvl){
        int hourlyInterval = (24 - DEF_FIRST_HYDRATION_HOUR) / f;
        int hour = DEF_FIRST_HYDRATION_HOUR;
        for (int i = f; i > 0; i--) {
            Hydration h = new Hydration(hour, lvl);
            hydrations.add(h);
            hour += hourlyInterval;
        }
    }

    public void clearAllHydrations(){
        hydrations.clear();
    }

    public int getNumHydrations(){
        return hydrations.size();
    }

    @Override
    public Iterator<Hydration> iterator() {
        return hydrations.iterator();
    }

    @Override
    public void addItem(ScheduleItem item) {
        addHydration((Hydration) item);
    }

    @Override
    public void removeItem(ScheduleItem item) {
        removeHydration((Hydration) item);
    }

    @Override
    public void replaceItem(ScheduleItem item) {
        removeHydration((Hydration) item);
    }

    public String toEncodedString() {
        String tag = "D" + getDayNum();
        StringBuilder sb = new StringBuilder(tag);
        for(Hydration h: hydrations){
            sb.append(h.toEncodedString());
        }
        return sb.toString();
    }
}
