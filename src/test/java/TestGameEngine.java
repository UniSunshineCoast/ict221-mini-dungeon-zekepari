import dungeon.engine.*;
import dungeon.engine.cells.*;import dungeon.engine.cells.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class TestGameEngine {
    
    @Test
    void testGetSize() {
        GameEngine engine = new GameEngine(1);
        assertEquals(10, engine.getSize(), "GameEngine should have a 10x10 map");
    }
    
    @Test
    void testNewGame() {
        GameEngine engine = GameEngine.newGame(2);
        assertEquals(2, engine.getDifficulty(), "Difficulty should be set correctly");
        assertNotNull(engine.getPlayer(), "Player should be initialized");
        assertEquals(new Position(0, 0), engine.getPlayer().getPosition(), "Player should start at entry (0,0)");
        assertFalse(engine.isGameOver(), "Game should not be over at start");
    }
    
    @Test
    void testMovePlayer() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Initial position should be (0,0)
        assertEquals(new Position(0, 0), player.getPosition(), "Player should start at entry (0,0)");
        
        // Move right
        assertTrue(engine.movePlayer(Direction.RIGHT), "Move should succeed");
        assertEquals(new Position(0, 1), player.getPosition(), "Player should move to (0,1)");
        assertEquals(1, player.getSteps(), "Steps should increment");
        
        // Move down
        assertTrue(engine.movePlayer(Direction.DOWN), "Move should succeed");
        assertEquals(new Position(1, 1), player.getPosition(), "Player should move to (1,1)");
        assertEquals(2, player.getSteps(), "Steps should increment");
        
        // Try to move into a wall (need to create one first)
        Position wallPos = new Position(1, 2);
        Cell originalCell = engine.getMap()[wallPos.getRow()][wallPos.getCol()];
        engine.replaceCell(wallPos, new WallCell());
        
        assertFalse(engine.movePlayer(Direction.RIGHT), "Move into wall should fail");
        assertEquals(new Position(1, 1), player.getPosition(), "Player position should not change");
        assertEquals(2, player.getSteps(), "Steps should not increment");
        
        // Restore original cell and clean up
        engine.replaceCell(wallPos, originalCell);
    }
    
    @Test
    void testReplaceCell() {
        GameEngine engine = new GameEngine(1);
        Position pos = new Position(5, 5);
        
        // Replace with a trap cell
        engine.replaceCell(pos, new TrapCell());
        assertTrue(engine.getMap()[pos.getRow()][pos.getCol()] instanceof TrapCell, 
            "Cell should be replaced with TrapCell");
        
        // Replace with an empty cell
        engine.replaceCell(pos, new EmptyCell());
        assertTrue(engine.getMap()[pos.getRow()][pos.getCol()] instanceof EmptyCell, 
            "Cell should be replaced with EmptyCell");
    }
    
    @Test
    void testReproducibility() {
        // Create two games with the same seed
        long seed = 54321L;
        GameEngine engine1 = new GameEngine(1, seed);
        GameEngine engine2 = new GameEngine(1, seed);
        
        // The maps should be identical
        Cell[][] map1 = engine1.getMap();
        Cell[][] map2 = engine2.getMap();
        
        for (int row = 0; row < engine1.getSize(); row++) {
            for (int col = 0; col < engine1.getSize(); col++) {
                assertEquals(map1[row][col].getClass(), map2[row][col].getClass(),
                    "Cells at position (" + row + ", " + col + ") should be the same type");
            }
        }
    }
    
    @Test
    void testGameLossFromHP() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Verify initial state
        assertFalse(engine.isGameOver(), "Game should not be over initially");
        
        // Ensure the cell at (0,1) is not a wall so the move will succeed
        engine.replaceCell(new Position(0, 1), new EmptyCell());
        
        // Reduce player HP to 0
        while (player.getHp() > 0) {
            player.takeDamage(1);
        }
        
        // Move to trigger game over check
        engine.movePlayer(Direction.RIGHT);
        
        // Verify game is over due to HP loss
        assertTrue(engine.isGameOver(), "Game should be over due to HP loss");
        assertTrue(engine.getStatusMessage().contains("health"),
            "Status message should mention health: " + engine.getStatusMessage());
    }
    
    @Test
    void testGameLossFromSteps() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Set steps close to maximum
        for (int i = 0; i < 98; i++) {
            player.incrementSteps();
        }
        
        // Verify game is not over yet
        assertFalse(engine.isGameOver(), "Game should not be over before max steps");
        
        // Move twice more to exceed max steps
        engine.movePlayer(Direction.RIGHT);
        engine.movePlayer(Direction.DOWN);
        
        // Verify game is over due to step limit
        assertTrue(engine.isGameOver(), "Game should be over due to step limit");
        assertTrue(engine.getStatusMessage().contains("steps"),
            "Status message should mention steps: " + engine.getStatusMessage());
    }
    
    @Test
    void testLevelProgression() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Verify initial level
        assertEquals(1, player.getLevel(), "Player should start at level 1");
        
        // Advance to next level
        engine.advanceToNextLevel();
        
        // Verify level increased
        assertEquals(2, player.getLevel(), "Player should now be at level 2");
        assertFalse(engine.isGameOver(), "Game should not be over yet");
        
        // Advance to beyond final level to trigger win
        engine.advanceToNextLevel();
        
        // Verify game is won
        assertTrue(engine.isGameOver(), "Game should be over with a win");
        assertTrue(engine.getStatusMessage().toLowerCase().contains("won") || 
                  engine.getStatusMessage().toLowerCase().contains("congratulations"),
            "Status message should indicate a win: " + engine.getStatusMessage());
    }
    
    @Test
    void testWinGameScenario() {
        // Create a deterministic game with a known seed
        long seed = 12345L;
        GameEngine engine = new GameEngine(1, seed);
        Player player = engine.getPlayer();
        
        // First, find where the ladder is at level 1
        Position ladderPos = null;
        for (int row = 0; row < engine.getSize(); row++) {
            for (int col = 0; col < engine.getSize(); col++) {
                if (engine.getMap()[row][col] instanceof LadderCell) {
                    ladderPos = new Position(row, col);
                    break;
                }
            }
            if (ladderPos != null) break;
        }
        
        assertNotNull(ladderPos, "Should find a ladder in the map");
        
        // Move the player directly to the ladder cell
        player.setPosition(ladderPos);
        
        // Verify we're at level 1
        assertEquals(1, player.getLevel(), "Player should be at level 1");
        
        // Simulate entering the ladder cell
        engine.getMap()[ladderPos.getRow()][ladderPos.getCol()].onEnter(player, engine);
        
        // Verify we're at level 2
        assertEquals(2, player.getLevel(), "Player should now be at level 2");
        
        // Find level 2 ladder
        ladderPos = null;
        for (int row = 0; row < engine.getSize(); row++) {
            for (int col = 0; col < engine.getSize(); col++) {
                if (engine.getMap()[row][col] instanceof LadderCell) {
                    ladderPos = new Position(row, col);
                    break;
                }
            }
            if (ladderPos != null) break;
        }
        
        assertNotNull(ladderPos, "Should find a ladder in level 2");
        
        // Move to and enter the level 2 ladder to win
        player.setPosition(ladderPos);
        engine.getMap()[ladderPos.getRow()][ladderPos.getCol()].onEnter(player, engine);
        
        // Verify the game is won
        assertTrue(engine.isGameOver(), "Game should be won after completing level 2");
        assertTrue(engine.getStatusMessage().toLowerCase().contains("won") || 
                  engine.getStatusMessage().toLowerCase().contains("congratulations"),
            "Status message should indicate a win: " + engine.getStatusMessage());
    }
    
    @Test
    void testMove() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Initial position should be (0,0)
        assertEquals(new Position(0, 0), player.getPosition(), "Player should start at entry (0,0)");
        
        // Move right using the new move method
        assertTrue(engine.move(Direction.RIGHT), "Move should succeed");
        assertEquals(new Position(0, 1), player.getPosition(), "Player should move to (0,1)");
        assertEquals(1, player.getSteps(), "Steps should increment");
        
        // Move down
        assertTrue(engine.move(Direction.DOWN), "Move should succeed");
        assertEquals(new Position(1, 1), player.getPosition(), "Player should move to (1,1)");
        assertEquals(2, player.getSteps(), "Steps should increment");
        
        // Test move into a wall
        Position wallPos = new Position(1, 2);
        Cell originalCell = engine.getMap()[wallPos.getRow()][wallPos.getCol()];
        engine.replaceCell(wallPos, new WallCell());
        
        assertFalse(engine.move(Direction.RIGHT), "Move into wall should fail");
        assertEquals(new Position(1, 1), player.getPosition(), "Player position should not change");
        assertEquals(2, player.getSteps(), "Steps should not increment");
        
        // Restore original cell
        engine.replaceCell(wallPos, originalCell);
        
        // Test move on game over
        // First make game over by reducing HP to 0
        while (player.getHp() > 0) {
            player.takeDamage(1);
        }
        engine.movePlayer(Direction.LEFT); // Trigger game over check
        
        // Now try to move when game is over
        assertFalse(engine.move(Direction.UP), "Move should fail when game is over");
    }
    
    @Test
    void testIntegrationWithMutant() {
        GameEngine engine = new GameEngine(1);
        Player player = engine.getPlayer();
        
        // Place a mutant in position (0,1)
        Position mutantPos = new Position(0, 1);
        engine.replaceCell(mutantPos, new MeleeMutantCell());
        
        // Record player's initial HP and score
        int initialHp = player.getHp();
        int initialScore = player.getScore();
        
        // Move into the mutant cell
        engine.move(Direction.RIGHT);
        
        // Verify player took damage and gained points
        assertEquals(initialHp - Mutant.DAMAGE, player.getHp(), 
            "Player should take damage from mutant");
        assertEquals(initialScore + Mutant.POINTS, player.getScore(), 
            "Player should gain points from defeating mutant");
        
        // Verify the mutant is gone (cell is now empty)
        assertTrue(engine.getMap()[mutantPos.getRow()][mutantPos.getCol()] instanceof EmptyCell,
            "Mutant cell should be replaced with empty cell");
    }
    
    @Test
    void testIntegrationFullGame() {
        // Create a game with a known seed for reproducibility
        long seed = 12345L;
        GameEngine engine = new GameEngine(1, seed);
        Player player = engine.getPlayer();
        
        // Verify initial state
        assertEquals(1, player.getLevel());
        assertEquals(10, player.getHp());
        assertEquals(0, player.getScore());
        assertEquals(0, player.getSteps());
        
        // Find the ladder in level 1
        Position ladderPos = null;
        for (int row = 0; row < engine.getSize(); row++) {
            for (int col = 0; col < engine.getSize(); col++) {
                if (engine.getMap()[row][col] instanceof LadderCell) {
                    ladderPos = new Position(row, col);
                    break;
                }
            }
            if (ladderPos != null) break;
        }
        assertNotNull(ladderPos, "Should find a ladder in the map");
        
        // Place player next to the ladder
        Position nextToLadder = new Position(
            ladderPos.getRow() > 0 ? ladderPos.getRow() - 1 : ladderPos.getRow() + 1,
            ladderPos.getCol()
        );
        player.setPosition(nextToLadder);
        
        // Move to ladder using the move method
        Direction directionToLadder = ladderPos.getRow() > nextToLadder.getRow() ? 
            Direction.DOWN : Direction.UP;
        engine.move(directionToLadder);
        
        // Verify level increase
        assertEquals(2, player.getLevel(), "Player should now be at level 2");
        
        // Find level 2 ladder
        ladderPos = null;
        for (int row = 0; row < engine.getSize(); row++) {
            for (int col = 0; col < engine.getSize(); col++) {
                if (engine.getMap()[row][col] instanceof LadderCell) {
                    ladderPos = new Position(row, col);
                    break;
                }
            }
            if (ladderPos != null) break;
        }
        assertNotNull(ladderPos, "Should find a ladder in level 2");
        
        // Place player next to the level 2 ladder
        nextToLadder = new Position(
            ladderPos.getRow() > 0 ? ladderPos.getRow() - 1 : ladderPos.getRow() + 1,
            ladderPos.getCol()
        );
        player.setPosition(nextToLadder);
        
        // Move to level 2 ladder to win the game
        directionToLadder = ladderPos.getRow() > nextToLadder.getRow() ? 
            Direction.DOWN : Direction.UP;
        engine.move(directionToLadder);
        
        // Verify game is won
        assertTrue(engine.isGameOver(), "Game should be won after completing level 2");
        assertTrue(engine.getStatusMessage().toLowerCase().contains("won") || 
                  engine.getStatusMessage().toLowerCase().contains("congratulations"),
            "Status message should indicate a win: " + engine.getStatusMessage());
    }
}
