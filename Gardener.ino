#include <TimeLib.h>
#include <Time.h>
#include <TimeAlarms.h>
#include <SoftwareSerial.h>
#include <Servo.h>

/*
 Name:		ArduinoGardener.ino
 Author:	Bulat Nugmanov
*/

// BOOLEAN 
const byte TRUE = 1;
const byte FALSE = 0;

// MESSAGES
const char BEGIN = '<';
const char END = '>';
const char SET = 'S';
const char GET = 'G';
const char ACT = 'A';
const char HOUR = 'H';
const char DAY = 'D';
const char WEEK = 'W';
const char LVL = 'L';
const char TIME = 'T';
const char DELIM = ']';

// PINS AND TOOLS
const byte TX = 10;      // RX in from HC06
const byte RX = 11;      // TX in from HC06
const byte SRV1 = 9;     // Water valve control servo
const int SNS1 = A1;     // Soil moisture sensor analog in pin
const byte DEBUG = 8;
Servo servo1;

// SETTINGS
const int BAUD = 9600;
const int ALARM_TMR = 60 * 60;
const int MAX_MSG_LENGTH = 16;
const byte MIN_MSG_LENGTH = 8;
const byte MAX_MST = 50;        // Maximum and minimum soil moisture levels in percent
const byte MIN_MST = 20;
const int MAX_RUN = 10 * 1000; // Max and min time to run water valve
const int MIN_RUN = 1 * 1000;
const byte TOLERANCE = 5;
const byte TO_SET = 1;
SoftwareSerial bluetooth(TX, RX);

/** SCHEDULES
*  Represents a watering schedule, where elements at i=0 to i=6
*  in schedule each point to an array, with Monday at i=0. Each
*  contained array has 24 elements, where i = hour in 0-base military time,
*  and array[i] = target moisture level in percent. 0 means no
*  hydration scheduled for the hour.
*/
byte schedules[7][24];

// STATE
byte bt_available = FALSE;
byte moistureLevel = 0;
byte lastWateredDay = 1;
byte lastWateredMonth = 1;
byte lastWateredHour = 0;
byte lastCheckedDay = 1;
byte lastCheckedMonth = 1;
byte lastCheckedHour = 0;

// SETUP
void setup() {

	// Set pins
	pinMode(TX, OUTPUT);
	pinMode(RX, INPUT);
	pinMode(SNS1, INPUT);
	pinMode(DEBUG, OUTPUT);
	servo1.attach(SRV1);

	// Set state
	readSensor();
	setTime(0, 0, 0, 1, 1, 16);

	// Initialize watering schedule to no hydrations at all
	resetSchedule();

	// Timer to check current moisture level against target,
	// repeated every ALARM_TMR seconds (60^2 by default)
	Alarm.timerRepeat(ALARM_TMR, checkSchedule);

	// Bluetooth Setup
	bt_available = FALSE;
	bluetooth.begin(BAUD);
	Serial.begin(BAUD);
}

// LOOP
void loop() {
	if (bluetooth.available()) {
		char charIn = bluetooth.read();
		if (charIn == BEGIN){
			char message[MAX_MSG_LENGTH];
			while (charIn != DELIM) {
				bluetooth.readBytesUntil(END, message, MAX_MSG_LENGTH);
				if (sizeof(message) > MIN_MSG_LENGTH){
					handleMessage(message);
				}
			}
		}
	}

	bt_available = FALSE;
	digitalWrite(DEBUG, FALSE);
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
*  returns input mapped yo percent from 0 to max moisture level,
*  and updates last checked time values.
*/
void readSensor(){
	moistureLevel = map(analogRead(SNS1), 0, 1023, 0, MAX_MST);
	updateLastChecked();
}

/**
* Checks the difference in current and target moisture levels,
* and runs the servo controlling the water valve accordingly
* if difference is larger than a predefined tolerance level
*/
void checkSchedule(){
	readSensor();
	int d = compareMoistureLevel();
	if (d > TOLERANCE){
		Alarm.delay(1000);
		runServo(servo1, valveServoRunTime(d));
	}
}

void handleSetRequest(char message[]){
	for (int i = 1; i < sizeof(message); i++) {
		int day, hr, lvl;
		if (message[i] == DAY){
			day = message[i + 1];
		}
	}
}

void handleAction(char message[]){}

/**
*  Method for handling messages received through Bluetooth from
*  Android companion app
*/
void handleMessage(char message[]){
	switch (message[0]){
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
* and target for the current hour (% - % = %)
*/
byte compareMoistureLevel(){
	int d = getWeekday();
	int h = hour();
	return moistureLevel - schedules[d][h];
}

/**
*  Returns the index for the current weekday's schedule in schedules, where Monday = 0.
*  Required because Time.weekday() returns 1 for Sunday, and so on.
*/
byte getWeekday(){
	int w = weekday();
	if (w == 1) {
		return 7;
	}
	else {
		return (w + 5) % 7;
	}
}

/**
* Turns servo to 180 degrees, waits t milliseconds, and closes it again
*/
void runServo(Servo servo, int t){
	servo.write(180);
	Alarm.delay(t);
	servo.write(0);
}

/**
* Returns how long the water valve should be fully open for in milliseconds
* to make up the given d = target - moisture level
*/
int valveServoRunTime(byte d){
	return map(d, 0, MAX_MST - MIN_MST, MIN_RUN, MAX_RUN);
}


/**
* Writes current moisture level to bluetooth as a byte
*/
void sendStatus(){
	readSensor();
	bluetooth.write(BEGIN);
	bluetooth.write(moistureLevel);
	bluetooth.write(END);
}

void updateLastChecked(){
	lastCheckedHour = hour();
	lastCheckedDay = day();
	lastCheckedMonth = month();
}

void resetSchedule(){
	for (int i = 0; i < 7; i++){
		for (int j = 0; i < 24; i++){
			schedules[i][j] = 0;
		}
	}
}


