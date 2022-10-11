package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.models.GameSettingModel;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class GameSettingDao {

  /**
   * creates a new setting into the database and returns the id
   *
   * @param userId of current user
   * @return id of new setting
   */
  public int add(String userId) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      // add new row with all settings initialised to "EASY"
      String query =
          "INSERT INTO settings (user_id, words, time, accuracy, confidence) VALUES (?,\"EASY\",\"EASY\",\"EASY\",\"EASY\")";
      PreparedStatement ps = connection.prepareStatement(query);
      // set paramaters (column values) to into the table
      ps.setString(1, userId);
      ps.executeUpdate();
      // get next unique id for setting
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
   * retrieves the settings of the latest game played by a user
   *
   * @param userId of current user
   * @return the settings for the user
   */
  public GameSettingModel get(String userId) {
    Connection connection = SqliteConnection.openConnection();
    GameSettingModel user = null;
    try {
      // find the matching settings for user
      String query = "SELECT * FROM settings WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setString(1, userId);
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

  /**
   * update the selected settings for a game played by a user
   *
   * @param settings any changes to settings even if user keeps the same settings
   * @return whether or not the settings have been updated
   */
  public boolean update(GameSettingModel settings) {
    Connection connection = SqliteConnection.openConnection();
    boolean updated = false;
    try {
      // update any settings for a user
      String query =
          "UPDATE settings SET words=?, time=?, accuracy=?, confidence=? WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // input the values to the different settings
      ps.setString(1, settings.getWords());
      ps.setString(2, settings.getTime());
      ps.setString(3, settings.getAccuracy());
      ps.setString(4, settings.getConfidence());
      // settings for this user
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
   * @param rs a table of data from a sql query
   * @return instance of game setting
   * @throws SQLException
   */
  private GameSettingModel getSettings(ResultSet rs) throws SQLException {
    // helper to convert a game setting in sql to game setting in java
    return new GameSettingModel(
        rs.getInt("id"),
        rs.getString("user_id"),
        rs.getString("words"),
        rs.getString("time"),
        rs.getString("accuracy"),
        rs.getString("confidence"));
  }
}
