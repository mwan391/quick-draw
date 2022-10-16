package nz.ac.auckland.se206.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;

public class ProfileSettingsController implements Controller {

  @FXML private ComboBox<String> picChooser;
  @FXML private ImageView picPreview;

  /** This method loads default values and images upon scene load in the UI */
  public void initialize() {

    // Loading options for profile picture
    String picStrings[] = {"boy", "dad", "girl", "mother", "woman"};
    ObservableList<String> picNames = FXCollections.observableArrayList(picStrings);
    picChooser.setItems(picNames);
  }

  /**
   * This method changes the scene back to the category select and plays the sound
   *
   * @param event, the button press used to determine current scene
   */
  @FXML
  private void onSeeMenu(ActionEvent event) {
    // get root
    Scene scene = ((Button) event.getSource()).getScene();
    Parent categoryRoot = SceneManager.getUiRoot(AppUi.CATEGORY_SELECT);
    // change scene
    SoundManager.playSound();
    scene.setRoot(categoryRoot);
  }

  @FXML
  private void onChangePicture() {
    Image preview =
        new Image(
            getClass()
                .getResourceAsStream("/images/profileicons/" + picChooser.getValue() + ".png"));
    // Changing the preview to the new selection
    picPreview.setImage(preview);
    SoundManager.playSound();
  }
}
