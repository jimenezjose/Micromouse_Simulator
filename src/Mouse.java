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
import java.util.Queue;

/**
 * Micromouse class to emulate autonomous robot behavior.
 */
public class Mouse {

  private final double PROPORTION = 0.3;
  private final int EVEN = 2;
  private final int TOTAL_RUNS = 4;

  public int x;
  public int y;
  private int column;
  private int row;

  private final JFrame canvas;
  private MouseShape mouse;
  private Maze ref_maze;
  private Maze maze;
  private Point center = new Point();

  private Point start_position;
  private Orientation orientation;
  private Stack<MazeNode> explore_stack = new Stack<MazeNode>();
  private LinkedList<MazeNode> mousePath = new LinkedList<MazeNode>();
  private boolean visited[][]; 

  private int num_of_runs = 0;
 
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
    this.visited = new boolean[ maze.getDimension() ][ maze.getDimension() ];
    start();
  }

  /**
   * Flood Fill Algorithm iteration.
   * @return true if mouse is in progress to get to target; false if 
   *         mouse is at target.
   */
  public boolean exploreNextCell() {
    if( explore_stack.empty() ) {
      /* mouse is at target. */
      if( num_of_runs != TOTAL_RUNS ) retreat();
      return false;
    }

    MazeNode cell = explore_stack.pop(); 

    rotateTo( cell );
    moveTo( cell );
    setVisited( cell, true );
    markNeighborWalls( cell, orientation );
    /* update maze distances from new discovered wall */
    callibrateDistances( cell );

    for( MazeNode openNeighbor : cell.getNeighborList() ) {
      if( openNeighbor.distance == cell.distance - 1 ) {
        /* hueristic to move closer to the target */
        explore_stack.push( openNeighbor );
	if( openNeighbor.distance == 0 ) explore_stack.push( openNeighbor );
	return true;
      } 
      else if( openNeighbor.distance == 0 && this.visited( openNeighbor ) == false ) {
        /* visit all target nodes */
	explore_stack.push( openNeighbor );
        return true;
      }
    }
    return true;
  }

  /**
   * TODO delegates to callibrate
   */
  private void callibrateDistances( MazeNode cell ) {
    callibrate( cell );
    for( MazeNode globalNeighbor : maze.getAdjacentCellsList( cell ) ) {
      if( globalNeighbor.distance == 0 ) continue;
      callibrate( globalNeighbor );
    }
  }
  /**
   * TODO
   */
  private void callibrate( MazeNode cell ) {
    int minDistance = Integer.MAX_VALUE; 

    for( MazeNode openNeighbor : cell.getNeighborList() ) {
      /* validate cell's need for callibration */
      if( openNeighbor.distance == cell.distance - 1 ) return; 
      if( openNeighbor.distance < minDistance ) minDistance = openNeighbor.distance;
    }

    /* update non target cell to a higher elevation */
    if( cell.distance != 0 ) cell.distance = minDistance + 1; 

    for( MazeNode globalNeighbor : maze.getAdjacentCellsList( cell ) ) {
      /* callibrate all global neighbors except for the target cells */
      if( globalNeighbor.distance == 0 ) continue;
      callibrate( globalNeighbor );
    }
  }

  /**
   * Continue exploring maze by retreating to the starting position.
   * @return Nothing.
   */
  private void retreat() {
    MazeNode newTargetCell = maze.at( start_position );
    start_position.setLocation( x, y );
    updateMazeDistances( newTargetCell );
    explore_stack.push( maze.at(row, column) );
    num_of_runs++;
  }

  /**
   * Update distance values for each cell in the maze given the target.
   * @return Nothing.
   */
  private void updateMazeDistances( MazeNode target ) {
    Queue<MazeNode> q = new LinkedList<MazeNode>();

    for( MazeNode cell : maze ) {
      cell.setVisited( false );
    }

    q.add( target );
    target.setVisited( true );
    target.distance = 0;

    while( !q.isEmpty() ) {
      /* BFS traversal */
      MazeNode cell = q.remove();

      for( MazeNode openNeighbor : cell.getNeighborList() ) {
        if( openNeighbor.visited ) continue;
	q.add( openNeighbor );
	openNeighbor.setVisited( true );
	openNeighbor.distance = cell.distance + 1;
      }
    }

  }

  /**
   * TODO
   */
  public void trackSteps() {
    if( mousePath.size() != 0 || isDone() == false ) return;
    updateMousePath( maze.at(start_position), maze.at(row, column) );
  }

  /**
   * TODO
   */
  private void updateMousePath( MazeNode start, MazeNode end ) {
    mousePath.push( start );

    if( start.equals(end) ) return;

    for( MazeNode neighbor : start.getNeighborList() ) {
      if( this.visited( neighbor ) == false ) continue;
      if( neighbor.distance == start.distance - 1 ) {
        updateMousePath( neighbor, end );
	return;
      }
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
   * Erases all memory about the maze configuration.
   * @return Nothing.
   */
  private void clearMazeMemory() {
    /* break all walls in maze - (this fully connected graph) */
    maze.clearWalls();
    /* erase memory from exploring maze */
    explore_stack.clear();
    mousePath.clear();
    num_of_runs = 0;

    /* mark manhattan distance of clear maze  */ 
    for( MazeNode cell : maze ) {
      Point center = getClosestCenter( cell );
      /* manhattan distance */
      cell.setDistance( Math.abs(center.x - cell.x) + Math.abs(center.y - cell.y) );
      cell.setVisited( false );
      setVisited( cell, false );
    }
  }

  /**
   * TODO Use of gloal center because I only need one throughout the life of the program
   */
  private Point getClosestCenter( MazeNode cell ) {
    int centerX = maze.getDimension() / EVEN;
    int centerY = maze.getDimension() / EVEN;

    /* singular solution cell */
    if( maze.getDimension() % EVEN == 1 ) {
      center.setLocation( centerX, centerY );
      return center;
    }

    /* quad-cell solution */
    if( cell.x < maze.getDimension() / EVEN ) {
      centerX = maze.getDimension() / EVEN - 1;
    }
    if( cell.y < maze.getDimension() / EVEN ) {
      centerY = maze.getDimension() / EVEN - 1;
    }
    center.setLocation( centerX, centerY );
    return center;
  }

  /**
   * Rotate mouse to face towards the given cell.
   * @param cell the cell the mouse will face towards.
   * @return Nothing.
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
  * TODO
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
    double UNIT = (1.0 / ref_maze.getDimension()) * maze_diameter;
    double unitCenterX = maze_draw_point.x + column * UNIT + (UNIT / 2.0);
    double unitCenterY = maze_draw_point.y + row * UNIT + (UNIT / 2.0);
    double width = UNIT * PROPORTION; 
    double height = UNIT * PROPORTION; 
    double x = unitCenterX - UNIT * PROPORTION / 2.0; 
    double y = unitCenterY - UNIT * PROPORTION / 2.0;

    mouse.setLocation( (int)x, (int)y );
    mouse.setDimension( (int)width, (int)height );
  }
  
  private void setVisited( MazeNode cell, boolean truthValue ) {
    visited[ cell.row ][ cell.column ] = truthValue;
  }

  private boolean visited( MazeNode cell ) {
    return visited[ cell.row ][ cell.column ];
  }

  /**
   * TODO
   */
  public boolean isDone() {
    return explore_stack.empty() && num_of_runs == TOTAL_RUNS;
  }

  /**
   *
   */
  public LinkedList<MazeNode> getMousePath() {
    return new LinkedList<MazeNode>( mousePath );
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

