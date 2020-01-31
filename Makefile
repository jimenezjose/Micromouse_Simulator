BUILD_DIR=build
TEST_DIR=test

JAVA_SOURCES= \
src/MazeGUI.java \
src/Mouse.java \
src/Maze.java \
src/MazeNode.java \
src/utility/Pair.java \
src/utility/PQNode.java \
src/strings/ParsingStrings.java

JAVA_TEST= \
src/tester/TestMazeGUI.java \
src/tester/Mouse.java \
src/Maze.java \
src/MazeNode.java \
src/utility/Pair.java \
src/utility/PQNode.java \
src/strings/ParsingStrings.java

all:
	mkdir -p $(BUILD_DIR)
	javac -Xlint:unchecked -g $(JAVA_SOURCES) -d $(BUILD_DIR)

#test: clean
#	mkdir -p $(TEST_DIR)
#	javac -Xlint:unchecked -g $(JAVA_TEST) -d $(TEST_DIR)

clean:
	rm -rf $(BUILD_DIR)
	rm -rf $(TEST_DIR)
