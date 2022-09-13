package nz.ac.auckland.se206.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nz.ac.auckland.se206.util.SqliteConnection;

public class GameDao {

  public int addNewGame(int userId, int difficultyValue, String word) throws SQLException {
    Connection connection = SqliteConnection.openConnection();
    String query = "INSERT INTO games (user_id, difficulty, word, won, time) VALUES (?,?,?,0,60)";
    PreparedStatement ps = connection.prepareStatement(query);
    // get next primary key

    ps.setInt(1, userId);
    ps.setInt(2, difficultyValue);
    ps.setString(3, word);
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    int gameId = 0;
    if (rs.next()) {
      gameId = rs.getInt(1);
    }

    SqliteConnection.closeConnection(connection);
    return gameId;
  }
}
