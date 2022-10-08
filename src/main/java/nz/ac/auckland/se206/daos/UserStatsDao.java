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

  public List<GameModel> getTen(int userId) {
    Connection connection = SqliteConnection.openConnection();
    List<GameModel> games = new ArrayList<>();
    try {
      // query for most recent game by finding last id
      String query = "SELECT * FROM games WHERE user_id=? ORDER BY id DESC LIMIT 10";
      PreparedStatement ps = connection.prepareStatement(query);
      // filter results under user
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // convert results to a game instance to list
      while (rs.next()) {
        games.add(getGame(rs));
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return games;
  }

  private GameModel getGame(ResultSet rs) {
    // helper to convert to game instance
    GameModel game = null;
    try {
      // translate each column to its game field
      game =
          new GameModel(
              rs.getInt("id"),
              rs.getInt("user_id"),
              rs.getString("difficulty"),
              rs.getString("word"),
              rs.getBoolean("won"),
              rs.getInt("time"));
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
    return game;
  }

  private Map<Difficulty, List<GameModel>> rsToMap(
      ResultSet rs, Map<Difficulty, List<GameModel>> map) {
    GameModel game = getGame(rs);
    // convert difficulty (string) to difficulty (enum)
    Difficulty gDifficulty = Difficulty.valueOf(game.getDifficulty());
    // initiate list for each key
    map.computeIfAbsent(gDifficulty, d -> new ArrayList<>());
    map.get(gDifficulty).add(game);
    return map;
  }

  public Map<Difficulty, List<GameModel>> getMapHistory(int userId) {
    Map<Difficulty, List<GameModel>> map =
        new EnumMap<Difficulty, List<GameModel>>(Difficulty.class);
    Connection connection = SqliteConnection.openConnection();
    try {
      // query for all games player by user
      String query = "SELECT * FROM games WHERE user_id=? ORDER BY id";
      PreparedStatement ps = connection.prepareStatement(query);
      // input specific user
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // map each difficulty to its games e.g. EASY -> [game1, game2, game3]
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
