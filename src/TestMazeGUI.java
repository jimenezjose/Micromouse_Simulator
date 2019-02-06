import java.io.*;
import java.util.PriorityQueue;

class TestMazeGUI {


  public void testPQNode() {
    PQNode<MazeNode> node_A = new PQNode<MazeNode>( 1, new MazeNode(0, 0) );
    PQNode<MazeNode> node_B = new PQNode<MazeNode>( 2, new MazeNode(0, 0) );
    PQNode<MazeNode> node_C = new PQNode<MazeNode>( 3, new MazeNode(0, 0) );
    PQNode<MazeNode> node_D = new PQNode<MazeNode>( 4, new MazeNode(0, 0) );

    PriorityQueue<PQNode> pq = new PriorityQueue<PQNode>();

    pq.add( node_A );
    pq.add( node_B );
    pq.add( node_C );
    pq.add( node_D );

    System.err.println( "insert in order: " + pq );

    pq.clear();

    pq.add( node_D );
    pq.add( node_C );
    pq.add( node_B );
    pq.add( node_A );

    System.err.println( "insert reverse: " + pq );
  }

  public static void main( String[] args ) {
    TestMazeGUI maze = new TestMazeGUI();
    maze.testPQNode();
  }

}
