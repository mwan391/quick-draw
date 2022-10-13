package nz.ac.auckland.se206.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;

public class SignUpController implements Controller {

  @FXML private TextField userEntry;
  @FXML private Label lblWarning;
  @FXML private ChoiceBox<String> picChooser;
  @FXML private ImageView picPreview;

  public void initialize() {
    // Loading options for profile picture
    String picStrings[] = {"boy", "dad", "girl", "mother", "woman"};
    ObservableList<String> picNames = FXCollections.observableArrayList(picStrings);
    picChooser.setItems(picNames);

    // Loading default option
    picChooser.setValue("boy");
    Image boyImage = new Image(getClass().getResourceAsStream("/images/profileicons/boy.png"));
    picPreview.setImage(boyImage);

    // Adding a listener to change the picture preview
    picChooser.setOnAction(
        event -> {
          Image preview =
              new Image(
                  getClass()
                      .getResourceAsStream(
                          "/images/profileicons/" + picChooser.getValue() + ".png"));
          // Changing the preview to the new selection
          picPreview.setImage(preview);
        });
  }

  @FXML
  private void onLogIn(ActionEvent event) {
    UserDaoJson userDao = new UserDaoJson();
    String userName = userEntry.getText();

    // check if the user name is taken
    if (userDao.checkExists(userName)) {
      lblWarning.setText("This username is already taken.");
      return;
    }

    // check if the field is left blank
    if (userName.equals("")) {
      lblWarning.setText("Please select a valid username.");
      return;
    }

    // check if the user name is too long
    if (userName.length() > 15) {
      lblWarning.setText("Max length is 15.");
      return;
    }

    // add new user to database
    UserModel user = new UserModel(userName, picChooser.getValue());
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

    // change scene
    scene.setRoot(categoryRoot);
  }
}
