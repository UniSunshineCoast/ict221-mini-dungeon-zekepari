package dungeon.engine;

import java.io.Serializable;

/**
 * Represents an immutable position in the game grid.
 * Valid positions have coordinates in the range 0-9.
 */
public class Position implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final int row;
    private final int col;
    
    // The bounds of the game grid
    private static final int MIN_BOUND = 0;
    private static final int MAX_BOUND = 9;

    /**
     * Creates a new Position with the given coordinates.
     *
     * @param row the row coordinate
     * @param col the column coordinate
     * @throws IllegalArgumentException if the coordinates are out of bounds
     */
    public Position(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IllegalArgumentException("Position coordinates must be between " + 
                                              MIN_BOUND + " and " + MAX_BOUND + ", got: (" + row + "," + col + ")");
        }
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row coordinate.
     *
     * @return the row coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate.
     *
     * @return the column coordinate
     */
    public int getCol() {
        return col;
    }

    /**
     * Creates a new Position by adding the offsets of the given Direction to this Position.
     * Returns null if the resulting position would be out of bounds.
     *
     * @param direction the Direction to move in
     * @return a new Position in the given direction, or null if out of bounds
     */
    public Position plus(Direction direction) {
        if (direction == null) {
            return this;
        }
        
        int newRow = row + direction.getDRow();
        int newCol = col + direction.getDCol();
        
        if (isInBounds(newRow, newCol)) {
            return new Position(newRow, newCol);
        } else {
            return null;
        }
    }

    /**
     * Checks if the given coordinates are within the valid bounds.
     *
     * @param row the row coordinate
     * @param col the column coordinate
     * @return true if the coordinates are in bounds, false otherwise
     */
    private static boolean isInBounds(int row, int col) {
        return row >= MIN_BOUND && row <= MAX_BOUND && col >= MIN_BOUND && col <= MAX_BOUND;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "Position(" + row + "," + col + ")";
    }
}
