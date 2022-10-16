package nz.ac.auckland.se206.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.models.GameModel;
import nz.ac.auckland.se206.models.GameSettingModel;

public class SqlConverter {

  /**
   * Helper to convert a SQL row (game details) to a game instance,
   *
   * @param rs a table of data from a sql query
   * @return instance of game session
   */
  public static GameModel getGame(ResultSet rs) {
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

  /**
   * Helper to convert a game setting (in SQL) to a setting instance
   *
   * @param rs result rows of settings from a sql query
   * @return instance of game setting
   */
  public static GameSettingModel getSettings(ResultSet rs) {
    GameSettingModel gameSetting = null;
    try {
      // Construct game setting linking SQL columns (id, words, time, etc) to its fields
      gameSetting =
          new GameSettingModel(
              rs.getInt("id"),
              rs.getString("user_id"),
              rs.getString("words"),
              rs.getString("time"),
              rs.getString("accuracy"),
              rs.getString("confidence"));
      // All columns of settings specified
    } catch (SQLException e) {
      // Catch exceptions
      Logger.printSqlError(e);
    }
    return gameSetting;
  }
}
