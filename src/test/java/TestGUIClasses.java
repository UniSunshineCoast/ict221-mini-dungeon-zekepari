import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dungeon.gui.CellView;
import dungeon.engine.cells.*;

/**
 * Tests for GUI classes that can be tested without full JavaFX setup.
 * These tests focus on basic logic and getter methods.
 */
public class TestGUIClasses {
    
    @Test
    void testCellViewGettersWithEmptyCell() {
        EmptyCell cell = new EmptyCell();
        CellView cellView = new CellView(cell, 5, 10);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(5, cellView.getRow());
        assertEquals(10, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithWallCell() {
        WallCell cell = new WallCell();
        CellView cellView = new CellView(cell, 0, 0);
        
        // Test getter methods  
        assertEquals(cell, cellView.getCell());
        assertEquals(0, cellView.getRow());
        assertEquals(0, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithGoldCell() {
        GoldCell cell = new GoldCell();
        CellView cellView = new CellView(cell, 3, 7);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(3, cellView.getRow());
        assertEquals(7, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithHealthPotionCell() {
        HealthPotionCell cell = new HealthPotionCell();
        CellView cellView = new CellView(cell, 2, 4);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(2, cellView.getRow());
        assertEquals(4, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithTrapCell() {
        TrapCell cell = new TrapCell();
        CellView cellView = new CellView(cell, 1, 8);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(1, cellView.getRow());
        assertEquals(8, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithLadderCell() {
        LadderCell cell = new LadderCell();
        CellView cellView = new CellView(cell, 9, 9);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(9, cellView.getRow());
        assertEquals(9, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithEntryCell() {
        EntryCell cell = new EntryCell();
        CellView cellView = new CellView(cell, 4, 6);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(4, cellView.getRow());
        assertEquals(6, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithMeleeMutantCell() {
        MeleeMutantCell cell = new MeleeMutantCell();
        CellView cellView = new CellView(cell, 8, 2);
        
        // Test getter methods
        assertEquals(cell, cellView.getCell());
        assertEquals(8, cellView.getRow());
        assertEquals(2, cellView.getCol());
    }
    
    @Test
    void testCellViewGettersWithCustomSpriteCells() {
        GoldCell customGold = new GoldCell("custom_treasure.png");
        CellView goldView = new CellView(customGold, 1, 1);
        
        assertEquals(customGold, goldView.getCell());
        assertEquals(1, goldView.getRow());
        assertEquals(1, goldView.getCol());
        
        HealthPotionCell customHealth = new HealthPotionCell("custom_health.png");
        CellView healthView = new CellView(customHealth, 2, 2);
        
        assertEquals(customHealth, healthView.getCell());
        assertEquals(2, healthView.getRow());
        assertEquals(2, healthView.getCol());
    }
    
    @Test
    void testCellViewWithNegativeCoordinates() {
        EmptyCell cell = new EmptyCell();
        CellView cellView = new CellView(cell, -1, -5);
        
        // Test that negative coordinates are handled correctly
        assertEquals(cell, cellView.getCell());
        assertEquals(-1, cellView.getRow());
        assertEquals(-5, cellView.getCol());
    }
    
    @Test
    void testCellViewWithLargeCoordinates() {
        WallCell cell = new WallCell();
        CellView cellView = new CellView(cell, 1000, 9999);
        
        // Test that large coordinates are handled correctly
        assertEquals(cell, cellView.getCell());
        assertEquals(1000, cellView.getRow());
        assertEquals(9999, cellView.getCol());
    }
}
