package nz.ac.auckland.se206.controllers;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameDao;
import nz.ac.auckland.se206.daos.UserStatsDao;
import nz.ac.auckland.se206.models.GameModel;
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

    // set best game
    setBestGame();

    // set latest game
    setLastGame();
  }

  private void setWinRate() {
    // get relevant statistics
    // initialise counts to avoid breakage
    int winCount = userStatsDao.countWins(activeUser.getId());
    int gameCount = userStatsDao.countGames(activeUser.getId());

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

  private void setBestGame() {
    // get relevant statistics
    GameModel bestGame = userStatsDao.getBestGame(activeUser.getId());
    String category = CategorySelect.Difficulty.values()[bestGame.getDifficulty()].toString();

    // build and update the best game text
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("Drew \"")
        .append(bestGame.getWord())
        .append("\" in ")
        .append(bestGame.getTime())
        .append(" seconds, from the ")
        .append(category)
        .append(" category");
    txtBestGame.setText(stringBuilder.toString());
  }

  private void setLastGame() {
    // get relevant statistics
    List<GameModel> lastGames = userStatsDao.getTen(activeUser.getId());
    GameModel lastGame = lastGames.get(0);
    String category = CategorySelect.Difficulty.values()[lastGame.getDifficulty()].toString();

    // build and update the best game text
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("Drew \"")
        .append(lastGame.getWord())
        .append("\" in ")
        .append(lastGame.getTime())
        .append(" seconds, from the ")
        .append(category)
        .append(" category");
    txtLastGame.setText(stringBuilder.toString());
  }
}
