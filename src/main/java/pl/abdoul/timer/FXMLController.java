package pl.abdoul.timer;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import static pl.abdoul.timer.Constants.*;

public class FXMLController {

    private static final boolean IS_WINDOWS = System.getProperty("os.name")
            .toLowerCase()
            .startsWith("windows");

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> task;

    @FXML
    private Slider slider;

    @FXML
    private Label label;

    @FXML
    private Button runButton;

    @FXML
    private Button cancelButton;

    public void initialize() {
        setMode(false);
        slider.valueProperty().addListener(e -> {
            label.setText(formatValue() + "");
        });
    }

    @FXML
    private void run(ActionEvent event) throws InterruptedException, IOException {
        double target = slider.getValue() * 60;
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Poweroff in " + formatValue() + " minutes.");
        alert.setContentText("Are you ok with this?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            setMode(true);
            final AtomicInteger i = new AtomicInteger();
            task = EXECUTOR.scheduleAtFixedRate(() -> {
                var value = i.incrementAndGet();
                Platform.runLater(() -> {
                    label.setText(value/60 + " / " + formatValue());
                });
                if (value > target) {
                    shutdown();
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    @FXML
    private void cancel(ActionEvent event) throws InterruptedException, IOException {
        task.cancel(true);
        setMode(false);
    }

    private void shutdown() {
        ProcessBuilder builder = new ProcessBuilder();
        if (IS_WINDOWS) {
            builder.command(SHUTDOWN_WINDOWS);
        } else {
            builder.command(SHUTDOWN_LINUX);
        }
        builder.directory(new File(System.getProperty(USER_HOME)));
        Process process;
        try {
            process = builder.start();
            int exitCode = process.waitFor();
        } catch (IOException | InterruptedException ex) {
            setMode(false);
            ex.printStackTrace();
        }
    }

    private void setMode(boolean running) {
        label.setText("");
        runButton.setDisable(running);
        cancelButton.setDisable(!running);
    }

    private int formatValue() {
        return (int) Math.round(slider.getValue());
    }
}
