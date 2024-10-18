package procedures;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K and values of type V.
 * Associative Arrays store key/value pairs and permit you to look up values by key.
 * 
 * @author Samuel A. Rebelsky
 * @author Sunjae Kim
 * @param <K> the key type
 * @param <V> the value type
 */
public class AssociativeArray<K, V> {



  
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /** The default capacity of the initial array. */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /** The size of the associative array (the number of key/value pairs). */
  int size;

  /** The array of key/value pairs. */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /** Create a new, empty associative array. */
  @SuppressWarnings({"unchecked"})
  public AssociativeArray() {
    this.pairs = (KVPair<K, V>[]) newInstance(KVPair.class, DEFAULT_CAPACITY);
    this.size = 0;
  } // end of constructor

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> copy = new AssociativeArray<>();

    // Copy each key-value pair (deep copy)
    copy.pairs = (KVPair<K, V>[]) newInstance(KVPair.class, pairs.length);
    for (int i = 0; i < this.size; i++) {
      copy.pairs[i] =
          new KVPair<>(this.pairs[i].key, this.pairs[i].val); // Deep copy of each KVPair
    } //end of for loop

    // Copy the size
    copy.size = this.size;

    return copy;
  } // end of clone method

  /**
   * Adds or updates the key-value pair in the associative array.
   *
   * @param key The key to add or update
   * @param value The value associated with the key
   * @throws NullKeyException if the provided key is null
   */
  public void put(K key, V value) throws NullKeyException {
    if (key == null) {
        throw new NullKeyException("Null key provided");
    }

    // Check if the key already exists
    for (int i = 0; i < size; i++) {
        if (pairs[i].key.equals(key)) {
            // Update the existing value
            pairs[i].val = value;
            return;
        }
    }

    // Add a new key-value pair if the key doesn't exist
    if (size >= pairs.length) {
        expand();  // Expand the array if needed
    }

    System.out.println("Adding key: " + key);  // Debugging: Ensure key is added correctly

    pairs[size] = new KVPair<>(key, value);  // Store the key-value pair
    size++;
}

  /** Convert the array to a string representation for printing. */
  public String toString() {
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < this.size; i++) {
      if (i > 0) {
        result.append(", ");
      } //end of if statement
      result.append(this.pairs[i].key + "=" + this.pairs[i].val);
    } //end of for loop
    return result + "]";
  } // end of toString method

  /**
   * Get the value associated with a key.
   *
   * @param key The key we want to look up
   * @return The value associated with the key
   * @throws KeyNotFoundException if the key is not in the array
   */
  public V get(K key) throws KeyNotFoundException {
    for (int i = 0; i < size; i++) {
      if (pairs[i].key.equals(key)) {
        return pairs[i].val;
      } //end of if statement
    } //end of for loop
    throw new KeyNotFoundException("Key not found: " + key);
  } // end of get method

  /**
   * Determine if key appears in the associative array.
   *
   * @param key The key we're looking for.
   * @return true if the key appears and false otherwise.
   */
  public boolean hasKey(K key) {
    if (key == null) {
      return false;
    } //end of if statement
    for (int i = 0; i < size; i++) {
      if (pairs[i].key.equals(key)) {
        return true;
      } // end of if statement
    } //end of for loop
    return false;
  } // end of hasKey method

  /**
   * Get the key at the given index.
   *
   * @param index The index of the key to retrieve.
   * @return The key at the specified index.
   * @throws IndexOutOfBoundsException if the index is out of bounds.
   */
  public K getKey(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }
    return pairs[index].key;
  }

  /**
   * Remove the key/value pair associated with a key.
   *
   * @param key The key to remove.
   */
  public void remove(K key) {
    for (int i = 0; i < size; i++) {
      if (pairs[i].key.equals(key)) {
        // Shift the remaining elements to fill the gap
        for (int j = i; j < size - 1; j++) {
          pairs[j] = pairs[j + 1];
        } //end of for loop
        pairs[size - 1] = null; // Clear the last entry
        size--; // Reduce the size of the array
        return;
      } //end of if statement
    } //end of for loop
  } // end of remove method

  /**
   * Determine how many key/value pairs are in the associative array.
   *
   * @return The number of key/value pairs in the array.
   */
  public int size() {
    return this.size;
  } // end of size method

  // +-----------------+---------------------------------------------

  /** Expand the underlying array to accommodate more elements. */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // end of expand method

  public String[] keys() {
    String[] keyArray = new String[size];  // Create an array for the keys
    for (int i = 0; i < size; i++) {
        keyArray[i] = pairs[i].key.toString();  // Ensure you're returning the keys as strings
    }
    return keyArray;
}


public boolean containsKey(String imageLoc) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'containsKey'");
}





} // end of class AssociativeArray
