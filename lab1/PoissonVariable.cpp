#include "RandomVariable.h"
#include "PoissonVariable.h"
#include <cmath>

bool PoissonVariable::generatePoisson(int size){
  this->size = size;
  this->generateUniform(this->size);
  int j;
  double p;
  double f;
  for(int i = 0; i < this->size; i++){
    j = 0;
    p = exp((-1 * this->lambda));
    f = p;
    while( this->values[i] > f ){
      p = (this->lambda * p) / (j + 1);
      f += p;
      j++;
    }
    this->values[i] = j;
  }

  return this->verifyMean(this->lambda) && this->verifyVariance(this->lambda);
}
