package nz.ac.auckland.se206.controllers;

import java.sql.SQLException;
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
  @FXML private Button btnUserStatistics;
  @FXML private Button btnEasy;
  @FXML private Button btnMedium;
  @FXML private Button btnHard;
  @FXML private Button btnMaster;
  @FXML private Label lblCategory;
  @FXML private Label categoryMessage;

  @FXML
  public void initialize() {
    btnMedium.setDisable(true);
    btnHard.setDisable(true);
    btnMaster.setDisable(true);
    btnStartGame.setDisable(true);
    categoryMessage.setVisible(false);
  }

  @FXML
  private void onStartGame(ActionEvent event) throws SQLException {

    // get the parent and controller of the canvas game page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent canvasRoot = SceneManager.getUiRoot(AppUi.CANVAS);
    CanvasController canvasController = (CanvasController) SceneManager.getController(canvasRoot);

    // use tts on background thread to avoid lags
    TextToSpeech.main(new String[] {"Let's draw!"});
    ;

    // change the scene and start the game
    canvasController.startTimer();
    scene.setRoot(canvasRoot);

    // reset the page in case a new game gets started
    resetPage();
  }

  @FXML
  private void onGenerateEasyCategory(ActionEvent event) {
    categoryMessage.setVisible(true);
    CategorySelect.setWordDifficulty(Difficulty.EASY);
    lblCategory.setText("\"" + CategorySelect.generateSetCategory() + "\"");

    // use tts on background thread to avoid lags
    TextToSpeech.main(new String[] {"Your word is " + CategorySelect.getCategory()});
    ;

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

  @FXML
  private void onSeeUserStatistics(ActionEvent event) {
    // get root and controller for statistics page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent statsRoot = SceneManager.getUiRoot(AppUi.USER_STATS);
    StatisticsController statisticsController =
        (StatisticsController) SceneManager.getController(statsRoot);

    // load the necessary stats and change the scene
    statisticsController.loadPage();
    scene.setRoot(statsRoot);
  }

  private void resetPage() {
    // return the page to its initial state.
    lblCategory.setText("[Choose A Difficulty]");
    btnStartGame.setDisable(true);
    btnEasy.setDisable(false);
    categoryMessage.setVisible(false);
  }
}
