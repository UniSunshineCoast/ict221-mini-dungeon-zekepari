package dungeon.engine.cells;

import dungeon.engine.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RangedMutantCell functionality.
 */
public class TestRangedMutantCell {
    
    private GameEngine engine;
    private Player player;
    private RangedMutantCell rangedMutant;
    
    @BeforeEach
    public void setUp() {
        engine = new GameEngine(2, 12345L); // Use fixed seed for reproducible tests
        player = engine.getPlayer();
        rangedMutant = new RangedMutantCell();
    }
    
    @Test
    public void testRangedMutantCreation() {
        assertNotNull(rangedMutant);
        assertNotNull(rangedMutant.getMutant());
    }
    
    @Test
    public void testCanShootAtPlayerInRange() {
        // Set up a clear shot scenario - clear the path first
        Cell[][] map = engine.getMap();
        
        // Clear a cross pattern around position (5,5) to ensure clear line of sight
        for (int i = 3; i <= 7; i++) {
            map[5][i] = new EmptyCell(); // Clear horizontal line
            map[i][5] = new EmptyCell(); // Clear vertical line
        }
        
        // Place mutant at center
        map[5][5] = rangedMutant;
        Position mutantPos = new Position(5, 5);
        
        // Test shooting up (player at 3,5)
        Position playerPos = new Position(3, 5);
        player.setPosition(playerPos);
        assertTrue(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should be able to shoot at player 2 tiles up");
        
        // Test shooting down (player at 7,5)
        playerPos = new Position(7, 5);
        player.setPosition(playerPos);
        assertTrue(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should be able to shoot at player 2 tiles down");
        
        // Test shooting left (player at 5,3)
        playerPos = new Position(5, 3);
        player.setPosition(playerPos);
        assertTrue(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should be able to shoot at player 2 tiles left");
        
        // Test shooting right (player at 5,7)
        playerPos = new Position(5, 7);
        player.setPosition(playerPos);
        assertTrue(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should be able to shoot at player 2 tiles right");
    }
    
    @Test
    public void testCannotShootAtPlayerTooFar() {
        // Set up mutant at (5,5) with clear paths
        Cell[][] map = engine.getMap();
        for (int i = 3; i <= 8; i++) {
            if (i < 10) map[5][i] = new EmptyCell(); // Clear horizontal line
        }
        
        map[5][5] = rangedMutant;
        Position mutantPos = new Position(5, 5);
        
        // Test player 3 tiles away (out of range)
        Position playerPos = new Position(5, 8); // 3 tiles right
        player.setPosition(playerPos);
        assertFalse(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should not be able to shoot at player 3 tiles away");
        
        // Test player diagonally (not in cardinal direction)
        playerPos = new Position(6, 6);
        player.setPosition(playerPos);
        assertFalse(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should not be able to shoot at player diagonally");
    }
    
    @Test
    public void testCannotShootThroughWalls() {
        // Set up mutant at (5,5) and clear the horizontal path first
        Cell[][] map = engine.getMap();
        for (int i = 2; i <= 7; i++) {
            map[5][i] = new EmptyCell(); // Clear horizontal line
        }
        
        map[5][5] = rangedMutant;
        Position mutantPos = new Position(5, 5);
        
        // Place wall between mutant and player
        map[5][4] = new WallCell(); // Wall at (5,4)
        
        // Player at (5,3) - blocked by wall
        Position playerPos = new Position(5, 3);
        player.setPosition(playerPos);
        assertFalse(rangedMutant.getMutant().canShootAt(mutantPos, playerPos, engine),
                "Should not be able to shoot through walls");
    }
    
    @Test
    public void testShootAtPlayerHitProbability() {
        // Test hit probability over multiple attempts
        int hits = 0;
        int trials = 1000;
        
        for (int i = 0; i < trials; i++) {
            // Reset player HP
            player = new Player(new Position(0, 0));
            int initialHp = player.getHp();
            
            boolean hit = rangedMutant.getMutant().shootAt(player);
            if (hit) {
                hits++;
                assertEquals(initialHp - 2, player.getHp(), "Should lose 2 HP on hit");
            } else {
                assertEquals(initialHp, player.getHp(), "Should not lose HP on miss");
            }
        }
        
        // Test that hit rate is approximately 50% (within reasonable tolerance)
        double hitRate = (double) hits / trials;
        assertTrue(hitRate >= 0.45 && hitRate <= 0.55, 
                "Hit rate should be approximately 50%, got " + hitRate);
    }
    
    @Test
    public void testPlayerSteppingOnRangedMutant() {
        // Place ranged mutant on the map
        Position mutantPos = new Position(5, 5);
        engine.getMap()[5][5] = rangedMutant;
        
        // Move player to mutant position
        player.setPosition(mutantPos);
        int initialScore = player.getScore();
        int initialHp = player.getHp();
        
        // Trigger onEnter
        rangedMutant.onEnter(player, engine);
        
        // Should gain points but not lose HP
        assertEquals(initialScore + 2, player.getScore(), "Should gain 2 points");
        assertEquals(initialHp, player.getHp(), "Should not lose HP when stepping on ranged mutant");
        
        // Mutant should be replaced with empty cell
        assertTrue(engine.getMap()[5][5] instanceof EmptyCell, 
                "Ranged mutant should be replaced with empty cell");
    }
    
    @Test
    public void testProcessTurnWithPlayerInRange() {
        // Set up test with console logger to capture output
        engine.setActionLogger(new dungeon.engine.ConsoleActionLogger());
        
        // Place mutant and player in shooting range
        Position mutantPos = new Position(3, 3);
        engine.getMap()[3][3] = rangedMutant;
        
        Position playerPos = new Position(3, 1); // 2 tiles left of mutant
        player.setPosition(playerPos);
        
        int initialHp = player.getHp();
        
        // Process turn (this will attempt to shoot)
        rangedMutant.processTurn(mutantPos, engine);
        
        // HP should either stay the same (miss) or decrease by 2 (hit)
        int newHp = player.getHp();
        assertTrue(newHp == initialHp || newHp == initialHp - 2,
                "Player HP should either stay same (miss) or lose 2 (hit)");
    }
    
    @Test
    public void testProcessTurnWithPlayerOutOfRange() {
        // Set up test with player out of range
        Position mutantPos = new Position(3, 3);
        engine.getMap()[3][3] = rangedMutant;
        
        Position playerPos = new Position(6, 6); // Far away diagonally
        player.setPosition(playerPos);
        
        int initialHp = player.getHp();
        
        // Process turn (should not attempt to shoot)
        rangedMutant.processTurn(mutantPos, engine);
        
        // HP should remain unchanged
        assertEquals(initialHp, player.getHp(), "Player HP should not change when out of range");
    }
}
