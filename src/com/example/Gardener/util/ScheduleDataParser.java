package com.example.Gardener.util;

import com.example.Gardener.model.*;
import java.nio.charset.StandardCharsets;

/**
 * Class used for encoding schedules into byte arrays
 * for upload to Arduino companion devices over Bluetooth.
 */

/**
 * Schedules are encoded by implementations of toEncodedString in subclasses of Schedule
 * (which should all be defined by messages in ArduinoMessages).
 */
public class ScheduleDataParser {

    public static byte[] setScheduleMsg(Schedule schedule){
        String str = ArduinoMessages.BEGIN +
                ArduinoMessages.SET +
                schedule.toEncodedString() +
                ArduinoMessages.END;
        return str.getBytes(StandardCharsets.US_ASCII);
    }

    }


