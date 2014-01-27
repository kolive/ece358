#include "RandomVariable.h"
#include "Logger.h"
#include <stdlib.h>     /* srand, rand */
#include <stdio.h>
#include <cmath>

#define LOGGING 0

RandomVariable::RandomVariable(int errorFactor){
  this->size = 0;
  this->errorFactor = errorFactor;

}

int RandomVariable::getErrorFactor(){
  return this->errorFactor;
}

bool RandomVariable::generateUniform(int size){
  this->values = new double[size];
  this->size = size;
  for(int i = 0; i < size; i++){
    this->values[i] = ((double)rand() / (RAND_MAX));
  }

  return (this->verifyMean(0.5) && this->verifyVariance(0.08333333333));

}

bool RandomVariable::verifyMean(double expectedMean){
  double mean = 0;
  double error = 0;

  for(int i = 0; i < this->size; i++){
    mean += this->values[i];
  }
  mean = mean / this->size;

  //calculate error
  error = fabs(((mean - expectedMean)/mean) * 100);

  if(error < errorFactor){

    Logger::log(LOGGING, (char *)"LOG: Calculated mean within %d percent of expected mean of %G \n", this->errorFactor, expectedMean);
    Logger::log(LOGGING, (char *) "LOG: Actual error: %G\% \n", error);

    return true;
  }else{

    Logger::log(LOGGING,(char *) "LOG: Calculated mean of %G did not match expected mean of %G. ", mean, expectedMean);
    Logger::log(LOGGING,(char *)"LOG: Actual error: %G\%\n", error);

    return false;
  }

}

bool RandomVariable::verifyVariance(double expectedVariance){
  double mean = 0;
  double variance = 0;
  double error = 0;

  for(int i = 0; i < this->size; i++){
    mean += this->values[i];
  }
  mean = mean / this->size;

  for(int i = 0; i < this->size; i++){
    double tmp = this->values[i] - mean;
    variance += (tmp*tmp);
  }

  variance = variance / this->size;

  error = fabs(((variance - expectedVariance)/variance) * 100);

  if(error < errorFactor){

   Logger::log(LOGGING, (char *) "LOG: Calculated variance within %d percent of expected variance of %G \n", this->errorFactor, expectedVariance);
   Logger::log(LOGGING, (char*) "LOG: Actual error: %G\% \n", error);
   return true;
  }else{

   Logger::log(LOGGING, (char *) "LOG: Calculated variance %G not within error range of expected variance of %G \n", variance, expectedVariance);
   Logger::log(LOGGING, (char*) "LOG: Actual error: %G\% \n", error);

   return false;
  }
}
