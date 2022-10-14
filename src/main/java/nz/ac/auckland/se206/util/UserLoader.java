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
      loader.createFile();
    }
  }

  /**
   * Check if 'users.json' has been created
   *
   * @return whether or not file was found
   */
  private boolean existingFileExists() {
    System.out.println("Checking for existing Users JSON file");
    Path path = Paths.get(FILE_NAME);
    if (Files.exists(path) && Files.isRegularFile(path)) {
      System.out.println("Valid Users JSON file found");
      return true;
    } else {
      System.out.println("Invalid file or JSON not found");
      return false;
    }
  }

  /** Create the JSON files to store users */
  private void createFile() {
    try {
      File saveFile = new File(FILE_NAME);
      System.out.println("File created: " + saveFile.createNewFile());
    } catch (Exception e) {
      System.out.println("Couldn't create file");
    }
  }
}
