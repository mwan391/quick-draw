package nz.ac.auckland.se206.models;

public class GameModel {

  private int id;
  private String userId;
  private String difficulty;
  private String word;
  private boolean won;
  private int time;

  /**
   * This models what how a 'game' is structured in SQLite, each field is a column title
   *
   * @param id unique id of game session
   * @param userId user that played this game
   * @param difficulty difficulty of words of this game
   * @param word that was played in this game
   * @param won boolean of whether this game was won or lost
   * @param time time took to win the game, or by default 60 seconds if game was lost
   */
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
