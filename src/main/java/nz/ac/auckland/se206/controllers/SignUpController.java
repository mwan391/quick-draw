package nz.ac.auckland.se206.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.util.ComboBoxRender;

public class SignUpController implements Controller {

  @FXML private TextField userEntry;
  @FXML private Label lblWarning;
  @FXML private ComboBox<String> picChooser;
  @FXML private ImageView picPreview;

  /** This method loads default values and images upon scene load in the UI */
  public void initialize() {

    // Increase default size of font in combo box to 18px
    ComboBoxRender.increaseFontSize(picChooser);
    // Disabling error message
    lblWarning.setVisible(false);

    // Loading options for profile picture
    String picStrings[] = {"Boy", "Dad", "Girl", "Mother", "Woman"};
    ObservableList<String> picNames = FXCollections.observableArrayList(picStrings);
    picChooser.setItems(picNames);

    // Loading default option
    picChooser.setValue("Boy");
    Image boyImage = new Image(getClass().getResourceAsStream("/images/profileicons/boy.png"));
    picPreview.setImage(boyImage);

    // Adding a listener to change the picture preview
    picChooser.setOnAction(
        event -> {
          Image preview =
              new Image(
                  getClass()
                      .getResourceAsStream(
                          "/images/profileicons/" + picChooser.getValue().toLowerCase() + ".png"));
          // Changing the preview to the new selection
          picPreview.setImage(preview);
          SoundManager.playSound();
        });
  }

  /**
   * creates a new user with the specified fields and logs them into the game
   *
   * @param event, the button press
   */
  @FXML
  private void onLogIn(ActionEvent event) {
    UserDaoJson userDao = new UserDaoJson();
    String userName = userEntry.getText();

    // check if the user name is taken
    if (userDao.checkExists(userName)) {
      lblWarning.setVisible(true);
      lblWarning.setText("This name is taken!");
      return;
    }

    // check if the field is left blank
    if (userName.equals("")) {
      lblWarning.setVisible(true);
      lblWarning.setText("You can't have an empty name!");
      return;
    }

    // check if the user name is too long
    if (userName.length() > 15) {
      lblWarning.setVisible(true);
      lblWarning.setText("Your name is too long!");
    }

    // add new user to database
    UserModel user = new UserModel(userName, picChooser.getValue().toLowerCase());
    userDao.add(user);

    // set the user as the active user
    UserModel.setActiveUser(userDao.get(userName));

    // create a blank settings entry for the new user
    GameSettingDao settingDao = new GameSettingDao();
    settingDao.add(user.getId());

    // get root and controller
    Scene scene = ((Button) event.getSource()).getScene();
    Parent categoryRoot = SceneManager.getUiRoot(AppUi.CATEGORY_SELECT);
    CategoryController categoryController =
        (CategoryController) SceneManager.getController(categoryRoot);

    // set the setting model
    String id = UserModel.getActiveUser().getId();
    categoryController.setUserSettings(id);

    // reset the page
    userEntry.setText("");
    lblWarning.setText("");
    lblWarning.setVisible(false);

    // change scene
    scene.setRoot(categoryRoot);
    SoundManager.playSound(SoundName.LOG_IN);
  }

  /**
   * Changes the current scene from sign up back to log in
   *
   * @param event, the button press used to determine current scene
   */
  @FXML
  private void onBack(ActionEvent event) {
    // get root
    Scene scene = ((Button) event.getSource()).getScene();
    Parent loginRoot = SceneManager.getUiRoot(AppUi.LOG_IN);
    // change scene
    SoundManager.playSound(SoundName.LOG_OUT);
    scene.setRoot(loginRoot);
  }
}
