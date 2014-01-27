#ifndef EVENT_H
#define EVENT_H
#include "EventType.h"
class Event{
  double arrivalTime;
  Event * parent; // pointer to the event that is it's parent
  EventType type;
  public:
    Event(double, EventType);
    double getArrivalTime();
    EventType getType();
    void setParent(Event *);
    Event * getParent();
    static bool EventPredicate(Event* e1, Event* e2){
      return e1->getArrivalTime() < e2->getArrivalTime();
    }
    static bool EventPredicate2(Event* e1, Event* e2){
      return e1->getArrivalTime() > e2->getArrivalTime();
    }

};

#endif
