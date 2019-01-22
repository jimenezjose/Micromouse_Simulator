JAVA_SOURCES= \
MazeGUI.java \
Mouse.java

all:
	javac $(JAVA_SOURCES)
clean:
	rm *.class
