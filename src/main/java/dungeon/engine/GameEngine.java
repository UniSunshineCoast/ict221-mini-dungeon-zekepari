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
     * The action logger for recording game events.
     */
    private ActionLogger actionLogger;

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
     * Sets the action logger for recording game events.
     * 
     * @param logger the action logger to use
     */
    public void setActionLogger(ActionLogger logger) {
        this.actionLogger = logger;
    }
    
    /**
     * Logs a message using the action logger if one is set.
     * 
     * @param message the message to log
     */
    public void logAction(String message) {
        if (actionLogger != null) {
            actionLogger.log(message);
        }
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
        
        // Log the movement
        logAction("Moved " + direction.toString().toLowerCase() + " to (" + newPosition.getRow() + ", " + newPosition.getCol() + ")");
        
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
            logAction("DEFEAT: You ran out of health!");
        } else if (player.getSteps() >= MAX_STEPS) {
            gameOver = true;
            statusMessage = "Game Over! You ran out of steps.";
            logAction("DEFEAT: You ran out of steps!");
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
            logAction("VICTORY: Congratulations! You've completed all levels and won the game!");
            return;
        }
        
        // Generate a new map for the next level
        this.gameMap = new GameMap(difficulty, rng);
        
        // Place player at the entry point of the new level
        player.setPosition(new Position(0, 0));
        
        // Update status message
        statusMessage = "You reached level " + newLevel + "! Find the ladder to continue.";
        logAction("Reached level " + newLevel + "! Find the ladder to continue.");
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
            
            // Process ranged mutant shots after player movement
            processRangedMutantTurns();
            
            // Return true since the move was successful
            return true;
        }
        
        // Move was not successful
        return false;
    }
    
    /**
     * Processes all ranged mutant turns, checking for shots at the player.
     */
    private void processRangedMutantTurns() {
        Cell[][] map = getMap();
        
        // Iterate through all cells looking for ranged mutants
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                Cell cell = map[row][col];
                if (cell instanceof RangedMutantCell) {
                    RangedMutantCell rangedCell = (RangedMutantCell) cell;
                    Position mutantPos = new Position(row, col);
                    
                    // Let the ranged mutant process its turn (attempt to shoot)
                    rangedCell.processTurn(mutantPos, this);
                }
            }
        }
    }
    
    /**
     * Console interface for the game.
     */
    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        // Parse difficulty from command line args or prompt user
        int difficulty = 2; // Default difficulty
        if (args.length > 0) {
            try {
                difficulty = Integer.parseInt(args[0]);
                if (difficulty < 1) difficulty = 1;
                if (difficulty > 5) difficulty = 5;
            } catch (NumberFormatException e) {
                System.out.println("Invalid difficulty argument. Using default difficulty 2.");
            }
        } else {
            System.out.println("=== MiniDungeon Console Interface ===");
            System.out.println("Select difficulty level (1-5, default 2): ");
            System.out.println("1 = Easy (3 Melee Mutants)");
            System.out.println("2 = Medium (3 Melee + 2 Ranged Mutants)");
            System.out.println("3 = Hard (3 Melee + 3 Ranged Mutants)");
            System.out.println("4+ = Expert (3 Melee + 4+ Ranged Mutants)");
            System.out.print("Enter difficulty (1-5) or press Enter for default: ");
            
            String difficultyInput = scanner.nextLine().trim();
            if (!difficultyInput.isEmpty()) {
                try {
                    difficulty = Integer.parseInt(difficultyInput);
                    if (difficulty < 1) difficulty = 1;
                    if (difficulty > 5) difficulty = 5;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Using default difficulty 2.");
                }
            }
        }
        
        GameEngine engine = newGame(difficulty);
        
        // Set up console logging
        engine.setActionLogger(new ConsoleActionLogger());
        
        System.out.println("\n=== MiniDungeon Console Interface ===");
        System.out.println("Difficulty Level: " + difficulty);
        System.out.println("Commands: up, down, left, right, save, load, quit");
        System.out.println("=====================================");
        
        printGameState(engine);
        
        while (!engine.isGameOver()) {
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine().trim().toLowerCase();
            
            switch (command) {
                case "up":
                    handleMove(engine, Direction.UP);
                    break;
                case "down":
                    handleMove(engine, Direction.DOWN);
                    break;
                case "left":
                    handleMove(engine, Direction.LEFT);
                    break;
                case "right":
                    handleMove(engine, Direction.RIGHT);
                    break;
                case "save":
                    saveGame(engine, scanner);
                    break;
                case "load":
                    engine = loadGame(scanner);
                    if (engine == null) {
                        engine = newGame(2);
                        System.out.println("Load failed, starting new game.");
                    }
                    break;
                case "quit":
                    System.out.println("Thanks for playing!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Unknown command. Use: up, down, left, right, save, load, quit");
                    continue;
            }
            
            printGameState(engine);
            
            if (engine.isGameOver()) {
                System.out.println("\n" + engine.getStatusMessage());
                System.out.print("Play again? (y/n): ");
                String playAgain = scanner.nextLine().trim().toLowerCase();
                if (playAgain.equals("y") || playAgain.equals("yes")) {
                    engine = newGame(2);
                    System.out.println("\n=== New Game Started ===");
                    printGameState(engine);
                } else {
                    break;
                }
            }
        }
        
        scanner.close();
        System.out.println("Game ended. Thanks for playing!");
    }
    
    private static void handleMove(GameEngine engine, Direction direction) {
        boolean moved = engine.move(direction);
        if (!moved) {
            System.out.println("Cannot move in that direction!");
        }
    }
    
    private static void printGameState(GameEngine engine) {
        System.out.println("\n=== Dungeon Level " + engine.getPlayer().getLevel() + " ===");
        
        // Print the grid
        Cell[][] map = engine.getMap();
        Position playerPos = engine.getPlayer().getPosition();
        
        for (int row = 0; row < engine.getSize(); row++) {
            for (int col = 0; col < engine.getSize(); col++) {
                if (row == playerPos.getRow() && col == playerPos.getCol()) {
                    System.out.print("P ");  // Player
                } else {
                    Cell cell = map[row][col];
                    if (cell instanceof dungeon.engine.cells.WallCell) {
                        System.out.print("# ");
                    } else if (cell instanceof dungeon.engine.cells.GoldCell) {
                        System.out.print("G ");
                    } else if (cell instanceof dungeon.engine.cells.HealthPotionCell) {
                        System.out.print("H ");
                    } else if (cell instanceof dungeon.engine.cells.TrapCell) {
                        System.out.print("T ");
                    } else if (cell instanceof dungeon.engine.cells.LadderCell) {
                        System.out.print("L ");
                    } else if (cell instanceof dungeon.engine.cells.MeleeMutantCell) {
                        System.out.print("M ");
                    } else if (cell instanceof dungeon.engine.cells.RangedMutantCell) {
                        System.out.print("R ");
                    } else if (cell instanceof dungeon.engine.cells.EntryCell) {
                        System.out.print("E ");
                    } else {
                        System.out.print(". ");  // Empty
                    }
                }
            }
            System.out.println();
        }
        
        // Print status
        Player player = engine.getPlayer();
        System.out.printf("HP: %d | Score: %d | Steps: %d/%d | Level: %d\n", 
            player.getHp(), player.getScore(), player.getSteps(), 100, player.getLevel());
    }
    
    private static void saveGame(GameEngine engine, java.util.Scanner scanner) {
        System.out.print("Enter save filename: ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "savegame.dat";
        }
        
        try {
            dungeon.engine.persistence.SaveState saveState = new dungeon.engine.persistence.SaveState(engine);
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(filename));
            oos.writeObject(saveState);
            oos.close();
            System.out.println("Game saved to " + filename);
        } catch (Exception e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }
    
    private static GameEngine loadGame(java.util.Scanner scanner) {
        System.out.print("Enter save filename: ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "savegame.dat";
        }
        
        try {
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.FileInputStream(filename));
            dungeon.engine.persistence.SaveState saveState = 
                (dungeon.engine.persistence.SaveState) ois.readObject();
            ois.close();
            System.out.println("Game loaded from " + filename);
            return saveState.restoreGame();
        } catch (Exception e) {
            System.out.println("Load failed: " + e.getMessage());
            return null;
        }
    }
}
