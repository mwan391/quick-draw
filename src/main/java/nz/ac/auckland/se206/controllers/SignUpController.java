package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;

public class SignUpController implements Controller {

  @FXML private TextField userEntry;
  @FXML private Label lblWarning;

  private UserDaoJson userDao = new UserDaoJson();

  @FXML
  private void onLogIn(ActionEvent event) {
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
    UserModel user = new UserModel(userName);
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

    // change scene
    scene.setRoot(categoryRoot);
  }
}
