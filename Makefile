BUILD_DIR=build
TEST_DIR=test

JAVA_SOURCES= \
src/MazeGUI.java \
src/Mouse.java \
src/Maze.java \
src/MazeNode.java \
src/Pair.java

JAVA_TEST= \
src/Mouse.java \
src/Maze.java \
src/MazeNode.java \
src/Pair.java \
src/TestMazeGUI.java 


all:
	javac -g $(JAVA_SOURCES) -d $(BUILD_DIR)

test:
	javac -g $(JAVA_TEST)$ -d $(TEST_DIR)

clean:
	rm -rf $(BUILD_DIR)
	rm -rf $(TEST_DIR)
