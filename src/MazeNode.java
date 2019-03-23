/**
 *
 * Jose Jimenez
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
  private final String REMOVE_EDGE_ERROR = "Error: attempt to remove edge to a non-adjacent node. ";

  /* begin - maze generation data */
  public MazeNode parent = null;
  public int rank = 0;
  /* end - maze generation data */

  public final int column, x;
  public final int row, y;
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
   * @param x column location of node.
   * @param y row location of node.
   */
  public MazeNode( double row, double column ) {
    this.column = x = (int)column;
    this.row = y = (int)row;
    diagonal_x = column;
    diagonal_y = row;
  }

  /**
   * Attaches vertex as neighbobr of currentNode.
   * @param vertex An adjacent cell in the maze from current node.
   * @return Nothing.
   */
  public void addNeighbor( MazeNode vertex ) {
    if( vertex == null ) return;
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
   * Detach neighbor from neighborlist of this node.
   * @param vertex neighbor to be removed.
   * @return Nothing.
   */
  public void removeNeighbor( MazeNode vertex ) {
    if( vertex == null ) return; 
    if( x == vertex.x ) {
      /* vertical neighbor */
      if( y + 1 == vertex.y ) down = null;
      else if( y - 1 == vertex.y ) up = null;
    }
    else if( y == vertex.y ) {
      /* horizontal neighbor */
      if( x + 1 == vertex.x ) right = null; 
      else if( x - 1 == vertex.x ) left = null;
    }
    else {
      /* vertex is not adjacent */
      System.err.println( REMOVE_EDGE_ERROR + this + "<->" + vertex );
    }
  }

  /**
   * Clear MazeNode graph data.
   */
  public void clearData() {
    up = down = left = right = null;
    prev = null;
    parent = null;
    visited = false;
    distance = 0;
    rank = 0;
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
    /* (row, column) */
    return "(" + this.row + ", " + this.column + ")";
  }

  /**
   * Setter method for visited attribute.
   * @param visited new value for object attribute visited.
   * @return Nothing.
   */
  public void setVisited( boolean visited ) {
    this.visited = visited;
  }

  /**
   * Setter method for prev attribute.
   * @param visited new value for object attribute prev.
   * @return Nothing.
   */
  public void setPrev( MazeNode prev ) {
    this.prev = prev;
  }

  /**
   * Setter method for distance attribute.
   * @param visited new value for object attribute distance.
   * @return Nothing.
   */
  public void setDistance( int distance ) {
    this.distance = distance;
  }

  /**
   * Getter method for visited attribute.
   * @return boolean value of visited.
   */
  public boolean getVisited() {
    return visited;
  }

  /**
   * Getter method for prev attribute.
   * @return reference to MazeNode prev.
   */
  public MazeNode getPrev() {
    return prev;
  }

  /**
   * Getter method for distance attribute.
   * @return integer value of distace attribute.
   */
  public int getDistance() {
    return distance;
  }

  /**
   * Gets the neighbors of this as a linked list.
   * @return iteratable list of neighbors.
   */
  public LinkedList<MazeNode> getNeighborList() {
    //return new MazeNode[]{ up, down, left, right };
    LinkedList<MazeNode> neighbor_list = new LinkedList<MazeNode>();

    if( up != null ) neighbor_list.push( up );
    if( down != null ) neighbor_list.push( down );
    if( left != null ) neighbor_list.push( left );
    if( right != null ) neighbor_list.push( right );

    return neighbor_list;
  }

  /**
   * Getter for attribute diagonal_x which is for path 
   * optimization and allows "half steps" in the x-axis.
   * @return double value of the attribute diagonal_x.
   */
  public double getDiagonalX() {
    return diagonal_x;
  }

  /**
   * Getter for attribute diagonal_y which is for path 
   * optimization and allows "half steps" in the y-axis.
   * @return double value of the attribute diagonal_x.
   */
  public double getDiagonalY() {
    return diagonal_y;
  }
}
