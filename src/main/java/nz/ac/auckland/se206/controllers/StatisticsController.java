package nz.ac.auckland.se206.controllers;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameDao;
import nz.ac.auckland.se206.daos.UserStatsDao;
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
  private UserStatsDao userStatsDao = new UserStatsDao();
  private GameDao gameDao = new GameDao();

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

    // set win rate
    setWinRate();
  }

  private void setWinRate() {
    // get relevant statistics
    // initialise counts to avoid breakage
    int winCount = 0;
    int gameCount = 0;
    try {
      winCount = userStatsDao.countWins(activeUser.getId());
      gameCount = userStatsDao.countGames(activeUser.getId());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // build and update the win rate text
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append(winCount)
        .append(" games won out of ")
        .append(gameCount)
        .append(" games total.");
    txtWinRateWord.setText(stringBuilder.toString());
    // clear builder, and build and update the win rate percentage
    stringBuilder.setLength(0);
    stringBuilder
        .append("Win rate of ")
        .append(100 * ((float) winCount / (float) gameCount))
        .append("%");
    txtWinRatePercent.setText(stringBuilder.toString());
  }
}
