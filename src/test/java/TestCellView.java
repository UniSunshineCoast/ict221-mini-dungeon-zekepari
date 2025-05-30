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
        assertEquals("wall.png", wallCell.spritePath());
        assertFalse(wallCell.canEnter());
    }
    
    @Test
    void testEntryPointCell() {
        EntryCell entryCell = new EntryCell();
        assertNotNull(entryCell);
        assertEquals("entry.png", entryCell.spritePath());
        assertTrue(entryCell.canEnter());
    }
    
    @Test
    void testLadderCellMethods() {
        LadderCell ladderCell = new LadderCell();
        assertNotNull(ladderCell);
        assertEquals("ladder.png", ladderCell.spritePath());
        assertTrue(ladderCell.canEnter());
    }
    
    @Test
    void testTrapCellMethods() {
        TrapCell trapCell = new TrapCell();
        assertNotNull(trapCell);
        assertEquals("trap.png", trapCell.spritePath());
        assertTrue(trapCell.canEnter());
    }
    
    @Test
    void testGoldCellWithCustomSprite() {
        GoldCell goldCell = new GoldCell("custom_treasure.png");
        assertNotNull(goldCell);
        assertEquals("custom_treasure.png", goldCell.spritePath());
        assertTrue(goldCell.canEnter());
    }
    
    @Test
    void testHealthPotionCellWithCustomSprite() {
        HealthPotionCell healthCell = new HealthPotionCell("custom_potion.png");
        assertNotNull(healthCell);
        assertEquals("custom_potion.png", healthCell.spritePath());
        assertTrue(healthCell.canEnter());
    }
    
    @Test
    void testAbstractItemCellMethods() {
        GoldCell goldCell = new GoldCell();
        
        // Test inherited methods from AbstractItemCell
        assertTrue(goldCell.canEnter());
        assertNotNull(goldCell.spritePath());
        assertFalse(goldCell.spritePath().isEmpty());
    }
    
    @Test
    void testCellInterface() {
        EmptyCell emptyCell = new EmptyCell();
        
        // Test Cell interface methods
        assertTrue(emptyCell.canEnter());
        assertNotNull(emptyCell.spritePath());
    }
}
