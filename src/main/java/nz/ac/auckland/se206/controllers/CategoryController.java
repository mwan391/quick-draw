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
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.CategorySelect;
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
  @FXML private ComboBox<String> dbxWordDifficulty;
  @FXML private VBox vbxWordDifficulty;
  @FXML private ComboBox<String> dbxAccuracyDifficulty;
  @FXML private VBox vbxTimeDifficulty;
  @FXML private ComboBox<String> dbxTimeDifficulty;
  @FXML private VBox vbxConfidenceDifficulty;
  @FXML private ComboBox<String> dbxConfidenceDifficulty;
  @FXML private VBox vbxAccuracyDifficulty;
  @FXML private Label lblCategory;
  @FXML private Label categoryMessage;

  private GameSettingModel userSetting;

  public void initialize() {
    // add items to difficulty combo boxes
    ObservableList<String> difficultiesAll = FXCollections.observableArrayList();
    Collections.addAll(difficultiesAll, "Easy", "Medium", "Hard", "Master");
    dbxWordDifficulty.setItems(difficultiesAll);
    dbxTimeDifficulty.setItems(difficultiesAll);
    dbxConfidenceDifficulty.setItems(difficultiesAll);
    ObservableList<String> difficultiesAccuracy = FXCollections.observableArrayList();
    Collections.addAll(difficultiesAccuracy, "Easy", "Medium", "Hard");
    dbxAccuracyDifficulty.setItems(difficultiesAccuracy);
    ;
  }

  public void setUserSettings(int settingId) {
    GameSettingDao settingDao = new GameSettingDao();
    userSetting = settingDao.get(settingId);

    // word difficulty since it can be null
    dbxWordDifficulty.setValue("");
    vbxWordDifficulty.setId("null");

    // set presets
    loadWordDifficulty();
    loadAccuracyDifficulty();
  }

  private void loadWordDifficulty() {
    String wordDifficulty = userSetting.getWords().toLowerCase();
    wordDifficulty = wordDifficulty.substring(0, 1).toUpperCase() + wordDifficulty.substring(1);
    if (wordDifficulty.equals("Null")) {
      // Activating text to speech instructions
      TextToSpeech.main(new String[] {"Select a difficulty"});
      // disable start game button
      btnStartGame.setDisable(true);

      // hide message before it has been set
      categoryMessage.setVisible(false);
    } else {
      dbxWordDifficulty.setValue(wordDifficulty);
      onSetWordDifficulty();
    }
  }

  @FXML
  private void onSetWordDifficulty() {
    // get difficulty and check if it is valid
    String wordDifficulty = dbxWordDifficulty.getValue().toUpperCase();

    if (wordDifficulty.equals("")) {
      return;
    }

    // set difficulty in manager
    CategorySelect.Difficulty wordDifficultyEnum =
        CategorySelect.Difficulty.valueOf(wordDifficulty);
    CategorySelect.setWordDifficulty(wordDifficultyEnum);

    // update game model
    userSetting.setWords(wordDifficulty);

    // update box graphics
    vbxWordDifficulty.setId(wordDifficulty.toLowerCase());

    // generate set word
    categoryMessage.setVisible(true);
    btnStartGame.setDisable(false);
    lblCategory.setText("\"" + CategorySelect.generateSetCategory() + "\"");

    // use tts on background thread to avoid lags
    TextToSpeech.main(new String[] {"Your word is " + CategorySelect.getCategory()});
  }

  private void loadAccuracyDifficulty() {
    String accuracyDifficulty = userSetting.getAccuracy().toLowerCase();
    accuracyDifficulty =
        accuracyDifficulty.substring(0, 1).toUpperCase() + accuracyDifficulty.substring(1);
    dbxAccuracyDifficulty.setValue(accuracyDifficulty);
    onSetAccuracyDifficulty();
  }

  @FXML
  private void onSetAccuracyDifficulty() {
    // get difficulty and check if it is valid
    String accuracyDifficulty = dbxAccuracyDifficulty.getValue().toUpperCase();

    // set difficulty in manager
    CategorySelect.Difficulty accuracyDifficultyEnum =
        CategorySelect.Difficulty.valueOf(accuracyDifficulty);
    CategorySelect.setWordDifficulty(accuracyDifficultyEnum);

    // update game model
    userSetting.setAccuracy(accuracyDifficulty);

    // update box graphics
    vbxAccuracyDifficulty.setId(accuracyDifficulty.toLowerCase());
  }

  @FXML
  private void onStartGame(ActionEvent event) throws SQLException {

    // get the parent and controller of the canvas game page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent canvasRoot = SceneManager.getUiRoot(AppUi.CANVAS);
    CanvasController canvasController = (CanvasController) SceneManager.getController(canvasRoot);

    // use tts on background thread to avoid lags
    TextToSpeech.main(new String[] {"Let's draw!"});

    // update settings in database
    GameSettingDao settingDao = new GameSettingDao();
    settingDao.update(userSetting);

    // change the scene and start the game
    canvasController.startTimer();
    scene.setRoot(canvasRoot);

    // reset the page in case a new game gets started
    resetPage();
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
    categoryMessage.setVisible(false);
    dbxWordDifficulty.setValue("");

    // change container colour to neutral
    vbxWordDifficulty.setId("null");
  }
}
