#!/bin/bash

GREEN="\e[32m"
DEFAULT="\e[39m"
LOGFILE="session.log"
DIR=$(dirname "${0}")

# ensure working directory is in periscope
cd $DIR

# clear log file
echo -n > $LOGFILE

# serial monitor UI
#stty -echo
clear
printf "${GREEN}"
printf "Micromouse Periscope Monitor\n\n"
tail -f $LOGFILE
printf "${DEFAULT}"
#stty echo
