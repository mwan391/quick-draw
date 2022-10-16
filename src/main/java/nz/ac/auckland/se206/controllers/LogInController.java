package nz.ac.auckland.se206.controllers;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.models.UserModel;

public class LogInController implements Controller {

  @FXML private Text userTextOne;
  @FXML private Text userTextTwo;
  @FXML private Text userTextThree;
  @FXML private Text userTextFour;
  @FXML private Text userTextFive;
  @FXML private Text userTextSix;
  @FXML private ImageView iconOne;
  @FXML private ImageView iconTwo;
  @FXML private ImageView iconThree;
  @FXML private ImageView iconFour;
  @FXML private ImageView iconFive;
  @FXML private ImageView iconSix;

  private UserDaoJson userDao = new UserDaoJson();

  /**
   * onLogOne logs in the first user or sends them to the signup page
   *
   * @param event that calls this method
   */
  @FXML
  private void onLogOne(ActionEvent event) {
    logX(event, userTextOne.getText());
  }

  /**
   * onLogTwo logs in the second user or sends them to the signup page
   *
   * @param event that calls this method
   */
  @FXML
  private void onLogTwo(ActionEvent event) {
    logX(event, userTextTwo.getText());
  }

  /**
   * onLogThree logs in the third user or sends them to the signup page
   *
   * @param event that calls this method
   */
  @FXML
  private void onLogThree(ActionEvent event) {
    logX(event, userTextThree.getText());
  }

  /**
   * onLogFour logs in the fourth user or sends them to the signup page
   *
   * @param event that calls this method
   */
  @FXML
  private void onLogFour(ActionEvent event) {
    logX(event, userTextFour.getText());
  }

  /**
   * onLogFive logs in the fifth user or sends them to the signup page
   *
   * @param event that calls this method
   */
  @FXML
  private void onLogFive(ActionEvent event) {
    logX(event, userTextFive.getText());
  }

  /**
   * onLogSix logs in the sixth user or sends them to the signup page
   *
   * @param event that calls this method
   */
  @FXML
  private void onLogSix(ActionEvent event) {
    logX(event, userTextSix.getText());
  }

  /**
   * Logs in the user using their username or sends to signup
   *
   * @param event that calls this method
   * @param username the name of the account being logged in
   */
  private void logX(ActionEvent event, String username) {
    // Logging in the user if the profile has been created
    if (username.equals("New User")) {
      signUp(event);
    } else {
      logIn(event, username);
    }
  }

  /** this method loads userdata by displaying the username and icon for each user */
  public void loadUserData() {
    List<UserModel> tempUsers = userDao.getAll();

    resetAll();
    // Putting info onto usercards
    if (tempUsers.size() >= 1) {
      // Updating usercard one
      userTextOne.setText(tempUsers.get(0).getUsername());
      iconOne.setImage(loadImage(tempUsers.get(0).getIcon()));
      if (tempUsers.size() >= 2) {
        // Updating usercard two
        userTextTwo.setText(tempUsers.get(1).getUsername());
        iconTwo.setImage(loadImage(tempUsers.get(1).getIcon()));
        if (tempUsers.size() >= 3) {
          // Updating usercard three
          userTextThree.setText(tempUsers.get(2).getUsername());
          iconThree.setImage(loadImage(tempUsers.get(2).getIcon()));
          if (tempUsers.size() >= 4) {
            // Updating usercard four
            userTextFour.setText(tempUsers.get(3).getUsername());
            iconFour.setImage(loadImage(tempUsers.get(3).getIcon()));
            if (tempUsers.size() >= 5) {
              // Updating usercard five
              userTextFive.setText(tempUsers.get(4).getUsername());
              iconFive.setImage(loadImage(tempUsers.get(4).getIcon()));
              if (tempUsers.size() == 6) {
                // Updating usercard six
                userTextSix.setText(tempUsers.get(5).getUsername());
                iconSix.setImage(loadImage(tempUsers.get(5).getIcon()));
              }
            }
          }
        }
      }
    }
  }

  /** This method resets every card back to it's initial state, as if no users have signed up */
  private void resetAll() {
    // user One
    userTextOne.setText("New User");
    iconOne.setImage(loadImage("new"));
    // user Two
    userTextTwo.setText("New User");
    iconTwo.setImage(loadImage("new"));
    // user Three
    userTextThree.setText("New User");
    iconThree.setImage(loadImage("new"));
    // user Four
    userTextFour.setText("New User");
    iconFour.setImage(loadImage("new"));
    // user Five
    userTextFive.setText("New User");
    iconFive.setImage(loadImage("new"));
    // user Six
    userTextSix.setText("New User");
    iconSix.setImage(loadImage("new"));
  }

  /**
   * This method creates an image by finding its source file using its filename
   *
   * @param name of the image file being loaded
   * @return the loaded image
   */
  private Image loadImage(String name) {
    Image icon = new Image(getClass().getResourceAsStream("/images/profileicons/" + name + ".png"));
    return icon;
  }

  /**
   * This method logs the user in by setting them as the active user
   *
   * @param event the button press
   * @param username the name of the active user
   */
  private void logIn(ActionEvent event, String username) {
    // set the user as the active user
    UserModel.setActiveUser(userDao.get(username));

    // go to the next screen
    nextCategory(event);
  }

  /**
   * This method sends the user to the sign up page and plays the appropriate sound
   *
   * @param event the button press
   */
  private void signUp(ActionEvent event) {
    // get root and signup
    Scene scene = ((Button) event.getSource()).getScene();
    Parent signUpRoot = SceneManager.getUiRoot(AppUi.SIGN_UP);

    SoundManager.playSound();

    // change scene
    scene.setRoot(signUpRoot);
  }

  /**
   * sends the logged in user to the category screen and gives them a setting model
   *
   * @param event the button press
   */
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

    SoundManager.playSound(SoundName.LOG_IN);
  }
}
