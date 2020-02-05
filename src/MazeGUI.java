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
 * File Name:   MazeGUI.java
 * Description: Emulate a real-time micromouse environment for quicker debugging 
 *              and efficient testing. 
 */

import java.io.PrintStream;
import java.io.IOException;
import java.io.File;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Image;

/**
 * MazeGUI will create maze exploring interface.
 */
public class MazeGUI implements ActionListener {
  public static final double MAZE_PROPORTION = 0.49;
  private static final File DATAFILE = new File("../datafile");
  private static final int DELAY = 250;
  private static final int EVEN = 2;

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
  private SerialRoute serialComm;
  private JComboBox<String> portComboBox; 

  private boolean runDijkstra = false;
  private boolean runDFS      = false;
  private boolean outputStats = true;

  /**
   * Constructor: Creates and sets up MazeGUI 
   * @param dimension number of unit cells per side of square maze.
   * @param non_tree_edges number of no tree edges in maze graph (adds multiple path solutions).
   * @param dijkstra color the dijkstra path on the reference maze in DIJKSTRA_PATH_COLOR.
   * @param dfs color the dfs path on the reference maze in DFS_PATH_COLOR.
   */
  public MazeGUI( int dimension, int non_tree_edges, boolean dijkstra, boolean dfs ) {
    if( dimension < 1 ) dimension = 1;
    ref_maze   = new Maze( dimension );
    mouse_maze = new Maze( dimension );
    if( ref_maze.loadMaze(DATAFILE) == false ) {
      /* load datafile - otherwise create new random maze if that didn't work */
      ref_maze.createRandomMaze( non_tree_edges, DATAFILE );
    }
    mouse = new Mouse( dimension - 1, 0, ref_maze, mouse_maze );
    runDijkstra = dijkstra;
    runDFS = dfs;
    begin();
  }

  /**
   * Initialize the GUI Environment. 
   * @return Nothing.
   */
  private void begin() {
    JFrame main_frame = new JFrame( "Maze Graphics" );
    main_frame.setSize( 800, 800 );
    main_frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    main_frame.setBackground( Color.BLACK );
    main_frame.setResizable( true );

    northPanel  = new JPanel();
    southPanel  = new JPanel();
    renderPanel = new RenderPanel();

    /* set layout for button panels */
    northPanel.setLayout( new BoxLayout(northPanel, BoxLayout.LINE_AXIS) );
    southPanel.setLayout( new BoxLayout(southPanel, BoxLayout.LINE_AXIS) );

    /* sets names of new buttons */
    animateButton  = new JButton( "Animate" );
    clearButton    = new JButton( "Clear" );
    mazeButton     = new JButton( "New Maze" );
    nextButton     = new JButton( "Next" );

    /* Create port combo box */
    serialComm = SerialRoute.getInstance();
    Vector<String> portList = serialComm.getPortList();
    portList.add( 0, "Disconnected" );
    portComboBox = new JComboBox<String>( portList );
    portComboBox.setMaximumSize( portComboBox.getPreferredSize() );
    portComboBox.setSelectedItem( 0 );

    /* Activates button/comboBox to register state change */
    clearButton.addActionListener( this );
    animateButton.addActionListener( this );
    mazeButton.addActionListener( this );
    nextButton.addActionListener( this );
    portComboBox.addActionListener( this );
    /* Activates multithreaded serial communication on a specified port */
    serialComm.addActionListener( this );

    /* add button to panels */
    northPanel.add( animateButton );
    northPanel.add( Box.createHorizontalGlue() );
    northPanel.add( portComboBox );
    northPanel.add( Box.createHorizontalGlue() );
    northPanel.add( mazeButton );
    southPanel.add( nextButton );
    southPanel.add( Box.createHorizontalGlue() );
    southPanel.add( clearButton );

    /* background color of button panels */
    northPanel.setBackground( Color.BLACK );
    southPanel.setBackground( Color.BLACK );

    /* add panels with their buttons on the final window */
    Container contentPane = main_frame.getContentPane();
    contentPane.add( northPanel, BorderLayout.NORTH );
    contentPane.add( southPanel, BorderLayout.SOUTH );
    contentPane.add( renderPanel, BorderLayout.CENTER );
    contentPane.validate();

    main_frame.setVisible( true );
    timer = new Timer( DELAY, this );
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
    if( evt.getSource() == serialComm ) {
      /* data received from serial port */
      handleSerialCommEvent( evt );
    }

    if( evt.getSource() == clearButton ) {
      /* clear button was pressed */
      handleClearButtonEvent( evt );
    }
    else if( evt.getSource() == portComboBox ) {
      handlePortComboBoxEvent( evt );
    }
    else if( evt.getSource() == animateButton ) {
      /* animate button was pressed */
      handleAnimateButtonEvent( evt );
    }
    else if( evt.getSource() == mazeButton ) {
      /* new maze button was pressed */
      handleMazeButtonEvent( evt );
    }
    else if( evt.getSource() == nextButton || evt.getSource() == timer ) {
      /* animation timer */
      handleNextButtonEvent( evt );
    }
  }

