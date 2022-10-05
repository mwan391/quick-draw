package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.models.GameSettingModel;

public class SettingsController implements Controller {

  @FXML private Button btnCancel;
  @FXML private Button btnSave;
  // Accuracy buttons
  @FXML private Button btnAccuracyEasy;
  @FXML private Button btnAccuracyMedium;
  @FXML private Button btnAccuracyHard;
  // Time buttons
  @FXML private Button btnTimeEasy;
  @FXML private Button btnTimeMedium;
  @FXML private Button btnTimeHard;
  @FXML private Button btnTimeMaster;
  // confidence buttons
  @FXML private Button btnConfidenceEasy;
  @FXML private Button btnConfidenceMedium;
  @FXML private Button btnConfidenceHard;
  @FXML private Button btnConfidenceMaster;

  // saved settings model and updated settings
  private GameSettingModel userSetting;
  private String accuracy;
  private String time;
  private String confidence;

  public void loadPage(GameSettingModel model) {
    userSetting = model;
    accuracy = model.getAccuracy();
    time = model.getTime();
    confidence = model.getConfidence();

    // update page buttons and labels
    loadAccuracy();
  }

  @FXML
  private void onSetAccuracy(ActionEvent event) {
    // enable all accuracy buttons
    btnAccuracyEasy.setDisable(false);
    btnAccuracyMedium.setDisable(false);
    btnAccuracyHard.setDisable(false);

    // get button that triggered event
    Button button = (Button) event.getSource();
    // set difficulty according to button
    accuracy = button.getText().toUpperCase();
    // disable correct button
    loadAccuracy();
  }

  private void loadAccuracy() {
    // disable relevant button
    switch (accuracy) {
      case "EASY":
        btnAccuracyEasy.setDisable(true);
        break;
      case "MEDIUM":
        btnAccuracyMedium.setDisable(true);
        break;
      case "HARD":
        btnAccuracyHard.setDisable(true);
        break;
      default:
        // do nothing
        break;
    }
  }

  @FXML
  private void onCancel(ActionEvent event) {
    // do nothing except run the scene change procedure
    changeScene(event);
  }

  @FXML
  private void onSave(ActionEvent event) {
    // update settings model
    userSetting.setAccuracy(accuracy);
    userSetting.setTime(time);
    userSetting.setConfidence(confidence);

    // update settings in database
    GameSettingDao settingDao = new GameSettingDao();
    settingDao.update(userSetting);

    // run scene change procedure
    changeScene(event);
  }

  private void changeScene(ActionEvent event) {
    // change the scene
    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));
  }
}
