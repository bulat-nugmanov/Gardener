#include <TimeAlarms.h>
#include <SoftwareSerial.h>
#include <Servo.h>

// BOOLEAN 
const int TRUE = 1;
const int FALSE = 0;

// MESSAGES
const char BEGIN = '<';
const char END = '>';
const char SET = 'S';
const char GET = 'G';
const char ACT = 'A';
const char HOUR  = 'H';
const char DAY = 'D';
const char WEEK = 'W';
const char LVL = 'L';
const char TIME = 'T';

// PINS AND TOOLS
const int TX = 10;      // RX in from HC06
const int RX = 11;      // TX in from HC06
const int SRV1 = 8;     // Water valve control servo
const int SNS1 = 9;     // Soil moisture sensor analog in pin
Servo servo1;

// SETTINGS
const int BAUD = 9600;
const int ALARM_TMR = 60 * 60;
const int MAX_MSG_LENGTH = 1024;
const int MIN_MSG_LENGTH = 8;
const int MAX_MST = 50;        // Maximum and minimum soil moisture levels in percent
const int MIN_MST = 20;
const int MAX_RUN = 10 * 1000; // Lengths of time to run water valve in milliseconds corresponding to
const int MIN_RUN = 1 * 1000;  // max and min difference between current and target moisture level
const int TOLERANCE = 5;
const int TO_SET = 1;
SoftwareSerial bluetooth(TX,RX);


/** SCHEDULES
 *  Represents a watering schedule, where elements at i=0 to i=6
 *  in schedule each point to an array, with Monday at i=0. Each
 *  contained array has 24 elements, where i = hour in 0-base military time, 
 *  and array[i] = target moisture level in percent. 0 means no
 *  hydration scheduled for the hour.
 */
int schedules[7][24];

// STATE
int bt_available = FALSE;
int moistureLevel;
int onDailySchedule = FALSE;          // TRUE if hydration schedule is same for every day,
                              // in which case only schedules[0] should ever be accessed
// SETUP
void setup() {

  // Set pins
  pinMode(TX, OUTPUT);
  pinMode(RX, INPUT);
  pinMode(SNS1, INPUT);
  servo1.attach(SRV1);

  // Set state
  readMoistureLevelFromSensor();
  setTime(0, 0, 0, 1, 1, 16);

  // Initialize watering schedule to no hydrations at all
  for(int i = 0; i < 7; i++){
    for(int j = 0; i < 24; i++){
      schedules[i][j] = 0;
    }
  }
  onDailySchedule = TRUE;
  
  // Timer to check current moisture level against target,
  // repeated every ALARM_TMR seconds (60^2 by default)
  Alarm.timerRepeat(ALARM_TMR, checkSchedule);

  // Bluetooth Setup
  bt_available = FALSE;
  bluetooth.begin(BAUD);
}

// LOOP
void loop() {

  if(bluetooth.available() > 0){
    bt_available = TRUE;
    
    //Serial.println("Bluetooth Serial available");
    //digitalWrite(DEBUG, bt_available);

    // Wait to recieve the BEGIN symbol from the bluetooth serial,
    // record until the END symbol and handle the message in between
    char charIn = bluetooth.read();
    if(charIn == BEGIN){
      char message[MAX_MSG_LENGTH];
      bluetooth.readBytesUntil(END, message, MAX_MSG_LENGTH);
      if(sizeof(message)>MIN_MSG_LENGTH){
        handleMessage(message);
      }
    }
  }

  bt_available = FALSE;
}

// METHODS
/**
 * Sets current time as defined by the input char array, which should be
 * in the form MMHHDDMMYY.
 */
void setTimeFromMsg(char message[]){
  
  }

/** 
 *  Reads analog input from resistance-based soil moisture level sensor,
 *  and maps the input into percent from 0 to max moisture level defined for model.
 */
void readMoistureLevelFromSensor(){
  moistureLevel = map(analogRead(SNS1), 0, 1023, 0, MAX_MST);
}

/**
 * Checks the difference in current and target moisture levels,
 * and runs the servo controlling the water valve accordingly
 * if difference is larger than a predefined tolerance level
 */
void checkSchedule(){
  readMoistureLevelFromSensor();
  int d = checkMoistureLevel();
  if (d > TOLERANCE){
    runServo(servo1, valveServoRunTime(d));
  }
  }

void handleSetRequest(char message[]){}

void handleAction(char message[]){}

/** 
 *  Method for handling messages received through Bluetooth from
 *  Android companion app
 */
void handleMessage(char message[]){
  switch(message[0]){    
    
    case SET:
      handleSetRequest(message);
      break;

    case GET:
      sendStatus();
      break;
      
    case ACT:
      handleAction(message);
      break;
  }
}

/**
 * Returns the difference between the last recorded moisture level
 * and target for the current hour
 */
int checkMoistureLevel(){
  int i = weekdayIndexInScheduleArray();
  int j = hour();
  return moistureLevel - schedules[i][j];
}

/** 
 *  Returns the index for the current day schedule in schedules, where Monday = 0.
 */
int weekdayIndexInScheduleArray(){
  int w = weekday();
  if(w == 1) {
    return 7;
  } else {
    return (w + 5) % 7;
  }
}

/**
 * Turns servo to 180 degrees, waits t milliseconds, and closes it again
 */
void runServo(Servo servo, int t){
  servo.write(180);
  delay(t);
  servo.write(0);
  }

/**
 * Returns how long the water valve should be fully open for in milliseconds
 * to make up the given d = target - moisture level 
 */
int valveServoRunTime(int d){
  return map(d, 0, MAX_MST - MIN_MST, MIN_RUN, MAX_RUN);
  }

void calibrate(){}

int isOptimal(int level){}

void sendStatus(){
      checkMoistureLevel();
      byte lvl = byte(moistureLevel);
      bluetooth.write(BEGIN);
      bluetooth.write(lvl);
      bluetooth.write(END);
}



