
// Compiled for use with an Arduino MEGA ONLY
// DO NOT WRITE CODE ABOVE LIBRARY IMPORTS
// Import Controller Emulation Library
#include "UnoJoy.h"
// Import LiquidCrystal Display Library and Define Pins
#include <LiquidCrystal.h>
#define LCD_BACKLIGHT_PIN  13
#define LCD_BACKLIGHT_OFF()  digitalWrite(LCD_BACKLIGHT_PIN, LOW)
#define LCD_BACKLIGHT_ON()  digitalWrite(LCD_BACKLIGHT_PIN, HIGH)
#define LCD_BACKLIGHT(state)    { if( state ){digitalWrite( LCD_BACKLIGHT_PIN,
    HIGH );}else{digitalWrite( LCD_BACKLIGHT_PIN, LOW );} }
// Setup LCD Pins
LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
String lastEdited = "2.22.15  08:30pm";
String ver = "2.0";
// Auto Choice Button
const int nextAutoButton = 22;
// PID Stack
const int stackUp = 26;
const int stackDown = 28;
// Switch PID values for container
const int containerButton = 30;
// Stack tote(s)/container (Switch to Container PID Values)
const int stack = 32;
// Tilt Button
const int tiltButton = 34;
// Switch PID values for totes
const int toteButton = 36;
// PID Reset Encoder (Set encoder's new "0" value with current pos)
const int resetEnc = 38;
// Define Joystick Analog Ports
const int thumbY = 15;
// Define Joystick Values;
int joyY = 0;
int joyYold = 0;
// Read Delay Values
// RD = ReadDelay
int printAutoLCDrd = 0;
// Display Timer Value
int lcdBacklight = 5000;
// Booleans
boolean manualChanged = true;  //Boolean for thumbY
boolean autoNext = false;
boolean autoChanged = false;
boolean stackUpPID = false;
boolean stackDownPID = false;
boolean toteSet = false;
boolean pidResetEnc = false;
"0")
boolean containerSet = false;
boolean stacked = false;
boolean tilt = false;
boolean startup = false;
boolean defaultLCD = false;
boolean printDefaultLCD = false;
boolean warning = false;
boolean printWarning = false;
boolean printAutoLCD = false;
boolean printAutoLCDallow = false;
// Auto Mode Values
int autoMode = 1;
int oldAuto = 1;
// nextAutoButton
//
// Level Up
// Level Down
// Change PID to use tote values
// resetEnc (current pos = encoder new
// Change PID to use container values
// Stack
// Tilt
// When ran, clears the display with empty spaces.
void clearLCD()
{
  lcd.setCursor(0, 0);
  lcd.print("                ");
  lcd.setCursor(0, 1);
  lcd.print("                ");
}
void warningLCD()
{
  printWarning = true;
  clearLCD();
  lcd.setCursor(0, 0);
  lcd.print("You are pressing");
  lcd.setCursor(0, 1);
  lcd.print("too many buttons");
}
void writeLCD()
{
  if (startup)
  {
    clearLCD();
    printAutoLCD = true;
    startup = false;
}
  else if (printAutoLCD)
  {
    //    defaultLCD = false;
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
    lcd.print("READY");
    lcd.setCursor(10, 0);
    lcd.print("Auto");
    lcd.setCursor(15, 0);
    lcd.print(autoMode);
    lcd.setCursor(2, 1);
    lcd.print("Press a Button");
    printDefaultLCD = false;
} }
void sendControls()
{
  dataForController_t controllerData = getControllerData();
  setControllerData(controllerData);
  lcdBacklight = 5000;
}
void setup()
{
  // Define which pins are inputs or outputs.
  pinMode ((nextAutoButton, tiltButton, stackUp, stackDown, containerButton,
      toteButton, stack, pidResetEnc), INPUT);
  pinMode ((LCD_BACKLIGHT_PIN), HIGH);
  // Initialize UnoJoy and set controller to default values.
  setupUnoJoy();
  getBlankDataForController();
  // Startup LCD
  digitalWrite(LCD_BACKLIGHT_PIN, HIGH);
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
  delay(1000);
  // Clear the display to ready it for main loop.
clearLCD();
  // Read Joystick to get stationary vaules
  joyY = map(analogRead(thumbY), 0, 1023, 0, 255);
  sendControls();
  startup = true;
}
// Main Program Loop
void loop() {
  if (startup) writeLCD();
  // Get values for Manual Joystick
  joyY = map(analogRead(thumbY), 0, 1023, 0, 255);
  if (joyYold != joyY)
  {
    manualChanged = true;
    sendControls();
    joyYold = joyY;
    manualChanged = false;
  }
  if (manualChanged) manualChanged = false;
  // Read Next Auto Button
  if (digitalRead (nextAutoButton == HIGH) && !autoChanged)
  {
    autoMode++;
    autoNext = true;
    autoChanged = true;
  }
  else if (digitalRead (nextAutoButton == LOW) && autoChanged)
  {
    autoNext = false;
    autoChanged = false;
  }
  // Read Tilt Button
  if (digitalRead (tiltButton == HIGH)) tilt = true;
  else if (digitalRead (tiltButton == LOW)) tilt = false;
// Read Stack Up/Down Buttons
if (digitalRead (stackUp == HIGH)) stackUpPID = true;
else if (digitalRead (stackUp == LOW)) stackUpPID = false;
if (digitalRead (stackDown == HIGH)) stackDownPID = true;
else if (digitalRead (stackDown == LOW)) stackDownPID = false;
// Read Container Button
if (digitalRead (containerButton == HIGH)) containerSet = true;
else if (digitalRead (containerButton == LOW)) containerSet = false;
// Read Stack Button
if (digitalRead (stack == HIGH)) stacked = true;
else if (digitalRead (stack == LOW)) stacked = false;
// Read Reset to tote mode
if (digitalRead (toteButton == HIGH)) toteSet = true;
else if (digitalRead (toteButton == LOW)) toteSet = false;
// Read Reset Encoder to "0"
if (digitalRead (resetEnc == HIGH)) pidResetEnc = true;
else if (digitalRead (resetEnc == LOW)) pidResetEnc = false;
// Check if certain combinations of buttons are pushed and enable (warning)
if ((stackUpPID && stackDownPID) || ((stackUpPID || stackDownPID) && stacked ||
    containerSet || toteSet || pidResetEnc) || (stacked && (containerSet ||
    toteSet || pidResetEnc)) || (containerSet && (toteSet || pidResetEnc)) ||
    (toteSet && pidResetEnc))
{
  if ((stackUpPID && stackDownPID) || ((stackUpPID || stackDownPID) && stacked
      || containerSet || toteSet || pidResetEnc)) warning = true;
  else if (stacked && (containerSet || toteSet || pidResetEnc)) warning = true;
  else if (containerSet && (toteSet || pidResetEnc)) warning = true;
  else if (toteSet && pidResetEnc) warning = true;
  else
  {
    printWarning = false;
    warning = false;
  }
}
// Send controls for any button pressed
if (autoNext || tilt || stackUpPID || stackDownPID || containerSet || stacked |
    | toteSet || pidResetEnc)
{
  sendControls();
}
// Check for changes to autoMode and update LCD
if (autoNext && autoChanged)
{
  if (autoMode > 3) autoMode = 1;
  if (oldAuto != autoMode)
  {
    defaultLCD = false;
    printDefaultLCD = false;
    printAutoLCD = true;
      printAutoLCDrd = 2000;
      writeLCD();
      oldAuto = autoMode;
} }
  if (printAutoLCDrd > 0) printAutoLCDrd--;
  if (printAutoLCDrd == 0 && !defaultLCD && !printDefaultLCD) defaultLCD = true;
  if (!printWarning)
  {
    warningLCD();
  }
  else if (defaultLCD && printAutoLCDrd == 0)
  {
    printAutoLCD = false;
    printDefaultLCD = true;
    writeLCD();
    defaultLCD = false;
}
  // Control LCD Backlight with Timer
  if (lcdBacklight == 0) LCD_BACKLIGHT_OFF();
  else if (lcdBacklight > 0)
  {
    LCD_BACKLIGHT_ON();
    lcdBacklight--;
  }
}
// Out put controller values.  (reference PS3 DualShock buttons)
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
  if (manualChanged) controllerData.leftStickY = joyY;
  // Tilt Button
  if (tilt) controllerData.l1On = 1;
  else if (!tilt) controllerData.l1On = 0;
  // Auto Button (Right Bumper)
  if (autoNext) controllerData.r1On = 1;
  else if (!autoNext) controllerData.r1On = 0;
  // Stack Up Button
  if (stackUpPID && !stackDownPID && !warning) controllerData.triangleOn = 1;
  else if (!stackUpPID || warning) controllerData.triangleOn = 0;
  // Stack Down Button
  if (stackDownPID && !stackUpPID && !warning) controllerData.crossOn = 1;
  else if (!stackDownPID || warning) controllerData.crossOn = 0;
  // Stack Button
  if (stacked && !warning) controllerData.squareOn = 1;
  else if (!stacked || warning) controllerData.squareOn = 0;
  // Container PID Mode
  if (containerSet && !warning) controllerData.circleOn = 1;
  else if (!containerSet || warning) controllerData.circleOn = 0;
  // Reset PID Mode to tote level 1
  if (toteSet && !warning) controllerData.startOn = 1;
  else if (!toteSet || warning) controllerData.startOn = 0;
  // Reset PID Encoder "0" position
  if (pidResetEnc && !warning) controllerData.selectOn = 1;
  else if (!pidResetEnc || warning) controllerData.selectOn = 0;
  // And return the data!
  return controllerData;
}
// Code written by Nathan Baugh ~ Member of FRC Team 4322 ~ Game: 2015 Recycle
    Rush ~ Mary-Anne


