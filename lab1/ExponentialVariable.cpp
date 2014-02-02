#include "RandomVariable.h"
#include "ExponentialVariable.h"
#include <cmath>

bool ExponentialVariable::generateExponential(int size){
  double inverse;
  this->size = size;
  this->generateUniform(this->size);
  for(int i = 0; i < this->size; i++){
    inverse = -1 * log((1 -this->values[i]));
    inverse = inverse /this->lambda;
    this->values[i] = inverse;
  }
  return this->verifyMean((double)(1/this->lambda)) && this->verifyVariance((double)(1/(this->lambda*this->lambda)));
}

