package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserStatsDao {

  public int countGames(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // number of played games
    int count = rs.next() ? rs.getInt("count") : 0;
    SqliteConnection.closeConnection(connection);
    return count;
  }

  public int countWins(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=? AND won=1";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // number of winning games
    int count = rs.next() ? rs.getInt("count") : 0;
    SqliteConnection.closeConnection(connection);
    return count;
  }

  public GameModel getBestGame(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    // query for game with shortest time
    StringBuilder sb =
        new StringBuilder("SELECT id, user_id, difficulty, word, won, MIN(time) FROM games ");
    sb.append("WHERE user_id=? AND won=1");
    String query = sb.toString();
    PreparedStatement ps = connection.prepareStatement(query);
    // filter for given user
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // convert results to a game instance
    GameModel game = rs.next() ? getGame(rs) : null;
    SqliteConnection.closeConnection(connection);
    return game;
  }

  public List<String> getWordHistory(int userId) throws SQLException {
    // words from games given user has played
    List<String> words = new ArrayList<>();
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT word FROM games WHERE user_id=? ORDER BY id";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      words.add(rs.getString("word"));
    }
    SqliteConnection.closeConnection(connection);
    return words;
  }

  private GameModel getGame(ResultSet rs) throws SQLException {
    return new GameModel(
        rs.getInt("id"),
        rs.getInt("user_id"),
        rs.getInt("difficulty"),
        rs.getString("word"),
        rs.getBoolean("won"),
        rs.getInt("time"));
  }
}
