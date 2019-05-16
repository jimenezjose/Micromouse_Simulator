import java.io.*;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

public class RenderPanel extends JPanel {

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


  public RenderPanel() {

  }


  @Override
  protected void paintComponent( Graphics g ) {
    super.paintComponent( g );
  }

}
