import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dungeon.engine.*;
import dungeon.engine.cells.*;

/**
 * Additional tests to improve code coverage by targeting specific uncovered areas.
 */
public class TestEdgeCaseCoverage {
    
    @Test
    void testGameEngineEdgeCases() {
        GameEngine engine = new GameEngine(10);
        
        // Test getting map dimensions
        assertEquals(10, engine.getSize());
        
        // Test initial state
        assertNotNull(engine.getPlayer());
        assertNotNull(engine.getMap());
        assertEquals(10, engine.getDifficulty());
        
        // Test seed getter
        assertTrue(engine.getSeed() > 0);
    }
    
    @Test
    void testPlayerEdgeCases() {
        Position startPos = new Position(0, 0);
        Player player = new Player(startPos);
        
        // Test initial state
        assertEquals(10, player.getHp()); // MAX_HP is 10 based on source
        assertEquals(0, player.getScore());
        assertNotNull(player.getPosition());
        assertEquals(1, player.getLevel());
        
        // Test position bounds
        Position pos = player.getPosition();
        assertTrue(pos.getRow() >= 0);
        assertTrue(pos.getCol() >= 0);
        
        // Test hp modification edge cases
        player.modifyHp(-100); // Should not go below 0
        assertEquals(0, player.getHp());
        
        player.modifyHp(75); // Should not exceed max
        assertEquals(10, player.getHp()); // Should be clamped to max
        
        // Test score modification
        player.addScore(10);
        assertEquals(10, player.getScore());
        
        player.addScore(-5); // Test negative score addition
        assertEquals(5, player.getScore());
    }
    
    @Test
    void testPositionEdgeCases() {
        // Test normal construction
        Position pos1 = new Position(5, 7);
        assertEquals(5, pos1.getRow());
        assertEquals(7, pos1.getCol());
        
        // Test boundary values
        Position pos2 = new Position(0, 0);
        assertEquals(0, pos2.getRow());
        assertEquals(0, pos2.getCol());
        
        // Test maximum valid values
        Position pos3 = new Position(9, 9);
        assertEquals(9, pos3.getRow());
        assertEquals(9, pos3.getCol());
        
        // Test immutability by checking equals
        Position pos4 = new Position(5, 7);
        assertEquals(pos1, pos4);
        assertTrue(pos1.equals(pos4));
        assertFalse(pos1.equals(pos2));
        
        // Test hash code consistency
        assertEquals(pos1.hashCode(), pos4.hashCode());
        
        // Test toString
        assertNotNull(pos1.toString());
        assertTrue(pos1.toString().contains("5"));
        assertTrue(pos1.toString().contains("7"));
    }
    
    @Test
    void testDirectionEdgeCases() {
        // Test all directions have valid deltas
        for (Direction dir : Direction.values()) {
            // Deltas should be -1, 0, or 1
            assertTrue(dir.getDRow() >= -1 && dir.getDRow() <= 1);
            assertTrue(dir.getDCol() >= -1 && dir.getDCol() <= 1);
        }
        
        // Test specific directions
        assertEquals(-1, Direction.UP.getDRow());
        assertEquals(0, Direction.UP.getDCol());
        
        assertEquals(1, Direction.DOWN.getDRow());
        assertEquals(0, Direction.DOWN.getDCol());
        
        assertEquals(0, Direction.LEFT.getDRow());
        assertEquals(-1, Direction.LEFT.getDCol());
        
        assertEquals(0, Direction.RIGHT.getDRow());
        assertEquals(1, Direction.RIGHT.getDCol());
        
        // Test toString method
        for (Direction dir : Direction.values()) {
            assertNotNull(dir.toString());
            assertFalse(dir.toString().isEmpty());
        }
    }
    
    @Test
    void testMutantInnerClass() {
        MeleeMutantCell mutantCell = new MeleeMutantCell();
        
        // Test the cell creation
        assertNotNull(mutantCell);
        assertEquals("zombie.png", mutantCell.spritePath());
        
        // Create a game scenario to test the mutant behavior
        GameEngine engine = new GameEngine(5);
        Player player = engine.getPlayer();
        
        // Store initial HP
        int initialHp = player.getHp();
        
        // Test mutant onEnter (this will test the inner Mutant class)
        mutantCell.onEnter(player, engine);
        
        // Player should take damage
        assertTrue(player.getHp() < initialHp);
    }
    
