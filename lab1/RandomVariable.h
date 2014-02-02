#ifndef RANDOM_VARIABLE_H
#define RANDOM_VARIABLE_H

class RandomVariable{
  public:
    int errorFactor;
    int size;
    double* values;
    RandomVariable(int);
    int getErrorFactor();
    bool verifyMean(double);
    bool verifyVariance(double);
    double getMean();
    double getVariance();
    bool generateUniform(int);
    

};

#endif
