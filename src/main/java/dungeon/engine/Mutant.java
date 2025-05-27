package dungeon.engine;

/**
 * Abstract base class for all mutant enemies in the dungeon.
 * Mutants deal damage to the player and award points when defeated.
 */
public abstract class Mutant {
    
    /** Damage dealt by all mutants when they attack */
    public static final int DAMAGE = 2;
    
    /** Points awarded when a mutant is defeated */
    public static final int POINTS = 2;
    
    /**
     * Attack the player, dealing damage.
     * This method handles the common attack logic for all mutants.
     *
     * @param player the player to attack
     * @param engine the game engine
     */
    public void attack(Player player, GameEngine engine) {
        player.takeDamage(DAMAGE);
    }
    
    /**
     * Award points to the player for defeating this mutant.
     *
     * @param player the player to award points to
     */
    public void giveReward(Player player) {
        player.addScore(POINTS);
    }
    
    /**
     * Get the sprite path for this mutant.
     * Subclasses must implement this to define their appearance.
     *
     * @return the path to the mutant's sprite image
     */
    public abstract String getSpritePath();
}