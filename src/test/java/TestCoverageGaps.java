import org.junit.jupiter.api.Test;

import dungeon.engine.*;
import dungeon.engine.cells.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests targeting specific coverage gaps to reach 85% threshold.
 * Focus on MeleeMutant inner class and other uncovered areas.
 */
public class TestCoverageGaps {
    
    @Test
    void testMeleeMutantCellInnerClassSpritePath() {
        // Create a MeleeMutantCell to test the inner MeleeMutant class
        MeleeMutantCell mutantCell = new MeleeMutantCell();
        
        // Create game engine and player to trigger inner class methods
        GameEngine engine = new GameEngine(5);
        Player player = new Player(new Position(1, 1));
        
        // Store initial stats
        int initialHp = player.getHp();
        int initialScore = player.getScore();
        
        // Position player and cell for testing
        engine.replaceCell(new Position(1, 1), mutantCell);
        player.setPosition(new Position(1, 1));
        
        // Trigger onEnter which uses inner MeleeMutant class methods
        mutantCell.onEnter(player, engine);
        
        // Verify the inner class methods were called (damage dealt, reward given)
        assertTrue(player.getHp() < initialHp, "Player should have taken damage from mutant");
        assertTrue(player.getScore() > initialScore, "Player should have gained points from defeating mutant");
        
        // Verify cell was replaced with empty cell
        Cell newCell = engine.getMap()[1][1];
        assertTrue(newCell instanceof EmptyCell, "Cell should be replaced with EmptyCell after mutant defeat");
    }
    
    @Test
    void testHealthPotionCellOverheal() {
        HealthPotionCell healthCell = new HealthPotionCell();
        GameEngine engine = new GameEngine(5);
        Player player = new Player(new Position(0, 0));
        
        // Player starts at full health (10 HP)
        int initialHp = player.getHp();
        assertEquals(10, initialHp, "Player should start at max HP");
        
        // Use health potion when already at max HP
        healthCell.onEnter(player, engine);
        
        // Should not exceed max HP
        assertEquals(10, player.getHp(), "HP should not exceed maximum");
    }
    
    @Test
    void testPlayerHealingBoundary() {
        Player player = new Player(new Position(0, 0));
        
        // Damage player first
        player.takeDamage(5);
        int damagedHp = player.getHp();
        assertEquals(5, damagedHp, "Player should have 5 HP after taking 5 damage");
        
        // Heal with amount that would exceed max (modifyHp with positive value)
        player.modifyHp(10);
        
        // Should cap at max HP
        assertEquals(10, player.getHp(), "Healing should cap at max HP");
    }
    
    @Test
    void testPlayerDeathFromDamage() {
        Player player = new Player(new Position(0, 0));
        
        // Deal massive damage
        player.takeDamage(15);
        
        // HP should not go below 0
        assertTrue(player.getHp() >= 0, "HP should not go below 0");
        assertEquals(0, player.getHp(), "HP should be exactly 0 when dealt excessive damage");
    }
    
    @Test
    void testGameEngineGetSeed() {
        long seed = 12345L;
        GameEngine engine = new GameEngine(5, seed);
        
        assertEquals(seed, engine.getSeed(), "Engine should return the correct seed");
    }
    
    @Test
    void testWallCellProperties() {
        WallCell wallCell = new WallCell();
        assertNull(wallCell.spritePath(), "Wall cell should have null sprite path by default");
    }
    
    @Test
    void testEntryPointDetails() {
        EntryCell entryCell = new EntryCell();
        assertNull(entryCell.spritePath(), "Entry cell should have null sprite path by default");
    }
    
    @Test
    void testLadderCellDetails() {
        LadderCell ladderCell = new LadderCell();
        assertEquals("ladder.png", ladderCell.spritePath(), "Ladder cell should use correct sprite");
    }
    
    @Test
    void testTrapCellDetails() {
        TrapCell trapCell = new TrapCell();
        assertEquals("trap.png", trapCell.spritePath(), "Trap cell should use correct sprite");
    }
    
    @Test
    void testCellDefaultImplementations() {
        EmptyCell emptyCell = new EmptyCell();
        
        // Test default implementations - empty cells have null sprite paths
        assertNull(emptyCell.spritePath(), "Empty cells should have null sprite path");
        
        // Test that onEnter doesn't throw exception
        GameEngine engine = new GameEngine(5);
        Player player = new Player(new Position(0, 0));
        assertDoesNotThrow(() -> emptyCell.onEnter(player, engine), 
            "Empty cell onEnter should not throw exception");
    }
    
    @Test
    void testGoldCellAlreadyCollected() {
        GoldCell goldCell = new GoldCell();
        GameEngine engine = new GameEngine(5);
        Player player = new Player(new Position(0, 0));
        
        // Collect gold once
        int initialScore = player.getScore();
        goldCell.onEnter(player, engine);
        int scoreAfterFirstCollection = player.getScore();
        
        // Try to collect again - should not give more points
        goldCell.onEnter(player, engine);
        int scoreAfterSecondCollection = player.getScore();
        
        assertTrue(scoreAfterFirstCollection > initialScore, "First collection should give points");
        assertEquals(scoreAfterFirstCollection, scoreAfterSecondCollection, 
            "Second collection should not give additional points");
    }
    
    @Test
    void testHealthPotionAlreadyUsed() {
        HealthPotionCell healthCell = new HealthPotionCell();
        GameEngine engine = new GameEngine(5);
        Player player = new Player(new Position(0, 0));
        
        // Damage player first
        player.takeDamage(2);
        int damagedHp = player.getHp();
        
        // Use potion once
        healthCell.onEnter(player, engine);
        int hpAfterFirstUse = player.getHp();
        
        // Damage again
        player.takeDamage(1);
        int hpAfterSecondDamage = player.getHp();
        
        // Try to use potion again - should not heal
        healthCell.onEnter(player, engine);
        int hpAfterSecondUse = player.getHp();
        
        assertTrue(hpAfterFirstUse > damagedHp, "First use should heal player");
        assertEquals(hpAfterSecondDamage, hpAfterSecondUse, 
            "Second use should not heal player");
    }
    
    @Test
    void testPlayerModifyHpPositive() {
        Player player = new Player(new Position(0, 0));
        
        // Damage first
        player.takeDamage(3);
        assertEquals(7, player.getHp(), "Player should have 7 HP");
        
        // Heal using modifyHp
        player.modifyHp(2);
        assertEquals(9, player.getHp(), "Player should have 9 HP after healing");
    }
    
    @Test
    void testPlayerModifyHpNegative() {
        Player player = new Player(new Position(0, 0));
        
        // Use modifyHp with negative value (alternative to takeDamage)
        player.modifyHp(-4);
        assertEquals(6, player.getHp(), "Player should have 6 HP after negative modifyHp");
    }
}
