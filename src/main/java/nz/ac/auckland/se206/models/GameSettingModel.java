package nz.ac.auckland.se206.models;

public class GameSettingModel {

  private int id;
  private String username;
  private int words;
  private int time;
  private int accuracy;
  private int confidence;

  public GameSettingModel(
      int id, String username, int words, int time, int accuracy, int confidence) {
    this.id = id;
    this.username = username;
    this.words = words;
    this.time = time;
    this.accuracy = accuracy;
    this.confidence = confidence;
  }

  public int getId() {
    return id;
  }

  public String getUser() {
    return username;
  }

  public int getWords() {
    return words;
  }

  public int getTime() {
    return time;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public int getConfidence() {
    return confidence;
  }
}
