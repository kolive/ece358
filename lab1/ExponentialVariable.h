
class ExponentialVariable : public RandomVariable {
  public:
    ExponentialVariable(int errorFactor, double lambda) : RandomVariable(errorFactor){
      this->lambda = lambda;
    }
    
    double lambda;
    bool generateExponential(int);

};
