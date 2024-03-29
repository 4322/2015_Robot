// Compiled for use with an Arduino MEGA ONLY

// DO NOT WRITE CODE ABOVE LIBRARY IMPORTS


#include "UnoJoy.h" // Import Controller Emulation Library
#include "config.h" // import defines from config file


// Import LiquidCrystal Display Library and Define Pins
#include <LiquidCrystal.h>

#define LCD_BACKLIGHT_PIN  13
#define LCD_BACKLIGHT_OFF()  digitalWrite(LCD_BACKLIGHT_PIN, LOW)
#define LCD_BACKLIGHT_ON()  digitalWrite(LCD_BACKLIGHT_PIN, HIGH)

// Setup LCD Pins
LiquidCrystal lcd(8, 9, 4, 5, 6, 7);


// initialize integers for buttons.
int nextAutoButton;          // Next Auto Button              (D22)
int stackUp;                 // Stack Up                      (D24)
int stackDown;               // Stack Down                    (D26)
int stack;                   // Stack                         (D28)
int containerButton;         // Switch PID to Container Mode  (D30)
int tiltButton;              // Tilt                          (D34)
int resetEnc;                // Reset Encoder to new "0"      (D36)


int intpid0;             // elevator position 0
int intpid1;             // elevator position 1
int intpid2;           // elevator position 2
int intpid3;            // elevator position 3
int intpid4;            // elevator position 4

// initialize Booleans for buttons

boolean autoNext = false;                 // nextAutoButton
boolean stackUpPID = false;               // Level Up
boolean stackDownPID = false;             // Level Down
boolean stacked = false;                  // Stack
boolean swtichSet = false;            	 // Change PID to use container values
boolean tilt = false;                     // Tilt
boolean pidResetEnc = false;              // resetEnc (current pos = encoder new "0")

boolean boolpid0 = false;             // elevator position 0
boolean boolpid1 = false;             // elevator position 1
boolean boolpid2 = false;           // elevator position 2
boolean boolpid3 = false;            // elevator position 3
boolean boolpid4 = false;            // elevator position 4

//booleans is changed?
boolean manualChanged = true;  			//Boolean for thumbY
boolean autoChanged = false;

// initialize Joystick Values;
int joyY = 0;
int joyYold = 0;

// Read Delay Values
// RD = ReadDelay
int printAutoLCDrd = 0;

// Display Timer Value
int lcdBacklight;

int lcdInfo = 0;




boolean startup = false;
boolean defaultLCD = false;
boolean printDefaultLCD = false;
boolean printAutoLCD = false;
boolean printAutoLCDallow = false;

// Auto Mode Values
int autoMode = 1;
int oldAuto = 1;

//slide pot variables
int toteIndex = 0;
int rawJoyY = 0;


// When ran, clears the display with empty spaces.
void clearLCD()
{
  lcd.setCursor(0, 0);
  lcd.print("                ");
  lcd.setCursor(0, 1);
  lcd.print("                ");
}

// Write auto mode to LCD
void writeLCD()
{
  if (startup) //if its the first time being run
  {
    clearLCD(); //clear the LCD
    printAutoLCD = false;
    startup = false; //don't run again
  }
  else if (printAutoLCD)
  {
    clearLCD();
    lcd.setCursor(3, 0);
    lcd.print("Autonomous");
    lcd.setCursor(5, 1);
    lcd.print("MODE");
    lcd.setCursor(10, 1);
    lcd.print(autoMode);
    printAutoLCDallow = false;
  }
  else if (printDefaultLCD && !printAutoLCD)
  {
    clearLCD();
    lcd.setCursor(0, 0);
    lcd.print("Co-Pilot");
    lcd.setCursor(10, 0);
    lcd.print("Auto");
    lcd.setCursor(15, 0);
    lcd.print(autoMode);
    lcd.setCursor(0, 1);
    lcd.print("                ");
    printDefaultLCD = false;
  }
}

