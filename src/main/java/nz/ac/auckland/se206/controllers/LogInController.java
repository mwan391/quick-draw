package nz.ac.auckland.se206.controllers;

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
  @FXML private Text userTextTwo;
  @FXML private Text userTextThree;
  @FXML private Text userTextFour;
  @FXML private Text userTextFive;
  @FXML private Text userTextSix;

  private UserDaoJson userDao = new UserDaoJson();

  public void initialize() {
    List<UserModel> tempUsers = userDao.getAll();

    // Putting info onto usercards
    if (tempUsers.size() >= 1) {
      userTextOne.setText(tempUsers.get(0).getUsername());
      if (tempUsers.size() >= 2) {
        userTextTwo.setText(tempUsers.get(1).getUsername());
        if (tempUsers.size() >= 3) {
          userTextTwo.setText(tempUsers.get(2).getUsername());
          if (tempUsers.size() >= 4) {
            userTextTwo.setText(tempUsers.get(3).getUsername());
            if (tempUsers.size() >= 5) {
              userTextTwo.setText(tempUsers.get(4).getUsername());
              if (tempUsers.size() == 6) {
                userTextTwo.setText(tempUsers.get(5).getUsername());
              }
            }
          }
        }
      }
    }
  }

  @FXML
  private void onLogOne(ActionEvent event) {
    String username = userTextOne.getText();
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  @FXML
  private void onLogTwo(ActionEvent event) {
    String username = userTextTwo.getText();
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  @FXML
  private void onLogThree(ActionEvent event) {
    String username = userTextThree.getText();
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  @FXML
  private void onLogFour(ActionEvent event) {
    String username = userTextFour.getText();
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  @FXML
  private void onLogFive(ActionEvent event) {
    String username = userTextFive.getText();
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  @FXML
  private void onLogSix(ActionEvent event) {
    String username = userTextSix.getText();
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  private void logIn(ActionEvent event, String username) {
    // set the user as the active user
    UserModel.setActiveUser(userDao.get(username));

    // go to the next screen
    nextCategory(event);
  }

  private void signUp(ActionEvent event) {
    // get root and signup
    Scene scene = ((Button) event.getSource()).getScene();
    Parent signUpRoot = SceneManager.getUiRoot(AppUi.SIGN_UP);

    // change scene
    scene.setRoot(signUpRoot);
  }

  private void nextCategory(ActionEvent event) {

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
