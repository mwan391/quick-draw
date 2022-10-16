package nz.ac.auckland.se206.controllers;

import animatefx.animation.Pulse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class MenuController implements Controller {

  @FXML private Button btnStart;

  @FXML private ImageView image;

  /** This method sets up the pulse animation for the Quick Draw pencil logo */
  public void initialize() {
    // Animate the Quick Draw pencil icon
    Timeline time = new Timeline(new KeyFrame(Duration.millis(2000), e -> new Pulse(image).play()));
    time.setCycleCount(Timeline.INDEFINITE);
    time.play();
  }

  /**
   * This method takes the user to the sign in screen and plays appropriate sound effect + tts
   *
   * @param event that triggers this method call
   */
  @FXML
  private void onStartGame(ActionEvent event) {
    // get scene, root and controller
    Scene scene = ((Button) event.getSource()).getScene();
    Parent root = SceneManager.getUiRoot(AppUi.LOG_IN);
    LogInController controller = (LogInController) SceneManager.getController(root);

    // initialise login screen and change scene
    controller.loadUserData();
    scene.setRoot(root);

    // play sound effects + tts
    SoundManager.playSound(SoundName.START_GAME);
    TextToSpeech.main(new String[] {"Welcome to Quick Draw!"});
  }
}
