#include <stdarg.h>
#include <stdio.h>
#include "Logger.h"
void Logger::log(int verbose, char * format, ...){
  va_list args;
  va_start(args, format);
  if(verbose) vprintf(format, args);
  va_end(args);
}
