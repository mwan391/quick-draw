package nz.ac.auckland.se206.models;

public class UserModel {

  private static UserModel activeUser = null;

  private int id;
  private String username;
  private String password;
  private int gameId;

  public UserModel(int id, String username, String password, int gameId) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.gameId = gameId;
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public int getGameId() {
    return gameId;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(username);
    return sb.toString();
  }

  public static UserModel getActiveUser() {
    return activeUser;
  }

  public static void setActiveUser(UserModel activeUser) {
    UserModel.activeUser = activeUser;
  }
}
