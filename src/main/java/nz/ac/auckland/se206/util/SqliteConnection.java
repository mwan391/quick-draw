package nz.ac.auckland.se206.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection {

  private static final String URL = "jdbc:sqlite:database.db";

  /** connect game to backend to access all locally stored data */
  public static void start() {
    SqliteConnection connectionToDb = new SqliteConnection();
    connectionToDb.createTables();
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

  /**
   * Removes all rows (data) for a database table
   *
   * @param tableName of given table
   * @return the given SQL query command
   */
  private static String removeAllEntities(String tableName) {
    StringBuilder sb = new StringBuilder("DELETE FROM ");
    return sb.append(tableName).toString();
  }

  /**
   * Resets autoincrement for SQLite databases so that the column ID start from 1 again
   *
   * @param tableName of given table
   * @return the given SQL query command
   */
  private static String resetAutoIncrement(String tableName) {
    StringBuilder sb = new StringBuilder("DELETE FROM sqlite_sequence WHERE name='");
    return sb.append(tableName).append("'").toString();
  }

  /**
   * Deletes all data from a given database
   *
   * @param tableName of table to remove
   */
  public static void clearTable(String tableName) {
    Connection connection = null;
    try {
      connection = openConnection();
      Statement statement = connection.createStatement();
      // delete all data under the table
      statement.addBatch(removeAllEntities(tableName));
      // reset the auto increment for id to start from 1
      statement.addBatch(resetAutoIncrement(tableName));
      // run both commands
      statement.executeBatch();
    } catch (SQLException e) {
      Logger.printSqlError(e);
    } finally {
      closeConnection(connection);
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
   * initialises a table to track game settings if it does not already exist
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
