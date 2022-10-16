package nz.ac.auckland.se206.controllers;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;

public class ProfileSettingsController implements Controller {

  @FXML private ComboBox<String> picChooser;
  @FXML private ImageView picPreview;

  private UserModel activeUser;

  /** This method loads default values and images upon scene load in the UI */
  public void initialize() {

    // Loading options for profile picture
    String picStrings[] = {"boy", "dad", "girl", "mother", "woman"};
    ObservableList<String> picNames = FXCollections.observableArrayList(picStrings);
    picChooser.setItems(picNames);
  }

  /** This method loads the user's profile pic and progress bars into the scene */
  public void loadProfileInfo() {

    // get user image and set it
    activeUser = UserModel.getActiveUser();
    picChooser.setValue(activeUser.getIcon());
    onChangePicture();
  }

  /**
   * This method changes the scene back to the category select and saves the selected user picture
   *
   * @param event, the button press used to determine current scene
   */
  @FXML
  private void onSeeMenu(ActionEvent event) {
    // get root
    Scene scene = ((Button) event.getSource()).getScene();
    Parent categoryRoot = SceneManager.getUiRoot(AppUi.CATEGORY_SELECT);

    // save image to database
    UserDaoJson userDao = new UserDaoJson();
    userDao.updateAvatar(activeUser, picChooser.getValue());

    // change scene
    SoundManager.playSound();
    scene.setRoot(categoryRoot);
  }

  /** this method updates the image shown on screen to match the choice in the choice box */
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

  /**
   * This method will show a warning pop up. clicking delete will delete everything and take the
   * user back to the first screen, while clicking cancel will do nothing.
   */
  @FXML
  private void onDeleteAccount() {

    // create popup
    Dialog<Void> badgePopup = new Dialog<>();

    // write main dialog
    String str =
        "WARNING!\nYou will lose all progress, and you cannot undo this.\n\nAre you sure you want to delete everything?";
    badgePopup.setContentText(str);
    // add buttons
    ButtonType btnDelete = new ButtonType("Delete Account", ButtonData.OK_DONE);
    badgePopup.getDialogPane().getButtonTypes().add(btnDelete);
    ButtonType btnCancel = new ButtonType("Go Back", ButtonData.CANCEL_CLOSE);
    badgePopup.getDialogPane().getButtonTypes().add(btnCancel);

    // change the top title
    badgePopup.setTitle("WARNING!");
    // set size of dialog and buttons
    DialogPane popupPane = badgePopup.getDialogPane();
    popupPane.setPrefSize(550, 200);
    popupPane.getButtonTypes().stream()
        .map(popupPane::lookupButton)
        .forEach(btn -> ButtonBar.setButtonUniformSize(btn, false));
    // set css formatting for pane and buttons
    popupPane.getStylesheets().add("/css/style.css");

    // show badge
    SoundManager.playSound(SoundName.LOSE_GAME);
    Optional<Void> result = badgePopup.showAndWait();
  }
}