  /**
   * Handles the functionality of the clear button click.
   * @param evt Event that fired from the clear JButton.
   * @return Nothing.
   */
  private void handleClearButtonEvent( ActionEvent evt ) {
    mouse.restart();
    outputStats = true;
    renderPanel.repaint();
  }

  /**
   * Handles the functionality of the Animate button.
   * @param evt Event that fired from the animate JButton.
   * @return Nothing.
   */
  private void handleAnimateButtonEvent( ActionEvent evt ) {
    if( timer.isRunning() == false ) {
      /* start animation */
      timer.start();
      animateButton.setText( "Stop" );
      nextButton.setEnabled( false );
    }
    else {
      /* stop animation */
      timer.stop();
      animateButton.setText( "Animate" );
      nextButton.setEnabled( true );
    }
    renderPanel.repaint();
  }

  /**
   * Handles the functionality of the new maze button click.
   * @param evt Event that fired the maze button click.
   * @return Nothing.
   */
  private void handleMazeButtonEvent( ActionEvent evt ) {
    System.out.println( "\nnew maze" );
    animateButton.setText( "Animate" );
    if( timer.isRunning() == true ) timer.stop();
    nextButton.setEnabled( true );
    ref_maze.clear();
    ref_maze.createRandomMaze( DATAFILE );
    mouse.restart();
    outputStats = true;
    renderPanel.repaint();
  }

  /**
   * Handles the functionality of the next button click.
   * @param evt Event that registered the next button click.
   * @return Nothing.
   */
  private void handleNextButtonEvent( ActionEvent evt ) {
    if( mouse.exploreNextCell() || outputStats ) {
      /* mouse is exploring maze or display mouse statistics after its run */
      renderPanel.repaint();
    }
    else if( mouse.isDone() ) {
      /* mouse is done running. */
      System.out.println("Mouse is done running.");
      if( timer.isRunning() ) timer.stop();
      animateButton.setText( "Animate" );
      animateButton.setEnabled( true );
      nextButton.setEnabled( true );
    }
  }

  /**
   * Handles serial port communication.
   * @param evt Event that was fired by SerialRoute when data is recieved.
   * @return Nothing.
   */
  private void handleSerialCommEvent( ActionEvent evt ) {
    SerialRouteEvent serialEvt = (SerialRouteEvent) evt;
    String data = serialEvt.getReceivedMessage();
    System.err.println("Received: " + data);
  }

  /**
   * Handles 
   */
  private void handlePortComboBoxEvent( ActionEvent evt ) {
    String selectedPort = portComboBox.getSelectedItem().toString();
    String noPort = portComboBox.getItemAt(0).toString();

    if( serialComm.connectTo(selectedPort) ) {
      /* serial communication is binded to the new selected port */
      System.out.println( "Connected: " + selectedPort );
    }
    else if( selectedPort.equals(noPort) ) {
      /* Manual disconnection option */
      System.out.println("Disconnected.");
      serialComm.disconnect();
    }
    else {
      /* unsuccessful port connection */
      System.out.println( "Failed Connection: " + selectedPort );
      portComboBox.setSelectedItem( 0 );
    }

    if( portComboBox.getItemCount() - 1 != serialComm.getAvailablePortCount() ) {
      /* new available port detected. update portComboBox */
      System.out.println("Not implementd: new port detected but not updated in JComboBox");
    }
  }

  /**
   * Handles a double buffered image screen for smooth animations.
   */
  private class RenderPanel extends JPanel {
    private Point leftMazePoint  = new Point();
    private Point rightMazePoint = new Point();
    private Point currentPoint   = new Point();
    private Point rightPoint     = new Point();
    private Point downPoint      = new Point();
    private Point center         = new Point();


    /**
     * Constructor: Creates a JPanel for the maze GUI.
     */
    public RenderPanel() {
      try {
        image = ImageIO.read( new File( "../src/utility/images/UCSD-logo.png" ) );
      }
      catch( IOException e ) {
        System.err.println( "UCSD logo non-existent" );
      }
    }
 
    /**
     * Double buffered image screen paint on GUI.
     * @return Nothing.
     */
    @Override
    protected void paintComponent( Graphics g ) {
      super.paintComponent( g );
      render( g );
    }

