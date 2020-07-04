package ch.epfl.javass;

/**
 * Class which verifies certain conditions, and have to be verified before starting any procedure
 */
public final class Preconditions {

    /**
     * Insignificant constructor
     * Goal : make impossible the creation of an object of type Preconditions
     */
    private Preconditions() {}

    /**
     * Throws an exception if its parameter b is false, and does nothing otherwise
     */
    public static void checkArgument(boolean b) {
        if(!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws an exception if the parameter index is negative or superior or equal to the parameter size
     * @param index (int): the index to check
     * @param size (int) : the maximum size
     * @return the index itself if it verifies the restrictions
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        else {
            return index;
        }
    }
}
