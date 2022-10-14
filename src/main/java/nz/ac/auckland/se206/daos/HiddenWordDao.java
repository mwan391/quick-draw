package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.util.Logger;
import nz.ac.auckland.se206.util.SqliteConnection;

public class HiddenWordDao {

  /**
   * Store a word played in Hidden Game Mode for a user
   *
   * @param userId of user
   * @param word to be added
   * @return unique id for the word
   */
  public int add(String userId, String word) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      // add new row to table
      String query = "INSERT INTO words (user_id, word) VALUES (?,?)";
      PreparedStatement ps = connection.prepareStatement(query);
      // set paramaters (column titles) to add into the table
      ps.setString(1, userId);
      ps.setString(2, word);
      ps.executeUpdate();
      // get next unique id for new word
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
   * Returns all the words played by user under Hidden Game Mode
   *
   * @param userId of user
   * @return list of words
   */
  public List<String> getWordHistory(String userId) {
    Connection connection = SqliteConnection.openConnection();
    // words from games user has played
    List<String> words = new ArrayList<>();
    try {
      // get word history from oldest to recent
      String query = "SELECT word FROM words WHERE user_id=? ORDER BY id";
      PreparedStatement ps = connection.prepareStatement(query);
      // input the user you want the history for
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        words.add(rs.getString("word"));
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return words;
  }
}
