CC = g++ 
#CFLAGS = -Wall -g 
LDFLAGS = -lm #-lpthread

all: Lab1.exe

RandomVariable.o : RandomVariable.cpp RandomVariable.h
	${CC} ${CFLAGS} -c RandomVariable.cpp

ExponentialVariable.o : ExponentialVariable.cpp ExponentialVariable.h
	${CC} ${CFLAGS} -c ExponentialVariable.cpp

Lab1.o : Lab1.cpp
	${CC} ${CFLAGS} -c Lab1.cpp

Event.o : Event.cpp
	${CC} ${CFLAGS} -c Event.cpp

Lab1.exe : RandomVariable.o Event.o ExponentialVariable.o  Lab1.o
	${CC} ${CFLAGS} RandomVariable.o ExponentialVariable.o Event.o  Lab1.o ${LDFLAGS} -o Lab1.exe
	make run

build : RandomVariable.o Event.o ExponentialVariable.o  Lab1.o
	${CC} ${CFLAGS} RandomVariable.o ExponentialVariable.o Event.o  Lab1.o ${LDFLAGS} -o Lab1.exe

clean :
	rm *.exe *.o

run :
	./Lab1.exe 1 > q1.csv
	./Lab1.exe 3 > q3.csv
	./Lab1.exe 4 > q4.csv
	./Lab1.exe 6 > q6.csv
	./Lab1.exe 7 > q6ba.csv
	./Lab1.exe 8 > q6bb.csv
	./Lab1.exe 9 > q6bc.csv
