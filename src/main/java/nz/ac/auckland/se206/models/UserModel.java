package nz.ac.auckland.se206.models;

public class UserModel {

  private static UserModel activeUser = null;

  public static UserModel getActiveUser() {
    return activeUser;
  }

  public static void setActiveUser(UserModel activeUser) {
    UserModel.activeUser = activeUser;
  }

  private int id;
  private String username;
  private int gameId;

  public UserModel(int id, String username, int gameId) {
    this.id = id;
    this.username = username;
    this.gameId = gameId;
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public int getGameId() {
    return gameId;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(username);
    return sb.toString();
  }
}
