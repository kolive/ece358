CC = g++ 
CFLAGS = -Wall -g 
LDFLAGS = -lm -lpthread

all: Lab1.exe

RandomVariable.o : RandomVariable.cpp RandomVariable.h
	${CC} ${CFLAGS} -c RandomVariable.cpp

ExponentialVariable.o : ExponentialVariable.cpp ExponentialVariable.h
	${CC} ${CFLAGS} -c ExponentialVariable.cpp

Lab1.o : Lab1.cpp
	${CC} ${CFLAGS} -c Lab1.cpp

Logger.o : Logger.cpp
	${CC} ${CFLAGS} -c Logger.cpp

PoissonVariable.o : PoissonVariable.cpp PoissonVariable.h
	${CC} ${CFLAGS} -c PoissonVariable.cpp

Event.o : Event.cpp
	${CC} ${CFLAGS} -c Event.cpp

Lab1.exe : Lab1.o RandomVariable.o PoissonVariable.o Event.o ExponentialVariable.o Logger.o
	${CC} ${CFLAGS} RandomVariable.o PoissonVariable.o ExponentialVariable.o Event.o Logger.o Lab1.o ${LDFLAGS} -o Lab1.exe

clean :
	rm *.exe *.o
