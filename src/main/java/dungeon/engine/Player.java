package dungeon.engine;

/**
 * Represents a player in the game.
 */
public class Player {
    private Position position;
    private int hp; // Health points
    private int score; // Player's score
    private int steps; // Number of steps taken
    private int level; // Current dungeon level
    
    // Constants for HP limits
    private static final int MIN_HP = 0;
    private static final int MAX_HP = 10;
    
    /**
     * Creates a new player at the given position.
     *
     * @param position the starting position of the player
     */
    public Player(Position position) {
        this.position = position;
        this.hp = MAX_HP; // Start with max health
        this.score = 0;
        this.steps = 0;
        this.level = 1;
    }
    
    /**
     * Gets the current position of the player.
     *
     * @return the current position
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * Sets the player's position.
     *
     * @param position the new position
     */
    public void setPosition(Position position) {
        this.position = position;
    }
    
    /**
     * Attempts to move the player in the specified direction.
     * This method only calculates the new position without performing
     * any collision detection or game logic - that's the GameEngine's responsibility.
     *
     * @param direction the direction to move
     * @return the new position after moving, or null if the move is invalid
     */
    public Position move(Direction direction) {
        if (direction == null) {
            return position;
        }
        
        Position newPosition = position.plus(direction);
        return newPosition;
    }
    
    /**
     * Gets the player's current health points.
     * 
     * @return the current HP
     */
    public int getHp() {
        return hp;
    }
    
    /**
     * Modifies the player's health points, keeping them within valid range.
     *
     * @param amount the amount to change HP by (positive or negative)
     */
    public void modifyHp(int amount) {
        hp += amount;
        if (hp > MAX_HP) {
            hp = MAX_HP;
        }
        if (hp < MIN_HP) {
            hp = MIN_HP;
        }
    }
    
    /**
     * Gets the player's current score.
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Adds points to the player's score.
     *
     * @param points the points to add
     */
    public void addScore(int points) {
        this.score += points;
    }
    
    /**
     * Deals damage to the player, reducing their HP.
     * HP is clamped to the valid range (0-10).
     *
     * @param damage the amount of damage to deal
     */
    public void takeDamage(int damage) {
        modifyHp(-damage);
    }
    
    /**
     * Gets the number of steps the player has taken.
     *
     * @return the step count
     */
    public int getSteps() {
        return steps;
    }
    
    /**
     * Increments the step counter.
     */
    public void incrementSteps() {
        this.steps++;
    }
    
    /**
     * Gets the current dungeon level.
     *
     * @return the current level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Sets the current dungeon level.
     *
     * @param level the new level
     */
    public void setLevel(int level) {
        this.level = level;
    }
}