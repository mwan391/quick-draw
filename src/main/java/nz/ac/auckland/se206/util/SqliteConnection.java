package nz.ac.auckland.se206.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection {

  private static final String URL = "jdbc:sqlite:database.db";
  private static SqliteConnection instance = new SqliteConnection();

  public static void start() {
    SqliteConnection test = new SqliteConnection();
    test.createTables();
  }

  public static SqliteConnection getInstance() throws SQLException {
    return instance;
  }

  public static Connection openConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(URL);
      System.out.println("Opened database successfully");
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
    return connection;
  }

  public static void closeConnection(Connection connection) {
    try {
      if (connection != null) connection.close();
      System.out.println("Closed connection succesfully");
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
  }

  private void createTables() {
    Connection connection = null;
    try {
      connection = openConnection();
      Statement statement = connection.createStatement();
      // initialise tables if they do not exist
      createUsersTable(statement);
      createGamesTable(statement);
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      closeConnection(connection);
    }
  }

  private boolean createUsersTable(Statement statement) throws SQLException {
    return statement.execute(
        "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR(256) NOT NULL, password VARCHAR(256) NOT NULL, game_id INTEGER);");
  }

  private boolean createGamesTable(Statement statement) throws SQLException {
    return statement.execute(
        "CREATE TABLE IF NOT EXISTS games (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, difficulty INTEGER, word VARCHAR, won BOOLEAN, time INTEGER);");
  }
}
