package com.example.csiifinal;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ScoreHistoryManager {
    private final Queue<String> history = new LinkedList<>();
    private final int maxSize;

    public ScoreHistoryManager(int maxSize) {
        this.maxSize = maxSize;
    }

    public void record(boolean isCorrect, int currentScore) {
        String entry = String.format("%s (%s)",
                isCorrect ? "✔ Correct" : "✘ Incorrect",
                "Score: " + currentScore
        );

        history.add(entry);
        if (history.size() > maxSize) {
            history.poll();
        }
    }

    public void clear() {
        history.clear();
    }

    public void showPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Score History");
        alert.setHeaderText("Recent Actions");

        StringBuilder content = new StringBuilder();
        for (String s : history) {
            content.append(s).append("\n");
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(300, 400);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
}
