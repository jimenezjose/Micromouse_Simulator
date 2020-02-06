#!/bin/bash

GREEN="\e[32m"
DEFAULT="\e[39m"
PROMPT="> "
DIR=$(dirname "${0}")

# ensure working directory is in periscope
cd $DIR

# serial prompt UI
clear
printf "${GREEN}"
printf "Micromouse Periscope Prompt\n\n"

while true; do
	printf "$PROMPT"
	read
	echo ${REPLY}
done
printf "${DEFAULT}"
