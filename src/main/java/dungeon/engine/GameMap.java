package dungeon.engine;

import dungeon.engine.cells.*;
import java.util.Random;

/**
 * Represents the map of a dungeon level.
 * The map is a 10×10 grid of cells that is procedurally generated
 * based on a difficulty level and random seed.
 */
public class GameMap {
    // Constants for map generation
    private static final int MAP_SIZE = 10;
    private static final int GOLD_COUNT = 5;
    private static final int TRAP_COUNT = 5;
    private static final int HEALTH_POTION_COUNT = 2;
    private static final int MELEE_MUTANT_COUNT = 3;
    
    // The grid of cells
    private Cell[][] grid;
    
    // The difficulty level
    private int difficulty;
    
    // The random number generator used for map generation
    private Random rng;
    
    /**
     * Creates a new dungeon map with the specified difficulty.
     *
     * @param difficulty the difficulty level (affects number of ranged mutants)
     * @param rng the random number generator to use
     */
    public GameMap(int difficulty, Random rng) {
        this.difficulty = difficulty;
        this.rng = rng;
        this.grid = new Cell[MAP_SIZE][MAP_SIZE];
        
        // Initialize grid with empty cells
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                grid[row][col] = new EmptyCell();
            }
        }
        
        generateMap();
    }
    
    /**
     * Generates the dungeon map.
     */
    private void generateMap() {
        // Place entry at top-left
        grid[0][0] = new EntryCell();
        
        // Place ladder at bottom-right
        grid[MAP_SIZE - 1][MAP_SIZE - 1] = new LadderCell();
        
        // Place walls to create obstacles
        placeWalls();
        
        // Place gold
        placeRandomCells(GOLD_COUNT, GoldCell::new);
        
        // Place traps
        placeRandomCells(TRAP_COUNT, TrapCell::new);
        
        // Place health potions
        placeRandomCells(HEALTH_POTION_COUNT, HealthPotionCell::new);
        
        // Place melee mutants
        placeRandomCells(MELEE_MUTANT_COUNT, MeleeMutantCell::new);
        
        // Place ranged mutants based on difficulty
        placeRandomCells(difficulty, RangedMutantCell::new);
    }
    
    /**
     * Places walls in the map to create obstacles.
     */
    private void placeWalls() {
        // Create a few walls along the border with lots of gaps
        for (int i = 2; i < MAP_SIZE - 2; i += 2) {
            if (rng.nextDouble() < 0.4) { // 40% chance to place a wall
                // Top and bottom walls - just a few
                grid[2][i] = new WallCell();
                grid[MAP_SIZE - 3][i] = new WallCell();
                
                // Left and right walls - just a few
                grid[i][2] = new WallCell();
                grid[i][MAP_SIZE - 3] = new WallCell();
            }
        }
        
        // Add some internal walls
        // Add just a few internal walls based on difficulty
        int internalWalls = 3 + difficulty; // Much fewer walls, just enough to create some obstacles
        for (int i = 0; i < internalWalls; i++) {
            int row = 2 + rng.nextInt(MAP_SIZE - 4); // Avoid the border
            int col = 2 + rng.nextInt(MAP_SIZE - 4);
            grid[row][col] = new WallCell();
        }
    }
    
    /**
     * Places a number of cells of the given type at random empty locations.
     *
     * @param count the number of cells to place
     * @param cellSupplier a supplier for the cell type to place
     */
    private void placeRandomCells(int count, CellSupplier cellSupplier) {
        for (int i = 0; i < count; i++) {
            Position pos = findRandomEmptyPosition();
            if (pos != null) {
                grid[pos.getRow()][pos.getCol()] = cellSupplier.get();
            }
        }
    }
    
    /**
     * Finds a random empty position in the grid.
     *
     * @return a random position that contains an empty cell, or null if none are available
     */
    private Position findRandomEmptyPosition() {
        // Count the number of empty cells
        int emptyCount = 0;
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (grid[row][col] instanceof EmptyCell) {
                    emptyCount++;
                }
            }
        }
        
        if (emptyCount == 0) {
            return null; // No empty cells
        }
        
        // Select a random empty cell
        int targetIndex = rng.nextInt(emptyCount);
        int currentIndex = 0;
        
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (grid[row][col] instanceof EmptyCell) {
                    if (currentIndex == targetIndex) {
                        return new Position(row, col);
                    }
                    currentIndex++;
                }
            }
        }
        
        return null; // Should never happen
    }
    
    /**
     * Gets the cell at the specified position.
     *
     * @param position the position to check
     * @return the cell at that position, or null if the position is out of bounds
     */
    public Cell cellAt(Position position) {
        if (position == null || 
            position.getRow() < 0 || position.getRow() >= MAP_SIZE ||
            position.getCol() < 0 || position.getCol() >= MAP_SIZE) {
            return null;
        }
        
        return grid[position.getRow()][position.getCol()];
    }
    
    /**
     * Gets the size of the map.
     *
     * @return the size of the map (both width and height)
     */
    public int getSize() {
        return MAP_SIZE;
    }
    
    /**
     * Gets the entire grid of cells.
     *
     * @return the 2D array of cells
     */
    public Cell[][] getGrid() {
        return grid;
    }
    
    /**
     * Sets a cell at the specified position.
     * Used for replacing cells when items are consumed or enemies defeated.
     *
     * @param position the position of the cell to replace
     * @param cell the new cell to place at that position
     * @return true if the cell was successfully replaced, false otherwise
     */
    public boolean setCell(Position position, Cell cell) {
        if (position == null || 
            position.getRow() < 0 || position.getRow() >= MAP_SIZE ||
            position.getCol() < 0 || position.getCol() >= MAP_SIZE) {
            return false;
        }
        
        grid[position.getRow()][position.getCol()] = cell;
        return true;
    }
    
    /**
     * A functional interface for cell creation.
     */
    @FunctionalInterface
    private interface CellSupplier {
        Cell get();
    }
}
