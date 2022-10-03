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
      // connect to jdbc .db file
      connection = DriverManager.getConnection(URL);
      System.out.println("Opened database successfully");
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
    return connection;
  }

  public static void closeConnection(Connection connection) {
    try {
      // close jdbc connection
      if (connection != null) {
        connection.close();
        System.out.println("Closed connection");
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
  }

  private void createTables() {
    Connection connection = null;
    try {
      // establish connetion to jdbc sqlite
      connection = openConnection();
      Statement statement = connection.createStatement();
      // initialise tables if they do not exist
      createUsersTable(statement);
      createGamesTable(statement);
      createSettingsTable(statement);
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      closeConnection(connection);
    }
  }

  private boolean createUsersTable(Statement statement) throws SQLException {
    // create users table with following fields
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS users ");
    sb.append("(id INTEGER PRIMARY KEY AUTOINCREMENT, ");
    sb.append("username TEXT NOT NULL, ");
    sb.append("game_id INTEGER);");
    return statement.execute(sb.toString());
  }

  private boolean createGamesTable(Statement statement) throws SQLException {
    // create games table with following fields
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS games ");
    sb.append("(id INTEGER PRIMARY KEY AUTOINCREMENT, ");
    sb.append("user_id INTEGER, ");
    sb.append("difficulty TEXT, word TEXT, won BOOLEAN, ");
    sb.append("time INTEGER);");
    return statement.execute(sb.toString());
  }

  private boolean createSettingsTable(Statement statement) throws SQLException {
    // create settings table, integers correspond to a difficulty (0=Easy 1=Medium 2=Hard 3=Master)
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS settings ");
    sb.append("(id INTEGER PRIMARY KEY AUTOINCREMENT, ");
    sb.append("username TEXT, ");
    sb.append("words INTEGER, time INTEGER, ");
    sb.append("accuracy INTEGER, confidence INTEGER);");
    return statement.execute(sb.toString());
  }
}
