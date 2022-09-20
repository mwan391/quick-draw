package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.util.SqliteConnection;

public class GameDao {

  /**
   * add a new game to the database and return its id
   *
   * @param userId of current user
   * @param difficultyValue using ordinal of difficulty enum
   * @param word of chosen category
   * @return id of new game
   * @throws SQLException
   */
  public int addNewGame(int userId, int difficultyValue, String word) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "INSERT INTO games (user_id, difficulty, word, won, time) VALUES (?,?,?,0,60)";
    PreparedStatement ps = connection.prepareStatement(query);
    // set paramaters for the user table
    ps.setInt(1, userId);
    ps.setInt(2, difficultyValue);
    ps.setString(3, word);
    ps.executeUpdate();
    // get next unique id for new game
    ResultSet rs = ps.getGeneratedKeys();
    int gameId = 0;
    if (rs.next()) {
      gameId = rs.getInt(1);
    }

    SqliteConnection.closeConnection(connection);
    return gameId;
  }

  /**
   * when game ends, update time it took to draw the prediction in seconds
   *
   * @param time in seconds of drawing time
   * @param gameId of current game
   * @throws SQLException
   */
  public void setTime(int time, int gameId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "UPDATE games SET time=? WHERE id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, time);
    ps.setInt(2, gameId);
    // update playing time for given game
    ps.execute();
    SqliteConnection.closeConnection(connection);
  }

  /**
   * set the result of the game
   *
   * @param won boolean of winning status
   * @param gameId of current game
   * @throws SQLException
   */
  public void setWon(boolean won, int gameId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "UPDATE games SET won=? WHERE id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setBoolean(1, won);
    ps.setInt(2, gameId);
    // update winning status for given game
    ps.execute();
    SqliteConnection.closeConnection(connection);
  }

  public GameModel getGameById(int gameId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT id, user_id, difficulty, word, won, time FROM games WHERE id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, gameId);
    ResultSet rs = ps.executeQuery();
    // return an instance of the given game
    GameModel game = rs.next() ? getGame(rs) : null;
    SqliteConnection.closeConnection(connection);
    return game;
  }

  /**
   * retrieves all games played by a given user
   *
   * @param userId of given user
   * @return list of games
   * @throws SQLException
   */
  public List<GameModel> getGames(int userId) throws SQLException {
    List<GameModel> games = new ArrayList<>();
    Connection connection = SqliteConnection.openConnection();
    String query =
        "SELECT id, user_id, difficulty, word, won, time FROM games FROM games WHERE user_id=? ORDER by id";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(userId, 1);
    // filter for games played by given user
    ResultSet rs = ps.executeQuery();
    // convert the results to GameModel instances
    while (rs.next()) {
      GameModel game = getGame(rs);
      games.add(game);
    }
    SqliteConnection.closeConnection(connection);
    return games;
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
