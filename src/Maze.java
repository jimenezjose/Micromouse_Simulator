/**
 * Jose Jimenez-Olivas 
 * Brandon Cramer
 * Email: jjj023@ucsd.edu
 * 
 *                 University of California, San Diego
 *                           IEEE Micromouse
 *
 * File Name:   Maze.java
 * Description: Maze data structure that will handle minimum spanning tree 
 *              and random maze generation algorithms
 * Source of Help: A wonderful blog about maze generation algorithms from
 *                 Jamis Buck:
 *                 weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap
 */

import java.io.PrintStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Maze will handle the internal maze structures, and ensure a proper graph is
 * implemented. 
 */
class Maze implements Iterable<MazeNode> {
  private static final int EVEN = 2;
  private final int dimension;
  private int non_tree_edges;
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
    this.non_tree_edges = 0;
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
        LinkedList<MazeNode> neighbor_list = currentNode.getNeighborList();

	for( MazeNode neighbor : neighbor_list ) {
	  /* iterate through unvisited node's neighbors */
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

    LinkedList<MazeNode> neighbor_list = currentVertex.getNeighborList();

    for( MazeNode neighbor : neighbor_list ) {
      if( endVertex.getVisited() ) break;
      if( neighbor.getVisited() == false ) {
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
      double row_bar = 0.5 * ( currentNode.row + nextNode.row );
      double column_bar = 0.5 * ( currentNode.column + nextNode.column );
      bestPath.addLast( new MazeNode(row_bar, column_bar) );
    }

    bestPath.addLast( endVertex );
    return bestPath;
  }

  /**
   * Creates a new random maze and saves maze to datafile.
   * @param non_tree_edges Number of non tree cycles present in MST. 
   * @param datafile Encoded maze data file.
   * @return Nothing.
   */
  public void createRandomMaze( int non_tree_edges, File datafile ) {
    createRandomMaze( non_tree_edges );
    saveMaze( datafile );
  }
 
  /**
   * Creates random maze and saves maze to datafile.
   * @param datafile Encoded maze data file.
   * @return Nothing.
   */
  public void createRandomMaze( File datafile ) {
    createRandomMaze();
    saveMaze( datafile );
  }

  /**
   * Creates a new random maze with a the given number of non-tree edges.
   * @param non_tree_edges Number of non tree cycles present in MST. 
   * @return Nothing.
   */
  public void createRandomMaze( int non_tree_edges ) {
    this.non_tree_edges = non_tree_edges;
    createRandomMaze();
  }

  /**
   * Creates a random maze using Kruskals Algorithm.
   * @return Nothing.
   */
  public void createRandomMaze() {                                                                    
    final int MIN_DIM = 3;
    ArrayList<Pair<MazeNode, MazeNode>> walls = new ArrayList<Pair<MazeNode, MazeNode>>( getDimension() * getDimension() );
    Random rand = new Random();

    System.err.println( "Generating Random Maze..." );

    if( getDimension() < MIN_DIM ) {
      /* invalid dimension for random maze generation */
      System.err.println( "Invalid Dimension for Maze Generation. Valid dimension >= 3." );
      return; 
    }

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        if( row < maze.length - 1 ) {
          /* insert wall below the current cell */
	        walls.add( new Pair<>( maze[row][column], maze[row + 1][column] ) );
	}
	if( column < maze[0].length - 1 ) {
          /* insert wall to the right current cell */
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
      MazeNode neighbor = maze[ target.y + dr ][ target.x + dc ];
      union( target, neighbor );
      addEdge( target, neighbor );
      walls.remove( new Pair<>( target, neighbor) );
      count++;
    }
 
    /* list of non_tree_edges */
    ArrayList<Pair<MazeNode, MazeNode>> extraWalls = new ArrayList<Pair<MazeNode, MazeNode>>( getDimension() * getDimension() );

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
        extraWalls.add( node_pair );
      }
      /* remove wall from wall list */
      walls.remove( randomIndex );
    }

