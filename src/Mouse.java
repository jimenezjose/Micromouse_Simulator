/**
 *
 * Jose Jimenez
 * Brandon Cramer
 * Minh Pham
 * Tony Guan
 * Victor Chen
 *
 *                 University of California, San Diego
 *                      IEEE Micromouse Team 2020
 *
 * File Name:   Mouse.java
 * Description: The mouse object that is emulating an ideal robotic micromouse
 * Sources of Help: An IEEE Research paper about quantitive comparisons 
 *                  between different types of flood fill algorithm 
 *                  implementations by Dr George Law:
 *                  http://ijcte.org/papers/738-T012.pdf
 */

import java.io.PrintStream;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
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
  private final int TOTAL_RUNS = 2;

  public int x;
  public int y;
  private int column;
  private int row;

  private MouseShape mouse;
  private Maze ref_maze;
  private Maze maze;
  private Point center = new Point();

  private Point origin;
  private Point start_position;
  private Orientation orientation;
  private Stack<MazeNode> explore_stack = new Stack<MazeNode>();
  private boolean visited[][]; 

  private int num_of_runs = 0;
  private LinkedList<MazeNode> mousePath    = new LinkedList<MazeNode>();
  private LinkedList<MazeNode> previousPath = new LinkedList<MazeNode>();
  private boolean done = false;
 
  /**
   * Creates mouse object on GUI.
   * @param row starting row position of mouse.
   * @param column starting column position of mouse.
   * @param ref_maze radom maze.
   * @param maze empty maze.
   */
  public Mouse( int row, int column, Maze ref_maze, Maze maze ) {
    this.row = this.y = row;
    this.column = this.x = column;
    this.ref_maze = ref_maze;
    this.maze = maze;
    this.mouse = new MouseShape();
    this.origin = new Point( x, y );
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
      done = true;
      trackSteps();
      /* An optimal path was discovered */
      if( mousePath.size() == previousPath.size() && isCompletePath(mousePath) ) return false;
      /* otherwise continue traversing maze */
      done = false;
      retreat();
      setPreviousPath( mousePath );
      return false;
    }

    MazeNode cell = explore_stack.pop();

    /* sensor surroundings */
    rotateTo( cell );
    moveTo( cell );
    setVisited( cell, true );
    markNeighborWalls( cell, orientation );
    /* notify other cells of new walls */
    callibrateDistances( cell );

    for( MazeNode openNeighbor : cell.getNeighborList() ) {
      /* choose best adjacent open cell */
      if( openNeighbor.distance == cell.distance - 1 ) {
        /* hueristic to move closer to the target */
        explore_stack.push( openNeighbor );
        if( openNeighbor.distance == 0 ) explore_stack.push( openNeighbor );
        return true;
      }
      else if( openNeighbor.distance == 0 && this.visited( openNeighbor ) == false ) {
        /* visit all target nodes in quad-cell solution */
        explore_stack.push( openNeighbor );
        return true;
      }
    }
    return true;
  }

  /**
   * Floods the current cell and its adjacent cell distance value towards the target;
   * This fuction delegates to callibrate.
   * @param cell curret positional cell.
   * @return Nothing.
   */
  private void callibrateDistances( MazeNode cell ) {
    callibrate( cell );
    for( MazeNode globalNeighbor : maze.getAdjacentCellsList( cell ) ) {
      if( globalNeighbor.distance == 0 ) continue;
      callibrate( globalNeighbor );
    }
  }

  /**
   * Floods currenct cell such that there exist an "open" neighbor with a 
   * distance of cell.distance - 1.
   * @param cell a cell in need of distance validation.
   * @return Nothing.
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
   * @param target target cell that will have a distance of 0.
   * @return Nothing.
   */
  private void updateMazeDistances( MazeNode target ) {
    Queue<MazeNode> q = new LinkedList<MazeNode>();

    for( MazeNode cell : maze ) {
      /* reset visited values of all cells in the maze */
      cell.setVisited( false );
    }

    q.add( target );
    target.setVisited( true );
    target.distance = 0;

    while( !q.isEmpty() ) {
      /* BFS traversal */
      MazeNode cell = q.remove();

      for( MazeNode openNeighbor : cell.getNeighborList() ) {
        /* update distance only to open neighbor of cell */
        if( openNeighbor.visited ) continue;
	q.add( openNeighbor );
	openNeighbor.setVisited( true );
	openNeighbor.distance = cell.distance + 1;
      }
    }
  }

  /**
   * Tracks the mouse's next maze traversal from starting point to 
   * target point.
   * @return Nothing.
   */
  public void trackSteps() {
    mousePath.clear();
    updateMousePath( maze.at(start_position), maze.at(row, column) );
  }

  /**
   * Appends path traversal to mousePath linked list.
   * @param start beginning of path.
   * @param end terminatinnce cell of path.
   * @return Nothing.
   */
  private void updateMousePath( MazeNode start, MazeNode end ) {
    mousePath.push( start );

    /* current node is at destination */
    if( start.equals(end) ) return;

    /* move to the next least expensive cell */
    for( MazeNode neighbor : start.getNeighborList() ) {
      /* if mouse did not visit neighbor do not consider it */
      if( this.visited( neighbor ) == false ) continue;
      /* otherwise least */
      if( neighbor.distance == start.distance - 1 ) {
        updateMousePath( neighbor, end );
	return;
      }
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
    start_position.setLocation( origin.x, origin.y );
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
    mousePath.clear();
    previousPath.clear();
    num_of_runs = 0;
    done = false;

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
   * Retrieves the closest target location relative to the passed cell location;
   * This is needed when the target is a quad-cell solution set.
   * @param cell relative cell location in maze.
   * @return updated global variable "center" with the closest target location.
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
   * Traslates mouse to the give cell.
   * @param cell maze cell that the mouse will move to.
   * @return Nothing.
   */
  private void moveTo( MazeNode cell ) {
    moveTo( cell.x, cell.y );
  }

  /**
   * Translates mouse to the given coordinate in the maze.
   * @param coordinate point coordinate that the mouse will move to.
   * @return Nothing.
   */
  private void moveTo( Point coordinate ) {
    moveTo( coordinate.x, coordinate.y );
  }

  /**
   * Translates mouse to (x,y) position in maze.
   * @param x x location that the mouse will move to.
   * @param y y location that the mouse will move to.
   * @return Nothing.
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
    column = x += dx;
    row = y += dy;
  }
 
  /**
   * Moves the mouse one unit to the right.
   * @param color color of mouse to be drawn 
   * @return Nothing.
   */
  public void draw( Graphics g, Color color ) {
    mouse.draw( g, color );
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
  
  /**
   * Sets the mouse visited 2d array to the truth value provided; This signifies 
   * that the mouse itself has visited the cell location.
   * @param cell cell location to update.
   * @param truthValue the state that the cell location should be updated to.
   * @return Nothing.
   */
  private void setVisited( MazeNode cell, boolean truthValue ) {
    visited[ cell.row ][ cell.column ] = truthValue;
  }

  /**
   * Checks if mouse visited the cell location.
   * @param cell the cell of interest.
   * @return true if the cell has been visited by the mouse, false otherwise.
   */
  public boolean visited( MazeNode cell ) {
    return visited[ cell.row ][ cell.column ];
  }

  /**
   * Setter to set previouse path to be a shallow copy of list.
   * @param list linked list of nodes that will be copied to previous path.
   * @return Nothing.
   */
  private void setPreviousPath( LinkedList<MazeNode> list ) {
    previousPath.clear();
    previousPath.addAll( list );
  }

  /**
   * Checks if the mouse has found the most optimal path to the target.
   * @return true if the mouse is done running, false otherwise.
   */
  public boolean isDone() {
    return done; 
  }

  /**
   * Checks if the given path contains the starting cell and the cell the mouse
   * is currently run
   * @param path linked list of cells that represent the path mouse took from 
   *             its starting point to its current cell
   * @return true if the path contains the starting cell and the current mouse
   *         cell at the ends of the path list.
   */
  private boolean isCompletePath( LinkedList<MazeNode> path ) {
    if( path == null || path.size() == 0 ) {
      /* invlaid argument */
      System.err.println( "Mouse.java:isCompletePath parameter is invalid: path: " + path );
      return false;
    }

    MazeNode mouse_cell = maze.at( row, column );
    MazeNode start_cell = maze.at( start_position );
    boolean path_contains_start = path.getFirst().equals(start_cell) || path.getLast().equals(start_cell);
    boolean path_contains_mouse = path.getFirst().equals(mouse_cell) || path.getLast().equals(mouse_cell);

    if( path_contains_start && path_contains_mouse  ) { 
      /* path contains both end points */
      return true;
    }
    return false;
  }

  /**
   * Getter for the most optimal path the mouse found.
   * @return a linked list that starts from the starting cell to the target 
   *         cell.
   */
  public LinkedList<MazeNode> getMousePath() {
    return new LinkedList<MazeNode>( mousePath );
  }

  /**
   * Statistic that gets the total number of cells the mouse visited on the maze.
   * @return total cells the mouse visited.
   */
  public int getTotalCellsVisited() {
    int cells_visited = 0;
    for( MazeNode cell : maze ) if( this.visited(cell) ) cells_visited++;
    return cells_visited;
  }

  /**
   * Statistic that measure how many times the mouse ran from a starting point 
   * to a target, e.g 2 runs is counted when the mouse runs to the middle and back.
   * @return number of runs the mouse took to get the optimal solution.
   */
  public int getNumberOfRuns() {
    return ( this.isDone() ) ? num_of_runs + 1: num_of_runs; 
  }

  /**
   * Enum class the define the orientation of the mouse in the maze.
   */
  private enum Orientation {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    /**
     * Relative to the current orientation, what is right.
     * @return the relative orientation facing to the right.
     */
    public Orientation relativeRight() {
      int length = size();
      return values()[ (ordinal() + 1) % length ];
    }

    /**
     * Relative to the current orientation, what is left.
     * @return the relative orientation facing to the left.
     */
    public Orientation relativeLeft() {
      int length = size();
      return values()[ (ordinal() + (length - 1)) % length ];
    }

    /**
     * Realtive to the current orientation, what is back.
     * @return the relative orientation facing to the back.
     */
    public Orientation relativeBack() {
      int length = size();
      return values()[ (ordinal() + (length / 2)) % length ];
    }

    /**
     * Number of orientation possibilities.
     * @return the totale number of orientation possiblilities.
     */
    public int size() {
      return values().length;
    }

    /**
     * Moves orientation clockwise. 
     * @return the relative right orientaion.
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
     * Draws the mouse shape on GUI.
     * @param color Color of mouse shape.
     * @return Nothing.
     */
    public void draw( Graphics g, Color color ) {
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

