package nz.ac.auckland.se206.controllers;

import java.sql.SQLException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.UserDao;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class LogInController implements Controller {

  @FXML private Button btnSignUp;
  @FXML private Button btnLogIn;
  @FXML private Label lblWarning;
  @FXML private TextField fldUserName;
  @FXML private PasswordField fldPassword;
  private UserDao userDao = new UserDao();

  @FXML
  private void onSignUp(ActionEvent event) throws SQLException {
    String userName = fldUserName.getText();
    String password = fldPassword.getText();

    // check if the user name/password combination is taken

    if (userDao.checkExists(userName)) {
      lblWarning.setText("This username is already taken.");
      return;
    }

    // the user name is not taken so we add it to the database, set the newly made user as the
    // active user
    UserModel.setActiveUser(userDao.getUserById(userDao.addNewUser(userName, password)));
    ;

    // go to the next screen
    nextScreen(event);
  }

  @FXML
  private void onLogIn(ActionEvent event) throws SQLException {

    String userName = fldUserName.getText();
    String password = fldPassword.getText();

    // check if the un/pw combination is correct
    int userId = userDao.getId(userName, password);
    if (userId == -1) {
      lblWarning.setText("Invalid login attempt.");
      return;
    }
    userDao.getUserById(userId);

    // go to the next screen
    nextScreen(event);
  }

  private void nextScreen(ActionEvent event) {

    // change the scene
    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));

    // run the text to speech on a background thread to avoid lags
    TextToSpeech textToSpeech = new TextToSpeech();
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak("Select a category.");
            return null;
          }
        };

    // start the thread
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    // reset the page for the next log in
    resetPage();
  }

  private void resetPage() {
    fldUserName.setText("");
    fldPassword.setText("");
    lblWarning.setText("");
  }

  @FXML
  private void onExitGame() {
    System.exit(0);
  }
}
