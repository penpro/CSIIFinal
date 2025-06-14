package com.example.csiifinal;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class UnitCircleAppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new UnitCircleApp().start(stage); // launch the app
    }

    @Test
    public void testStartup() {
        // this test passes if the app initializes without exception
    }
}