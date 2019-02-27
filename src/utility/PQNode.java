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
 * File Name:   PQNode.java
 * Description: Utility class for a priority queue node; a nodes with smaller 
 *              weights have higher priority. i.e. if the priority queue is 
 *              using a max binary heap to store data, PQNode definition will 
 *              convert the max heap to a min binary heap.
 */

import java.lang.Comparable;

class PQNode<T> implements Comparable<PQNode<T>> {
  public final int weight;
  public final T data;

  /**
   * Creates a priority queue node.
   * @param weight or priority of node.
   * @param data the generic data that is associated with its priority.
   */
  PQNode( int weight, T data ) {
    this.weight = weight;
    this.data = data;
  }

  /**
   * PQNode is comparable - for correct implemntation of insert for the priority
   * queue.
   * @param node node to be compared with.
   * @return 0 if equal, negative if node has less priority, positive otherwise.
   */
  @Override
  public int compareTo( PQNode<T> node ) {
    /* Min priority queue node */
    return weight - node.weight;
  }

  @Override
  public boolean equals( Object o ) {
    if( o == this ) {
      return true;
    }

    if( !(o instanceof PQNode<?>) ) {
      return false;
    }

    PQNode<?> pq_node = (PQNode<?>) o;

    if( data.equals(pq_node.data) && weight == pq_node.weight ) {
      return true;
    }

    return false;
  }

  @Override
  public String toString() {
    return "{" + data + ", " + weight + "}";
  }

  public T getData() {
    return data;
  }

  public int getWeight() {
    return weight;
  }
}
