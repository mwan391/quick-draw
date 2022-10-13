package nz.ac.auckland.se206.dictionary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.CategorySelect.Difficulty;

public class DictionaryLookup {

  private final String FILE_NAME = "definitions.json";

  /**
   * Returns a list of all words with its definitions from JSON file
   *
   * @return all words
   */
  private List<WordInfo> getAll() {
    List<WordInfo> words = new ArrayList<>();
    URL definitionsUrl = DictionaryLookup.class.getResource(FILE_NAME);
    // Typing to convert JSON object to Java object
    Type wordsType = new TypeToken<List<WordInfo>>() {}.getType();
    try {
      // Open JSON file
      File file = new File(definitionsUrl.getPath());
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
   * Generates a random word classified as easy
   *
   * @return a easy word
   */
  public String getEasyLevel() {
    List<WordInfo> easyWords = getWordsOfThisDifficulty(Difficulty.EASY);
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(easyWords.size());
    return easyWords.get(randNum).getMeaning().getDefinition();
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
  public String getMediumLevel() {
    List<WordInfo> easyAndMediumWords = getWordsOfThisDifficulty(Difficulty.MEDIUM);
    List<WordInfo> easyWords = getWordsOfThisDifficulty(Difficulty.EASY);
    // combine easy and medium words
    easyAndMediumWords.addAll(easyWords);
    // mix the words up
    Collections.shuffle(easyAndMediumWords);
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(easyAndMediumWords.size());
    return easyAndMediumWords.get(randNum).getMeaning().getDefinition();
  }

  /**
   * Generates a random word classified as easy, medium or hard
   *
   * @return a easy, medium or hard word
   */
  public String getHardLevel() {
    List<WordInfo> allWords = getAll();
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(allWords.size());
    return allWords.get(randNum).getMeaning().getDefinition();
  }

  /**
   * Generates a random word classified as hard
   *
   * @return a hard word
   */
  public String getMasterLevel() {
    List<WordInfo> hardWords = getAll();
    // generate random number to select random word
    int randNum = new Random(System.currentTimeMillis()).nextInt(hardWords.size());
    return hardWords.get(randNum).getMeaning().getDefinition();
  }
}
