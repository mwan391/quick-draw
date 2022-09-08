package nz.ac.auckland.se206.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection {

  private static final String URL = "jdbc:sqlite:database.db";
  private static SqliteConnection instance;

  public static void start() {
    SqliteConnection test = new SqliteConnection();
    test.createTables();
  }

  public static SqliteConnection getInstance() throws SQLException {
    if (instance == null) {
      synchronized (SqliteConnection.class) {
        if (instance == null) {
          instance = new SqliteConnection();
        }
      }
    }
    return instance;
  }

  public static Connection openConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(URL);
      System.out.println("Opened database successfully");
    } catch (SQLException e) {
      System.err.println("Failed to open database connection");
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
      boolean tableCreated =
          statement.execute(
              "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR(256) NOT NULL, password VARCHAR(256) NOT NULL, active BOOLEAN, game_id INTEGER);");
      if (tableCreated) System.out.println("Created Users table.");
      tableCreated =
          statement.execute(
              "CREATE TABLE IF NOT EXISTS games (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, difficulty INTEGER, word VARCHAR, won BOOLEAN, time INTEGER);");
      if (tableCreated) System.out.println("Created Games table.");
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      closeConnection(connection);
    }
  }
}
