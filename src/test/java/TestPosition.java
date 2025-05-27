import dungeon.engine.Position;
import dungeon.engine.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPosition {

    @Test
    void testConstructorValidPosition() {
        Position position = new Position(5, 5);
        assertEquals(5, position.getRow());
        assertEquals(5, position.getCol());
    }

    @Test
    void testConstructorMinBounds() {
        Position position = new Position(0, 0);
        assertEquals(0, position.getRow());
        assertEquals(0, position.getCol());
    }

    @Test
    void testConstructorMaxBounds() {
        Position position = new Position(9, 9);
        assertEquals(9, position.getRow());
        assertEquals(9, position.getCol());
    }

    @Test
    void testConstructorInvalidBelowMinRow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1, 5);
        });
        assertTrue(exception.getMessage().contains("Position coordinates must be between"));
    }

    @Test
    void testConstructorInvalidAboveMaxRow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Position(10, 5);
        });
        assertTrue(exception.getMessage().contains("Position coordinates must be between"));
    }

    @Test
    void testConstructorInvalidBelowMinCol() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Position(5, -1);
        });
        assertTrue(exception.getMessage().contains("Position coordinates must be between"));
    }

    @Test
    void testConstructorInvalidAboveMaxCol() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Position(5, 10);
        });
        assertTrue(exception.getMessage().contains("Position coordinates must be between"));
    }

    @Test
    void testPlusDirectionUp() {
        Position position = new Position(5, 5);
        Position newPosition = position.plus(Direction.UP);
        assertEquals(4, newPosition.getRow());
        assertEquals(5, newPosition.getCol());
    }

    @Test
    void testPlusDirectionDown() {
        Position position = new Position(5, 5);
        Position newPosition = position.plus(Direction.DOWN);
        assertEquals(6, newPosition.getRow());
        assertEquals(5, newPosition.getCol());
    }

    @Test
    void testPlusDirectionLeft() {
        Position position = new Position(5, 5);
        Position newPosition = position.plus(Direction.LEFT);
        assertEquals(5, newPosition.getRow());
        assertEquals(4, newPosition.getCol());
    }

    @Test
    void testPlusDirectionRight() {
        Position position = new Position(5, 5);
        Position newPosition = position.plus(Direction.RIGHT);
        assertEquals(5, newPosition.getRow());
        assertEquals(6, newPosition.getCol());
    }

    @Test
    void testPlusDirectionNull() {
        Position position = new Position(5, 5);
        Position newPosition = position.plus(null);
        assertSame(position, newPosition);
    }

    @Test
    void testPlusDirectionOutOfBoundsUp() {
        Position position = new Position(0, 5);
        Position newPosition = position.plus(Direction.UP);
        assertNull(newPosition);
    }

    @Test
    void testPlusDirectionOutOfBoundsDown() {
        Position position = new Position(9, 5);
        Position newPosition = position.plus(Direction.DOWN);
        assertNull(newPosition);
    }

    @Test
    void testPlusDirectionOutOfBoundsLeft() {
        Position position = new Position(5, 0);
        Position newPosition = position.plus(Direction.LEFT);
        assertNull(newPosition);
    }

    @Test
    void testPlusDirectionOutOfBoundsRight() {
        Position position = new Position(5, 9);
        Position newPosition = position.plus(Direction.RIGHT);
        assertNull(newPosition);
    }

    @Test
    void testEquals() {
        Position position1 = new Position(5, 5);
        Position position2 = new Position(5, 5);
        Position position3 = new Position(6, 5);
        Position position4 = new Position(5, 6);

        assertEquals(position1, position2);
        assertNotEquals(position1, position3);
        assertNotEquals(position1, position4);
        assertNotEquals(position1, null);
        assertNotEquals(position1, "Not a Position");
    }

    @Test
    void testHashCode() {
        Position position1 = new Position(5, 5);
        Position position2 = new Position(5, 5);
        
        assertEquals(position1.hashCode(), position2.hashCode());
    }

    @Test
    void testToString() {
        Position position = new Position(5, 5);
        assertEquals("Position(5,5)", position.toString());
    }
}