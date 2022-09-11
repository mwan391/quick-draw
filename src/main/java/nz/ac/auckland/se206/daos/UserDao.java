package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserDao {

  /**
   * Creates a new user into the database and returns the id
   *
   * @param username of new user
   * @param password of new user
   * @param active status of new user
   * @return id of new user
   * @throws SQLException
   */
  public int addNewUser(String username, String password, boolean active) throws SQLException {

    Connection connection = SqliteConnection.openConnection();
    String query = "INSERT INTO users (username, password, active, game_id) VALUES (?,?,?,0)";
    PreparedStatement ps = connection.prepareStatement(query);
    // input query parameters
    ps.setString(1, username);
    ps.setString(2, password);
    ps.setBoolean(3, active);
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
  public int getUser(String username, String pwd) throws SQLException {

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
}
