/**
 * Jose Jimenez-Olivas 
 * Brandon Cramer
 * Email: jjj023@ucsd.edu
 * 
 *                 University of California, San Diego
 *                           IEEE Micromouse
 *
 * File Name:   PQNode.java
 * Description: Utility class for a priority queue node; a nodes with smaller 
 *              weights have higher priority; ie if the priority queue is 
 *              using a max binary heap to store data, PQNode definition will 
 *              convert the max heap to a min binary heap.
 */

import java.lang.Comparable;

/**
 * Generic PQNode object for a min priority queue.
 */
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

  /**
   * Pair comparison feature; to evaluate pair equivalence.
   * @return true if weight and data are equal, false otherwise.
   */
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

  /**
   * String representation of the PQNode object.
   * @return String object of the data and the weight of the PQNode.
   */
  @Override
  public String toString() {
    return "{" + data + ", " + weight + "}";
  }

  /**
   * Getter for the data attribute in this PQNode.
   * @return the data associated with this PQNode.
   */
  public T getData() {
    return data;
  }

  /**
   * Getter method for the weight attribute of this PQNode object.
   * @return the integer weight associated with this PQNode.
   */
  public int getWeight() {
    return weight;
  }
}
