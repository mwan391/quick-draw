package nz.ac.auckland.se206.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {

  private static final String URL = "jdbc:sqlite:database.db";
  private static SqliteConnection instance;

  // testing
  public static void main(String[] args) {
    Connection connection = openConnection();
    closeConnection(connection);
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
}
