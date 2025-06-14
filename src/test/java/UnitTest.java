package com.example.csiifinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    private ScoreHistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new ScoreHistoryManager(5);
    }

    // -------------------------------
    // UCAHelpers Tests
    // -------------------------------
    @Test
    void testGetPiFraction() {
        assertEquals("π/2", UCAHelpers.getPiFraction(90), "Should return π/2 for 90 degrees");
        assertEquals("π", UCAHelpers.getPiFraction(180), "Should return π for 180 degrees");
        assertEquals("7π/6", UCAHelpers.getPiFraction(210), "Should return 7π/6 for 210 degrees");
    }

    @Test
    void testGetLabelForAngleSin() {
        String label = UCAHelpers.getLabelForAngle(30, UnitCircleApp.AngleDisplayMode.SIN, true);
        assertEquals("sin: 1/2", label);
    }

    // -------------------------------
    // ScoreHistoryManager Tests
    // -------------------------------
    @Test
    void testRecordAddsToHistory() {
        historyManager.record(true, 5);
        historyManager.record(false, 4);
        List<String> hist = historyManager.getHistory();
        assertEquals(2, hist.size());
        assertTrue(hist.get(0).contains("✔") || hist.get(0).contains("✘"));
    }

    @Test
    void testClearEmptiesHistory() {
        historyManager.record(true, 10);
        historyManager.clear();
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testHistoryMaxSize() {
        for (int i = 0; i < 10; i++) {
            historyManager.record(i % 2 == 0, i);
        }
        assertEquals(5, historyManager.getHistory().size());  // max size
    }
}