// send the buffered data to PC over HID
void sendControls()
{
  dataForController_t controllerData = getControllerData();
  setControllerData(controllerData);
}


void setup()
{

// Define which pins are inputs or outputs.
	pinMode ((nextAutoButton, tiltButton, stackUp, stackDown, containerButton, stack, pidResetEnc), INPUT);
	pinMode (LCD_BACKLIGHT_PIN, OUTPUT);

// Initialize UnoJoy and set controller to default values.
	setupUnoJoy();
	getBlankDataForController();

// Read Joystick to get stationary vaules
	joyY = map(analogRead(15), 0, 1023, 0, 255);
	sendControls();



//LCD code
  /*
  // Startup LCD
  digitalWrite(LCD_BACKLIGHT_PIN, LOW);
  lcd.begin(16, 2);

  // Print Initial Display Text
  lcd.setCursor(0, 0);
  lcd.print("ClockworkOranges");
  lcd.setCursor(4, 1);
  lcd.print("FRC 4322");
  delay(2000);
  clearLCD();
  lcd.setCursor(0, 0);
  lcd.print("Version");
  lcd.setCursor(13, 0);
  lcd.print(ver);
  lcd.setCursor(0, 1);
  lcd.print(lastEdited);
  delay(2000);

  // Clear the display to ready it for main loop.
  clearLCD();



  lcdBacklight = 2000;
  startup = true;
  */
}



// Main Program Loop
void loop()
{

  if (startup) writeLCD(); //shouldn't happen because startup has already been set false

				
// Get values for Manual Joystick
	joyY = map(analogRead(thumbYPin), 0, 1023, 0, 255);
 	//Serial.println(joyY);			

// Read Stack Up Button
		stackUp = digitalRead(stackUpPin);
		if (stackUp == HIGH) stackUpPID = true;
		else if (stackUp == LOW) stackUpPID = false;
// Read Stack Down Button
		stackDown = digitalRead(stackDownPin);
		if (stackDown == HIGH) stackDownPID = true;
		else if (stackDown == LOW) stackDownPID = false;

// Read Stack Button
		stack = digitalRead(stackPin);
		if (stack == HIGH) stacked = true;
		else if (stack == LOW) stacked = false;

// Read PID Switch Button
		containerButton = digitalRead(containerPin);
		if (containerButton == HIGH) swtichSet = true;
		else if (containerButton == LOW) swtichSet = false;

// Read Tilt Button
		tiltButton = digitalRead(tiltPin);
		if (tiltButton == HIGH) tilt = true;
		else if (tiltButton == LOW) tilt = false;

// Read Reset Encoder to "0"
		resetEnc = digitalRead(resetEncPin);
		if (resetEnc == HIGH) pidResetEnc = true;
		else if (resetEnc == LOW) pidResetEnc = false;
		
		
		
// Read PID0
		intpid0 = digitalRead(elevator0ButtonPin);
		if (resetEnc == HIGH) boolpid0 = true;
		else if (resetEnc == LOW) boolpid0 = false;
// Read PID1
		intpid1 = digitalRead(elevator1ButtonPin);
		if (resetEnc == HIGH) boolpid1 = true;
		else if (resetEnc == LOW) boolpid1 = false;
// Read PID2
		intpid2 = digitalRead(elevator2ButtonPin);
		if (resetEnc == HIGH) boolpid2 = true;
		else if (resetEnc == LOW) boolpid2 = false;
// Read PID3
		intpid3 = digitalRead(elevator3ButtonPin);
		if (resetEnc == HIGH) boolpid3 = true;
		else if (resetEnc == LOW) boolpid3 = false;
//Read PID4
		intpid4 = digitalRead(elevator4ButtonPin);
		if (resetEnc == HIGH) boolpid4 = true;
		else if (resetEnc == LOW) boolpid4 = false;
		


// Send Controls
		sendControls();
  
  // Nathan's LCD Code
/*
  // Check for changes to autoMode and update LCD
  if (autoNext && autoChanged)
  {
    if (autoMode > 3) autoMode = 1;
    if (oldAuto != autoMode)
    {
      lcdBacklight = 2000;
      defaultLCD = false;
      printDefaultLCD = false;
      printAutoLCD = true;
      printAutoLCDrd = 4000;
      writeLCD();
      oldAuto = autoMode;
    }
  }

  if (printAutoLCDrd > 0) printAutoLCDrd--;

  if (!defaultLCD && printAutoLCDrd == 0)
  {
    printAutoLCD = false;
    printDefaultLCD = true;
    writeLCD();
    lcdInfo = 0;
    defaultLCD = true;
  }
  else if (printAutoLCDrd == 0)
  {
    lcdInfo++;
    if (lcdInfo < 1000)
    {
      lcd.setCursor(0,1);
      lcd.print("   Team #4322   ");
    }
    else if (lcdInfo >= 1000 && lcdInfo < 2000)
    {
      lcd.setCursor(0,1);
      lcd.print("  Recycle Rush  ");
    }
    else if (lcdInfo >= 2000) lcdInfo = 0;
  }
      

  // Control LCD Backlight with Timer
  if (lcdBacklight > 0) 
  {
    lcdBacklight--;
    digitalWrite (13, HIGH); 
  }
  else if (lcdBacklight == 0) digitalWrite (13, LOW);
  */
  
}


