package com.example.csiifinal;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;

import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import static com.example.csiifinal.UCAHelpers.*;

/**
 * JavaFX application that displays an interactive unit circle.
 * Allows users to explore trigonometric values and practice identifying them in a game mode.
 */
public class UnitCircleApp extends Application {

    private int currentTargetAngle = 0;
    private final Random random = new Random();
    private final Label promptLabel = new Label("Click the button at 30 degrees");
    private final Button startGameButton = new Button("Start Game");
    // the big circle
    private final Circle circle = new Circle();


    // the inner circle
    private final Circle innerCircle = new Circle();

    // circle colors
    private static final Color CORRECT_COLOR = Color.LIGHTGREEN;
    private static final Color INCORRECT_COLOR = Color.INDIANRED;
    private static final Color DEFAULT_COLOR = Color.CORNFLOWERBLUE;

    // sounds
    private final AudioClip correctSound = new AudioClip(getClass().getResource("/sounds/correct.wav").toExternalForm());
    private final AudioClip incorrectSound = new AudioClip(getClass().getResource("/sounds/incorrect.mp3").toExternalForm());

    // timer
    private final Label timerLabel = new Label();
    private Timeline countdownTimer;
    private int secondsRemaining = 120;

    // score ui
    private final Label scoreLabel = new Label("Score: 0");
    private int score = 0;

    // scores history
    private final Button historyButton = new Button("Score History");
    private final ScoreHistoryManager scoreHistoryManager = new ScoreHistoryManager(50);

    // end game button
    private final Button endGameButton = new Button("End Game");

    // high score save game and button
    private final Button highScoresButton = new Button("High Scores");
    private final HighScoreManager highScoreManager = new HighScoreManager();

    // overlays to restrict sin, cos, tangent domains
    private final Rectangle quadrantIOverlay = new Rectangle();
    private final Rectangle quadrantIIOverlay = new Rectangle();
    private final Rectangle quadrantIIIOverlay = new Rectangle();
    private final Rectangle quadrantIVOverlay = new Rectangle();

    // new pane for the overlays
    private final Pane overlayPane = new Pane();

    // used for adding weights to the game progress to focus on missed items
    private final Map<Integer, Integer> missCounts = new HashMap<>();


    /** Enum for the mode of trigonometric value display. */
    enum AngleDisplayMode {
        DEGREES, RADIANS, SIN, COS, TAN
    }

    /** Defines different application modes for the app. */
    public enum AppMode {
        EXPLORATION,
        UNIT_CIRCLE,
        SPECIAL_TRIANGLES
    }

    // sets default function mode to degrees
    private AngleDisplayMode currentMode = AngleDisplayMode.DEGREES;
    // function mode selector combo box and label
    private final Label functionLabel = new Label("FUNCTION");
    private final ComboBox<String> modeSelector = new ComboBox<>();
    // game mode selector combo box and label
    private final Label modeLabel = new Label("MODE");
    private final ComboBox<String> modeComboBox = new ComboBox<>();
    // sets current game mode to exploration via the enumerator
    private AppMode currentAppMode = AppMode.EXPLORATION;

    // array lists for the buttons and lines for the different angles
    private final List<Button> buttons = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();

    /**
     * JavaFX application entry point.
     * @param primaryStage the primary window for this application
     */
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #808080;");

        // makes the big circle and makes the clip mask from it
        circle.setFill(Color.CORNFLOWERBLUE);
        root.getChildren().add(circle);
        Circle clipCircle = new Circle();
        clipCircle.radiusProperty().bind(circle.radiusProperty());
        clipCircle.centerXProperty().bind(circle.centerXProperty());
        clipCircle.centerYProperty().bind(circle.centerYProperty());
        overlayPane.setClip(clipCircle);  // Apply clip only to overlays

        // Add overlays to dim incorrect half for sin(x)
        // All overlays should be sized at half radius
        double halfRadius = circle.getRadius();
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        root.getChildren().add(overlayPane);

        // Quadrant I (top-right)
        quadrantIOverlay.setX(cx);
        quadrantIOverlay.setY(cy - halfRadius);
        quadrantIOverlay.setWidth(halfRadius);
        quadrantIOverlay.setHeight(halfRadius);

