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

  public String toString() {
    StringBuilder sb = new StringBuilder("Username: ");
    sb.append(username);
    sb.append(" Badges: ");
    sb.append(badges.toString());
    return sb.toString();
  }
}
