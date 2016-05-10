package com.example.Gardener.util;

import com.example.Gardener.model.*;
import java.nio.charset.StandardCharsets;

/**
 * Class used for encoding schedules into byte arrays
 * for upload to Arduino companion devices over Bluetooth;
 * Resulting byte should at worst be 956 bytes long
 */
public class ScheduleDataParser {

    private final static String BEGIN = "<";
    private final static String END = ">";
    private final static String HOUR = "H";
    private final static String LVL = "A";
    private final static String DAY = "D";
    private final static String WEEK = "W";

    public static byte[] scheduleToBytes(Schedule schedule){
        String result = BEGIN + schedule.toEncodedString() + END;
        return result.getBytes(StandardCharsets.US_ASCII);
    }
}

