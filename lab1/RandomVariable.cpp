#include "RandomVariable.h"
#include <stdlib.h>
#include <cmath>

RandomVariable::RandomVariable(int errorFactor){
  this->values = new double[1];
  this->size = 0;
  this->errorFactor = errorFactor;

}

int RandomVariable::getErrorFactor(){
  return this->errorFactor;
}


bool RandomVariable::generateUniform(int size){
  delete[] this->values;
  this->values = new double[size];
  this->size = size;
  for(int i = 0; i < size; i++){
    this->values[i] = ((double)rand() / (RAND_MAX));
  }

  return (this->verifyMean(0.5) && this->verifyVariance(0.08333333333));

}

double RandomVariable::getMean(){
  double mean = 0;

  for(int i = 0; i < this->size; i++){
    mean += this->values[i];
  }
  mean = mean / this->size;
  return mean;
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
    return true;
  }else{
    return false;
  }

}

double RandomVariable::getVariance(){
  double mean = 0;
  double variance = 0;
  double tmp = 0;

  for(int i = 0; i < this->size; i++){
    mean += this->values[i];
  }
  mean = mean / this->size;

  for(int i = 0; i < this->size; i++){
    tmp = this->values[i] - mean;
    variance += (tmp*tmp);
  }

  variance = variance / this->size;

  return variance;

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
   return true;
  }else{
   return false;
  }
}
