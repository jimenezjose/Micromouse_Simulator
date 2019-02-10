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
 * File Name:   MazeGUI.java
 * Description: Emulate a real-time micromouse environment for quicker debugging
 *              and efficient testing. 
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.lang.Math;
import java.util.LinkedList;

/**
 * MazeGUI will create maze exploring interface.
 */
public class MazeGUI extends JFrame implements ActionListener {

  public static final double MAZE_PROPORTION = 0.49; 
  public static final Color LIGHT_BLACK = new Color( 32, 32, 32 ); 
  public static final int EVEN = 2;
  private Maze ref_maze;
  private Maze unknown_maze;
  private Mouse mouse; 
  private Point center = new Point();
  private JPanel northPanel, southPanel;
  private JButton backButton;
  private JButton continueButton;
  private JButton mazeButton;

  private static final Color WALL_COLOR = Color.BLACK;
  private static final Color MAZE_BORDER_COLOR = Color.BLACK;
  private static final Color NO_WALL_COLOR = new Color(135, 135, 135);
  private static final Color MOUSE_COLOR = Color.YELLOW;
  private static final Color DJIKSTRA_PATH_COLOR = Color.RED;
  private static final Color MAZE_BACKGROUND_COLOR = Color.GRAY;

  private Point leftMazePoint  = new Point();
  private Point rightMazePoint = new Point();
  private Point currentPoint = new Point();
  private Point rightPoint   = new Point();
  private Point downPoint    = new Point();

  /**
   * Constructor: Creates and sets up MazeGUI 
   * @param dimension number of unit cells per side of square maze.
   */
  public MazeGUI( int dimension ) {
    super( "Maze Graphics" );
    ref_maze = new Maze( dimension );
    unknown_maze = new Maze( dimension );
    ref_maze.createRandomMaze();
    mouse = new Mouse( dimension - 1, 0, ref_maze, unknown_maze, this );
    begin();
  }

  /**
   * As soon as the program begins to run, initialize the settings of the 
   * canvas
   * @return Nothing.
   */
  private void begin() {
    setSize( 800, 800 );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setBackground( Color.BLACK );
    //getContentPane().setBackground( Color.BLACK );
    setResizable( true );

    northPanel = new JPanel();
    southPanel = new JPanel();

    /* set layout for button panels */
    northPanel.setLayout( new BoxLayout(northPanel, BoxLayout.LINE_AXIS) );
    southPanel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
    

    /* sets names of new buttons */
    backButton = new JButton( "Back" );
    continueButton = new JButton( "Continue" );
    mazeButton = new JButton( "New Maze" );

    /* Activates button to register state change */
    continueButton.addActionListener( this );
    backButton.addActionListener( this );
    mazeButton.addActionListener( this );

    /* add button to panels */
    northPanel.add( backButton );
    northPanel.add( Box.createHorizontalGlue() );
    northPanel.add( mazeButton );
    southPanel.add( continueButton );
    northPanel.setBackground( Color.BLACK );
    southPanel.setBackground( Color.BLACK );

    /* add panels with their buttons on the final window */
    Container contentPane = getContentPane();
    contentPane.add( northPanel, BorderLayout.NORTH );
    contentPane.add( southPanel, BorderLayout.SOUTH );
    contentPane.validate();
    
    setVisible( true );
  }

  /**
   * As soon as the canvas window changes in dimension, paint will be called 
   * implicitly.
   * @param g the canvas object created.
   * @return Nothing.
   */
  @Override
  public void paint( Graphics g ) {
    super.paint( g );
    drawMaze( g );
  }

  /**
   * Draws the maze interface of the program.
   * @param g the canvas object created
   * @return Nothing.
   */
  private void drawMaze( Graphics g ) {
    center.setLocation( getWidth() / 2, getHeight() / 2 );
    int canvas_height = getHeight() - 2 * backButton.getHeight();
    int canvas_width  = getWidth();
    int wall_width   = 2;
    int num_of_walls = ref_maze.getDimension() - 1;
    int maze_diameter = (int)(double)( MAZE_PROPORTION * Math.min(canvas_height, canvas_width) ); 
    int maze_radius   = (int)(double)( 0.5 * maze_diameter );
    int maze_offset   = (int)(double)( 0.25 * (canvas_width - 2 * maze_diameter) );
    double cell_unit   = (1.0 / ref_maze.getDimension()) * maze_diameter ;
    int wall_height  = (int) cell_unit;

    g.setColor( MAZE_BACKGROUND_COLOR );
    g.fillRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.fillRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.setColor( MAZE_BORDER_COLOR );
    g.drawRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.drawRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );

