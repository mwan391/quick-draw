package nz.ac.auckland.se206.controllers;

import java.sql.SQLException;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.models.GameSettingModel;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CategoryController implements Controller {

  @FXML private Button btnStartGame;
  @FXML private Button btnLogOut;
  @FXML private Button btnUserStatistics;
  @FXML private Button btnSettings;
  @FXML private Button btnEasy;
  @FXML private Button btnMedium;
  @FXML private Button btnHard;
  @FXML private Button btnMaster;
  @FXML private ComboBox<String> dbxWordDifficulty;
  @FXML private Label lblCategory;
  @FXML private Label categoryMessage;

  private GameSettingModel userSetting;

  public void initialize() {
    // add items to difficulty combo boxes
    ObservableList<String> difficultiesAll = FXCollections.observableArrayList();
    Collections.addAll(difficultiesAll, "Easy", "Medium", "Hard", "Master");
    dbxWordDifficulty.setItems(difficultiesAll);
    ;
  }

  public void setUserSettings(int settingId) {
    GameSettingDao settingDao = new GameSettingDao();
    userSetting = settingDao.get(settingId);

    // set presets

    // set word difficulty
    String wordDifficulty = userSetting.getWords();
    switch (wordDifficulty) {
      case "EASY":
        onGenerateEasyCategory();
        break;
      case "MEDIUM":
        onGenerateMediumCategory();
        break;
      case "HARD":
        onGenerateHardCategory();
        break;
      case "MASTER":
        onGenerateMasterCategory();
        break;
      default:
        // Activating text to speech instructions
        TextToSpeech.main(new String[] {"Select a difficulty"});
        // disable start game button
        btnStartGame.setDisable(true);
        // hide message before it has been set
        categoryMessage.setVisible(false);
        break;
    }
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

    // update settings in database
    GameSettingDao settingDao = new GameSettingDao();
    settingDao.update(userSetting);

    // change the scene and start the game
    canvasController.startTimer();
    scene.setRoot(canvasRoot);

    // reset the page in case a new game gets started
    resetPage();
  }

  private void generateCategory(CategorySelect.Difficulty wordDifficulty) {
    // re-enable all buttons
    btnStartGame.setDisable(false);
    btnEasy.setDisable(false);
    btnMedium.setDisable(false);
    btnHard.setDisable(false);
    btnMaster.setDisable(false);

    // generate set word
    categoryMessage.setVisible(true);
    CategorySelect.setWordDifficulty(wordDifficulty);
    lblCategory.setText("\"" + CategorySelect.generateSetCategory() + "\"");

    // use tts on background thread to avoid lags
    TextToSpeech.main(new String[] {"Your word is " + CategorySelect.getCategory()});
  }

  @FXML
  private void onGenerateEasyCategory() {
    // set categories
    generateCategory(Difficulty.EASY);

    // update game model
    userSetting.setWords("EASY");

    // disable the category button so users cannot reroll
    btnEasy.setDisable(true);
  }

  @FXML
  private void onGenerateMediumCategory() {
    // set categories
    generateCategory(Difficulty.MEDIUM);

    // update game model
    userSetting.setWords("MEDIUM");

    // disable the category button so users cannot reroll
    btnMedium.setDisable(true);
  }

  @FXML
  private void onGenerateHardCategory() {
    // set categories
    generateCategory(Difficulty.HARD);

    // update game model
    userSetting.setWords("HARD");

    // disable the category button so users cannot reroll
    btnHard.setDisable(true);
  }

  @FXML
  private void onGenerateMasterCategory() {
    // set categories
    generateCategory(Difficulty.MASTER);

    // update game model
    userSetting.setWords("MASTER");

    // disable the category button so users cannot reroll
    btnMaster.setDisable(true);
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

  @FXML
  private void onSeeSettings(ActionEvent event) {
    // get root and controller for settings page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent settingsRoot = SceneManager.getUiRoot(AppUi.SETTINGS);
    SettingsController settingsController =
        (SettingsController) SceneManager.getController(settingsRoot);

    // load the necessary settings and change the scene
    settingsController.loadPage(userSetting);
    scene.setRoot(settingsRoot);
  }

  private void resetPage() {
    // return the page to its initial state.
    lblCategory.setText("Choose A Difficulty:");
    btnStartGame.setDisable(true);
    btnEasy.setDisable(false);
    categoryMessage.setVisible(false);
  }
}