    /**
     * Renders the main GUI interface - drawing all GUI components.
     * @param g GUI graphics environment. 
     * @return Nothing.
     */
    private void render( Graphics g ) {
      center.setLocation( getWidth() / 2, getHeight() / 2 );
      int num_of_walls  = ref_maze.getDimension() - 1;
      int maze_diameter = (int)(double)( MAZE_PROPORTION * Math.min(getHeight(), getWidth()) );
      int maze_radius   = (int)(double)( 0.5 * maze_diameter );
      int maze_offset   = (int)(double)( 0.25 * (getWidth() - 2 * maze_diameter) );
      double cell_unit  = (1.0 / ref_maze.getDimension()) * maze_diameter;

      /* draws the UCSD Logo - upper left corner */
      drawImage( g, image, 0, 0, (int)(0.5 * maze_diameter), (int)(0.5 * maze_diameter) );

      /* draws the 2 square mazes in the center of the frame */
      leftMazePoint.setLocation( maze_offset, center.y - maze_radius );
      rightMazePoint.setLocation( center.x + maze_offset, center.y - maze_radius );
      drawMaze( g, leftMazePoint, maze_diameter, ref_maze, false );
      drawMaze( g, rightMazePoint, maze_diameter, mouse_maze, true );

      /* draws mouse on maze */
      mouse.setGraphicsEnvironment( rightMazePoint, maze_diameter );
      mouse.draw( g, MOUSE_COLOR );
     
      if( runDFS ) {
        /* draw dfs path on ref maze */
        drawDFSPath( g, ref_maze, leftMazePoint, ref_maze.getBegin(), ref_maze.getEnd(), cell_unit, DFS_PATH_COLOR );
      }

      if( runDijkstra ) {
        /* draw dijkstra path on ref maze */
        drawDijkstraPath( g, ref_maze, leftMazePoint, ref_maze.getBegin(), ref_maze.getEnd(), cell_unit, DIJKSTRA_PATH_COLOR );
      }

      if( mouse.isDone() ) {
        /* draws path found by mouse and checks if path is most optimal */
        drawMousePath( g, mouse_maze, rightMazePoint, cell_unit, MOUSE_PATH_COLOR );
        if( ref_maze.getDijkstraPath().size() == 0 ) ref_maze.dijkstra( ref_maze.getBegin(), ref_maze.getEnd() );
        drawSolutionMessage( g, center, leftMazePoint, maze_diameter );
      }

      if( mouse.isDone() && outputStats ) {
        /* output statistics about the mouse's run */
        outputStats = false;
        int mouse_visited = mouse.getTotalCellsVisited();
        int total = mouse_maze.getDimension() * mouse_maze.getDimension();
        System.out.println( "Proportion of cells visited by mouse: " + ((double)(mouse_visited) / total * 100) + "% on a dimension of " + mouse_maze.getDimension() + "x" + mouse_maze.getDimension() );
        System.out.println( "Total number of mouse runs: " + mouse.getNumberOfRuns() );
      }
    }

    /**
     * Draws a maze on the GUI.
     * @param g Graphics environment variable.
     * @param mazePoint desired top left point of maze being drawn.
     * @param side length of a square side in pixels of the desired drawn maze.
     * @param maze maze data structure that will be drawn on GUI.
     * @param drawFloodFillValues flag to draw flood fill values of maze.
     * @return Nothing.
     */
    private void drawMaze( Graphics g, Point mazePoint, int side, Maze maze, boolean drawFloodFillValues ) {
      double cell_unit = (1.0 / maze.getDimension()) * side;
      /* Maze Background */
      g.setColor( MAZE_BACKGROUND_COLOR );
      g.fillRect( mazePoint.x, mazePoint.y, side, side );
      g.setColor( MAZE_BORDER_COLOR );
      g.drawRect( mazePoint.x, mazePoint.y, side, side );

      /* Maze Foreground - Maze Generation graphics */
      int wall_width  = 2;
      int wall_height = (int) cell_unit;
      Rectangle vertical_wall   = new Rectangle( 0, 0, wall_width, wall_height );
      Rectangle horizontal_wall = new Rectangle( 0, 0, wall_height, wall_width );
      drawGridLines( g, maze, mazePoint, vertical_wall, horizontal_wall, cell_unit );

      if( drawFloodFillValues ) {
        /* draws flood fill values for every cell in maze */
        drawFloodFillCellValues( g, maze, mazePoint, cell_unit );
      }
    }

    /**
     * Draws an image to the GUI.
     * @param g Graphics environment variable.
     * @param image desired image to be drawn on GUI.
     * @param x top left x-coordinate for desired x-location of image on GUI.
     * @param y top left y-coordinate for desired y-location of image on GUI.
     * @param width width of image to be drawn.
     * @param height height of image to be drawn.
     * @return Nothing.
     */
    private void drawImage( Graphics g, Image image, int x, int y, int width, int height ) {
      if( image == null ) return;
      Image scaled_screen = image.getScaledInstance( width, height, Image.SCALE_SMOOTH );
      BufferedImage scaled_image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
      Graphics2D g2d = scaled_image.createGraphics();
      g2d.drawImage( scaled_screen, 0, 0, null );
      g2d.dispose();
      g.drawImage( scaled_image, x, y, null );
    }
  
