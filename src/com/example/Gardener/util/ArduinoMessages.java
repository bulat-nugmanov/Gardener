package com.example.Gardener.util;

/**
 * A class for storing string constants used in sending encoded
 * setting data to the companion Arduino device.
 **/

public class ArduinoMessages {

    // Setting messages
    public final static String SET = "S";
    public final static String GET = "G";
    public final static String ACT = "A";

    // Setting targets
    public final static String CALIBRATE = "C";
    public final static String UPDATE = "U";
    public final static String TIME = "T";
    public final static String HOUR = "H";
    public final static String LVL = "L";
    public final static String DAY = "D";
    public final static String WEEK = "W";

    // Delimiters
    public final static String BEGIN = "<";
    public final static String END = ">";
    public final static String DELIM = "]";

}
