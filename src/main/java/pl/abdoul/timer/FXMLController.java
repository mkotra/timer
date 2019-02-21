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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import static pl.abdoul.timer.Constants.*;

public class FXMLController {

    private static final boolean IS_WINDOWS = System.getProperty("os.name")
            .toLowerCase()
            .contains("win");
    
    private static final boolean IS_MAC = System.getProperty("os.name")
            .toLowerCase()
            .contains("mac");

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> task;

    private double target;
    
    private double value;
    
    @FXML
    private Slider slider;

    @FXML
    ProgressBar progressBar;
    
    @FXML
    private Label labelTarget;
   
    @FXML
    private Label labelRemaining;
    
    @FXML
    private Button runButton;

    @FXML
    private Button cancelButton;

    public void initialize() {
        slider.setValue(30);
        slider.valueProperty().addListener(e -> {
            labelTarget.setText("Target: " +  formatValue() + " min");
        });
        setMode(false);
    }

    @FXML
    private void run(ActionEvent event) throws InterruptedException, IOException {
        target = slider.getValue() * 60;
        if (confirm()) {
            setMode(true);
            final AtomicInteger i = new AtomicInteger();
            task = EXECUTOR.scheduleAtFixedRate(() -> {
                value = i.incrementAndGet();
                Platform.runLater(() -> {
                    double remaining = calculateRemaining();
                    double progress = value / target;
                    this.labelRemaining.setText(String.format("Remaining: %.2f min", remaining));
                    this.progressBar.setProgress(progress);
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

    private boolean confirm() {
        if (target < 1) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Poweroff in " + formatValue() + " minutes");
            alert.setContentText("Are you ok with this?");
            Optional<ButtonType> result = alert.showAndWait();
            return result.get() == ButtonType.OK;
        } else {
            return true;
        }
    }
    
    private void shutdown() {
        ProcessBuilder builder = new ProcessBuilder();
        if (IS_WINDOWS) {
            builder.command(SHUTDOWN_WINDOWS);    
        } else if (IS_MAC) {
            builder.command(SHUTDOWN_MAC);
        } else {
            builder.command(SHUTDOWN_LINUX);
        }
        builder.directory(new File(System.getProperty(USER_HOME)));
        Process process;
        try {
            process = builder.start();
            process.waitFor();
        } catch (IOException | InterruptedException ex) {
            setMode(false);
            handleException(ex);
        }
    }

    private void setMode(boolean running) {
        if(!running) {
            progressBar.setProgress(0);
        }
        labelTarget.setText("Target: " +  formatValue() + " min");
        labelRemaining.setText("");
        slider.setDisable(running);
        runButton.setDisable(running);
        cancelButton.setDisable(!running);
    }

    private int formatValue() {
        return (int) Math.round(slider.getValue());
    }
    
    private double calculateRemaining() {
        return (target - value) / 60;
    }
    
    private void handleException(Exception ex) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong:");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}
