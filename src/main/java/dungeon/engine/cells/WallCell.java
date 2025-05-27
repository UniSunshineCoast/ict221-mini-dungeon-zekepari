package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents a wall cell that blocks player movement.
 */
public class WallCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = null; // No sprite for walls
    
    /**
     * Creates a new WallCell with the default sprite.
     */
    public WallCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new WallCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public WallCell(String spritePath) {
        super(spritePath);
    }
    
    /**
     * A wall blocks movement, so this method is never actually called
     * when movement is properly checked.
     * 
     * @param player the player attempting to enter
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        // Walls block movement, so this shouldn't be called
        // If it is called, we'll keep the player in their current position
    }
    
    /**
     * Checks if this cell blocks movement.
     * 
     * @return true because walls always block movement
     */
    public boolean blocksMovement() {
        return true;
    }
}