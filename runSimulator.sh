################################################################################
# Jose Jimenez
# Brandon Cramer
# Email: jjj023@ucsd.edu
# 
#                 University of California, San Diego
#                           IEEE Micromouse
#
# File Name: runMicromouseSimulator.sh
# Description: Execute Micromouse Simulator.
################################################################################
#!/bin/bash

make
cd build
java -cp ../lib/*:. MazeGUI $@
