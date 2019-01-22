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
import java.util.*;

public class MazeGUI extends JFrame implements ActionListener {

  public static final double MAZE_PROPORTION = 0.55; 
  public static final Color LIGHT_BLACK = new Color( 32, 32, 32 ); 
  public Maze maze;
  private Point center;
  private JPanel northPanel, southPanel;
  private JButton backButton;
  private JButton continueButton;

  public MazeGUI( int dimension ) {
    super( "Maze Graphics" );
    maze = new Maze( dimension );
    begin();
  }

  private void begin() {
    setSize( 400, 400 );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setBackground( Color.BLACK );
    getContentPane().setBackground( Color.BLACK );
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
    
    int dimension = 2 * maze_height - 1;
    maze = new boolean[ dimension ][ dimension ];

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        /* build grid - grid lines are set to true */
        if( row % 2 == 1 || column % 2 == 1 ) {
	  maze[row][column] = true; 
	}
      }
    }

    for( int row = 0; row < maze.length; row++ ) {
      for( int column = 0; column < maze[0].length; column++ ) {
        char value = ( maze[row][column] ) ? '|' : ' ';
        System.out.print( value + " " );
      }
      System.out.println();
    }

    setVisible( true );

  }

  @Override
  public void paint( Graphics g ) {
    super.paint( g );
    drawMaze( g );
  }

  private void drawMaze( Graphics g ) {
    center = new Point( getWidth() / 2, getHeight() / 2 );
    int canvas_height = getHeight() - 2 * northPanel.getHeight();
    int canvas_width  = getWidth();

    int maze_diameter = (int)(double)( MAZE_PROPORTION * Math.min(canvas_height, canvas_width) ); 
    int maze_radius   = (int)(double)( 0.5 * maze_diameter );
    int maze_offset   = (int)(double)( 0.25 * (canvas_width - 2 * maze_diameter) );

    g.setColor( Color.GRAY );
    g.fillRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.fillRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    //createRandomMaze();

  }

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

  public static void main( String[] args ) {
    new MazeGUI( 3 );

    while(true) {}
  }

}

class Maze {
  private int dimension;
  private Node[][] maze;

  public Maze( int dim ) {
    dimension = dim;
  }

  public int getDimension() {
    return dimension;
  }

}

class Node {
  public int x = 0;
  public int y = 0;
  public Node up = null;
  public Node down = null;
  public Node left = null;
  public Node right = null;

  public Node( int x, int y ) {
    this.x = x;
    this.y = y;
  }

  public void addEdge( Node vertex ) {
    /* neighbor must be with in 1 unit */
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
    
  }

  @Override
  public boolean equals( Object o ) {
    if( o == this ) return true;
    if( !(o instanceof Node) ) return false;
    Node node = (Node) o;
    if( x ==  node.x && y == node.y ) return true;
    else return false;
  }

}