    /**
     * Draw Dijkstra's path on maze.
     * @param g Graphics environment variable.
     * @param maze maze graph where dijkstra will run.
     * @param mazePoint top left corner of maze to draw path.
     * @param startVertex starting point for dijkstra.
     * @param endVertex terminating vertex in path.
     * @param cell_unit side dimension of one cell in maze.
     * @param color color of path to be drawn.
     * @return Nothing.
     */
    private void drawDijkstraPath( Graphics g, Maze maze, Point mazePoint, MazeNode startVertex, MazeNode endVertex, double cell_unit, Color color ) {
      if( maze.getDijkstraPath().size() == 0 ) maze.dijkstra( startVertex, endVertex );
      colorPath( g, maze.getDijkstraPath(), color, mazePoint, cell_unit );
    }

    /**
     * Draw DFS path on maze.
     * @param g Graphics environment variable.
     * @param maze maze graph where DFS will run
     * @param mazePoint top left corner of maze to draw path
     * @param startVertex starting point for DFS
     * @param endVertex terminating vertex in path
     * @param cell_unit side dimension of one cell in maze
     * @param color color of path to be drawn
     * @return Nothing.
     */
    private void drawDFSPath( Graphics g, Maze maze, Point mazePoint, MazeNode startVertex, MazeNode endVertex, double cell_unit, Color color ) {
      if( maze.getDFSPath().size() == 0 ) maze.dfs( startVertex, endVertex );
      colorPath( g, maze.getDFSPath(), color, mazePoint, cell_unit );
    }

    /**
     * Draw the path that mouse will take on the next run from the starting point.
     * @param g Graphics environment variable.
     * @param maze the maze that mouse is in.
     * @param mazePoint upper left point of the maze in the GUI.
     * @param cell_unit distance from one cell to its adjacent cell.
     * @param color the color that the mouse path will be drawn in.  
     * @return Nothing.
     */
    private void drawMousePath( Graphics g, Maze maze, Point mazePoint, double cell_unit, Color color ) {
      /* mouse object should do this on its own when ready */
      if( !mouse.isDone() ) return;
      colorPath( g, mouse.getMousePath(), color, mazePoint, cell_unit );
    }

    /**
     * Draws path from traversing path, front to end.
     * @param g Graphics environment variable.
     * @param path sequence of cell nodes that will be traversed and colored on maze gui.
     * @param color color of path to be drawn.
     * @param mazePoint top Left corner of maze that path will draw on.
     * @param cell_unit side dimension of one cell in maze.
     * @return Nothing.
     */
    private void colorPath( Graphics g, LinkedList<MazeNode> path, Color color, Point mazePoint, double cell_unit ) {
      final double PATH_PROPORTION = 0.1;
      Graphics2D g2d = (Graphics2D) g;
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
     * @param g Graphics environment variable.
     * @param maze maze graph 
     * @param mazePoint top left corner of maze in GUI.
     * @param vertical_wall rectangular representation of a vertical wall in maze.
     * @param horizontal_wall rectangular representation of horizontal wall in maze.
     * @param cell_unit side dimension of one cell in maze.
     * @return Nothing.
     */
    private void drawGridLines( Graphics g, Maze maze, Point mazePoint, Rectangle vertical_wall, Rectangle horizontal_wall, double cell_unit ) {
      Graphics2D g2d = (Graphics2D) g;

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
     * @param g Graphics environment variable.
     * @param maze the maze which the node distance (flood fill value) is fetched.
     * @param mazePoint upper left point of which the maze is located in the GUI.
     * @param cell_unit distance from one cell to an adjacent cell in the GUI.
     * @return Nothing.
     */
    void drawFloodFillCellValues( Graphics g, Maze maze, Point mazePoint, double cell_unit ) {
      final double FONT_PROPORTION = 0.5;

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
    private void drawSolutionMessage( Graphics g, Point center, Point mazePoint, int maze_diameter ) {
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
      g.drawString( message, (int)(center.x - width_offset), mazePoint.y + maze_diameter + (int)((getHeight() - maze_diameter) / 4.0) );
    }
  }

  /**
   * MazeGUI program execution.
   * @param args command line arguments.
   * @return Nothing.
   */
  public static void main( String[] args ) {
    int dimension = 16;
    int non_tree_edges = 0;
    boolean dijkstra = true;
    boolean dfs = false;

    for( int index = 0; index < args.length; index++ ) {
      /* parse command line arguments */
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

    MazeGUI gui = new MazeGUI( dimension, non_tree_edges, dijkstra, dfs );
  }
}
