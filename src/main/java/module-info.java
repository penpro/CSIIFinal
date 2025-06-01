module com.example.csiifinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.example.csiifinal to javafx.fxml;
    exports com.example.csiifinal;
}