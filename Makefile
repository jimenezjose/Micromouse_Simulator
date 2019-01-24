BUILD_DIR=build

JAVA_SOURCES= \
src/MazeGUI.java \
src/Mouse.java

JAVA_CLASSES= \
build/MazeGUI.class \
build/Mouse.class \
build/Maze.class \
build/Node.class 

all:
	javac $(JAVA_SOURCES) -d $(BUILD_DIR)
clean:
	rm -rf $(BUILD_DIR)
