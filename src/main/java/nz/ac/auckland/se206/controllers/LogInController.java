package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogInController implements Controller {

  @FXML private Button btnSignUp;
  @FXML private Button btnLogIn;
  @FXML private TextField fldUserName;
  @FXML private PasswordField fldPassword;
}
