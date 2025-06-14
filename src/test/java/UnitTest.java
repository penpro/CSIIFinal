import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.csiifinal.UCAHelpers;

public class UnitTest {

    @Test
    void testGetPiFraction() {
        assertEquals("π/2", UCAHelpers.getPiFraction(90), "Should return π/2 for 90 degrees");
    }


}
