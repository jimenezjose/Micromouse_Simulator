import java.io.*;

class TestMazeGUI {

  public void testMazeNode() {

  }

  public void testMaze() {

  }

  public void testCreateRandomMaze() {
    Maze maze = new Maze( 3 );
    maze.createRandomMaze();
  }

  public static void main( String[] args ) {
    TestMazeGUI maze = new TestMazeGUI();

    maze.testCreateRandomMaze();
    
  }

}
