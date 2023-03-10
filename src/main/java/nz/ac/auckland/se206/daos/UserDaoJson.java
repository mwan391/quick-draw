package nz.ac.auckland.se206.daos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.models.BadgeModel;
import nz.ac.auckland.se206.models.UserModel;

public class UserDaoJson {

  private static final String FILE_NAME = "./users.json";

  /**
   * Returns a list of all existing users with its details from JSON file
   *
   * @return a list of all users
   */
  public List<UserModel> getAll() {
    List<UserModel> users = new ArrayList<>();
    // Typing to match JSON to Java types
    Type usersType = new TypeToken<List<UserModel>>() {}.getType();
    try {
      File file = new File(FILE_NAME);
      BufferedReader br = new BufferedReader(new FileReader(file));
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      // Convert from JSON to Java object
      users = gson.fromJson(br, usersType);
      // Empty file, start a new list
      if (users == null) {
        users = new ArrayList<>();
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error reading from file");
    }
    return users;
  }

  /**
   * check if a username has already been add to JSON file and returns true if user with the
   * username exists
   *
   * @param username to check for
   * @return whether user with username was found
   */
  public boolean checkExists(String username) {
    List<UserModel> users = getAll();
    return users.stream().anyMatch(u -> Objects.equals(username, u.getUsername()));
  }

  /**
   * get instance of user from JSON file by username, returns null if none is found
   *
   * @param username of user
   * @return the username
   */
  public UserModel get(String username) {
    List<UserModel> users = getAll();
    // Filter to match user by username
    UserModel foundUser =
        users.stream().filter(u -> username.equals(u.getUsername())).findFirst().orElse(null);
    return foundUser;
  }

  /**
   * Creates and save a new user to JSON file and returns true if succesful
   *
   * @param user new user to be added
   * @return whether user was added to file succesfully
   */
  public boolean add(UserModel user) {
    List<UserModel> users = getAll();
    if (users != null) {
      users.add(user);
    }
    return saveToFile(users);
  }

  /**
   * Add and store a new achievement for user to its list of badges in JSON
   *
   * @param badge new badge to be added (duplicates are allowed)
   * @param username of user
   * @return whether badge was added to file successfully
   */
  public boolean addBadge(BadgeModel badge, String username) {
    List<UserModel> users = getAll();
    // Find the matching user from JSON file
    UserModel foundUser =
        users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    if (foundUser == null) {
      return false;
    }
    foundUser.getBadges().add(badge);
    // Save updated user to JSON
    return saveToFile(users);
  }

  /**
   * Update the profile picture for an existing user, returns true if update to file is succesful
   *
   * @param user old user to update
   * @param picUrl new picture to replace
   * @return true if updating user's avatar is succesful
   */
  public boolean updateAvatar(UserModel user, String picUrl) {
    List<UserModel> users = getAll();
    UserModel foundUser =
        users.stream()
            .filter(u -> u.getUsername().equals(user.getUsername()))
            .findFirst()
            .orElse(null);
    // No user found in file
    if (foundUser == null) {
      return false;
    }
    // Set the new picture url
    foundUser.setIcon(picUrl);
    return saveToFile(users);
  }

  /**
   * Deletes a user from the JSON file and returns if it was successful
   *
   * @param username of user to remove
   * @return true if deletion was succesful
   */
  public boolean remove(UserModel user) {
    List<UserModel> users = getAll();
    int originalSize = users.size();
    // Filter all users except for the one specified
    List<UserModel> updatedUsers =
        users.stream()
            .filter(u -> !Objects.equals(user.getUsername(), u.getUsername()))
            .collect(Collectors.toCollection(ArrayList::new));
    // If username could not be found
    if (updatedUsers.size() == originalSize) {
      return false;
    }
    // Update new users to JSON
    return saveToFile(updatedUsers);
  }

  /**
   * Removes all existing saved users from JSON file and returns true if was succesful
   *
   * @return whether or not removal was successful
   */
  public boolean removeAll() {
    // Create an empty list
    List<UserModel> users = new ArrayList<>();
    // Update file to be empty
    return saveToFile(users);
  }

  /**
   * Check if a user has already a particular badge from the JSON file
   *
   * @param badge to find match for
   * @param user user to check its badges
   * @return whether or not badge was found in the user
   */
  public boolean checkBadgeExists(BadgeModel badge, UserModel user) {
    boolean foundMatch = user.getBadges().contains(badge);
    return foundMatch;
  }

  /**
   * Writes all users and its old or new details to JSON file
   *
   * @param users list of updated users to save to file
   * @return whether or not saving to file was successful
   */
  private boolean saveToFile(List<UserModel> users) {
    // Write to file 'users.json'
    try (Writer fileWriter = new FileWriter(FILE_NAME)) {
      Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      // Write Java object ot JSON file
      gson.toJson(users, fileWriter);
      return true;
    } catch (JsonIOException | IOException e) {
      System.out.println("Error in trying to save users");
      e.printStackTrace();
    }
    return false;
  }
}
