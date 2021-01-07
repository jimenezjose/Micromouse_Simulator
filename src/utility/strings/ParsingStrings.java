/**
 *
 * Jose Jimenez
 * Brandon Cramer
 *
 *                 University of California, San Diego
 *                      IEEE Micromouse Team 2020
 *
 * File Name:   ParsingStrings.java
 * Description: Contains a list of public strings for user notifications during
 *              the parsing of the given flags. 
 */

/**
 * Output strings for parsing flags.
 */
public class ParsingStrings {

    public static final String USAGE = "Usage: java MazeGUI [-help] [-dimension <number>] [-non_tree_edges <number>] [-dijkstra] [-dfs]";
    public static final String HELP_FLAG_1 = "-h";
    public static final String HELP_FLAG_2 = "-help";
    public static final String DIM_FLAG_1 = "-d";
    public static final String DIM_FLAG_2 = "-dimension";
    public static final String NUM_PATHS_FLAG_1 = "-non_tree_edges";
    public static final String NUM_PATHS_FLAG_2 = "-nte";
    public static final String DIJKSTRA_FLAG = "-dijkstra";
    public static final String DFS_FLAG = "-dfs";
    public static final String HELP_MSG = "\n-help | -h\t\tHelp message\n" + 
      "-dimension | -d\t\tDimension size of maze generated\n-non_tree_edges | -nte\t" +
      "Total number of non-tree edges in maze.\n-dijkstra\t\tDijkstra's " +
      "Algorithm solution path traversal\n-dfs\t\t\tDepth First Search solution path traversal\n";

    public static final String[] FLAGS = { 
      HELP_FLAG_1, HELP_FLAG_2, DIM_FLAG_1, DIM_FLAG_2, NUM_PATHS_FLAG_1, 
      NUM_PATHS_FLAG_2, DIJKSTRA_FLAG, DFS_FLAG 
    };
}
