String lastEdited = "3.01.15   8:33pm";
String ver = "2.1";


//elevator encoder positions
//absolute encoder ticks for each elevator position
#define elevatorMin 0
#define elevatorMax 1300
#define elevatorPosition0 0
#define elevatorPosition1 3095
#define elevatorPosition2 6245
#define elevatorPosition3 9430
#define elevatorPosition4 12580


 

// define digital button pins
// these buttons get sent directly to the driver station over the HID interface
#define stackPin 28
#define containerPin 30
#define tiltPin 34
#define resetEncPin 36
#define nextAutoPin 22

// define elevator control button pins
// these buttons get processed by the Arduino, the slide pot moves, then that value
// gets sent to the driver station
#define stackUpPin 24
#define stackDownPin 26
#define elevator0ButtonPin
#define elevator1ButtonPin
#define elevator2ButtonPin
#define elevator3ButtonPin
#define elevator4ButtonPin

// Define Joystick Analog Pins
#define thumbYPin 15

//slide pot constants
#define slidePotMin 0
#define slidePotMax 1000

// slide pot pins
#define analogInPin 1
#define motorUpPin 2
#define motorDownPin 3
#define capacitiveInPin 
#define capacitiveOutPin
