package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents a ladder that allows the player to progress to the next level.
 */
public class LadderCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = "ladder.png";
    
    /**
     * Creates a new LadderCell with the default sprite.
     */
    public LadderCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new LadderCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public LadderCell(String spritePath) {
        super(spritePath);
    }
    
    /**
     * When a player enters a ladder cell, they progress to the next level.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        // Advance to the next level or win the game if this is the final level
        engine.advanceToNextLevel();
    }
}