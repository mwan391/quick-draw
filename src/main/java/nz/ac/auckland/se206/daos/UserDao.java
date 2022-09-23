package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserDao {

  /**
   * Creates a new user into the database and returns the id
   *
   * @param username of new user
   * @param password of new user
   * @return id of new user
   */
  public int addNewUser(String username, String password) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      String query = "INSERT INTO users (username, password, game_id) VALUES (?,?,0)";
      PreparedStatement ps = connection.prepareStatement(query);
      // input query parameters
      ps.setString(1, username);
      ps.setString(2, password);
      ps.executeUpdate();
      // get next unique id for user
      ResultSet resultSet = ps.getGeneratedKeys();
      if (resultSet.next()) {
        id = resultSet.getInt(1);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return id;
  }

  /**
   * Get user by username and password and returns the id, if no user is found will return -1
   *
   * @param username of user
   * @param pwd of user
   * @return id or -1 if no existing user is found
   */
  public int getId(String username, String pwd) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      PreparedStatement statement =
          connection.prepareStatement("SELECT id FROM users WHERE username=? AND password=?");
      // input query parameters
      statement.setString(1, username);
      statement.setString(2, pwd);
      ResultSet rst = statement.executeQuery();
      // filter for id of given user
      if (rst.next()) {
        id = rst.getInt("id");
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return id;
  }

  /**
   * check if a username has already been used in the database and returns the result
   *
   * @param username to check for
   * @return true or false
   * @throws SQLException
   */
  public boolean checkExists(String username) {
    Connection connection = SqliteConnection.openConnection();
    boolean exists = false;
    try {
      PreparedStatement statement =
          connection.prepareStatement("SELECT 1 FROM users WHERE username=?");
      // filter for matching username
      statement.setString(1, username);
      ResultSet rst = statement.executeQuery();
      exists = rst.next();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return exists;
  }

  /**
   * get instance of user by id
   *
   * @param userId
   * @return user
   */
  public UserModel getUserById(int userId) {
    Connection connection = SqliteConnection.openConnection();
    UserModel user = null;
    try {
      // find the matching user
      String query = "SELECT id, username, password, game_id FROM users WHERE id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();
      // convert result to an instance of user
      if (rs.next()) {
        user = getUser(rs);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return user;
  }

  /**
   * set the latest game the user has played
   *
   * @param gameId of latest game played
   * @param userId of user
   */
  public void setGameId(int gameId, int userId) {
    Connection connection = SqliteConnection.openConnection();
    try {
      String query = "UPDATE users SET game_id=? WHERE id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // update the current game
      ps.setInt(1, gameId);
      ps.setInt(2, userId);
      ps.execute();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
  }

  /**
   * Returns a list of all existing users in the database
   *
   * @return list of all user instances in database
   */
  public List<UserModel> getUsers() {
    Connection connection = SqliteConnection.openConnection();
    List<UserModel> users = new ArrayList<>();
    try {
      // get all users
      String query = "SELECT id, username, password, game_id from users";
      PreparedStatement ps = connection.prepareStatement(query);
      ResultSet rs = ps.executeQuery();
      // convert all found users to a user instance
      while (rs.next()) {
        UserModel user = getUser(rs);
        users.add(user);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return users;
  }

  /**
   * getter for user model instance
   *
   * @param rs
   * @return instance of user
   * @throws SQLException
   */
  private UserModel getUser(ResultSet rs) throws SQLException {
    // helper to convert a user row in table to user instnace
    return new UserModel(
        rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getInt("game_id"));
  }
}
