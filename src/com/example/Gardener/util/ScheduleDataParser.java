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
 * For example:
 * <SW,D0,H12,L50,D3,H12,L50
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


