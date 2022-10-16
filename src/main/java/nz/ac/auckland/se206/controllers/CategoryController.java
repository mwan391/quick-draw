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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.models.GameSettingModel;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CategoryController implements Controller {

  @FXML private Button btnStartGame;
  @FXML private Button btnLogOut;
  @FXML private Button btnUserStatistics;
  @FXML private ComboBox<String> dbxWordDifficulty;
  @FXML private VBox vbxWordDifficulty;
  @FXML private ComboBox<String> dbxAccuracyDifficulty;
  @FXML private VBox vbxTimeDifficulty;
  @FXML private ComboBox<String> dbxTimeDifficulty;
  @FXML private VBox vbxConfidenceDifficulty;
  @FXML private ComboBox<String> dbxConfidenceDifficulty;
  @FXML private VBox vbxAccuracyDifficulty;
  @FXML private CheckBox hiddenMode;
  @FXML private CheckBox zenMode;

  private GameSettingModel userSetting;

  /**
   * This method sets up necessary variables and dependencies that only need to be done once during
   * the initial fxml load
   */
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
  }

  /**
   * This method updates the category screen to the preset settings as saved from the user's last
   * session, or all on easy if new user, as well as playing the necessary sounds
   *
   * @param userId ID of the user playing
   */
  public void setUserSettings(String userId) {
    GameSettingDao settingDao = new GameSettingDao();
    userSetting = settingDao.get(userId);

    // Activating text to speech instructions
    TextToSpeech.main(new String[] {"Choose a difficulty"});

    SoundManager.playSound(SoundName.LOG_IN);

    // set presets
    loadAllDifficulty();
  }

  /**
   * updates the saved word difficulty setting according to what the user selects. this will not be
   * saved unless the user chooses to move onto the game scene
   *
   * @param event that triggers this method call
   */
  @FXML
  private void onSetWordDifficulty(ActionEvent event) {
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

    // play sound only if an event triggered it
    if (event != null) {
      SoundManager.playSound();
    }
  }

  /** This method loads loads all of the saved settings for each individual setting */
  private void loadAllDifficulty() {
    // word
    String wordDifficulty = userSetting.getWords().toLowerCase();
    wordDifficulty = wordDifficulty.substring(0, 1).toUpperCase() + wordDifficulty.substring(1);
    dbxWordDifficulty.setValue(wordDifficulty);
    onSetWordDifficulty(null);

    // accuracy
    String accuracyDifficulty = userSetting.getAccuracy().toLowerCase();
    accuracyDifficulty =
        accuracyDifficulty.substring(0, 1).toUpperCase() + accuracyDifficulty.substring(1);
    dbxAccuracyDifficulty.setValue(accuracyDifficulty);
    onSetAccuracyDifficulty(null);

    // time
    String timeDifficulty = userSetting.getTime().toLowerCase();
    timeDifficulty = timeDifficulty.substring(0, 1).toUpperCase() + timeDifficulty.substring(1);
    dbxTimeDifficulty.setValue(timeDifficulty);
    onSetTimeDifficulty(null);

    // confidence
    String confidenceDifficulty = userSetting.getConfidence().toLowerCase();
    confidenceDifficulty =
        confidenceDifficulty.substring(0, 1).toUpperCase() + confidenceDifficulty.substring(1);
    dbxConfidenceDifficulty.setValue(confidenceDifficulty);
    onSetConfidenceDifficulty(null);
  }

  /**
   * updates the saved accuracy difficulty setting according to what the user. this will not be
   * saved unless the user chooses to move onto the game scene selects
   *
   * @param event that triggers this method call
   */
  @FXML
  private void onSetAccuracyDifficulty(ActionEvent event) {
    // get difficulty and check if it is valid
    String accuracyDifficulty = dbxAccuracyDifficulty.getValue().toUpperCase();

    // update game model
    userSetting.setAccuracy(accuracyDifficulty);

    // update box graphics
    vbxAccuracyDifficulty.setId(accuracyDifficulty.toLowerCase());

    // play sound only if an event triggered it
    if (event != null) {
      SoundManager.playSound();
    }
  }

  /**
   * updates the saved time difficulty setting according to what the user selects. this will not be
   * saved unless the user chooses to move onto the game scene
   *
   * @param event that triggers this method call
   */
  @FXML
  private void onSetTimeDifficulty(ActionEvent event) {
    // get difficulty and check if it is valid
    String difficulty = dbxTimeDifficulty.getValue().toUpperCase();

    // update game model
    userSetting.setTime(difficulty);

    // update box graphics
    vbxTimeDifficulty.setId(difficulty.toLowerCase());

    // play sound only if an event triggered it
    if (event != null) {
      SoundManager.playSound();
    }
  }

  /**
   * updates the saved confidence difficulty setting according to what the user selects. this will
   * not be saved unless the user chooses to move onto the game scene
   *
   * @param event that triggers this method call
   */
  @FXML
  private void onSetConfidenceDifficulty(ActionEvent event) {
    // get difficulty and check if it is valid
    String difficulty = dbxConfidenceDifficulty.getValue().toUpperCase();

    // update game model
    userSetting.setConfidence(difficulty);

    // update box graphics
    vbxConfidenceDifficulty.setId(difficulty.toLowerCase());

    // play sound only if an event triggered it
    if (event != null) {
      SoundManager.playSound();
    }
  }

  /**
   * This method updates the saved settings and changes the scene to the game scene
   *
   * @param event
   * @throws SQLException
   */
  @FXML
  private void onStartGame(ActionEvent event) throws SQLException {

    // get the parent and controller of the canvas game page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent canvasRoot = SceneManager.getUiRoot(AppUi.CANVAS);
    CanvasController canvasController = (CanvasController) SceneManager.getController(canvasRoot);

    // enabling or disabling hidden mode
    if (hiddenMode.isSelected()) {
      canvasController.isHidden = true;
    } else {
      canvasController.isHidden = false;
    }
    // enabling or disabling zen mode
    if (zenMode.isSelected()) {
      canvasController.isZen = true;
    } else {
      canvasController.isZen = false;
    }

    // update settings in database
    GameSettingDao settingDao = new GameSettingDao();
    settingDao.update(userSetting);

    // change the scene and start the game
    canvasController.initializeGame();
    scene.setRoot(canvasRoot);

    SoundManager.playSound(SoundName.START_GAME);
  }

  /**
   * This method logs the current user out by taking the user back to the log in scene and
   * deactivating the saved user
   *
   * @param event
   */
  @FXML
  private void onLogOut(ActionEvent event) {
    // finding the scene
    Scene scene = ((Button) event.getSource()).getScene();
    Parent logInRoot = SceneManager.getUiRoot(AppUi.LOG_IN);
    scene.setRoot(logInRoot);
    UserModel.setActiveUser(null);

    // updating the userlist
    LogInController controller = (LogInController) SceneManager.getController(logInRoot);
    controller.loadUserData();

    SoundManager.playSound(SoundName.LOG_OUT);
  }

  /**
   * this method takes the user to the statistics page and calls the method to load the statistics
   * onto the page
   *
   * @param event that calls this method
   */
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

    SoundManager.playSound();
  }

  /**
   * this method takes the user to the Profile settings page and calls the method to load the image
   * and barsonto the page
   *
   * @param event that calls this method
   */
  @FXML
  private void onSeeSettings(ActionEvent event) {
    // get root and controller for settings page
    Scene scene = ((Button) event.getSource()).getScene();
    Parent settingsRoot = SceneManager.getUiRoot(AppUi.PROFILE_SETTINGS);
    ProfileSettingsController settingsController =
        (ProfileSettingsController) SceneManager.getController(settingsRoot);

    // load the necessary stats and change the scene
    scene.setRoot(settingsRoot);
    SoundManager.playSound();
  }

  /**
   * this method plays a sound whenever one of the boxes are checked for either zen mode or hidden
   * word mode
   */
  @FXML
  private void onCheck() {
    SoundManager.playSound();
  }
}
