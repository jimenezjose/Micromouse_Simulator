BUILD_DIR=build

JSERIALCOMM_JAR = \
lib/jSerialComm-2.5.1.jar

JAR_SOURCES= \
$(JSERIALCOMM_JAR)

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

all:
	mkdir -p $(BUILD_DIR)
	javac -Xlint:unchecked -cp $(JAR_SOURCES) -g $(JAVA_SOURCES) -d $(BUILD_DIR)

clean:
	rm -rf $(BUILD_DIR)

#jar: $(all)
#	jar cvf MazeGUI.jar $(BUILD_DIR)
