import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Point;

public class Mouse {

  private final double PROPORTION = 0.3;
  private double UNIT;
  private final JFrame canvas;
  private Point maze_draw_point;
  private int maze_diameter;
  private MouseShape mouse;
  private Maze ref_maze;
  private Maze maze;
  private int row;
  private int column;

  /**
   * Creates mouse object on canvas
   * @param row starting row position of mouse.
   * @param column starting column position of mouse.
   * @param ref_maze radom maze.
   * @param maze empty maze.
   * @param canvas JFrame that the mouse will exist in.
   */
  public Mouse( int row, int column, Maze ref_maze, Maze maze, JFrame canvas ) {
    this.row = row;
    this.column = column;
    this.ref_maze = ref_maze;
    this.maze = maze;
    this.canvas = canvas;
    this.mouse = new MouseShape();
  }

  //public exploreMaze()
  public void exploreMaze() {
    MazeNode startVertex = maze.at( row, column );

  }

  /**
   * Translates the mouse by the given differential.
   * @param dx horizontal differential.
   * @param dy vertical differential.
   * @return Nothing.
   */
  public void move( int dx, int dy ) {
    mouse.translate( dx, dy );
  }
 
  /**
   * Moves the mouse one unit to the right.
   * @return Nothing.
   */
  public void moveRight() {
    mouse.translate((int)UNIT, 0);
  }

  /**
   * Moves the mouse one unit to the left.
   * @return Nothing.
   */
  public void moveLeft() {
    mouse.translate((int)-UNIT, 0);
  }

  /**
   * Moves the mouse one unit up.
   * @return Nothing.
   */
  public void moveUp() {
    mouse.translate(0, (int)-UNIT);
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
   public void setEnvironment( Point maze_draw_point, int maze_diameter ) {
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

