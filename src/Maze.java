/**
 *
 * Jose Jimenez
 * Alex Hu
 * Brandon Cramer
 * Chris Robles
 * Srinivas Venkatraman
 *
 *                 University of California, San Diego
 *                      IEEE Micromouse Team 2019
 *
 * File Name:   Maze.java
 * Description: Maze data structure that will handle minimum spanning tree 
 *              algorithms and random maze generation algorithms.
 */


/**
 * Maze will handle the internal maze structures, and ensure a proper graph is
 * implemented. 
 */
class Maze {
  private static final int EVEN = 2;
  private static final String DIM_TOO_LARGE = "Cannot create maze. Dimension: %d, too large\n";
  private int dimension;
  private Node[][] maze;


  /**
   * Creates a Maze object as a 2d array of MazeNodes.
   * @param dimension Side units for square maze.
   */
  public Maze( int dimension ) {
    /* dimension padding added to emulate maze walls */
    this.dimension = dimension;
    maze = new Node[ 2 * dimension - 1 ][ 2 * dimension - 1 ];

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        /* init maze with null indicating a maze wall */
        if( row % EVEN == 0 && column % EVEN == 0 ) {
          maze[ row ][ column ] = new Node( row, column );
        }
        else {
          maze[ row ][ column ] = null;
        }
      }
    }
  }

  /**
   * Creates a random maze using Kruskals Algorithm.
   * @return Nothing.
   */
  public void createRandomMaze() {                                                                    
    /* set up maze cells with disjoint cell classifiers */

    /*                                                                                                
    randomly chose wall (a, b)                                                                        
  
    if a not in set b                                                                                 
      union a and b into the same set
    */                                                                                                
  
  }
  

  /**
   * Gets the side dimension of created square maze.
   * @return number of square units on one side of square.
   */
  public int getDimension() {                                                                         
    return dimension;
  }

}
