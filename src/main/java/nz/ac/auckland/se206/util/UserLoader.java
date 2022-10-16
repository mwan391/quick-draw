package nz.ac.auckland.se206.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserLoader {

  private static final String FILE_NAME = "./users.json";

  /** On initialisation of game, checks for users json file or creates a new one */
  public static void start() {
    UserLoader loader = new UserLoader();
    if (!loader.existingFileExists()) {
      // JSON created at location
      loader.createFile();
    }
  }

  /**
   * Check if the file 'users.json' has been created at location path
   *
   * @return whether or not file was found
   */
  private boolean existingFileExists() {
    // Check for existing JSON file to store users
    Path path = Paths.get(FILE_NAME);
    if (Files.exists(path) && Files.isRegularFile(path)) {
      // Valid Users JSON file found
      return true;
    } else {
      // Invalid file or JSON not found
      return false;
    }
  }

  /** Create a new JSON file to store users and all its details */
  private void createFile() {
    // Create new JSON file at location
    try {
      File saveFile = new File(FILE_NAME);
      System.out.println("File created: " + saveFile.createNewFile());
    } catch (Exception e) {
      System.out.println("Couldn't create file");
    }
  }
}
