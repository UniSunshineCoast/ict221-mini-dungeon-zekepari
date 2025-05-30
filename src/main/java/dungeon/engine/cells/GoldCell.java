package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Represents a cell containing gold that gives the player points when collected.
 */
public class GoldCell extends AbstractItemCell {
    private static final String DEFAULT_SPRITE_PATH = "treasure.png";
    private static final int SCORE_VALUE = 2;
    private boolean collected = false;
    
    /**
     * Creates a new GoldCell with the default sprite.
     */
    public GoldCell() {
        super(DEFAULT_SPRITE_PATH);
    }
    
    /**
     * Creates a new GoldCell with a custom sprite.
     *
     * @param spritePath the path to the sprite image
     */
    public GoldCell(String spritePath) {
        super(spritePath);
    }
    
    /**
     * When a player enters a gold cell, they collect the gold and receive points.
     * The gold disappears after being collected.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        if (!collected) {
            player.addScore(SCORE_VALUE);
            collected = true;
            
            // Replace this gold cell with an empty cell in the game map
            Position playerPos = player.getPosition();
            engine.getMap()[playerPos.getRow()][playerPos.getCol()] = new EmptyCell();
            
            engine.logAction("Gold collected! +" + SCORE_VALUE + " points");
        }
    }
    
    /**
     * Checks if the gold has been collected.
     *
     * @return true if the gold has been collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
}