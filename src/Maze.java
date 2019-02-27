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
import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* import local packages */
//import utility.Pair;
//import utility.PQNode;

/**
 * Maze will handle the internal maze structures, and ensure a proper graph is
 * implemented. 
 */
class Maze implements Iterable<MazeNode> {
  private static final int EVEN = 2;
  private final int dimension;
  private int max_cycles;
  private MazeNode[][] maze;
  private LinkedList<MazeNode> dijkstraPath = new LinkedList<MazeNode>();
  private LinkedList<MazeNode> dfsPath = new LinkedList<MazeNode>();

  /**
   * Creates a Maze object as a 2d array of MazeNodes.
   * @param dimension Side units for square maze.
   */
  public Maze( int dimension ) {
    /* dimension padding added to emulate maze walls */
    this.dimension = dimension;
    this.max_cycles = 0;
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
  public void dijkstra( MazeNode startVertex, MazeNode endVertex ) {
    if( startVertex == null || endVertex == null ) {
      /* invlaid starting vertex */
      System.err.println( "Invalid starting or ending vertex for Dijkstra." );
      return;
    }

    for( MazeNode node : this ) {
      /* set up initial environment in graph */
      node.setDistance( Integer.MAX_VALUE );
      node.setPrev( null );
      node.setVisited( false );
    }

    PriorityQueue<PQNode<MazeNode>> pq = new PriorityQueue<PQNode<MazeNode>>();
  
    startVertex.setDistance( 0 );
    pq.add( new PQNode<MazeNode>(0, startVertex) );

    while( pq.size() != 0 ) {
      PQNode<MazeNode> pq_node = pq.poll();
      MazeNode currentNode = pq_node.getData();
      int distance = currentNode.getDistance(); 

      if( currentNode.getVisited() == false ) {
        /* only visit traverse currentNode's edges exactly once */
        currentNode.setVisited( true );
        MazeNode[] edge_list = currentNode.getEdgeList();

	for( MazeNode neighbor : edge_list ) {
	  /* iterate through unvisited node's neighbors */
	  if( neighbor == null ) continue;
	  int weight = 1; 
          int cost = distance + weight;
	  if( cost < neighbor.getDistance() ) {
	    /* new path with lower total cost encountered */
	    neighbor.setDistance( cost );
	    neighbor.setPrev( currentNode );
            pq.add( new PQNode<MazeNode>(cost, neighbor) );
	  }
	}
      }
    }

    /* erase previous dijkstra path */
    dijkstraPath.clear();
    Stack<MazeNode> pathStack = new Stack<MazeNode>();
    MazeNode currentNode = endVertex;

    while( currentNode.getPrev() != null ) {
      /* traversing optimal path backwards */
      pathStack.push( currentNode );
      currentNode = currentNode.getPrev();
    }
    /* pushing starting vertex */
    pathStack.push( currentNode );

    while( !pathStack.empty() ) {
      /* dijkstra path : startVertex to endVertex */
      currentNode = pathStack.pop();
      dijkstraPath.addLast( currentNode );
    }

    System.err.println( "Dijkstra's Algorithm, Done." );
  }

  /**
   * DFS algorithm to find a solution to the maze.
   * @param currentVertex traversing node in maze.
   * @param endVertex target node to end DFS.
   * @return Nothing.
   */
  public void dfs( MazeNode startVertex, MazeNode endVertex ) {
    if( startVertex == null || endVertex == null ) {
      /* invalid input */
      System.err.println( "Maze.dfs: invalid vertices" );
      return;
    }

    for( MazeNode node : this ) {
      /* set up initial conditions for dfs */
      node.setVisited( false );
    }

    dfsPath.clear();
    dfsHelper( startVertex, endVertex );
    System.err.println( "DFS Algorithm, Done." );
  }

  /**
   * Recursive definition of DFS.
   * @param currentVertex traversing node in maze.
   * @param endVertex target node to end DFS.
   * @return Nothing.
   */
  private void dfsHelper( MazeNode currentVertex, MazeNode endVertex ) {
    currentVertex.setVisited( true );

    if( currentVertex == endVertex ) {
      /* base case */
      dfsPath.addFirst( currentVertex );
      return;
    }

    MazeNode[] neighbor_list = currentVertex.getEdgeList();

    for( MazeNode neighbor : neighbor_list ) {
      if( endVertex.getVisited() ) break;
      if( neighbor != null && neighbor.getVisited() == false ) {
        /* visit every node exactly once */
	dfsHelper( neighbor, endVertex );
      }
    }

    if( endVertex.getVisited() ) {
      /* popping from RTS stack -- save sequence of nodes */
      dfsPath.addFirst( currentVertex );
    }
  }

  /**
   * Path optimization with regards to the mouse's ability to move in 
   * diagonal directions.
   * @param path linked list path to be optimized.
   * @return Nothing.
   */
  public LinkedList<MazeNode> optimize( LinkedList<MazeNode> path ) {
    LinkedList<MazeNode> bestPath = new LinkedList<MazeNode>();
    MazeNode startVertex = path.peekFirst();
    MazeNode endVertex = path.peekLast();

    bestPath.addLast( startVertex );

    while( path.size() > 1 ) {
      /* smoothen sharp turns by averaging direction */
      MazeNode currentNode = path.removeFirst();
      MazeNode nextNode = path.peekFirst();
      double x_bar = 0.5 * ( currentNode.x + nextNode.x );
      double y_bar = 0.5 * ( currentNode.y + nextNode.y );
      bestPath.addLast( new MazeNode(x_bar, y_bar) );
    }

    bestPath.addLast( endVertex );
    return bestPath;
  }

  /**
   * TODO
   */
  public void createRandomMaze( int max_cycles ) {
    this.max_cycles = max_cycles;
    createRandomMaze();
  }

  /**
   * Creates a random maze using Kruskals Algorithm.
   * @return Nothing.
   */
  public void createRandomMaze() {                                                                    
    final int MIN_DIM = 3;
    final int MAX_CYCLES = max_cycles;
    ArrayList<Pair<MazeNode, MazeNode>> walls = new ArrayList<Pair<MazeNode, MazeNode>>( getDimension() * getDimension() );
    Random rand = new Random();

    if( getDimension() < MIN_DIM ) {
      /* invalid dimension for random maze generation */
      System.err.println( "Invalid Dimension for Maze Generation. Valid dimension >= 3." );
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
    long prevMillis = System.currentTimeMillis();

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

    /* combine target nodes into one meta node (solution cell(s) )*/
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
    ArrayList<Pair<MazeNode, MazeNode>> cycleWalls = new ArrayList<Pair<MazeNode, MazeNode>>( getDimension() * getDimension() );

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
        /* walls that border cells in the same set */
        cycleWalls.add( node_pair );
      }
      
      /* remove wall from wall list */
      walls.remove( randomIndex );
    }

    /* create multiple paths to solution */
    int numOfPaths = ( MAX_CYCLES == 0 ) ? 0 : rand.nextInt( MAX_CYCLES ) + 1;
    for( int index = 0; cycleWalls.size() != 0 && index < numOfPaths; index++ ) {
      randomIndex = rand.nextInt( cycleWalls.size() );
      Pair<MazeNode, MazeNode> node_pair = cycleWalls.get( randomIndex );
      MazeNode vertex_A = node_pair.first;
      MazeNode vertex_B = node_pair.second;
      
      /* add cycle : alternate path */
      addEdge( vertex_A, vertex_B );
      count++;

      /* remove back edge picked */
      cycleWalls.remove( randomIndex );
    }

    cycleWalls.clear();
    System.err.println( "Number of walls taken down: " + count);
    System.err.println( "Number of cycles: " + numOfPaths );

    System.err.println( "Time taken for Maze Generation: " + (System.currentTimeMillis() - prevMillis) / 1000.0 + " sec" );
  }

