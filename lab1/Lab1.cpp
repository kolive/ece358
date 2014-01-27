#include <stdio.h>
#include "RandomVariable.h"
#include "ExponentialVariable.h"
#include "PoissonVariable.h"
#include "Event.h"
#include "EventType.h"
#include <vector>
#include <algorithm>
#include <cmath>
#include "Logger.h"
#include <map>

#define LOGGING 0

typedef std::vector<Event*> event_set;
typedef std::vector<int> count_sample;
typedef std::map<int, bool> droppedStatus;
void simulate(double T, double alpha, double lambda, double L, double C, int K){

  event_set es;
  event_set ds;
  count_sample cs;

  //generate an exponential random variable with lambda alpha to use as interarrival times
  //NOTE: currently assuming T*2 arrivals should span the time between 0-T. May have to tweak
  ExponentialVariable *obv = new ExponentialVariable(10, alpha);
  if(!obv->generateExponential(20000)){
    printf("Failure in generating observer distribution. \n");
    return;
  }
  double currentTime = 0.0;
  int n = 0;
  //build observers
  while(currentTime <= T){
    currentTime += obv->values[n];
    n = (n+1) % 20000;
    es.push_back( new Event(currentTime, OBSERVER) );
  }

  //generate an exponential random variable to use as interarrival times for arrivals
  ExponentialVariable *arv = new ExponentialVariable(10, lambda);
  if(!arv->generateExponential(20000)){
    printf("Failure in generating arrival distribution. \n");
    return;
  }

  //generate packet sizes based on exponential distribution of parameter 1/L
  ExponentialVariable *dep = new ExponentialVariable(10, (double)(1/L));
  if(!dep->generateExponential(40000)){
    printf("Failure in generating size distribution. \n");
    return;
  }

  currentTime = 0.0;
  n = 0;
  while(currentTime <= T){
    //move to next arrival
    currentTime += arv->values[n];
    n = (n+1) % 20000;

    es.push_back( new Event((double)currentTime, ARRIVAL) );

    //calculate departure time of packet n. departureTime = max(latestPreviousDeparture, latestArrival) + transmitTime
    // logically, this means that the packet can start being sent either when the last one is being sent, or the next one is recieved
    // whichever is longer.
    // the next latestdeparture is departureTime.
    // WHY IS THIS RIGHT FOR M/M/1 ?
    //   -> this is right because the arrival times are markovian (defined by poisson process)
    //   -> the service time is markovian (defined by exponential distribution of size of packets)
    //   -> there is one server (a packet cannot be processed until the previous one is finished)
    //transmitTime = dep->values[n] / C;
    //latestDeparture = std::max(latestDeparture, currentTime) + transmitTime;

    //if this is an M/M/1 queue we can precalculate the departure times since no packets will be dropped
    //if(K == -1) ds.push_back( new Event(latestDeparture, DEPARTURE) );

  }
  //sorts events with smallest time at index 0
  std::sort(es.begin(), es.end(), Event::EventPredicate);
  //sorts departures with smallest time at back
  std::sort(ds.begin(), ds.end(), Event::EventPredicate2);

  //EventSet generated. Begin Simulation...
  int arrivalCount = 0;
  int departureCount = 0;
  int observerCount = 0;
  int droppedCount = 0;
  double nextDepartureStart = 0;
  double latestDepartureEnd = 0;
  for(unsigned int i = 0; i < es.size(); i++){
    Event * e = es.at(i);
    if(ds.size() > 0 && e->getArrivalTime() >= ds.back()->getArrivalTime()){
      //if there are departures that happen before the a/o
      e = ds.back();
      ds.pop_back();
    }else if(ds.size() > 0){
      //if the event is an arrival we calculate the time that the next departure starts
      nextDepartureStart = ds.at(0)->getArrivalTime();
    }
    switch(e->getType()){
      case ARRIVAL:
        Logger::log(LOGGING, (char *) "%G, ARRIVAL\n", e->getArrivalTime());
        if(((int)ds.size()) < K || K == -1){
          arrivalCount++;
          latestDepartureEnd = std::max(nextDepartureStart, e->getArrivalTime()) + (dep->values[(i%20000)] / C);
          ds.push_back(new Event(latestDepartureEnd, DEPARTURE));
          std::sort(ds.begin(), ds.end(), Event::EventPredicate2);
          Logger::log(LOGGING, (char *) "NEXT DEPARTURE SCHEDULED FOR: %G \n", latestDepartureEnd);
        }else if(((int)ds.size()) >= K){
          droppedCount++;
        }
        //if the event is an arrival and K != -1 > calculate the next departure
        break;
      case DEPARTURE:
        Logger::log(LOGGING, (char *)"%G, DEPARTURE\n", e->getArrivalTime());
        departureCount++;
        break;
      case OBSERVER:
        //record performance metrics
        cs.push_back(ds.size());
        observerCount++;
        break;
      default:
        break;
    }
  }

  double mean_count = 0;
  for(unsigned int i = 0; i < cs.size(); i++){
    mean_count += cs.at(i);
  }

  mean_count = (mean_count*1.0) / cs.size();


 // printf(" ARRIVAL COUNT: %d\n DEPARTURE COUNT: %d\n OBSERVER COUNT: %d\n", arrivalCount, departureCount, observerCount);

  printf("%d,%d,%d,", arrivalCount, departureCount, observerCount);

  //printf(" MEAN PACKETS IN SYSTEM: %G \n", mean_count);
  printf("%f,", mean_count);

  double ploss = (droppedCount * 100.0)/arrivalCount;
  //printf(" PACKETS DROPPED: %f percent \n", ploss);
  printf("%f", ploss);

  for(unsigned int i = 0; i < es.size(); i++){
    delete es.at(i);
  }

}

