import dungeon.engine.Direction;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestDirection {

    @Test
    void testUpDirection() {
        assertEquals(-1, Direction.UP.getDRow());
        assertEquals(0, Direction.UP.getDCol());
    }

    @Test
    void testDownDirection() {
        assertEquals(1, Direction.DOWN.getDRow());
        assertEquals(0, Direction.DOWN.getDCol());
    }

    @Test
    void testLeftDirection() {
        assertEquals(0, Direction.LEFT.getDRow());
        assertEquals(-1, Direction.LEFT.getDCol());
    }

    @Test
    void testRightDirection() {
        assertEquals(0, Direction.RIGHT.getDRow());
        assertEquals(1, Direction.RIGHT.getDCol());
    }

    @Test
    void testFromKeyCodeUp() {
        assertEquals(Direction.UP, Direction.fromKeyCode(KeyCode.UP));
    }

    @Test
    void testFromKeyCodeDown() {
        assertEquals(Direction.DOWN, Direction.fromKeyCode(KeyCode.DOWN));
    }

    @Test
    void testFromKeyCodeLeft() {
        assertEquals(Direction.LEFT, Direction.fromKeyCode(KeyCode.LEFT));
    }

    @Test
    void testFromKeyCodeRight() {
        assertEquals(Direction.RIGHT, Direction.fromKeyCode(KeyCode.RIGHT));
    }

    @Test
    void testFromKeyCodeNull() {
        assertNull(Direction.fromKeyCode(null));
    }

    @Test
    void testFromKeyCodeInvalid() {
        assertNull(Direction.fromKeyCode(KeyCode.SPACE));
    }
}