module timer {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.openjfx to javafx.fxml;
    exports pl.abdoul.timer;
}
