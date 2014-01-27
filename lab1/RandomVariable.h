#ifndef RANDOM_VARIABLE_H
#define RANDOM_VARIABLE_H

class RandomVariable{
  public:
    //amount in % that variance and mean are allowed to drift
    int errorFactor;
    int size;
    double* values;
    RandomVariable(int);
    int getErrorFactor();
    bool verifyMean(double);
    bool verifyVariance(double);
    bool generateUniform(int);
    

};

#endif