    /* create multiple paths to solution */
    int numOfPaths = non_tree_edges;
    for( int index = 0; extraWalls.size() != 0 && index < numOfPaths; index++ ) {
      randomIndex = rand.nextInt( extraWalls.size() );
      Pair<MazeNode, MazeNode> node_pair = extraWalls.get( randomIndex );
      MazeNode vertex_A = node_pair.first;
      MazeNode vertex_B = node_pair.second;
      
      /* add cycle : alternate path */
      addEdge( vertex_A, vertex_B );
      count++;

      /* remove non-tree edge picked */
      extraWalls.remove( randomIndex );
    }

    extraWalls.clear();
    System.err.println( "Number of non-tree edges: " + numOfPaths );
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
    return wallBetween( at(alpha), at(beta) );
  }

  /**
   * Evaluates if a wall exists between two nodes in the maze.
   * @param vertex_A a node in the maze.
   * @param vertex_B a node in the maze.
   * @return true if there is no adjacent path from A to B.
   */
  public boolean wallBetween( MazeNode vertex_A, MazeNode vertex_B ) {
    if( vertex_A == null || vertex_B == null ) return false;

    LinkedList<MazeNode> neighbors_of_A = vertex_A.getNeighborList();

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

  /**
   * Removes the edge that connexts the two adjacent vertices.
   * @return Nothing.
   */
  public void removeEdge( MazeNode vertex_A, MazeNode vertex_B ) {
    if( vertex_A == null || vertex_B == null ) return;
    /* removing undirected edge */
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
   * Accessor method for maze internal structure.
   * @param alpha point with x and y coordinate of node of interest.
   * @return MazeNode object at that coordinate given.
   */
  public MazeNode at( Point alpha ) {
    /* Recall: y = row && x = column */
    return at( alpha.y, alpha.x );
  }

  /**
   * Accessor method for the maze internal structure.
   * @param row cell in 2d array maze.
   * @param column cell in 2d array maze.
   */
  public MazeNode at( int row, int column ) {
    if( outOfBounds(row) || outOfBounds(column) ) {
      System.err.println( "Maze:at() out of bounds (" + row + ", " + column + ")" );
      return null;
    }
    /* Abstract the maze data structure */
    return maze[ row ][ column ];
  }

  /**
   * Clears the maze such that no walls will exist - this method will
   * create a fully connected maze.
   * @return Nothing.
   */
  public void clearWalls() {
    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        /* create empty maze with no walls */
        MazeNode currentNode = maze[ row ][ column ];
    
        if( !outOfBounds(row + 1) ) {
	        /* vertical deviation downwards */
	        addEdge( currentNode, maze[ row + 1 ][ column ] );
	      }
	      if( !outOfBounds(column + 1) ) {
	        /* horizontal deviation to the right */
	        addEdge( currentNode, maze[ row ][ column + 1 ] );
	      }
      }
    }
  }

  /**
   * Remove the edge that connects vertex A and B, such that vertex A and B are
   * are adjacent in the maze.
   * @return Nothing.
   */
  public void addWall( MazeNode vertex_A, MazeNode vertex_B ) {
    removeEdge( vertex_A, vertex_B );
  }

  /**
   * Returning all global neighbors to vertex cell.
   * @param vertex relative cell that is requsting to get the adjacent neighbors.
   * @return a linked list of all adjacent existing neighbors.
   */
  public LinkedList<MazeNode> getAdjacentCellsList( MazeNode vertex ) {
    int MAX_CELLS = 4;
    LinkedList<MazeNode> list = new LinkedList<MazeNode>();

    for( int index = 0; index < MAX_CELLS; index++ ) {
      /* append all adjacent neighbors to list */
      int deviation = ( index < EVEN ) ? +1 : -1; 
      int dr = ( index % EVEN == 0 ) ? deviation : 0; 
      int dc = ( index % EVEN == 1 ) ? deviation : 0;
      if( !outOfBounds(vertex.row + dr) && !outOfBounds(vertex.column + dc) ) {
        list.add( maze[ vertex.row + dr ][ vertex.column + dc ] );
      }
    }

    return list;
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

  /**
   * Getter for the max cycle attribute, which entails the upper bound of 
   * cycles that can exist in a randomly generated maze.
   * @return the current max cycle attribute.
   */
  public int getTotalNonTreeEdges() {
    return non_tree_edges;
  }

  /**
   * Starting position for maze.
   * @return the starting position for solving the maze.
   */
  public MazeNode getBegin() {
    return at( dimension - 1, 0 );
  }

  /**
   * Destination/target position to solve maze.
   * @return the target position to solve the maze. 
   */
  public MazeNode getEnd() {
    MazeNode end = at( getDimension() / EVEN, getDimension() / EVEN );
    if( getDimension() % EVEN == 0 ) {
      /* quad-cell solution set. find initial entrance node */
      int lowerBound = getDimension() / EVEN - 1;
      for( int delta = 0; delta < EVEN; delta++ ) {
        /* find target node with 3 children in quad-cell solution */
        MazeNode topNode = at( lowerBound, lowerBound + delta );
        MazeNode lowerNode = at( lowerBound + 1, lowerBound + delta );
        if( topNode.getNeighborList().size() > EVEN ) {
          end = topNode;
          break;
        }
        if( lowerNode.getNeighborList().size() > EVEN ) {
          end = lowerNode;
          break;
        }
      }
    }
    return end;
  }

  /**
   * Encodes a binary representation of the maze to a file.
   * @return Nothing.
   */
  public void saveMaze( File datafile ) {
    FileOutputStream out = null;

    long prevMillis = System.currentTimeMillis();
    System.err.println( "Saving Maze..." );

    try {
      out = new FileOutputStream( datafile );
      serialize( out );
    }
    catch( IOException e ) {
      e.printStackTrace();
    }
    finally {
      try {
        if( out != null ) {
          out.close();
        }
      }
      catch( IOException e ) {
        e.printStackTrace();
      }
    }
    System.err.println( "Time taken to save maze: " + (System.currentTimeMillis() - prevMillis) / 1000.0 + " sec" );
  }

  /**
   * Loads the saved maze.
   * @param datafile Encoded maze data file.
   * @return True if load is successful, false otherwise.
   */
  public boolean loadMaze( File datafile ) {
    if( !datafile.exists() || datafile.isDirectory() ) {
      /* datafile does not exist */
      return false;
    }

    FileInputStream in = null;
    boolean status = false;

    long prevMillis = System.currentTimeMillis();
    System.err.println( "Loading Maze..." );

    try {
      in = new FileInputStream( datafile );
      status = deserialize( in );
    }
    catch( IOException e ) {
      e.printStackTrace();
      status = false;
    }
    finally {
      try {
        if( in != null ) {
          in.close();
	      }
      }
      catch( IOException e ) {
        e.printStackTrace();
        status = false;
      }
    }

    if( status == true ) {
      System.err.println( "Time taken to load maze: " + (System.currentTimeMillis() - prevMillis) / 1000.0 + " sec" );
    }
    else {
      System.err.println( "Unsuccessful maze load." );
    }

    return status;
  }

  /**
   * Binary string representation of Maze with decimal dimensions.
   * @param stream output stream that serialize will write data to.
   * @return String of encoded maze.
   */ 
  public void serialize( FileOutputStream outstream ) throws IOException {
    int data = 0;
    int bitcount = 0;
    /* write dimensions of maze out to stream - order: width height */
    ByteBuffer buffer = ByteBuffer.allocate( EVEN * Integer.BYTES );
    buffer.putInt( dimension ).putInt( dimension );
    outstream.write( buffer.array() );

    /* cell info to stream */
    for( int row = 0; row < dimension; row++ ) {
      for( int column = 0; column < dimension; column++ ) {
        MazeNode currentNode = at( row, column );
	      /* bitflag of open down neighbor */
	      data = data << 1;
	      if( currentNode.down != null ) data |= 0x01;
	      bitcount++;
              /* bitflag of open right neighbor */
	      data = data << 1;
	      if( currentNode.right != null ) data |= 0x01;
	      bitcount++;
	      /* time to write byte to stream */
	      if( bitcount == Byte.SIZE ) {
                outstream.write( (byte) data );
	        data = bitcount = 0;
	      }
      }
    }
    /* flush data to outstream */
    if( bitcount != 0 ) {
      outstream.write( (byte) (data << (Byte.SIZE - bitcount)) ); /* trailing zeros only */
    }
  }

  /**
   * Deserialize encoded maze data.
   * @param instream File stream pointing to an encoded data file.
   * @return True for successful deserialization, false otherwise.
   */
  private boolean deserialize( FileInputStream instream ) throws IOException {
    final int EOF = -1;
    final int codeword_size = 2; /* bit size */
    final int codeword_bitmask = ( 0x3 << (Byte.SIZE - codeword_size) ); 
    int recieved; 

    /* read dimensions of maze from input stream - order: width height */
    byte[] proxy = new byte[EVEN * Integer.BYTES];
    recieved = instream.read( proxy ); /* reads in two integers from instream */
    ByteBuffer buffer = ByteBuffer.wrap( proxy ); /* copy byte array */
    if( recieved == EOF ) {
      /* corrupted datafile - missing bytes */
      System.err.println( "Currupted file detected: Incompatible file size: Aborting maze build" );
      return false;
    }
    int read_width = buffer.getInt();
    int read_height = buffer.getInt();
    System.err.println( "Loading dimensions: (" + read_width + "," + read_height + ")" );
    if( read_width != dimension || read_height != dimension ) {
      /* width or height is not the same dimension as this maze object */
      System.err.println( "Incompatible dimensions read from file: Aborting maze build" );
      return false;
    }

    /* read and build maze graph cell by cell */
    int row = 0;
    int column = 0;
    clearWalls(); /* creating walls are easier than removing walls */
    recieved = instream.read(); /* read byte */
    while( recieved != EOF ) {
      /* reading 2-bit codewords. (1 codeword = 1 encoded maze node) */
      for( int index = 0; index < Byte.SIZE / codeword_size; index++ ) {
        if( row == dimension ) {
                /* maze building is done - all cells visited */
	        if( instream.available() > 1 ) {
	          System.err.println( "Curropted file detected: Incompatible file size: Aborting maze build" );
	          clear();
	          return false;
	        }
	        return true;
	      }
	      /* build walls of node */
              MazeNode currentNode = at( row, column );
	      int codeword = ((recieved & codeword_bitmask) >>> (Byte.SIZE - codeword_size));
	      deserializeNode( currentNode, codeword );
	      recieved = recieved << codeword_size;
	      column++;
	      if( column == dimension ) {
                column = 0;
	        row++;
	      }
      }
      recieved = instream.read();
    }
    return true;
  }

  /**
   * Builds encoded maze node from a two bit codeword.
   * @param node Node to be deserialized.
   * @param codeword Two-bit encoded node.
   * @return Nothing.
   */
  private void deserializeNode( MazeNode node, int codeword ) {
    final int down_bitmask = 0x01 << 1;
    final int right_bitmask = 0x01;

    if( (codeword & down_bitmask) == 0 && node.row != dimension - 1 ) {
      /* add down wall */
      addWall( at(node.row + 1, node.column), at(node.row, node.column) );
    }
    if( (codeword & right_bitmask) == 0 && node.column != dimension - 1 ) {
      /* add right wall */
      addWall( at(node.row, node.column), at(node.row, node.column + 1) );
    }
  }

  /**
   * Iterator for the the Maze data structure.
   * @return An iterator to maze.
   */
  public Iterator<MazeNode> iterator() {
    return new MazeIterator();
  }


  /**
   * MazeIterator is an intuitive way to traverse the maze without exposing the
   * internal implementation of the maze data structure to the user; Also allows
   * loose coupling of classes that use the Maze data structure.
   */
  private class MazeIterator implements Iterator<MazeNode> {
    private int current_row;
    private int current_column;

    /**
     * MazeIterator constructor starting element begins in the top left corner
     * of the maze.
     */
    public MazeIterator() {
      this.current_row = 0;
      this.current_column = 0;
    }

    /**
     * Boolean evaluation to check if the iterator is done traversing through
     * the maze or not.
     * @return True if there exists more nodes to traverse to, False otherwise.
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
     * Moves the iterator to the next consequetive node in the maze.
     * @return the maze node that is next in the data structure. If no 
     *         consequetive node exist an exception will be thrown which 
     *         indicates the termination of the data structure iteration.
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
     * Method inherited from Iterator remove is not supported in the Maze
     * data structure.
     * @return an unsupported operation exception to the user.
     */
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
  }
}
