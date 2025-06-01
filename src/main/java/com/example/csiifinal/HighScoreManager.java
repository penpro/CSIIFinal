package com.example.csiifinal;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class HighScoreManager {

    private static final String HIGHSCORE_FILE = "highscores.txt";
    private static final int MAX_ENTRIES = 20;

    public static class HighScoreEntry {
        final int score;
        final String initials;
        final String timestamp;
        final String mode;

        public HighScoreEntry(int score, String initials, String timestamp, String mode) {
            this.score = score;
            this.initials = initials;
            this.timestamp = timestamp;
            this.mode = mode;
        }

        @Override
        public String toString() {
            return String.format("%d - %s (%s) [%s]", score, initials, timestamp, mode);
        }
    }

    private final List<HighScoreEntry> highScores = new ArrayList<>();

    public HighScoreManager() {
        loadHighScores();
    }

    private void loadHighScores() {
        highScores.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(HIGHSCORE_FILE));
            for (String line : lines) {
                String[] parts = line.split(",", -1); // use -1 to keep empty trailing fields
                if (parts.length >= 3) {
                    try {
                        int score = Integer.parseInt(parts[0].trim());
                        String initials = parts[1].trim();
                        String timestamp = parts[2].trim();
                        String mode = (parts.length >= 4) ? parts[3].trim() : "Unknown";  // fallback if no mode
                        highScores.add(new HighScoreEntry(score, initials, timestamp, mode));
                    } catch (NumberFormatException ignored) {}
                }
            }
            highScores.sort(Comparator.comparingInt(h -> -h.score));
        } catch (IOException ignored) {}
    }


    public void trySaveScore(int score, String mode) {
        if (isHighScore(score)) {
            String initials = promptInitials();
            if (initials == null) return;

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            HighScoreEntry newEntry = new HighScoreEntry(score, initials, timestamp, mode);
            highScores.add(newEntry);
            highScores.sort(Comparator.comparingInt(h -> -h.score));
            if (highScores.size() > MAX_ENTRIES) {
                highScores.remove(highScores.size() - 1);
            }
            saveHighScores();
        }
    }

    private boolean isHighScore(int score) {
        return highScores.size() < MAX_ENTRIES || score > highScores.get(highScores.size() - 1).score;
    }

    private String promptInitials() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New High Score!");
        dialog.setHeaderText("You made it into the Top 20!");
        dialog.setContentText("Enter your initials:");
        Optional<String> result = dialog.showAndWait();

        String initials = result.orElse("").trim().toUpperCase();
        if (initials.isEmpty()) return null;
        return initials.length() > 3 ? initials.substring(0, 3) : initials;
    }

    private void saveHighScores() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(HIGHSCORE_FILE))) {
            for (HighScoreEntry entry : highScores) {
                writer.write(entry.score + "," + entry.initials + "," + entry.timestamp + "," + entry.mode + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showHighScoresPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("High Scores");
        alert.setHeaderText("Top 20 High Scores");

        StringBuilder content = new StringBuilder();
        int rank = 1;
        for (HighScoreEntry entry : highScores) {
            content.append(String.format("%2d. %s\n", rank++, entry));
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setPrefSize(400, 400);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}
