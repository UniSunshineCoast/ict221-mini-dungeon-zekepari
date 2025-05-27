package dungeon.engine;

import dungeon.engine.cells.*;
import java.util.Random;

public class GameEngine {

    /**
     * The game map for the current dungeon level.
     */
    private GameMap gameMap;
    
    /**
     * The player in the game.
     */
    private Player player;
    
    /**
     * The random number generator used for the game.
     */
    private Random rng;
    
    /**
     * The current difficulty level.
     */
    private int difficulty;

    /**
     * The maximum number of steps a player can take before losing.
     */
    private static final int MAX_STEPS = 100;
    
    /**
     * The number of levels required to win the game.
     */
    private static final int WINNING_LEVEL = 2;
    
    /**
     * Flag indicating if the game is over.
     */
    private boolean gameOver;
    
    /**
     * The status message explaining the game state.
     */
    private String statusMessage;

    // The seed of the random number generator
    private long seed;

    /**
     * Creates a new game with the specified difficulty.
     *
     * @param difficulty the difficulty level (affects number of enemies)
     */
    public GameEngine(int difficulty) {
        this(difficulty, System.currentTimeMillis());
    }
    
    /**
     * Creates a new game with the specified difficulty and random seed.
     * This constructor is useful for testing or creating reproducible games.
     *
     * @param difficulty the difficulty level (affects number of enemies)
     * @param seed the seed for the random number generator
     */
    public GameEngine(int difficulty, long seed) {
        this(difficulty, new Random(seed));
        this.seed = seed;
    }
    
    /**
     * Creates a new game with the specified difficulty and random number generator.
     *
     * @param difficulty the difficulty level (affects number of enemies)
     * @param rng the random number generator to use
     */
    public GameEngine(int difficulty, Random rng) {
        this.difficulty = difficulty;
        this.rng = rng;
        this.seed = 0; // Unknown seed when Random is provided directly
        this.gameMap = new GameMap(difficulty, rng);
        this.gameOver = false;
        this.statusMessage = "Game in progress. Good luck!";
        
        // Create a player at the entry position
        player = new Player(new Position(0, 0));
    }

    /**
     * The size of the current game map.
     *
     * @return this is both the width and the height.
     */
    public int getSize() {
        return gameMap.getSize();
    }

    /**
     * Gets the current game map.
     *
     * @return the map of the current level
     */
    public Cell[][] getMap() {
        return gameMap.getGrid();
    }
    
    /**
     * Gets the player object.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Moves the player in the specified direction if possible.
     * 
     * @param direction the direction to move
     * @return true if the move was successful, false otherwise
     */
    public boolean movePlayer(Direction direction) {
        // If the game is already over, don't allow further moves
        if (isGameOver()) {
            return false;
        }
        
        if (direction == null) {
            return false;
        }
        
        Position currentPosition = player.getPosition();
        Position newPosition = currentPosition.plus(direction);
        
        // Check if the move is valid (not out of bounds)
        if (newPosition == null) {
            return false;
        }
        
        // Get the destination cell
        Cell destinationCell = gameMap.cellAt(newPosition);
        
        // Check if the destination is null (out of bounds) or a wall
        if (destinationCell == null || destinationCell instanceof WallCell) {
            return false;
        }
        
        // Move is valid, so update player position
        player.setPosition(newPosition);
        
        // Trigger the cell's onEnter behavior
        destinationCell.onEnter(player, this);
        
        // Update step count
        player.incrementSteps();
        
        // Check game over conditions
        checkGameOverConditions();
        
        return true;
    }
    
    /**
     * Replaces the cell at the specified position with a new cell.
     * This is used when items are consumed or enemies are defeated.
     *
     * @param position the position of the cell to replace
     * @param newCell the new cell to place at that position
     */
    public void replaceCell(Position position, Cell newCell) {
        gameMap.setCell(position, newCell);
    }
    
    /**
     * Gets the current difficulty level.
     *
     * @return the difficulty level
     */
    public int getDifficulty() {
        return difficulty;
    }
    
    /**
     * Checks if the game is over (either won or lost).
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Gets a message describing the current game status.
     * This will include win/loss messages when the game is over.
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }
    
    /**
     * Gets the seed used for the random number generator.
     *
     * @return the seed
     */
    public long getSeed() {
        return seed;
    }
    
    /**
     * Checks the game's win and loss conditions:
     * - Win: Player reaches level 2 and finds the ladder
     * - Lose: Player's HP drops to 0 or steps exceed 100
     */
    private void checkGameOverConditions() {
        // Check for loss conditions
        if (player.getHp() <= 0) {
            gameOver = true;
            statusMessage = "Game Over! You ran out of health.";
        } else if (player.getSteps() >= MAX_STEPS) {
            gameOver = true;
            statusMessage = "Game Over! You ran out of steps.";
        }
        
        // Note: Win condition is checked in advanceToNextLevel()
    }
    
    /**
     * Advances the player to the next dungeon level.
     * If they've completed the final level, they win the game.
     */
    public void advanceToNextLevel() {
        // Increase player's level
        int newLevel = player.getLevel() + 1;
        player.setLevel(newLevel);
        
        // Check if they've won the game
        if (newLevel > WINNING_LEVEL) {
            gameOver = true;
            statusMessage = "Congratulations! You've completed all levels and won the game!";
            return;
        }
        
        // Generate a new map for the next level
        this.gameMap = new GameMap(difficulty, rng);
        
        // Place player at the entry point of the new level
        player.setPosition(new Position(0, 0));
        
        // Update status message
        statusMessage = "You reached level " + newLevel + "! Find the ladder to continue.";
    }
    
    /**
     * Creates a new game with the specified difficulty.
     * This is a convenience method for starting a new game.
     *
     * @param difficulty the difficulty level
     * @return a new GameEngine instance
     */
    public static GameEngine newGame(int difficulty) {
        return new GameEngine(difficulty);
    }
    
    /**
     * Moves the player in the specified direction, applying all game logic.
     * This method handles movement, cell effects, mutant attacks, and step costs.
     * It also checks for win/loss conditions after the move.
     *
     * @param direction the direction to move
     * @return true if the move was successful, false otherwise
     */
    public boolean move(Direction direction) {
        // If the game is already over, don't allow further moves
        if (isGameOver()) {
            return false;
        }
        
        // Execute the player movement
        boolean moved = movePlayer(direction);
        
        // If the move was successful, apply additional game logic
        if (moved) {
            // The movePlayer method already:
            // - Updates player position
            // - Triggers cell onEnter behavior
            // - Increments step count
            // - Checks game over conditions
            
            // You can add additional game logic here if needed
            
            // Return true since the move was successful
            return true;
        }
        
        // Move was not successful
        return false;
    }
    
    /**
     * Sample main method for testing.
     */
    public static void main(String[] args) {
        GameEngine engine = newGame(2);
        System.out.printf("The size of map is %d * %d\n", engine.getSize(), engine.getSize());
    }
}
