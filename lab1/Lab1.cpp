#include <stdio.h>
#include "RandomVariable.h"
#include "ExponentialVariable.h"
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
typedef std::vector<double> sojourn_times;
typedef std::map<int, bool> droppedStatus;

void simulate(double T, double alpha, double lambda, double L, double C, int K){
  event_set ao;
  event_set d;
  count_sample pc;
  sojourn_times st;

  ExponentialVariable * interarrival = new ExponentialVariable(10, (lambda));
  if(!interarrival->generateExponential(20000)){
    return; //error
  }

  ExponentialVariable * interobserver = new ExponentialVariable(10, (alpha));
  if(!interobserver->generateExponential(20000)){
    return; //error
  }

  ExponentialVariable * sizes = new ExponentialVariable(10, 1/L);
  if(!sizes->generateExponential(20000)){
    return; //error
  }
  //generate observers
  double currentTime = 0;
  int n = 0;
  while( currentTime < T ) {
    currentTime += interobserver->values[n];
    n = (n+1) % 20000;

    ao.push_back( new Event(currentTime, OBSERVER) );

  }

  n = 0;
  currentTime = 0;
  //generate arrivals
  while(currentTime < T){
    currentTime += interarrival->values[n];
    n = (n+1) % 20000;
    ao.push_back( new Event(currentTime, ARRIVAL) );
  }

  std::sort(ao.begin(), ao.end(), Event::EventPredicate2);

  //begin simulation
  currentTime = 0;
  n = 0;
  int droppedCount = 0;
  int arrivalCount = 0;
  int observerCount = 0;
  int departureCount = 0;
  while( ao.size() > 0 || d.size() > 0){
    n = (n+1) % 20000;
    Event * e;
    if(ao.size() > 0 && d.size() > 0){
      if(ao.back()->getArrivalTime() < d.back()->getArrivalTime()){
        e = ao.back();
        ao.pop_back();
      }else{
        e = d.back();
        d.pop_back();
      }
    }else if(ao.size() > 0){
      e = ao.back();
      ao.pop_back();
    }else if(d.size() > 0){
      e = d.back();
      d.pop_back();
    }

    switch(e->getType()){
      case ARRIVAL:
        if((int)d.size() == K){
          //dropped packet
          droppedCount++;
        }else{
          arrivalCount++;
          //new arrival
          double processingTime = sizes->values[n] / C;
          Event * dep;
          if(d.size() > 0){

            dep = new Event((d.at(0)->getArrivalTime() + processingTime), DEPARTURE);
          }else{
            dep = new Event((e->getArrivalTime() + processingTime), DEPARTURE);
          }
          d.push_back(dep);
          std::sort(d.begin(), d.end(), Event::EventPredicate2);
          st.push_back((dep->getArrivalTime() - e->getArrivalTime()));
        }
        break;
      case DEPARTURE:
        departureCount++;
        break;
      case OBSERVER:
        observerCount++;
        pc.push_back((int)d.size());
        break;
      default:
        break;
    }

    delete e;

  }

  double mean_count = 0;
  double proportion_idle = 0;
  while(pc.size() > 0){
    mean_count += pc.back();
    if(pc.back() == 0){
      proportion_idle++;
    }
    pc.pop_back();
  }
  mean_count = mean_count / observerCount;
  proportion_idle = proportion_idle / observerCount;

  double mean_sojourn = 0;
  while(st.size() > 0){
    mean_sojourn += st.back();
    st.pop_back();
  }
  mean_sojourn = mean_sojourn/departureCount;

  delete interarrival;
  delete interobserver;
  delete sizes;

  printf("%d,%d,%d,%d,%f,%f,%f,%f",
    K,
    arrivalCount,
    departureCount,
    observerCount,
    mean_count,
    mean_sojourn,
    proportion_idle,
    ((double)droppedCount / arrivalCount));
/*
  printf("Arrival Count: %d, Departure Count: %d, Observer Count: %d \t", arrivalCount, departureCount, observerCount);
  printf("E[N] = %G \t", mean_count);
  printf("E[T] = %G \t", mean_sojourn);
  printf("Pidle = %G \t", proportion_idle);
  printf("Ploss = %G \n", ((double)droppedCount / arrivalCount));
*/
}

void questionThree(){
//  printf(" \n BEGINNING SIMULATION OF M/M/1 QUEUE \n ");

  double simulationLength = 20000;
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

  double simulationLength = 20000;
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

  double simulationLength = 20000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 100000;
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

  double simulationLength = 20000;
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

  printf("\nQuestion #, Row, Simulation Time (s), K, ArrivalCount, DepartureCount, ObserverCount, E[N], E[T], Pidle, Ploss");

  printf("\nQuestion 3,,,,,,,,,");
  questionThree();
//  printf("\nQuestion 4,,,,,,,,,");
//  questionFour();
//  printf("\nQuestion 6A,,,,,,,,,");
//  questionSixA();
//  printf("\nQuestion 6B,,,,,,,,,");
//  questionSixB();

}
