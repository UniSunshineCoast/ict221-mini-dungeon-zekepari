import dungeon.engine.*;
import dungeon.engine.cells.*;import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestCell {

    @Test
    void testEmptyCell() {
        Cell cell = new EmptyCell();
        assertNull(cell.spritePath()); // Empty cells have no sprite
    }
    
    @Test
    void testWallCell() {
        WallCell cell = new WallCell();
        assertNull(cell.spritePath()); // Wall cells have no sprite
        assertTrue(cell.blocksMovement());
    }
    
    @Test
    void testEntryCell() {
        Cell cell = new EntryCell();
        assertNull(cell.spritePath()); // Entry cells have no sprite
    }
    
    @Test
    void testLadderCell() {
        Cell cell = new LadderCell();
        assertEquals("ladder.png", cell.spritePath());
    }
    
    @Test
    void testCustomSpriteCell() {
        String customSprite = "custom_sprite.png";
        Cell cell = new EmptyCell(customSprite);
        assertEquals(customSprite, cell.spritePath());
    }
}