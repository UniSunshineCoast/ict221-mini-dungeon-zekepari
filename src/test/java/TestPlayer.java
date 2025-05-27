import dungeon.engine.Direction;
import dungeon.engine.Player;
import dungeon.engine.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {
    
    @Test
    public void testPlayerInitialization() {
        Position startPosition = new Position(5, 5);
        Player player = new Player(startPosition);
        
        // Check initial values
        assertEquals(startPosition, player.getPosition(), "Position should be initialized correctly");
        assertEquals(10, player.getHp(), "HP should start at maximum (10)");
        assertEquals(0, player.getScore(), "Score should start at 0");
        assertEquals(0, player.getSteps(), "Steps should start at 0");
        assertEquals(1, player.getLevel(), "Level should start at 1");
    }
    
    @Test
    public void testMove() {
        Position startPosition = new Position(5, 5);
        Player player = new Player(startPosition);
        
        // Test movement in all four directions
        Position upPos = player.move(Direction.UP);
        assertEquals(new Position(4, 5), upPos, "Should move UP correctly");
        
        Position rightPos = player.move(Direction.RIGHT);
        assertEquals(new Position(5, 6), rightPos, "Should move RIGHT correctly");
        
        Position downPos = player.move(Direction.DOWN);
        assertEquals(new Position(6, 5), downPos, "Should move DOWN correctly");
        
        Position leftPos = player.move(Direction.LEFT);
        assertEquals(new Position(5, 4), leftPos, "Should move LEFT correctly");
        
        // Verify that move() doesn't actually update the player's position
        assertEquals(startPosition, player.getPosition(), "Player position should not change after move() calls");
    }
    
    @Test
    public void testIncrementSteps() {
        Player player = new Player(new Position(0, 0));
        
        // Initial steps should be 0
        assertEquals(0, player.getSteps(), "Initial steps should be 0");
        
        // Increment steps multiple times
        for (int i = 1; i <= 5; i++) {
            player.incrementSteps();
            assertEquals(i, player.getSteps(), "Steps should increment by 1");
        }
    }
    
    @Test
    public void testHpClamp() {
        Player player = new Player(new Position(0, 0));
        
        // Test HP clamping at maximum (10)
        player.modifyHp(5); // Try to go to 15
        assertEquals(10, player.getHp(), "HP should be clamped at maximum (10)");
        
        // Test HP reduction
        player.modifyHp(-3);
        assertEquals(7, player.getHp(), "HP should decrease correctly");
        
        // Test HP clamping at minimum (0)
        player.modifyHp(-10); // Try to go to -3
        assertEquals(0, player.getHp(), "HP should be clamped at minimum (0)");
    }
    
    @Test
    public void testTakeDamage() {
        Player player = new Player(new Position(0, 0));
        int initialHp = player.getHp();
        
        // Test taking damage
        player.takeDamage(3);
        assertEquals(initialHp - 3, player.getHp(), "Taking damage should reduce HP");
        
        // Test taking damage that would reduce HP below 0
        player.takeDamage(10);
        assertEquals(0, player.getHp(), "HP should be clamped at 0 when taking excessive damage");
    }
    
    @Test
    public void testAddScore() {
        Player player = new Player(new Position(0, 0));
        
        // Test adding to score
        player.addScore(5);
        assertEquals(5, player.getScore(), "Score should increase correctly");
        
        // Test adding more to score
        player.addScore(10);
        assertEquals(15, player.getScore(), "Score should accumulate correctly");
    }
    
    @Test
    public void testLevel() {
        Player player = new Player(new Position(0, 0));
        
        // Test initial level
        assertEquals(1, player.getLevel(), "Level should start at 1");
        
        // Test setting level
        player.setLevel(2);
        assertEquals(2, player.getLevel(), "Level should be updated correctly");
    }
}
