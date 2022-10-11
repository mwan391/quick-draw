package nz.ac.auckland.se206.models;

import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.util.Meaning;

public class WordInfoModel {

  private String word;
  private Meaning meaning;
  private Difficulty difficulty;

  public WordInfoModel(String word, Meaning meaning, Difficulty difficulty) {
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
