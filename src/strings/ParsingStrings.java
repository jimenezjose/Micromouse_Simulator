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
 * File Name:   ParsingStrings.java
 * Description: Contains a list of public strings for user notifications during
 *              the parsing of the given flags. 
 */

/**
 * Output strings for parsing flags.
 */
public class ParsingStrings {

    public static final String USAGE = "Usage: java MazeGUI [-help] [-dimension number ] [-num_of_paths number] [-dijkstra] [-dfs]";
    public static final String HELP_FLAG_1 = "-h";
    public static final String HELP_FLAG_2 = "-help";
    public static final String DIM_FLAG_1 = "-d";
    public static final String DIM_FLAG_2 = "-dimension";
    public static final String NUM_PATHS_FLAG_1 = "-extra_paths";
    public static final String NUM_PATHS_FLAG_2 = "-ep";
    public static final String DIJKSTRA_FLAG = "-dijkstra";
    public static final String DFS_FLAG = "-dfs";
    public static final String HELP_MSG = "\n-help | -h\t\tHelp message\n" + 
      "-dimension | -d\t\tDimension size of maze generated\n-extra_paths | -ep\t" +
      "Total number of extra distinct paths to target.\n-dijkstra\t\tDijkstra's " +
      "Algorithm solution path traversal\n-dfs\t\t\tDepth First Search solution path traversal\n";

    public static final String[] FLAGS = { 
      HELP_FLAG_1, HELP_FLAG_2, DIM_FLAG_1, DIM_FLAG_2, NUM_PATHS_FLAG_1, 
      NUM_PATHS_FLAG_2, DIJKSTRA_FLAG, DFS_FLAG 
    };
}
