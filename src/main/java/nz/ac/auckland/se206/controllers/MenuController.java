package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class MenuController implements Controller {

  @FXML private Button btnStart;

  /**
   * This method takes the user to the sign in screen and plays appropriate sound effect + tts
   *
   * @param event that triggers this method call
   */
  @FXML
  private void onStartGame(ActionEvent event) {
    // change the scene
    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.LOG_IN));

    SoundManager.playSound(SoundName.START_GAME);

    // run the text to speech on a background thread to avoid lags
    TextToSpeech.main(new String[] {"Welcome to Quick Draw!"});
  }
}
