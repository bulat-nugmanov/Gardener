package com.example.Gardener.model;
import com.example.Gardener.model.exception.ExistingItemException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A singleton Schedule Manager
 */
public class ScheduleManager implements Iterable<AbstractSchedule> {

    private static ScheduleManager instance;
    private ArrayList<AbstractSchedule> schedules;
    private AbstractSchedule selected;

    private ScheduleManager() {
        this.schedules = new ArrayList<>();
        selected = null;
    }

    public static ScheduleManager getInstance(){
        if(instance == null) {
            instance = new ScheduleManager();
        }

        return instance;
    }

    public ArrayList<AbstractSchedule> getSchedules() {
        return schedules;
    }

    /**
     * Creates a new daily schedule by populating it with hydration events,
     * and adds it to schedules
     * @param name: desired name
     * @param f: frequency to determing hydration event interval for each day
     * @param lvl: target moisture level to maintain
     * @param day: desired day of the week when populating a weekly schedule
     *           leave null when creating a standalone daily schedule
     * @throws ExistingItemException if a schedule with given name already exists
     */
    public void createDailySchedule(String name, int f, int lvl, Day day) throws ExistingItemException{
        checkArguments(name, f, lvl);
        DailySchedule newSchedule = new DailySchedule(name);
        newSchedule.setDay(day);
        newSchedule.populate(f, lvl);
        addSchedule(newSchedule);
    }

    /**
     * Modifies existing daily schedule with given params
     * @param name
     * @param newFreq
     * @param newLvl
     * @param day
     */
    public void editDailyScheduleWithName(String name, int newFreq, int newLvl, Day day){
        DailySchedule scheduleToEdit = (DailySchedule) getScheduleWithName(name);
        scheduleToEdit.setName(name);
        scheduleToEdit.setDay(day);
        scheduleToEdit.clearAllHydrations();
        scheduleToEdit.populate(newFreq, newLvl);
    }

    /**
     * Used to check that no schedule with given name exists and that the entered params ar legal
     * @param name
     * @param frequency
     * @param level
     * @throws IllegalArgumentException if any of the above conditions are not met
     */
    private void checkArguments(String name, int frequency, int level)
            throws IllegalArgumentException, ExistingItemException{

        if(name == null || name.equals("schedule with name already exists")){
            throw new ExistingItemException();
        } if(frequency < 1 || frequency > 4) {
            throw new IllegalArgumentException("illegal frequency");
        } if(level < 20 || level > 50) {
            throw new IllegalArgumentException("illegal moisture level");
        }
    }

    /**
     * Adds given schedule and sorts the list
     * @param schedule
     * @throws ExistingItemException if schedule with same name as given already exists
     */
    public void addSchedule(AbstractSchedule schedule) throws ExistingItemException {
         if (hasScheduleWithName(schedule.getName())) {
            throw new ExistingItemException();
        } else {
            schedules.add(schedule);
            Collections.sort(schedules);
        }
    }

    public void removeSchedule (AbstractSchedule schedule){
        schedules.remove(schedule);
    }

    /**
     * @param name
     * @return true if a schedule with given name exists
     */
    private Boolean hasScheduleWithName(String name){
        for(AbstractSchedule next: schedules){
            if(next.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public void replaceSchedule (AbstractSchedule schedule) {
        removeSchedule(getScheduleWithName(schedule.getName()));
        schedules.add(schedule);
    }

    public AbstractSchedule getScheduleWithName(String name){
        for(AbstractSchedule s: schedules){
            if(s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }

    public Iterator<AbstractSchedule> iterator(){
        return schedules.iterator();
    }
}
