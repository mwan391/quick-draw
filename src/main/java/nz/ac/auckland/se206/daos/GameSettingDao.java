package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.models.GameSettingModel;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class GameSettingDao {

  public int add(String username) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      // add new row to table
      String query =
          "INSERT INTO settings (username, words, time, accuracy, confidence) VALUES (?,-1,-1,-1,-1)";
      PreparedStatement ps = connection.prepareStatement(query);
      // set paramaters (column values) to into the table
      ps.setString(1, username);
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
   * check if any settings exist for a given user
   *
   * @param userId of user
   * @return whether or not settings were found for the user
   */
  public boolean check(String username) {
    Connection connection = SqliteConnection.openConnection();
    boolean settingFound = false;
    try {
      // filter for a setting configured for the user
      PreparedStatement statement =
          connection.prepareStatement("SELECT 1 FROM settings WHERE username=?");
      // input query parameters
      statement.setString(1, username);
      ResultSet rst = statement.executeQuery();
      settingFound = rst.next();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return settingFound;
  }

  public GameSettingModel get(String username) {
    Connection connection = SqliteConnection.openConnection();
    GameSettingModel user = null;
    try {
      // find the matching settings for user
      String query = "SELECT * FROM settings WHERE username=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      // convert result to an instance of game settings
      if (rs.next()) {
        user = getSettings(rs);
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return user;
  }

  public boolean update(GameSettingModel settings) {
    Connection connection = SqliteConnection.openConnection();
    boolean updated = false;
    try {
      // update any settings for a user
      String query =
          "UPDATE settings SET words=?, time=?, accuracy=?, confidence=? WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // input the different settings to the query
      ps.setString(1, settings.getTime());
      ps.setString(2, settings.getAccuracy());
      ps.setString(3, settings.getAccuracy());
      ps.setString(4, settings.getConfidence());
      // for this user
      ps.setString(5, settings.getUser());
      ps.execute();
      updated = true;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return updated;
  }

  /**
   * getter for game settings instance
   *
   * @param rs
   * @return instance of game setting
   * @throws SQLException
   */
  private GameSettingModel getSettings(ResultSet rs) throws SQLException {
    // helper to convert a game setting in sql to game setting in java
    return new GameSettingModel(
        rs.getInt("id"),
        rs.getString("username"),
        rs.getString("words"),
        rs.getString("time"),
        rs.getString("accuracy"),
        rs.getString("confidence"));
  }
}
