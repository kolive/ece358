#include "Event.h"
#include "EventType.h"

Event::Event(double arrivalTime, EventType type){
  this->arrivalTime = arrivalTime;
  this->type = type;
}

Event* Event::getParent(){
  return this->parent;
}

void Event::setParent(Event * p){
  this->parent = p;
}

double Event::getArrivalTime(){
  return this->arrivalTime;
}

EventType Event::getType(){
  return this->type;
}

