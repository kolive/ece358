#include "Event.h"
#include "EventType.h"

Event::Event(double arrivalTime, EventType type){
  this->arrivalTime = arrivalTime;
  this->type = type;
}

double Event::getArrivalTime(){
  return this->arrivalTime;
}

EventType Event::getType(){
  return this->type;
}

