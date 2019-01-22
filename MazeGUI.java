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

/**
 * MazeGUI will create maze exploring interface.
 */
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

  /**
   * As soon as the program begins to run, initialize the settings of the 
   * canvas
   */
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
    
    setVisible( true );
  }

  /**
   * As soon as the canvas window changes in dimension, paint will be called 
   * implicitly.
   *
   * @param g the canvas object created.
   */
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

  /**
   * Method implemented from class ActionListener; Once a button is pressed this
   * method will be called implicitly. 
   * 
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
   *
   * @param args Command line arguments. 
   */
  public static void main( String[] args ) {
    new MazeGUI( 3 );
    while(true) {}
  }

}

/**
 * Maze will handle the internal maze structures, and ensure a proper graph is
 * implemented. 
 */
class Maze {
  private int dimension;
  private Node[][] maze;

  public Maze( int dimension ) {
    /* dimension padding added to emulate maze walls */
    this.dimension = dimension;
    maze = new Node[ 2 * dimension - 1 ][ 2 * dimension - 1 ];
  }

  public int getDimension() {
    return dimension;
  }

}

/**
 * Node contains information about its location in Maze, and its reachabble
 * neighbors.
 */
class Node {
  private final String ADD_EDGE_ERROR = "Error: attempt to add edge to a pair on non-adjacent nodes. ";
  private final int offset = 2; /* offset to ignore walls */

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

  /**
   * Attaches vertex as neighbobr of currentNode.
   * 
   * @param vertex An adjacent cell in the maze from current node.
   */
  public void addNeighbor( Node vertex ) {
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

  /**
   * Checks if two nodes are equivalent.
   *
   * @param o Generic object.
   */
  @Override
  public boolean equals( Object o ) {
    if( o == this ) return true;
    if( !(o instanceof Node) ) return false;
    Node node = (Node) o;
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
