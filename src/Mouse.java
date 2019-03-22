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

/**
 * TODO
 */
public class Mouse {

  public int x;
  public int y;
  private final double PROPORTION = 0.3;
  private final int EVEN = 2;
  private double UNIT;
  private final JFrame canvas;
  private Point maze_draw_point;
  private int maze_diameter;
  private MouseShape mouse;
  private Maze ref_maze;
  private Maze maze;
  private int row;
  private int column;
  private Point center = new Point();
  Orientation orientation;
 
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
  }

  /**
   * TODO
   */
  public void exploreMaze() {
    MazeNode startVertex = maze.at( row, column );
    orientation = Orientation.NORTH;
    clearMouseMemory(); /* erase memory about maze */

    /* initial node distances to center of maze (maze with no walls) */
    for( MazeNode node : maze ) {
      /* closest target node : (used in mazes with a quad-cell target) */
      Point center = getClosestCenter( node );
      /* manhattan distance */
      node.setDistance( Math.abs(center.x - node.x) + Math.abs(center.y - node.y) );
    }

    floodFill( startVertex );
  }


  /**
   * TODO
   */
  private void floodFill( MazeNode startVertex ) {
    markNeighborWalls( startVertex, orientation );
  }

  /**
   * TODO
   */
  private void clearMouseMemory() {
    /* break all walls in maze - (fully connected maze) */
    maze.clearWalls();

    for( MazeNode node : maze ) {
      /* zero strain node memory */
      node.setVisited( false );
      node.setDistance( 0 );
      node.setPrev( null );
    }
  }

  /**
   * TODO
   */
  private void markNeighborWalls( MazeNode vertex, Orientation orientation ) {
    MazeNode ref_vertex = ref_maze.at( vertex.x, vertex.y );
    MazeNode[] ref_neighbors = { ref_vertex.up, ref_vertex.right, ref_vertex.down, ref_vertex.left };
    MazeNode[] neighbors = { vertex.up, vertex.right, vertex.down, vertex.left };

    Orientation point = orientation.relativeLeft();
    while( point != orientation.relativeRight() ) {
      /* sweep across the left wall, up wall, and right wall */
      if( ref_neighbors[ point.ordinal() ] == null ) {
        /* wall found in reference maze */
	maze.removeEdge( vertex, neighbors[ point.ordinal() ] );
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
      /* odd dimension means singular */
      center.setLocation( centerX, centerY );
      return center;
    }

    /* quad-cell solution */
    if( node.x < maze.getDimension() / EVEN ) {
      /* node in left half of maze quadrant */
      centerX = maze.getDimension() / EVEN - 1;
    }
    if( node.y < maze.getDimension() / EVEN ) {
      /* node in upper half of maze quadrant */
      centerY = maze.getDimension() / EVEN - 1;
    }

    center.setLocation( centerX, centerY );
    return center;
  }

  public void moveTo( int x, int y ) {
    move( x - column, y - row );
  }

  /**
   * Translates the mouse by the given differential.
   * @param dx horizontal differential.
   * @param dy vertical differential.
   * @return Nothing.
   */
  public void move( int dx, int dy ) {
    mouse.translate( dx, dy );
    column = x += dx;
    row = y += dy;
  }
 
  /**
   * Moves the mouse one unit to the right.
   * @return Nothing.
   */
  public void moveRight() {
    mouse.translate((int)UNIT, 0);
    column = y += (int)UNIT; 
  }

  /**
   * Moves the mouse one unit to the left.
   * @return Nothing.
   */
  public void moveLeft() {
    mouse.translate((int)-UNIT, 0);
    row = x -= (int)UNIT;
  }

  /**
   * Moves the mouse one unit up.
   * @return Nothing.
   */
  public void moveUp() {
    mouse.translate(0, (int)-UNIT);
    column = y -= (int)UNIT;
  }

  /**
   * Moves the mouse one unit down.
   * @return Nothing.
   */
  public void moveDown() {
    mouse.translate( 0, (int)UNIT );
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

