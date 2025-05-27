package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents an empty cell in the dungeon that a player can freely move through.
 */
public class EmptyCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = null; // No sprite for empty cells
    
    /**
     * Creates a new EmptyCell with the default sprite.
     */
    public EmptyCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new EmptyCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public EmptyCell(String spritePath) {
        super(spritePath);
    }
}