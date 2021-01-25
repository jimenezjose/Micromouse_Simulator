/**
 * Jose Jimenez-Olivas 
 * Brandon Cramer
 * Email: jjj023@ucsd.edu
 * 
 *                 University of California, San Diego
 *                           IEEE Micromouse
 *
 * File Name:   Pair.java
 * Description: Utility object to represent a generic pair.
 */

/**
 * Generic pair object that hold two objects together.
 */
class Pair<T,K> {
  public final T first;
  public final K second;

  /**
   * Creates an immutible pair.
   * @param first element with an alias of first.
   * @param second element in pair with an alias of second.
   */
  public Pair( T first, K second ) {
    this.first = first;
    this.second = second;
  }

  /**
   * Two pairs are equal if the content in the pair are equivalent order does 
   * not matter.
   * @param o object of comparison.
   * @return true if pairs have the same content.
   */
  @Override
  public boolean equals( Object o ) {
    if( o == this ) {
      return true;
    }
    if( !(o instanceof Pair<?,?>) ) {
      return false;
    }

    Pair<?,?> pair = (Pair<?,?>) o;
    boolean elementOneExists = pair.first.equals( first ) || pair.first.equals( second );
    boolean elementTwoExists = pair.second.equals( first ) || pair.second.equals( second );

    if( elementOneExists && elementTwoExists ) {
      return true;
    }
    return false;
  }

  /**
   * String representation of this pair object.
   * @return string representation of this pair.
   */
  @Override
  public String toString() {
    return "( " + first + ", " + second + " )";
  }

}
