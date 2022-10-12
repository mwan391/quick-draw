package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;

public class LogInController implements Controller {

  @FXML private Text userTextOne;
  private UserDaoJson userDao = new UserDaoJson();
  private ArrayList<String> existingUsers = new ArrayList<String>();

  public void initialize() {
    List<UserModel> tempUsers = userDao.getAll();

    for (UserModel user : tempUsers) {
      existingUsers.add(user.getUsername());
    }

    userTextOne.setText(existingUsers.get(0));
  }

  @FXML
  private void onLogIn(ActionEvent event) {

    String userName = "placeholder";

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
  }
}
