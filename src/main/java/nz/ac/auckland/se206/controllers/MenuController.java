package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class MenuController implements Controller {

  @FXML private Button btnStart;

  @FXML
  private void onStartGame(ActionEvent event) {
    // change the scene
    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.LOG_IN));

    // run the text to speech on a background thread to avoid lags
    TextToSpeech textToSpeech = new TextToSpeech();
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak("Welcome to Quick Draw.");
            return null;
          }
        };

    // start the thread
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
