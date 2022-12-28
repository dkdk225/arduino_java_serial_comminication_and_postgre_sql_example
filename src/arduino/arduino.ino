String ledValues[] = {"LOW", "LOW", "LOW", "LOW"};
void toggleLED(int ledNum);
void getDegreeWithTime(unsigned long array[] );
void sendDegreeAndTime();
unsigned long lastSent;
int sendInterval = 3000;//ms for each interval
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  //init pins
  for (int i = 2; i < 6; i++){
    pinMode(i, OUTPUT);
  }

  while(!Serial) {
    ;
  }
  lastSent = millis();
}


void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0) {
    byte incomingByte = Serial.read();
    if(incomingByte != -1) {
      toggleLED(int(incomingByte));
    }
  }
  unsigned long currentTime = millis();
  if(currentTime - lastSent > sendInterval){
    sendDegreeAndTime();
    lastSent = currentTime;
  }
}

void toggleLED(int ledNum){
  int pin = ledNum + 2; 
  if(ledValues[ledNum] == "LOW"){
    digitalWrite(pin, HIGH);
    ledValues[ledNum] = "HIGH";
    Serial.println("set pin "+String(pin)+" value to HIGH");
  }else {
    digitalWrite(pin, LOW);
    ledValues[ledNum] = "LOW";
    Serial.println("set pin "+String(pin)+" value to LOW");
  }
}
void getDegreeWithTime(unsigned long array[]){
  unsigned long degree = random(22,33);
  unsigned long time = millis();
  array[0] = degree;
  array[1] = time;
}


void sendDegreeAndTime(){
    unsigned long degreeAndTime[2];
    getDegreeWithTime(degreeAndTime);
    unsigned long degree = degreeAndTime[0];
    unsigned long time = degreeAndTime[1];
    Serial.println("obtained degree: " + String(degree) + " and time: " + String(time));
}
