package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.Collections;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
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
  @FXML private ScrollPane spnBadgeView;

  // badges
  @FXML private Button badge0;
  @FXML private Button badge1;
  @FXML private Button badge2;
  @FXML private Button badge3;
  @FXML private Button badge4;
  @FXML private Button badge5;
  @FXML private Button badge6;
  @FXML private Button badge7;
  @FXML private Button badge8;
  @FXML private Button badge9;
  @FXML private Button badge10;
  @FXML private Button badge11;
  @FXML private Button badge12;
  @FXML private Button badge13;
  @FXML private Button badge14;
  @FXML private Button badge15;
  @FXML private Button badge16;
  @FXML private Button badge17;
  @FXML private Button badge18;
  @FXML private Button badge19;

  private UserModel activeUser;
  private UserStatsDao userStatsDao = new UserStatsDao();
  private ObservableList<String> gamesEasyHistory;
  private ArrayList<Button> allBadges = new ArrayList<>();

  public void initialize() {
    // ready the games history back end
    gamesEasyHistory = FXCollections.observableArrayList();
    lvwEasyHistory.setItems(gamesEasyHistory);
    // set up badges scrollbar
    spnBadgeView.setVbarPolicy(ScrollBarPolicy.NEVER);
    spnBadgeView.setHbarPolicy(ScrollBarPolicy.ALWAYS);
    spnBadgeView.setFitToHeight(true);
    // set up list of badges
    Collections.addAll(
        allBadges, badge0, badge1, badge2, badge3, badge4, badge5, badge6, badge7, badge8, badge9,
        badge10, badge11, badge12, badge13, badge14, badge15, badge16, badge17, badge18, badge19);
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
      String category = Difficulty.valueOf(game.getDifficulty()).toString();

      // get won status
      String won;
      if (game.getWon()) {
        won = "Won \"";
      } else {
        won = "Lost \"";
      }

      // build string
      stringBuilder.setLength(0);
      stringBuilder
          .append(won)
          .append(game.getWord())
          .append("\" in ")
          .append(game.getTime())
          .append(" seconds");

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

    String category = Difficulty.valueOf(bestGame.getDifficulty()).toString();

    // build and update the best game text
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("Drew \"")
        .append(bestGame.getWord())
        .append("\" in ")
        .append(bestGame.getTime())
        .append(" seconds");
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
    String category = Difficulty.valueOf(lastGame.getDifficulty()).toString();

    // build and update the best game text
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("Drew \"")
        .append(lastGame.getWord())
        .append("\" in ")
        .append(lastGame.getTime())
        .append(" seconds");
    txtLastGame.setText(stringBuilder.toString());
  }

  @FXML
  private void onPressBadge() {}
}
