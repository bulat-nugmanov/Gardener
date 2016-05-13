package com.example.Gardener.model;

import com.example.Gardener.util.ArduinoMessages;

/**
 * Represents a single hydration event, with hour and target moisture level.
 * Two hydrations are considered equal if they are scheduled for the same hour
 *
 */
public class Hydration implements Comparable<Hydration>, ScheduleItem {

    private int hour;
    private int moistureLevel;

    public Hydration(int hour, int moistureLevel) throws IllegalArgumentException {
        if(hour < 0 || hour > 23) {
            throw new IllegalArgumentException();
        } else {
            this.hour = hour;
            //this.minute = 0;
            this.setMoistureLevel(moistureLevel);
        }
    }

    public int getHour() {
        return hour;
    }

    /**
     * Effects: sets hour to given value if legal,
     * otherwise throws Illegal Argument Exception
     */
    public void setHour(int hour) throws IllegalArgumentException {
        if(hour <= 23 && hour >= 0) {
            this.hour = hour;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getMoistureLevel() {
        return moistureLevel;
    }

    /**
     * Effects: sets moistureLevel to between 0 and 100 inclusive,
     * throws an IllegalArgumentException if the given moistureLevel is outside that domain
     */
    public void setMoistureLevel(int moistureLevel) {
        if (moistureLevel < 0 || moistureLevel > 100) {
            throw new IllegalArgumentException();
        } else {
            this.moistureLevel = moistureLevel;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hydration hydration = (Hydration) o;

        return hour == hydration.hour;

    }

    @Override
    public int hashCode() {
        return hour;
    }

    @Override
    public int compareTo(Hydration h) {
        return hour - h.getHour();
    }

    @Override
    public int getTime() {
        return hour;
    }

    /**
     * Encodes a hydration into a String in the following format:
     * HOURhLVLl, where HOUR and LVL are characters defined in ArduinoMessages,
     * h is the scheduled hour, and l the target moisture level in percent.
     */
    public String toEncodedString() {
        return ArduinoMessages.HOUR + hour + ArduinoMessages.LVL + moistureLevel + ArduinoMessages.DELIM;
    }
}
