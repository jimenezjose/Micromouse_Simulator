JAVA_SOURCES= \
src/MazeGUI.java \
src/Mouse.java

JAVA_CLASSES= \
build/MazeGUI.class \
build/Mouse.class \
build/Maze.class \
build/Node.class 

all:
	javac $(JAVA_SOURCES)
clean:
	rm $(JAVA_CLASSES)
