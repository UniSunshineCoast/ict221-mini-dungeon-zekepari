import dungeon.engine.*;
import dungeon.engine.cells.*;import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class TestGameMap {
    
    @Test
    public void testMapSize() {
        GameMap map = new GameMap(1, new Random());
        assertEquals(10, map.getSize(), "GameMap should be 10x10");
        assertEquals(10, map.getGrid().length, "Grid should have 10 rows");
        assertEquals(10, map.getGrid()[0].length, "Grid should have 10 columns");
    }
    
    @Test
    public void testEntryAndLadderPlacement() {
        GameMap map = new GameMap(1, new Random());
        
        Cell entryCell = map.cellAt(new Position(0, 0));
        assertTrue(entryCell instanceof EntryCell, "Entry should be at top-left");
        
        Cell ladderCell = map.cellAt(new Position(9, 9));
        assertTrue(ladderCell instanceof LadderCell, "Ladder should be at bottom-right");
    }
    
    @Test
    public void testCellAt() {
        GameMap map = new GameMap(1, new Random());
        
        // Test with valid position
        assertNotNull(map.cellAt(new Position(5, 5)), "Should return a cell for valid position");
        
        // Test with null position
        assertNull(map.cellAt(null), "Should return null for null position");
        
        // Test with out-of-bounds positions - but note that Position constructor will throw exception
        // so we can't directly test out-of-bounds positions
    }
    
    @Test
    public void testSetCell() {
        GameMap map = new GameMap(1, new Random());
        
        // Test with valid position
        Position validPos = new Position(5, 5);
        Cell originalCell = map.cellAt(validPos);
        assertTrue(map.setCell(validPos, new TrapCell()), "Should return true for valid position");
        assertTrue(map.cellAt(validPos) instanceof TrapCell, "Cell should be updated");
        
        // Test with null position
        assertFalse(map.setCell(null, new TrapCell()), "Should return false for null position");
        
        // Note: Testing out-of-bounds positions would cause Position constructor to throw exceptions
    }
    
    @Test
    public void testMapContainsCellTypes() {
        GameMap map = new GameMap(2, new Random());
        Cell[][] grid = map.getGrid();
        
        // Count cell types
        int emptyCells = 0;
        int wallCells = 0;
        int goldCells = 0;
        int trapCells = 0;
        int healthPotionCells = 0;
        int meleeMutantCells = 0;
        int rangedMutantCells = 0;
        
        for (int row = 0; row < map.getSize(); row++) {
            for (int col = 0; col < map.getSize(); col++) {
                Cell cell = grid[row][col];
                
                if (cell instanceof EmptyCell) emptyCells++;
                else if (cell instanceof WallCell) wallCells++;
                else if (cell instanceof GoldCell) goldCells++;
                else if (cell instanceof TrapCell) trapCells++;
                else if (cell instanceof HealthPotionCell) healthPotionCells++;
                else if (cell instanceof MeleeMutantCell) meleeMutantCells++;
                else if (cell instanceof RangedMutantCell) rangedMutantCells++;
            }
        }
        
        assertTrue(emptyCells > 0, "Map should contain empty cells");
        assertTrue(wallCells > 0, "Map should contain wall cells");
        assertEquals(5, goldCells, "Map should contain 5 gold cells");
        assertEquals(5, trapCells, "Map should contain 5 trap cells");
        assertEquals(2, healthPotionCells, "Map should contain 2 health potion cells");
        assertEquals(3, meleeMutantCells, "Map should contain 3 melee mutant cells");
        assertEquals(2, rangedMutantCells, "Map should contain 2 ranged mutant cells (difficulty = 2)");
    }
    
    @Test
    public void testMapReproducibility() {
        // Create two maps with the same seed
        long seed = 12345L;
        GameMap map1 = new GameMap(2, new Random(seed));
        GameMap map2 = new GameMap(2, new Random(seed));
        
        // The maps should be identical
        Cell[][] grid1 = map1.getGrid();
        Cell[][] grid2 = map2.getGrid();
        
        for (int row = 0; row < map1.getSize(); row++) {
            for (int col = 0; col < map1.getSize(); col++) {
                assertEquals(grid1[row][col].getClass(), grid2[row][col].getClass(),
                    "Cells at position (" + row + ", " + col + ") should be the same type");
            }
        }
    }
}
