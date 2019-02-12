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
  /* begin - attributes for path optimization */
  public final double diagonal_x;
  public final double diagonal_y;
  /* end - attributes for path optimization */
  public MazeNode up = null;
  public MazeNode down = null;
  public MazeNode left = null;
  public MazeNode right = null;

  /* begin - graph search data */
  public boolean visited = false;
  public MazeNode prev = null;
  public int distance = 0;
  /* end - graph search data */

  /**
   * Creates a node object for an associated location in a 2d maze.
   * @param x row location of node.
   * @param y column location of node.
   */
  public MazeNode( double x, double y ) {
    this.x = (int)x;
    this.y = (int)y;
    this.diagonal_x = x;
    this.diagonal_y = y;
  }

  /**
   * Attaches vertex as neighbobr of currentNode.
   * @param vertex An adjacent cell in the maze from current node.
   * @return Nothing.
   */
  public void addNeighbor( MazeNode vertex ) {

    if( x == vertex.x ) {
      /* computer y-axis is inverted */
      if( y + 1 == vertex.y ) right = vertex;
      else if( y - 1 == vertex.y ) left = vertex;
    }
    else if( y == vertex.y ) {
      /* normal x-axis convention */
      if( x + 1 == vertex.x ) down = vertex;
      else if( x - 1 == vertex.x ) up = vertex;
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
   * Clear MazeNode graph data.
   */
  public void clearData() {
    up = down = left = right = null;
    visited = false;
    prev = null;
    distance = 0;
    parent = null;
    subset_list.clear();
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

  /**
   * TODO
   */
  public void setVisited( boolean visited ) {
    this.visited = visited;
  }

  /**
   * TODO
   */
  public void setPrev( MazeNode prev ) {
    this.prev = prev;
  }

  /**
   * TODO
   */
  public void setDistance( int distance ) {
    this.distance = distance;
  }

  /**
   * TODO
   */
  public boolean getVisited() {
    return visited;
  }

  /**
   * TODO
   */
  public MazeNode getPrev() {
    return prev;
  }

  /**
   * TODO
   */
  public int getDistance() {
    return distance;
  }

  public MazeNode[] getEdgeList() {
    return new MazeNode[]{ up, down, left, right };
  }

  /**
   * TODO
   */
  public double getDiagonalX() {
    return diagonal_x;
  }

  /**
   * TODO
   */
  public double getDiagonalY() {
    return diagonal_y;
  }
}
