package nz.ac.auckland.se206.models;

public class GameModel {

  private int id;
  private String userId;
  private String difficulty;
  private String word;
  private boolean won;
  private int time;

  public GameModel(int id, String userId, String difficulty, String word, boolean won, int time) {
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

  public String getUserId() {
    return userId;
  }

  public String getDifficulty() {
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

  @Override
  public String toString() {
    // print out details of the game
    StringBuilder sb = new StringBuilder("\nGame ID: ");
    sb.append(getId());
    sb.append(" Played by user ID: ");
    sb.append(getUserId());
    sb.append(" Result: ");
    sb.append(getWon());
    return sb.toString();
  }
}
