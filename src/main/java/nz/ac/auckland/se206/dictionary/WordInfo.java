package nz.ac.auckland.se206.dictionary;

import nz.ac.auckland.se206.CategorySelect.Difficulty;

public class WordInfo {

  private String word;
  private Meaning meaning;
  private Difficulty difficulty;

  /**
   * Models a category combined with its definition and difficulty
   *
   * @param word the category selected
   * @param meaning holds the definition
   * @param difficulty level of difficulty
   */
  public WordInfo(String word, Meaning meaning, Difficulty difficulty) {
    this.word = word;
    this.meaning = meaning;
    this.difficulty = difficulty;
  }

  public String getWord() {
    return word;
  }

  public Meaning getMeaning() {
    return meaning;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  /** Formats WordInfo by word, meaning (definition) and difficulty */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Word: ");
    sb.append(word);
    sb.append(" Meaning: ");
    sb.append(meaning);
    sb.append("Difficulty: ");
    sb.append(difficulty);
    return sb.toString();
  }
}
