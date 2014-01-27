#ifndef EVENT_H
#define EVENT_H
#include "EventType.h"
class Event{
  double arrivalTime;
  EventType type;
  public:
    Event(double, EventType);
    double getArrivalTime();
    EventType getType();
    static bool EventPredicate(Event* e1, Event* e2){
      return e1->getArrivalTime() < e2->getArrivalTime();
    }
    static bool EventPredicate2(Event* e1, Event* e2){
      return e1->getArrivalTime() > e2->getArrivalTime();
    }

};

#endif
