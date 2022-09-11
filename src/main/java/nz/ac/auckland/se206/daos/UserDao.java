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

    ps.setString(1, username);
    ps.setString(2, password);
    ps.setBoolean(3, active);
    ps.executeUpdate();

    int userId = 0;
    ResultSet resultSet = ps.getGeneratedKeys();
    if (resultSet.next()) {
      // System.out.println("there is user next: ");
      userId = resultSet.getInt(1);
      // System.out.println("user id is " + userId);
    }
    SqliteConnection.closeConnection(connection);

    return userId;
  }
}
