package nz.ac.auckland.se206.controllers;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.UserStatsDao;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.models.UserModel;

public class StatisticsController implements Controller {

  @FXML private ListView<String> lvwEasyHistory;
  @FXML private Label header;
  @FXML private Text txtWinRateWord;
  @FXML private Text txtWinRatePercent;
  @FXML private Text txtBestGame;
  @FXML private Text txtLastGame;
  @FXML private Button btnBackToMenu;

  private UserModel activeUser;
  private UserStatsDao userStatsDao = new UserStatsDao();
  private ObservableList<String> gamesEasyHistory;

  public void initialize() {
    // ready the games history back end
    gamesEasyHistory = FXCollections.observableArrayList();
    lvwEasyHistory.setItems(gamesEasyHistory);
  }

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
    header.setText(activeUser.getUsername() + "'s Statistics");

    // set game statistics
    setWinRate();
    setBestGame();
    setLastGame();

    // set word histories
    setEasyHistory();
  }

  private void setEasyHistory() {
    // get relevant statistics
    List<GameModel> latestGames = userStatsDao.getTen(activeUser.getId());

    // refresh history
    gamesEasyHistory.clear();
    StringBuilder stringBuilder = new StringBuilder();

    // build strings and add to list view
    for (GameModel game : latestGames) {

      // get category name
      String category = CategorySelect.Difficulty.values()[game.getDifficulty()].toString();

      // get won status
      String won;
      if (game.getWon()) {
        won = ": Won \"";
      } else {
        won = ": Lost \"";
      }

      // build string
      stringBuilder.setLength(0);
      stringBuilder
          .append(game.getId())
          .append(won)
          .append(game.getWord())
          .append("\" in ")
          .append(game.getTime())
          .append(" seconds, from the ")
          .append(category)
          .append(" category");

      // add to list
      gamesEasyHistory.add(stringBuilder.toString());
    }
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

    // check if user has played any games
    if (gameCount == 0) {
      txtWinRatePercent.setText("You have not played any games yet!");
      return;
    }

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

    // check if user has played any games
    if (bestGame.getId() == 0) {
      txtBestGame.setText("You have not played any games yet!");
      return;
    }

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

    // check if user has played any games
    if (lastGames.size() == 0) {
      txtLastGame.setText("You have not played any games yet!");
      return;
    }

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
