package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class LogInController implements Controller {

  @FXML private Button btnSignUp;
  @FXML private Button btnLogIn;
  @FXML private TextField fldUserName;
  @FXML private PasswordField fldPassword;

  @FXML
  private void onSignUp(ActionEvent event) {
    // go to the next screen
    nextScreen(event);
  }

  @FXML
  private void onLogIn(ActionEvent event) {
    // go to the next screen
    nextScreen(event);
  }

  private void nextScreen(ActionEvent event) {

    // change the scene
    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));

    // run the text to speech on a background thread to avoid lags
    TextToSpeech textToSpeech = new TextToSpeech();
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak("Select a category.");
            return null;
          }
        };

    // start the thread
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
