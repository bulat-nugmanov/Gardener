package com.example.Gardener.util;

import com.example.Gardener.model.*;
import java.nio.charset.StandardCharsets;

/**
 * Class used for encoding schedules into byte arrays
 * for upload to Arduino companion devices over Bluetooth.
 *
 * Before ANSII encoding into bytes, the messages are strings of
 * the following form (without spaces):
 *
 * < M X1 V1, X2 V2, ... Xn Vn,>
 *
 * The parameters are as defined in the ArduinoMessages class:
 * - M = Setting Message (always first character following Begin symbol)
 * - X = Setting target
 * - V = Setting value
 *      (1 or 2 digits for all targets except time, which requires 10:
 *      MMHHDDMMYY)
 *
 * Examples:
 * <SW,D0,H12,L50,D3,H12,L50,H16,L25> =
 * Monday   12pm - 50%
 * Thursday 12pm - 50%, 4pm - 25%
 *
 * <SD0,H8,L30,H12,L40,H16,L50> =
 * Everyday 8am - 30%, 12pm - 40%, 4pm - 50%
 *
 * <GU> = get update
 *
 * <AL40> = water soil 40% moisture level now
 */

/**
 * Schedules are encoded by implementations of toEncodedString in subclasses of Schedule
 * (which should all be defined by messages in ArduinoMessages).
 */
public class ScheduleDataParser {

    /**
     * @param schedule to encode
     * @return SET message for given schedule as BluetoothSocket ready byte array
     */
    public static byte[] setMsg(Schedule schedule){
        String str = setScheduleMsgString(schedule);
        return str.getBytes(StandardCharsets.US_ASCII);
    }

    public static String setScheduleMsgString(Schedule schedule){
    return  ArduinoMessages.BEGIN +
            ArduinoMessages.SET +
            schedule.toEncodedString() +
            ArduinoMessages.END;
    }
    }


