/* two pairs are equal if both pairs contain the same objects within the container */
/* i.e (x,y) == (y,x) -> true */
class Pair<T,K> {
  public T first;
  public K second;

  public Pair( T first, K second ) {
    this.first = first;
    this.second = second;
  }

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

  @Override
  public String toString() {
    return "( " + first + ", " + second + " )";
  }

}
