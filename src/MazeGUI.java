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

/**
 * MazeGUI will create maze exploring interface.
 */
public class MazeGUI extends JFrame implements ActionListener {

  public static final double MAZE_PROPORTION = 0.49; 
  public static final Color LIGHT_BLACK = new Color( 32, 32, 32 ); 
  public Maze ref_maze;
  public Maze unknown_maze;
  private Point center = new Point();
  private JPanel northPanel, southPanel;
  private JButton backButton;
  private JButton continueButton;

  private Point leftMazePoint  = new Point();
  private Point rightMazePoint = new Point();
  private Point currentPoint = new Point();
  private Point rightPoint   = new Point();
  private Point downPoint    = new Point();

  /**
   * Creates sets up MazeGUI 
   * @param dimension number of unit cells per side of square maze.
   */
  public MazeGUI( int dimension ) {
    super( "Maze Graphics" );
    ref_maze = new Maze( dimension );
    unknown_maze = new Maze( dimension );
    ref_maze.createRandomMaze();
    begin();
  }

  /**
   * As soon as the program begins to run, initialize the settings of the 
   * canvas
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
    northPanel.setLayout( new FlowLayout(FlowLayout.LEFT) );
    southPanel.setLayout( new FlowLayout(FlowLayout.RIGHT) );

    /* sets names of new buttons */
    backButton = new JButton( "Back" );
    continueButton = new JButton( "Continue" );

    /* Activates button to register state change */
    continueButton.addActionListener( this );
    backButton.addActionListener( this );

    /* add button to panels */
    northPanel.add( backButton );
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
   */
  @Override
  public void paint( Graphics g ) {
    super.paint( g );
    drawMaze( g );
  }

  /**
   * Draws the maze interface of the program.
   * @param g the canvas object created
   */
  private void drawMaze( Graphics g ) {
    //Graphics2D g2d = (Graphics2D) g;
    center.setLocation( getWidth() / 2, getHeight() / 2 );
    int canvas_height = getHeight() - 2 * backButton.getHeight();
    int canvas_width  = getWidth();
    int wall_width   = 2;
    int num_of_walls = ref_maze.getDimension() - 1;
    int maze_diameter = (int)(double)( MAZE_PROPORTION * Math.min(canvas_height, canvas_width) /*+ num_of_walls * wall_width*/ ); 
    int maze_radius   = (int)(double)( 0.5 * maze_diameter );
    int maze_offset   = (int)(double)( 0.25 * (canvas_width - 2 * maze_diameter) );
    double cell_unit   = (1.0 / ref_maze.getDimension()) * maze_diameter ;
    int wall_height  = (int) cell_unit;

    g.setColor( Color.GRAY );
    g.fillRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.fillRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.setColor( Color.BLACK );
    g.drawRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.drawRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );

    leftMazePoint.setLocation( maze_offset, center.y - maze_radius );
    rightMazePoint.setLocation( center.x + maze_offset, center.y - maze_radius );

    /* Maze Generation graphics */
    Rectangle vertical_wall   = new Rectangle( 0, 0, wall_width, wall_height );
    Rectangle horizontal_wall = new Rectangle( 0, 0, wall_height, wall_width );

    drawGridLines( ref_maze, leftMazePoint, vertical_wall, horizontal_wall, cell_unit );
    drawGridLines( unknown_maze, rightMazePoint, vertical_wall, horizontal_wall, cell_unit );

  }

  /**
   * TODO
   */
  private void drawGridLines( Maze maze, Point mazePoint, Rectangle vertical_wall, Rectangle horizontal_wall, double cell_unit ) {
    Graphics2D g2d = (Graphics2D) getGraphics();

    for( int row = 0; row < maze.getDimension(); row++ ) {
      for( int column = 0; column < maze.getDimension(); column++  ) {
        /* draw walls */
        currentPoint.setLocation( row, column );
        rightPoint.setLocation( row, column + 1 );
        downPoint.setLocation( row + 1, column );

        if( column < maze.getDimension() - 1 && maze.wallBetween(currentPoint, rightPoint) ) {
          /* vertical wall is present to the right of current cell */
          vertical_wall.setLocation( mazePoint.x + (int)((column + 1) * cell_unit), mazePoint.y + (int)(row * cell_unit) );
          g2d.fill( vertical_wall );
        }

        if( row < maze.getDimension() - 1 && maze.wallBetween(currentPoint, downPoint)  ) {
          /* horizontal wall is also present below current cell */
          horizontal_wall.setLocation( mazePoint.x + (int)(column * cell_unit), mazePoint.y + (int)((row + 1) * cell_unit) );
          g2d.fill( horizontal_wall );
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

  }

  /**
   * Where program execution begins, such that a MazeGUI object is created.
   * @param args Command line arguments. 
   */
  public static void main( String[] args ) {
    new MazeGUI( 40 );
    while(true) {}
  }

}
