package nz.ac.auckland.se206.dictionary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.daos.HiddenWordDao;
import nz.ac.auckland.se206.models.UserModel;

public class DictionaryLookup {

  private final String FILE_PATH = "src/main/java/nz/ac/auckland/se206/dictionary/definitions.json";
  private UserModel user;

  /**
   * Be able to select WordInfo objects of different difficulties that are unique to the user
   *
   * @param user of the gameto find words for
   */
  public DictionaryLookup(UserModel user) {
    this.user = user;
  }

  /**
   * Returns a Word object under the Word Game Setting, no duplicates should occur
   *
   * @param difficulty the word setting difficulty
   * @return the Word object (includes the word and its definition)
   */
  public WordInfo generateWordInLevel(Difficulty difficulty) {
    // Return a WordInfo object of specified difficulty
    switch (difficulty) {
      case EASY:
        // Easy word
        return getEasyLevel();
      case MEDIUM:
        // Easy or medium word
        return getMediumLevel();
      case HARD:
        // Easy, medium or hard word
        return getHardLevel();
      case MASTER:
        // Hard word
        return getMasterLevel();
      default:
        throw new IllegalArgumentException("=== Invalid Difficulty For Words Setting ===");
    }
  }

  /**
   * Returns a list of all WordInfo objects from JSON file
   *
   * @return all words objects (with its definition)
   */
  private List<WordInfo> getAll() {
    List<WordInfo> words = new ArrayList<>();

    // Typing to convert JSON object to Java object
    Type wordsType = new TypeToken<List<WordInfo>>() {}.getType();
    try {
      // Open JSON file
      File file = new File(FILE_PATH);
      BufferedReader br = new BufferedReader(new FileReader(file));
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      // Converts JSON objects to List of Java objects
      words = gson.fromJson(br, wordsType);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error reading from file");
    }
    return words;
  }

  /**
   * Return a list of words that haven't been played before by filtering out those that have been
   *
   * @param uniqueWords words that are to be played in the game
   * @return playable words
   */
  private List<WordInfo> getUnplayedWords(List<WordInfo> uniqueWords) {
    HiddenWordDao dao = new HiddenWordDao();
    List<String> wordHistory = dao.getWordHistory(user.getId());
    Set<String> playedWords = new HashSet<String>(wordHistory);
    // Remove word if word has been played before
    uniqueWords.removeIf(word -> playedWords.contains(word.getWord()));
    return uniqueWords;
  }

  /**
   * Generates a random word classified as easy
   *
   * @return a easy word
   */
  private WordInfo getEasyLevel() {
    List<WordInfo> easyWords = getWordsOfThisDifficulty(Difficulty.EASY);
    List<WordInfo> unplayedEasyWords = getUnplayedWords(easyWords);
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(unplayedEasyWords.size());
    return unplayedEasyWords.get(randNum);
  }

  /**
   * Returns a list of all words of given difficulty
   *
   * @param difficulty of category
   * @return list of E,M,H,M words
   */
  private List<WordInfo> getWordsOfThisDifficulty(Difficulty difficulty) {
    List<WordInfo> words = getAll();
    // Filter for words of given difficulty
    List<WordInfo> easyWords =
        words.stream()
            .filter(word -> word.getDifficulty() == difficulty)
            .collect(Collectors.toCollection(ArrayList::new));
    return easyWords;
  }

  /**
   * Generates a random word classified as easy or medium
   *
   * @return a easy or medium word
   */
  private WordInfo getMediumLevel() {
    List<WordInfo> easyAndMediumWords = getWordsOfThisDifficulty(Difficulty.MEDIUM);
    List<WordInfo> easyWords = getWordsOfThisDifficulty(Difficulty.EASY);
    // combine easy and medium words
    easyAndMediumWords.addAll(easyWords);
    // Filter out words that have been played before
    List<WordInfo> unplayedEasyAndMedium = getUnplayedWords(easyAndMediumWords);
    // mix the words up
    Collections.shuffle(unplayedEasyAndMedium);
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(unplayedEasyAndMedium.size());
    return unplayedEasyAndMedium.get(randNum);
  }

  /**
   * Generates a random word classified as easy, medium or hard
   *
   * @return a easy, medium or hard word
   */
  private WordInfo getHardLevel() {
    List<WordInfo> allWords = getAll();
    List<WordInfo> unplayedWords = getUnplayedWords(allWords);
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(unplayedWords.size());
    return unplayedWords.get(randNum);
  }

  /**
   * Generates a random word classified as hard
   *
   * @return a hard word
   */
  private WordInfo getMasterLevel() {
    List<WordInfo> hardWords = getWordsOfThisDifficulty(Difficulty.HARD);
    List<WordInfo> unplayedHardWords = getUnplayedWords(hardWords);
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(unplayedHardWords.size());
    return unplayedHardWords.get(randNum);
  }
}
