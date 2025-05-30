package dungeon.engine.cells;

import dungeon.engine.GameEngine;
import dungeon.engine.Mutant;
import dungeon.engine.Player;
import dungeon.engine.Position;

/**
 * A cell containing a ranged mutant that can shoot at the player from a distance.
 * Ranged mutants check for line-of-sight to the player within 2 tiles in cardinal directions,
 * and have a 50% chance to hit for 2 damage. When stepped on, they award 2 points without damage.
 */
public class RangedMutantCell extends AbstractItemCell {
    
    /**
     * Inner class representing the ranged mutant enemy.
     */
    public static class RangedMutant extends Mutant {
        private static final java.util.Random random = new java.util.Random();
        
        public RangedMutant() {
            super();
        }
        
        @Override
        public String getSpritePath() {
            return "ranged_mutant.png";
        }
        
        /**
         * Checks if the ranged mutant can shoot at the player.
         * Returns true if player is within 2 tiles in a cardinal direction with clear line of sight.
         */
        public boolean canShootAt(Position mutantPos, Position playerPos, GameEngine engine) {
            if (mutantPos == null || playerPos == null) return false;
            
            // Check if player is exactly 2 tiles away in cardinal directions
            int rowDiff = playerPos.getRow() - mutantPos.getRow();
            int colDiff = playerPos.getCol() - mutantPos.getCol();
            
            // Must be exactly 2 tiles in one cardinal direction
            if (Math.abs(rowDiff) == 2 && colDiff == 0) {
                // Check vertical line of sight
                int middleRow = mutantPos.getRow() + (rowDiff / 2);
                Position middlePos = new Position(middleRow, mutantPos.getCol());
                Cell middleCell = engine.getMap()[middlePos.getRow()][middlePos.getCol()];
                return !(middleCell instanceof WallCell);
            } else if (Math.abs(colDiff) == 2 && rowDiff == 0) {
                // Check horizontal line of sight
                int middleCol = mutantPos.getCol() + (colDiff / 2);
                Position middlePos = new Position(mutantPos.getRow(), middleCol);
                Cell middleCell = engine.getMap()[middlePos.getRow()][middlePos.getCol()];
                return !(middleCell instanceof WallCell);
            }
            
            return false;
        }
        
        /**
         * Attempts to shoot at the player with 50% hit chance.
         * Returns true if shot was fired and hit.
         */
        public boolean shootAt(Player player) {
            if (random.nextBoolean()) { // 50% chance
                player.takeDamage(DAMAGE);
                return true;
            }
            return false;
        }
    }
    
    private RangedMutant mutant;
    
    /**
     * Creates a new ranged mutant cell.
     */
    public RangedMutantCell() {
        super("ranged_mutant.png");
        this.mutant = new RangedMutant();
    }
    
    /**
     * Returns the mutant contained in this cell.
     */
    public RangedMutant getMutant() {
        return mutant;
    }
    
    @Override
    public void onEnter(Player player, GameEngine engine) {
        // When player steps on ranged mutant, gain points but take no damage
        player.addScore(Mutant.POINTS);
        
        // Replace this cell with an empty cell (mutant is defeated)
        Position currentPos = player.getPosition();
        engine.replaceCell(currentPos, new EmptyCell());
        
        // Log the event
        engine.logAction("Defeated ranged mutant! +" + Mutant.POINTS + " points");
    }
    
    /**
     * Processes the ranged mutant's turn - checks for shots at the player.
     */
    public void processTurn(Position mutantPos, GameEngine engine) {
        Player player = engine.getPlayer();
        Position playerPos = player.getPosition();
        
        if (mutant.canShootAt(mutantPos, playerPos, engine)) {
            boolean hit = mutant.shootAt(player);
            if (hit) {
                engine.logAction("Ranged mutant shot hit! -" + RangedMutant.DAMAGE + " HP");
            } else {
                engine.logAction("Ranged mutant shot missed!");
            }
        }
    }
}
