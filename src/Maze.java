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
 *              and random maze generation algorithms.
 */

import java.util.LinkedList;
import java.util.Random;

/**
 * Maze will handle the internal maze structures, and ensure a proper graph is
 * implemented. 
 */
class Maze {
  private static final int EVEN = 2;
  private static final String DIM_TOO_LARGE = "Cannot create maze. Dimension: %d, too large\n";
  private int dimension;
  private MazeNode[][] maze;


  /**
   * Creates a Maze object as a 2d array of MazeNodes.
   * @param dimension Side units for square maze.
   */
  public Maze( int dimension ) {
    /* dimension padding added to emulate maze walls */
    this.dimension = dimension;
    maze = new MazeNode[ dimension ][ dimension ];

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        maze[ row ][ column ] = new MazeNode( row, column );
      }
    }

  }

  /**
   * Creates a random maze using Kruskals Algorithm.
   * @return Nothing.
   */
  public void createRandomMaze() {                                                                    

    LinkedList<Pair<MazeNode, MazeNode>> walls = new LinkedList<Pair<MazeNode, MazeNode>>(); 
    Random rand = new Random();

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        if( row < maze.length - 1 ) {
          /* insert wall to the right of current cell */
	  walls.add( new Pair<>( maze[row][column], maze[row + 1][column] ) );
	}
	if( column < maze[0].length - 1 ) {
          /* insert wall below current cell */
	  walls.add( new Pair<>( maze[row][column], maze[row][column + 1] ) );
	}
      }
    }

    System.err.println( "Number of walls counted: " + walls.size() );
    int count = 0;

    while( walls.size() != 0 ) {
      /* choose a random wall from the maze */
      int randomIndex = rand.nextInt( walls.size() );
      Pair<MazeNode, MazeNode> node_pair = walls.get( randomIndex );
      MazeNode vertex_A = node_pair.first;
      MazeNode vertex_B = node_pair.second;

      if( inSameSet( vertex_A, vertex_B ) == false ) {
        /* combine disjoint sets and create new edge */
        union( vertex_A, vertex_B );
	addEdge( vertex_A, vertex_B );
	count++;
      }
      
      /* remove wall from wall list */
      walls.remove( randomIndex );
    }

    System.err.println( "number of walls taken down: " + count );
  
  }

  /**
   * Evaluates if a wall exists between two nodes in the maze.
   * @param vertex_A a node in the maze.
   * @param vertex_B a node in the maze.
   * @return true if there is no adjacent path from A to B.
   */
  public boolean wallBetween( MazeNode vertex_A, MazeNode vertex_B ) {
    if( vertex_A == null || vertex_B == null ) return false;

    MazeNode[] neighbors_of_A = { vertex_A.up, vertex_A.down, vertex_A.left, vertex_A.right };

    for( int index = 0; index < neighbors_of_A.length; index++ ) {
      if( neighbors_of_A[ index ] == vertex_B ) {
        /* There is a path directly connect A and B, therefore no wall */
        return false;
      }
    }

    return true;

  }

  /* Maze Generation Routines */

  /*
   * Goal: during the maze generation algorithm the parent node
   *       will always be the representative of a disjoint set.
   *       Otherwise, if parent is null, vertex is the representative.
   *
   * Reason: Constant time disjoint set evaluation. In other words,
   *         Knowing the set of vertex should be fast.
   *         --> inSameSet will be be called more than union
   *
   */

  /**
   * Combines two vertices in disjoint set into one set.
   * @param vertex_A Node in maze.
   * @param vertex_B Node in maze.
   * @return Nothing.
   */
  private void union( MazeNode vertex_A, MazeNode vertex_B ) {

    if( inSameSet(vertex_A, vertex_B) ) {
      /* no union needed */
      return;
    }

    /* disjont set representatives */
    MazeNode a_set = find( vertex_A );
    MazeNode b_set = find( vertex_B );

    /* a will be the subset of b */
    a_set.parent = b_set;
    b_set.addSubsetElement( a_set );

    while( a_set.subset_list.size() != 0 ) {
      /* migrates all vertices in set a to set b */
      MazeNode element = a_set.subset_list.pop();
      b_set.addSubsetElement( element );
    }
  }

  /**
   * Finds the set vertex belongs to.
   * @param vertex Node in maze.
   * @return The set to which vertex belongs to.
   */
  private MazeNode find( MazeNode vertex ) {
    return ( vertex.parent == null ) ? vertex : vertex.parent;
  }

  /* this must be fast. inSameSet will be called more than union. Therefore     */
  /* union will be a relatively expensive operation for constant time set check */

  /**
   * Constant time check for set equivalence.
   * @param vertex_A Node in maze.
   * @param vertex_B Node in maze.
   * @return Nothing.
   */
  private boolean inSameSet( MazeNode vertex_A, MazeNode vertex_B ) {
    return (find( vertex_A ) == find( vertex_B ));
  }
 
   /**
   * Class method to create an undirected edge between two vertices.
   * @param vertex_A A node in the maze.
   * @param vertex_B A node in the maze.
   * @return Nothing.
   */
  public void addEdge( MazeNode vertex_A, MazeNode vertex_B ) {
    if( vertex_A == null || vertex_B == null ) return;
    /* undirected edge added */
    vertex_A.addNeighbor( vertex_B );
    vertex_B.addNeighbor( vertex_A );
  }

  /* END OF MAZE GENERATION ROUTINES */

  /**
   * Checks if an index is out of the range of the maze.
   * @param index x or y coordinate in the square 2d maze.
   * @return true if and only if index does not exists in maze.
   */
  public boolean outOfBounds( int index ) {
    return ( index < 0 || index >= getDimension() );
  }

  /**
   * Accessor method for the maze internal structure.
   * @param x row cell in 2d array maze.
   * @param y column cell in 2d array maze.
   */
  public MazeNode at( int x, int y ) {
    if( outOfBounds(x) || outOfBounds(y)  ) {
      System.err.println( "Maze:at() out of bounds (" + x + ", " + y + ")" );
      return null;
    }
    /* Abstract the maze data structure */
    return maze[ x ][ y ];
  }

  /**
   * Gets the side dimension of created square maze.
   * @return number of square units on one side of square.
   */
  public int getDimension() {                                                                         
    return dimension;
  }

}
