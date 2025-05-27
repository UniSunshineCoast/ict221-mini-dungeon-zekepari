package dungeon.engine.cells;

import dungeon.engine.Player;
import dungeon.engine.GameEngine;

/**
 * Interface for cells in the dungeon.
 * Each cell represents a position in the game grid and defines
 * how it interacts with a player who enters it.
 */
public interface Cell {
    
    /**
     * Called when a player enters this cell.
     * By default, this does nothing, but subclasses can override
     * this to define special behaviors.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    default void onEnter(Player player, GameEngine engine) {
        // Default implementation does nothing
    }
    
    /**
     * Returns the path to the sprite image for this cell.
     *
     * @return the path to the sprite image
     */
    String spritePath();
}
