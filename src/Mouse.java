import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class Mouse {

  private final double PROPORTION = 0.3;
  private final double UNIT;
  private final JFrame canvas;
  private MouseShape mouse;

  /**
   * Creates mouse object on canvas
   * @param unit          uniform distance to move the mouse by.
   * @param startingX     starting x coordinate, top left corner of cell/unit.
   * @param startingY     starting y coordinate, top left corner of cell/unit.
   * @param referenceMaze completed maze with sshortest path.
   * @param maze          empty maze.
   * @param canvas        MazeGUI object to have access to repaint.
   */
  public Mouse(double unit, double startingX, double startingY, Maze ref_maze, Maze maze, JFrame canvas ) {
    double unitCenterX = startingX + (UNIT / 2.0);
    double unitCenterY = startingY + (UNIT / 2.0);
    double width = UNIT * PROPORTION; 
    double height = UNIT * PROPORTION; 
    double x = unitCenterX - width / 2.0; 
    double y = unitCenterY - height / 2.0;

    this.UNIT = unit;
    this.canvas = canvas;
    this.mouse = new MouseShape( (int)x, (int)y, width );
  }

  //public exploreMaze()
  
  public void moveRight() {
    mouse.translate((int)UNIT, 0);
  }

  public void moveLeft() {
    mouse.translate((int)-UNIT, 0);
  }

  public void moveUp() {
    mouse.translate(0, (int)-UNIT);
  }

  public void moveDown() {
    mouse.translate( 0, (int)UNIT );
  }

  public void draw( Graphics g, Color color ) {
    mouse.draw( g, color );
  }

  /* Generic mouse shape */
  private class MouseShape {
    private int x;
    private int y;
    private int length;
    private Rectangle body;

    public MouseShape( int x, int y, int length ) {
      this.x = x;
      this.y = y;
      body = new Rectangle( x, y, length, length );
    }

    public void translate( int dx, int dy ) {
      body.translate( dx, dy );
      x +=  dx;
      y += dy;
    }

    public void draw( Graphics g, Color color ) {
      g.setColor( color );
      g.fillRect( x, y, length, length );
    }
  }
}

