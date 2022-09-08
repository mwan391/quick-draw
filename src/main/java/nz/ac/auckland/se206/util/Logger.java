package nz.ac.auckland.se206.util;

import java.sql.SQLException;

public class Logger {

  public static void printSqlError(SQLException e) {
    StringBuilder sb = new StringBuilder("SQL error: ");
    sb.append(e.getMessage())
        .append("\n Error code: ")
        .append(e.getErrorCode())
        .append("\n Error state: ")
        .append(e.getSQLState());
    System.out.println(sb);
  }
}
