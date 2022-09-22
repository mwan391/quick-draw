package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class StatisticsController implements Controller {

  @FXML private ListView<String> lvwEasyHistory;
  @FXML private Text txtHeader;
  @FXML private Text txtWinRateWord;
  @FXML private Text txtWinRatePercent;
  @FXML private Text txtBestGame;
  @FXML private Text txtLastGame;
  @FXML private Button btnBackToMenu;

  @FXML
  private void onBackToMenu() {}
}
