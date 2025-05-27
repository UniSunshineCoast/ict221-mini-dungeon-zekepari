package dungeon.engine.persistence;

import dungeon.engine.*;
import dungeon.engine.cells.Cell;

import java.io.*;
import java.util.Arrays;

/**
 * A serializable snapshot of the game state.
 * This allows saving and loading games.
 */
public class SaveState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Core game state
    private final long seed;
    private final int difficulty;
    private final PlayerState playerState;
    private final CellState[][] mapState;
    private final boolean gameOver;
    private final String statusMessage;
    
    /**
     * Creates a save state from the current game engine state.
     *
     * @param engine the game engine to save
     */
    public SaveState(GameEngine engine) {
        this.seed = engine.getSeed();
        this.difficulty = engine.getDifficulty();
        this.gameOver = engine.isGameOver();
        this.statusMessage = engine.getStatusMessage();
        
        // Save player state
        Player player = engine.getPlayer();
        this.playerState = new PlayerState(
            player.getPosition(),
            player.getHp(),
            player.getScore(),
            player.getSteps(),
            player.getLevel()
        );
        
        // Save map state
        Cell[][] grid = engine.getMap();
        int size = grid.length;
        this.mapState = new CellState[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                this.mapState[row][col] = new CellState(grid[row][col].getClass().getName());
            }
        }
    }
    
    /**
     * Restores a game engine from this save state.
     *
     * @return a new GameEngine instance with the restored state
     */
    public GameEngine restoreGame() {
        // Create a new game with the same seed and difficulty
        GameEngine engine = new GameEngine(difficulty, seed);
        
        // Restore player state
        Player player = engine.getPlayer();
        player.setPosition(playerState.position);
        player.setLevel(playerState.level);
        
        // Set HP (may be different from initial value)
        int hpChange = playerState.hp - player.getHp();
        player.modifyHp(hpChange);
        
        // Set score (may be different from initial value)
        int scoreChange = playerState.score - player.getScore();
        if (scoreChange > 0) {
            player.addScore(scoreChange);
        }
        
        // Set steps (may be different from initial value)
        while (player.getSteps() < playerState.steps) {
            player.incrementSteps();
        }
        
        // Force check of game over conditions to restore the state properly
        if (gameOver || player.getHp() <= 0 || player.getSteps() >= 100) {
            // Trigger a move that will check game over conditions
            engine.move(Direction.UP); // This will fail but trigger the check
        }
        
        return engine;
    }
    
    /**
     * Saves this state to a file.
     *
     * @param file the file to save to
     * @throws IOException if an I/O error occurs
     */
    public void saveToFile(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }
    
    /**
     * Loads a save state from a file.
     *
     * @param file the file to load from
     * @return the loaded save state
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static SaveState loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (SaveState) in.readObject();
        }
    }
    
    /**
     * A serializable snapshot of a player's state.
     */
    private static class PlayerState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final Position position;
        private final int hp;
        private final int score;
        private final int steps;
        private final int level;
        
        public PlayerState(Position position, int hp, int score, int steps, int level) {
            this.position = position;
            this.hp = hp;
            this.score = score;
            this.steps = steps;
            this.level = level;
        }
    }
    
    /**
     * A serializable snapshot of a cell's state.
     */
    private static class CellState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final String cellClassName;
        
        public CellState(String cellClassName) {
            this.cellClassName = cellClassName;
        }
    }
}
