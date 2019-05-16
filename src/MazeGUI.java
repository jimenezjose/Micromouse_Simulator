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
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

/**
 * MazeGUI will create maze exploring interface.
 */
public class MazeGUI extends JFrame implements ActionListener {

  public static final double MAZE_PROPORTION = 0.49; 

  private static final int EVEN = 2;
  private static final int DELAY = 250;
  private static final Color LIGHT_BLACK   = new Color( 32, 32, 32 ); 
  private static final Color NO_WALL_COLOR = new Color( 135, 135, 135 );
  private static final Color WALL_COLOR            = Color.BLACK;
  private static final Color MAZE_BORDER_COLOR     = Color.BLACK;
  private static final Color MOUSE_COLOR           = Color.YELLOW;
  private static final Color DIJKSTRA_PATH_COLOR   = Color.RED;
  private static final Color DFS_PATH_COLOR        = Color.BLUE;
  private static final Color MAZE_BACKGROUND_COLOR = Color.GRAY;
  private static final Color NUMBER_COLOR          = Color.DARK_GRAY;
  private static final Color MOUSE_PATH_COLOR      = Color.YELLOW;
  private static final Color EXCITEMENT_COLOR      = Color.BLUE;
  private static BufferedImage image = null;

  private Maze ref_maze;
  private Maze mouse_maze;
  private Mouse mouse; 

  private Timer timer;
  private JPanel northPanel;
  private JPanel southPanel;
  private JPanel renderPanel;

  private JButton animateButton;
  private JButton clearButton;
  private JButton mazeButton;
  private JButton nextButton;

  private Point leftMazePoint  = new Point();
  private Point rightMazePoint = new Point();
  private Point currentPoint   = new Point();
  private Point rightPoint     = new Point();
  private Point downPoint      = new Point();
  private Point center         = new Point();

  private boolean runDijkstra = false;
  private boolean runDFS      = false;
  private boolean outputStats = true;