  /**
   * Clears all data of each node in maze excepty for its coordinates.
   * @return Nothing.
   */
  public void clear() {
    for( MazeNode node : this ) {
      /* clear data for all nodes in maze */
      node.clearData();
    }
    dfsPath.clear();
    dijkstraPath.clear();
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

  /**
   * Combines two vertices in disjoint set into one set via union by rank.
   * @param vertex_A Node in maze.
   * @param vertex_B Node in maze.
   * @return Nothing.
   */
  private void union( MazeNode vertex_A, MazeNode vertex_B ) {
    /* disjont set representatives */
    MazeNode a_set = find( vertex_A );
    MazeNode b_set = find( vertex_B );

    if( a_set == b_set ) {
      /* no union needed - same set */
      return;
    }

    if( a_set.rank > b_set.rank ) {
      /* a rank is greater than rank of b */
      b_set.parent = a_set;
    }
    else {
      /* a_set is a smaller tree than b_set */
      a_set.parent = b_set;

      if( a_set.rank == b_set.rank ) {
        /* union of trees of equal height */
	b_set.rank = a_set.rank + 1;
      }
    }
  }

  /**
   * Finds the set vertex belongs to while doing a path compression.
   * @param vertex Node in maze.
   * @return The set to which vertex belongs to.
   */
  private MazeNode find( MazeNode vertex ) {
    Stack<MazeNode> stack = new Stack<MazeNode>();
    stack.push( vertex );

    while( vertex.parent != null ) {
      /* find set representative */
      vertex = vertex.parent;
      stack.push( vertex );
    }

    /* set representative found */
    MazeNode root = stack.pop();

    while( !stack.empty() ) {
      /* path compression */
      vertex = stack.pop();
      vertex.parent = root;
      vertex.rank = 0;
    }
    return root;
  }

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

  public void removeEdge( MazeNode vertex_A, MazeNode vertex_B ) {
    if( vertex_A == null || vertex_B == null ) return;
    vertex_A.removeNeighbor( vertex_B );
    vertex_B.removeNeighbor( vertex_A );
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
   * TODO
   */
  public void clearWalls() {
    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        /* create empty maze with no walls */
        MazeNode currentNode = maze[ row ][ column ];
	MazeNode up, down, left, right;
        up    = ( !outOfBounds(row - 1) ) ? maze[ row - 1 ][ column ] : null;
        down  = ( !outOfBounds(row + 1) ) ? maze[ row + 1 ][ column ] : null;
        left  = ( !outOfBounds(column - 1) ) ? maze[ row ][ column - 1 ] : null;
        right = ( !outOfBounds(column + 1) ) ? maze[ row ][ column + 1 ] : null;
	/* create edges for current node */
        MazeNode[] neighbor_list = { up, down, left, right };
	for( MazeNode neighbor : neighbor_list ) {
          addEdge( currentNode, neighbor );
	}
      }
    }
  }

