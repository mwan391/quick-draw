package nz.ac.auckland.se206.controllers;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;

public class LogInController implements Controller {

  @FXML private Button btnSignUp;
  @FXML private Button btnLogIn;
  @FXML private Label lblWarning;
  @FXML private ComboBox<String> fldUserName;
  private UserDaoJson userDao = new UserDaoJson();
  private ObservableList<String> existingUsers;

  public void initialize() {
    fldUserName.setEditable(true);

    // create the observable list of existing user names for the drop down menu
    existingUsers = FXCollections.observableArrayList();

    List<UserModel> tempUsers = userDao.getAll();

    for (UserModel user : tempUsers) {
      existingUsers.add(user.getUsername());
    }

    fldUserName.setItems(existingUsers);
  }

  @FXML
  private void onSignUp(ActionEvent event) {
    String userName = fldUserName.getValue();

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

    // check if either the user name or password is too long
    if (userName.length() > 15) {
      lblWarning.setText("Max length is 15.");
      return;
    }

    // add new user to database
    UserModel user = new UserModel(userName);
    userDao.add(user);

    // set the newly made user as the active user and add to the drop down list
    UserModel.setActiveUser(user);
    existingUsers.add(userName);

    // create a blank settings entry for the new user
    GameSettingDao settingDao = new GameSettingDao();
    settingDao.add(user.getId());

    // go to the next screen
    nextScreen(event);
  }

  @FXML
  private void onLogIn(ActionEvent event) {

    String userName = fldUserName.getValue();

    // check if the un is in the system
    if (!userDao.checkExists(userName)) {
      lblWarning.setText("Invalid login attempt.");
      return;
    }

    // set the user as the active user
    UserModel.setActiveUser(userDao.get(userName));

    // go to the next screen
    nextScreen(event);
  }

  private void nextScreen(ActionEvent event) {

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

    // reset the page for the next log in
    resetPage();
  }

  private void resetPage() {
    fldUserName.setValue("");
    lblWarning.setText("");
  }
}
