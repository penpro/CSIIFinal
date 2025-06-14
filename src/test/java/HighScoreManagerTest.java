import com.example.csiifinal.HighScoreManager;
import com.example.csiifinal.HighScoreManager.HighScoreEntry;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HighScoreManagerTest {

    private static final String TEST_FILE = "test_highscores.txt";

    @BeforeEach
    void setup() throws IOException {
        Files.writeString(Paths.get(TEST_FILE), """
            10,ABC,2024-01-01 10:00,DEGREES
            5,XYZ,2024-01-02 11:00,SIN
        """);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testLoadHighScoresAndSortOrder() {
        HighScoreManager manager = new HighScoreManager() {
            @Override
            protected void loadHighScores() {
                super.loadHighScores();
                assertEquals(2, getAllScores().size());
                assertEquals("ABC", getAllScores().get(0).initials);  // 10 > 5
            }

            @Override
            protected String getHighScoreFilePath() {
                return TEST_FILE;
            }
        };
    }

    @Test
    void testIsHighScoreWithSpace() {
        HighScoreManager manager = new HighScoreManager() {
            @Override
            protected String getHighScoreFilePath() {
                return TEST_FILE;
            }
        };
        assertTrue(manager.getAllScores().size() < 20);  // Room to grow
        assertTrue(manager.isHighScore(1));
    }

    @Test
    void testGetAllScoresImmutability() {
        HighScoreManager manager = new HighScoreManager() {
            @Override
            protected String getHighScoreFilePath() {
                return TEST_FILE;
            }
        };

        List<HighScoreEntry> scores = manager.getAllScores();
        int originalSize = scores.size();
        scores.clear();  // Should not affect internal list
        assertEquals(originalSize, manager.getAllScores().size());
    }
}
