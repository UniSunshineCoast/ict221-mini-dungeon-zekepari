import org.junit.jupiter.api.Test;

import dungeon.engine.cells.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for additional cell coverage and MeleeMutant inner class.
 * Focus on increasing overall test coverage to reach 85% threshold.
 */
public class TestAdditionalCellCoverage {
    
    @Test
    void testMeleeMutantCellCreation() {
        MeleeMutantCell mutantCell = new MeleeMutantCell();
        assertNotNull(mutantCell);
        assertEquals("zombie.png", mutantCell.spritePath());
    }
    
    @Test
    void testWallCellMethods() {
        WallCell wallCell = new WallCell();
        assertNotNull(wallCell);
        assertNull(wallCell.spritePath()); // Walls have null sprite path by default
    }
    
    @Test
    void testEntryPointCell() {
        EntryCell entryCell = new EntryCell();
        assertNotNull(entryCell);
        assertNull(entryCell.spritePath()); // Entry cells have null sprite path by default
    }
    
    @Test
    void testLadderCellMethods() {
        LadderCell ladderCell = new LadderCell();
        assertNotNull(ladderCell);
        assertEquals("ladder.png", ladderCell.spritePath());
        assertFalse(ladderCell.spritePath().isEmpty());
    }
    
    @Test
    void testTrapCellMethods() {
        TrapCell trapCell = new TrapCell();
        assertNotNull(trapCell);
        assertEquals("trap.png", trapCell.spritePath());
        assertFalse(trapCell.spritePath().isEmpty());
    }
    
    @Test
    void testGoldCellWithCustomSprite() {
        GoldCell goldCell = new GoldCell("custom_treasure.png");
        assertNotNull(goldCell);
        assertEquals("custom_treasure.png", goldCell.spritePath());
        assertFalse(goldCell.spritePath().isEmpty());
    }
    
    @Test
    void testHealthPotionCellWithCustomSprite() {
        HealthPotionCell healthCell = new HealthPotionCell("custom_potion.png");
        assertNotNull(healthCell);
        assertEquals("custom_potion.png", healthCell.spritePath());
        assertFalse(healthCell.spritePath().isEmpty());
    }
    
    @Test
    void testAbstractItemCellMethods() {
        GoldCell goldCell = new GoldCell();
        
        // Test inherited methods from AbstractItemCell
        assertNotNull(goldCell.spritePath());
        assertFalse(goldCell.spritePath().isEmpty());
    }
    
    @Test
    void testCellInterface() {
        EmptyCell emptyCell = new EmptyCell();
        
        // Test Cell interface methods - empty cells have null sprite paths
        assertNull(emptyCell.spritePath());
    }
}
