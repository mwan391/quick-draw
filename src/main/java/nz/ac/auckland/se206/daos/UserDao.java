package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserDao {

  /**
   * Creates a new user into the database and returns the id
   *
   * @param username of new user
   * @param password of new user
   * @return id of new user
   * @throws SQLException
   */
  public int addNewUser(String username, String password) throws SQLException {

    Connection connection = SqliteConnection.openConnection();
    String query = "INSERT INTO users (username, password, game_id) VALUES (?,?,0)";
    PreparedStatement ps = connection.prepareStatement(query);
    // input query parameters
    ps.setString(1, username);
    ps.setString(2, password);
    ps.executeUpdate();

    int userId = 0;
    // get next unique id for user
    ResultSet resultSet = ps.getGeneratedKeys();

    if (resultSet.next()) {
      userId = resultSet.getInt(1);
    }
    SqliteConnection.closeConnection(connection);
    return userId;
  }

  /**
   * Get user by username and password and returns the id, if no user is found will return -1
   *
   * @param username of user
   * @param pwd of user
   * @return id or -1 if no existing user is found
   * @throws SQLException
   */
  public int getId(String username, String pwd) throws SQLException {

    int userId = -1;
    Connection connection = SqliteConnection.openConnection();
    PreparedStatement statement =
        connection.prepareStatement("SELECT id FROM users WHERE username=? AND password=?");
    // input query parameters
    statement.setString(1, username);
    statement.setString(2, pwd);

    ResultSet rst = statement.executeQuery();

    if (rst.next()) {
      userId = rst.getInt("id");
    }
    SqliteConnection.closeConnection(connection);
    return userId;
  }

  /**
   * check if a username has already been used in the database and returns the result
   *
   * @param username to check for
   * @return true or false
   * @throws SQLException
   */
  public boolean checkExists(String username) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    PreparedStatement statement =
        connection.prepareStatement("SELECT 1 FROM users WHERE username=?");
    statement.setString(1, username);
    ResultSet rst = statement.executeQuery();
    boolean exists = rst.next();
    SqliteConnection.closeConnection(connection);
    return exists;
  }

  /**
   * get instance of user by id
   *
   * @param userId
   * @return user
   * @throws SQLException
   */
  public UserModel getUserById(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT id, username, password, game_id FROM users WHERE id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();

    UserModel user = rs.next() ? getUser(rs) : null;
    SqliteConnection.closeConnection(connection);
    return user;
  }

  /**
   * set the latest game the user has played
   *
   * @param gameId of latest game played
   * @param userId of user
   * @throws SQLException
   */
  public void setGameId(int gameId, int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "UPDATE users SET game_id=? WHERE id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, gameId);
    ps.setInt(2, userId);
    ps.execute();
    SqliteConnection.closeConnection(connection);
  }

  /**
   * getter for user model instance
   *
   * @param rs
   * @return instance of user
   * @throws SQLException
   */
  private UserModel getUser(ResultSet rs) throws SQLException {
    return new UserModel(
        rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getInt("game_id"));
  }
}