        // Quadrant II (top-left)
        quadrantIIOverlay.setX(cx - halfRadius);
        quadrantIIOverlay.setY(cy - halfRadius);
        quadrantIIOverlay.setWidth(halfRadius);
        quadrantIIOverlay.setHeight(halfRadius);

        // Quadrant III (bottom-left)
        quadrantIIIOverlay.setX(cx - halfRadius);
        quadrantIIIOverlay.setY(cy);
        quadrantIIIOverlay.setWidth(halfRadius);
        quadrantIIIOverlay.setHeight(halfRadius);

        // Quadrant IV (bottom-right)
        quadrantIVOverlay.setX(cx);
        quadrantIVOverlay.setY(cy);
        quadrantIVOverlay.setWidth(halfRadius);
        quadrantIVOverlay.setHeight(halfRadius);

        // Common styling
        for (Rectangle r : List.of(quadrantIOverlay, quadrantIIOverlay, quadrantIIIOverlay, quadrantIVOverlay)) {
            r.setFill(Color.rgb(0, 0, 0, 0.4));
            r.setMouseTransparent(true);
            r.setVisible(false);
        }

        // adds the overlays as children of root
        for (Rectangle r : List.of(quadrantIOverlay, quadrantIIOverlay, quadrantIIIOverlay, quadrantIVOverlay)) {
            r.setFill(Color.rgb(0, 0, 0, 0.4));
            r.setMouseTransparent(true);
            r.setVisible(false);
        }

        overlayPane.getChildren().addAll(
                quadrantIOverlay, quadrantIIOverlay, quadrantIIIOverlay, quadrantIVOverlay
        );
        // sets to overlays to invisible for now
        overlayPane.setVisible(false);

