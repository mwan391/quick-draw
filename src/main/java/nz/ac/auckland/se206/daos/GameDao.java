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

public class GameDao {

  /**
   * add a new game to the database and return its id
   *
   * @param userId of current user
   * @param difficultyValue using ordinal of difficulty enum
   * @param word of chosen category
   * @return id of new game
   */
  public int addNewGame(int userId, int difficultyValue, String word) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      String query = "INSERT INTO games (user_id, difficulty, word, won, time) VALUES (?,?,?,0,60)";
      PreparedStatement ps = connection.prepareStatement(query);
      // set paramaters (column titles) to add into the table
      ps.setInt(1, userId);
      ps.setInt(2, difficultyValue);
      ps.setString(3, word);
      ps.executeUpdate();
      // get next unique id for new game
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return id;
  }

  /**
   * when game ends, update time it took to draw the prediction in seconds
   *
   * @param time in seconds of drawing time
   * @param gameId of current game
   */
  public void setTime(int time, int gameId) {
    Connection connection = SqliteConnection.openConnection();
    try {
      // update playing time for given game
      String query = "UPDATE games SET time=? WHERE id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, time);
      ps.setInt(2, gameId);
      ps.execute();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
  }

  /**
   * set the result of the game
   *
   * @param won boolean of winning status
   * @param gameId of current game
   */
  public void setWon(boolean won, int gameId) {
    Connection connection = SqliteConnection.openConnection();
    try {
      // update winning status for given game
      String query = "UPDATE games SET won=? WHERE id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setBoolean(1, won);
      ps.setInt(2, gameId);
      ps.execute();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
  }

  public GameModel getGameById(int gameId) {
    Connection connection = SqliteConnection.openConnection();
    GameModel game = null;
    try {
      // find matching game
      String query = "SELECT id, user_id, difficulty, word, won, time FROM games WHERE id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, gameId);
      ResultSet rs = ps.executeQuery();
      // return an instance of the given game
      game = rs.next() ? getGame(rs) : null;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return game;
  }

  /**
   * retrieves all games played by a given user
   *
   * @param userId of given user
   * @return list of games
   */
  public List<GameModel> getGames(int userId) {
    Connection connection = SqliteConnection.openConnection();
    List<GameModel> games = new ArrayList<>();
    try {
      String query = "SELECT * FROM games WHERE user_id=? ORDER by id";
      PreparedStatement ps = connection.prepareStatement(query);
      // filter for games played by given user
      ps.setInt(userId, 1);
      ResultSet rs = ps.executeQuery();
      // convert the results to game instances
      while (rs.next()) {
        GameModel game = getGame(rs);
        games.add(game);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return games;
  }

  private GameModel getGame(ResultSet rs) throws SQLException {
    // helper to convert a game row in table to game instance
    return new GameModel(
        rs.getInt("id"),
        rs.getInt("user_id"),
        rs.getInt("difficulty"),
        rs.getString("word"),
        rs.getBoolean("won"),
        rs.getInt("time"));
  }
}
