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
 * File Name:   MazeNode.java 
 * Description: Contains all information for traversing a maze and creating a 
 *              maze.
 */

import java.util.LinkedList;

/**
 * MazeNode contains information about its location in Maze, and its reachabble
 * neighbors.
 */
class MazeNode {
  private final String ADD_EDGE_ERROR = "Error: attempt to add edge to a pair on non-adjacent nodes. ";

  /* begin - maze generation data */
  public MazeNode parent = null;
  public LinkedList<MazeNode> subset_list = new LinkedList<MazeNode>();
  /* end - maze generation data */

  public final int x;
  public final int y;
  public MazeNode up = null;
  public MazeNode down = null;
  public MazeNode left = null;
  public MazeNode right = null;
  public boolean visited = false;

  /**
   * Creates a node object for an associated location in a 2d maze.
   * @param x row location of node.
   * @param y column location of node.
   */
  public MazeNode( int x, int y ) {
    this.x = x;
    this.y = y;
  }

  /**
   * Attaches vertex as neighbobr of currentNode.
   * @param vertex An adjacent cell in the maze from current node.
   * @return Nothing.
   */
  public void addNeighbor( MazeNode vertex ) {

    if( x == vertex.x ) {
      /* computer y-axis is inverted */
      if( y + 1 == vertex.y ) down = vertex;
      else if( y - 1 == vertex.y ) up = vertex;
    }
    else if( y == vertex.y ) {
      /* normal x-axis convention */
      if( x + 1 == vertex.x ) right = vertex;
      else if( x - 1 == vertex.x ) left = vertex;
    }
    else {
      /* vertex is not adjacent */
      System.err.println( ADD_EDGE_ERROR + this + " <-> " + vertex );
    }
  }

  /**
   * Keep track of subset nodes for union operations.
   * @param vertex Node that is in the disjoint set of this.
   * @return Nothing.
   */
  public void addSubsetElement( MazeNode vertex ) {
    subset_list.push( vertex );
    vertex.parent = this;
  }

  /**
   * Checks if two nodes are equivalent.
   * @param o Generic object.
   * @return True if and only if o is equivalent to this.
   */
  @Override
  public boolean equals( Object o ) {
    if( o == this ) return true;
    if( !(o instanceof MazeNode) ) return false;
    MazeNode node = (MazeNode) o;
    if( x ==  node.x && y == node.y ) return true;
    else return false;
  }

  /**
   * Allows for implicit string conversion of a node object.
   * @return String representation of MazeNode objects.
   */
  @Override
  public String toString() {
    return "(" + this.x + ", " + this.y + ")";
  }
}
