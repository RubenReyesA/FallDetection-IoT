#include <Arduino.h>
#include <BLEPeripheral.h>
#include <SPI.h>
#include "Ticker.h"

BLEPeripheral ledPeripheral = BLEPeripheral();

BLEService ledService = BLEService("157ece0d-a1d0-4b56-9ee0-3304b24d59a8"); //Service UUID
BLECharCharacteristic ledCharacteristic = BLECharCharacteristic(
  "ef3d230b-b091-415a-8694-1b7eb7b1739f", BLENotify);

int count = 0;

void sendNotif(){
BLECentral central = ledPeripheral.central();

  if (central) {
   ledCharacteristic.setValue(count);
   ledCharacteristic.broadcast();
  }

  count++;

}

Ticker tickerObject(sendNotif, 25); 

void setup() {

  Serial.begin(9600);

  tickerObject.start(); //start the ticker.

  ledPeripheral.setLocalName("Nordic Rubén");
  ledPeripheral.setAdvertisedServiceUuid(ledService.uuid());

  ledPeripheral.addAttribute(ledService);
  ledPeripheral.addAttribute(ledCharacteristic);

  ledPeripheral.begin();

}

void loop() {
  // put your main code here, to run repeatedly:

  tickerObject.update(); //it will check the Ticker and if necessary, it will run the callback function.
  
}