package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserStatsDao {

  public int countGames(int userId) {
    Connection connection = SqliteConnection.openConnection();
    int count = -1;
    try {
      // count the number of rows with matching user id
      String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // number of played games, if none return 0
      count = rs.next() ? rs.getInt("count") : 0;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return count;
  }

  public int countWins(int userId) {
    Connection connection = SqliteConnection.openConnection();
    int count = -1;
    try {
      // count the number of rows with matching user id
      String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=? AND won=1";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // number of winning games else return 0
      count = rs.next() ? rs.getInt("count") : 0;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return count;
  }

  public GameModel getBestGame(int userId) {
    Connection connection = SqliteConnection.openConnection();
    GameModel game = null;
    try {
      // query for game with shortest time
      StringBuilder sb =
          new StringBuilder("SELECT id, user_id, difficulty, word, won, MIN(time) AS time ");
      sb.append("FROM games WHERE user_id=? AND won=1");
      String query = sb.toString();
      PreparedStatement ps = connection.prepareStatement(query);
      // filter for given user
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // convert results to a game instance
      game = rs.next() ? getGame(rs) : null;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return game;
  }

  public List<String> getWordHistory(int userId) {
    Connection connection = SqliteConnection.openConnection();
    // words from games user has played
    List<String> words = new ArrayList<>();
    try {
      // get word history from oldest to recent
      String query = "SELECT word FROM games WHERE user_id=? ORDER BY id";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        words.add(rs.getString("word"));
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return words;
  }

  public GameModel getLatest(int userId) {
    Connection connection = SqliteConnection.openConnection();
    GameModel game = null;
    try {
      // query for most recent game by finding last id
      StringBuilder sb =
          new StringBuilder("SELECT MAX(id) AS id, user_id, difficulty, word, won, time ");
      sb.append("FROM games WHERE user_id=?");
      String query = sb.toString();
      PreparedStatement ps = connection.prepareStatement(query);
      // filter for given user
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // convert result to a game instance
      game = rs.next() ? getGame(rs) : null;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return game;
  }

  private GameModel getGame(ResultSet rs) throws SQLException {
    // helper to convert to game instance
    return new GameModel(
        rs.getInt("id"),
        rs.getInt("user_id"),
        rs.getInt("difficulty"),
        rs.getString("word"),
        rs.getBoolean("won"),
        rs.getInt("time"));
  }
}
