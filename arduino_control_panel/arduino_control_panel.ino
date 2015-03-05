// Compiled for use with an Arduino MEGA ONLY

// DO NOT WRITE CODE ABOVE LIBRARY IMPORTS

// Import Controller Emulation Library
#include "UnoJoy.h"

// Import LiquidCrystal Display Library and Define Pins
#include <LiquidCrystal.h>

#define LCD_BACKLIGHT_PIN  13
#define LCD_BACKLIGHT_OFF()  digitalWrite(LCD_BACKLIGHT_PIN, LOW)
#define LCD_BACKLIGHT_ON()  digitalWrite(LCD_BACKLIGHT_PIN, HIGH)

// Setup LCD Pins
LiquidCrystal lcd(8, 9, 4, 5, 6, 7);

String lastEdited = "3.01.15   8:33pm";
String ver = "2.1";

// Setup labels for buttons.
int nextAutoButton;          // Next Auto Button              (D22)
int stackUp;                 // Stack Up                      (D24)
int stackDown;               // Stack Down                    (D26)
int stack;                   // Stack                         (D28)
int switchButton;            // Switch PID to Container Mode  (D30)
int tiltButton;              // Tilt                          (D34)
int resetEnc;                // Reset Encoder to new "0"      (D36)

// Define Joystick Analog Ports
int thumbY = 15;

// Define Joystick Values;
int joyY = 0;
int joyYold = 0;

// Read Delay Values
// RD = ReadDelay
int printAutoLCDrd = 0;

// Display Timer Value
int lcdBacklight;

int lcdInfo = 0;

// Booleans
boolean manualChanged = true;  //Boolean for thumbY
boolean autoNext = false;                 // nextAutoButton
boolean autoChanged = false;              //
boolean stackUpPID = false;               // Level Up
boolean stackDownPID = false;             // Level Down
boolean pidResetEnc = false;              // resetEnc (current pos = encoder new "0")
boolean swtichSet = false;             // Change PID to use container values
boolean stacked = false;                  // Stack
boolean tilt = false;                     // Tilt

boolean startup = false;
boolean defaultLCD = false;
boolean printDefaultLCD = false;
boolean printAutoLCD = false;
boolean printAutoLCDallow = false;

// Auto Mode Values
int autoMode = 1;
int oldAuto = 1;



// When ran, clears the display with empty spaces.
void clearLCD()
{
  lcd.setCursor(0, 0);
  lcd.print("                ");
  lcd.setCursor(0, 1);
  lcd.print("                ");
}

void writeLCD()
{
  if (startup)
  {
    clearLCD();
    printAutoLCD = false;
    startup = false;
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

void sendControls()
{
  dataForController_t controllerData = getControllerData();
  setControllerData(controllerData);
}


void setup()
{
  // Define which pins are inputs or outputs.
  pinMode ((nextAutoButton, tiltButton, stackUp, stackDown, switchButton, stack, pidResetEnc), INPUT);
  pinMode (LCD_BACKLIGHT_PIN, OUTPUT);

  // Initialize UnoJoy and set controller to default values.
  setupUnoJoy();
  getBlankDataForController();

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

  // Read Joystick to get stationary vaules
  joyY = map(analogRead(thumbY), 0, 1023, 0, 255);
  sendControls();

  lcdBacklight = 2000;
  startup = true;
}



// Main Program Loop
void loop()
{
  if (startup) writeLCD();
  
  // Get values for Manual Joystick
  joyY = map(analogRead(thumbY), 0, 1023, 255, 0);

  if (joyYold != joyY)
  {
    manualChanged = true;
    //sendControls();
    joyYold = joyY;
    manualChanged = false;
  }
  if (manualChanged) manualChanged = false;
  //Serial.println(joyY);

  // Read Next Auto Button
  nextAutoButton = digitalRead(22);
  if (nextAutoButton == HIGH && !autoChanged)
  {
    autoMode++;
    autoNext = true;
    autoChanged = true;
  }
  else if (nextAutoButton == LOW && autoChanged)
  {
    autoNext = false;
    autoChanged = false;
  }

  // Read Stack Up/Down Buttons
  stackUp = digitalRead(24);
  if (stackUp == HIGH) stackUpPID = true;
  else if (stackUp == LOW) stackUpPID = false;
  
  stackDown = digitalRead(26);
  if (stackDown == HIGH) stackDownPID = true;
  else if (stackDown == LOW) stackDownPID = false;

  // Read Stack Button
  stack = digitalRead(28);
  if (stack == HIGH) stacked = true;
  else if (stack == LOW) stacked = false;

  // Read PID Switch Button
  switchButton = digitalRead(30);
  if (switchButton == HIGH) swtichSet = true;
  else if (switchButton == LOW) swtichSet = false;
  
  // Read Tilt Button
  tiltButton = digitalRead(34);
  if (tiltButton == HIGH) tilt = true;
  else if (tiltButton == LOW) tilt = false;

  // Read Reset Encoder to "0"
  resetEnc = digitalRead(36);
  if (resetEnc == HIGH) pidResetEnc = true;
  else if (resetEnc == LOW) pidResetEnc = false;

  // Send Controls
  sendControls();
  
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
  //if (manualChanged) controllerData.leftStickY = joyY;
  controllerData.leftStickY = joyY;

  // Tilt Button
  if (tilt) controllerData.l2On = 1;
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

  // And return the data!
  return controllerData;
}

// Code written by Nathan Baugh ~ Member of FRC Team 4322 ~ Game: 2015 Recycle Rush