// Output controller values.  (reference PS3 DualShock buttons)
dataForController_t getControllerData(void) {

  // Set up a place for our controller data
  //  Use the getBlankDataForController() function, since
  //  just declaring a fresh dataForController_t tends
  //  to get you one filled with junk from other, random
  //  values that were in those memory locations before
  dataForController_t controllerData = getBlankDataForController();
  // Since our buttons are all held high and
  //  pulled low when pressed, we use the "!"
  //  operator to invert the readings from the pins
  controllerData.leftStickY = joyY;


// Tilt Button
  if (tilt) controllerData.l2On  = 1;
  else if (!tilt) controllerData.l2On = 0;

// Auto Button (Right Bumper)
  if (autoNext) controllerData.r2On = 1;
  else if (!autoNext) controllerData.r2On = 0;

// Stack Up Button
  if (stackUpPID) controllerData.triangleOn = 1;
  else if (!stackUpPID) controllerData.triangleOn = 0;

// Stack Down Button
  if (stackDownPID) controllerData.crossOn = 1;
  else if (!stackDownPID) controllerData.crossOn = 0;

// Stack Button
  if (stacked) controllerData.squareOn = 1;
  else if (!stacked) controllerData.squareOn = 0;

// Switch PID Mode
  if (swtichSet) controllerData.circleOn = 1;
  else if (!swtichSet) controllerData.circleOn = 0;

// Reset PID Encoder "0" position
  if (pidResetEnc) controllerData.selectOn = 1;
  else if (!pidResetEnc) controllerData.selectOn = 0;

// pid0
  if (boolpid0) controllerData.l2On = 1;
  else if (!boolpid0) controllerData.l2On = 0;
  
// pid1
  if (boolpid1) controllerData.l3On = 1;
  else if (!boolpid1) controllerData.l3On = 0;
  
// pid2
  if (boolpid2) controllerData.r1On = 1;
  else if (!boolpid2) controllerData.r1On = 0;
  
// pid3
  if (boolpid3) controllerData.r3On = 1;
  else if (!boolpid3) controllerData.r3On = 0;
  
// pid4
  if (boolpid4) controllerData.dpadLeftOn = 1;
  else if (!boolpid4) controllerData.dpadLeftOn = 0;

  // And return the data!
  return controllerData;
}

// Code written by Nathan Baugh and Seth Itow ~ Members of FRC Team 4322 ~ Game: 2015 Recycle Rush
