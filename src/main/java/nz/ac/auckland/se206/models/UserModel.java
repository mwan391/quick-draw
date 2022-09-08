package nz.ac.auckland.se206.models;

public class UserModel {
  private int id;
  private String username;
  private String password;
  private boolean active;
  private int gameId;

  public UserModel(int id, String username, String password, boolean active, int gameId) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.active = active;
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

  public boolean isActive() {
    return active;
  }

  public int getGameId() {
    return gameId;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(username);
    sb.append("\nActive Status: ");
    sb.append(this.isActive());
    return sb.toString();
  }
}
