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
   * Creates a new user according to the inserted parameters
   *
   * @param username, the user's name
   * @param icon, the filename of the selected profile icon
   */
  public UserModel(String username, String icon) {
    this.id = UUID.randomUUID().toString();
    this.username = username;
    this.icon = icon;
    this.badges = new ArrayList<>();
  }

  /**
   * Gets the id for the user
   *
   * @return id, the identification string for the user
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the user's name
   *
   * @return username, the name chosen by the user
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the user's name
   *
   * @param username, the name chosen by the user
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the icon chosen by the user
   *
   * @return icon, the icon chosen by the user
   */
  public String getIcon() {
    return icon;
  }

  /** Sets the icon chosen by the user */
  public void setIcon(String icon) {
    this.icon = icon;
  }

  /**
   * Gets the badges that the user has been awarded
   *
   * @return badges, a list of attained badges
   */
  public List<BadgeModel> getBadges() {
    return badges;
  }

  /** Sets the badges that the user has been awarded */
  public void setBadges(List<BadgeModel> badges) {
    this.badges = badges;
  }

  /**
   * Converts the user and their badges into a string
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
