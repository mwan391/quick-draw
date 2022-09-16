package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CategoryController implements Controller {

  @FXML private Button btnStartGame;
  @FXML private Button btnLogOut;
  @FXML private Button btnEasy;
  @FXML private Button btnMedium;
  @FXML private Button btnHard;
  @FXML private Button btnMaster;
  @FXML private Label lblCategory;
  private TextToSpeech textToSpeech = new TextToSpeech();

  @FXML
  public void initialize() {
    btnMedium.setDisable(true);
    btnHard.setDisable(true);
    btnMaster.setDisable(true);
    btnStartGame.setDisable(true);
  }

  @FXML
  private void onStartGame(ActionEvent event) {

    // get the parent and controller of the canvas game page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent canvasRoot = SceneManager.getUiRoot(AppUi.CANVAS);
    CanvasController canvasController = (CanvasController) SceneManager.getController(canvasRoot);

    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak("Let's draw!");
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    // change the scene and start the game
    canvasController.startTimer();
    scene.setRoot(canvasRoot);

    // reset the page in case a new game gets started
    resetPage();
  }

  @FXML
  private void onGenerateEasyCategory(ActionEvent event) {
    CategorySelect.setWordDifficulty(Difficulty.EASY);
    lblCategory.setText("\"" + CategorySelect.generateCategory(Difficulty.EASY) + "\"");

    // speak
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak("Your category is " + CategorySelect.getCategory());
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    // disable the category button so users cannot reroll
    btnStartGame.setDisable(false);
    btnEasy.setDisable(true);
  }

  @FXML
  private void onLogOut(ActionEvent event) {
    // change the scene
    Scene scene = ((Button) event.getSource()).getScene();
    Parent logInRoot = SceneManager.getUiRoot(AppUi.LOG_IN);
    scene.setRoot(logInRoot);
    UserModel.setActiveUser(null);

    // reset the page in case a new game gets started
    resetPage();
  }

  private void resetPage() {
    // return the page to its initial state.
    lblCategory.setText("[Choose A Difficulty]");
    btnStartGame.setDisable(true);
    btnEasy.setDisable(false);
  }
}
