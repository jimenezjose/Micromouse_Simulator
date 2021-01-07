################################################################################
 # Jose Jimenez
 # Brandon Cramer
 #
 #                 University of California, San Diego
 #                            IEEE Micromouse
 # File Name:   serialPrompt.sh
 # Description: (Inactive) Serial Prompt UI that sends serial data to a device.
################################################################################
#!/bin/bash

BLUE="\e[34m"
GREEN="\e[32m"
DEFAULT="\e[39m"
PROMPT="> "
DEVICE_FILE="/tmp/device.connected"
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

NEW_DEVICE="$DEVICE_DIR/`awk '/./{line=$0} END{print line}' $DEVICE_FILE`"
while true; do
	#NEW_DEVICE="$DEVICE_DIR/`awk '/./{line=$0} END{print line}' $DEVICE_FILE`"

	#if [[ "$NEW_DEVICE" != "$DEVICE" ]]; then
	#	# stop interacting with old device - abort 
	#	printf "Disconnected.\n"
	#	exit 0
	#fi

	# prompt user to interact with device
	printf "$PROMPT"
	read
	#if [[ "$REPLY" == "clear" ]]; then
	#	clear
	#	printf "Micromouse Periscope Prompt:\n\n"
	#	continue
	#fi
	printf "$REPLY\r\n" > "$DEVICE"
done
printf "${DEFAULT}"
