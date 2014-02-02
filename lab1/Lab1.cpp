#include <stdio.h>
#include "RandomVariable.h"
#include "ExponentialVariable.h"
#include "Event.h"
#include "EventType.h"
#include <queue>
#include <cmath>

typedef std::queue<Event*> event_queue;
typedef std::queue<int> count_sample;
typedef std::queue<double> sojourn_times;

void simulate(double T, double alpha, double lambda, double L, double C, double K){
  event_queue a;
  event_queue o;
  event_queue d;
  count_sample pc;
  sojourn_times st;

  ExponentialVariable * interarrival = new ExponentialVariable(15, (lambda));
  if(!interarrival->generateExponential(20000)){
    return; //error
  }

  ExponentialVariable * interobserver = new ExponentialVariable(15, (alpha));
  if(!interobserver->generateExponential(20000)){
    return; //error
  }

  ExponentialVariable * sizes = new ExponentialVariable(15, 1/L);
  if(!sizes->generateExponential(20000)){
    return; //error
  }
  //generate observers
  double currentTime = 0;
  int n = 0;
  while( currentTime < T ) {
    currentTime += interobserver->values[n];
    //if n hass rolled over to 0, need to generate a new distribution
    n = (n+1) % 20000;
    if(n == 0)
      if(!interobserver->generateExponential(20000)){
        return; //error in generation
      }
    o.push( new Event(currentTime, OBSERVER) );

  }

  n = 0;
  currentTime = 0;
  //generate arrivals
  while(currentTime < T){
    currentTime += interarrival->values[n];
    //if n hass rolled over to 0, need to generate a new distribution
    n = (n+1) % 20000;
    if(n == 0)
      if(!interarrival->generateExponential(20000)){
        return; //error in generation
      }
    a.push( new Event(currentTime, ARRIVAL) );
  }

  //begin simulation
  currentTime = 0;
  n = 0;
  int droppedCount = 0;
  int arrivalCount = 0;
  int observerCount = 0;
  int departureCount = 0;
  while( a.size() > 0 || d.size() > 0 || o.size() > 0){
    Event * e;
    if(a.size() > 0 && d.size() > 0 && o.size() > 0){
      e = Event::minEvent(a.front(), d.front(), o.front());
    }else if(a.size() > 0 && d.size() > 0){
      e = Event::minEvent(a.front(), d.front());
    }else if(a.size() > 0 && o.size() > 0){
      e = Event::minEvent(a.front(), o.front());
    }else if(o.size() > 0 && d.size() > 0){
      e = Event::minEvent(o.front(), d.front());
    }else if(o.size() > 0){
      e = o.front();
    }else if(a.size() > 0){
      e = a.front();
    }else{
      e = d.front();
    }

    switch(e->getType()){
      case ARRIVAL:
        a.pop();
        if((int)d.size() == K){
          //dropped packet
          droppedCount++;
        }else{
          arrivalCount++;
          //new arrival
          double processingTime = sizes->values[n] / C;
          Event * dep;
          if(d.size() > 0){
            dep = new Event((d.back()->getArrivalTime() + processingTime), DEPARTURE);
          }else{
            dep = new Event((e->getArrivalTime() + processingTime), DEPARTURE);
          }
          d.push(dep);
          //std::sort(d.begin(), d.end(), Event::EventPredicate2);
          st.push((dep->getArrivalTime() - e->getArrivalTime()));

          //if n hass rolled over to 0, need to generate a new distribution
          n = (n+1) % 20000;
          if(n == 0)
            if(!sizes->generateExponential(20000)){
              return; //error in generation
            }

        }
        break;
      case DEPARTURE:
        d.pop();
        departureCount++;
        break;
      case OBSERVER:
        o.pop();
        observerCount++;
        pc.push((int)d.size());
        break;
      default:
        break;
    }

    delete e;

  }

  double mean_count = 0;
  double proportion_idle = 0;
  while(pc.size() > 0){
    mean_count += pc.front();
    if(pc.front() == 0){
      proportion_idle++;
    }
    pc.pop();
  }
  mean_count = mean_count / observerCount;
  proportion_idle = proportion_idle / observerCount;

  double mean_sojourn = 0;
  while(st.size() > 0){
    mean_sojourn += st.front();
    st.pop();
  }
  mean_sojourn = mean_sojourn/departureCount;

  delete[] interarrival->values;
  delete[] interobserver->values;
  delete[] sizes->values;
  delete interarrival;
  delete interobserver;
  delete sizes;

  printf("%f,%d,%d,%d,%d,%f,%f,%f,%f",
    K,
    arrivalCount,
    droppedCount,
    departureCount,
    observerCount,
    mean_count,
    mean_sojourn,
    proportion_idle,
    ((double)droppedCount / (arrivalCount+droppedCount)));
}

void questionOne(){
  double * values = new double[1000];
  double inverse = 0;
  double mean = 0;
  double variance = 0;
  double tmp = 0;

  //generate 1000 values matching Exponential rv with lamba = 75
  for(int i = 0; i < 1000; i++){
    values[i] = ((double)rand() / (RAND_MAX));
    inverse = -1 * log((1 - values[i]));
    inverse = inverse / 75;
    values[i] = inverse;
  }

  //calculate mean
  for(int i = 0; i < 1000; i++){
    mean += values[i];
  }
  mean = mean / 1000;

  //calculate variance
  for(int i = 0; i < 1000; i++){
    tmp = values[i] - mean;
    variance += (tmp*tmp);
  }

  variance = variance / 1000;

  printf("\nERV Generated: \n Mean of: %f, \n Variance of: %f, \n", mean, variance);
  printf("Expected Mean: %f, \n Expected Variance: %f", (1/75.0), (1/(75.0*75.0)));

}

