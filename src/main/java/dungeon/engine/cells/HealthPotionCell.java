package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents a health potion that restores the player's HP when collected.
 */
public class HealthPotionCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = "health.png";
    private static final int HEAL_AMOUNT = 4;
    private boolean collected = false;
    
    /**
     * Creates a new HealthPotionCell with the default sprite.
     */
    public HealthPotionCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new HealthPotionCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public HealthPotionCell(String spritePath) {
        super(spritePath);
    }
    
    /**
     * When a player enters a health potion cell, they drink the potion and restore HP.
     * The potion disappears after being consumed.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        if (!collected) {
            player.modifyHp(HEAL_AMOUNT);
            collected = true;
            
            // Replace this potion cell with an empty cell in the game map
            Position playerPos = player.getPosition();
            engine.getMap()[playerPos.getRow()][playerPos.getCol()] = new EmptyCell();
            
            System.out.println("Health potion consumed! +4 HP");
        }
    }
    
    /**
     * Checks if the health potion has been collected.
     *
     * @return true if the potion has been collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
}