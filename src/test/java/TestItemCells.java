import dungeon.engine.*;
import dungeon.engine.cells.*;import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestItemCells {

    @Test
    void testGoldCellScoreIncrease() {
        // Setup
        GameEngine engine = new GameEngine(10);
        Player player = engine.getPlayer();
        Position testPosition = new Position(5, 5);
        player.setPosition(testPosition);
        
        GoldCell goldCell = new GoldCell();
        engine.getMap()[5][5] = goldCell;
        
        int initialScore = player.getScore();
        
        // Action
        goldCell.onEnter(player, engine);
        
        // Assert
        assertEquals(initialScore + 2, player.getScore(), "Player score should increase by 2");
        assertTrue(goldCell.isCollected(), "Gold should be marked as collected");
        assertTrue(engine.getMap()[5][5] instanceof EmptyCell, "Gold cell should be replaced with empty cell");
    }
    
    @Test
    void testTrapCellDamage() {
        // Setup
        GameEngine engine = new GameEngine(10);
        Player player = engine.getPlayer();
        Position testPosition = new Position(5, 5);
        player.setPosition(testPosition);
        
        TrapCell trapCell = new TrapCell();
        engine.getMap()[5][5] = trapCell;
        
        int initialHp = player.getHp();
        
        // Action
        trapCell.onEnter(player, engine);
        
        // Assert
        assertEquals(initialHp - 2, player.getHp(), "Player HP should decrease by 2");
        assertTrue(engine.getMap()[5][5] instanceof TrapCell, "Trap cell should persist");
    }
    
    @Test
    void testHealthPotionCellHealing() {
        // Setup
        GameEngine engine = new GameEngine(10);
        Player player = engine.getPlayer();
        // Damage the player first
        player.modifyHp(-5);
        Position testPosition = new Position(5, 5);
        player.setPosition(testPosition);
        
        HealthPotionCell potionCell = new HealthPotionCell();
        engine.getMap()[5][5] = potionCell;
        
        int initialHp = player.getHp();
        
        // Action
        potionCell.onEnter(player, engine);
        
        // Assert
        assertEquals(initialHp + 4, player.getHp(), "Player HP should increase by 4");
        assertTrue(potionCell.isCollected(), "Potion should be marked as collected");
        assertTrue(engine.getMap()[5][5] instanceof EmptyCell, "Potion cell should be replaced with empty cell");
    }
    
    @Test
    void testHealthPotionCellMaxHp() {
        // Setup
        GameEngine engine = new GameEngine(10);
        Player player = engine.getPlayer();
        // Damage the player slightly
        player.modifyHp(-1);
        Position testPosition = new Position(5, 5);
        player.setPosition(testPosition);
        
        HealthPotionCell potionCell = new HealthPotionCell();
        engine.getMap()[5][5] = potionCell;
        
        // Action
        potionCell.onEnter(player, engine);
        
        // Assert
        assertEquals(10, player.getHp(), "Player HP should be capped at 10");
    }
    
    @Test
    void testCollectedItemsDontAffectPlayerAgain() {
        // Setup
        GameEngine engine = new GameEngine(10);
        Player player = engine.getPlayer();
        Position testPosition = new Position(5, 5);
        player.setPosition(testPosition);
        
        GoldCell goldCell = new GoldCell();
        engine.getMap()[5][5] = goldCell;
        
        // First interaction
        goldCell.onEnter(player, engine);
        int scoreAfterFirstCollection = player.getScore();
        
        // Second interaction (shouldn't happen normally due to cell replacement, but testing the logic)
        goldCell.onEnter(player, engine);
        
        // Assert
        assertEquals(scoreAfterFirstCollection, player.getScore(), "Score shouldn't change on second interaction");
    }
    
    @Test
    void testPlayerHpClampAtZero() {
        // Setup
        GameEngine engine = new GameEngine(10);
        Player player = engine.getPlayer();
        // Set player HP near zero
        player.modifyHp(-9);
        Position testPosition = new Position(5, 5);
        player.setPosition(testPosition);
        
        TrapCell trapCell = new TrapCell();
        engine.getMap()[5][5] = trapCell;
        
        // First hit will bring player to zero
        trapCell.onEnter(player, engine);
        
        // Player should have 0 HP now
        assertEquals(0, player.getHp(), "Player HP should be 0");
        
        // Second hit shouldn't bring HP negative
        trapCell.onEnter(player, engine);
        
        // Assert
        assertEquals(0, player.getHp(), "Player HP should still be 0, not negative");
    }
}