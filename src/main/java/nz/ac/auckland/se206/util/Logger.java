package nz.ac.auckland.se206.util;

import java.sql.SQLException;

public class Logger {

  /**
   * Helper that prints SQL exception to console in readable format
   *
   * @param e SQLException caught
   */
  public static void printSqlError(SQLException e) {
    StringBuilder sb = new StringBuilder("SQL error: ");
    // print sql error status to indicate type of error
    sb.append(e.getMessage())
        .append("\n Error code: ")
        .append(e.getErrorCode())
        .append("\n Error state: ")
        .append(e.getSQLState());
    System.out.println(sb);
  }
}
