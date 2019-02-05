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
import java.awt.Point;
import java.util.PriorityQueue;

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
   * Create a MST from the maze with Dijkstra's Algorithm
   * @param startVertex Where to begin traversing maze graph.
   * @return Nothing.
   */
  public void dijkstra( MazeNode startVertex ) {
    if( startVertex == null ) {
      /* invlaid starting vertex */
      System.err.println( "Invalid starting vertex for Dijkstra." );
      return;
    }

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        /* set up initial environment in graph */
	MazeNode currentNode = maze[ row ][ column ];
	currentNode.distance = Integer.MAX_VALUE;
	currentNode.prev = null;
	currentNode.visited = false;
      }
    }

    PriorityQueue<PQNode<MazeNode>> pq = new PriorityQueue<PQNode<MazeNode>>();
  
    startVertex.distance = 0;
    pq.add( new PQNode<MazeNode>(0, startVertex) );

    while( pq.size() != 0 ) {
      PQNode<MazeNode> pq_node = pq.poll();
      MazeNode currentNode = pq_node.getData();
      int distance = currentNode.distance; 

      if( currentNode.visited == false ) {
        currentNode.visited = true;
        MazeNode[] edge_list = currentNode.getEdgeList();

	for( MazeNode neighbor : edge_list ) {
	  /* iterate through unvisited node's neighbors */
	  if( neighbor == null ) continue;
	  int weight = 1; 
          int cost = distance + weight;
	  if( cost < neighbor.distance ) {
	    /* new path with lower total cost encountered */
	    neighbor.distance = cost;
	    neighbor.prev = currentNode;
            pq.add( new PQNode<MazeNode>(cost, neighbor) );
	  }
	}
      }
    }

    System.err.println( "Dijkstra's Algorithm, Done." );

    
  }

  /**
   * Creates a random maze using Kruskals Algorithm.
   * @return Nothing.
   */
  public void createRandomMaze() {                                                                    
    
    final int MIN_DIM = 3;
    final int MAX_CYCLES = 5;
    LinkedList<Pair<MazeNode, MazeNode>> walls = new LinkedList<Pair<MazeNode, MazeNode>>(); 
    Random rand = new Random();

    if( getDimension() < MIN_DIM ) {
      /* invalid dimension for random maze generation */
      System.err.println( "Invalid Dimension for Maze Generation. Valid dimension > 3." );
      return; 
    }

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

    /* square center solution */
    LinkedList<Pair<MazeNode, MazeNode>> solutionEntry = new LinkedList<Pair<MazeNode, MazeNode>>();
    LinkedList<MazeNode> targetNodes = new LinkedList<MazeNode>(); 
    int lowerCenter = (getDimension() - 1) / 2;
    int upperCenter = getDimension() / 2;
    int count = 0;

    for( int row = lowerCenter; row <= upperCenter; row++ ) {
      for( int column = lowerCenter; column <= upperCenter; column++ ) {
        if( getDimension() % EVEN != 0 ) {
          /* singular solution cell */
          solutionEntry.add( new Pair<>( maze[ row ][ column ], maze[ row ][ column - 1 ] ) );
          solutionEntry.add( new Pair<>( maze[ row ][ column ], maze[ row ][ column + 1 ] ) );
          solutionEntry.add( new Pair<>( maze[ row ][ column ], maze[ row - 1 ][ column ] ) );
          solutionEntry.add( new Pair<>( maze[ row ][ column ], maze[ row + 1][ column ] ) );
	  break;
	}

        targetNodes.addLast( maze[ row ][ column ] );
	int dr = ( row == lowerCenter ) ? -1 : +1;
	int dc = ( column == lowerCenter ) ? -1 : + 1;
	
        /* quad-cell solution */
	solutionEntry.add( new Pair<>( maze[ row ][ column ], maze[ row + dr ][ column ] ) );
	solutionEntry.add( new Pair<>( maze[ row ][ column ], maze[ row ][ column + dc ] ) );
      }
    }

    System.err.println( "Total Walls: " + walls.size() );

    /* create entry point for target */
    int randomIndex = rand.nextInt( solutionEntry.size() );
    Pair<MazeNode, MazeNode> entry_pair = solutionEntry.get( randomIndex );
    union( entry_pair.first, entry_pair.second );
    addEdge( entry_pair.first, entry_pair.second );
    count++;

    /* remove solution entry candidates from walls list */
    while( solutionEntry.size() != 0 ) {
      walls.remove( solutionEntry.pop() );
    }

    /* combine target nodes into one meta node */
    for( int index = 0, init_size = targetNodes.size(); targetNodes.size() != 0; index++ ) {
      int sign = ( index < init_size / EVEN ) ? +1 : -1;
      int dr = ( (index + 1) % init_size < init_size / EVEN ) ? 0 : sign * 1;
      int dc = ( (index + 1) % init_size < init_size / EVEN ) ? sign * 1: 0;
      MazeNode target = targetNodes.removeFirst();
      MazeNode neighbor = maze[ target.x + dr ][ target.y + dc ];
      union( target, neighbor );
      addEdge( target, neighbor );
      walls.remove( new Pair<>( target, neighbor) );
      count++;
    }
 
    /* list of back edges */
    LinkedList<Pair<MazeNode, MazeNode>> cycleWalls = new LinkedList<Pair<MazeNode, MazeNode>>();

    /* random maze generation */
    while( walls.size() != 0 ) {
      /* choose a random wall from the maze */
      randomIndex = rand.nextInt( walls.size() );
      Pair<MazeNode, MazeNode> node_pair = walls.get( randomIndex );
      MazeNode vertex_A = node_pair.first;
      MazeNode vertex_B = node_pair.second;

      if( inSameSet( vertex_A, vertex_B ) == false ) {
        /* combine disjoint sets and create new edge */
        union( vertex_A, vertex_B );
	addEdge( vertex_A, vertex_B );
	count++;
      }
      else {
        cycleWalls.add( node_pair );
      }
      
      /* remove wall from wall list */
      walls.remove( randomIndex );
    }

    /* create multiple paths to solution */
    int numOfPaths = rand.nextInt( MAX_CYCLES );
    for( int index = 0; index < numOfPaths; index++ ) {
      randomIndex = rand.nextInt( cycleWalls.size() );
      Pair<MazeNode, MazeNode> node_pair = cycleWalls.get( randomIndex );
      MazeNode vertex_A = node_pair.first;
      MazeNode vertex_B = node_pair.second;
      
      /* add cycle : alternate path */
      addEdge( vertex_A, vertex_B );
      count++;
    }

    cycleWalls.clear();

    System.err.println( "Number of walls taken down: " + count);
    System.err.println( "Number of cycles: " + numOfPaths );
  
  }

  public void clear() {
    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        maze[ row ][ column ].clearData();
      }
    }
  }

  /**
   * Checks if a there exists an edge between two point in the maze.
   * @param alpha point in the maze.
   * @param beta second point in the maze.
   * @return true if an edge in alpha points to beta.
   */
  public boolean wallBetween( Point alpha, Point beta ) {
    return wallBetween( at(alpha.x, alpha.y), at(beta.x, beta.y) );
  }

  /**
   * Evaluates if a wall exists between two nodes in the maze.
   * @param vertex_A a node in the maze.
   * @param vertex_B a node in the maze.
   * @return true if there is no adjacent path from A to B.
   */
  public boolean wallBetween( MazeNode vertex_A, MazeNode vertex_B ) {
    if( vertex_A == null || vertex_B == null ) return false;

    MazeNode[] neighbors_of_A = vertex_A.getEdgeList();

    for( MazeNode neighbor : neighbors_of_A ) {
      if( neighbor == vertex_B ) {
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
