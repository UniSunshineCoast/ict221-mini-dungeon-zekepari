package dungeon.engine;

import javafx.scene.input.KeyCode;

/**
 * Represents the four cardinal directions in which the player can move.
 * Each direction has associated row and column offsets.
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int dRow;
    private final int dCol;

    /**
     * Constructor for Direction enum.
     *
     * @param dRow the change in row position
     * @param dCol the change in column position
     */
    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    /**
     * Gets the change in row position for this direction.
     *
     * @return the change in row position
     */
    public int getDRow() {
        return dRow;
    }

    /**
     * Gets the change in column position for this direction.
     *
     * @return the change in column position
     */
    public int getDCol() {
        return dCol;
    }

    /**
     * Maps a key code to a direction.
     *
     * @param keyCode the key code to map
     * @return the corresponding direction, or null if the key doesnt map to a direction
     */
    public static Direction fromKeyCode(KeyCode keyCode) {
        if (keyCode == null) {
            return null;
        }
        
        switch (keyCode) {
            case UP:
            case W:
                return Direction.UP;
            case DOWN:
            case S:
                return Direction.DOWN;
            case LEFT:
            case A:
                return Direction.LEFT;
            case RIGHT:
            case D:
                return Direction.RIGHT;
            default:
                return null;
        }
    }
}
