# Gardener
Imaginatively named, Gardener is a duet of two components: 1) a sensor-enabled automated watering system built on the Arduino Uno board, and 2) a companion Android app used to create custom watering schedules, and communicate them along with other information with the device over Bluetooth.

Note: As of 7:41 am May 13th there are three remaining issues, which I expect to close in the coming week.

# Project Structure: Android


# Project Structure: Arduino
About the device:
The physical component is currently equipped with a servo used to control the water supply valve (preferably replaced with a solenoid valve or pump for finer control), and an affordable resistance-based sensor for keeping track of potted soil moisture. Other and better sensors can be easily added with minimal additions to the source code on both ends.

List of parts:
- Arduino Uno board
- HC-06 Bluetooth slave module
- Moisture sensor
- Servo/Solenoid Valve/Pump
- Relays if needed
- Resistors (220, 1K, 2K)
- LED
- 9-12V Power source (adapter, battery, etc)
- Tubing, water vessel, enough tape to keep it together.

Circuit diagram:
// TO UPLOAD

The sketch:
I developed this component of Gardener for the Arduino Uno (based on the ATmega328P from Atmel), but with some optimization I expect the sketch to work on smaller microcontrollers as well. The sketch itself, Gardener.ino, is located in the Arduino folder.

Schedules are represented as a two-dimensional byte array with 24 columns and 7 rows. At every hour, an Alarm checks the target soil moisture level, with the weekday and hour at that time being the row and column 0-based indices. The sketch then reads from the sensor, compares the read and target levels, and runs the appropriate amount of water to make up the difference.

