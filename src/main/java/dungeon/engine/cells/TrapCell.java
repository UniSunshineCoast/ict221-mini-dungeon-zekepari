package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents a dangerous trap that damages the player when stepped on.
 */
public class TrapCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = "trap.png";
    private static final int DAMAGE = 2;
    
    /**
     * Creates a new TrapCell with the default sprite.
     */
    public TrapCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new TrapCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public TrapCell(String spritePath) {
        super(spritePath);
    }
    
    /**
     * When a player enters a trap cell, they take damage.
     * The trap persists and will damage the player each time they step on it.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        player.modifyHp(-DAMAGE);
        engine.logAction("Trap triggered! -" + DAMAGE + " HP");
    }
}