  /**
   * Maze abstraction with walls alias...TODO
   */
  public void addWall( MazeNode vertex_A, MazeNode vertex_B ) {
    removeEdge( vertex_A, vertex_B );
  }

  /**
   * Gets the side dimension of created square maze.
   * @return number of square units on one side of square.
   */
  public int getDimension() {                                                                         
    return dimension;
  }

  /**
   * Gets a deep copy of dijkstraPath
   * @return deep copy of dijkstraPath
   */
  public LinkedList<MazeNode> getDijkstraPath() {
    return new LinkedList<MazeNode>( dijkstraPath );
  }

  /**
   * Gets a deep copy of dfsPath
   * @return deep copt of dfsPath
   */
  public LinkedList<MazeNode> getDFSPath() {
    return new LinkedList<MazeNode>( dfsPath );
  }

  public int getMaxCycles() {
    return max_cycles;
  }

  /**
   * TODO
   */
  public Iterator<MazeNode> iterator() {
    return new MazeIterator();
  }


  /**
   * TODO
   */
  private class MazeIterator implements Iterator<MazeNode> {

    private int current_row;
    private int current_column;

    /**
     * TODO
     */
    public MazeIterator() {
      this.current_row = 0;
      this.current_column = 0;
    }

    /**
     * TODO
     */
    @Override
    public boolean hasNext() {
      if( current_column == maze[0].length ) {
        /* wrapping around 2d row */
        if( current_row == maze.length ) {
	  /* last row index has been reached. cannot increment */
          return false;
        }
      }
      return (current_row < maze.length && current_column < maze[0].length);
    }

    /**
     * TODO
     */
    @Override
    public MazeNode next() {
        if( !hasNext() ) {
          throw new NoSuchElementException();
        }
      
        MazeNode nextNode = maze[ current_row ][ current_column ];

        if( current_column + 1 == maze[0].length ) {
	  /* wrapping around 2d array */
          current_column = 0;
          current_row++;
        }
        else {
	  /* typical 1d array increment */
          current_column++;
        }
        return nextNode; 
      }

    /**
     * TODO
     */
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
  }
}
