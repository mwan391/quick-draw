package nz.ac.auckland.se206;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nz.ac.auckland.se206.daos.UserStatsDao;
import nz.ac.auckland.se206.models.UserModel;

public class CategorySelect {

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD,
    MASTER
  }

  private static HashMap<Difficulty, ArrayList<String>> categories = new HashMap<>();
  private static String category = null;
  private static Difficulty wordDifficulty;

  /*
   * This method is invoked when the application starts. It loads the given csv
   * file into a hashmap
   * corresponding to the difficulty.
   *
   * @param csvName the name of the csv file in the resource folder.
   *
   * @throws IOException If "src/main/resources/csvName.csv" is not found.
   *
   */
  public static void setCategories(String csvName) throws IOException {

    HashMap<String, ArrayList<String>> tempCategories = new HashMap<>();
    tempCategories.put("E", new ArrayList<>());
    tempCategories.put("M", new ArrayList<>());
    tempCategories.put("H", new ArrayList<>());

    URL csvFile = CategorySelect.class.getResource("/" + csvName + ".csv");

    // code adapted from
    // https://cowtowncoder.medium.com/reading-csv-with-jackson-c4e74a15ddc1
    final CsvMapper mapper = new CsvMapper();
    // read csv file lines into an iterator class
    MappingIterator<List<String>> csvValues =
        mapper
            .readerForListOf(String.class)
            .with(CsvParser.Feature.WRAP_AS_ARRAY)
            .readValues(csvFile);
    // iterate through every line in the csv and assign the values to the
    // corresponding array
    while (csvValues.hasNextValue()) {
      List<String> row = csvValues.nextValue();
      tempCategories.get(row.get(1)).add(row.get(0));
    }

    // move the arrays into the proper hashmap with the enum keys
    categories.put(Difficulty.EASY, tempCategories.get("E"));
    categories.put(Difficulty.MEDIUM, tempCategories.get("M"));
    categories.put(Difficulty.HARD, tempCategories.get("H"));
  }

  /**
   * Generates a word from the given category. It will not return an already played word unless all
   * the words from that category has been played already. Uses actual difficulty, not settings
   * difficulty.
   *
   * @param wordDifficulty to be generated. Actual difficulty.
   */
  private static void generateCategory(Difficulty wordDifficulty) {
    // get words in category
    ArrayList<String> words = new ArrayList<>(categories.get(wordDifficulty));
    // get user's history of words in the difficulty
    UserStatsDao userStatsDao = new UserStatsDao();
    String activeUserId = UserModel.getActiveUser().getId();
    List<String> completeHistory = userStatsDao.getHistoryMap(activeUserId).get(wordDifficulty);

    // if history is empty, just create an empty list
    if (completeHistory == null) {
      completeHistory = new ArrayList<>();
    }

    // create relevant history list (ignoring repeats)
    int completeSize = (completeHistory.size());
    int relevantSize = completeSize - (completeSize % words.size());
    List<String> relevantHistory = completeHistory.subList(relevantSize, completeSize);

    // generate random words until the new word isn't in the relevant history
    category = "";

    while (category.equals("") || relevantHistory.contains(category)) {
      // get random word in the words array
      int randomNum = (int) Math.floor(Math.random() * words.size());
      category = words.get(randomNum);

      // remove from potential pool of words
      words.remove(category);
    }
  }

  /**
   * Generates a word from the settings difficulty. Picks a random word difficulty depending on the
   * setting.
   *
   * @return actual difficulty of word
   */
  public static Difficulty generateSetCategory() {
    int randomNum;
    // determine which list to pull from
    Difficulty actualDifficulty = Difficulty.EASY;
    // also return the actual difficulty of word as opposed to settings difficulty
    switch (wordDifficulty) {
      case EASY:
        // easy settings only give easy words
        generateCategory(Difficulty.EASY);
        break;
      case MEDIUM:
        // choose randomly between easy/medium
        randomNum = (int) Math.floor(Math.random() * 2);
        if (randomNum == 1) {
          generateCategory(Difficulty.EASY);
        } else {
          generateCategory(Difficulty.MEDIUM);
          actualDifficulty = Difficulty.MEDIUM;
        }
        break;
      case HARD:
        // choose randomly between easy/medium/hard
        randomNum = (int) Math.floor(Math.random() * 3);
        if (randomNum == 1) {
          generateCategory(Difficulty.EASY);
        } else if (randomNum == 2) {
          generateCategory(Difficulty.MEDIUM);
          actualDifficulty = Difficulty.MEDIUM;
        } else {
          generateCategory(Difficulty.HARD);
          actualDifficulty = Difficulty.HARD;
        }
        break;
      case MASTER:
        // master returns only hard words
        generateCategory(Difficulty.HARD);
        actualDifficulty = Difficulty.HARD;
        break;
    }

    return actualDifficulty;
  }

  public static String getCategory() {
    return category;
  }

  public static Difficulty getWordDifficulty() {
    return wordDifficulty;
  }

  public static void setWordDifficulty(Difficulty wordDifficulty) {
    CategorySelect.wordDifficulty = wordDifficulty;
  }
}
