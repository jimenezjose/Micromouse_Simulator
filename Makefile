################################################################################
#
# Jose Jimenez
# Brandon Cramer
# Email: jjj023@ucsd.edu
# 
#                 University of California, San Diego
#                           IEEE Micromouse
#
# File Name: Makefile
################################################################################
BUILD_DIR=build

JSERIALCOMM_JAR = \
lib/jSerialComm-2.5.1.jar

COMMONS_LANG3_JAR = \
lib/commons-lang3-3.11.jar

JAR_SOURCES= \
$(JSERIALCOMM_JAR):$(COMMONS_LANG3_JAR)

JAVA_SOURCES= \
src/MazeGUI.java \
src/Mouse.java \
src/Maze.java \
src/MazeNode.java \
src/utility/Pair.java \
src/utility/PQNode.java \
src/utility/strings/ParsingStrings.java \
src/utility/comm/SerialRoute.java \
src/utility/comm/SerialRouteEvent.java 

CLASS_FILES=$(JAVA_SOURCES:%.java=%.class)

.SUFFIXES: .java .class

all:
	mkdir -p $(BUILD_DIR)
	javac -Xlint:unchecked -cp $(JAR_SOURCES) -g $(JAVA_SOURCES) -d $(BUILD_DIR)

clean:
	rm -rf $(BUILD_DIR)
