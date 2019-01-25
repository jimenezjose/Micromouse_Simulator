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

    while( walls.size() != 0 ) {
      int randomIndex = rand.nextInt( walls.size() );
      Pair<MazeNode, MazeNode> node_pair = walls.get( randomIndex );
      MazeNode vertex_A = node_pair.first;
      MazeNode vertex_B = node_pair.second;

      if( inSameSet( vertex_A, vertex_B ) == false ) {
        /* combine disjoint sets and create new edge */
        union( vertex_A, vertex_B );
	addEdge( vertex_A, vertex_B );
      }
    }
  
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
  public void union( MazeNode vertex_A, MazeNode vertex_B ) {

    if( inSameSet(vertex_A, vertex_B) ) {
      /* no union needed */
      return;
    }

    /* disjont set representatives */
    MazeNode a_set = find( vertex_A );
    MazeNode b_set = find( vertex_B );

    /* a will be the subset of b */
    a_set.parent = b_set;

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
  public MazeNode find( MazeNode vertex ) {
    return ( vertex.parent == null ) ? vertex : vertex.parent;
  }

  /* this must be fast. inSame set will be called more than union. */
  /* therefore union will be an expensive operation for constant time set check */

  /**
   * Constant time check for set equivalence.
   * @param vertex_A Node in maze.
   * @param vertex_B Node in maze.
   * @return Nothing.
   */
  public boolean inSameSet( MazeNode vertex_A, MazeNode vertex_B ) {
    return (find( vertex_A ) == find( vertex_B ));
  }
 
   /**
   * Class method to create an undirected edge between two vertices.
   * @param vertex_A A node in the maze.
   * @param vertex_B A node in the maze.
   * @return Nothing.
   */
  public static void addEdge( MazeNode vertex_A, MazeNode vertex_B ) {
    vertex_A.addNeighbor( vertex_B );
    vertex_B.addNeighbor( vertex_A );
  }

  /**
   * Gets the side dimension of created square maze.
   * @return number of square units on one side of square.
   */
  public int getDimension() {                                                                         
    return dimension;
  }

}
