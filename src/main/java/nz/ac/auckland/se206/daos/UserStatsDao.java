package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.util.SqliteConnection;

public class UserStatsDao {

  public int countGames(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT COUNT(*) AS count FROM games WHERE user_id=?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // number of played games
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
    // number of winning games
    int count = rs.next() ? rs.getInt("count") : 0;
    SqliteConnection.closeConnection(connection);
    return count;
  }

  public int getBestTime(int userId) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT MIN(time) AS best FROM games WHERE user_id=? AND won=1";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // shortest winning game time
    int bestTime = rs.next() ? rs.getInt("best") : 0;
    SqliteConnection.closeConnection(connection);
    return bestTime;
  }

  public List<String> getWordHistory(int userId) throws SQLException {
    // words from games given user has played
    List<String> words = new ArrayList<>();
    Connection connection = SqliteConnection.openConnection();
    String query = "SELECT word FROM games WHERE user_id=? ORDER BY id";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      words.add(rs.getString("word"));
    }
    SqliteConnection.closeConnection(connection);
    return words;
  }
}
