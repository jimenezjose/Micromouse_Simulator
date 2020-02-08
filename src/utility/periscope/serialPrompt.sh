#!/bin/bash

BLUE="\e[34m"
GREEN="\e[32m"
DEFAULT="\e[39m"
PROMPT="> "
DEVICE_FILE="devices.connected"
DEVICE_DIR="/dev"
DIR=$(dirname "${0}")
DEVICE=""

# ensure working directory is in periscope
cd $DIR

# ensure file exists
touch $DEVICE_FILE

# ensure device is connected
DEVICE="$DEVICE_DIR/`awk '/./{line=$0} END{print line}' $DEVICE_FILE`"
if [[ ! -c $DEVICE ]] || [[ "$DEVICE" == "" ]]; then
	# device not found
	printf "Device \"$DEVICE\" not found.\n"
	exit 0
else
	PROMPT="${BLUE}$DEVICE:${GREEN} periscope $PROMPT"
fi


# serial prompt UI
clear
printf "${GREEN}"
printf "Micromouse Periscope Prompt:\n\n"

while true; do
	NEW_DEVICE="$DEVICE_DIR/`awk '/./{line=$0} END{print line}' $DEVICE_FILE`"

	if [[ "$NEW_DEVICE" != "$DEVICE" ]]; then
		# stop interacting with old device - abort 
		printf "Disconnected.\n"
		exit 0
	fi

	# prompt user to interact with device
	printf "$PROMPT"
	read
	printf "$REPLY" > "$DEVICE"
done
printf "${DEFAULT}"
