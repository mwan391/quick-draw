package nz.ac.auckland.se206.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserModel {

  private static UserModel activeUser = null;

  public static UserModel getActiveUser() {
    return activeUser;
  }

  public static void setActiveUser(UserModel activeUser) {
    UserModel.activeUser = activeUser;
  }

  private String id;
  private String username;
  private String icon;
  private List<BadgeModel> badges;

  /**
   * Creates a new user according to the inserted parameters to be stored in JSON
   *
   * @param username the user's name
   * @param icon the filename of the selected profile icon
   */
  public UserModel(String username, String icon) {
    this.id = UUID.randomUUID().toString();
    this.username = username;
    this.icon = icon;
    this.badges = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public List<BadgeModel> getBadges() {
    return badges;
  }

  public void setBadges(List<BadgeModel> badges) {
    this.badges = badges;
  }

  /**
   * Converts the user and their badges as a list into a formatted string
   *
   * @return a string of badges
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("Username: ");
    sb.append(username);
    sb.append(" Badges: ");
    sb.append(badges.toString());
    return sb.toString();
  }
}
