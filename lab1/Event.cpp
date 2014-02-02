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

Event * Event::minEvent(Event * a, Event * b){
  Event * min;
  double at = a->getArrivalTime();
  double bt = b->getArrivalTime();
  if(at < bt){
    min = a;
  }else{
    min = b;
  }
  return min;

}
Event * Event::minEvent(Event * a, Event * b, Event * c){
  Event * min;
  if(a->getArrivalTime() < b->getArrivalTime()){
    min = a;
  }else{
    min = b;
  }

  if(min->getArrivalTime() <= c->getArrivalTime()){
    return min;
  }

  return c;
}