  /**
   * Constructor: Creates and sets up MazeGUI 
   * @param dimension number of unit cells per side of square maze.
   */
  public MazeGUI( int dimension, int non_tree_edges, boolean dijkstra, boolean dfs ) {
    super( "Maze Graphics" );
    if( dimension < 1 ) dimension = 1;
    runDijkstra = dijkstra;
    runDFS = dfs;
    ref_maze = new Maze( dimension );
    mouse_maze = new Maze( dimension );
    ref_maze.createRandomMaze( non_tree_edges );
    mouse = new Mouse( dimension - 1, 0, ref_maze, mouse_maze, this );

    try {
      image = ImageIO.read( new File("../images/UCSD-logo.png") );
    }
    catch( IOException e ) {
      System.err.println( "UCSD logo non-existent" );
    }

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
    setResizable( true );

    northPanel = new JPanel();
    southPanel = new JPanel();
    renderPanel = new RenderPanel();

    /* set layout for button panels */
    northPanel.setLayout( new BoxLayout(northPanel, BoxLayout.LINE_AXIS) );
    southPanel.setLayout( new BoxLayout(southPanel, BoxLayout.LINE_AXIS) );
    

    /* sets names of new buttons */
    animateButton  = new JButton( "Animate" );
    clearButton    = new JButton( "Clear" );
    mazeButton     = new JButton( "New Maze" );
    nextButton     = new JButton( "Next" );

    /* Activates button to register state change */
    clearButton.addActionListener( this );
    animateButton.addActionListener( this );
    mazeButton.addActionListener( this );
    nextButton.addActionListener( this );

    /* add button to panels */
    northPanel.add( animateButton );
    northPanel.add( Box.createHorizontalGlue() );
    northPanel.add( mazeButton );
    southPanel.add( nextButton );
    southPanel.add( Box.createHorizontalGlue() );
    southPanel.add( clearButton );

    /* background color of button panels */
    northPanel.setBackground( Color.BLACK );
    southPanel.setBackground( Color.BLACK );

    /* add panels with their buttons on the final window */
    Container contentPane = getContentPane();
    contentPane.add( northPanel, BorderLayout.NORTH );
    contentPane.add( southPanel, BorderLayout.SOUTH );
    contentPane.validate();

    setVisible( true );
    timer = new Timer( DELAY, this );
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
    int canvas_height = getHeight() - 2 * mazeButton.getHeight();
    int canvas_width  = getWidth();
    int wall_width    = 2;
    int num_of_walls  = ref_maze.getDimension() - 1;
    int maze_diameter = (int)(double)( MAZE_PROPORTION * Math.min(canvas_height, canvas_width) ); 
    int maze_radius   = (int)(double)( 0.5 * maze_diameter );
    int maze_offset   = (int)(double)( 0.25 * (canvas_width - 2 * maze_diameter) );
    double cell_unit  = (1.0 / ref_maze.getDimension()) * maze_diameter ;
    int wall_height   = (int) cell_unit;

    g.setColor( MAZE_BACKGROUND_COLOR );
    g.fillRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.fillRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.setColor( MAZE_BORDER_COLOR );
    g.drawRect( maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );
    g.drawRect( center.x + maze_offset, center.y - maze_radius, maze_diameter, maze_diameter );

    if( image != null ) {
      Image scaled_screen = image.getScaledInstance( (int)(maze_diameter * 0.5), (int)(maze_diameter * 0.5), Image.SCALE_SMOOTH );
      BufferedImage scaled_image = new BufferedImage( (int)(maze_diameter * 0.5), (int)(maze_diameter * 0.5), BufferedImage.TYPE_INT_ARGB );
      Graphics2D g2d = scaled_image.createGraphics();
      g2d.drawImage( scaled_screen, 0, 0, null );
      g2d.dispose();
      g.drawImage( scaled_image, 0, (int)(1.25 * mazeButton.getHeight()), null );
    }

    leftMazePoint.setLocation( maze_offset, center.y - maze_radius );
    rightMazePoint.setLocation( center.x + maze_offset, center.y - maze_radius );

    /* Maze Generation graphics */
    Rectangle vertical_wall   = new Rectangle( 0, 0, wall_width, wall_height );
    Rectangle horizontal_wall = new Rectangle( 0, 0, wall_height, wall_width );

    drawGridLines( ref_maze, leftMazePoint, vertical_wall, horizontal_wall, cell_unit );
    drawGridLines( mouse_maze, rightMazePoint, vertical_wall, horizontal_wall, cell_unit );

    drawFloodFillCellValues( mouse_maze, rightMazePoint, cell_unit );

    mouse.setGraphicsEnvironment( rightMazePoint, maze_diameter );
    mouse.draw( MOUSE_COLOR );

    MazeNode startVertex = ref_maze.at( ref_maze.getDimension() - 1, 0 );
    MazeNode endVertex = ref_maze.at( ref_maze.getDimension() / EVEN, ref_maze.getDimension() / EVEN );

    if( ref_maze.getDimension() % EVEN == 0 ) {
      /* quad-cell solution set. find initial entrance node */
      int lowerBound = ref_maze.getDimension() / EVEN - 1;
      for( int delta = 0; delta < EVEN; delta++ ) {
        MazeNode topNode = ref_maze.at(lowerBound, lowerBound + delta);
        MazeNode lowerNode = ref_maze.at(lowerBound + 1, lowerBound + delta);
        if( topNode.getNeighborList().size() > EVEN ) {
          endVertex = topNode;
          break;
        }
        if( lowerNode.getNeighborList().size() > EVEN ) {
          endVertex = lowerNode;
          break;
        }
      }
    }

    if( runDFS ) {
      drawDFSPath( ref_maze, leftMazePoint, startVertex, endVertex, cell_unit, DFS_PATH_COLOR );
    }

    if( runDijkstra ) {
      drawDijkstraPath( ref_maze, leftMazePoint, startVertex, endVertex, cell_unit, DIJKSTRA_PATH_COLOR );
    }
    if( mouse.isDone() ) {
      drawMousePath( mouse_maze, rightMazePoint, cell_unit, MOUSE_PATH_COLOR );
      if( ref_maze.getDijkstraPath().size() == 0 ) ref_maze.dijkstra( startVertex, endVertex );
      drawSolutionMessage( g, center, leftMazePoint, maze_diameter, canvas_height );
    }
    
    if( mouse.isDone() && outputStats ) {
      outputStats = false;
      int mouse_visited = mouse.getTotalCellsVisited();
      int total = mouse_maze.getDimension() * mouse_maze.getDimension();
      System.out.println( "Proportion of cells visited by mouse: " + ((double)(mouse_visited) / total * 100) + "% on a dimension of " + mouse_maze.getDimension() + "x" + mouse_maze.getDimension() );
      System.out.println( "Total number of mouse runs: " + mouse.getNumberOfRuns() );
    }
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
    if( maze.getDijkstraPath().size() == 0 ) maze.dijkstra( startVertex, endVertex ); 
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
    if( maze.getDFSPath().size() == 0 ) maze.dfs( startVertex, endVertex ); 
    colorPath( maze.getDFSPath(), color, mazePoint, cell_unit );
  }