void questionThree(){
  double simulationLength = 15000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 0;

  for(row = 0.25; row <= 0.95; row += 0.1){
    lambda = (linkRate * row) / packetSize;
    alpha = 2*lambda;
    printf("\n,%f,%f,", row, simulationLength);
    simulate(simulationLength, alpha, lambda, packetSize, linkRate, -1);
    printf("\n,%f,%f,",row,(simulationLength*2));
    simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, -1);
  }
}

void questionFour(){
  double simulationLength = 15000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 1.2;

  lambda = (linkRate * row) / packetSize;
  alpha = 2*lambda;
  printf("\n,%f,%f,", row, simulationLength);
  simulate(simulationLength, alpha, lambda, packetSize, linkRate, -1);
  printf("\n,%f,%f,",row,(simulationLength*2));
  simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, -1);
}

void questionSixA(){
  double simulationLength = 15000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 100000;
  double row = 0.0;
  double k[] = {5, 10, 40, -1};

  for(int i = 0; i < 4; i++){
    for(row = 0.5; row < 1.6; row += 0.1){
      lambda = (linkRate * row) / packetSize;
      alpha = 2*lambda;
      printf("\n,%f,%f,", row, simulationLength);
      simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
      printf("\n,%f,%f,",row,(simulationLength*2));
      simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
   }
 }
}

void questionSixB(int queueSize){
  double simulationLength = 15000;
  double alpha = 0;
  double lambda = 0;
  double packetSize = 12000;
  double linkRate = 1000000;
  double row = 0.0;
  double k[] = {5, 10, 40};
  int i = queueSize;
  for(row = 0.4; row < 2; row += 0.1){
    lambda = (linkRate * row) / packetSize;
    alpha = 2*lambda;
    printf("\n,%f,%f,", row, simulationLength);
    simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
    printf("\n,%f,%f,",row,(simulationLength*2));
    simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
  }

  for(row = 2; row < 5; row += 0.2){
    lambda = (linkRate * row) / packetSize;
    alpha = 2*lambda;
    printf("\n,%f,%f,", row, simulationLength);
    simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
    printf("\n,%f,%f,",row,(simulationLength*2));
    simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
  }

  for(row = 5; row <= 10; row += 0.4){
    lambda = (linkRate * row) / packetSize;
    alpha = lambda*2;
    printf("\n,%f,%f,", row, simulationLength);
    simulate(simulationLength, alpha, lambda, packetSize, linkRate, k[i]);
    printf("\n,%f,%f,",row,(simulationLength*2));
    simulate((2*simulationLength), alpha, lambda, packetSize, linkRate, k[i]);
  }
}
int main(int argc, char *argv[]){

  printf("\n *********************************************** \n");
  printf("  ECE 358 - Lab 1: Queue Simulator \n");
  printf("  Kyle Olive - 20378286 - kolive \n");
  printf("  January 14th 2014 \n");
  printf("  Valid args: -1,1,3,4,6,7,8,9 \n");
  printf(" ************************************************ \n");

  if(argc <= 1){
    printf("\n Invalid Arguments. \n Usage: %s [QuestionNumber] \n", argv[0]);
    exit(1);
  }

  if(atoi(argv[1]) == 1){
    printf("\nQuestion 1,,,,,,,,,");
    questionOne();
  }

  printf("\nQuestion #, Row, Simulation Time (s), K, Accepted Arrival Count, Dropped Arrival Count, Departure Count, Observer Count, E[N], E[T], Proportion of Time Idle, Proportion of Dropped Arrivals");

  if(atoi(argv[1]) == -1){
    printf("\nQuestion 3,,,,,,,,,,");
    questionThree();
    printf("\nQuestion 4,,,,,,,,,,");
    questionFour();
    printf("\nQuestion 6A,,,,,,,,,,");
    questionSixA();
    printf("\nQuestion 6Ba,,,,,,,,,,");
    questionSixB(0);
    printf("\nQuestion 6Bb,,,,,,,,,,");
    questionSixB(1);
    printf("\nQuestion 6Bc,,,,,,,,,,");
    questionSixB(2);
  }


  if(atoi(argv[1]) == 3){
    printf("\nQuestion 3,,,,,,,,,,");
    questionThree();
  }

  if(atoi(argv[1]) == 4){
    printf("\nQuestion 4,,,,,,,,,,");
    questionFour();
  }

  if(atoi(argv[1]) == 6){
    printf("\nQuestion 6A,,,,,,,,,,");
    questionSixA();
  }

  if(atoi(argv[1]) == 7){
    printf("\nQuestion 6Ba,,,,,,,,,,");
    questionSixB(0);
  }

  if(atoi(argv[1]) == 8){
    printf("\nQuestion 6Bb,,,,,,,,,,");
    questionSixB(1);
  }

  if(atoi(argv[1]) == 9){
    printf("\nQuestion 6Bc,,,,,,,,,,");
    questionSixB(2);
  }
}
