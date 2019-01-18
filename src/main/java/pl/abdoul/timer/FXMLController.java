package pl.abdoul.timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FXMLController {

    @FXML
    private Button button;
    
    @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) throws InterruptedException, IOException {
        button.setDisable(true);
        final AtomicInteger i = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            var v = i.incrementAndGet();

            Platform.runLater(
                    () -> {
                        label.setText(v + "");
                    }
            );

        }, 0, 1, TimeUnit.SECONDS);

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", "dir");
        } else {
            builder.command("sh", "-c", "touch file");
        }
        builder.directory(new File(System.getProperty("user.home")));
        //Process process = builder.start();
        //int exitCode = process.waitFor();

    }

    public void initialize() {
        // TODO
    }
}
