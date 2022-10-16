package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqlConverter;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserStatsDao {

  /**
   * Returns the number of games played by a user from SQLite for a user
   *
   * @param userId of given user
   * @return how many games were found
   */
  public int countGames(String userId) {
    Connection connection = SqliteConnection.openConnection();
    int count = -1;
    try {
      // count the number of rows with matching user id
      String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setString(1, userId);
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

  /**
   * Returns the number of winning games from SQlite for a user
   *
   * @param userId of given user
   * @return how many winning games were found
   */
  public int countWins(String userId) {
    Connection connection = SqliteConnection.openConnection();
    int count = -1;
    try {
      // count the number of rows with matching user id
      String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=? AND won=1";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setString(1, userId);
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

  /**
   * Return the fastest played game from SQLite (shortest played winning game) for a user
   *
   * @param userId of given user
   * @return the game instance
   */
  public GameModel getBestGame(String userId) {
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
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      // convert results to a game instance
      game = rs.next() ? SqlConverter.getGame(rs) : null;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return game;
  }

  /**
   * Returns all the words played by a user from SQLite as a list, will contain all easy, medium and
   * hard words
   *
   * @param userId of user
   * @return list of words
   */
  public List<String> getWordHistory(String userId) {
    Connection connection = SqliteConnection.openConnection();
    // words from games user has played
    List<String> words = new ArrayList<>();
    try {
      // get word history from oldest to recent
      String query = "SELECT word FROM games WHERE user_id=? ORDER BY id";
      PreparedStatement ps = connection.prepareStatement(query);
      // input the user you want the history for
      ps.setString(1, userId);
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

  /**
   * Returns the ten most recently played games from SQLite as a list for a user
   *
   * @param userId of user
   * @return list of games
   */
  public List<GameModel> getTen(String userId) {
    Connection connection = SqliteConnection.openConnection();
    List<GameModel> games = new ArrayList<>();
    try {
      // query for most recent game by finding last id
      String query = "SELECT * FROM games WHERE user_id=? ORDER BY id DESC LIMIT 10";
      PreparedStatement ps = connection.prepareStatement(query);
      // filter results under user
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      // convert results to a game instance to list
      while (rs.next()) {
        games.add(SqlConverter.getGame(rs));
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return games;
  }

  /**
   * Helper to convert SQL results to a map where each difficulty contains a list of words of that
   * difficulty
   *
   * @param rs a set of SQL rows as a result of a query
   * @param map where each difficulty contains a list of words of that difficulty
   * @return the map of difficulty -> words
   * @throws SQLException if SQL results are invalid
   */
  private Map<Difficulty, List<String>> rsToMap(ResultSet rs, Map<Difficulty, List<String>> map)
      throws SQLException {
    String word = rs.getString("word");
    // convert difficulty (string) to difficulty (enum)
    Difficulty difficulty = Difficulty.valueOf(rs.getString("difficulty"));
    // initiate list for each key
    map.computeIfAbsent(difficulty, d -> new ArrayList<>());
    map.get(difficulty).add(word);
    return map;
  }

  /**
   * Retrives all words from SQLite as list played by a user by its difficulty
   *
   * @param userId of game user
   * @return a map where each difficulty maps to a list of words of same difficulty
   */
  public Map<Difficulty, List<String>> getHistoryMap(String userId) {
    Map<Difficulty, List<String>> map = new EnumMap<Difficulty, List<String>>(Difficulty.class);
    Connection connection = SqliteConnection.openConnection();
    try {
      // query for all games player by user
      String query = "SELECT word, difficulty FROM games WHERE user_id=? ORDER BY id";
      PreparedStatement ps = connection.prepareStatement(query);
      // input specific user
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      // map each difficulty to its words e.g. EASY -> [chandelier, banana, diamond]
      while (rs.next()) {
        map = rsToMap(rs, map);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return map;
  }
}
