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

import java.net.URL;
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

    URL correctURL = getClass().getResource("/sounds/correct.wav");
    URL incorrectURL = getClass().getResource("/sounds/incorrect.mp3");

    private final AudioClip correctSound;
    {
        AudioClip temp = null;
        try {
            if (correctURL != null)
                temp = new AudioClip(correctURL.toExternalForm());
        } catch (Exception | Error e) {
            System.err.println("Could not load correct sound: " + e);
        }
        correctSound = temp;
    }
    private final AudioClip incorrectSound = incorrectURL != null ? new AudioClip(incorrectURL.toExternalForm()) : null;

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
    //private final AudioClip correctSound = new AudioClip(getClass().getResource("/sounds/correct.wav").toExternalForm());
    //private final AudioClip incorrectSound = new AudioClip(getClass().getResource("/sounds/incorrect.mp3").toExternalForm());

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
     * JavaFX application entry point. Initializes the UI components, event handlers,
     * and game logic for the Unit Circle educational app.
     *
     * @param primaryStage the primary window for this JavaFX application
     */
    @Override
    public void start(Stage primaryStage) {
        // Set up root container
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #808080;");

        // Create the main unit circle
        circle.setFill(Color.CORNFLOWERBLUE);
        root.getChildren().add(circle);

        // Clip mask for overlays (so overlays only cover the circle)
        Circle clipCircle = new Circle();
        clipCircle.radiusProperty().bind(circle.radiusProperty());
        clipCircle.centerXProperty().bind(circle.centerXProperty());
        clipCircle.centerYProperty().bind(circle.centerYProperty());
        overlayPane.setClip(clipCircle);

        // Calculate quadrant boundaries
        double halfRadius = circle.getRadius();
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        root.getChildren().add(overlayPane);

        // Position quadrant overlays
        quadrantIOverlay.setX(cx);
        quadrantIOverlay.setY(cy - halfRadius);
        quadrantIIOverlay.setX(cx - halfRadius);
        quadrantIIOverlay.setY(cy - halfRadius);
        quadrantIIIOverlay.setX(cx - halfRadius);
        quadrantIIIOverlay.setY(cy);
        quadrantIVOverlay.setX(cx);
        quadrantIVOverlay.setY(cy);

        for (Rectangle r : List.of(quadrantIOverlay, quadrantIIOverlay, quadrantIIIOverlay, quadrantIVOverlay)) {
            r.setWidth(halfRadius);
            r.setHeight(halfRadius);
            r.setFill(Color.rgb(0, 0, 0, 0.4));     // semi-transparent black
            r.setMouseTransparent(true);
            r.setVisible(false);
        }

        overlayPane.getChildren().addAll(quadrantIOverlay, quadrantIIOverlay, quadrantIIIOverlay, quadrantIVOverlay);
        overlayPane.setVisible(false);

        // Create radial lines from origin to each angle
        for (int angle : ANGLES) {
            Line line = new Line();
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1.5);
            lines.add(line);
            root.getChildren().add(line);
        }

        // Score display
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        scoreLabel.setVisible(false);
        root.getChildren().add(scoreLabel);

        // Create interactive angle buttons
        for (int angle : ANGLES) {
            Button btn = new Button(angle + "°");
            btn.setPrefSize(75, 45);
            btn.setFocusTraversable(false);
            btn.setStyle("-fx-font-size: 14px;");
            final int btnAngle = angle;

            btn.setOnAction(e -> {
                if (currentAppMode == AppMode.SPECIAL_TRIANGLES) {
                    SpecialTriangle.showTriangleForAngle(btnAngle);
                    return;
                }

                if (currentAppMode != AppMode.UNIT_CIRCLE) return;

                String expectedLabel = switch (currentMode) {
                    case DEGREES -> btnAngle + "°";
                    case RADIANS -> getPiFraction(btnAngle);
                    case SIN -> "sin: " + SIN_VALUES.getOrDefault(btnAngle, "?");
                    case COS -> "cos: " + COS_VALUES.getOrDefault(btnAngle, "?");
                    case TAN -> "tan: " + TAN_VALUES.getOrDefault(btnAngle, "?");
                };

                if (btnAngle == currentTargetAngle) {
                    updateScore(1);
                    flashCircle(CORRECT_COLOR, true);
                    pickNewTargetAngle();
                    missCounts.put(btnAngle, Math.max(0, missCounts.getOrDefault(btnAngle, 0) - 1));
                } else {
                    updateScore(-1);
                    flashCircle(INCORRECT_COLOR, false);
                    missCounts.put(currentTargetAngle, missCounts.getOrDefault(currentTargetAngle, 0) + 10);
                }
            });

            buttons.add(btn);
            root.getChildren().add(btn);
        }

        // Adjust layout on window resize
        root.widthProperty().addListener((obs, oldVal, newVal) -> updateLayout(root));
        root.heightProperty().addListener((obs, oldVal, newVal) -> updateLayout(root));

        // Initialize main scene
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Unit Circle");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> updateLayout(root)); // Final layout after window shows

        // Add small gray inner circle
        innerCircle.setFill(Color.GRAY);
        root.getChildren().add(innerCircle);

        // UI labels and dropdowns
        promptLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        root.getChildren().add(promptLabel);

        functionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        root.getChildren().add(functionLabel);

        modeSelector.getItems().addAll("Degrees", "Radians", "sin(x)", "cos(x)", "tan(x)");
        modeSelector.setValue("Degrees");
        modeSelector.setOnAction(e -> {
            currentMode = switch (modeSelector.getValue()) {
                case "Degrees" -> AngleDisplayMode.DEGREES;
                case "Radians" -> AngleDisplayMode.RADIANS;
                case "sin(x)" -> AngleDisplayMode.SIN;
                case "cos(x)" -> AngleDisplayMode.COS;
                case "tan(x)" -> AngleDisplayMode.TAN;
                default -> currentMode;
            };
            updateButtonLabels();
            if (currentAppMode == AppMode.UNIT_CIRCLE) pickNewTargetAngle();
        });
        root.getChildren().add(modeSelector);

        modeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        root.getChildren().add(modeLabel);

        modeComboBox.getItems().addAll("Exploration", "Unit Circle", "Special Triangles");
        modeComboBox.setValue("Exploration");
        modeComboBox.setPrefWidth(120);
        modeSelector.setPrefWidth(120);
        modeComboBox.setStyle("-fx-font-size: 14px;");
        modeSelector.setStyle("-fx-font-size: 14px;");
        root.getChildren().add(modeComboBox);

        modeComboBox.setOnAction(e -> {
            currentAppMode = switch (modeComboBox.getValue()) {
                case "Exploration" -> AppMode.EXPLORATION;
                case "Unit Circle" -> AppMode.UNIT_CIRCLE;
                case "Special Triangles" -> AppMode.SPECIAL_TRIANGLES;
                default -> currentAppMode;
            };

            boolean isGame = currentAppMode == AppMode.UNIT_CIRCLE;
            startGameButton.setVisible(isGame);
            endGameButton.setVisible(isGame);
            promptLabel.setVisible(false);
            stopTimer();

            updateButtonLabels();
            updatePromptVisibility();
        });

        startGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20;");
        startGameButton.setFocusTraversable(false);
        startGameButton.setVisible(false);
        startGameButton.setOnAction(e -> {
            startGameButton.setVisible(false);
            promptLabel.setVisible(true);
            startTimer();
            pickNewTargetAngle();
            score = 0;
            updateScore(0);
            scoreLabel.setVisible(true);
            scoreHistoryManager.clear();
            historyButton.setVisible(true);
            overlayPane.setVisible(true);
        });
        root.getChildren().add(startGameButton);

        updatePromptVisibility();
        promptLabel.textProperty().addListener((obs, oldText, newText) -> forcePromptCentering());

        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        timerLabel.setLayoutY(10);
        timerLabel.setVisible(false);
        root.getChildren().add(timerLabel);

        historyButton.setStyle("-fx-font-size: 14px;");
        historyButton.setFocusTraversable(false);
        historyButton.setOnAction(e -> scoreHistoryManager.showPopup());
        historyButton.setVisible(false);
        root.getChildren().add(historyButton);

        endGameButton.setStyle("-fx-font-size: 14px;");
        endGameButton.setFocusTraversable(false);
        endGameButton.setVisible(false);
        endGameButton.setOnAction(e -> resetGame());
        root.getChildren().add(endGameButton);

        highScoresButton.setStyle("-fx-font-size: 14px;");
        highScoresButton.setFocusTraversable(false);
        highScoresButton.setOnAction(e -> highScoreManager.showHighScoresPopup());
        highScoresButton.setVisible(true);
        root.getChildren().add(highScoresButton);

        // Initialize missCounts for weighted learning
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
        if (isCorrect && correctSound != null) {
            correctSound.play();
        } else if (!isCorrect && incorrectSound != null) {
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

    /**
     * Starts a countdown timer for the game session.
     * <p>
     * Initializes the timer with 120 seconds and updates the timer label to display the
     * initial time. The method creates a {@code Timeline} that decrements the remaining
     * time every second and updates the display accordingly. When the timer reaches zero,
     * it stops the countdown, displays a final message with the score, and attempts to
     * save the high score using the current game mode.
     */
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
                Platform.runLater(() -> highScoreManager.trySaveScore(score, currentMode.name()));
            }
        }));
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    /**
     * Stops the countdown timer if it is running and hides related UI elements.
     * <p>
     * This method halts the ongoing timer by calling {@code stop()} on the
     * {@code countdownTimer}, if it is not {@code null}. It also hides the timer label,
     * score label, and history button to reflect the paused or ended game state.
     */
    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        timerLabel.setVisible(false);
        scoreLabel.setVisible(false);
        historyButton.setVisible(false);
    }

    /**
     * Updates the timer label with the current time remaining in minutes and seconds,
     * and centers the label horizontally within the scene.
     * <p>
     * The label is formatted as "Time Left: M:SS", and repositioned based on the current
     * scene width to ensure it's horizontally centered.
     * This method should be called whenever {@code secondsRemaining} changes.
     */
    private void updateTimerLabel() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("Time Left: %d:%02d", minutes, seconds));
        // Center horizontally
        if (timerLabel.getScene() != null) {
            timerLabel.setLayoutX((timerLabel.getScene().getWidth() - timerLabel.getWidth()) / 2);
        }
    }

    /**
     * Updates the current game score by a specified delta and reflects the change in the UI.
     * <p>
     * This method increments (or decrements) the score by the given {@code delta}, updates the score label
     * displayed in the user interface, and logs the result in the score history manager.
     *
     * @param delta the amount to add to the current score (can be negative to represent a penalty)
     */
    private void updateScore(int delta) {
        score += delta;
        scoreLabel.setText("Score: " + score);

        scoreHistoryManager.record(delta > 0, score);
    }

    /**
     * Resets the game state to its initial configuration.
     * <p>
     * This method is typically called when the game ends or restarts. It performs the following actions:
     * <ul>
     *   <li>Saves the current score using the {@code HighScoreManager}, tagged by the current mode.</li>
     *   <li>Stops the game timer if it's running.</li>
     *   <li>Resets the current score and updates the score display.</li>
     *   <li>Hides all game-related UI elements such as the score label, prompt, and control buttons.</li>
     *   <li>Clears the score history.</li>
     *   <li>Hides all quadrant overlays and the overlay pane.</li>
     * </ul>
     * This ensures the UI and internal state are ready for a new game session.
     */
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

    /**
     * Shows only the specified quadrants by hiding the overlays for all quadrants first,
     * then making the overlays for the specified quadrants invisible.
     * <p>
     * This is used to visually restrict the view to certain quadrants in the unit circle,
     * often to help users deduce sine, cosine, or tangent values based on quadrant behavior.
     * <p>
     * Quadrant numbering follows standard mathematical convention:
     * <ul>
     *   <li>1 → Quadrant I</li>
     *   <li>2 → Quadrant II</li>
     *   <li>3 → Quadrant III</li>
     *   <li>4 → Quadrant IV</li>
     * </ul>
     *
     * @param visibleQuadrants The quadrant numbers (1–4) to make visible. All other overlays remain shown.
     *                         Internally, these quadrants will have their overlay visibility set to {@code false},
     *                         revealing the quadrant content.
     */
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

    /**
     * Updates the quadrant overlays for the sine display mode in the Unit Circle game.
     * <p>
     * When the app is in {@link AppMode#UNIT_CIRCLE} and {@link AngleDisplayMode#SIN} mode,
     * this method restricts which quadrants are visually shown to help the user
     * deduce the correct angle for a given sine value (y-coordinate on the unit circle).
     * <p>
     * Since sine is positive in Quadrants I and II, and negative in Quadrants III and IV,
     * this method determines the correct hemisphere to display based on the target angle:
     * <ul>
     *   <li>If the angle is in QI or QIV (angle &lt; 90 or &gt; 270), show the right half (QI and QIV).</li>
     *   <li>If the angle is in QII or QIII (angle ≥ 90 and ≤ 270), show the left half (QII and QIII).</li>
     * </ul>
     * If the app is not in the correct mode, all quadrants are shown.
     */
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

    /**
     * Updates the quadrant overlays based on the current angle when in COS mode.
     * <p>
     * In the Unit Circle game mode with the display set to COS, this method determines
     * whether to show the left or right half of the circle to visually restrict
     * user choices based on the cosine value.
     * <p>
     * Cosine (x-coordinate on the unit circle) is positive in Quadrants I and IV,
     * and negative in Quadrants II and III. This method uses that rule to determine
     * which quadrants to display:
     * <ul>
     *   <li>If the angle is between 0° and 180° inclusive, it shows Quadrants I and II (right side).</li>
     *   <li>Otherwise, it shows Quadrants III and IV (left side).</li>
     * </ul>
     * If the mode is not COS or not in UNIT_CIRCLE app mode, all four quadrants are shown.
     */
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

    /**
     * Updates the quadrant overlays on the unit circle visualization when in TAN mode.
     * <p>
     * This method ensures that only the relevant quadrants are visible based on the target angle.
     * Tangent is undefined when {@code cos(θ) = 0}, and is only valid where cosine is non-zero.
     * To help disambiguate which angle is correct when two angles share the same tangent value,
     * this method restricts quadrant visibility:
     * <ul>
     *     <li>If the angle is in QI or QIV (i.e., angle &lt; 90 or angle &gt; 270), only quadrants 1 and 4 are shown.</li>
     *     <li>If the angle is in QII or QIII (i.e., 90 ≤ angle ≤ 270), only quadrants 2 and 3 are shown.</li>
     *     <li>If the app is not in {@code AppMode.UNIT_CIRCLE} or the angle display mode is not {@code TAN}, all quadrants are shown.</li>
     * </ul>
     */
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

    /**
     * Applies a decay to the miss counts for all tracked angles.
     * <p>
     * This method reduces the recorded number of misses for each angle by 1,
     * down to a minimum of 0. It ensures that previously missed angles gradually
     * become less weighted over time, assuming they are answered correctly later.
     * This helps balance the angle selection process so that users are not indefinitely
     * penalized for earlier mistakes.
     * <p>
     * The list of angles affected is defined in the {@code ANGLES} collection,
     * and their associated miss counts are stored in the {@code missCounts} map.
     */
    private void decayMissCounts() {
        for (int angle : ANGLES) {
            missCounts.put(angle, Math.max(0, missCounts.get(angle) - 1));
        }
    }

}
