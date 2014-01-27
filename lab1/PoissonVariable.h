
class PoissonVariable : public RandomVariable {
  public:
    PoissonVariable(int errorFactor, double lambda) : RandomVariable(errorFactor){
      this->lambda = lambda;
    }
    
    double lambda;
    bool generatePoisson(int);

};
