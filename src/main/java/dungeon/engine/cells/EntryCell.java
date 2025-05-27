package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents the entry point to the dungeon.
 * This is where the player starts their journey.
 */
public class EntryCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = null; // No specific sprite for entry
    
    /**
     * Creates a new EntryCell with the default sprite.
     */
    public EntryCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new EntryCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public EntryCell(String spritePath) {
        super(spritePath);
    }
    
    /**
     * When a player enters the entry cell, nothing special happens.
     * However, we could update game state here if needed.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        // The entry cell doesn't do anything special when entered
        // This method could be expanded in the future if needed
    }
}