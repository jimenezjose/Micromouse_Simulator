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
import java.util.LinkedList;
import java.lang.Math;

/**
 * MazeGUI will create maze exploring interface.
 */
public class MazeGUI extends JFrame implements ActionListener {

  public static final double MAZE_PROPORTION = 0.49; 
  public static final Color LIGHT_BLACK = new Color( 32, 32, 32 ); 
  public Maze ref_maze;
  public Maze unknown_maze;
  private Point center;
  private JPanel northPanel, southPanel;
  private JButton backButton;
  private JButton continueButton;

  /**
   * Creates sets up MazeGUI 
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

  /**
   * Draws the maze interface of the program.
   */
  private void drawMaze( Graphics g ) {
    center = new Point( getWidth() / 2, getHeight() / 2 );
    int canvas_height = getHeight() - 2 * backButton.getHeight();
    int canvas_width  = getWidth();

    System.err.println( "Width: " + canvas_width + ", Height: " + canvas_height );

    int maze_diameter = (int)(double)( MAZE_PROPORTION * Math.min(canvas_height, canvas_width) ); 
    int maze_radius   = (int)(double)( 0.5 * maze_diameter );
    int maze_offset   = (int)(double)( 0.25 * (canvas_width - 2 * maze_diameter) );

    System.err.println( "Maze Side: " + maze_diameter );

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
  private static final int EVEN = 2;
  private static final String DIM_TOO_LARGE = "Cannot create maze. Dimension: %d, too large\n";
  private int dimension;
  private Node[][] maze;

  public Maze( int dimension ) {
    /* dimension padding added to emulate maze walls */
    this.dimension = dimension;
    maze = new Node[ 2 * dimension - 1 ][ 2 * dimension - 1 ];

    for( int row = 0; row < maze.length; row++ ) { 
      for( int column = 0; column < maze[0].length; column++ ) {
        /* init maze with null indicating a maze wall */
        if( row % EVEN == 0 && column % EVEN == 0 ) {
          maze[ row ][ column ] = new Node( row, column );
	}
	else {
          maze[ row ][ column ] = null;
	}
      }
    }
  }

  public void createRandomMaze() {
    /* set up maze cells with disjoint cell classifiers */

    

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

  /* begin - maze generation data */
  private Node parent = null;
  private LinkedList<Node> children_list = new LinkedList<Node>();
  /* end - maze generation data */

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

  /* Maze Generation Routines */

  /* 
   * Goal: during the maze generation algorithm the parent node 
   *       will always be the representative of a disjoint set.
   *       Otherwise, if parent is null, vertex is the representative.
   *
   * Reason: Constant time disjoint set evaluation. In other words,
   *         Knowing the set of vertex should be fast.
   *         --> inSameSet will be be called more than union
   *       
   */



  public void union( Node vertex ) {
    if( parent != null ) {
      /* recurse up the disjoint set tree - only a depth of 1 max */
      parent.union( vertex );
      return;
    }

    /* union two disjoint sets. - representatives located */
    parent = (vertex.parent == null) ? vertex : vertex.parent;

    while( children_list.size() != 0 ) {
      /* migrates all children to new parent */
      Node child = children_list.pop();
      parent.addChild( child );
    }
  }

  public void addChild( Node vertex ) {
    children_list.push( vertex );
  }

  /* this must be fast. inSame set will be called more than union. */
  /* therefore union will be an expensive operation for constant time set check */

  public boolean inSameSet( Node vertex ) {
    /* roots of both vertice sets */
    Node a_root = this;
    Node b_root = vertex;

    if( parent != null) a_root = parent;
    if( vertex.parent != null ) b_root = vertex.parent;

    return (a_root == b_root);
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
