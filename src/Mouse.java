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
 * File Name:   Maze.java
 * Description: Maze data structure that will handle minimum spanning tree 
 *              and random maze generation algorithms.
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Stack;
import java.util.LinkedList;

/**
 * Micromouse class to emulate autonomous robot behavior.
 */
public class Mouse {

  private final double PROPORTION = 0.3;
  private final int EVEN = 2;
  private double UNIT;

  public int x;
  public int y;
  private int column;
  private int row;

  private final JFrame canvas;
  private Point maze_draw_point;
  private int maze_diameter;
  private MouseShape mouse;
  private Maze ref_maze;
  private Maze maze;
  private Point center = new Point();

  private Point start_position;
  private Orientation orientation;
  private Stack<MazeNode> explore_stack = new Stack<MazeNode>();

 
  /**
   * Creates mouse object on canvas
   * @param row starting row position of mouse.
   * @param column starting column position of mouse.
   * @param ref_maze radom maze.
   * @param maze empty maze.
   * @param canvas JFrame that the mouse will exist in.
   */
  public Mouse( int row, int column, Maze ref_maze, Maze maze, JFrame canvas ) {
    this.row = this.y = row;
    this.column = this.x = column;
    this.ref_maze = ref_maze;
    this.maze = maze;
    this.canvas = canvas;
    this.mouse = new MouseShape();
    this.start_position = new Point( x, y );
    start();
  }

  /**
   * Flood Fill Algorithm iteration.
   * @return Nothing.
   */
  public boolean exploreNextCell() {
    if( explore_stack.empty() ) {
      return false;
    }

    MazeNode cell = explore_stack.pop(); 

    rotateTo( cell );
    moveTo( cell );
    markNeighborWalls( cell, orientation );
    cell.setVisited( true );

    callibrateNeighbors( cell );

    /* flood fill algorithm */
    for( MazeNode openNeighbor : cell.getNeighborList() ) {
      if( openNeighbor.distance == cell.distance - 1 ) {
        /* hueristic to move closer to the target */
        explore_stack.push( openNeighbor );
        return true;
      }
      else if( openNeighbor.distance == 0 && openNeighbor.visited == false ) {
        /* visit all target nodes */
	explore_stack.push( openNeighbor );
	return true;
      }
    }

    if( cell.distance == 0 ) return true;

    // recalibrate cell distances
    //callibrateNeighbors( cell );
    explore_stack.push( cell );
    return true;
  }

  /**
   * TODO
   */
  private void callibrateNeighbors( MazeNode cell ) {
    int minDistance = Integer.MAX_VALUE; 

    for( MazeNode openNeighbor : cell.getNeighborList() ) {
      /* validate cell's need for callibration */
      if( openNeighbor.distance == cell.distance - 1 ) return; 
      if( openNeighbor.distance < minDistance ) minDistance = openNeighbor.distance;
    }

    /* update non target cell to a higher elevation */
    if( cell.distance != 0 ) cell.distance = minDistance + 1; 
    LinkedList<MazeNode> global_neighbor_list = maze.getAdjacentCellsList( cell );

    for( MazeNode globalNeighbor : global_neighbor_list ) {
      /* callibrate all global neighbors except for the target cells */
      if( globalNeighbor == null || globalNeighbor.distance == 0 ) continue;
      callibrateNeighbors( globalNeighbor );
    }
  }

  /**
   * TODO
   */
  void rotateTo( MazeNode cell ) {
    if( x == cell.x ) {
      /* vertical deviation */
      if( y + 1 == cell.y ) orientation = Orientation.SOUTH;
      else if( y - 1 == cell.y ) orientation = Orientation.NORTH;
    }
    else if( y == cell.y ) {
      /* horizontal deviation */
      if( x + 1 == cell.x ) orientation = Orientation.EAST; 
      else if( x - 1 == cell.x ) orientation = Orientation.WEST;
    }
  }

  /**
   * Delegates to the restart method to restart mouse simulation. 
   * @return Nothing.
   */
  private void start() {
    restart();
  }

  /**
   * Erases maze memory and restarts mouse simulation from mouse initial position.
   * @return Nothing.
   */
  public void restart() {
    clearMazeMemory();
    /* initial position of mouse pushed */
    moveTo( start_position );
    explore_stack.clear();
    explore_stack.push( maze.at(row, column) );
    orientation = Orientation.NORTH;
  }

  /**
   * Erases all memory about the maze configuration.
   * @return Nothing.
   */
  private void clearMazeMemory() {
    /* break all walls in maze - (this fully connected graph) */
    maze.clearWalls();
    /* erase memory from exploring maze */
    explore_stack.clear();

    /* mark manhattan distance of clear maze  */ 
    for( MazeNode node : maze ) {
      Point center = getClosestCenter( node );
      /* manhattan distance */
      node.setDistance( Math.abs(center.x - node.x) + Math.abs(center.y - node.y) );
      node.setVisited( false );
    }
  }

  /**
   * Emulate sensor data of mouse to mark surrounding maze walls;
   * Physical Constraint: There are only sesors on the front, left, and right
   *                      faces of the mouse.
   * @param cell Location in maze.
   * @param orientation mouse front face direction.
   */
  private void markNeighborWalls( MazeNode cell, Orientation orientation ) {
    MazeNode ref_cell = ref_maze.at( cell.row, cell.column );
    MazeNode[] ref_neighbors = { ref_cell.up, ref_cell.right, ref_cell.down, ref_cell.left };
    MazeNode[] neighbors = { cell.up, cell.right, cell.down, cell.left };

    Orientation point = orientation.relativeLeft();
    while( point != orientation.relativeBack() ) {
      /* sweep across the left wall, up wall, and right wall */
      if( ref_neighbors[ point.ordinal() ] == null ) {
        /* wall found in reference maze */
	maze.removeEdge( cell, neighbors[ point.ordinal() ] );
      }
      point = point.next();
    }
  }