        // Create radial lines for the unit circle
        for (int angle : ANGLES) {
            Line line = new Line();
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1.5);
            lines.add(line);
            root.getChildren().add(line);
        }

        // score
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        scoreLabel.setVisible(false);
        root.getChildren().add(scoreLabel);

        // Create angle buttons and assign their logic
        for (int angle : ANGLES) {
            Button btn = new Button(angle + "°");
            btn.setStyle("-fx-font-size: 14px; -fx-padding: 8 12 8 12;");
            btn.setPrefWidth(75);
            btn.setPrefHeight(45);
            btn.setFocusTraversable(false);
            final int btnAngle = angle;

            // Handle button clicks in Game mode
            btn.setOnAction(e -> {
                // if you're in triangle mode, call the associated triangle
                if (currentAppMode == AppMode.SPECIAL_TRIANGLES) {
                    SpecialTriangle.showTriangleForAngle(btnAngle);
                    return;
                }

                // if you're in explore mode (!unit circle) don't do anything
                if (currentAppMode != AppMode.UNIT_CIRCLE) return;

                String expectedLabel;
                switch (currentMode) {
                    case DEGREES -> expectedLabel = btnAngle + "°";
                    case RADIANS -> expectedLabel = getPiFraction(btnAngle);
                    case SIN -> expectedLabel = "sin: " + SIN_VALUES.getOrDefault(btnAngle, "?");
                    case COS -> expectedLabel = "cos: " + COS_VALUES.getOrDefault(btnAngle, "?");
                    case TAN -> expectedLabel = "tan: " + TAN_VALUES.getOrDefault(btnAngle, "?");
                    default -> expectedLabel = "?";
                }

                if (btnAngle == currentTargetAngle) {
                    updateScore(1);
                    flashCircle(CORRECT_COLOR, true);
                    pickNewTargetAngle();
                    int currentMisses = missCounts.getOrDefault(btnAngle, 0);
                    if (currentMisses > 0) {
                        missCounts.put(btnAngle, currentMisses - 1);
                    }
                } else {
                    updateScore(-1);
                    flashCircle(INCORRECT_COLOR, false);
                    missCounts.put(currentTargetAngle, missCounts.getOrDefault(currentTargetAngle, 0) + 10);
                    System.out.println("Miss counts: " + missCounts);
                }
            });

            buttons.add(btn);
            root.getChildren().add(btn);
        }

        // Listen for window resize to update layout
        root.widthProperty().addListener((obs, oldVal, newVal) -> updateLayout(root));
        root.heightProperty().addListener((obs, oldVal, newVal) -> updateLayout(root));

        // window and title
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Unit Circle");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Perform layout after stage is shown
        Platform.runLater(() -> updateLayout(root));

        // small inner circle
        innerCircle.setFill(Color.GRAY);
        root.getChildren().add(innerCircle);

        // UI labels and combo boxes
        promptLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        root.getChildren().add(promptLabel);

        functionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        root.getChildren().add(functionLabel);

        // function combo box
        modeSelector.getItems().addAll("Degrees", "Radians", "sin(x)", "cos(x)", "tan(x)");
        modeSelector.setValue("Degrees");
        modeSelector.setOnAction(e -> {
            switch (modeSelector.getValue()) {
                case "Degrees" -> currentMode = AngleDisplayMode.DEGREES;
                case "Radians" -> currentMode = AngleDisplayMode.RADIANS;
                case "sin(x)" -> currentMode = AngleDisplayMode.SIN;
                case "cos(x)" -> currentMode = AngleDisplayMode.COS;
                case "tan(x)" -> currentMode = AngleDisplayMode.TAN;
            }
            updateButtonLabels();
            if (currentAppMode == AppMode.UNIT_CIRCLE) pickNewTargetAngle();
        });
        root.getChildren().add(modeSelector);


        // mode combo box
        modeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        root.getChildren().add(modeLabel);

        modeComboBox.getItems().addAll("Exploration", "Unit Circle", "Special Triangles");
        modeComboBox.setValue("Exploration");
        modeComboBox.setStyle("-fx-font-size: 14px;");
        modeComboBox.setFocusTraversable(false);
        root.getChildren().add(modeComboBox);

        double comboBoxWidth = 120;
        modeComboBox.setPrefWidth(comboBoxWidth);
        modeSelector.setPrefWidth(comboBoxWidth);
        String comboStyle = "-fx-font-size: 14px;";
        modeComboBox.setStyle(comboStyle);
        modeSelector.setStyle(comboStyle);

        // Mode switching logic
        modeComboBox.setOnAction(e -> {
            String selected = modeComboBox.getValue();
            switch (selected) {
                case "Exploration" -> currentAppMode = AppMode.EXPLORATION;
                case "Unit Circle" -> currentAppMode = AppMode.UNIT_CIRCLE;
                case "Special Triangles" -> currentAppMode = AppMode.SPECIAL_TRIANGLES;
            }

            if (currentAppMode == AppMode.UNIT_CIRCLE) {
                endGameButton.setVisible(true);
                promptLabel.setVisible(false);
                startGameButton.setVisible(true);
                stopTimer();
            } else {
                endGameButton.setVisible(false);
                stopTimer();
                promptLabel.setVisible(false);
                startGameButton.setVisible(false);
            }

            updateButtonLabels();
            updatePromptVisibility();
        });

        // start game button
        startGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20;");
        startGameButton.setFocusTraversable(false);
        startGameButton.setVisible(false);
        // do things when pressed
        startGameButton.setOnAction(e -> {
            startGameButton.setVisible(false);
            promptLabel.setVisible(true);
            startTimer();
            pickNewTargetAngle();
            score = 0;
            updateScore(0);  // refresh label
            scoreLabel.setVisible(true);
            scoreHistoryManager.clear();        // start fresh
            historyButton.setVisible(true);
            overlayPane.setVisible(true);
        });
        root.getChildren().add(startGameButton);

        // Ensure correct prompt visibility on startup and creates a listener to change the prompt centering on update
        updatePromptVisibility();
        promptLabel.textProperty().addListener((obs, oldText, newText) -> forcePromptCentering());

        // initialize the timer
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        timerLabel.setLayoutY(10); // top of screen
        timerLabel.setVisible(false);
        root.getChildren().add(timerLabel);

        // initialize the history screen
        historyButton.setStyle("-fx-font-size: 14px;");
        historyButton.setFocusTraversable(false);
        historyButton.setOnAction(e -> scoreHistoryManager.showPopup());
        historyButton.setVisible(false);
        root.getChildren().add(historyButton);

        // end game button
        endGameButton.setStyle("-fx-font-size: 14px;");
        endGameButton.setFocusTraversable(false);
        endGameButton.setVisible(false);
        endGameButton.setOnAction(e -> resetGame());
        root.getChildren().add(endGameButton);

        // high score button
        highScoresButton.setStyle("-fx-font-size: 14px;");
        highScoresButton.setFocusTraversable(false);
        highScoresButton.setOnAction(e -> highScoreManager.showHighScoresPopup());
        highScoresButton.setVisible(true);
        root.getChildren().add(highScoresButton);

        // resets the weighting factors
        for (int angle : ANGLES) {
            missCounts.put(angle, 0);
        }


    }



    /**
     * Used to show and hide the prompt for a given game mode and makes sure it checks centering
     */
    private void updatePromptVisibility() {
        boolean shouldShow = (currentAppMode == AppMode.UNIT_CIRCLE) && !startGameButton.isVisible();
        promptLabel.setVisible(shouldShow);
        if (shouldShow) forcePromptCentering();
    }

    /**
     * Centers the prompt
     */
    private void forcePromptCentering() {
        Platform.runLater(() -> {
            if (promptLabel.getScene() != null) {
                promptLabel.applyCss();  // ensures layout data is updated
                promptLabel.layout();    // forces layout pass
                double sceneWidth = promptLabel.getScene().getWidth();
                promptLabel.setLayoutX((sceneWidth - promptLabel.getWidth()) / 2);
            }
        });
    }

    /**
     * Updates the layout of all buttons and UI elements based on window size.
     * @param root the root pane to update
     */
    private void updateLayout(Pane root) {
        if (buttons.size() != ANGLES.length || lines.size() != ANGLES.length) return;

        double w = root.getWidth();
        double h = root.getHeight();
        double size = Math.min(w, h);
        double radius = size * 2 / 3 / 2;
        double centerX = w / 2;
        double centerY = h / 2;

        circle.setRadius(radius);
        circle.setCenterX(centerX);
        circle.setCenterY(centerY);

        // Position buttons and lines around the circle
        for (int i = 0; i < ANGLES.length; i++) {
            int angleDeg = ANGLES[i];
            double radians = Math.toRadians(angleDeg);
            double endX = centerX + Math.cos(radians) * radius;
            double endY = centerY - Math.sin(radians) * radius;

            Button btn = buttons.get(i);
            double btnWidth = btn.getWidth() == 0 ? 40 : btn.getWidth();
            double btnHeight = btn.getHeight() == 0 ? 20 : btn.getHeight();
            btn.setLayoutX(endX - btnWidth / 2);
            btn.setLayoutY(endY - btnHeight / 2);

            Line line = lines.get(i);
            line.setStartX(centerX);
            line.setStartY(centerY);
            line.setEndX(endX);
            line.setEndY(endY);
        }

        // Position inner circle and labels
        innerCircle.setRadius(radius / 2.5);
        innerCircle.setCenterX(centerX);
        innerCircle.setCenterY(centerY);

        promptLabel.setLayoutX(centerX - promptLabel.getWidth() / 2);
        promptLabel.setLayoutY(centerY - promptLabel.getHeight() / 2);

        double selectorX = root.getWidth() - modeSelector.getWidth() - 10;
        functionLabel.setLayoutX(selectorX);
        functionLabel.setLayoutY(10);
        modeSelector.setLayoutX(selectorX);
        modeSelector.setLayoutY(functionLabel.getLayoutY() + functionLabel.getHeight() + 5);

        double padding = 10;
        modeLabel.setLayoutX(padding);
        modeLabel.setLayoutY(padding);
        modeComboBox.setLayoutX(padding);
        modeComboBox.setLayoutY(modeLabel.getLayoutY() + modeLabel.getHeight() + 5);

        // Center the timer at the top
        timerLabel.setLayoutX((root.getWidth() - timerLabel.getWidth()) / 2);
        timerLabel.setLayoutY(10);

        // Center Start Game button in the same place as promptLabel
        startGameButton.setLayoutX(centerX - startGameButton.getWidth() / 2);
        startGameButton.setLayoutY(centerY - startGameButton.getHeight() / 2);

        // score layout
        scoreLabel.setLayoutX((root.getWidth() - scoreLabel.getWidth()) / 2);
        scoreLabel.setLayoutY(timerLabel.getLayoutY() + timerLabel.getHeight() + 5);

        // history button layout
        historyButton.setLayoutX(10);
        historyButton.setLayoutY(root.getHeight() - historyButton.getHeight() - 10);

        // end game button layout
        endGameButton.setLayoutX(modeSelector.getLayoutX());
        endGameButton.setLayoutY(modeSelector.getLayoutY() + modeSelector.getHeight() + 10);

        // high score button layout
        highScoresButton.setLayoutX(historyButton.getLayoutX());
        highScoresButton.setLayoutY(historyButton.getLayoutY() - highScoresButton.getHeight() - 10);

        // Gray overlay for incorrect sin(x) region
        double halfRadius = circle.getRadius();
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();

        // Position and size quadrant overlays
        quadrantIOverlay.setX(cx);
        quadrantIOverlay.setY(cy - halfRadius);
        quadrantIOverlay.setWidth(halfRadius);
        quadrantIOverlay.setHeight(halfRadius);

        quadrantIIOverlay.setX(cx - halfRadius);
        quadrantIIOverlay.setY(cy - halfRadius);
        quadrantIIOverlay.setWidth(halfRadius);
        quadrantIIOverlay.setHeight(halfRadius);

        quadrantIIIOverlay.setX(cx - halfRadius);
        quadrantIIIOverlay.setY(cy);
        quadrantIIIOverlay.setWidth(halfRadius);
        quadrantIIIOverlay.setHeight(halfRadius);

        quadrantIVOverlay.setX(cx);
        quadrantIVOverlay.setY(cy);
        quadrantIVOverlay.setWidth(halfRadius);
        quadrantIVOverlay.setHeight(halfRadius);
    }

    /**
     * Updates the labels of all angle buttons based on the current display mode.
     */
    private void updateButtonLabels() {
        for (int i = 0; i < ANGLES.length; i++) {
            Button btn = buttons.get(i);

            if (currentAppMode == AppMode.UNIT_CIRCLE) {
                btn.setText("x");
                continue;
            }

            int angle = ANGLES[i];
            double radians = Math.toRadians(angle);

            String label = getLabelForAngle(angle, currentMode, false);
            btn.setText(label);
        }
    }

    /**
     * Picks a new target angle for the game mode and updates the prompt label.
     */
    private void pickNewTargetAngle() {
        if (currentAppMode != AppMode.UNIT_CIRCLE) return;

        // Weighted selection based on miss counts
        List<Integer> weightedAngles = new ArrayList<>();
        for (int angle : ANGLES) {
            int weight = 1 + missCounts.getOrDefault(angle, 0); // base weight of 1
            for (int i = 0; i < weight; i++) {
                weightedAngles.add(angle);
            }
        }

        int newAngle;
        do {
            newAngle = weightedAngles.get(random.nextInt(weightedAngles.size()));
        } while (newAngle == currentTargetAngle);

        currentTargetAngle = newAngle;

        String label = getLabelForAngle(currentTargetAngle, currentMode, true);
        promptLabel.setText("Click the button at " + label);

        switch (currentMode) {
            case SIN -> updateQuadrantOverlaysForSin();
            case COS -> updateQuadrantOverlaysForCos();
            case TAN -> updateQuadrantOverlaysForTan();
            default -> showOnlyQuadrants(1, 2, 3, 4);
        }
    }


    /**
     * Briefly flashes the main circle with a specified color to indicate whether the player's answer was correct or incorrect,
     * and plays the corresponding sound effect. After a short delay, the circle returns to its default color.
     *
     * @param flashColor the color to flash the circle with (e.g., green for correct, red for incorrect)
     * @param isCorrect  whether the answer was correct (true) or incorrect (false); used to determine sound effect
     */
    private void flashCircle(Color flashColor, boolean isCorrect) {
        // Set the fill color of the circle to indicate correctness
        circle.setFill(flashColor);

        // Play the appropriate sound based on whether the answer was correct
        if (isCorrect) {
            correctSound.play();
        } else {
            incorrectSound.play();
        }

        // Launch a background thread to wait briefly, then revert the circle color
        new Thread(() -> {
            try {
                // Pause for 300 milliseconds to let the user see the flash
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
                // If the thread is interrupted, ignore it; not critical for UI feedback
            }

            // Return the circle to its default color on the JavaFX Application Thread
            Platform.runLater(() -> circle.setFill(DEFAULT_COLOR));
        }).start();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void startTimer() {
        secondsRemaining = 120;
        timerLabel.setVisible(true);
        updateTimerLabel();

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            updateTimerLabel();

            if (secondsRemaining <= 0) {
                countdownTimer.stop();
                promptLabel.setText("Time's up! Final Score: " + score);
                highScoreManager.trySaveScore(score, currentMode.name());
            }
        }));
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        timerLabel.setVisible(false);
        scoreLabel.setVisible(false);
        historyButton.setVisible(false);
    }

    private void updateTimerLabel() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("Time Left: %d:%02d", minutes, seconds));
        // Center horizontally
        if (timerLabel.getScene() != null) {
            timerLabel.setLayoutX((timerLabel.getScene().getWidth() - timerLabel.getWidth()) / 2);
        }
    }

    private void updateScore(int delta) {
        score += delta;
        scoreLabel.setText("Score: " + score);

        scoreHistoryManager.record(delta > 0, score);
    }

    private void resetGame() {
        highScoreManager.trySaveScore(score, currentMode.name());
        stopTimer();
        score = 0;
        updateScore(0);
        scoreLabel.setVisible(false);
        promptLabel.setVisible(false);
        historyButton.setVisible(false);
        endGameButton.setVisible(false);
        startGameButton.setVisible(true);
        scoreHistoryManager.clear();
        quadrantIOverlay.setVisible(false);
        quadrantIIOverlay.setVisible(false);
        quadrantIIIOverlay.setVisible(false);
        quadrantIVOverlay.setVisible(false);
        overlayPane.setVisible(false);
    }

    private void showOnlyQuadrants(int... visibleQuadrants) {
        quadrantIOverlay.setVisible(true);
        quadrantIIOverlay.setVisible(true);
        quadrantIIIOverlay.setVisible(true);
        quadrantIVOverlay.setVisible(true);

        for (int q : visibleQuadrants) {
            switch (q) {
                case 1 -> quadrantIOverlay.setVisible(false);
                case 2 -> quadrantIIOverlay.setVisible(false);
                case 3 -> quadrantIIIOverlay.setVisible(false);
                case 4 -> quadrantIVOverlay.setVisible(false);
            }
        }
    }

    private void updateQuadrantOverlaysForSin() {
        if (currentMode != AngleDisplayMode.SIN || !(currentAppMode == AppMode.UNIT_CIRCLE)) {
            showOnlyQuadrants(1, 2, 3, 4); // show all
            return;
        }

        int angle = currentTargetAngle;
        // Quadrants where x > 0: QI, QIV
        if (angle < 90 || angle > 270) {
            // Button is in QI or QIV → show right side → hide QII & QIII
            showOnlyQuadrants(1, 4);
        } else {
            // Button is in QII or QIII → show left side → hide QI & QIV
            showOnlyQuadrants(2, 3);
        }
    }

    private void updateQuadrantOverlaysForCos() {
        if (currentMode != AngleDisplayMode.COS || !(currentAppMode == AppMode.UNIT_CIRCLE)) {
            showOnlyQuadrants(1, 2, 3, 4);
            return;
        }

        int angle = currentTargetAngle;
        // Quadrants where x > 0: QI, QIV
        if (angle >= 0 && angle <= 180) {
            // Button is in QI or QIV → show right side → hide QII & QIII
            showOnlyQuadrants(1, 2);
        } else {
            // Button is in QII or QIII → show left side → hide QI & QIV
            showOnlyQuadrants(3, 4);
        }
    }

    private void updateQuadrantOverlaysForTan() {
        if (currentMode != AngleDisplayMode.TAN || !(currentAppMode == AppMode.UNIT_CIRCLE)) {
            showOnlyQuadrants(1, 2, 3, 4); // show all
            return;
        }

        int angle = currentTargetAngle;
        // Quadrants where x > 0: QI, QIV
        if (angle < 90 || angle > 270) {
            // Button is in QI or QIV → show right side → hide QII & QIII
            showOnlyQuadrants(1, 4);
        } else {
            // Button is in QII or QIII → show left side → hide QI & QIV
            showOnlyQuadrants(2, 3);
        }
    }

    private void decayMissCounts() {
        for (int angle : ANGLES) {
            missCounts.put(angle, Math.max(0, missCounts.get(angle) - 1));
        }
    }

}
