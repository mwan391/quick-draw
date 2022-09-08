package nz.ac.auckland.se206.models;

public class GameModel {

  private int id;
  private int userId;
  private int difficulty;
  private String word;
  private boolean won;
  private int time;

  public GameModel(int id, int userId, int difficulty, String word, boolean won, int time) {
    this.id = id;
    this.userId = userId;
    this.difficulty = difficulty;
    this.word = word;
    this.won = won;
    this.time = time;
  }

  public int getId() {
    return id;
  }

  public int getUserId() {
    return userId;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public String getWord() {
    return word;
  }

  public boolean getWon() {
    return won;
  }

  public int getTime() {
    return time;
  }
}
