String lastEdited = "3.01.15   8:33pm";
String ver = "2.1";


//elevator encoder positions
//absolute encoder ticks for each elevator position
#define elevatorMin 0
#define elevatorMax 13881
#define elevatorPosition0 0
#define elevatorPosition1 3095
#define elevatorPosition2 6245
#define elevatorPosition3 9430
#define elevatorPosition4 12580


 

// define digital button pins
// these buttons get sent directly to the driver station over the HID interface
#define stackPin 34
#define containerPin 30
#define tiltPin 28
#define resetEncPin 36
#define nextAutoPin 22

// define elevator control button pins
// these buttons get processed by the Arduino, the slide pot moves, then that value
// gets sent to the driver station
#define stackUpPin 24
#define stackDownPin 26
#define elevator0ButtonPin 33
#define elevator1ButtonPin 35
#define elevator2ButtonPin 37
#define elevator3ButtonPin 39
#define elevator4ButtonPin 41

// Define Joystick Analog Pins
int thumbYPin = 15;

//slide pot constants
#define slidePotMin 0
#define slidePotMax 1023

// slide pot pins
#define analogInPin A0
#define motorUpPin 2
#define motorDownPin 3
#define capacitiveInPin 
#define capacitiveOutPin
