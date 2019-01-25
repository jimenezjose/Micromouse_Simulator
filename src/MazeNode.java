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
import java.util.HashMap;

/**
 * MazeNode contains information about its location in Maze, and its reachabble
 * neighbors.
 */
class MazeNode {
  private final String ADD_EDGE_ERROR = "Error: attempt to add edge to a pair on non-adjacent nodes. ";
  private final int offset = 2; /* offset to ignore walls */

  /* begin - maze generation data */
  private MazeNode parent = null;
  private LinkedList<MazeNode> children_list = new LinkedList<MazeNode>();
  /* end - maze generation data */

  public int x = 0;
  public int y = 0;
  public MazeNode up = null;
  public MazeNode down = null;
  public MazeNode left = null;
  public MazeNode right = null;

  /**
   * Creates a maze object for an associated location in a 2d maze.
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
   */
  public void addNeighbor( MazeNode vertex ) {
    if( x == vertex.x ) {
      /* computer y-axis is inverted */
      if( y + offset == vertex.y ) down = vertex;
      else if( y - offset == vertex.y ) up = vertex;
    }
    else if( y == vertex.y ) {
      /* normal x-axis convention */
      if( x + offset == vertex.x ) right = vertex;
      else if( x - offset == vertex.x ) left = vertex;
    }
    else {
      /* vertex is not adjacent */
      System.err.println( ADD_EDGE_ERROR + this + " <-> " + vertex );
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

  public void union( MazeNode vertex ) {
    if( parent != null ) {
      /* recurse up the disjoint set tree - only a depth of 1 max */
      parent.union( vertex );
      return;
    }

    /* union two disjoint sets. - representatives located */
    parent = (vertex.parent == null) ? vertex : vertex.parent;

    while( children_list.size() != 0 ) {
      /* migrates all children to new parent */
      MazeNode child = children_list.pop();
      parent.addChild( child );
    }
  }

  /**
   * Keep track of subset nodes for union operations
   * @param vertex node that is in the disjoint set of this.
   */
  public void addChild( MazeNode vertex ) {
    children_list.push( vertex );
  }

  /* this must be fast. inSame set will be called more than union. */
  /* therefore union will be an expensive operation for constant time set check */

  public boolean inSameSet( MazeNode vertex ) {
    /* roots of both vertice sets */
    MazeNode a_root = this;
    MazeNode b_root = vertex;

    if( parent != null) a_root = parent;
    if( vertex.parent != null ) b_root = vertex.parent;

    return (a_root == b_root);
  }

  /**
   * Checks if two nodes are equivalent.
   * @param o Generic object.
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
   */
  @Override
  public String toString() {
    return "(" + this.x + ", " + this.y + ")";
  }
}
