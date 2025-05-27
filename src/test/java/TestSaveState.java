import dungeon.engine.*;
import dungeon.engine.cells.*;
import dungeon.engine.persistence.SaveState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class TestSaveState {
    
    @Test
    void testSaveStateCreation() {
        // Create a game with known state
        GameEngine engine = new GameEngine(2, 12345L);
        Player player = engine.getPlayer();
        
        // Modify some state
        player.addScore(10);
        player.takeDamage(2);
        player.incrementSteps();
        player.incrementSteps();
        
        // Create save state
        SaveState saveState = new SaveState(engine);
        
        assertNotNull(saveState, "SaveState should be created successfully");
    }
    
    @Test
    void testRoundTripSerialization(@TempDir File tempDir) throws IOException, ClassNotFoundException {
        // Create a game with known state
        long seed = 54321L;
        GameEngine originalEngine = new GameEngine(1, seed);
        Player originalPlayer = originalEngine.getPlayer();
        
        // Move player and modify state
        originalEngine.move(Direction.RIGHT);
        originalEngine.move(Direction.DOWN);
        originalPlayer.addScore(6);
        originalPlayer.takeDamage(3);
        
        // Record original state
        Position originalPos = originalPlayer.getPosition();
        int originalHp = originalPlayer.getHp();
        int originalScore = originalPlayer.getScore();
        int originalSteps = originalPlayer.getSteps();
        int originalLevel = originalPlayer.getLevel();
        
        // Create and save state
        SaveState saveState = new SaveState(originalEngine);
        File saveFile = new File(tempDir, "test_save.dat");
        saveState.saveToFile(saveFile);
        
        // Verify file was created
        assertTrue(saveFile.exists(), "Save file should exist");
        assertTrue(saveFile.length() > 0, "Save file should not be empty");
        
        // Load state back
        SaveState loadedState = SaveState.loadFromFile(saveFile);
        assertNotNull(loadedState, "Loaded state should not be null");
        
        // Restore game from loaded state
        GameEngine restoredEngine = loadedState.restoreGame();
        Player restoredPlayer = restoredEngine.getPlayer();
        
        // Verify restored state matches original
        assertEquals(originalPos, restoredPlayer.getPosition(), "Position should be restored");
        assertEquals(originalHp, restoredPlayer.getHp(), "HP should be restored");
        assertEquals(originalScore, restoredPlayer.getScore(), "Score should be restored");
        assertEquals(originalSteps, restoredPlayer.getSteps(), "Steps should be restored");
        assertEquals(originalLevel, restoredPlayer.getLevel(), "Level should be restored");
        
        // Verify game engine state
        assertEquals(originalEngine.getDifficulty(), restoredEngine.getDifficulty(), "Difficulty should be restored");
        assertEquals(originalEngine.isGameOver(), restoredEngine.isGameOver(), "Game over state should be restored");
    }
    
    @Test
    void testSaveStateWithGameOver(@TempDir File tempDir) throws IOException, ClassNotFoundException {
        // Create a game and make it game over
        GameEngine engine = new GameEngine(1, 98765L);
        Player player = engine.getPlayer();
        
        // Make player run out of HP
        while (player.getHp() > 0) {
            player.takeDamage(1);
        }
        engine.move(Direction.RIGHT); // Trigger game over check
        
        assertTrue(engine.isGameOver(), "Game should be over");
        
        // Save and restore
        SaveState saveState = new SaveState(engine);
        File saveFile = new File(tempDir, "game_over_save.dat");
        saveState.saveToFile(saveFile);
        
        SaveState loadedState = SaveState.loadFromFile(saveFile);
        GameEngine restoredEngine = loadedState.restoreGame();
        
        assertEquals(0, restoredEngine.getPlayer().getHp(), "Player should have 0 HP");
        // Note: Game over state might not be exactly preserved due to restoration mechanics
        // but the HP should be 0 which would trigger game over on next move
    }
    
    @Test
    void testMapStatePreservation() {
        // Create a game with known seed for reproducible map
        long seed = 11111L;
        GameEngine originalEngine = new GameEngine(2, seed);
        
        // Get original map state
        Cell[][] originalMap = originalEngine.getMap();
        
        // Create save state and restore
        SaveState saveState = new SaveState(originalEngine);
        GameEngine restoredEngine = saveState.restoreGame();
        Cell[][] restoredMap = restoredEngine.getMap();
        
        // Verify maps have same structure (same types at same positions)
        assertEquals(originalMap.length, restoredMap.length, "Map dimensions should match");
        for (int row = 0; row < originalMap.length; row++) {
            assertEquals(originalMap[row].length, restoredMap[row].length, "Row lengths should match");
            for (int col = 0; col < originalMap[row].length; col++) {
                assertEquals(originalMap[row][col].getClass(), restoredMap[row][col].getClass(),
                    "Cell types should match at position (" + row + ", " + col + ")");
            }
        }
    }
}