void questionThree(){
//  printf(" \n BEGINNING SIMULATION OF M/M/1 QUEUE \n ");

  double simulationLength = 10000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 0;

  for(row = 0.25; row < 0.95; row += 0.1){
    lambda = (linkRate * row) / packetSize;
    alpha = lambda;
    printf("\n,%f,%f,", row, simulationLength);
    simulate(simulationLength, alpha, lambda, packetSize, linkRate, -1);
    printf("\n,%f,%f,",row,(simulationLength*2));
    simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, -1);
  }


 // printf("\n SIMULATION COMPLETE. \n");

}

void questionFour(){
//  printf(" \n BEGINNING SIMULATION OF M/M/1 QUEUE \n ");

  double simulationLength = 10000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 1.2;

  lambda = (linkRate * row) / packetSize;
  alpha = lambda;
  printf("\n,%f,%f,", row, simulationLength);
  simulate(simulationLength, alpha, lambda, packetSize, linkRate, -1);
  printf("\n,%f,%f,",row,(simulationLength*2));
  simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, -1);


 // printf("\n SIMULATION COMPLETE. \n");

}

void questionSixA(){
//  printf(" \n BEGINNING SIMULATION OF M/M/1 QUEUE \n ");

  double simulationLength = 10000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 0.0;
  double k[] = {5, 10, 40, -1};

  for(int i = 0; i < 4; i++){
    for(row = 0.5; row < 1.5; row += 0.1){
      lambda = (linkRate * row) / packetSize;
      alpha = lambda;
      printf("\n,%f,%f,", row, simulationLength);
      simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
      printf("\n,%f,%f,",row,(simulationLength*2));
      simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
   }
 }

 // printf("\n SIMULATION COMPLETE. \n");

}

void questionSixB(){
//  printf(" \n BEGINNING SIMULATION OF M/M/1 QUEUE \n ");

  double simulationLength = 10000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 0.0;
  double k[] = {5, 10, 40};

  for(int i = 0; i < 3; i++){
    for(row = 0.4; row <= 2; row += 0.1){
      lambda = (linkRate * row) / packetSize;
      alpha = lambda;
      printf("\n,%f,%f,", row, simulationLength);
      simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
      printf("\n,%f,%f,",row,(simulationLength*2));
      simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
    }
  }

  for(int i = 0; i < 3; i++){
    for(row = 2.1; row <= 2.5; row += 0.2){
      lambda = (linkRate * row) / packetSize;
      alpha = lambda;
      printf("\n,%f,%f,", row, simulationLength);
      simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
      printf("\n,%f,%f,",row,(simulationLength*2));
      simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
    }
  }

  for(int i = 0; i < 3; i++){
    for(row = 2.7; row < 10; row += 0.4){
      lambda = (linkRate * row) / packetSize;
      alpha = lambda;
      printf("\n,%f,%f,", row, simulationLength);
      simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
      printf("\n,%f,%f,",row,(simulationLength*2));
      simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
    }
  }
 // printf("\n SIMULATION COMPLETE. \n");

}
int main(){

  printf("\n *********************************************** \n");
  printf("  ECE 358 - Lab 1: Queue Simulator \n");
  printf("  Kyle Olive - 20378286 - kolive \n");
  printf("  January 14th 2014 \n");
  printf(" ************************************************ \n");

  printf("\nQuestion #, Row, Simulation Time (s), ArrivalCount, DepartureCount, ObserverCount, E[Packets In System], Ploss");

  printf("\nQuestion 3,,,,,,,");
  questionThree();
  printf("\nQuestion 4,,,,,,,");
  questionFour();
  printf("\nQuestion 6A,,,,,,,");
  questionSixA();
  printf("\nQuestion 6B,,,,,,,");
  questionSixB();

}
