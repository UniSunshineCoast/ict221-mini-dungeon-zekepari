package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * Abstract base class for all cells that contain items or special terrain.
 * This class provides common functionality for all item cells.
 */
public abstract class AbstractItemCell implements Cell {
    private final String spritePath;
    
    /**
     * Creates a new AbstractItemCell with the given sprite path.
     *
     * @param spritePath the path to the sprite image file
     */
    public AbstractItemCell(String spritePath) {
        this.spritePath = spritePath;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String spritePath() {
        return spritePath;
    }
}