package nz.ac.auckland.se206.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection {

  private static final String URL = "jdbc:sqlite:database.db";
  private static SqliteConnection instance = new SqliteConnection();

  /** connect game to backend to access all locally stored data */
  public static void start() {
    SqliteConnection connectionToDb = new SqliteConnection();
    connectionToDb.createTables();
  }

  public static SqliteConnection getInstance() throws SQLException {
    return instance;
  }

  /**
   * starts connection to a database
   *
   * @return the connection (session) with the local database
   */
  public static Connection openConnection() {
    Connection connection = null;
    try {
      // connect to jdbc .db file
      connection = DriverManager.getConnection(URL);
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
    return connection;
  }

  /**
   * shuts down the connection to database so any access to its content is stopped
   *
   * @param connection with a database
   */
  public static void closeConnection(Connection connection) {
    try {
      // close jdbc connection
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      Logger.printSqlError(e);
    }
  }

  /** initialises all the tables that will store the game's info */
  private void createTables() {
    Connection connection = null;
    try {
      // establish connetion to jdbc sqlite
      connection = openConnection();
      Statement statement = connection.createStatement();
      // initialise tables if they do not exist
      createGamesTable(statement);
      createSettingsTable(statement);
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      closeConnection(connection);
    }
  }

  /**
   * initialises a table to tracks games if it does not already exist
   *
   * @param statement to allow sql command to be executed
   * @return whether or not command was successful
   * @throws SQLException when SQL query is invalid
   */
  private boolean createGamesTable(Statement statement) throws SQLException {
    // create games table with following fields
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS games ");
    sb.append("(id INTEGER PRIMARY KEY AUTOINCREMENT, ");
    sb.append("user_id INTEGER, ");
    sb.append("difficulty TEXT, word TEXT, won BOOLEAN, ");
    sb.append("time INTEGER);");
    return statement.execute(sb.toString());
  }

  /**
   * initialises a table to tracks game settings if it does not already exist
   *
   * @param statement to allow sql command to be executed
   * @return whether or not command was successful
   * @throws SQLException when SQL query is invalid
   */
  private boolean createSettingsTable(Statement statement) throws SQLException {
    // create settings table, integers correspond to a difficulty (0=Easy 1=Medium
    // 2=Hard 3=Master)
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS settings ");
    sb.append("(id INTEGER PRIMARY KEY AUTOINCREMENT, ");
    sb.append("user_id STRING, ");
    sb.append("words TEXT, time TEXT, ");
    sb.append("accuracy TEXT, confidence TEXT);");
    return statement.execute(sb.toString());
  }
}
