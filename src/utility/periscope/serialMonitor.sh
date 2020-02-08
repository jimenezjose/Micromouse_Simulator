#!/bin/bash

GREEN="\e[32m"
DEFAULT="\e[39m"
LOGFILE="session.log"
DEVICE_FILE="devices.connected"
DEVICE_DIR="/dev"
DIR=$(dirname "${0}")

DEVICE=""

# ensure file exists
while [[ ! -f $DEVICE_FILE ]]; do
        sleep 5
done

# ensure device is connected
DEVICE="$DEVICE_DIR/`awk '/./{line=$0} END{print line}' $DEVICE_FILE`"

echo "tail -f $DEVICE_FILE"
tail -f $DEVICE_FILE

if [[ ! -f $DEVICE ]] || [[ "$DEVICE" == "" ]]; then
        # device not found
	printf "Device \"$DEVICE\" not found.\n"
        exit 0
fi

# ensure working directory is in periscope
cd $DIR

# clear log file
echo -n > $LOGFILE

# serial monitor UI
stty -echo
clear
printf "${DEFAULT}Connected: $DEVICE${GREEN}\n\n"
printf "${GREEN}"
printf "Micromouse Periscope Live Stream Monitor:\n\n"
tail -f $LOGFILE
printf "${DEFAULT}"
stty echo
