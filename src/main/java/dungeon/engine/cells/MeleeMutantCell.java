package dungeon.engine.cells;

import dungeon.engine.*;

/**
 * A cell containing a melee mutant enemy.
 * When the player enters this cell, the mutant attacks the player,
 * dealing damage. The player then defeats the mutant, gaining points,
 * and the mutant disappears (cell becomes empty).
 */
public class MeleeMutantCell extends AbstractItemCell {
    
    private final MeleeMutant mutant;
    
    /**
     * Creates a new melee mutant cell.
     */
    public MeleeMutantCell() {
        super("zombie.png"); // Sprite path for melee mutant
        this.mutant = new MeleeMutant();
    }
    
    /**
     * When the player enters this cell, the mutant attacks and is then defeated.
     * The player takes damage but gains points, and the cell becomes empty.
     *
     * @param player the player entering the cell
     * @param engine the game engine
     */
    @Override
    public void onEnter(Player player, GameEngine engine) {
        // Mutant attacks player (deals damage)
        mutant.attack(player, engine);
        
        // Player defeats mutant (gains points)
        mutant.giveReward(player);
        
        // Replace this cell with an empty cell (mutant disappears)
        engine.replaceCell(player.getPosition(), new EmptyCell());
    }
    
    /**
     * Inner class representing the melee mutant entity.
     */
    private static class MeleeMutant extends Mutant {
        
        @Override
        public String getSpritePath() {
            return "mutant_melee.png";
        }
    }
}