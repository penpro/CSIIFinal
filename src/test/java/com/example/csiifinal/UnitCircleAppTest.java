
package com.example.csiifinal;

import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class UnitCircleAppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new UnitCircleApp().start(stage); // launch the app
    }



    @Test
    public void testStartup() {
        // this test passes if the app initializes without exception
    }

    @Test
    void testStartGameButtonVisibleAndClickable() {
        // Ensure button is visible and can be clicked
        clickOn("Start Game");
    }

    @Test
    void testChangeFunctionModeToRadians() {
        ComboBox<String> functionCombo = lookup("#functionComboBox").queryComboBox();
        interact(() -> functionCombo.setValue("Radians"));
        verifyThat("#functionComboBox", (ComboBox<String> cb) -> cb.getValue().equals("Radians"));
    }

    @Test
    void testSwitchToUnitCircleModeAndStartGame() {
        ComboBox<String> modeCombo = lookup("#modeComboBox").queryComboBox();
        interact(() -> modeCombo.setValue("Unit Circle"));

        // Now that we're in Unit Circle mode, click start
        clickOn("#startGameButton");
    }

    @Test
    void testClickAngleButton() {
        // You must start the game first to make angle buttons active
        clickOn("#modeComboBox");
        clickOn("Unit Circle");
        clickOn("#startGameButton");

        // now, wait for prompt and click any visible angle
        clickOn("#angleBtn30");
        clickOn("#angleBtn180");
    }

    @Test
    void testOpenHighScores() {
        clickOn("High Scores");
    }

    @Test
    public void testOpenScoreHistory() {
        clickOn("#modeComboBox");
        clickOn("Unit Circle");  // switch to Unit Circle mode
        clickOn("#startGameButton"); // start game to make history button visible

        // now it's visible
        clickOn("#historyButton");
    }
    @Test
    public void testFullModeAngleCoverage() {
        // Step 1: Switch to Unit Circle mode
        clickOn("#modeComboBox");
        clickOn("Unit Circle");

        // Step 2: Start the game
        clickOn("#startGameButton");

        // Step 3: Loop through all function modes
        String[] modes = {"Degrees", "Radians", "sin(x)", "cos(x)", "tan(x)"};
        for (String mode : modes) {
            clickOn("#functionComboBox");
            clickOn(mode);

            // Step 4: Click all angle buttons for this mode
            for (int angle : com.example.csiifinal.UCAHelpers.ANGLES) {
                String buttonId = "#angleBtn" + angle;
                try {
                    clickOn(buttonId);
                } catch (Exception e) {
                    System.err.println("Button not found: " + buttonId);
                }
            }
        }
    }


}