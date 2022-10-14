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

public class HiddenWordDao {

  /**
   * Store a word played in Hidden Game Mode for a user
   *
   * @param userId of user
   * @param word to be added
   * @return unique id for the word
   */
  public int add(String word, String userId) {
    Connection connection = SqliteConnection.openConnection();
    int id = 0;
    try {
      // add new row to table
      String query = "INSERT INTO hidden_words (user_id, word) VALUES (?,?)";
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
    // words from hidden-mode games
    List<String> words = new ArrayList<>();
    try {
      // get word history from oldest to recent
      String query = "SELECT word FROM hidden_words WHERE user_id=? ORDER BY id";
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

  /**
   * Removes hidden word entries linked to a user in hidden_words Sqlite databse
   *
   * @param user of removed user
   * @return true if removal of settings was succesful
   */
  public boolean removeHiddenWordsFromUser(UserModel user) {
    Connection connection = SqliteConnection.openConnection();
    boolean isRemoved = false;
    try {
      // Remove hidden words under user
      String query = "DELETE from hidden_words WHERE user_id=?";
      PreparedStatement ps = connection.prepareStatement(query);
      // Input the user
      ps.setString(1, user.getId());
      ps.executeUpdate();
      // Removal is succesful
      isRemoved = true;
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      SqliteConnection.closeConnection(connection);
    }
    return isRemoved;
  }
}