  /**
   * draw the path that mouse will take on the next run from the starting point.
   * @param maze the maze that mouse is in.
   * @param mazePoint upper left point of the maze in the GUI.
   * @param cell_unit distance from one cell to its adjacent cell.
   * @param color the color that the mouse path will be drawn in.  
   * @return Nothing.
   */
  private void drawMousePath( Maze maze, Point mazePoint, double cell_unit, Color color ) {
    /* mouse object should do this on its own when ready */
    if( !mouse.isDone() ) return;
    colorPath( mouse.getMousePath(), color, mazePoint, cell_unit );
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

    if( path.size() == 0 ) return;
    /* set starting location and trail width */
    MazeNode currentNode = path.removeFirst();
    int x = mazePoint.x + (int)(currentNode.getDiagonalX() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
    int y = mazePoint.y + (int)(currentNode.getDiagonalY() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
    int sideLength = (int)(PATH_PROPORTION * cell_unit);
    if( sideLength == 0 ) sideLength = 1;
    Rectangle cellBlock = new Rectangle( x, y, sideLength, sideLength );

    while( path.size() != 0 ) {
      /* traverse through path */
      currentNode = path.removeFirst();
      x = mazePoint.x + (int)(currentNode.getDiagonalX() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
      y = mazePoint.y + (int)(currentNode.getDiagonalY() * cell_unit + 0.5 * (1 - PATH_PROPORTION) * cell_unit);
      int current_x = cellBlock.x;
      int current_y = cellBlock.y;

      while( current_x != x || current_y != y ) {
        /* draw trail */
        int dx = ( x - cellBlock.x == 0 ) ? 0 : Math.abs(x - cellBlock.x) / (x - cellBlock.x);
        int dy = ( y - cellBlock.y == 0 ) ? 0 : Math.abs(y - cellBlock.y) / (y - cellBlock.y);
        current_x += dx;
        current_y += dy;
        cellBlock.setLocation( current_x, current_y );
        g2d.fill( cellBlock );
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
	int x = column;
	int y = row;
        currentPoint.setLocation( x, y );
        rightPoint.setLocation( x + 1, y );
        downPoint.setLocation( x, y + 1 );

        /* vertical wall is present to the right of current cell */
        vertical_wall.setLocation( mazePoint.x + (int)((column + 1) * cell_unit), mazePoint.y + (int)(row * cell_unit) );

        /* horizontal wall is also present below current cell */
        horizontal_wall.setLocation( mazePoint.x + (int)(column * cell_unit), mazePoint.y + (int)((row + 1) * cell_unit) );

	if( column < maze.getDimension() - 1 && maze.wallBetween(currentPoint, rightPoint) ) {
          g2d.setColor( WALL_COLOR );
          g2d.fill( vertical_wall );
        }
        else if( column != maze.getDimension() -1 ) {
          g2d.setColor( NO_WALL_COLOR );
          g2d.fill( vertical_wall );
        }

        if( row < maze.getDimension() - 1 && maze.wallBetween(currentPoint, downPoint)  ) {
          g2d.setColor( WALL_COLOR );
          g2d.fill( horizontal_wall );
        }
        else if( row != maze.getDimension() - 1 ) {
          g2d.setColor( NO_WALL_COLOR );
          g2d.fill( horizontal_wall );
        }
      }
    }
  }

  /**
   * Draws the flood fill values on each cell of the given maze.
   * @param maze the maze which the node distance (flood fill value) is fetched.
   * @param mazePoint upper left point of which the maze is located in the GUI.
   * @param cell_unit distance from one cell to an adjacent cell in the GUI.
   * @return Nothing.
   */
  void drawFloodFillCellValues( Maze maze, Point mazePoint, double cell_unit ) {
    final double FONT_PROPORTION = 0.5;
    Graphics g = getGraphics();

    Font numberFont = new Font( Font.SANS_SERIF, Font.BOLD, (int)(FONT_PROPORTION * cell_unit) );
    g.setFont( numberFont );
    g.setColor( NUMBER_COLOR );

    for( MazeNode cell : maze ) {
      /* draw distance (flood fill) values in all cells of the maze */
      if( cell.x == mouse.x && cell.y == mouse.y ) continue;
      double height_offset = ((1 - FONT_PROPORTION) * cell_unit) / 2.0;
      double width_offset  = (cell_unit - g.getFontMetrics().stringWidth(Integer.toString(cell.distance))) / 2.0;
      g.drawString( Integer.toString(cell.distance), mazePoint.x + (int)(cell.x * cell_unit + width_offset), mazePoint.y + (int)((cell.y + 1) * cell_unit - height_offset)); 
    }
  }

  /**
   * Draws a string to the GUI that notifies the user if the most optimal path was found.
   * @param g reference to the GUI graphices component.
   * @param center center of the canvas.
   * @param mazePoint the upper left corner of the maze any maze. (assumption both mazes are in the same section of the GUI)
   * @param maze_diameter length of the maze side in pixels.
   * @param canvas_height height of drawable canvas in GUI.
   * @return Nothing.
   */
  private void drawSolutionMessage( Graphics g, Point center, Point mazePoint, int maze_diameter, int canvas_height ) {
    String message;
    g.setFont( new Font(Font.SANS_SERIF, Font.BOLD, (int)(0.05 * maze_diameter)) );
    g.setColor( EXCITEMENT_COLOR );

    if( ref_maze.getDijkstraPath().size() == mouse.getMousePath().size() ) {
      message = "Most Optimal Solution Found!";
    }
    else {
      message = "Non-optimal. Dijkstra: " + ref_maze.getDijkstraPath().size() + " steps. Flood Fill: " + mouse.getMousePath().size() + " steps.";
    }

    double width_offset  = g.getFontMetrics().stringWidth( message ) / 2.0;
    int charHeight = (int)(0.05 * maze_diameter);
    g.drawString( message, (int)(center.x - width_offset), mazePoint.y + maze_diameter + (int)((canvas_height - maze_diameter) / 4.0) );
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
   
    if( evt.getSource() == clearButton ) {
      /* continue button was pressed */
      mouse.restart();
      outputStats = true;
      repaint();
    }
    else if( evt.getSource() == animateButton ) {
      /* animate button was pressed */
      if( timer.isRunning() == false ) {
        timer.start();
	animateButton.setText( "Stop" );
	nextButton.setEnabled( false );
      }
      else {
        timer.stop();
	animateButton.setText( "Animate" );
	nextButton.setEnabled( true );
      }
      repaint();
    }
    else if( evt.getSource() == mazeButton ) {
      /* new maze button was pressed */
      System.out.println( "\nnew maze" );
      animateButton.setText( "Animate" );
      if( timer.isRunning() == true ) timer.stop();
      nextButton.setEnabled( true );
      ref_maze.clear();
      ref_maze.createRandomMaze();
      mouse.restart();
      outputStats = true;
      repaint();
    }
    else if( evt.getSource() == nextButton || evt.getSource() == timer ) {
      /* animation timer */
      if( mouse.exploreNextCell() || outputStats ) {
        repaint();
      }
    }
  }

  /**
   * Where program execution begins, such that a MazeGUI object is created.
   * @param args Command line arguments. 
   * @return Nothing.
   */
  public static void main( String[] args ) {
    int dimension = 16;
    int non_tree_edges = 0;
    boolean dijkstra = true;
    boolean dfs = false;

    for( int index = 0; index < args.length; index++ ) {
      String flag = args[ index ];
      boolean independent_flag = false;
      boolean invalidFlag = true;

      for( String valid_flag : ParsingStrings.FLAGS ) {
        /* search if arg is a valid flag  */
        if( flag.equals(valid_flag) ) {
          invalidFlag = false;
	  break;
	}
      }

      if( invalidFlag ) {
        /* no such flag defined */
	System.out.println( "Unrecognized Argument: " + args[ index ] + "\n" );
        System.out.println( ParsingStrings.USAGE );
	System.exit( 1 );
      }

      /* independent args */
      switch( flag ) {
        case ParsingStrings.HELP_FLAG_1:
	case ParsingStrings.HELP_FLAG_2:
          /* program usage */
          System.out.println( ParsingStrings.USAGE );
	  System.out.println( ParsingStrings.HELP_MSG );
	  System.exit( 1 );
	  break;
	case ParsingStrings.DIJKSTRA_FLAG:
	  /* run dijkstra */
	  independent_flag = true;
	  dijkstra = true;
	  break;
	case ParsingStrings.DFS_FLAG:
	  /* run dfs */
	  independent_flag = true;
	  dfs = true;
	  break;
      }

      if( independent_flag ) continue;

      /* dependent args */

      if( index + 1 == args.length ) {
        /* invalid number of args */
	System.out.println( "Flag " + args[ index ] + " is expecting an argument." );
        System.out.println( ParsingStrings.USAGE );
	System.exit( 1 );
      }

      switch( flag ) {
	case ParsingStrings.DIM_FLAG_1:
	case ParsingStrings.DIM_FLAG_2:
	  /* dimension input */
	  try {
            dimension = Integer.parseInt( args[ index + 1 ] );   
	  }
	  catch( NumberFormatException e ) {
            System.out.println( "Integer Parsing Error: dimension: " + args[ index + 1 ] + "\n" );
	    System.exit( 1 );
	  }
	  break;
	case ParsingStrings.NUM_PATHS_FLAG_1:
	case ParsingStrings.NUM_PATHS_FLAG_2:
	  /* number of cycles in graph - number of alternative solutions */
	  try {
            non_tree_edges = Integer.parseInt( args[ index + 1 ] );   
	  }
	  catch( NumberFormatException e ) {
            System.out.println( "Integer Parsing Error: non_tree_edges: " + args[ index + 1 ] + "\n" );
            System.out.println( ParsingStrings.USAGE );
	    System.exit( 1 );
	  }
	  if( non_tree_edges < 0 ) {
            System.out.println( "Max Non-Tree Edges Error: argument must be positive: " + non_tree_edges + "\n" );
	    System.out.println( ParsingStrings.USAGE );
	    System.exit( 1 );
	  }
	  break;
      }
      index++;
    }

    if( dimension <= 0 ) {
      System.out.println( "Dimension Argument Error: not positive\n" );
      System.out.println( "Example: java MazeGUI -dimension 16\n" );
      System.out.println( ParsingStrings.USAGE );
      System.exit( 1 );
    }

    MazeGUI maze_frame = new MazeGUI( dimension, non_tree_edges, dijkstra, dfs );

    while(true) {}
  }

}
