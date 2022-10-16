package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class GameDao {

  /**
   * add a new game to the database and return its id
   *
   * @param userId of current user
   * @param difficulty using difficulty enum
   * @param word of chosen category
   * @return id of new game
   */
  public int addNewGame(String userId, Difficulty difficulty, String word) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      // add new row to table
      String query = "INSERT INTO games (user_id, difficulty, word, won, time) VALUES (?,?,?,0,60)";
      PreparedStatement ps = connection.prepareStatement(query);
      // set paramaters (column titles) to add into the table
      ps.setString(1, userId);
      ps.setString(2, difficulty.toString());
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
      // update drawing time for given game
      String query = "UPDATE games SET time=? WHERE id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // input query parameters
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
      // input query parameters
      ps.setBoolean(1, won);
      ps.setInt(2, gameId);
      ps.execute();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
  }

  /**
   * Removes any games linked to a given user from Sqlite games database
   *
   * @param user to remove games for
   * @return true if removal is successful
   */
  public boolean removeGamesFromUser(UserModel user) {
    Connection connection = SqliteConnection.openConnection();
    boolean isRemoved = false;
    try {
      // Remove the games under user
      String query = "DELETE from games WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // Input the user
      ps.setString(1, user.getId());
      ps.executeUpdate();
      // Removal is succesful
      isRemoved = true;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return isRemoved;
  }

  /**
   * retrieves a previously played game by its id
   *
   * @param gameId of current game
   * @return the game instance with all its details
   */
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
   * @return list of game session
   */
  public List<GameModel> getGames(String userId) {
    Connection connection = SqliteConnection.openConnection();
    List<GameModel> games = new ArrayList<>();
    try {
      // filter for matching games
      String query = "SELECT * FROM games WHERE user_id=? ORDER by id";
      PreparedStatement ps = connection.prepareStatement(query);
      // input query paramter
      ps.setString(1, userId);
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

  /**
   * Helper to convert a row (game details) to a game instance
   *
   * @param rs a table of data from a sql query
   * @return instance of game session
   */
  private GameModel getGame(ResultSet rs) {
    GameModel game = null;
    try {
      // translate each game detail (id, time, result) to its game field
      game =
          new GameModel(
              rs.getInt("id"),
              rs.getString("user_id"),
              rs.getString("difficulty"),
              rs.getString("word"),
              rs.getBoolean("won"),
              rs.getInt("time"));
      // all table columns converted to fields
    } catch (SQLException e) {
      // Catch invalid SQL results
      Logger.printSqlError(e);
    }
    return game;
  }
}