  /**
   * TODO Use of gloal center because I only need one throughout the life of the program
   */
  private Point getClosestCenter( MazeNode node ) {
    int centerX = maze.getDimension() / EVEN;
    int centerY = maze.getDimension() / EVEN;

    /* singular solution cell */
    if( maze.getDimension() % EVEN == 1 ) {
      center.setLocation( centerX, centerY );
      return center;
    }

    /* quad-cell solution */
    if( node.x < maze.getDimension() / EVEN ) {
      centerX = maze.getDimension() / EVEN - 1;
    }
    if( node.y < maze.getDimension() / EVEN ) {
      centerY = maze.getDimension() / EVEN - 1;
    }
    center.setLocation( centerX, centerY );
    return center;
  }

 /**
  *
  */
  private void moveTo( MazeNode cell ) {
    moveTo( cell.x, cell.y );
  }

  /**
   * TODO
   */
  private void moveTo( Point point ) {
    moveTo( point.x, point.y );
  }

  /**
   * TODO
   */
  private void moveTo( int x, int y ) {
    move( x - column, y - row );
  }

  /**
   * Translates the mouse by the given differential.
   * @param dx horizontal differential.
   * @param dy vertical differential.
   * @return Nothing.
   */
  public void move( int dx, int dy ) {
    //TODO possible error may be that i am not multiplying by UNIT
    column = x += dx;
    row = y += dy;
  }
 
  /**
   * Moves the mouse one unit to the right.
   * @param color color of mouse to be drawn 
   * @return Nothing.
   */
  public void draw( Color color ) {
    mouse.draw( color );
  }

  /**
   * Sets the left corner of the maze on GUI, and the maze diameter.
   * @param maze_draw_point top left corner of maze on GUI.
   * @param maze_diameter   pixel diameter of maze on GUI.
   * @return Nothing.
   */
   public void setGraphicsEnvironment( Point maze_draw_point, int maze_diameter ) {
     this.maze_draw_point = maze_draw_point;
     this.maze_diameter = maze_diameter;
     this.UNIT = (1.0 / ref_maze.getDimension()) * maze_diameter;

     double startingX = maze_draw_point.x + column * UNIT;
     double startingY = maze_draw_point.y + row * UNIT;
     double unitCenterX = startingX + (UNIT / 2.0);
     double unitCenterY = startingY + (UNIT / 2.0);
     double width = UNIT * PROPORTION; 
     double height = UNIT * PROPORTION; 
     double x = unitCenterX - width / 2.0; 
     double y = unitCenterY - height / 2.0;

     mouse.setLocation( (int)x, (int)y );
     mouse.setDimension( (int)width, (int)height );
   }
   
  /**
   * TODO
   */
  private enum Orientation {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    /**
     * TODO
     */
    public Orientation relativeRight() {
      int length = size();
      return values()[ (ordinal() + 1) % length ];
    }

    /**
     * TODO
     */
    public Orientation relativeLeft() {
      int length = size();
      return values()[ (ordinal() + (length - 1)) % length ];
    }

    /**
     * TODO
     */
    public Orientation relativeBack() {
      int length = size();
      return values()[ (ordinal() + (length / 2)) % length ];
    }

    /**
     * TODO
     */
    public int size() {
      return values().length;
    }

    /**
     * TODO
     */
    public Orientation next() {
      return relativeRight();
    }
  }

  /**
   * Class: MouseShape: Consitiutes the generic mouse shape.
   */
  private class MouseShape {
    private Rectangle body;
    private Point location;
    private Dimension dimension;

    /**
     * Constructor for mouse shape.
     * @param x Top left corner x-coordinate of shape.
     * @param y Top left corner y-coordinate of shape.
     * @param width Pixel width of mouse body.
     * @param height Pixel height of mouse body.
     */
    public MouseShape( int x, int y, int width, int height ) {
      body = new Rectangle( x, y, width, height );
      location = new Point( x, y );
      dimension = new Dimension( width, height );
    }

    /**
     * Default constructor for empty mouse shape.
     */
    public MouseShape() {
      this( 0, 0, 0, 0 );
    }

    /**
     * Shifts the mouse shape by dx and dy pixels.
     * @param dx Pixel differential in the x-axis.
     * @param dy Pixel differential in the y-axis.
     * @return Nothing.
     */
    public void translate( int dx, int dy ) {
      body.translate( dx, dy );
      location.x +=  dx;
      location.y += dy;
    }

    /**
     * Draws the mouse shape on canvas.
     * @param color Color of mouse shape.
     * @return Nothing.
     */
    public void draw( Color color ) {
      Graphics g = canvas.getGraphics();
      g.setColor( color );
      g.fillRect( location.x, location.y, dimension.width, dimension.height );
    }

    /**
     * Sets new dimension of mouse shape.
     * @param width width of mouse in dimensions.
     * @param height height of mouse in dimensions.
     * @return Nothing.
     */
    public void setDimension( int width, int height ) {
      dimension.setSize( width, height );
    }
   
    /**
     * Sets new location for mouse.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return Nothing.
     */
    public void setLocation( int x, int y ) {
      location.setLocation( x, y );
    }
  }
}

