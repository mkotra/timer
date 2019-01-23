package pl.abdoul.timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

public class FXMLController {

    private static final boolean IS_WINDOWS = System.getProperty("os.name")
            .toLowerCase()
            .startsWith("windows");

    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    private Slider slider;
    
    public void initialize() {
        slider.valueProperty().addListener(e -> {
              label.setText(formatValue() + "");
        });
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws InterruptedException, IOException {
        button.setDisable(true);
        double target = slider.getValue() * 60;
        final AtomicInteger i = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduleAtFixedRate = executor.scheduleAtFixedRate(() -> {
            var value = i.incrementAndGet();
            Platform.runLater(() -> {
                label.setText(value + " / " + formatValue());
            });
            if (value > target) {
                shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    public int formatValue() {
        return (int) Math.round(slider.getValue());
    }

    public void shutdown() {
        ProcessBuilder builder = new ProcessBuilder();
        if (IS_WINDOWS) {
            builder.command("cmd.exe", "/c", "shutdown");
        } else {
            builder.command("sh", "-c", "poweroff");
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process;
        try {
            process = builder.start();
            int exitCode = process.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