    leftMazePoint.setLocation( maze_offset, center.y - maze_radius );
    rightMazePoint.setLocation( center.x + maze_offset, center.y - maze_radius );

    /* Maze Generation graphics */
    Rectangle vertical_wall   = new Rectangle( 0, 0, wall_width, wall_height );
    Rectangle horizontal_wall = new Rectangle( 0, 0, wall_height, wall_width );

    drawGridLines( ref_maze, leftMazePoint, vertical_wall, horizontal_wall, cell_unit );
    drawGridLines( unknown_maze, rightMazePoint, vertical_wall, horizontal_wall, cell_unit );

    //Mouse mouse = new Mouse( ref_maze, unknown_maze, cell_unit, rightMazePoint.x, rightMazePoint.y + maze_diameter - cell_unit );
    mouse.setEnvironment( rightMazePoint, maze_diameter );
    mouse.draw( MOUSE_COLOR );

    MazeNode startVertex = ref_maze.at( ref_maze.getDimension() - 1, 0 );
    MazeNode endVertex = ref_maze.at( ref_maze.getDimension() / EVEN, ref_maze.getDimension() / EVEN );

    //drawDFSPath( ref_maze, leftMazePoint, startVertex, endVertex, cell_unit, Color.PINK );
    drawDijkstraPath( ref_maze, leftMazePoint, startVertex, endVertex,
      cell_unit, DJIKSTRA_PATH_COLOR );
    //ref_maze.dijkstra( startVertex, endVertex );
    //colorPath( ref_maze.optimize(ref_maze.getDijkstraPath()), Color.GREEN, leftMazePoint, cell_unit );
  }


  /**
   * draw Dijkstra's path on maze.
   * @param maze maze graph where dijkstra will run
   * @param mazePoint top left corner of maze to draw path
   * @param startVertex starting point for dijkstra
   * @param endVertex terminating vertex in path
   * @param cell_unit side dimension of one cell in maze
   * @param color color of path to be drawn
   * @return Nothing.
   */
  private void drawDijkstraPath( Maze maze, Point mazePoint, MazeNode startVertex, MazeNode endVertex, double cell_unit, Color color ) {
    if( startVertex == null || endVertex == null ) {
      /* invalid start or end point */
      System.err.println( "MazeGUI.drawDijkstra: Invalid start or end vertex" );
      return;
    }
    maze.dijkstra( startVertex, endVertex );
    colorPath( maze.getDijkstraPath(), color, mazePoint, cell_unit );
  }

  /**
   * draw DFS path on maze.
   * @param maze maze graph where DFS will run
   * @param mazePoint top left corner of maze to draw path
   * @param startVertex starting point for DFS
   * @param endVertex terminating vertex in path
   * @param cell_unit side dimension of one cell in maze
   * @param color color of path to be drawn
   * @return Nothing.
   */
  private void drawDFSPath( Maze maze, Point mazePoint, MazeNode startVertex, MazeNode endVertex, double cell_unit, Color color ) {
    if( startVertex == null || endVertex == null ) {
      /* invalid start or end point */
      System.err.println( "MazeGUI.drawDFS: Invalid start or end vertex" );
      return;
    }

    maze.dfs( startVertex, endVertex );
    colorPath( maze.getDFSPath(), color, mazePoint, cell_unit );
  }

  /**
   * Draws path from traversing path, front to end.
   * @param path sequence of cell nodes that will be traversed and colored on maze gui.
   * @param color color of path to be drawn.
   * @param mazePoint top Left corner of maze that path will draw on.
   * @param cell_unit side dimension of one cell in maze.
   * @return Nothing.
   */
  private void colorPath( LinkedList<MazeNode> path, Color color, Point mazePoint, double cell_unit ) {
    final double PATH_PROPORTION = 0.1;
    Graphics2D g2d = (Graphics2D) getGraphics();
    g2d.setColor( color );

    MazeNode currentNode = path.removeFirst();
    int x = mazePoint.x + (int)(currentNode.getDiagonalY() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
    int y = mazePoint.y + (int)(currentNode.getDiagonalX() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
    int sideLength = (int)(PATH_PROPORTION * cell_unit);
    if( sideLength == 0 ) sideLength = 1;
    Rectangle cellBlock = new Rectangle( x, y, sideLength, sideLength );

    while( path.size() != 0 ) {
      /* traverse through path */
      currentNode = path.removeFirst();
      x = mazePoint.x + (int)(currentNode.getDiagonalY() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
      y = mazePoint.y + (int)(currentNode.getDiagonalX() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);

      int dx = ( x - cellBlock.x == 0 ) ? 0 : Math.abs(x - cellBlock.x) / (x - cellBlock.x);
      int dy = ( y - cellBlock.y == 0 ) ? 0 : Math.abs(y - cellBlock.y) / (y - cellBlock.y);

      int current_x = cellBlock.x;
      int current_y = cellBlock.y;

      while( current_x != x || current_y != y ) {
        /* draw trail */
        current_x += dx;
        current_y += dy;
        cellBlock.setLocation( current_x, current_y );
        g2d.fill( cellBlock );
	/* update dx dy to account casting double to int arithmetic */
        dx = ( x - cellBlock.x == 0 ) ? 0 : Math.abs(x - cellBlock.x) / (x - cellBlock.x);
        dy = ( y - cellBlock.y == 0 ) ? 0 : Math.abs(y - cellBlock.y) / (y - cellBlock.y);
      }

      cellBlock.setLocation( x, y );
      g2d.fill( cellBlock );
    }
  }

  /**
   * Draws grid lines on maze.
   * @param maze maze graph 
   * @param mazePoint top left corner of maze in GUI.
   * @param vertical_wall rectangular representation of a vertical wall in maze.
   * @param horizontal_wall rectangular representation of horizontal wall in maze.
   * @param cell_unit side dimension of one cell in maze.
   * @return Nothing.
   */
  private void drawGridLines( Maze maze, Point mazePoint, Rectangle vertical_wall, Rectangle horizontal_wall, double cell_unit ) {
    Graphics2D g2d = (Graphics2D) getGraphics();

    for( int row = 0; row < maze.getDimension(); row++ ) {
      for( int column = 0; column < maze.getDimension(); column++  ) {
        /* draw walls */
        currentPoint.setLocation( row, column );
        rightPoint.setLocation( row, column + 1 );
        downPoint.setLocation( row + 1, column );

        /* vertical wall is present to the right of current cell */
        vertical_wall.setLocation( mazePoint.x + (int)((column + 1) * cell_unit), mazePoint.y + (int)(row * cell_unit) );

        /* horizontal wall is also present below current cell */
        horizontal_wall.setLocation( mazePoint.x + (int)(column * cell_unit), mazePoint.y + (int)((row + 1) * cell_unit) );

        if( column < maze.getDimension() - 1 && maze.wallBetween(currentPoint, rightPoint) ) {
          g2d.setColor(WALL_COLOR);
          g2d.fill( vertical_wall );
        }

        else  {
          g2d.setColor( NO_WALL_COLOR );
          g2d.fill(vertical_wall);
        }

        if( row < maze.getDimension() - 1 && maze.wallBetween(currentPoint, downPoint)  ) {
          g2d.setColor( WALL_COLOR );
          g2d.fill( horizontal_wall );
        }

        else {
          g2d.setColor( NO_WALL_COLOR );
          g2d.fill(horizontal_wall);
        }
      }
    }

  }

  /**
   * Method implemented from class ActionListener; Once a button is pressed this
   * method will be called implicitly. 
   * @param evt generic object that records information about the change in 
   *            state of the "listening" object. In this case the "listening
   *            object" is a JButton.
   * @return Nothing.
   */
  public void actionPerformed( ActionEvent evt ) {
   
    if( evt.getSource() == continueButton ) {
      /* continue button was pressed */
      System.out.println( "continue" );
    }
    else if( evt.getSource() == backButton ) {
      /* back button was pressed */
      System.out.println( "back" );
    }
    else if( evt.getSource() == mazeButton ) {
      /* new maze button was pressed */
      System.err.println( "\nnew maze" );
      ref_maze.clear();
      ref_maze.createRandomMaze();
      repaint();

    }

  }

  /**
   * Where program execution begins, such that a MazeGUI object is created.
   * @param args Command line arguments. 
   * @return Nothing.
   */
  public static void main( String[] args ) {
    new MazeGUI( 16 );
    while(true) {}
  }

}
