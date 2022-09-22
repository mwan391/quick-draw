package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.models.UserModel;

public class StatisticsController implements Controller {

  @FXML private ListView<String> lvwEasyHistory;
  @FXML private Text txtHeader;
  @FXML private Text txtWinRateWord;
  @FXML private Text txtWinRatePercent;
  @FXML private Text txtBestGame;
  @FXML private Text txtLastGame;
  @FXML private Button btnBackToMenu;

  private UserModel activeUser;

  @FXML
  private void onBackToMenu(ActionEvent event) {
    Scene scene = ((Button) event.getSource()).getScene();
    Parent logInRoot = SceneManager.getUiRoot(AppUi.CATEGORY_SELECT);
    scene.setRoot(logInRoot);
  }

  public void loadPage() {
    // get relevant statistics
    activeUser = UserModel.getActiveUser();

    // set header label
    txtHeader.setText(activeUser.getUsername() + "'s Statistics");
  }
}
