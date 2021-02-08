################################################################################
 # Jose Jimenez
 # Brandon Cramer
 # Email: jjj023@ucsd.edu
 #
 #                 University of California, San Diego
 #                            IEEE Micromouse
 #
 # File Name:   grimReaper.sh
 # Description: Silently waits until the device is disconnected to kill 
 #              the parent periscope process.
################################################################################
#!/bin/bash

DEVICE_DIR="/dev"
DEVICE_FILE="/tmp/device_connected.log"
DEVICE=`awk '/./{line=$0} END{print line}' $DEVICE_FILE`

while [ "$DEVICE" != "null" ]; do
	DEVICE=`awk '/./{line=$0} END{print line}' $DEVICE_FILE`
done

PID=$1
kill -9 $PID
