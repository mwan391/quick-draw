package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserStatsDao {

  public int countGames(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    int count = rs.next() ? rs.getInt("count") : 0;
    SqliteConnection.closeConnection(connection);
    return count;
  }

  public int countWins(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=? AND won=1";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    int count = rs.next() ? rs.getInt("count") : 0;
    SqliteConnection.closeConnection(connection);
    return count;
  }
}
