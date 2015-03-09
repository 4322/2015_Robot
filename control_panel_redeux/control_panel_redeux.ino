
#include "UnoJoy.h"

void setup(){
  setupPins();
  setupUnoJoy();
}

void loop(){
  // Always be getting fresh data
  dataForController_t controllerData = getControllerData();
  setControllerData(controllerData);
}

void setupPins(void){
  // Set all the digital pins as inputs
  // with the pull-up enabled, except for the 
  // two serial line pins
  for (int i = 2; i <= 12; i++){
    pinMode(i, INPUT);
    digitalWrite(i, HIGH);
  }
  pinMode(15, INPUT);
  digitalWrite(15, HIGH);
//  pinMode(A5, INPUT);
//  digitalWrite(A5, HIGH);
}

dataForController_t getControllerData(void){
  
  // Set up a place for our controller data
  //  Use the getBlankDataForController() function, since
  //  just declaring a fresh dataForController_t tends
  //  to get you one filled with junk from other, random
  //  values that were in those memory locations before
  dataForController_t controllerData = getBlankDataForController();
  // Since our buttons are all held high and
  //  pulled low when pressed, we use the "!"
  //  operator to invert the readings from the pins
  controllerData.triangleOn = digitalRead(24); //button 4
  controllerData.circleOn = digitalRead(39); //button 3
  controllerData.squareOn = digitalRead(28); //button 1
  controllerData.crossOn = digitalRead(26); //button 2
  controllerData.dpadUpOn = digitalRead(33); 
  controllerData.dpadDownOn = digitalRead(37);
  controllerData.dpadLeftOn = digitalRead(41);
  controllerData.dpadRightOn = digitalRead(35);
  controllerData.l1On = digitalRead(34); //button
  if(digitalRead(30)==HIGH){
    controllerData.dpadUpOn = 1;
    controllerData.dpadRightOn = 1;
  }
    
 // controllerData.r1On = !digitalRead(11);
  //controllerData.selectOn = !digitalRead(12);
  //controllerData.startOn = !digitalRead(A4);
  //controllerData.homeOn = !digitalRead(A5);
  
  // Set the analog sticks
  //  Since analogRead(pin) returns a 10 bit value,
  //  we need to perform a bit shift operation to
  //  lose the 2 least significant bits and get an
  //  8 bit number that we can use  
//  controllerData.leftStickX = analogRead(A0) >> 2;
  controllerData.leftStickY = analogRead(15) >> 2;
//  controllerData.rightStickX = analogRead(A2) >> 2;
//  controllerData.rightStickY = analogRead(A3) >> 2;
  // And return the data!
  return controllerData;
}