    @Test
    void testCellConstructorVariations() {
        // Test all cells with custom sprites
        GoldCell customGold = new GoldCell("my_treasure.png");
        assertEquals("my_treasure.png", customGold.spritePath());
        
        HealthPotionCell customHealth = new HealthPotionCell("my_health.png");
        assertEquals("my_health.png", customHealth.spritePath());
        
        TrapCell customTrap = new TrapCell("my_trap.png");
        assertEquals("my_trap.png", customTrap.spritePath());
        
        LadderCell customLadder = new LadderCell("my_ladder.png");
        assertEquals("my_ladder.png", customLadder.spritePath());
        
        WallCell customWall = new WallCell("my_wall.png");
        assertEquals("my_wall.png", customWall.spritePath());
        
        EntryCell customEntry = new EntryCell("my_entry.png");
        assertEquals("my_entry.png", customEntry.spritePath());
        
        EmptyCell customEmpty = new EmptyCell("my_empty.png");
        assertEquals("my_empty.png", customEmpty.spritePath());
    }
    
    @Test
    void testGameMapGeneration() {
        GameEngine engine = new GameEngine(8);
        Cell[][] map = engine.getMap();
        
        // Map should be non-null and correct size
        assertNotNull(map);
        assertEquals(10, map.length); // Maps are always 10x10
        assertEquals(10, map[0].length);
        
        // Count different cell types
        int wallCount = 0;
        int emptyCount = 0;
        int itemCount = 0;
        int entryCount = 0;
        
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Cell cell = map[i][j];
                assertNotNull(cell);
                
                if (cell instanceof WallCell) wallCount++;
                else if (cell instanceof EmptyCell) emptyCount++;
                else if (cell instanceof EntryCell) entryCount++;
                else itemCount++;
            }
        }
        
        // Should have at least some walls and empty spaces
        assertTrue(wallCount > 0);
        assertTrue(emptyCount > 0);
        assertTrue(entryCount >= 1); // Should have at least one entry point
        
        // Total cells should equal map size
        assertEquals(100, wallCount + emptyCount + itemCount + entryCount);
    }
    
    @Test
    void testPlayerMovementEdgeCases() {
        GameEngine engine = new GameEngine(5);
        Player player = engine.getPlayer();
        
        // Try all directions
        engine.movePlayer(Direction.DOWN);
        engine.movePlayer(Direction.LEFT);
        engine.movePlayer(Direction.RIGHT);
        engine.movePlayer(Direction.UP);
        
        // Player should still have a valid position
        assertNotNull(player.getPosition());
        assertTrue(player.getPosition().getRow() >= 0);
        assertTrue(player.getPosition().getCol() >= 0);
        assertTrue(player.getPosition().getRow() < 10); // Maps are 10x10
        assertTrue(player.getPosition().getCol() < 10);
    }
    
    @Test
    void testGameOverConditions() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Test initial state
        assertFalse(engine.isGameOver());
        assertNotNull(engine.getStatusMessage());
        
        // Test HP-based game over - use the same approach as the working test
        // Ensure the cell at (0,1) is not a wall so the move will succeed
        engine.replaceCell(new Position(0, 1), new EmptyCell());
        
        while (player.getHp() > 0) {
            player.takeDamage(1);
        }
        
        // Move to trigger game over check
        engine.movePlayer(Direction.RIGHT);
        
        assertTrue(engine.isGameOver());
        assertTrue(engine.getStatusMessage().toLowerCase().contains("health"));
    }
    
    @Test
    void testStaticGameEngineMethod() {
        GameEngine engine = GameEngine.newGame(3);
        assertNotNull(engine);
        assertEquals(3, engine.getDifficulty());
        assertFalse(engine.isGameOver());
    }
}
