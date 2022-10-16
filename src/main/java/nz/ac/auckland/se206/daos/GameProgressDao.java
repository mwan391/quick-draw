package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class GameProgressDao {

  /**
   * Returns the number of games of specified difficulty under a user
   *
   * @param difficulty difficulty level to filter for
   * @param userId of user
   * @return number of games counted
   */
  public int getNumberPlayedOfThisDifficulty(Difficulty difficulty, String userId) {
    Connection connection = SqliteConnection.openConnection();
    int count = -1;
    try {
      // count the number of rows with matching user id
      String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=? AND difficulty=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // input inputs to complete SQL query
      ps.setString(1, userId);
      ps.setString(2, difficulty.toString());
      ResultSet rs = ps.executeQuery();
      // number of <difficulty> games, if none return 0
      count = rs.next() ? rs.getInt("count") : 0;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return count;
  }
}
