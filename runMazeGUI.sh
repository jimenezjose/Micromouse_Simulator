#!/bin/bash

make
cd build
java -cp ../lib/*:. MazeGUI $@
