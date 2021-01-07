################################################################################
 # Jose Jimenez
 # Brandon Cramer
 #
 #                 University of California, San Diego
 #                            IEEE Micromouse
 # File Name:   serialMonitor.sh
 # Description: Reads serial data from a log file and displays it to the console
################################################################################
#!/bin/bash

GREEN="\e[32m"
DEFAULT="\e[39m"
LOGFILE="/tmp/session.log"
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
fi

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
